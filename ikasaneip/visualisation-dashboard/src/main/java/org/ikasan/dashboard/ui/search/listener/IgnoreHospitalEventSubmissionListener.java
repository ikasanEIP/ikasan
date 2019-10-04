package org.ikasan.dashboard.ui.search.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.data.provider.Query;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.general.component.ProgressIndicatorDialog;
import org.ikasan.dashboard.ui.search.component.HospitalCommentsDialog;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.model.hospital.ExclusionEventActionImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class IgnoreHospitalEventSubmissionListener extends HospitalEventActionListener implements ComponentEventListener<ClickEvent<Button>>
{
    private Logger logger = LoggerFactory.getLogger(IgnoreHospitalEventSubmissionListener.class);

    private HospitalAuditService hospitalAuditService;

    public IgnoreHospitalEventSubmissionListener(HospitalAuditService hospitalAuditService, SolrSearchFilteringGrid searchResultsGrid, HashMap<String, Checkbox> selectionBoxes
        , HashMap<String, IkasanSolrDocument> selectionItems)
    {
        this.hospitalAuditService = hospitalAuditService;
        this.searchResultsGrid = searchResultsGrid;
        this.selectionBoxes = selectionBoxes;
        this.selectionItems = selectionItems;
    }

    @Override
    public void onComponentEvent(ClickEvent<Button> buttonClickEvent)
    {
        if(!confirmSelectedEvents())
        {
            NotificationHelper.showErrorNotification("At least one record must be selected!");
            return;
        }

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        ExclusionEventActionImpl exclusionEventAction = new ExclusionEventActionImpl();

        HospitalCommentsDialog commentsDialog = new HospitalCommentsDialog(exclusionEventAction, ExclusionEventAction.IGNORED);
        commentsDialog.open();

        commentsDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
        {
            if(!dialogOpenedChangeEvent.isOpened() && commentsDialog.isActioned())
            {
                ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(true);

                if (selected)
                {
                    progressIndicatorDialog.open(String.format("Ignoring %s exclusions", searchResultsGrid.getResultSize()));
                }
                else
                {
                    progressIndicatorDialog.open(String.format("Ignoring %s exclusions",  super.getNumberOfSeletedItems()));
                }

                final UI current = UI.getCurrent();
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() ->
                {
                    try
                    {
                        List<ExclusionEventAction> exclusionEventActions = new ArrayList<>();
                        ExclusionEventAction eventAction;

                        ObjectMapper mapper = new ObjectMapper();

                        if (!selected)
                        {
                            for (IkasanSolrDocument document : this.selectionItems.values())
                            {
                                if (this.shouldActionEvent(document))
                                {
                                    logger.info("resubmitting [{}]", document.getEventId());

                                    // This is where we make a call out to the service...

                                    eventAction = getExclusionEventAction(exclusionEventAction.getComment(), ExclusionEventActionImpl.IGNORED
                                        , document, authentication.getName());

                                    eventAction.setEvent(mapper.writeValueAsString(eventAction));

                                    exclusionEventActions.add(eventAction);
                                }
                            }
                        }
                        else
                        {
                            for (int i = 0; i < searchResultsGrid.getResultSize(); i += 100)
                            {
                                if (progressIndicatorDialog.isCancelled())
                                {
                                    return;
                                }

                                List<IkasanSolrDocument> docs = (List<IkasanSolrDocument>) searchResultsGrid.getDataProvider().fetch
                                    (new Query<>(i, i + 100, Collections.EMPTY_LIST, null, null)).collect(Collectors.toList());

                                for (IkasanSolrDocument document : docs)
                                {
                                    if (this.shouldActionEvent(document))
                                    {
                                        logger.info("resubmitting [{}]", document.getEventId());

                                        // This is where we make a call out to the listener...

                                        eventAction = getExclusionEventAction(exclusionEventAction.getComment(), ExclusionEventActionImpl.IGNORED
                                            , document, authentication.getName());

                                        eventAction.setEvent(mapper.writeValueAsString(eventAction));

                                        hospitalAuditService.save(eventAction);
                                    }

                                    logger.info("ignoring [{}]", document.getErrorUri());
                                }
                            }
                        }

                        hospitalAuditService.save(exclusionEventActions);

                        current.access(() ->
                        {
                            progressIndicatorDialog.close();
                            NotificationHelper.showUserNotification("Ignoring exclusions complete.");
                            this.searchResultsGrid.getDataProvider().refreshAll();
                        });
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        current.access(() ->
                        {
                            progressIndicatorDialog.close();
                            NotificationHelper.showErrorNotification("Error occurred while ignoring exclusions! " + e.getLocalizedMessage());
                        });

                        return;
                    }
                });
            }
        });
    }
}

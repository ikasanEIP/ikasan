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
import org.ikasan.dashboard.ui.general.component.HospitalCommentsDialog;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.model.hospital.ExclusionEventActionImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
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

public class ResubmitHospitalEventSubmissionListener extends HospitalEventActionListener implements ComponentEventListener<ClickEvent<Button>>
{
    private Logger logger = LoggerFactory.getLogger(ResubmitHospitalEventSubmissionListener.class);

    private HospitalAuditService hospitalAuditService;
    private ResubmissionRestServiceImpl resubmissionRestService;

    public ResubmitHospitalEventSubmissionListener(HospitalAuditService hospitalAuditService, ResubmissionRestServiceImpl resubmissionRestService
        , ModuleMetaDataService moduleMetadataService, ErrorReportingService errorReportingService, String actionMessage
        , SolrSearchFilteringGrid searchResultsGrid, HashMap<String, Checkbox> selectionBoxes
        , HashMap<String, IkasanSolrDocument> selectionItems)
    {
        super(actionMessage, errorReportingService, moduleMetadataService, resubmissionRestService
            , searchResultsGrid, selectionBoxes, selectionItems);
        this.hospitalAuditService = hospitalAuditService;
        if(this.hospitalAuditService == null)
        {
            throw new IllegalArgumentException("hospitalAuditService cannot be null!");
        }
        this.resubmissionRestService = resubmissionRestService;
        if(this.hospitalAuditService == null)
        {
            throw new IllegalArgumentException("hospitalAuditService cannot be null!");
        }
    }

    @Override
    public void onComponentEvent(ClickEvent<Button> buttonClickEvent)
    {
        if(!confirmSelectedEvents())
        {
            NotificationHelper.showErrorNotification(getTranslation("message.at-least-one-record-needs-to-be-selected", UI.getCurrent().getLocale()));
            return;
        }

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        ExclusionEventActionImpl exclusionEventAction = new ExclusionEventActionImpl();

        HospitalCommentsDialog commentsDialog = new HospitalCommentsDialog(exclusionEventAction, ExclusionEventAction.RESUBMIT);
        commentsDialog.open();

        commentsDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
        {
            if(!dialogOpenedChangeEvent.isOpened() && commentsDialog.isActioned())
            {
                ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(true);

                if (selected)
                {
                    progressIndicatorDialog.open(String.format(String.format(getTranslation("message.resubmitting-exclusions", UI.getCurrent().getLocale())
                        , searchResultsGrid.getResultSize())));
                }
                else
                {
                    progressIndicatorDialog.open(String.format(String.format(getTranslation("message.resubmitting-exclusions", UI.getCurrent().getLocale())
                        , super.getNumberOfSeletedItems())));
                }

                final UI current = UI.getCurrent();
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try
                    {
                        List<ExclusionEventAction> exclusionEventActions = null;
                        ExclusionEventAction eventAction;

                        ObjectMapper mapper = new ObjectMapper();

                        if (!selected)
                        {
                            List<IkasanSolrDocument> resubmissionEvents = this.selectionItems.values()
                                .stream()
                                .filter(document -> this.shouldActionEvent(document))
                                .collect(Collectors.toList());

                            exclusionEventActions = super.actionHospitalEvents(resubmissionEvents, exclusionEventAction, progressIndicatorDialog,
                                "resubmit", authentication.getName(), current);
                        }
                        else
                        {
                            exclusionEventActions = new ArrayList<>();

                            for (int i = 0; i < searchResultsGrid.getResultSize(); i += 100)
                            {
                                if (progressIndicatorDialog.isCancelled())
                                {
                                    break;
                                }

                                List<IkasanSolrDocument> docs = (List<IkasanSolrDocument>) searchResultsGrid.getDataProvider().fetch
                                    (new Query<>(i, i + 100, Collections.EMPTY_LIST, null, null)).collect(Collectors.toList());

                                List<IkasanSolrDocument> resubmissionEvents = docs
                                    .stream()
                                    .filter(document -> this.shouldActionEvent(document))
                                    .collect(Collectors.toList());

                                exclusionEventActions.addAll(super.actionHospitalEvents(resubmissionEvents, exclusionEventAction, progressIndicatorDialog,
                                    "resubmit", authentication.getName(), current));
                            }
                        }

                        if(exclusionEventActions.size() > 0)
                        {
                            hospitalAuditService.save(exclusionEventActions);
                        }

                        if(progressIndicatorDialog.isOpened())
                        {
                            current.access(() ->
                            {
                                selectionBoxes.keySet().forEach(key -> selectionBoxes.get(key).setValue(false));
                                selectionItems.clear();
                                progressIndicatorDialog.close();
                                NotificationHelper.showUserNotification(getTranslation("message.successfully-resubmitted-exclusions", UI.getCurrent().getLocale()));
                                this.searchResultsGrid.getDataProvider().refreshAll();
                            });
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        current.access(() ->
                        {
                            progressIndicatorDialog.close();
                            NotificationHelper.showErrorNotification(getTranslation("message.error-bulk-resubmit-exclusions", UI.getCurrent().getLocale()));
                        });

                        return;
                    }
                });
            }
        });
    }
}

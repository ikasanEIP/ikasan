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
import org.ikasan.dashboard.ui.search.component.ReplayCommentsDialog;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.model.replay.ReplayAuditEventImpl;
import org.ikasan.dashboard.ui.search.model.replay.ReplayAuditImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.replay.ReplayAudit;
import org.ikasan.spec.replay.ReplayAuditEvent;
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

public class ReplayEventSubmissionListener extends IkasanEventActionListener implements ComponentEventListener<ClickEvent<Button>>
{
    Logger logger = LoggerFactory.getLogger(ReplayEventSubmissionListener.class);

    public ReplayEventSubmissionListener(ModuleMetaDataService moduleMetadataService, SolrSearchFilteringGrid searchResultsGrid, HashMap<String, Checkbox> selectionBoxes
        , HashMap<String, IkasanSolrDocument> selectionItems)
    {
        // todo
        super(moduleMetadataService);
        this.searchResultsGrid = searchResultsGrid;
        this.selectionBoxes = selectionBoxes;
        this.selectionItems = selectionItems;
    }

    @Override
    public void onComponentEvent(ClickEvent<Button> buttonClickEvent)
    {
        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if(!confirmSelectedEvents())
        {
            NotificationHelper.showErrorNotification("At least one record must be selected!");
            return;
        }

        ReplayAudit replayAudit = new ReplayAuditImpl();
        replayAudit.setUser(authentication.getName());

        ReplayCommentsDialog commentsDialog = new ReplayCommentsDialog(replayAudit);
        commentsDialog.open();

        commentsDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
        {
            if(!dialogOpenedChangeEvent.isOpened() && commentsDialog.isSaved())
            {
                ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(true);

                if (selected)
                {
                    progressIndicatorDialog.open(String.format("Replaying %s events", searchResultsGrid.getResultSize()));
                }
                else
                {
                    progressIndicatorDialog.open(String.format("Replaying %s events", this.selectionItems.size()));
                }

                final UI current = UI.getCurrent();
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try
                    {
                        List<ReplayAuditEvent> replayAuditEvents = new ArrayList<>();
                        ReplayAuditEvent replayAuditEvent;

                        if (!selected)
                        {
                            for (IkasanSolrDocument document : this.selectionItems.values())
                            {
                                if (this.shouldActionEvent(document))
                                {
                                    logger.info("replaying [{}]", document.getEventId());

                                    // This is where we make a call out to the listener...

                                    replayAuditEvent = new ReplayAuditEventImpl();
                                    replayAuditEvent.setId(document.getId());
                                    replayAuditEvent.setReplayAudit(replayAudit);
                                    replayAuditEvent.setResultMessage("get message from rest call");
                                    replayAuditEvent.setSuccess(true);
                                    replayAuditEvent.setTimestamp(System.currentTimeMillis());

                                    replayAuditEvents.add(replayAuditEvent);
                                }
                            }

                            ObjectMapper mapper = new ObjectMapper();
                            String json = mapper.writeValueAsString(replayAuditEvents);

                            logger.info("Result json: " + json);
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
                                        logger.info("replaying [{}]", document.getEventId());

                                        // This is where we make a call out to the listener...

                                        replayAuditEvent = new ReplayAuditEventImpl();
                                        replayAuditEvent.setId(document.getId());
                                        replayAuditEvent.setReplayAudit(replayAudit);
                                        replayAuditEvent.setResultMessage("get message from rest call");
                                        replayAuditEvent.setSuccess(true);
                                        replayAuditEvent.setTimestamp(System.currentTimeMillis());

                                        replayAuditEvents.add(replayAuditEvent);
                                    }
                                }
                            }

                            ObjectMapper mapper = new ObjectMapper();
                            String json = mapper.writeValueAsString(replayAuditEvents);

                            logger.info("Result json: " + json);
                        }

                        current.access(() ->
                        {
                            progressIndicatorDialog.close();
                            NotificationHelper.showUserNotification("Replay complete.");
                        });
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        current.access(() ->
                        {
                            progressIndicatorDialog.close();
                            NotificationHelper.showErrorNotification("Error occurred while replaying! " + e.getLocalizedMessage());
                        });

                        return;
                    }
                });
            }
        });
    }
}

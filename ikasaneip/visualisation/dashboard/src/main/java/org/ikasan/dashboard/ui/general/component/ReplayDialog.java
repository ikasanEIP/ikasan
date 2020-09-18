package org.ikasan.dashboard.ui.general.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
import org.ikasan.dashboard.ui.search.model.replay.ReplayAuditEventImpl;
import org.ikasan.dashboard.ui.search.model.replay.ReplayAuditImpl;
import org.ikasan.dashboard.ui.search.model.replay.ReplayDialogDto;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.module.client.ReplayService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.replay.ReplayAuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ReplayDialog extends AbstractEntityViewDialog<IkasanSolrDocument>
{
    Logger logger = LoggerFactory.getLogger(ReplayDialog.class);

    private IkasanSolrDocument replayEvent;

    private TextField moduleNameTf;
    private TextField componentNameTf;
    private TextField flowNameTf;
    private TextField eventIdTf;
    private TextField dateTimeTf;

    private StreamResource streamResource;
    private FileDownloadWrapper buttonWrapper;

    private Button downloadButton;
    private Tooltip downloadButtonTooltip;

    private ReplayService replayRestService;
    private BatchInsert replayAuditService;

    public ReplayDialog(ReplayService replayRestService, BatchInsert replayAuditService)
    {
        this.replayRestService = replayRestService;
        if(this.replayRestService == null)
        {
            throw new IllegalArgumentException("replayRestService cannot be null!");
        }
        this.replayAuditService = replayAuditService;
        if(this.replayAuditService == null)
        {
            throw new IllegalArgumentException("solrGeneralService cannot be null!");
        }

        moduleNameTf = new TextField(getTranslation("text-field.module-name", UI.getCurrent().getLocale(), null));
        flowNameTf = new TextField(getTranslation("text-field.flow-name", UI.getCurrent().getLocale(), null));
        componentNameTf = new TextField(getTranslation("text-field.component-name", UI.getCurrent().getLocale(), null));
        eventIdTf = new TextField(getTranslation("text-field.event-id", UI.getCurrent().getLocale(), null));
        dateTimeTf = new TextField(getTranslation("text-field.date-time", UI.getCurrent().getLocale(), null));
    }

    @Override
    public Component getEntityDetailsLayout()
    {
        Image replayImage = new Image("/frontend/images/replay-service.png", "");
        replayImage.setHeight("70px");

        H3 replayLabel = new H3(getTranslation("label.replay-event-details", UI.getCurrent().getLocale()));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSpacing(true);
        headerLayout.add(replayImage, replayLabel);

        FormLayout formLayout = new FormLayout();

        moduleNameTf.setReadOnly(true);
        formLayout.add(moduleNameTf);

        flowNameTf.setReadOnly(true);
        formLayout.add(flowNameTf);

        eventIdTf.setReadOnly(true);
        formLayout.add(eventIdTf);

        dateTimeTf.setReadOnly(true);
        formLayout.add(dateTimeTf);

        formLayout.setSizeFull();

        downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        downloadButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(downloadButton, getTranslation("tooltip.download-replay-event", UI.getCurrent().getLocale()));

        this.streamResource = new StreamResource("replay.txt"
            , () -> new ByteArrayInputStream(super.aceEditor.getValue().getBytes()));

        buttonWrapper = new FileDownloadWrapper(this.streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        Button replayButton = new Button(getTranslation("button.replay", UI.getCurrent().getLocale(), null));
        replayButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            final UI current = UI.getCurrent();
            final I18NProvider i18NProvider = VaadinService.getCurrent().getInstantiator().getI18NProvider();

            ReplayDialogDto replayDialogDto = new ReplayDialogDto();
            ReplayCommentsDialog replayCommentsDialog = new ReplayCommentsDialog(replayDialogDto);

            replayCommentsDialog.open();

            replayCommentsDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if(!dialogOpenedChangeEvent.isOpened() && replayCommentsDialog.isSaved())
                {
                    ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(true);

                    progressIndicatorDialog.open(current.getTranslation("message.replaying-event"
                        , UI.getCurrent().getLocale()));

                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try
                        {
                            List<ReplayAuditEvent> replayAuditEvents = new ArrayList<>();
                            ReplayAuditEvent replayAuditEvent;

                            boolean result = this.replayRestService.replay(replayDialogDto.getTargetServer(), replayDialogDto.getAuthenticationUser(),
                                replayDialogDto.getPassword(), this.replayEvent.getModuleName(), this.replayEvent.getFlowName(), this.replayEvent.getPayloadRaw());

                            replayAuditEvent = new ReplayAuditEventImpl();
                            replayAuditEvent.setId(this.replayEvent.getId());
                            replayAuditEvent.setReplayAudit(new ReplayAuditImpl(replayDialogDto.getUser(),
                                replayDialogDto.getReplayReason(), replayDialogDto.getTargetServer(), System.currentTimeMillis()));
                            if(result)
                            {
                                replayAuditEvent.setResultMessage(String.format(i18NProvider.getTranslation("message.replay-audit-success"
                                    , current.getLocale()), replayEvent.getId()));
                            }
                            else
                            {
                                replayAuditEvent.setResultMessage(String.format(i18NProvider.getTranslation("message.replay-audit-failure"
                                    , current.getLocale()), replayEvent.getId()));
                            }
                            replayAuditEvent.setSuccess(result);
                            replayAuditEvent.setTimestamp(System.currentTimeMillis());

                            replayAuditEvents.add(replayAuditEvent);

                            current.access(() ->
                            {
                                progressIndicatorDialog.close();
                                NotificationHelper.showUserNotification(i18NProvider.getTranslation("message.replay-complete"
                                    , current.getLocale()));
                            });

                            this.replayAuditService.insert(replayAuditEvents);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            current.access(() ->
                            {
                                progressIndicatorDialog.close();
                                NotificationHelper.showUserNotification(i18NProvider.getTranslation("message.replay-error"
                                    , current.getLocale()));
                            });

                            return;
                        }
                    });
                }
            });
        });

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, formLayout, buttonWrapper, replayButton, downloadButtonTooltip);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonWrapper, downloadButtonTooltip);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, replayButton);

        return layout;
    }

    @Override
    public void populate(IkasanSolrDocument replayEvent)
    {
        super.title.setText("Replay " + replayEvent.getEventId());

        this.replayEvent = replayEvent;
        this.moduleNameTf.setValue(replayEvent.getModuleName());
        this.flowNameTf.setValue(replayEvent.getFlowName());
        this.eventIdTf.setValue(replayEvent.getEventId());
        this.dateTimeTf.setValue(DateFormatter.getFormattedDate(replayEvent.getTimestamp()));

        super.open(replayEvent.getEvent());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.downloadButtonTooltip.attachToComponent(downloadButton);
    }
}

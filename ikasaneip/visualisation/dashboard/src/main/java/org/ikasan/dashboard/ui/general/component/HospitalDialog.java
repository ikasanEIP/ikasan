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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import org.ikasan.dashboard.ui.search.model.hospital.ExclusionEventActionImpl;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HospitalDialog extends AbstractEntityViewDialog<IkasanSolrDocument>
{
    private TextField moduleNameTf;
    private TextField flowNameTf;
    private TextField eventIdTf;
    private TextField errorUriTf;
    private TextField errorActionTf;
    private TextField dateTimeTf;

    private StreamResource streamResource;
    private FileDownloadWrapper buttonWrapper;
    private ErrorReportingService errorReportingService;

    private ErrorOccurrence<String> errorOccurrence;
    private String exclusionPayload;

    private Button resubmitButton;
    private Button ignoreButton;

    private HospitalAuditService hospitalAuditService;

    private IkasanSolrDocument ikasanSolrDocument;

    private Button downloadButton;
    private Tooltip downloadButtonTooltip;

    private ResubmissionRestServiceImpl resubmissionRestService;
    private ModuleMetaDataService moduleMetadataService;

    private String translatedEventActionMessage;

    public HospitalDialog(ErrorReportingService errorReportingService, HospitalAuditService hospitalAuditService,
                          ResubmissionRestServiceImpl resubmissionRestService, ModuleMetaDataService moduleMetadataService)
    {
        this.errorReportingService = errorReportingService;
        if(this.errorReportingService == null)
        {
            throw new IllegalArgumentException("errorReportingService cannot be null!");
        }
        this.hospitalAuditService = hospitalAuditService;
        if(this.hospitalAuditService == null)
        {
            throw new IllegalArgumentException("hospitalAuditService cannot be null!");
        }
        this.resubmissionRestService = resubmissionRestService;
        if(this.resubmissionRestService == null)
        {
            throw new IllegalArgumentException("resubmissionRestService cannot be null!");
        }
        this.moduleMetadataService = moduleMetadataService;
        if(this.moduleMetadataService == null)
        {
            throw new IllegalArgumentException("moduleMetadataService cannot be null!");
        }

        moduleNameTf = new TextField(getTranslation("text-field.module-name", UI.getCurrent().getLocale(), null));
        flowNameTf = new TextField(getTranslation("text-field.flow-name", UI.getCurrent().getLocale(), null));
        eventIdTf = new TextField(getTranslation("text-field.event-id", UI.getCurrent().getLocale(), null));
        errorUriTf = new TextField(getTranslation("text-field.error-uri", UI.getCurrent().getLocale(), null));
        dateTimeTf = new TextField(getTranslation("text-field.date-time", UI.getCurrent().getLocale(), null));
        errorActionTf = new TextField(getTranslation("text-field.error-action", UI.getCurrent().getLocale(), null));

        translatedEventActionMessage = getTranslation("message.resubmission-event-action"
            , UI.getCurrent().getLocale());
    }


    public Component getEntityDetailsLayout()
    {
        Image hospitalImage = new Image("/frontend/images/hospital-service.png", "");
        hospitalImage.setHeight("70px");

        H3 hospitalLabel = new H3(getTranslation("label.hospital-event-details", UI.getCurrent().getLocale(), null));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSpacing(true);
        headerLayout.add(hospitalImage, hospitalLabel);

        FormLayout formLayout = new FormLayout();

        moduleNameTf.setReadOnly(true);
        formLayout.add(moduleNameTf);

        flowNameTf.setReadOnly(true);
        formLayout.add(flowNameTf);

        eventIdTf.setReadOnly(true);
        formLayout.add(eventIdTf);

        errorUriTf.setReadOnly(true);
        formLayout.add(errorUriTf);

        errorActionTf.setReadOnly(true);
        formLayout.add(errorActionTf);

        dateTimeTf.setReadOnly(true);
        formLayout.add(dateTimeTf);

        formLayout.setSizeFull();

        downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        downloadButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(downloadButton, getTranslation("tooltip.download-hospital-event", UI.getCurrent().getLocale()));

        this.streamResource = new StreamResource("exclusion.txt"
            , () -> new ByteArrayInputStream(super.juicyAceEditor.getValue().getBytes()));

        buttonWrapper = new FileDownloadWrapper(this.streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        resubmitButton = new Button(getTranslation("button.resubmit", UI.getCurrent().getLocale(), null));
        ignoreButton = new Button(getTranslation("button.ignore", UI.getCurrent().getLocale(), null));

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        resubmitButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            final ExclusionEventAction exclusionEventAction = new ExclusionEventActionImpl();

            HospitalCommentsDialog commentsDialog = new HospitalCommentsDialog(exclusionEventAction, ExclusionEventAction.RESUBMIT);
            commentsDialog.open();

            commentsDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if (!dialogOpenedChangeEvent.isOpened() && commentsDialog.isActioned())
                {
                    final UI current = UI.getCurrent();

                    ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(true);
                    progressIndicatorDialog.open(getTranslation("notification.re-submitting-hospital-event", UI.getCurrent().getLocale()));

                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(() ->
                    {
                        ModuleMetaData moduleMetaData = this.moduleMetadataService.findById(ikasanSolrDocument.getModuleName());
                        boolean result = this.resubmissionRestService.resubmit(moduleMetaData.getUrl(), ikasanSolrDocument.getModuleName(),
                            ikasanSolrDocument.getFlowName(), "resubmit", this.getErrorUri(ikasanSolrDocument.getId()));

                        if(!result)
                        {
                            current.access(() ->
                            {
                                progressIndicatorDialog.close();
                                NotificationHelper.showErrorNotification(getTranslation("error.exclusion-resubmission-error", UI.getCurrent().getLocale()));
                            });

                            return;
                        }

                        ExclusionEventAction eventAction = this.getExclusionEventAction(exclusionEventAction.getComment(), ExclusionEventAction.RESUBMIT,
                            this.ikasanSolrDocument, authentication.getName());
                        this.hospitalAuditService.save(eventAction);

                        current.access(() ->
                        {
                            resubmitButton.setVisible(false);
                            ignoreButton.setVisible(false);
                            progressIndicatorDialog.close();
                            NotificationHelper.showUserNotification(getTranslation("notification.hospital-event-resubmit-success", UI.getCurrent().getLocale()));
                            this.close();
                        });
                    });
                }
            });
        });


        ignoreButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {

            ExclusionEventActionImpl exclusionEventAction = new ExclusionEventActionImpl();

            HospitalCommentsDialog commentsDialog = new HospitalCommentsDialog(exclusionEventAction, ExclusionEventAction.IGNORED);
            commentsDialog.open();

            commentsDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
            {
                if (!dialogOpenedChangeEvent.isOpened() && commentsDialog.isActioned())
                {
                    final UI current = UI.getCurrent();

                    ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(true);
                    progressIndicatorDialog.open(String.format(getTranslation("notification.ignoring-hospital-event", UI.getCurrent().getLocale())));

                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(() ->
                    {
                        ModuleMetaData moduleMetaData = this.moduleMetadataService.findById(ikasanSolrDocument.getModuleName());
                        boolean result = this.resubmissionRestService.resubmit(moduleMetaData.getUrl(), ikasanSolrDocument.getModuleName(),
                            ikasanSolrDocument.getFlowName(), "ignore", this.getErrorUri(ikasanSolrDocument.getId()));

                        if(!result)
                        {
                            current.access(() ->
                            {
                                progressIndicatorDialog.close();
                                NotificationHelper.showErrorNotification(getTranslation("error.exclusion-ignore-error", UI.getCurrent().getLocale()));
                            });

                            return;
                        }

                        ExclusionEventAction eventAction = this.getExclusionEventAction(exclusionEventAction.getComment(), ExclusionEventAction.IGNORED,
                            this.ikasanSolrDocument, authentication.getName());

                        this.hospitalAuditService.save(eventAction);

                        current.access(() ->
                        {
                            resubmitButton.setVisible(false);
                            ignoreButton.setVisible(false);
                            progressIndicatorDialog.close();
                            NotificationHelper.showUserNotification(getTranslation("notification.hospital-event-ignore-success", UI.getCurrent().getLocale()));
                            this.close();
                        });
                    });
                }
            });
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(resubmitButton, ignoreButton);

        ComponentSecurityVisibility.applySecurity(buttonLayout, SecurityConstants.ACTIONED_EXCLUSION_ADMIN
            , SecurityConstants.EXCLUSION_WRITE, SecurityConstants.ALL_AUTHORITY);

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, formLayout, buttonWrapper, buttonLayout, downloadButtonTooltip);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonWrapper);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);

        Tab exclusionTab = new Tab(getTranslation("tab-label.exclusion", UI.getCurrent().getLocale(), null));
        Tab errorTab = new Tab(getTranslation("tab-label.error", UI.getCurrent().getLocale(), null));
        Tabs tabs = new Tabs(exclusionTab, errorTab);

        tabs.addSelectedChangeListener(event ->
        {
            if(tabs.getSelectedTab().equals(exclusionTab))
            {
                super.aceEditor.setValue(Optional.ofNullable(formatXml(this.exclusionPayload)).orElse(getTranslation("placeholder.not-content", UI.getCurrent().getLocale())));
            }
            else
            {
                super.aceEditor.setValue(Optional.ofNullable(formatXml(this.errorOccurrence.getErrorDetail())).orElse(getTranslation("placeholder.not-content", UI.getCurrent().getLocale())));
            }
        });

        super.aceEditor.setHeight("48vh");

        layout.add(tabs);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, tabs);

        return layout;
    }

    @Override
    public void populate(IkasanSolrDocument ikasanSolrDocument)
    {
        this.ikasanSolrDocument = ikasanSolrDocument;
        this.moduleNameTf.setValue(Optional.ofNullable(ikasanSolrDocument.getModuleName()).orElse(""));
        this.flowNameTf.setValue(Optional.ofNullable(ikasanSolrDocument.getFlowName()).orElse(""));
        this.eventIdTf.setValue(Optional.ofNullable(ikasanSolrDocument.getEventId()).orElse(""));
        this.errorUriTf.setValue(Optional.ofNullable(this.getErrorUri(ikasanSolrDocument.getId())).orElse(""));
        this.dateTimeTf.setValue(DateFormatter.getFormattedDate(ikasanSolrDocument.getTimestamp()));

        this.errorOccurrence = (ErrorOccurrence<String>) this.errorReportingService.find(this.getErrorUri(ikasanSolrDocument.getId()));
        this.exclusionPayload = ikasanSolrDocument.getEvent();
        this.errorActionTf.setValue(Optional.ofNullable(this.errorOccurrence.getAction()).orElse(""));

        ComponentSecurityVisibility.applySecurity(resubmitButton, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(ignoreButton, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.ALL_AUTHORITY);

        super.open(ikasanSolrDocument.getEvent());
    }

    /**
     * Helper method to get an initialised exclusion event action.
     *
     * @param comment
     * @param action
     * @param document
     * @param user
     * @return
     */
    protected ExclusionEventAction getExclusionEventAction(String comment, String action, IkasanSolrDocument document, String user)
    {
        ExclusionEventAction exclusionEventAction = new ExclusionEventActionImpl();
        exclusionEventAction.setComment(comment);
        exclusionEventAction.setActionedBy(user);
        exclusionEventAction.setAction(String.format(translatedEventActionMessage, comment, action, user, errorOccurrence.getEventAsString()));
        // the error uri is in fact the id of excluded events
        exclusionEventAction.setErrorUri(document.getId());
        exclusionEventAction.setModuleName(document.getModuleName());
        exclusionEventAction.setFlowName(document.getFlowName());
        exclusionEventAction.setTimestamp(System.currentTimeMillis());
        exclusionEventAction.setEvent(document.getEvent());

        return exclusionEventAction;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.downloadButtonTooltip.attachToComponent(downloadButton);
    }

    private String getErrorUri(String id){
        if(id.contains(":")) {
            id = id.substring(id.lastIndexOf(":") + 1);
        }
        return id;
    }
}

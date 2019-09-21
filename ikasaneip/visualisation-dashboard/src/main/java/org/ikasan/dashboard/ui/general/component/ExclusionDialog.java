package org.ikasan.dashboard.ui.general.component;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;

public class ExclusionDialog extends AbstractEntityViewDialog<IkasanSolrDocument>
{
    private TextField moduleNameTf;
    private TextField flowNameTf;
    private TextField eventIdTf;
    private TextField errorUriTf;
    private TextField dateTimeTf;

    private StreamResource streamResource;
    private FileDownloadWrapper buttonWrapper;
    private ErrorReportingService errorReportingService;

    private ErrorOccurrence<String> errorOccurrence;
    private String exclusionPayload;


    public ExclusionDialog(ErrorReportingService errorReportingService)
    {
        this.errorReportingService = errorReportingService;
        if(this.errorReportingService == null)
        {
            throw new IllegalArgumentException("errorReportingService cannot be null!");
        }

        moduleNameTf = new TextField(getTranslation("text-field.module-name", UI.getCurrent().getLocale(), null));
        flowNameTf = new TextField(getTranslation("text-field.flow-name", UI.getCurrent().getLocale(), null));
        eventIdTf = new TextField(getTranslation("text-field.event-id", UI.getCurrent().getLocale(), null));
        errorUriTf = new TextField(getTranslation("text-field.error-uri", UI.getCurrent().getLocale(), null));
        dateTimeTf = new TextField(getTranslation("text-field.date-time", UI.getCurrent().getLocale(), null));
    }


    public Component getEntityDetailsLayout()
    {
        H3 userProfileLabel = new H3(getTranslation("label.exclusion-event-details", UI.getCurrent().getLocale(), null));

        FormLayout formLayout = new FormLayout();

        moduleNameTf.setReadOnly(true);
        formLayout.add(moduleNameTf);

        flowNameTf.setReadOnly(true);
        formLayout.add(flowNameTf);

        eventIdTf.setReadOnly(true);
        formLayout.add(eventIdTf);

        errorUriTf.setReadOnly(true);
        formLayout.add(errorUriTf);

        dateTimeTf.setReadOnly(true);
        formLayout.add(dateTimeTf);

        formLayout.setSizeFull();

        Button downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        downloadButton.getElement().setProperty("title", getTranslation("help-message.download-exclusion", UI.getCurrent().getLocale(), null));

        this.streamResource = new StreamResource("exclusion.txt"
            , () -> new ByteArrayInputStream(super.juicyAceEditor.getValue().getBytes() ));

        buttonWrapper = new FileDownloadWrapper(this.streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        Button resubmitButton = new Button(getTranslation("button.resubmit", UI.getCurrent().getLocale(), null));
        Button ignoreButton = new Button(getTranslation("button.ignore", UI.getCurrent().getLocale(), null));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(resubmitButton, ignoreButton);

        ComponentSecurityVisibility.applySecurity(buttonLayout, SecurityConstants.ACTIONED_EXCLUSION_ADMIN
            , SecurityConstants.EXCLUSION_WRITE, SecurityConstants.ALL_AUTHORITY);

        VerticalLayout layout = new VerticalLayout();
        layout.add(userProfileLabel, formLayout, buttonWrapper, buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonWrapper);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);

        Tab exclusionTab = new Tab(getTranslation("tab-label.exclusion", UI.getCurrent().getLocale(), null));
        Tab errorTab = new Tab(getTranslation("tab-label.error", UI.getCurrent().getLocale(), null));
        Tabs tabs = new Tabs(exclusionTab, errorTab);

        tabs.addSelectedChangeListener(event ->
        {
            if(tabs.getSelectedTab().equals(exclusionTab))
            {
                super.juicyAceEditor.setValue(this.exclusionPayload);
            }
            else
            {
                super.juicyAceEditor.setValue(this.errorOccurrence.getErrorDetail());
            }
        });

        layout.add(tabs);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, tabs);

        return layout;
    }

    @Override
    public void populate(IkasanSolrDocument ikasanSolrDocument)
    {
        this.moduleNameTf.setValue(ikasanSolrDocument.getModuleName());
        this.flowNameTf.setValue(ikasanSolrDocument.getFlowName());
        this.eventIdTf.setValue(ikasanSolrDocument.getEventId());
        this.errorUriTf.setValue(ikasanSolrDocument.getId());
        this.dateTimeTf.setValue(DateFormatter.getFormattedDate(ikasanSolrDocument.getTimestamp()));

        this.errorOccurrence = (ErrorOccurrence<String>) this.errorReportingService.find(ikasanSolrDocument.getId());
        this.exclusionPayload = ikasanSolrDocument.getEvent();

        super.open(ikasanSolrDocument.getEvent());
    }
}

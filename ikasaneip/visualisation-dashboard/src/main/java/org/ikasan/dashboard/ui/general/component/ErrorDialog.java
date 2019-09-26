package org.ikasan.dashboard.ui.general.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;

public class ErrorDialog extends AbstractEntityViewDialog<IkasanSolrDocument>
{
    private TextField moduleNameTf;
    private TextField componentNameTf;
    private TextField flowNameTf;
    private TextField eventIdTf;
    private TextField dateTimeTf;
    private TextField errorUriTf;
    private TextField errorClassTf;

    private StreamResource streamResource;
    private FileDownloadWrapper buttonWrapper;

    private String errorEvent;
    private String errorDetails;

    public ErrorDialog()
    {
        moduleNameTf = new TextField(getTranslation("text-field.module-name", UI.getCurrent().getLocale(), null));
        flowNameTf = new TextField(getTranslation("text-field.flow-name", UI.getCurrent().getLocale(), null));
        componentNameTf = new TextField(getTranslation("text-field.component-name", UI.getCurrent().getLocale(), null));
        eventIdTf = new TextField(getTranslation("text-field.event-id", UI.getCurrent().getLocale(), null));
        errorUriTf = new TextField(getTranslation("text-field.error-uri", UI.getCurrent().getLocale(), null));
        dateTimeTf = new TextField(getTranslation("text-field.date-time", UI.getCurrent().getLocale(), null));
        errorClassTf = new TextField(getTranslation("text-field.exception-class", UI.getCurrent().getLocale(), null));
    }

    @Override
    public Component getEntityDetailsLayout()
    {
        Image errorImage = new Image("/frontend/images/error-service.png", "");
        errorImage.setHeight("70px");

        H3 errorLabel = new H3(getTranslation("label.error-event-details", UI.getCurrent().getLocale(), null));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSpacing(true);
        headerLayout.add(errorImage, errorLabel);

        FormLayout formLayout = new FormLayout();

        moduleNameTf.setReadOnly(true);
        formLayout.add(moduleNameTf);

        componentNameTf.setReadOnly(true);
        formLayout.add(componentNameTf);

        flowNameTf.setReadOnly(true);
        formLayout.add(flowNameTf);

        eventIdTf.setReadOnly(true);
        formLayout.add(eventIdTf);

        dateTimeTf.setReadOnly(true);
        formLayout.add(dateTimeTf);

        errorUriTf.setReadOnly(true);
        formLayout.add(errorUriTf);

        errorClassTf.setReadOnly(true);
        formLayout.add(errorClassTf);
        formLayout.setColspan(errorClassTf, 2);

        formLayout.setSizeFull();

        Button downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        Tooltip downloadButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(downloadButton, getTranslation("tooltip.download-error-event", UI.getCurrent().getLocale()));

        this.streamResource = new StreamResource("error.txt"
            , () -> new ByteArrayInputStream(super.juicyAceEditor.getValue().getBytes() ));

        buttonWrapper = new FileDownloadWrapper(this.streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, formLayout, buttonWrapper, downloadButtonTooltip);

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonWrapper, downloadButtonTooltip);

        Tab errorTab = new Tab(getTranslation("tab-label.error", UI.getCurrent().getLocale(), null));
        Tab errorEventTab = new Tab(getTranslation("tab-label.error-event", UI.getCurrent().getLocale(), null));
        Tabs tabs = new Tabs(errorTab, errorEventTab);

        tabs.addSelectedChangeListener(event ->
        {
            if(tabs.getSelectedTab().equals(errorTab))
            {
                super.juicyAceEditor.setValue(this.errorDetails);
            }
            else
            {
                super.juicyAceEditor.setValue(this.errorEvent);
            }
        });

        juicyAceEditor.setHeight("45vh");

        layout.add(tabs);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, tabs);

        return layout;
    }

    @Override
    public void populate(IkasanSolrDocument errorEvent)
    {
        this.moduleNameTf.setValue(errorEvent.getModuleName());
        this.flowNameTf.setValue(errorEvent.getFlowName());
        this.componentNameTf.setValue(errorEvent.getComponentName());
        this.eventIdTf.setValue(errorEvent.getEventId());
        this.errorUriTf.setValue(errorEvent.getErrorUri());
        this.dateTimeTf.setValue(DateFormatter.getFormattedDate(errorEvent.getTimestamp()));

        if (errorEvent.getExceptionClass() != null)
        {
            this.errorClassTf.setValue(errorEvent.getExceptionClass());
        }

        this.errorEvent = errorEvent.getEvent();
        this.errorDetails = errorEvent.getErrorDetail();

        super.open(errorEvent.getErrorDetail());
    }
}

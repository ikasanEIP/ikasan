package org.ikasan.dashboard.ui.general.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.AttachEvent;
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
import java.util.Optional;

public class ErrorDialog extends AbstractEntityViewDialog<IkasanSolrDocument>
{
    private TextField moduleNameTf;
    private TextField componentNameTf;
    private TextField flowNameTf;
    private TextField eventIdTf;
    private TextField errorActionTf;
    private TextField dateTimeTf;
    private TextField errorUriTf;
    private TextField errorClassTf;

    private StreamResource streamResource;
    private FileDownloadWrapper buttonWrapper;
    private Tooltip downloadButtonTooltip;

    private String errorEvent;
    private String errorDetails;

    public ErrorDialog()
    {
        moduleNameTf = new TextField(getTranslation("text-field.module-name", UI.getCurrent().getLocale(), null));
        flowNameTf = new TextField(getTranslation("text-field.flow-name", UI.getCurrent().getLocale(), null));
        componentNameTf = new TextField(getTranslation("text-field.component-name", UI.getCurrent().getLocale(), null));
        eventIdTf = new TextField(getTranslation("text-field.event-id", UI.getCurrent().getLocale(), null));
        errorUriTf = new TextField(getTranslation("text-field.error-uri", UI.getCurrent().getLocale(), null));
        errorActionTf = new TextField(getTranslation("text-field.error-action", UI.getCurrent().getLocale(), null));
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

        errorActionTf.setReadOnly(true);
        formLayout.add(errorActionTf);

        errorClassTf.setReadOnly(true);
        formLayout.add(errorClassTf);

        formLayout.setSizeFull();

        Button downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        downloadButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(downloadButton
            , getTranslation("tooltip.download-error-event", UI.getCurrent().getLocale()));

        this.streamResource = new StreamResource("error.txt"
            , () -> new ByteArrayInputStream(super.aceEditor.getValue().getBytes() ));

        buttonWrapper = new FileDownloadWrapper(this.streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, formLayout, buttonWrapper, downloadButtonTooltip);

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonWrapper);

        Tab errorTab = new Tab(getTranslation("tab-label.error", UI.getCurrent().getLocale()));
        Tab errorEventTab = new Tab(getTranslation("tab-label.error-event", UI.getCurrent().getLocale()));
        Tabs tabs = new Tabs(errorTab, errorEventTab);

        tabs.addSelectedChangeListener(event ->
        {
            if(tabs.getSelectedTab().equals(errorTab))
            {
                super.aceEditor.setValue(Optional.ofNullable(formatXml(errorDetails))
                    .orElse(getTranslation("placeholder.not-content", UI.getCurrent().getLocale())));
            }
            else
            {
                super.aceEditor.setValue(Optional.ofNullable(formatXml(errorEvent))
                    .orElse(getTranslation("placeholder.not-content", UI.getCurrent().getLocale())));
            }
        });

        layout.add(tabs);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, tabs);

        return layout;
    }

    @Override
    public void populate(IkasanSolrDocument errorEvent)
    {
        this.moduleNameTf.setValue(Optional.ofNullable(errorEvent.getModuleName()).orElse(""));
        this.flowNameTf.setValue(Optional.ofNullable(errorEvent.getFlowName()).orElse(""));
        this.componentNameTf.setValue(Optional.ofNullable(errorEvent.getComponentName()).orElse(""));
        this.eventIdTf.setValue(Optional.ofNullable(errorEvent.getEventId()).orElse(""));
        this.errorUriTf.setValue(Optional.ofNullable(errorEvent.getErrorUri()).orElse(""));
        this.errorActionTf.setValue(Optional.ofNullable(errorEvent.getErrorAction()).orElse(""));
        this.dateTimeTf.setValue(DateFormatter.getFormattedDate(errorEvent.getTimestamp()));
        this.errorClassTf.setValue(Optional.ofNullable(errorEvent.getExceptionClass()).orElse(""));

        this.errorEvent = errorEvent.getEvent();
        this.errorDetails = errorEvent.getErrorDetail();

        super.open(errorEvent.getErrorDetail());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.downloadButtonTooltip.attachToComponent(buttonWrapper);
    }
}

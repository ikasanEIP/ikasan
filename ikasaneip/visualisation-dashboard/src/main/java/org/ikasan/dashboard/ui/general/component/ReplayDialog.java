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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;

public class ReplayDialog extends AbstractEntityViewDialog<IkasanSolrDocument>
{
    private TextField moduleNameTf;
    private TextField componentNameTf;
    private TextField flowNameTf;
    private TextField eventIdTf;
    private TextField dateTimeTf;

    private StreamResource streamResource;
    private FileDownloadWrapper buttonWrapper;

    private Button downloadButton;
    private Tooltip downloadButtonTooltip;

    private ReplayRestServiceImpl replayRestService;

    public ReplayDialog(ReplayRestServiceImpl replayRestService)
    {
        this.replayRestService = replayRestService;
        if(this.replayRestService == null)
        {
            throw new IllegalArgumentException("replayRestService cannot be null!");
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

        H3 replayLabel = new H3(getTranslation("label.replay-event-details", UI.getCurrent().getLocale(), null));

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
            , () -> new ByteArrayInputStream(super.juicyAceEditor.getValue().getBytes()));

        buttonWrapper = new FileDownloadWrapper(this.streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        Button replayButton = new Button(getTranslation("button.replay", UI.getCurrent().getLocale(), null));

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, formLayout, buttonWrapper, replayButton, downloadButtonTooltip);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonWrapper, downloadButtonTooltip);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, replayButton);

        return layout;
    }

    @Override
    public void populate(IkasanSolrDocument replayEvent)
    {
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

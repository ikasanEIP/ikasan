package org.ikasan.dashboard.ui.administration.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import org.ikasan.dashboard.ui.administration.util.ConfigurationChangedSystemEventFormatter;
import org.ikasan.dashboard.ui.administration.util.SystemEventFormatter;
import org.ikasan.dashboard.ui.general.component.AbstractEntityViewDialog;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.systemevent.model.SystemEventImpl;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;

public class SystemEventDialog extends AbstractEntityViewDialog<IkasanSolrDocument>
{
    private TextField actionedByTf;
    private TextField contextTf;
    private TextField dateTimeTf;

    private StreamResource streamResource;
    private  FileDownloadWrapper buttonWrapper;

    private Button downloadButton;
    private Tooltip downloadButtonTooltip;

    private ObjectMapper objectMapper = new ObjectMapper();

    public SystemEventDialog()
    {
        actionedByTf = new TextField(getTranslation("text-field.action-performed-by", UI.getCurrent().getLocale(), null));
        contextTf = new TextField(getTranslation("text-field.system-event-context", UI.getCurrent().getLocale(), null));
        dateTimeTf = new TextField(getTranslation("text-field.date-time", UI.getCurrent().getLocale(), null));
    }

    @Override
    public Component getEntityDetailsLayout()
    {
        H3 wiretapLabel = new H3(getTranslation("label.system-event-details", UI.getCurrent().getLocale(), null));

        FormLayout formLayout = new FormLayout();

        actionedByTf.setReadOnly(true);
        formLayout.add(actionedByTf);

        contextTf.setReadOnly(true);
        formLayout.add(contextTf);

        dateTimeTf.setReadOnly(true);
        formLayout.add(dateTimeTf);

        formLayout.setWidthFull();
        formLayout.setHeight("200px");

        downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        downloadButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(downloadButton, getTranslation("tooltip.download-system-event", UI.getCurrent().getLocale()));

        this.streamResource = new StreamResource("system-event.txt"
            , () -> new ByteArrayInputStream(super.aceEditor.getValue().getBytes() ));

        buttonWrapper = new FileDownloadWrapper(this.streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        VerticalLayout layout = new VerticalLayout();
        layout.add(wiretapLabel, formLayout, buttonWrapper, downloadButtonTooltip);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonWrapper);
        layout.setWidthFull();
        return layout;
    }

    @Override
    public void populate(IkasanSolrDocument systemEvent)
    {
        SystemEventImpl systemEventImpl = null;
        try {
            systemEventImpl = objectMapper.readValue(systemEvent.getEvent(), SystemEventImpl.class);
            super.title.setText("System Event");
            this.actionedByTf.setValue(systemEventImpl.getActor());
            this.contextTf.setValue(SystemEventFormatter.getContext(systemEventImpl));

            this.dateTimeTf.setValue(DateFormatter.getFormattedDate(systemEvent.getTimestamp()));

            open(systemEventImpl.getAction(), systemEventImpl);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void open(String event, SystemEventImpl systemEventImpl)
    {
        if(!initialised)
        {
            init();
            initialised = true;
        }

        open();

        if(event.startsWith("Configuration Updated") || event.startsWith("Configuration Deleted")) {
            try {
                event = ConfigurationChangedSystemEventFormatter.format(event);
            }
            catch (Exception e) {
                //ignore if we cannot format
            }
            aceEditor.setValue(event);
        }
        else {
            aceEditor.setValue(SystemEventFormatter.getEvent(systemEventImpl));
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.downloadButtonTooltip.attachToComponent(downloadButton);
    }
}

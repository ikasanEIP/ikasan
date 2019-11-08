package org.ikasan.dashboard.ui.general.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public class ConfigurationDialog extends Dialog
{
    private static Logger logger = LoggerFactory.getLogger(ConfigurationDialog.class);

    private ConfigurationRestServiceImpl configurationRestService;
    private ConfigurationMetaData configurationMetaData;
    private Module module;
    private String flowName;
    private String componentName;
    private Button downloadButton;
    private Tooltip downloadButtonTooltip;

    public ConfigurationDialog(Module module, String flowName, String componentName
        , ConfigurationRestServiceImpl configurationRestService)
    {
        this.module = module;
        this.flowName = flowName;
        this.componentName = componentName;
        this.configurationRestService = configurationRestService;
        init();
    }

    private void init()
    {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Image replayImage = new Image("/frontend/images/configuration-service.png", "");
        replayImage.setHeight("70px");

        this.loadConfigurationMetaDataFromModule();

        H3 replayLabel = new H3(String.format(getTranslation("label.configuration-management", UI.getCurrent().getLocale())
            , this.configurationMetaData.getConfigurationId()));


        downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        downloadButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(downloadButton, getTranslation("tooltip.download-hospital-event", UI.getCurrent().getLocale()));

        ObjectMapper objectMapper = new ObjectMapper();
        StreamResource streamResource = new StreamResource("configuration-"+ this.configurationMetaData.getConfigurationId() + ".txt"
            , () ->
        {
            try
            {
                return new ByteArrayInputStream(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.configurationMetaData).getBytes());
            }
            catch (JsonProcessingException e)
            {
                logger.warn("Could not create download button: " + e.getMessage());
            }
            return null;
        });

//        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(streamResource);
//        buttonWrapper.wrapComponent(downloadButton);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSpacing(true);
        headerLayout.add(replayImage, replayLabel, downloadButtonTooltip);

        layout.add(headerLayout);
        layout.add(new Divider());

        for(ConfigurationParameterMetaData configurationParameterMetaData: ((List<ConfigurationParameterMetaData >)configurationMetaData.getParameters()))
        {
            if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"))
            {
                layout.add(this.manageBooleanConfiguration(configurationParameterMetaData));
                layout.add(new Divider());
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"))
            {
                layout.add(this.manageIntegerConfiguration(configurationParameterMetaData));
                layout.add(new Divider());
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterStringImpl"))
            {
                layout.add(this.manageStringConfiguration(configurationParameterMetaData));
                layout.add(new Divider());
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterLongImpl"))
            {
                layout.add(this.manageLongConfiguration(configurationParameterMetaData));
                layout.add(new Divider());
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterMapImpl"))
            {
                layout.add(this.manageMapConfiguration(configurationParameterMetaData));
                layout.add(new Divider());
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterListImpl"))
            {
                layout.add(this.manageListConfiguration(configurationParameterMetaData));
                layout.add(new Divider());
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl"))
            {
                layout.add(this.manageMaskedStringConfiguration(configurationParameterMetaData));
                layout.add(new Divider());
            }
        }

//        Div div = new Div();
//        div.setText(configurationMetaData.toString());
//        layout.add(div);

        Button saveButton = new Button(getTranslation("button.save", UI.getCurrent().getLocale()));
        saveButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            boolean success = this.configurationRestService.storeConfiguration(this.module.getUrl(), this.configurationMetaData);
            if(success)
            {
                this.loadConfigurationMetaDataFromModule();
                NotificationHelper.showUserNotification(getTranslation("message.configuration-save-successful", UI.getCurrent().getLocale()));
            }
            else
            {
                NotificationHelper.showErrorNotification(getTranslation("message.configuration-save-unsuccessful", UI.getCurrent().getLocale()));
            }
        });

        Button cancelButton = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale()));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(saveButton, cancelButton);

        layout.add(buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);

        this.setWidth("700px");
        this.add(layout);
    }

    private void loadConfigurationMetaDataFromModule()
    {
        this.configurationMetaData = this.configurationRestService
            .getConfiguredResourceConfigurations(module.getUrl(), module.getName(), flowName, componentName);
    }

    private Component manageBooleanConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        RadioButtonGroup radioButtonGroup = new RadioButtonGroup();
        radioButtonGroup.setItems(true, false);
        radioButtonGroup.setLabel(configurationParameterMetaData.getName());
        radioButtonGroup.setValue(configurationParameterMetaData.getValue());

        layout.add(radioButtonGroup);

        return layout;
    }

    private Component manageIntegerConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        TextField textField = new TextField(configurationParameterMetaData.getName());
        textField.setWidth("100%");

        if(configurationParameterMetaData.getValue() != null)
        {
            textField.setValue(((Integer)configurationParameterMetaData.getValue()).toString());
        }

        layout.add(textField);

        return layout;
    }

    private Component manageStringConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        TextField textField = new TextField(configurationParameterMetaData.getName());
        textField.setWidth("100%");

        if(configurationParameterMetaData.getValue() != null)
        {
            textField.setValue((String) configurationParameterMetaData.getValue());
        }

        layout.add(textField);

        return layout;
    }

    private Component manageMaskedStringConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        PasswordField passwordField = new PasswordField(configurationParameterMetaData.getName());
        passwordField.setWidth("100%");

        if(configurationParameterMetaData.getValue() != null)
        {
            passwordField.setValue((String) configurationParameterMetaData.getValue());
        }

        layout.add(passwordField);

        return layout;
    }

    private Component manageLongConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        TextField textField = new TextField(configurationParameterMetaData.getName());
        textField.setWidth("100%");

        if(configurationParameterMetaData.getValue() != null)
        {
            textField.setValue(((Long)configurationParameterMetaData.getValue()).toString());
        }

        layout.add(textField);

        return layout;
    }

    private Component manageMapConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        verticalLayout.setWidthFull();

        Label mapLabel = new Label(configurationParameterMetaData.getName());
        verticalLayout.add(mapLabel);

        if(configurationParameterMetaData.getValue() != null)
        {
            Map<String, String> configurationMap = (Map<String, String>)configurationParameterMetaData.getValue();

            for(String key: configurationMap.keySet())
            {
                HorizontalLayout layout = new HorizontalLayout();
                layout.setWidthFull();

                TextField nameTextField = new TextField(getTranslation("label.name", UI.getCurrent().getLocale()));
                nameTextField.setWidth("100%");
                nameTextField.setValue(key);
                layout.add(nameTextField);

                TextField valueField = new TextField(getTranslation("label.value", UI.getCurrent().getLocale()));
                valueField.setWidth("100%");
                valueField.setValue(configurationMap.get(key));
                layout.add(valueField);

                verticalLayout.add(layout);
            }
        }

        return verticalLayout;
    }

    private Component manageListConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        verticalLayout.setWidthFull();

        Label listLabel = new Label(configurationParameterMetaData.getName());
        verticalLayout.add(listLabel);

        if(configurationParameterMetaData.getValue() != null)
        {
            List<String> configurationList = (List<String>)configurationParameterMetaData.getValue();

            for(String value: configurationList)
            {
                HorizontalLayout layout = new HorizontalLayout();
                layout.setWidthFull();

                TextField valueField = new TextField();
                valueField.setWidth("100%");
                valueField.setValue(value);
                layout.add(valueField);

                verticalLayout.add(layout);
            }
        }

        return verticalLayout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.downloadButtonTooltip.attachToComponent(downloadButton);
    }
}

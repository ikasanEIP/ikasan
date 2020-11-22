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
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.ikasan.spec.module.client.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.*;

public abstract class AbstractConfigurationDialog extends AbstractCloseableResizableDialog
{
    private static Logger logger = LoggerFactory.getLogger(AbstractConfigurationDialog.class);

    protected ConfigurationService configurationRestService;
    protected ConfigurationMetaData configurationMetaData;
    protected Module module;
    protected String flowName;
    protected String componentName;
    protected Button downloadButton;
    protected Tooltip downloadButtonTooltip;
    protected Tooltip addMapItemButtonTooltip;
    protected List<Tooltip> removeMapItemButtonTooltips;
    protected Tooltip addListItemButtonTooltip;
    protected List<Tooltip> removeListItemButtonTooltips;
    protected Map<ConfigurationParameterMetaData, Object> parameterMetaDataComponentMap;

    protected AbstractConfigurationDialog(Module module, String flowName, String componentName
        , ConfigurationService configurationRestService)
    {
        this.module = module;
        this.flowName = flowName;
        this.componentName = componentName;
        this.configurationRestService = configurationRestService;
        this.parameterMetaDataComponentMap = new HashMap<>();
        this.removeListItemButtonTooltips = new ArrayList<>();
        this.removeMapItemButtonTooltips = new ArrayList<>();
        init();
    }

    private void init()
    {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Image configurationImage = new Image("/frontend/images/configuration-service.png", "");
        configurationImage.setHeight("70px");

        if(!this.loadConfigurationMetaData())
        {
            NotificationHelper.showErrorNotification(getTranslation("error.could-not-open-configuration", UI.getCurrent().getLocale()));
            return;
        }

        H3 configurationLabel = new H3(String.format(getTranslation("label.configuration-management", UI.getCurrent().getLocale())
            , this.configurationMetaData.getConfigurationId()));


        downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
        downloadButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(downloadButton, getTranslation("tooltip.download-configuration", UI.getCurrent().getLocale()));

        ObjectMapper objectMapper = new ObjectMapper();
        StreamResource streamResource = new StreamResource("configuration-"+ this.configurationMetaData.getConfigurationId() + ".txt", () ->
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

        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(streamResource);
        buttonWrapper.wrapComponent(downloadButton);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSpacing(true);
        headerLayout.add(configurationImage, configurationLabel, buttonWrapper, downloadButtonTooltip);
        headerLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, configurationLabel, buttonWrapper);


        layout.add(headerLayout);
        layout.add(new Divider());

        for(ConfigurationParameterMetaData configurationParameterMetaData: ((List<ConfigurationParameterMetaData >)configurationMetaData.getParameters()))
        {
            if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"))
            {
                layout.add(this.manageBooleanConfiguration(configurationParameterMetaData));
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"))
            {
                layout.add(this.manageIntegerConfiguration(configurationParameterMetaData));
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterStringImpl"))
            {
                layout.add(this.manageStringConfiguration(configurationParameterMetaData));
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterLongImpl"))
            {
                layout.add(this.manageLongConfiguration(configurationParameterMetaData));
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterMapImpl"))
            {
                Component mapLayout = this.manageMapConfiguration(configurationParameterMetaData);
                layout.add(mapLayout);
                layout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, mapLayout);
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterListImpl"))
            {
                layout.add(this.manageListConfiguration(configurationParameterMetaData));
            }
            else if(configurationParameterMetaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl"))
            {
                layout.add(this.manageMaskedStringConfiguration(configurationParameterMetaData));
            }
        }

        Button saveButton = new Button(getTranslation("button.save", UI.getCurrent().getLocale()));
        saveButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> save());

        Button deleteButton = new Button(getTranslation("button.delete", UI.getCurrent().getLocale()));
        deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                this.configurationRestService.delete(module.getUrl(), this.configurationMetaData.getConfigurationId());
                NotificationHelper.showUserNotification(getTranslation("message.successfully-deleted-configuration", UI.getCurrent().getLocale()));
                this.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                NotificationHelper.showUserNotification(getTranslation("error.could-not-delete-configuration", UI.getCurrent().getLocale()));
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setMargin(true);
        buttonLayout.setSpacing(true);
        buttonLayout.add(saveButton, deleteButton);

        ComponentSecurityVisibility.applySecurity(saveButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.PLATORM_CONFIGURATON_ADMIN
            , SecurityConstants.PLATORM_CONFIGURATON_WRITE);

        ComponentSecurityVisibility.applySecurity(deleteButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.PLATORM_CONFIGURATON_ADMIN
            , SecurityConstants.PLATORM_CONFIGURATON_WRITE);

        layout.add(buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);

        this.setWidth("700px");
        super.content.add(layout);
    }

    private void save()
    {
        boolean formIsValid = true;
        for(ConfigurationParameterMetaData metaData: this.parameterMetaDataComponentMap.keySet())
        {
            Object component = this.parameterMetaDataComponentMap.get(metaData);

            if(component instanceof TextField)
            {
                if(metaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterStringImpl"))
                {
                    metaData.setValue(((TextField) component).getValue());
                }
                else if(metaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"))
                {
                    if(((TextField) component).getValue() != null && !((TextField) component).getValue().isEmpty())
                    {
                        try
                        {
                            metaData.setValue(Integer.parseInt(((TextField) component).getValue()));
                            ((TextField) component).setInvalid(false);
                        }
                        catch (NumberFormatException e)
                        {
                            ((TextField) component).setErrorMessage(getTranslation("error.configuration-must-be-an-integer", UI.getCurrent().getLocale()));
                            ((TextField) component).setInvalid(true);
                            formIsValid = false;
                        }
                    }

                }
                else if(metaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterLongImpl"))
                {
                    if(((TextField) component).getValue() != null && !((TextField) component).getValue().isEmpty())
                    {
                        try
                        {
                            metaData.setValue(Long.parseLong(((TextField) component).getValue()));
                            ((TextField) component).setInvalid(false);
                        }
                        catch (NumberFormatException e)
                        {
                            ((TextField) component).setErrorMessage(getTranslation("error.configuration-must-be-a-long", UI.getCurrent().getLocale()));
                            ((TextField) component).setInvalid(true);
                            formIsValid = false;
                        }
                    }
                }
            }
            else if(component instanceof RadioButtonGroup)
            {
               metaData.setValue(((RadioButtonGroup)component).getValue());
            }
            else if(component instanceof PasswordField)
            {
                metaData.setValue(((PasswordField) component).getValue());
            }
            else if(component instanceof List)
            {
                if(metaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterMapImpl"))
                {
                    HashMap<String, String> value = new HashMap<>();

                    for(TextFieldNVP textFieldNVP: ((List<TextFieldNVP>)component))
                    {
                        if(textFieldNVP.getNameTextField().getValue() == null || textFieldNVP.getNameTextField().getValue().isEmpty())
                        {
                            textFieldNVP.getNameTextField().setErrorMessage(getTranslation("error.configuration-map-name-cannot-be-empty", UI.getCurrent().getLocale()));
                            textFieldNVP.getNameTextField().setInvalid(true);
                            formIsValid = false;
                        }

                        value.put(textFieldNVP.getNameTextField().getValue(), textFieldNVP.getValueTextField().getValue());
                    }

                    metaData.setValue(value);
                }
                else if(metaData.getImplementingClass().equals("org.ikasan.configurationService.model.ConfigurationParameterListImpl"))
                {
                    List<String> value = new ArrayList<>();

                    for(TextField textField: ((List<TextField>)component))
                    {

                        value.add(textField.getValue());
                    }

                    metaData.setValue(value);
                }
            }
        }

        if(!formIsValid)
        {
            NotificationHelper.showErrorNotification(getTranslation("message.error-on-form", UI.getCurrent().getLocale()));
            return;
        }

        boolean success = this.configurationRestService.storeConfiguration(this.module.getUrl(), this.configurationMetaData);
        if(success)
        {
            this.loadConfigurationMetaData();
            NotificationHelper.showUserNotification(getTranslation("message.configuration-save-successful", UI.getCurrent().getLocale()));
        }
        else
        {
            NotificationHelper.showErrorNotification(getTranslation("message.configuration-save-unsuccessful", UI.getCurrent().getLocale()));
        }
    }

    /**
     * Abstract method responsible for loading the configuration metadata.
     *
     * @return
     */
    protected abstract boolean loadConfigurationMetaData();

    private Component manageBooleanConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        RadioButtonGroup<Boolean> radioButtonGroup = new RadioButtonGroup();
        radioButtonGroup.setItems(true, false);
        radioButtonGroup.setLabel(configurationParameterMetaData.getName());
        radioButtonGroup.setValue((Boolean) configurationParameterMetaData.getValue());

        layout.add(radioButtonGroup);

        this.parameterMetaDataComponentMap.put(configurationParameterMetaData, radioButtonGroup);

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

        this.parameterMetaDataComponentMap.put(configurationParameterMetaData, textField);

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

        this.parameterMetaDataComponentMap.put(configurationParameterMetaData, textField);

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

        this.parameterMetaDataComponentMap.put(configurationParameterMetaData, passwordField);

        return layout;
    }

    private Component manageLongConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        TextField textField = new TextField(configurationParameterMetaData.getName());
        textField.setWidth("100%");

        if(configurationParameterMetaData.getValue() != null && configurationParameterMetaData.getValue() instanceof Long)
        {
            textField.setValue(((Long)configurationParameterMetaData.getValue()).toString());
        }
        else if(configurationParameterMetaData.getValue() != null)
        {
            textField.setValue(((Integer)configurationParameterMetaData.getValue()).toString());
        }

        layout.add(textField);

        this.parameterMetaDataComponentMap.put(configurationParameterMetaData, textField);

        return layout;
    }

    private Component manageMapConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        verticalLayout.getThemeList().remove("padding");


        Button addButton = new Button(VaadinIcon.PLUS.create());
        this.addMapItemButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(addButton, getTranslation("tooltip.add-configuration-map-item", UI.getCurrent().getLocale()));
        addButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setMargin(false);
            layout.setSpacing(true);
            layout.setWidthFull();

            final TextField nameTextField;
            final TextField valueField;

            if(((List)this.parameterMetaDataComponentMap.get(configurationParameterMetaData)).size() == 0)
            {
                nameTextField = new TextField(getTranslation("label.name", UI.getCurrent().getLocale()));
                valueField = new TextField(getTranslation("label.value", UI.getCurrent().getLocale()));
            }
            else
            {
                nameTextField = new TextField();
                valueField = new TextField();
            }

            nameTextField.setWidth("100%");
            layout.add(nameTextField);

            valueField.setWidth("100%");
            layout.add(valueField);

            ((List)this.parameterMetaDataComponentMap.get(configurationParameterMetaData)).add(new TextFieldNVP(nameTextField, valueField));

            Button removeButton = new Button(VaadinIcon.MINUS.create());
            removeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 ->
            {
                layout.remove(nameTextField);
                layout.remove(valueField);
                layout.remove(removeButton);

                ((List)this.parameterMetaDataComponentMap.get(configurationParameterMetaData)).remove(new TextFieldNVP(nameTextField, valueField));
            });

            Tooltip tooltip = TooltipHelper.getTooltipForComponentTopLeft(removeButton, getTranslation("tooltip.remove-configuration-map-item", UI.getCurrent().getLocale()));
            this.removeMapItemButtonTooltips.add(tooltip);

            layout.add(removeButton, tooltip);
            layout.setVerticalComponentAlignment(FlexComponent.Alignment.END, removeButton);

            verticalLayout.add(layout);
        });

        Label mapLabel = new Label(configurationParameterMetaData.getName());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
        topLayout.setSpacing(false);
        topLayout.setMargin(false);
        topLayout.add(mapLabel, addButton, this.addMapItemButtonTooltip);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, mapLabel, addButton);

        verticalLayout.add(topLayout);

        List<TextFieldNVP> textFieldNVPS = new ArrayList<>();

        int count = 0;
        if(configurationParameterMetaData.getValue() != null)
        {
            Map<String, String> configurationMap = (Map<String, String>)configurationParameterMetaData.getValue();

            for(String key: configurationMap.keySet())
            {
                HorizontalLayout layout = new HorizontalLayout();
                layout.setMargin(false);
                layout.setSpacing(true);
                layout.setWidthFull();

                final TextField nameTextField;
                final TextField valueField;

                if(count++ == 0)
                {
                    nameTextField = new TextField(getTranslation("label.name", UI.getCurrent().getLocale()));
                    valueField = new TextField(getTranslation("label.value", UI.getCurrent().getLocale()));
                }
                else
                {
                    nameTextField = new TextField();
                    valueField = new TextField();
                }

                nameTextField.setWidth("100%");
                nameTextField.setValue(key);
                layout.add(nameTextField);

                valueField.setWidth("100%");
                valueField.setValue(configurationMap.get(key));
                layout.add(valueField);

                Button removeButton = new Button(VaadinIcon.MINUS.create());
                removeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 ->
                {
                    layout.remove(nameTextField);
                    layout.remove(valueField);
                    layout.remove(removeButton);

                    ((List)this.parameterMetaDataComponentMap.get(configurationParameterMetaData)).remove(new TextFieldNVP(nameTextField, valueField));
                });

                Tooltip tooltip = TooltipHelper.getTooltipForComponentTopLeft(removeButton, getTranslation("tooltip.remove-configuration-map-item", UI.getCurrent().getLocale()));
                this.removeMapItemButtonTooltips.add(tooltip);

                layout.add(removeButton, tooltip);
                layout.setVerticalComponentAlignment(FlexComponent.Alignment.END, removeButton);

                textFieldNVPS.add(new TextFieldNVP(nameTextField, valueField));

                verticalLayout.add(layout);
            }
        }

        this.parameterMetaDataComponentMap.put(configurationParameterMetaData, textFieldNVPS);

        return verticalLayout;
    }

    private Component manageListConfiguration(ConfigurationParameterMetaData configurationParameterMetaData)
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        verticalLayout.setWidthFull();

        Label listLabel = new Label(configurationParameterMetaData.getName());
        Button addButton = new Button(VaadinIcon.PLUS.create());
        this.addListItemButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(addButton, getTranslation("tooltip.add-configuration-list-item", UI.getCurrent().getLocale()));
        addButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setWidthFull();

            TextField valueField = new TextField();
            valueField.setWidth("100%");
            layout.add(valueField);

            Button removeButton = new Button(VaadinIcon.MINUS.create());
            removeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 ->
            {
                layout.remove(valueField);
                layout.remove(removeButton);
                ((List)this.parameterMetaDataComponentMap.get(configurationParameterMetaData)).remove(valueField);
            });

            Tooltip tooltip = TooltipHelper.getTooltipForComponentTopLeft(removeButton, getTranslation("tooltip.remove-configuration-list-item", UI.getCurrent().getLocale()));
            this.removeMapItemButtonTooltips.add(tooltip);

            layout.add(removeButton, tooltip);

            ((List)this.parameterMetaDataComponentMap.get(configurationParameterMetaData)).add(valueField);

            verticalLayout.add(layout);
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.add(listLabel, addButton, this.addListItemButtonTooltip);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, listLabel, addButton);
        verticalLayout.add(topLayout);

        List<TextField> textFields = new ArrayList<>();

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

                Button removeButton = new Button(VaadinIcon.MINUS.create());
                removeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 ->
                {
                    layout.remove(valueField);
                    layout.remove(removeButton);
                    ((List)this.parameterMetaDataComponentMap.get(configurationParameterMetaData)).remove(valueField);
                });

                Tooltip tooltip = TooltipHelper.getTooltipForComponentTopLeft(removeButton, getTranslation("tooltip.remove-configuration-list-item", UI.getCurrent().getLocale()));
                this.removeMapItemButtonTooltips.add(tooltip);

                layout.add(removeButton, tooltip);

                verticalLayout.add(layout);

                textFields.add(valueField);
            }
        }

        this.parameterMetaDataComponentMap.put(configurationParameterMetaData, textFields);

        return verticalLayout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        if (downloadButton != null)
        {
            this.downloadButtonTooltip.attachToComponent(downloadButton);
        }
    }

    private class TextFieldNVP
    {
        private TextField nameTextField;
        private TextField valueTextField;

        public TextFieldNVP(TextField nameTextField, TextField valueTextField)
        {
            this.nameTextField = nameTextField;
            this.valueTextField = valueTextField;
        }

        public TextField getNameTextField()
        {
            return nameTextField;
        }

        public TextField getValueTextField()
        {
            return valueTextField;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TextFieldNVP that = (TextFieldNVP) o;
            return Objects.equals(nameTextField, that.nameTextField) &&
                Objects.equals(valueTextField, that.valueTextField);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(nameTextField, valueTextField);
        }
    }
}

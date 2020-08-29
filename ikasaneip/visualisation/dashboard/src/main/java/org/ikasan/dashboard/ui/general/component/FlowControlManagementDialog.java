package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.component.ModuleVisualisation;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.module.client.ModuleControlService;

public class FlowControlManagementDialog extends Dialog
{
    private Flow flow;
    private Module module;
    private ModuleControlService moduleControlService;
    private ModuleVisualisation moduleVisualisation;

    public FlowControlManagementDialog(Module module, Flow flow, ModuleControlService moduleRestService, ModuleVisualisation moduleVisualisation)
    {
        this.flow = flow;
        this.module = module;
        this.moduleControlService = moduleRestService;
        this.moduleVisualisation = moduleVisualisation;

        init();
    }

    private void init()
    {
        VerticalLayout verticalLayout = new VerticalLayout();

        Image mrSquidImage = new Image("/frontend/images/mr-squid-head.png", "");
        mrSquidImage.setHeight("35px");

        H3 flowOptions = new H3(String.format(getTranslation("label.startup-control", UI.getCurrent().getLocale())));

        HorizontalLayout header = new HorizontalLayout();
        header.add(mrSquidImage, flowOptions);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, mrSquidImage, flowOptions);

        verticalLayout.add(header);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, header);

        ComboBox<StartupType> startupTypeCombo = new ComboBox<>();
        startupTypeCombo.setItems(StartupType.AUTOMATIC, StartupType.MANUAL, StartupType.DISABLED);
        startupTypeCombo.setWidthFull();

        TextArea textArea = new TextArea(getTranslation("text-field.comment", UI.getCurrent().getLocale()));
        textArea.setWidthFull();
        textArea.getStyle().set("minHeight", "150px");
        textArea.getStyle().set("maxHeight", "150px");
        textArea.setVisible(false);

        startupTypeCombo.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<StartupType>, StartupType>>) comboBoxStringComponentValueChangeEvent -> {
            if(comboBoxStringComponentValueChangeEvent.getValue() == StartupType.DISABLED){
                textArea.setVisible(true);
                textArea.setRequired(true);

            }
            else {
                textArea.setVisible(false);
                textArea.setRequired(false);
                textArea.setValue("");
            }
        });

        startupTypeCombo.setValue(flow.getStartupType());

        Binder<Flow> binder = new Binder<>();
        binder
            .forField(textArea)
            .withValidator(value -> startupTypeCombo.getValue() == StartupType.AUTOMATIC
                || startupTypeCombo.getValue() == StartupType.MANUAL
                || (startupTypeCombo.getValue() == StartupType.DISABLED
                && value.length() > 0), getTranslation("error.comment-mandatory", UI.getCurrent().getLocale()))
            .bind(Flow::getStartupComment, Flow::setStartupComment);
        binder.setBean(flow);

        Button saveButton = new Button(getTranslation("button.save", UI.getCurrent().getLocale()));
        saveButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
           BinderValidationStatus<Flow> binderValidationStatus = binder.validate();

           if(binderValidationStatus.isOk()) {
               this.moduleControlService.changeFlowStartupType(module.getUrl(), module.getName(), flow.getName(),
                   startupTypeCombo.getValue().name().toLowerCase(), startupTypeCombo.getValue() == StartupType.DISABLED ? textArea.getValue() : "");

               this.flow.setStartupType(startupTypeCombo.getValue());
               this.moduleVisualisation.redrawFlowControl();

               this.close();
           }
        });

        ComponentSecurityVisibility.applySecurity(saveButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.MODULE_CONTROL_ADMIN
            , SecurityConstants.MODULE_CONTROL_WRITE);

        verticalLayout.add(startupTypeCombo, textArea, saveButton);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, saveButton);

        this.add(verticalLayout);
    }
}

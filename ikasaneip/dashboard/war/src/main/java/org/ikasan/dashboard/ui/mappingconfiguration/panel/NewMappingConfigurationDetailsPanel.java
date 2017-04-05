package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.service.MappingConfigurationService;
import org.ikasan.security.service.authentication.IkasanAuthentication;

/**
 * Created by stewmi on 04/04/2017.
 */
public class NewMappingConfigurationDetailsPanel extends Panel
{
    private MappingConfiguration mappingConfiguration;

    protected ClientComboBox clientComboBox;
    protected TypeComboBox typeComboBox;
    protected SourceContextComboBox sourceContextComboBox;
    protected TargetContextComboBox targetContextComboBox;
    protected TextArea descriptionTextArea;

    public NewMappingConfigurationDetailsPanel(MappingConfigurationService mappingConfigurationService)
    {
        this.clientComboBox = new ClientComboBox(mappingConfigurationService);
        this.typeComboBox = new TypeComboBox(mappingConfigurationService);
        this.sourceContextComboBox = new SourceContextComboBox(mappingConfigurationService, false);
        this.targetContextComboBox = new TargetContextComboBox(mappingConfigurationService, false);

        init();
    }

    private void init()
    {
        this.clientComboBox.loadClientSelectValues();
        this.typeComboBox.loadClientTypeValues();
        this.sourceContextComboBox.loadContextValues();
        this.targetContextComboBox.loadContextValues();

        GridLayout layout = new GridLayout(5, 6);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setWidth("100%");

        this.clientComboBox.setVisible(true);
        this.clientComboBox.setWidth(300, Unit.PIXELS);
        this.clientComboBox.removeAllValidators();
        this.clientComboBox.addValidator(new NullValidator("A type must be selected!", false));
        this.clientComboBox.setValidationVisible(false);

        this.typeComboBox.setVisible(true);
        this.typeComboBox.setWidth(300, Unit.PIXELS);
        this.typeComboBox.removeAllValidators();
        this.typeComboBox.addValidator(new NullValidator("A type must be selected!", false));
        this.typeComboBox.setValidationVisible(false);

        this.sourceContextComboBox.setVisible(true);
        this.sourceContextComboBox.setWidth(300, Unit.PIXELS);
        this.sourceContextComboBox.removeAllValidators();
        this.sourceContextComboBox.addValidator(new NullValidator("A type must be selected!", false));
        this.sourceContextComboBox.setValidationVisible(false);

        this.targetContextComboBox .setVisible(true);
        this.targetContextComboBox.setWidth(300, Unit.PIXELS);
        this.targetContextComboBox.removeAllValidators();
        this.targetContextComboBox.addValidator(new NullValidator("A type must be selected!", false));
        this.targetContextComboBox.setValidationVisible(false);

        this.addStyleName(ValoTheme.PANEL_BORDERLESS);

        Label mappingConfigurationLabel = new Label("Details");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

        HorizontalLayout clientLabelLayout = new HorizontalLayout();
        clientLabelLayout.setHeight(25, Sizeable.Unit.PIXELS);
        clientLabelLayout.setWidth(100, Sizeable.Unit.PIXELS);

        Label clientLabel = new Label("Client:");
        clientLabel.setSizeUndefined();
        clientLabelLayout.addComponent(clientLabel);
        clientLabelLayout.setComponentAlignment(clientLabel, Alignment.MIDDLE_RIGHT);

        layout.addComponent(clientLabelLayout, 0, 1);
        layout.setComponentAlignment(clientLabelLayout, Alignment.MIDDLE_RIGHT);

        HorizontalLayout clientComboBoxLayout = new HorizontalLayout();
        clientComboBoxLayout.setHeight(25, Sizeable.Unit.PIXELS);
        clientComboBoxLayout.setWidth(350, Sizeable.Unit.PIXELS);
        this.clientComboBox.setWidth(300, Sizeable.Unit.PIXELS);
        this.clientComboBox.removeAllValidators();
        this.clientComboBox.addValidator(new NullValidator("A client must be selected!", false));
        this.clientComboBox.setValidationVisible(false);
        clientComboBoxLayout.addComponent(this.clientComboBox);
        layout.addComponent(clientComboBoxLayout, 1, 1);

        HorizontalLayout typeLabelLayout = new HorizontalLayout();
        typeLabelLayout.setHeight(25, Sizeable.Unit.PIXELS);
        typeLabelLayout.setWidth(100, Sizeable.Unit.PIXELS);

        Label typeLabel = new Label("Type:");
        typeLabel.setSizeUndefined();
        typeLabelLayout.addComponent(typeLabel);
        typeLabelLayout.setComponentAlignment(typeLabel, Alignment.MIDDLE_RIGHT);

        layout.addComponent(typeLabelLayout, 0, 2);
        layout.setComponentAlignment(typeLabelLayout, Alignment.MIDDLE_RIGHT);

        HorizontalLayout typeComboBoxLayout = new HorizontalLayout();
        typeComboBoxLayout.setHeight(25, Sizeable.Unit.PIXELS);
        typeComboBoxLayout.setWidth(350, Sizeable.Unit.PIXELS);
        this.typeComboBox.setWidth(300, Sizeable.Unit.PIXELS);
        this.typeComboBox.removeAllValidators();
        this.typeComboBox.addValidator(new NullValidator("A type must be selected!", false));
        this.typeComboBox.setValidationVisible(false);
        typeComboBoxLayout.addComponent(this.typeComboBox);
        layout.addComponent(typeComboBoxLayout, 1, 2);

        HorizontalLayout sourceContextLabelLayout = new HorizontalLayout();
        sourceContextLabelLayout.setHeight(25, Sizeable.Unit.PIXELS);
        sourceContextLabelLayout.setWidth(100, Sizeable.Unit.PIXELS);

        Label sourceContextLabel = new Label("Source Context:");
        sourceContextLabel.setSizeUndefined();
        sourceContextLabelLayout.addComponent(sourceContextLabel);
        sourceContextLabelLayout.setComponentAlignment(sourceContextLabel, Alignment.MIDDLE_RIGHT);

        layout.addComponent(sourceContextLabelLayout, 0, 3);
        layout.setComponentAlignment(sourceContextLabelLayout, Alignment.MIDDLE_RIGHT);

        HorizontalLayout sourceContextComboBoxLayout = new HorizontalLayout();
        sourceContextComboBoxLayout.setHeight(25, Sizeable.Unit.PIXELS);
        sourceContextComboBoxLayout.setWidth(350, Sizeable.Unit.PIXELS);
        this.sourceContextComboBox.setWidth(300, Sizeable.Unit.PIXELS);
        this.sourceContextComboBox.removeAllValidators();
        this.sourceContextComboBox.addValidator(new NullValidator("A source context must be selected", false));
        this.sourceContextComboBox.setValidationVisible(false);
        sourceContextComboBoxLayout.addComponent(this.sourceContextComboBox);
        layout.addComponent(sourceContextComboBoxLayout, 1, 3);

        HorizontalLayout targetContextLabelLayout = new HorizontalLayout();
        targetContextLabelLayout.setHeight(25, Sizeable.Unit.PIXELS);
        targetContextLabelLayout.setWidth(100, Sizeable.Unit.PIXELS);

        Label targetContextLabel = new Label("Target Context:");
        targetContextLabel.setSizeUndefined();
        targetContextLabelLayout.addComponent(targetContextLabel);
        targetContextLabelLayout.setComponentAlignment(targetContextLabel, Alignment.MIDDLE_RIGHT);

        layout.addComponent(targetContextLabelLayout, 0, 4);
        layout.setComponentAlignment(targetContextLabelLayout, Alignment.MIDDLE_RIGHT);

        HorizontalLayout targetContextComboBoxLayout = new HorizontalLayout();
        targetContextComboBoxLayout.setHeight(25, Sizeable.Unit.PIXELS);
        targetContextComboBoxLayout.setWidth(350, Sizeable.Unit.PIXELS);
        this.targetContextComboBox.setWidth(300, Sizeable.Unit.PIXELS);
        this.targetContextComboBox.removeAllValidators();
        this.targetContextComboBox.addValidator(new NullValidator("A target context must be selected",false));
        this.targetContextComboBox.setValidationVisible(false);
        targetContextComboBoxLayout.addComponent(this.targetContextComboBox);
        layout.addComponent(this.targetContextComboBox, 1, 4);

        HorizontalLayout descriptionLabelLayout = new HorizontalLayout();
        descriptionLabelLayout.setHeight(25, Sizeable.Unit.PIXELS);
        descriptionLabelLayout.setWidth(100, Sizeable.Unit.PIXELS);

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setSizeUndefined();
        descriptionLabelLayout.addComponent(descriptionLabel);
        descriptionLabelLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);

        layout.addComponent(descriptionLabelLayout, 0, 5);
        layout.setComponentAlignment(descriptionLabelLayout, Alignment.TOP_RIGHT);

        HorizontalLayout descriptionTextAreaLayout = new HorizontalLayout();
        descriptionTextAreaLayout.setHeight(75, Sizeable.Unit.PIXELS);
        descriptionTextAreaLayout.setWidth(350, Sizeable.Unit.PIXELS);
        this.descriptionTextArea = new TextArea();
        this.descriptionTextArea.setWidth(300, Sizeable.Unit.PIXELS);
        this.descriptionTextArea.setRows(4);
        this.descriptionTextArea.addValidator(new StringLengthValidator(
                "A description must be entered.",
                1, null, true));
        this.descriptionTextArea.setValidationVisible(false);
        descriptionTextAreaLayout.addComponent(this.descriptionTextArea);
        layout.addComponent(descriptionTextAreaLayout, 1, 5);

        this.setContent(layout);

        this.setSizeFull();
    }

    public boolean isValid()
    {
        try
        {
            this.clientComboBox.validate();
            this.typeComboBox.validate();
            this.sourceContextComboBox.validate();
            this.targetContextComboBox.validate();
            this.descriptionTextArea.validate();
        }
        catch (Validator.InvalidValueException e)
        {
            this.clientComboBox.setValidationVisible(true);
            this.typeComboBox.setValidationVisible(true);
            this.sourceContextComboBox.setValidationVisible(true);
            this.targetContextComboBox.setValidationVisible(true);
            this.descriptionTextArea.setValidationVisible(true);

            return false;
        }

        return true;
    }
}

package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.ParameterName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/04/2017.
 */
public class NewMappingConfigurationManyToManyTargetParamNamesPanel extends Panel
{
    private ArrayList<TextField> targetParameterNamesTextField;
    private GridLayout layout = null;
    private VerticalLayout namesLayout = null;
    private int numTargetParameters = 0;

    private MappingConfiguration mappingConfiguration;
    private List<ParameterName> parameterNames;

    public NewMappingConfigurationManyToManyTargetParamNamesPanel(MappingConfiguration mappingConfiguration)
    {
        this.mappingConfiguration = mappingConfiguration;
        this.parameterNames = new ArrayList<ParameterName>();
        init();
    }

    private void init()
    {
        layout = new GridLayout(5, 6);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setWidth("100%");

        this.namesLayout = new VerticalLayout();
        this.namesLayout.setSpacing(true);

        this.addStyleName(ValoTheme.PANEL_BORDERLESS);

        Label mappingConfigurationLabel = new Label("Target parameter names");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);


        this.layout.addComponent(this.namesLayout, 0, 1);

        this.setContent(layout);

        this.setSizeFull();
    }

    public void enter(int numSourceParameters)
    {
        if(numSourceParameters != this.numTargetParameters)
        {
            this.numTargetParameters = numSourceParameters;

            this.targetParameterNamesTextField = new ArrayList<TextField>();

            namesLayout.removeAllComponents();

            for (int i = 0; i < this.numTargetParameters; i++)
            {
                TextField nameField = new TextField();
                nameField.setCaption("Target Parameter Name " + (i + 1));
                nameField.setWidth(150, Unit.PIXELS);
                nameField.removeAllValidators();
                nameField.addValidator(new StringLengthValidator("You must provide a parameter name!",1 , null, false));
                nameField.setValidationVisible(false);

                this.targetParameterNamesTextField.add(nameField);

                namesLayout.addComponent(nameField);
            }
        }
    }

    public boolean isValid()
    {
        Set<String> setString = new HashSet<String>();

        try
        {
            for(TextField tf: this.targetParameterNamesTextField)
            {
                setString.add(tf.getValue());
                tf.validate();
            }
        }
        catch (Validator.InvalidValueException e)
        {
            for(TextField tf: this.targetParameterNamesTextField)
            {
                tf.setValidationVisible(true);
            }

            return false;
        }

        if(this.targetParameterNamesTextField.size() != setString.size())
        {
            Notification.show("Parameter names must be unique!", Notification.Type.ERROR_MESSAGE);
            return false;
        }

        int i = 1;

        for(TextField tf: this.targetParameterNamesTextField)
        {
            ParameterName parameterName = new ParameterName();
            parameterName.setContext(ParameterName.TARGET_CONTEXT);
            parameterName.setName(tf.getValue());
            parameterName.setOrdinal(i++);
            setString.add(tf.getValue());

            this.parameterNames.add(parameterName);
        }

        return true;
    }

    public List<ParameterName> getParameterNames()
    {
        return parameterNames;
    }
}

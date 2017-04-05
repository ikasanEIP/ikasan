package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.validator.IntegerStringValidator;
import org.ikasan.dashboard.ui.framework.validator.LongStringValidator;


/**
 * Created by stewmi on 04/04/2017.
 */
public class NewMappingConfigurationManyToOneNumParamsPanel extends Panel
{
    private Logger logger = Logger.getLogger(NewMappingConfigurationManyToOneNumParamsPanel.class);

    private TextField numberOfSourceParametersTextField;
    private Label numSourceParamsLabel;

    public NewMappingConfigurationManyToOneNumParamsPanel()
    {
        init();
    }

    private void init()
    {
        GridLayout layout = new GridLayout(5, 6);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setWidth("100%");

        this.addStyleName(ValoTheme.PANEL_BORDERLESS);

        Label mappingConfigurationLabel = new Label("Number of parameters");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);


        numSourceParamsLabel = new Label("Number of source parameters:");
        numSourceParamsLabel.setWidth(175, Unit.PIXELS);
        numSourceParamsLabel.setVisible(true);


        layout.addComponent(numSourceParamsLabel, 2, 2);
        this.numberOfSourceParametersTextField = new TextField();
        this.numberOfSourceParametersTextField.setWidth(75, Unit.PIXELS);
        this.numberOfSourceParametersTextField.removeAllValidators();
        this.numberOfSourceParametersTextField.addValidator(new LongStringValidator("Number of source parameters " +
                "must be defined."));
        this.numberOfSourceParametersTextField.setValidationVisible(false);
        this.numberOfSourceParametersTextField.setVisible(true);
        layout.addComponent(this.numberOfSourceParametersTextField, 3, 2);


        this.setContent(layout);

        this.setSizeFull();
    }

    public boolean isValid()
    {
        try
        {
            this.numberOfSourceParametersTextField.validate();
        }
        catch (Validator.InvalidValueException e)
        {
            this.numberOfSourceParametersTextField.setValidationVisible(true);

            return false;
        }

        return true;
    }

    public int getNumberSourceValues()
    {
        return Integer.parseInt(this.numberOfSourceParametersTextField.getValue());
    }

}

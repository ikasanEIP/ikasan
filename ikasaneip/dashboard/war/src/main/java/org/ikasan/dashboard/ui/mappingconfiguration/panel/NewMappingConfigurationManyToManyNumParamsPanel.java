package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.validator.IntegerStringValidator;
import org.ikasan.dashboard.ui.framework.validator.IntegerValidator;
import org.ikasan.dashboard.ui.framework.validator.LongStringValidator;
import org.ikasan.dashboard.ui.framework.validator.LongValidator;


/**
 * Created by stewmi on 04/04/2017.
 */
public class NewMappingConfigurationManyToManyNumParamsPanel extends Panel
{
    private Logger logger = Logger.getLogger(NewMappingConfigurationManyToManyNumParamsPanel.class);

    public enum ANSWER
    {
        YES,
        NO
    }

    private OptionGroup optionGroup = null;

    private TextField numberOfSourceParametersTextField;
    private TextField numberOfTargetParametersTextField;
    private Label numSourceParamsLabel;
    private Label numTargetParamsLabel;

    public NewMappingConfigurationManyToManyNumParamsPanel()
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

        optionGroup = new OptionGroup( "Specify the number of parameters:" );
        optionGroup.addItems( ANSWER.YES , ANSWER.NO );
        optionGroup.setItemCaption(ANSWER.YES , "Yes" );
        optionGroup.setItemCaption(ANSWER.NO , "No" );
        optionGroup.setValue(ANSWER.NO);  // Specify which radio button is selected by default.

        optionGroup.addValueChangeListener( new Property.ValueChangeListener()
        {
            @Override
            public void valueChange ( Property.ValueChangeEvent event )
            {
                if(optionGroup.getValue().equals(ANSWER.YES))
                {
                    numSourceParamsLabel.setVisible(true);
                    numTargetParamsLabel.setVisible(true);
                    numberOfSourceParametersTextField.setVisible(true);
                    numberOfTargetParametersTextField.setVisible(true);
                }
                else
                {
                    numSourceParamsLabel.setVisible(false);
                    numTargetParamsLabel.setVisible(false);
                    numberOfSourceParametersTextField.setVisible(false);
                    numberOfTargetParametersTextField.setVisible(false);
                }
            }
        } );

        layout.addComponent(optionGroup, 0, 1);
        layout.setComponentAlignment(optionGroup, Alignment.MIDDLE_CENTER);

        numSourceParamsLabel = new Label("Number of source parameters:");
        numSourceParamsLabel.setWidth(175, Unit.PIXELS);
        numSourceParamsLabel.setVisible(false);
        numTargetParamsLabel = new Label("Number of target parameters:");
        numTargetParamsLabel.setWidth(175, Unit.PIXELS);
        numTargetParamsLabel.setVisible(false);

        layout.addComponent(numSourceParamsLabel, 2, 2);
        this.numberOfSourceParametersTextField = new TextField();
        this.numberOfSourceParametersTextField.setWidth(75, Unit.PIXELS);
        this.numberOfSourceParametersTextField.removeAllValidators();
        this.numberOfSourceParametersTextField.addValidator(new LongStringValidator("Number of source parameters " +
                "must be defined."));
        this.numberOfSourceParametersTextField.setValidationVisible(false);
        this.numberOfSourceParametersTextField.setVisible(false);
        layout.addComponent(this.numberOfSourceParametersTextField, 3, 2);

        layout.addComponent(numTargetParamsLabel, 2, 3);
        this.numberOfTargetParametersTextField = new TextField();
        this.numberOfTargetParametersTextField.setWidth(75, Unit.PIXELS);
        this.numberOfTargetParametersTextField.removeAllValidators();
        this.numberOfTargetParametersTextField.addValidator(new IntegerStringValidator("Number of target parameters " +
                "must be defined."));
        this.numberOfTargetParametersTextField.setValidationVisible(false);
        this.numberOfTargetParametersTextField.setVisible(false);
        layout.addComponent(this.numberOfTargetParametersTextField, 3, 3);


        this.setContent(layout);

        this.setSizeFull();
    }

    public boolean isValid()
    {
        if(optionGroup.getValue().equals(ANSWER.NO))
        {
            return true;
        }

        try
        {
            this.numberOfSourceParametersTextField.validate();
            this.numberOfTargetParametersTextField.validate();
        }
        catch (Validator.InvalidValueException e)
        {
            this.numberOfSourceParametersTextField.setValidationVisible(true);
            this.numberOfTargetParametersTextField.setValidationVisible(true);

            return false;
        }

        return true;
    }

    public ANSWER getAnswer()
    {
        return (ANSWER)this.optionGroup.getValue();
    }

    public int getNumberSourceValues()
    {
        return Integer.parseInt(this.numberOfSourceParametersTextField.getValue());
    }

    public int getNumberTargetValues()
    {
        return Integer.parseInt(this.numberOfTargetParametersTextField.getValue());
    }
}

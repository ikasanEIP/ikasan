package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.ui.mappingconfiguration.component.ClientComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.SourceContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TargetContextComboBox;
import org.ikasan.dashboard.ui.mappingconfiguration.component.TypeComboBox;

import java.util.ArrayList;

/**
 * Created by stewmi on 04/04/2017.
 */
public class NewMappingConfigurationManyToManySourceParamNamesPanel extends Panel
{
    private ArrayList<TextField> sourceParameterNamesTextField;
    private GridLayout layout = null;
    private VerticalLayout namesLayout = null;
    private int numSourceParameters = 0;

    public NewMappingConfigurationManyToManySourceParamNamesPanel()
    {
        init();
    }


    private void init()
    {
        this.layout = new GridLayout(5, 6);
        this.layout.setSpacing(true);
        this.layout.setMargin(true);
        this.layout.setWidth("100%");

        this.namesLayout = new VerticalLayout();
        this.namesLayout.setSpacing(true);

        this.addStyleName(ValoTheme.PANEL_BORDERLESS);

        Label mappingConfigurationLabel = new Label("Source parameter names");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        this.layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

        this.layout.addComponent(this.namesLayout, 0, 1);

        this.setContent(layout);

        this.setSizeFull();
    }

    public void enter(int numSourceParameters)
    {
        if(numSourceParameters != this.numSourceParameters)
        {
            this.numSourceParameters = numSourceParameters;

            this.sourceParameterNamesTextField = new ArrayList<TextField>();

            namesLayout.removeAllComponents();

            for (int i = 0; i < this.numSourceParameters; i++)
            {
                TextField nameField = new TextField();
                nameField.setCaption("Name" + i);
                nameField.setWidth(150, Unit.PIXELS);
                nameField.removeAllValidators();
                nameField.addValidator(new StringLengthValidator("You must provide a parameter name!"));
                nameField.setValidationVisible(false);

                this.sourceParameterNamesTextField.add(nameField);

                namesLayout.addComponent(nameField);
            }
        }
    }
}

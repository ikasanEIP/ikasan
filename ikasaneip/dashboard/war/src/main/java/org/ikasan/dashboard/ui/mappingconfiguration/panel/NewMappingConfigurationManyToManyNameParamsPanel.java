package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Ikasan Development Team on 04/04/2017.
 */
public class NewMappingConfigurationManyToManyNameParamsPanel extends Panel
{
    private  OptionGroup nameParamOptionGroup = new OptionGroup( "Specify the name of the parameters:" );

    public enum ANSWER
    {
        YES,
        NO
    }

    public NewMappingConfigurationManyToManyNameParamsPanel()
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

        Label mappingConfigurationLabel = new Label("Names of parameters");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

        nameParamOptionGroup.addItems( ANSWER.YES , ANSWER.NO );
        nameParamOptionGroup.setItemCaption(ANSWER.YES , "Yes" );
        nameParamOptionGroup.setItemCaption(ANSWER.NO , "No" );
        nameParamOptionGroup.setValue(ANSWER.NO);  // Specify which radio button is selected by default.

        layout.addComponent(nameParamOptionGroup, 0, 1);
        layout.setComponentAlignment(nameParamOptionGroup, Alignment.MIDDLE_CENTER);

        this.setContent(layout);

        this.setSizeFull();
    }

    public ANSWER getAnswer()
    {
        return (ANSWER)this.nameParamOptionGroup.getValue();
    }
}

package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Ikasan Development Team on 04/04/2017.
 */
public class NewMappingConfigurationTypePanel extends Panel
{

    public enum TYPE
    {
        MANY_TO_MANY,
        MANY_TO_ONE,
        ONE_TO_ONE
    }

    private OptionGroup optionGroup = null;

    public NewMappingConfigurationTypePanel()
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

        Label mappingConfigurationLabel = new Label("Type");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

        optionGroup = new OptionGroup( "Select type:" );
        optionGroup.addItems( TYPE.MANY_TO_MANY , TYPE.MANY_TO_ONE, TYPE.ONE_TO_ONE );
        optionGroup.setItemCaption(TYPE.MANY_TO_MANY , "Many to many" );
        optionGroup.setItemCaption(TYPE.MANY_TO_ONE , "Many to one" );
        optionGroup.setItemCaption(TYPE.ONE_TO_ONE , "One to one" );
        optionGroup.setValue(TYPE.ONE_TO_ONE);  // Specify which radio button is selected by default.

        optionGroup.addValueChangeListener( new Property.ValueChangeListener()
        {

            @Override
            public void valueChange ( Property.ValueChangeEvent event )
            {
                Notification.show( "Radio Button" ,
                        "You chose: " + event.getProperty().getValue().toString() ,
                        Notification.Type.HUMANIZED_MESSAGE );
            }
        } );

        layout.addComponent(optionGroup, 0, 1);
        layout.setComponentAlignment(optionGroup, Alignment.MIDDLE_CENTER);
        
        this.setContent(layout);

        this.setSizeFull();
    }

    public TYPE getType()
    {
        return (TYPE)this.optionGroup.getValue();
    }
}

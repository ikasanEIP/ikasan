package org.ikasan.dashboard.ui.framework.navigation;

import java.util.ArrayList;

import org.vaadin.teemu.VaadinIcons;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

public class VerticalMenu extends VerticalLayout
{
	
	 private ArrayList<Button> buttons;

    /**
     *
     */
    private static final long serialVersionUID;
    
    static
    {
        serialVersionUID = -1508291768897794634L;
    }
    
    public VerticalMenu()
    {
        initRoot();
        initComponents();
    }
    
    private void initRoot()
    {
        setStyleName("vertical-menu");
    }
    
    private void initComponents()
    {
        initButtons();
    }
    
    private void initButtons(){
        buttons    = new ArrayList<Button>();
        
        buttons.add(new Button("Menu Item 1", VaadinIcons.ABACUS));
        buttons.add(new Button("Menu Item 2", VaadinIcons.ABACUS));
        buttons.add(new Button("Menu Item 3", VaadinIcons.ABACUS));
        
        
        for(Button button:buttons)
        {     
        	button.setStyleName("vertical-menu");
            addComponent(button);
        }
 
    }
}
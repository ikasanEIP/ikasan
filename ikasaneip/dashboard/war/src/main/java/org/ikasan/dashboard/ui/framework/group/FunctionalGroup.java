/*
 * $Id: FunctionalGroup.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/group/FunctionalGroup.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.group;

import com.vaadin.ui.Button;

/**
 * @author CMI2 Development Team
 *
 */
public class FunctionalGroup
{
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;

    /**
     * Constructor
     * 
     * @param editButton
     * @param saveButton
     * @param cancelButton
     */
    public FunctionalGroup(Button editButton, Button saveButton, Button cancelButton)
    {
        super();
        this.editButton = editButton;
        this.saveButton = saveButton;
        this.cancelButton = cancelButton;
    }

    /**
     * Helper method to initialise the button state.
     */
    public void initialiseButtonState()
    {
        this.editButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.cancelButton.setVisible(false);
    }

    /**
     * Method to indicate that a save or cancel button 
     * has been pressed.
     * 
     */
    public void saveOrCancelButtonPressed()
    {
        this.initialiseButtonState();
    }

    /**
     * Method to indicate the edit button is pressed.
     */
    public void editButtonPressed()
    {
        this.editButton.setVisible(false);
        this.saveButton.setVisible(true);
        this.cancelButton.setVisible(true);
    }
}

/*
 * $Id: SaveRequiredMonitor.java 40677 2014-11-07 17:14:59Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/util/SaveRequiredMonitor.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.util;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.action.NavigateToAction;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationPanel;

import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class SaveRequiredMonitor
{
    private Logger logger = Logger.getLogger(MappingConfigurationPanel.class);
    
    private boolean isSaveRequired = false;
    private FunctionalGroup functionalGroup;

    /**
     * Constructor
     * 
     * @param functionalGroup
     */
    public SaveRequiredMonitor(FunctionalGroup functionalGroup)
    {
        super();
        this.functionalGroup = functionalGroup;
    }

    /**
     * Method to pop up dialog if a save is required.
     * @param navigateTo
     */
    public void manageSaveRequired(String navigateTo)
    {
        logger.debug("isSaveRequired = " + isSaveRequired);
        if(this.isSaveRequired)
        {
            final NavigateToAction action = new NavigateToAction(navigateTo, this, functionalGroup);
           
            IkasanMessageDialog dialog = new IkasanMessageDialog("Unsaved data", 
                "The form that you have been working on has unsaved data. " +
                "Are you sure you wish to proceed?", action);
    
            UI.getCurrent().addWindow(dialog);
        }
        else
        {
            UI.getCurrent().getNavigator().navigateTo(navigateTo);
        }
    }

    /**
     * @return the isSaveRequired
     */
    public boolean isSaveRequired()
    {
        return isSaveRequired;
    }

    /**
     * @param isSaveRequired the isSaveRequired to set
     */
    public void setSaveRequired(boolean isSaveRequired)
    {
        logger.debug("setting isSaveRequired = " + isSaveRequired);
        this.isSaveRequired = isSaveRequired;
    }
}

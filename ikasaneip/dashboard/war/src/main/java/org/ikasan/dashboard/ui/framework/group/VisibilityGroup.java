/*
 * $Id: VisibilityGroup.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/group/VisibilityGroup.java $
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

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

/**
 * @author CMI2 Development Team
 *
 */
public class VisibilityGroup
{
    private static Logger logger = Logger.getLogger(VisibilityGroup.class);

    private ArrayList<Component> components = new ArrayList<Component>();
    private ArrayList<Table> refreshableTables = new ArrayList<Table>();

    private boolean isVisible = false;

    /**
     * Method to set if the components are visible.
     * 
     * @param visible
     */
    public void setVisible(boolean visible)
    {
        this.isVisible = visible;
        for(Component component: components)
        {
            component.setVisible(this.isVisible);
            logger.debug("Setting component visibility: " + component.getCaption() + ". Visible = " + this.isVisible);
        }

        for(Table table: refreshableTables)
        {
            table.refreshRowCache();
        }
    }

    /**
     * Register a component with this group.
     * @param component
     */
    public void registerComponent(Component component)
    {
        logger.debug("Registering component: " + component.getCaption() + ". Visible = " + this.isVisible);
        component.setVisible(this.isVisible);
        this.components.add(component);
    }

    /**
     * Register a table with this group.
     * @param table
     */
    public void registerRefreshableTable(Table table)
    {
        this.refreshableTables.add(table);
    }

    /**
     * @return the components
     */
    public ArrayList<Component> getComponents()
    {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(ArrayList<Component> components)
    {
        this.components = components;
    }
}

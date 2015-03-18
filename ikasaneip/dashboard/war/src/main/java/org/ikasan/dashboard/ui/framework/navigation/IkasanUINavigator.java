/*
 * $Id: IkasanUINavigator.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/navigation/IkasanUINavigator.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.navigation;

import java.util.List;

import org.ikasan.dashboard.ui.framework.display.IkasanUIView;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class IkasanUINavigator extends Navigator
{
    private static final long serialVersionUID = -193864770035097124L;

    /**
     * Constructor
     * 
     * @param ui
     * @param container
     * @param views
     */
    public IkasanUINavigator(UI ui, ComponentContainer container, List<IkasanUIView> views)
    {
        super(ui, container);
        init(views);
    }

    /**
     * Constructor
     * 
     * @param ui
     * @param stateManager
     * @param display
     * @param views
     */
    public IkasanUINavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display, List<IkasanUIView> views)
    {
        super(ui, stateManager, display);
        init(views);
    }

    /**
     * Constructor
     * 
     * @param ui
     * @param container
     * @param views
     */
    public IkasanUINavigator(UI ui, SingleComponentContainer container, List<IkasanUIView> views)
    {
        super(ui, container);
        init(views);
    }

    /**
     * Constructor
     * 
     * @param ui
     * @param display
     * @param views
     */
    public IkasanUINavigator(UI ui, ViewDisplay display, List<IkasanUIView> views)
    {
        super(ui, display);
        init(views);
    }

    /**
     * Helper method to initialise this object.
     * 
     * @param views
     */
    protected void init(List<IkasanUIView> views)
    {
        for(IkasanUIView view: views)
        {
            this.addView(view.getPath(), view.getView());
        }
    }
}

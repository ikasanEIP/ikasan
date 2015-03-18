/*
 * $Id: IkasanUIContainerViewDisplay.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/display/IkasanUIContainerViewDisplay.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.display;

import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.ui.ComponentContainer;

/**
 * @author CMI2 Development Team
 *
 */
public class IkasanUIContainerViewDisplay extends ComponentContainerViewDisplay
{
    private static final long serialVersionUID = -230690209255266158L;

    /**
     * Constructor
     * 
     * @param componentContainer
     */
    public IkasanUIContainerViewDisplay(ComponentContainer componentContainer)
    {
        super(componentContainer);
    }
}

/*
 * $Id: ViewComponentContainer.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/display/ViewComponentContainer.java $
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

import com.vaadin.ui.HorizontalLayout;

/**
 * @author CMI2 Development Team
 *
 */
public class ViewComponentContainer extends HorizontalLayout
{
    private static final long serialVersionUID = 3354917230638979229L;

    /**
     * Constructor
     */
    public ViewComponentContainer(boolean setMargin)
    {
        this.setSizeFull();
        this.setMargin(setMargin);
    }
}

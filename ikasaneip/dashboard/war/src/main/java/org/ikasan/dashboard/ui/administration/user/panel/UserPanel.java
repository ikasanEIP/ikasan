/*
 * $Id: EstateViewPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/EstateViewPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.administration.user.panel;

import org.apache.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * @author CMI2 Development Team
 *
 */
public class UserPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(UserPanel.class);

    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public UserPanel()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setWidth("100%");
        this.setHeight("100%");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setHeight("100%");
        horizontalLayout.setMargin(true);


        horizontalLayout.addComponent(new Label("I am the USER panel!"));
        this.setContent(horizontalLayout);
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        // TODO Auto-generated method stub
    }
}

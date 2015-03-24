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
package org.ikasan.dashboard.ui.dashboard.panel;

import org.apache.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

/**
 * @author CMI2 Development Team
 *
 */
public class DashboardPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(DashboardPanel.class);

    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public DashboardPanel()
    {
        super();
        init();
    }

    protected void init()
    {
        this.setWidth("100%");
        this.setHeight("100%");

        GridLayout gridLayout = new GridLayout(3,2);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("100%");
        gridLayout.setMargin(true);


        Panel p1 = new Panel("Dashboard Item 1");
        p1.setWidth("90%");
        p1.setHeight("90%");
        
        gridLayout.addComponent(p1, 0, 0);
        
        Panel p2 = new Panel("Dashboard Item 2");
        p2.setWidth("90%");
        p2.setHeight("90%");
        
        gridLayout.addComponent(p2, 1, 0);
        
        Panel p3 = new Panel("Dashboard Item 3");
        p3.setWidth("90%");
        p3.setHeight("90%");
        
        gridLayout.addComponent(p3, 2, 0);
        
        Panel p4 = new Panel("Dashboard Item 4");
        p4.setWidth("90%");
        p4.setHeight("90%");
        
        gridLayout.addComponent(p4, 0, 1);
        
        Panel p5 = new Panel("Dashboard Item 5");
        p5.setWidth("90%");
        p5.setHeight("90%");
        
        gridLayout.addComponent(p5, 1, 1);
        
        Panel p6 = new Panel("Dashboard Item 6");
        p6.setWidth("90%");
        p6.setHeight("90%");
        
        gridLayout.addComponent(p6, 2, 1);

        
        this.setContent(gridLayout);
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

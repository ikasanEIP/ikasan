/*
 * $Id$  
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.ui.monitor.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.cache.GraphCache;
import org.ikasan.dashboard.ui.framework.panel.LandingViewPanel;
import org.ikasan.dashboard.ui.monitor.component.MonitorPanel;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class MonitorViewPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(LandingViewPanel.class);

    private CssLayout dashboardPanels;
    private GraphCache graphCache;
    private TopologyService topologyService;
    private List<View> views;
    
    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public MonitorViewPanel(TopologyService topologyService)
    {
        super();

        this.topologyService = topologyService;
        if(topologyService == null)
		{
			throw new IllegalArgumentException("topologyService cannot be null!");
		}

        
        init();
    }

    protected void init()
    {       
    	addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
    	VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setHeight("100%");
        verticalLayout.setMargin(true);
        verticalLayout.addStyleName("dashboard-view");
        
        Responsive.makeResponsive(verticalLayout);
        
        Component content = buildContent();
        verticalLayout.addComponent(content);
               
        verticalLayout.setExpandRatio(content, 1);
        
        this.setSizeFull();
        this.setContent(verticalLayout);
    }
    
    
    private Component buildContent() 
    {
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        List<Server> servers = new ArrayList<Server>();
        
		try
		{
			servers = topologyService.getAllServers();
		}
		catch(Exception e)
		{
			logger.warn("An exception has occurred trying to update the topology state cache", e);
			// Ignoring this exception, as it may be the case that the database is not yet setup.
		}
        
        this.views = new ArrayList<View>();
        
        for(Server server: servers)
        {
        	MonitorPanel panel = new MonitorPanel(this.topologyService, server);
        	dashboardPanels.addComponent(panel.buildServerComponent());
        	
        	this.views.add(panel);
        }

        return dashboardPanels;
    }
    

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
    	this.buildContent();
    	
		for(View view: views)
		{
			view.enter(event);
		}
    }
}


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
package org.ikasan.dashboard.ui.administration.window;

import java.util.List;

import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationSearchPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationSearchResultsPanel;
import org.ikasan.mapping.model.MappingConfigurationLite;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PolicyAssociationModuleSearchWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7298145261413392839L;

	private TopologyService topologyService;
	private HorizontalSplitPanel horizontalSplitPanel;
	private Module module;
	private Panel searchPanel;
	private Panel resultsPanel;
	private ComboBox serverCombo;
	private Table resultsTable;

	/**
	 * @param mappingConfigurationSearchPanel
	 * @param mappingConfigurationSearchResultsPanel
	 */
	public PolicyAssociationModuleSearchWindow(TopologyService topologyService)
	{
		super();
		
		this.topologyService = topologyService;
		if(this.topologyService == null)
		{
			throw new IllegalArgumentException("topologyService cannot be null!");
		}
		
		init();
	}
	
	/**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init()
    {
    	this.setSizeFull();
    	this.setModal(true);
    	
    	this.serverCombo = new ComboBox();
    	
    	this.createSearchPanel();
    	this.createResultsPanel();
    	
    	VerticalLayout leftPanelLayout = new VerticalLayout();
    	leftPanelLayout.setMargin(true);
    	leftPanelLayout.setWidth(320, Unit.PIXELS);
    	leftPanelLayout.setHeight("100%");
    	leftPanelLayout.addComponent(this.searchPanel);
    	
    	HorizontalLayout rightPanelLayout = new HorizontalLayout();
    	rightPanelLayout.setSizeFull();
    	rightPanelLayout.setMargin(true);
    	rightPanelLayout.addComponent(this.resultsPanel);
    	
    	this.horizontalSplitPanel 
        	= new HorizontalSplitPanel(leftPanelLayout, rightPanelLayout);
	    this.horizontalSplitPanel.setSizeFull();
	    this.horizontalSplitPanel.setSplitPosition(320, Unit.PIXELS);
	    this.horizontalSplitPanel.setLocked(true);
	    this.horizontalSplitPanel.addStyleName("ikasansplitpanel");
	    this.setContent(horizontalSplitPanel);
    }
    
    private void createSearchPanel()
    {
    	this.searchPanel = new Panel();
    	this.searchPanel.setSizeFull();
    	this.searchPanel.setStyleName("dashboard");
    	
    	GridLayout layout = new GridLayout(2, 2);
    	layout.setWidth("100%");
    	layout.setHeight("120px");
    	layout.setMargin(true);
    	
    	Label serverLabel = new Label("Server");    	
    	layout.addComponent(serverLabel, 0, 0);
    	layout.addComponent(this.serverCombo, 1, 0);
  
    	
    	Button searchButton = new Button("Search");    	
    	searchButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	Server server = (Server)serverCombo.getValue();
            	
            	Long moduleId = null;
            	Long serverId = null;
            	
                if(module != null)
                {
                	moduleId = module.getId();
                }
            	
            	if(server != null)
            	{
            		serverId = server.getId();
            	}
            	
            	List<Module> modules = topologyService.getAllModules();
            	resultsTable.removeAllItems();
            	
            	for(Module module: modules)
            	{
            		resultsTable.addItem(new Object[]{module.getServer().getName(), module.getName(), module.getDescription()}, module);
            	}
            }
        });
    	
    	layout.addComponent(searchButton, 0, 1, 1, 1);
    	layout.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);
    	
    	this.searchPanel.setContent(layout);
    }
    
    private void createResultsPanel()
    {
    	this.resultsPanel = new Panel();
    	this.resultsPanel.setSizeFull();
    	this.resultsPanel.setStyleName("dashboard");
    	
    	this.resultsTable = new Table();
    	this.resultsTable.setSizeFull();
    	this.resultsTable.addContainerProperty("Server", String.class,  null);
    	this.resultsTable.addContainerProperty("Module", String.class,  null);
    	this.resultsTable.addContainerProperty("Flow", String.class,  null);
    	
    	this.resultsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
    	{
    	    @Override
    	    public void itemClick(ItemClickEvent itemClickEvent) 
    	    {
    	      module = (Module)itemClickEvent.getItemId();
    	      UI.getCurrent().removeWindow(PolicyAssociationModuleSearchWindow.this);
    	    }
    	});
    	
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.addComponent(this.resultsTable);
    	layout.setSizeFull();
    	layout.setMargin(true);
    	
    	this.resultsPanel.setContent(layout);
    }
    
    public void clear()
    {
    	this.serverCombo.removeAllItems();
    	
    	List<Server> servers = this.topologyService.getAllServers();
    	
    	for(Server server: servers)
    	{
    		this.serverCombo.addItem(server);
    		this.serverCombo.setItemCaption(server, server.getName());
    	}
    }

	/**
	 * @return the flow
	 */
	public Module getModule()
	{
		return module;
	}

}

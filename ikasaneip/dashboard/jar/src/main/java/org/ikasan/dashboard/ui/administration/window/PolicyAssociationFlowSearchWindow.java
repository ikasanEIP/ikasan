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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button.ClickEvent;
import org.tepi.filtertable.FilterTable;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PolicyAssociationFlowSearchWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7298145261413392839L;

	private TopologyService topologyService;
	private HorizontalSplitPanel horizontalSplitPanel;
	private Flow flow;
	private Panel searchPanel;
	private Panel resultsPanel;
	private ComboBox serverCombo;
	private ComboBox moduleCombo;
	private FilterTable resultsTable;
	private IndexedContainer container;

	/**
	 * Constructor
	 *
	 * @param topologyService
     */
	public PolicyAssociationFlowSearchWindow(TopologyService topologyService)
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
     */
    protected void init()
    {
    	this.setSizeFull();
    	this.setModal(true);
    	
    	this.serverCombo = new ComboBox();
    	this.moduleCombo = new ComboBox();
    	
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
    	
    	GridLayout layout = new GridLayout(2, 3);
    	layout.setWidth("100%");
    	layout.setHeight("180px");
    	layout.setMargin(true);
    	
    	Label serverLabel = new Label("Server");    	
    	layout.addComponent(serverLabel, 0, 0);
    	layout.addComponent(this.serverCombo, 1, 0);
    	
    	Label moduleLabel = new Label("Module");
    	
    	layout.addComponent(moduleLabel, 0, 1);
    	layout.addComponent(this.moduleCombo, 1, 1);
    	
    	Button searchButton = new Button("Search");    	
    	searchButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	Module module = (Module)moduleCombo.getValue();
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
            	
            	List<Flow> flows = topologyService.getFlowsByServerIdAndModuleId(serverId, moduleId);
            	resultsTable.removeAllItems();
            	
            	for(Flow flow: flows)
            	{
            		if(flow != null && flow.getModule() != null && flow.getModule().getServer() != null)
					{
						Item item = container.addItem(flow);

						item.getItemProperty("Server").setValue(flow.getModule().getServer().getName());
						item.getItemProperty("Module").setValue(flow.getModule().getName());
						item.getItemProperty("Flow").setValue(flow.getName());
					}
				}
            }
        });
    	
    	layout.addComponent(searchButton, 0, 2, 1, 2);
    	layout.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);
    	
    	this.searchPanel.setContent(layout);
    }
    
    private void createResultsPanel()
    {
    	this.resultsPanel = new Panel();
    	this.resultsPanel.setSizeFull();
    	this.resultsPanel.setStyleName("dashboard");
    	
    	this.resultsTable = new FilterTable();
		this.resultsTable.setFilterBarVisible(true);
    	this.resultsTable.setSizeFull();
		this.container = this.buildContainer();
		this.resultsTable.setContainerDataSource(this.container);
    	
    	this.resultsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
    	{
    	    @Override
    	    public void itemClick(ItemClickEvent itemClickEvent) 
    	    {
    	      flow = (Flow)itemClickEvent.getItemId();
    	      UI.getCurrent().removeWindow(PolicyAssociationFlowSearchWindow.this);
    	    }
    	});
    	
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.addComponent(this.resultsTable);
    	layout.setSizeFull();
    	layout.setMargin(true);
    	
    	this.resultsPanel.setContent(layout);
    }

	protected IndexedContainer buildContainer()
	{

		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Server", String.class,  null);
		cont.addContainerProperty("Module", String.class,  null);
		cont.addContainerProperty("Flow", String.class,  null);

		return cont;
	}
    
    public void clear()
    {
    	this.serverCombo.removeAllItems();
    	this.moduleCombo.removeAllItems();
    	this.resultsTable.removeAllItems();
    	this.flow = null;
    	
    	List<Server> servers = this.topologyService.getAllServers();
    	
    	for(Server server: servers)
    	{
    		this.serverCombo.addItem(server);
    		this.serverCombo.setItemCaption(server, server.getName());
    	}
    	
    	List<Module> modules = this.topologyService.getAllModules();
    	
    	for(Module module: modules)
    	{
    		this.moduleCombo.addItem(module);
    		this.moduleCombo.setItemCaption(module, module.getName());
    	}
    }

	/**
	 * @return the flow
	 */
	public Flow getFlow()
	{
		return flow;
	}

}

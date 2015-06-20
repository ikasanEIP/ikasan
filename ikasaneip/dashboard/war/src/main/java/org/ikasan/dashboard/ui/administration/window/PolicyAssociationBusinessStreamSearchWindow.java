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

import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PolicyAssociationBusinessStreamSearchWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7298145261413392839L;

	private TopologyService topologyService;
	private HorizontalSplitPanel horizontalSplitPanel;
	private Panel searchPanel;
	private Panel resultsPanel;
	private Table resultsTable;
	private BusinessStream businessStream;

	/**
	 * @param mappingConfigurationSearchPanel
	 * @param mappingConfigurationSearchResultsPanel
	 */
	public PolicyAssociationBusinessStreamSearchWindow(TopologyService topologyService)
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
    	
    	this.createSearchPanel();
    	this.createResultsPanel();
    	
    	VerticalLayout leftPanelLayout = new VerticalLayout();
    	leftPanelLayout.setWidth(320, Unit.PIXELS);
    	leftPanelLayout.setHeight("100%");
    	leftPanelLayout.addComponent(this.searchPanel);
    	
    	HorizontalLayout rightPanelLayout = new HorizontalLayout();
    	rightPanelLayout.setSizeFull();
    	rightPanelLayout.addComponent(this.resultsPanel);
    	
    	this.horizontalSplitPanel 
        	= new HorizontalSplitPanel(leftPanelLayout, rightPanelLayout);
	    this.horizontalSplitPanel.setSizeFull();
	    this.horizontalSplitPanel.setSplitPosition(320, Unit.PIXELS);
	    this.horizontalSplitPanel.setLocked(true);
	    this.horizontalSplitPanel.addStyleName("ikasansplitpanel");
	    this.setContent(horizontalSplitPanel);
    }
    
    public void clear()
    {
    	this.businessStream = null;
    	this.resultsTable.removeAllItems();
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
    	
    	Button searchButton = new Button("Search");    	
    	searchButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {           	
            	List<BusinessStream> businessStreams = topologyService.getAllBusinessStreams();
            	resultsTable.removeAllItems();
            	
            	for(BusinessStream businessStream: businessStreams)
            	{
            		resultsTable.addItem(new Object[]{businessStream.getName(), businessStream.getDescription()}, businessStream);
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
    	
    	this.resultsTable = new Table();
    	this.resultsTable.setSizeFull();
    	this.resultsTable.addContainerProperty("Name", String.class,  null);
    	this.resultsTable.addContainerProperty("Description", String.class,  null);

    	this.resultsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
    	{
    	    @Override
    	    public void itemClick(ItemClickEvent itemClickEvent) 
    	    {
    	      businessStream = (BusinessStream)itemClickEvent.getItemId();
    	      UI.getCurrent().removeWindow(PolicyAssociationBusinessStreamSearchWindow.this);
    	    }
    	});
    	
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.addComponent(this.resultsTable);
    	layout.setSizeFull();
    	layout.setMargin(true);
    	
    	this.resultsPanel.setContent(layout);
    }

	/**
	 * @return the businessStream
	 */
	public BusinessStream getBusinessStream()
	{
		return businessStream;
	}
}

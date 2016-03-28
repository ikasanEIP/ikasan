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
package org.ikasan.dashboard.ui.replay.component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.ReplayEventViewPopup;
import org.ikasan.dashboard.ui.ReplayPopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.replay.window.ReplayEventViewWindow;
import org.ikasan.dashboard.ui.topology.component.TopologyTab;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.ReplayManagementService;
import org.ikasan.spec.replay.ReplayService;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(ReplayTab.class);
	
	private FilterTable replayEventsTable;
	
	private ReplayManagementService<ReplayEvent, ReplayAudit>  replayManagementService;
	
	private ReplayService<ReplayEvent, ReplayAuditEvent>  replayService;
	

	private PopupDateField fromDate;
	private PopupDateField toDate;
	
	private TextField eventId;	
	
	private float splitPosition;
	private Unit splitUnit;
	
	private IndexedContainer tableContainer;
	
	private Label resultsLabel = new Label();
	
	private HorizontalLayout searchResultsSizeLayout = new HorizontalLayout();
	
	private PlatformConfigurationService platformConfigurationService;
	
	public ReplayTab(ReplayManagementService<ReplayEvent, ReplayAudit> replayManagementService, ReplayService<ReplayEvent, ReplayAuditEvent> replayService,
			PlatformConfigurationService platformConfigurationService)
	{
		this.replayManagementService = replayManagementService;
		this.replayService = replayService;
		this.platformConfigurationService = platformConfigurationService;
		
		tableContainer = this.buildContainer();
	}
	
	protected IndexedContainer buildContainer() 
	{			
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Event Id / Payload Id", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("", CheckBox.class,  null);
		cont.addContainerProperty(" ", Button.class,  null);
		
        return cont;
    }
	
	public void createLayout()
	{	
		this.replayEventsTable = new FilterTable();
		this.replayEventsTable.setFilterBarVisible(true);
		this.replayEventsTable.setSizeFull();
		this.replayEventsTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.replayEventsTable.addStyleName("ikasan");
		
		this.replayEventsTable.setColumnExpandRatio("Module Name", .14f);
		this.replayEventsTable.setColumnExpandRatio("Flow Name", .18f);
		this.replayEventsTable.setColumnExpandRatio("Component Name", .2f);
		this.replayEventsTable.setColumnExpandRatio("Event Id / Payload Id", .33f);
		this.replayEventsTable.setColumnExpandRatio("Timestamp", .1f);
		this.replayEventsTable.setColumnExpandRatio("", .05f);
		
		this.replayEventsTable.addStyleName("wordwrap-table");
		this.replayEventsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		
		this.replayEventsTable.setContainerDataSource(tableContainer);
		
		this.replayEventsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
			    	ReplayEvent replayEvent = (ReplayEvent)itemClickEvent.getItemId();
			    	ReplayEventViewWindow replayEventViewWindow = new ReplayEventViewWindow(replayEvent
			    			, replayService, platformConfigurationService);
			    
			    	UI.getCurrent().addWindow(replayEventViewWindow);
		    	}
		    }
		});
		

	   				
		final Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {           	
            	replayEventsTable.removeAllItems();

            	List<String> moduleNames = null;
            	
            	if(modules.getItemIds().size() > 0)
            	{
	            	moduleNames = new ArrayList<String>();
	            	for(Object module: modules.getItemIds())
	            	{
	            		moduleNames.add(((Module)module).getName());
	            	}
            	}
            	
            	List<String> flowNames = null;
            	
            	if(flows.getItemIds().size() > 0)
            	{
            		flowNames = new ArrayList<String>();
            		for(Object flow: flows.getItemIds())
                	{
                		flowNames.add(((Flow)flow).getName());
                	}
            	}

            	List<ReplayEvent> replayEvents = replayManagementService
            			.getReplayEvents(moduleNames, flowNames, eventId.getValue(),
            					fromDate.getValue(), toDate.getValue());
            	
            	if(replayEvents == null || replayEvents.size() == 0)
            	{
            		Notification.show("The replay event search returned no results!", Type.ERROR_MESSAGE);
            		
            		searchResultsSizeLayout.removeAllComponents();
                	resultsLabel = new Label("Number of records returned: 0 of 0");
                	searchResultsSizeLayout.addComponent(resultsLabel);
                	
            		return;
            	}
            	
            	searchResultsSizeLayout.removeAllComponents();
            	resultsLabel = new Label("Number of records returned: " + replayEvents.size() + " of " + replayEvents.size());
            	
            	if(replayEvents.size() > platformConfigurationService.getSearchResultSetSize())
            	{
            		Notification notif = new Notification(
            			    "Warning",
            			    "The number of results returned by this search exceeds the configured search " +
            			    "result size of " + platformConfigurationService.getSearchResultSetSize() + " records. " +
            			    "You can narrow the search with a filter or by being more accurate with the date and time range. ",
            			    Type.HUMANIZED_MESSAGE);
            		notif.setDelayMsec(-1);
            		notif.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
            		notif.setPosition(Position.MIDDLE_CENTER);
            		
            		notif.show(Page.getCurrent());
            	}

            	searchResultsSizeLayout.addComponent(resultsLabel);
            	
            	for(final ReplayEvent replayEvent: replayEvents)
            	{
            		Date date = new Date(replayEvent.getTimestamp());
            		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
            	    String timestamp = format.format(date);
            	    
            	    Item item = tableContainer.addItem(replayEvent);			            	    
            	    
            	    item.getItemProperty("Module Name").setValue(replayEvent.getModuleName());
        			item.getItemProperty("Flow Name").setValue(replayEvent.getFlowName());
        			item.getItemProperty("Event Id / Payload Id").setValue(replayEvent.getEventId());
        			item.getItemProperty("Timestamp").setValue(timestamp);
        			
        			CheckBox cb = new CheckBox();
        			cb.setImmediate(true);
        			cb.setDescription("Select in order to add to bulk download.");
        			
        			item.getItemProperty("").setValue(cb);
        			
        			Button popupButton = new Button();
        			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        			popupButton.setDescription("Open in new window");
        			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        			popupButton.setIcon(VaadinIcons.MODAL);
        			
        			BrowserWindowOpener popupOpener = new BrowserWindowOpener(ReplayEventViewPopup.class);
        			popupOpener.setFeatures("height=600,width=900,resizable");
        	        popupOpener.extend(popupButton);
        	        
        	        popupButton.addClickListener(new Button.ClickListener() 
        	    	{
        	            public void buttonClick(ClickEvent event) 
        	            {
        	            	 VaadinService.getCurrentRequest().getWrappedSession().setAttribute("replayEvent", (ReplayEvent)replayEvent);
        	            }
        	        });
        	        
        	        item.getItemProperty(" ").setValue(popupButton);
            	}
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.setStyleName(ValoTheme.BUTTON_SMALL);
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	modules.removeAllItems();
            	flows.removeAllItems();
            	components.removeAllItems();
            }
        });

		GridLayout layout = new GridLayout(1, 6);
		layout.setMargin(false);
		layout.setHeight(270 , Unit.PIXELS);
		
		super.initialiseFilterTables();
		
		GridLayout listSelectLayout = new GridLayout(2, 1);
		listSelectLayout.setSpacing(true);
		listSelectLayout.setSizeFull();
		listSelectLayout.addComponent(super.modules, 0, 0);
		listSelectLayout.addComponent(super.flows, 1, 0);		
		
		GridLayout dateSelectLayout = new GridLayout(2, 2);
		dateSelectLayout.setColumnExpandRatio(0, 0.25f);
		dateSelectLayout.setColumnExpandRatio(1, 0.75f);
		dateSelectLayout.setSizeFull();
		this.fromDate = new PopupDateField("From date");
		this.fromDate.setResolution(Resolution.MINUTE);
		this.fromDate.setValue(this.getMidnightToday());
		this.fromDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(this.fromDate, 0, 0);
		this.toDate = new PopupDateField("To date");
		this.toDate.setResolution(Resolution.MINUTE);
		this.toDate.setValue(this.getTwentyThreeFixtyNineToday());
		this.toDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(this.toDate, 0, 1);
		
		this.eventId = new TextField("Event Id");
		this.eventId.setWidth("80%");
		
		this.eventId.setNullSettingAllowed(true);
		
		dateSelectLayout.addComponent(this.eventId, 1, 0);				
		
		final VerticalSplitPanel vSplitPanel = new VerticalSplitPanel();
		vSplitPanel.setHeight("95%");
		
		GridLayout searchLayout = new GridLayout(2, 1);
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchButton, 0, 0);
		searchLayout.addComponent(clearButton, 1, 0);
		
		final Button hideFilterButton = new Button();
		hideFilterButton.setIcon(VaadinIcons.MINUS);
		hideFilterButton.setCaption("Hide Filter");
		hideFilterButton.setStyleName(ValoTheme.BUTTON_LINK);
		hideFilterButton.addStyleName(ValoTheme.BUTTON_SMALL);
		
		final Button showFilterButton = new Button();
		showFilterButton.setIcon(VaadinIcons.PLUS);
		showFilterButton.setCaption("Show Filter");
		showFilterButton.addStyleName(ValoTheme.BUTTON_LINK);
		showFilterButton.addStyleName(ValoTheme.BUTTON_SMALL);
		showFilterButton.setVisible(false);

		final HorizontalLayout hListSelectLayout = new HorizontalLayout();
		hListSelectLayout.setHeight(150 , Unit.PIXELS);
		hListSelectLayout.setWidth("100%");
		hListSelectLayout.addComponent(listSelectLayout);
		
		final HorizontalLayout hDateSelectLayout = new HorizontalLayout();
		hDateSelectLayout.setHeight(80, Unit.PIXELS);
		hDateSelectLayout.setWidth("100%");
		hDateSelectLayout.addComponent(dateSelectLayout);
		
		final HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(30 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(searchLayout);
		hSearchLayout.setComponentAlignment(searchLayout, Alignment.MIDDLE_CENTER);
		
		hideFilterButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	hideFilterButton.setVisible(false);
            	showFilterButton.setVisible(true);
            	splitPosition = vSplitPanel.getSplitPosition();
            	splitUnit = vSplitPanel.getSplitPositionUnit();
            	vSplitPanel.setSplitPosition(0, Unit.PIXELS);
            }
        });

		
		showFilterButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	hideFilterButton.setVisible(true);
            	showFilterButton.setVisible(false);
            	vSplitPanel.setSplitPosition(splitPosition, splitUnit);
            }
        });
		
		GridLayout filterButtonLayout = new GridLayout(2, 1);
		filterButtonLayout.setHeight(25, Unit.PIXELS);
		filterButtonLayout.addComponent(hideFilterButton, 0, 0);
		filterButtonLayout.addComponent(showFilterButton, 1, 0);
		
		Label filterHintLabel = new Label();
		filterHintLabel.setCaptionAsHtml(true);
		filterHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drag items from the topology tree to the tables below in order to narrow your search.");
		filterHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		filterHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		
		layout.addComponent(filterHintLabel);
		layout.addComponent(hListSelectLayout);
		layout.addComponent(hDateSelectLayout);
		layout.addComponent(hSearchLayout);
		layout.setSizeFull();
		
		Panel filterPanel = new Panel();
		filterPanel.setHeight(340, Unit.PIXELS);
		filterPanel.setWidth("100%");
		filterPanel.setContent(layout);
		filterPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
		vSplitPanel.setFirstComponent(filterPanel);
		
		GridLayout hErrorTable = new GridLayout();
		hErrorTable.setWidth("100%");
		
		GridLayout buttons = new GridLayout(3, 1);
		buttons.setWidth("80px");
		
		final Button selectAllButton = new Button();
		selectAllButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		selectAllButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
		selectAllButton.setImmediate(true);
		selectAllButton.setDescription("Select / deselect all records below.");
		
		selectAllButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	Collection<ReplayEvent> items = (Collection<ReplayEvent>)tableContainer.getItemIds();
            	
            	Resource r = selectAllButton.getIcon();
            	
            	if(r.equals(VaadinIcons.CHECK_SQUARE_O))
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE);
            		
            		for(ReplayEvent eo: items)
                	{
                		Item item = tableContainer.getItem(eo);
                		
                		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
                		
                		cb.setValue(true);
                	}
            	}
            	else
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
            		
            		for(ReplayEvent eo: items)
                	{
                		Item item = tableContainer.getItem(eo);
                		
                		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
                		
                		cb.setValue(false);
                	}
            	}
            }
        });
		
		final Button replayButton = new Button();
		replayButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		replayButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		replayButton.setIcon(VaadinIcons.RECYCLE);
		replayButton.setImmediate(true);
		replayButton.setDescription("Replay selected events.");
		
		BrowserWindowOpener popupOpener = new BrowserWindowOpener(ReplayPopup.class);
		popupOpener.setFeatures("height=600,width=900,resizable");
        popupOpener.extend(replayButton);
        
        replayButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	 // todo add replay events            	 
            	 VaadinService.getCurrentRequest().getWrappedSession().setAttribute("replayEvents", getReplayEvents());
         		 VaadinService.getCurrentRequest().getWrappedSession().setAttribute("replayService", replayService);
         		 VaadinService.getCurrentRequest().getWrappedSession().setAttribute("platformConfigurationService", platformConfigurationService);
            }
        });
 
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.addComponent(buttons);
		hl.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
		
		buttons.addComponent(selectAllButton);
		buttons.addComponent(replayButton);
		
		GridLayout gl = new GridLayout(2, 1);
		gl.setWidth("100%");
		
		searchResultsSizeLayout.setWidth("100%");
		searchResultsSizeLayout.addComponent(this.resultsLabel);
		searchResultsSizeLayout.setComponentAlignment(this.resultsLabel, Alignment.MIDDLE_LEFT);
		
		gl.addComponent(searchResultsSizeLayout);
		gl.addComponent(hl);
		
		hErrorTable.addComponent(gl);
		hErrorTable.addComponent(this.replayEventsTable);
		
		vSplitPanel.setSecondComponent(hErrorTable);
		vSplitPanel.setSplitPosition(350, Unit.PIXELS);
		
		GridLayout wrapper = new GridLayout(1, 2);
		wrapper.setRowExpandRatio(0, .01f);
		wrapper.setRowExpandRatio(1, .99f);
		wrapper.setSizeFull();
		wrapper.addComponent(filterButtonLayout);
		wrapper.setComponentAlignment(filterButtonLayout, Alignment.MIDDLE_RIGHT);
		wrapper.addComponent(vSplitPanel);
		
		this.setSizeFull();
		this.addComponent(wrapper);
	}
	
	/**
	 * Helper method to resubmit all selected excluded events.
	 */
	protected List<ReplayEvent> getReplayEvents()
	{
		Collection<ReplayEvent> items = (Collection<ReplayEvent>)tableContainer.getItemIds();       	
    	
    	final List<ReplayEvent> myItems = new ArrayList<ReplayEvent>(items);
    	
    	// We need to sort so that we can resubmit the oldest events first!
    	Comparator<ReplayEvent> comparator = new Comparator<ReplayEvent>() 
    	{
    	    public int compare(ReplayEvent c1, ReplayEvent c2) 
    	    {
    	        if (c2.getTimestamp() < c1.getTimestamp())
    	        {
    	        	return 1;
    	        }
    	        else if (c1.getTimestamp() < c2.getTimestamp())
    	        {
    	        	return -1;
    	        }
    	        else
    	        {
    	        	return 0;
    	        }
    	    }
    	};

    	Collections.sort(myItems, comparator);
    	
    	for(ReplayEvent eo: items)
    	{
    		Item item = tableContainer.getItem(eo);
    		
    		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
    		
    		if(cb.getValue() == false)
    		{
    			myItems.remove(eo);
    		}
    	}
    	
    	return myItems;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.ui.topology.component.TopologyTab#search()
	 */
	@Override
	public void search()
	{
		// TODO Auto-generated method stub
		
	}

}


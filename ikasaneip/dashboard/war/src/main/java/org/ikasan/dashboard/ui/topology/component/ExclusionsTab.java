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
package org.ikasan.dashboard.ui.topology.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.ExcludedEventPopup;
import org.ikasan.dashboard.ui.ResubmitIgnorePopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.icons.AtlassianIcons;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.TextWindow;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.panel.ResubmitIgnoreStatusPanel;
import org.ikasan.dashboard.ui.topology.window.ExclusionEventViewWindow;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.hospital.service.HospitalService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ExclusionsTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(ExclusionsTab.class);
	
	private FilterTable exclusionsTable;
	
	private PopupDateField fromDate;
	private PopupDateField toDate;
	
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private ErrorReportingService errorReportingService;
	private ErrorReportingManagementService errorReportingManagementService;
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
	private TopologyService topologyService;
	
	private IndexedContainer container = null;
	
	private HorizontalLayout searchResultsSizeLayout = new HorizontalLayout();
	
	private Label resultsLabel = new Label();
	
	private HospitalService<byte[]> hospitalService;
	
	
	public ExclusionsTab(ErrorReportingService errorReportingService, ErrorReportingManagementService errorReportingManagementService, ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService, TopologyService topologyService, ComboBox businessStreamCombo, HospitalService<byte[]> hospitalService)
	{
		this.errorReportingService = errorReportingService;
		this.errorReportingManagementService = errorReportingManagementService;
		this.exclusionManagementService = exclusionManagementService;
		this.hospitalManagementService = hospitalManagementService;
		this.topologyService = topologyService;
		this.businessStreamCombo = businessStreamCombo;
		this.hospitalService = hospitalService;
	}
	
	protected IndexedContainer buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Error Message", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || 
				authentication.hasGrantedAuthority(SecurityConstants.ACTION_EXCLUSIONS_AUTHORITY)))
		{	
			cont.addContainerProperty("", CheckBox.class,  null);
		}

		cont.addContainerProperty(" ", Button.class,  null);
		
        return cont;
    }
	
	public void createLayout()
	{
		this.container = this.buildContainer();
		
		this.exclusionsTable = new FilterTable();
		this.exclusionsTable.setFilterBarVisible(true);
		this.exclusionsTable.setSizeFull();
		this.exclusionsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.exclusionsTable.setContainerDataSource(container);
		this.exclusionsTable.setColumnExpandRatio("Module Name", .14f);
		this.exclusionsTable.setColumnExpandRatio("Flow Name", .18f);
		this.exclusionsTable.setColumnExpandRatio("Error Message", .33f);
		this.exclusionsTable.setColumnExpandRatio("Timestamp", .1f);
		this.exclusionsTable.setColumnExpandRatio(" ", .05f);
		
		this.exclusionsTable.addStyleName("wordwrap-table");
		this.exclusionsTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.exclusionsTable.addStyleName("ikasan");
		
		
		this.exclusionsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
			    	ExclusionEvent exclusionEvent = (ExclusionEvent)itemClickEvent.getItemId();
			    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEvent.getErrorUri());
			    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
			    	ExclusionEventViewWindow exclusionEventViewWindow = new ExclusionEventViewWindow(exclusionEvent, errorOccurrence
			    			, action, hospitalManagementService, topologyService, errorReportingManagementService, hospitalService);
			    
			    	UI.getCurrent().addWindow(exclusionEventViewWindow);
		    	}
		    }
		});
		
		final Button selectAllButton = new Button();
		
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
            	refreshExcludedEventsTable();
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
				
		GridLayout dateSelectLayout = new GridLayout(2, 1);

		dateSelectLayout.setSizeFull();
		fromDate = new PopupDateField("From date");
		fromDate.setResolution(Resolution.MINUTE);
		fromDate.setValue(this.getMidnightToday());
		fromDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(fromDate, 0, 0);
		toDate = new PopupDateField("To date");
		toDate.setResolution(Resolution.MINUTE);
		toDate.setValue(this.getTwentyThreeFixtyNineToday());
		toDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(toDate, 1, 0);
				
		
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
		hDateSelectLayout.setHeight(40, Unit.PIXELS);
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
		filterPanel.setHeight(300, Unit.PIXELS);
		filterPanel.setWidth("100%");
		filterPanel.setContent(layout);
		filterPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
		vSplitPanel.setFirstComponent(filterPanel);
		
		GridLayout hErrorTable = new GridLayout();
		hErrorTable.setWidth("100%");
		
		
		vSplitPanel.setSecondComponent(hErrorTable);
		vSplitPanel.setSplitPosition(310, Unit.PIXELS);
		
		GridLayout buttons = new GridLayout(6, 1);
		buttons.setWidth("150px");				
		
		
		selectAllButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		selectAllButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
		selectAllButton.setImmediate(true);
		selectAllButton.setDescription("Select / deselect all records below.");
		
		selectAllButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	Collection<ExclusionEvent> items = (Collection<ExclusionEvent>)container.getItemIds();
            	
            	Resource r = selectAllButton.getIcon();
            	
            	if(r.equals(VaadinIcons.CHECK_SQUARE_O))
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE);
            		
            		for(ExclusionEvent eo: items)
                	{
                		Item item = container.getItem(eo);
                		
                		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
                		
                		cb.setValue(true);
                	}
            	}
            	else
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
            		
            		for(ExclusionEvent eo: items)
                	{
                		Item item = container.getItem(eo);
                		
                		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
                		
                		cb.setValue(false);
                	}
            	}
            }
        });
		
		buttons.addComponent(selectAllButton);
		
		Button resubmitSelectedButton = new Button();
		resubmitSelectedButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		resubmitSelectedButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		resubmitSelectedButton.setIcon(VaadinIcons.PLAY);
		resubmitSelectedButton.setImmediate(true);
		resubmitSelectedButton.setDescription("Resubmit all the below selected exclusioned events.");
		
		BrowserWindowOpener resubmitPopupOpener = new BrowserWindowOpener(ResubmitIgnorePopup.class);
		resubmitPopupOpener.setFeatures("height=600,width=900,resizable");
		resubmitPopupOpener.extend(resubmitSelectedButton);
        
        resubmitSelectedButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionEvents", getResubmissionEvents());
     	    	
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("topologyService", topologyService);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportingService", errorReportingService);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportingManagementService", errorReportingManagementService);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionManagementService", exclusionManagementService);
     	 		
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("container", container);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("action", ResubmitIgnoreStatusPanel.RESUBMIT);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("hospitalService", hospitalService);
            }
        });
		
		buttons.addComponent(resubmitSelectedButton);
        
        
		Button ignoreSelectedButton = new Button();
		ignoreSelectedButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		ignoreSelectedButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		ignoreSelectedButton.setIcon(VaadinIcons.BAN);
		ignoreSelectedButton.setImmediate(true);
		ignoreSelectedButton.setDescription("Ignore all the below selected exclusioned events.");
		
		BrowserWindowOpener ignoreOpener = new BrowserWindowOpener(ResubmitIgnorePopup.class);
		ignoreOpener.setFeatures("height=600,width=900,resizable");
        ignoreOpener.extend(ignoreSelectedButton);
        
        ignoreSelectedButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionEvents", getResubmissionEvents());
     	    	
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("topologyService", topologyService);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportingService", errorReportingService);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportingManagementService", errorReportingManagementService);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionManagementService", exclusionManagementService);
     	 		
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("container", container);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("action", ResubmitIgnoreStatusPanel.IGNORE);
     			
     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("hospitalService", hospitalService);
            }
        });
		

		
		buttons.addComponent(ignoreSelectedButton);
		
		Button jiraButton = new Button();
		jiraButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		jiraButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		jiraButton.setIcon(AtlassianIcons.JIRA);
		jiraButton.setImmediate(true);
		jiraButton.setDescription("Export JIRA table");
		
		jiraButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	     
            	createJiraTable();
            }
        });

		
		buttons.addComponent(jiraButton);
		
		Button excelButton = new Button();
		excelButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		excelButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		excelButton.setIcon(FontAwesome.FILE_EXCEL_O);
		excelButton.setImmediate(true);
		excelButton.setDescription("Export Excel table");
		
		FileDownloader fd = new FileDownloader(this.getExcelDownloadStream());
        fd.extend(excelButton);
        
        buttons.addComponent(excelButton);
        
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.addComponent(buttons);
		hl.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
		
		searchResultsSizeLayout.setWidth("100%");
		searchResultsSizeLayout.addComponent(this.resultsLabel);
		searchResultsSizeLayout.setComponentAlignment(this.resultsLabel, Alignment.MIDDLE_LEFT);
		
		GridLayout gl = new GridLayout(2, 1);
		gl.setWidth("100%");
		
		gl.addComponent(searchResultsSizeLayout);
		gl.addComponent(hl);
		
		hErrorTable.addComponent(gl);
		hErrorTable.addComponent(this.exclusionsTable);
		
		GridLayout wrapper = new GridLayout(1, 2);
		wrapper.setRowExpandRatio(0, .01f);
		wrapper.setRowExpandRatio(1, .99f);
		wrapper.setSizeFull();
		wrapper.addComponent(filterButtonLayout);
		wrapper.setComponentAlignment(filterButtonLayout, Alignment.MIDDLE_RIGHT);
		wrapper.addComponent(vSplitPanel);
		
		this.addComponent(wrapper);
		this.setSizeFull();
	}
	
	/**
	 * Helper method to refresh the excluded events table.
	 */
	public void refreshExcludedEventsTable()
	{
		exclusionsTable.removeAllItems();

    	ArrayList<String> modulesNames = null;
    	
    	if(modules.getItemIds().size() > 0)
    	{
        	modulesNames = new ArrayList<String>();
        	for(Object module: modules.getItemIds())
        	{
        		modulesNames.add(((Module)module).getName());
        	}
    	}
    	
    	ArrayList<String> flowNames = null;
    	
    	if(flows.getItemIds().size() > 0)
    	{
    		flowNames = new ArrayList<String>();
    		for(Object flow: flows.getItemIds())
        	{
        		flowNames.add(((Flow)flow).getName());
        	}
    	}
    	
    	
    	if(modulesNames == null && flowNames == null
    			&& !((BusinessStream)businessStreamCombo.getValue()).getName().equals("All"))
    	{
    		BusinessStream businessStream = ((BusinessStream)businessStreamCombo.getValue());
    		
    		modulesNames = new ArrayList<String>();
    		
    		for(BusinessStreamFlow flow: businessStream.getFlows())
    		{
    			modulesNames.add(flow.getFlow().getModule().getName());
    		}
    	}
    	
    	List<ExclusionEvent> exclusionEvents = exclusionManagementService.find(modulesNames,
    			flowNames, fromDate.getValue(),  toDate.getValue(), null);
    	
    	searchResultsSizeLayout.removeAllComponents();
    	this.resultsLabel = new Label("Number of records returned: " + exclusionEvents.size() + " of " + exclusionEvents.size());
    	searchResultsSizeLayout.addComponent(this.resultsLabel);
    	
    	if(exclusionEvents == null || exclusionEvents.size() == 0)
    	{
    		Notification.show("The exclusions search returned no results!", Type.ERROR_MESSAGE);
    	}

    	for(final ExclusionEvent exclusionEvent: exclusionEvents)
    	{
    		Date date = new Date(exclusionEvent.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    final ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEvent.getErrorUri());
    	    
    	    Item item = container.addItem(exclusionEvent);			            	    
    	    
    	    item.getItemProperty("Module Name").setValue(exclusionEvent.getModuleName());
			item.getItemProperty("Flow Name").setValue(exclusionEvent.getFlowName());
			
			if(errorOccurrence != null && errorOccurrence.getErrorMessage() != null)
			{
				if(errorOccurrence.getErrorMessage().length() > 500)
				{
					item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorMessage().substring(0, 500));
				}
				else
				{
					item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorMessage());
				}
			}
			
			item.getItemProperty("Timestamp").setValue(timestamp);
    	    
    	    Button popupButton = new Button();
			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			popupButton.setDescription("Open in new window");
			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			popupButton.setIcon(VaadinIcons.MODAL);
			
			BrowserWindowOpener popupOpener = new BrowserWindowOpener(ExcludedEventPopup.class);
			popupOpener.setFeatures("height=600,width=900,resizable");
	        popupOpener.extend(popupButton);
	        
	        popupButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionEvent", exclusionEvent);
	         	    
	     	    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
	     	    	
	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorOccurrence", errorOccurrence);
	     			
	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionEventAction", action);
	     			
	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("hospitalManagementService", hospitalManagementService);
	     	 		
	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("topologyService", topologyService);
	     			
	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportingManagementService", errorReportingManagementService);
	     			
	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("hospitalService", hospitalService);
	            }
	        });
	        
	        final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
		        	.getAttribute(DashboardSessionValueConstants.USER);
			
			if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || 
					authentication.hasGrantedAuthority(SecurityConstants.ACTION_ERRORS_AUTHORITY))
			{	
				CheckBox cb = new CheckBox();
			
				cb.setValue(false);
				item.getItemProperty("").setValue(cb);
			}
	        
	        item.getItemProperty(" ").setValue(popupButton);    	    	    	    
    	}
	}
	
	/**
	 * Helper method to create a jira table and open a window
	 * displaying it. The window can then be cut and paste from.
	 */
	protected void createJiraTable()
	{
		StringBuffer sb = new StringBuffer();
    	
    	for(Object property: container.getContainerPropertyIds())
    	{
    		if(container.getType(property) == String.class)
    		{
    			sb.append("||").append(property);
    		}
    	}
    	sb.append("||\n");
    	
    	
    	for(Object errorOccurrence: container.getItemIds())
    	{
    		Item item = container.getItem(errorOccurrence);
    		
    		
    		for(Object propertyId: container.getContainerPropertyIds())
	    	{		    			
    			if(container.getType(propertyId) == String.class)
	    		{
    				Property property = item.getItemProperty(propertyId);
    				
    				if(((String)property.getValue()).length() > 300)
    				{
    					sb.append("|").append("{code}").append((String)property.getValue()).append("{code}");
    				}
    				else
    				{
    					sb.append("|").append(property.getValue());
    				}
	    		}
	    	}
    		
    		sb.append("|\n");
    	}
    	
    	
    	TextWindow tw = new TextWindow("Jira Table", sb.toString());
        
        UI.getCurrent().addWindow(tw);
	}
	
	/**
	 * Helper method to ignore all selected excluded events.
	 */
	protected void ignoreExcludedEvents()
	{
		Collection<ExclusionEvent> items = (Collection<ExclusionEvent>)container.getItemIds();
    	
    	final Collection<ExclusionEvent> myItems = new ArrayList<ExclusionEvent>(items);
    	
    	for(ExclusionEvent eo: items)
    	{
    		Item item = container.getItem(eo);
    		
    		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
    		
    		if(cb.getValue() == false)
    		{
    			myItems.remove(eo);
    		}
    	}
    	
    	if(myItems.size() == 0)
    	{
    		Notification.show("You need to select some excluded events to ignore.", Type.ERROR_MESSAGE);
    	}
    	else
    	{
    		
    		for(ExclusionEvent exclusionEvent: myItems)
    		{
    		
        		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        	        	.getAttribute(DashboardSessionValueConstants.USER);
            	
            	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
            	
            	ClientConfig clientConfig = new ClientConfig();
            	clientConfig.register(feature) ;
            	
            	Client client = ClientBuilder.newClient(clientConfig);
            	
            	Module module = topologyService.getModuleByName(exclusionEvent.getModuleName());
            	
            	if(module == null)
            	{
            		Notification.show("Unable to find server information for module we are attempting to re-submit to: " + exclusionEvent.getModuleName() 
            				, Type.ERROR_MESSAGE);
            		
            		return;
            	}
            	
            	Server server = module.getServer();
        		
            	String url = server.getUrl() + ":" + server.getPort()
        				+ module.getContextRoot() 
        				+ "/rest/resubmission/ignore/"
        				+ exclusionEvent.getModuleName() 
        	    		+ "/"
        	    		+ exclusionEvent.getFlowName()
        	    		+ "/"
        	    		+ exclusionEvent.getErrorUri();
        		
        		logger.debug("Ignore Url: " + url);
        		
        	    WebTarget webTarget = client.target(url);
        	    Response response = webTarget.request().put(Entity.entity(exclusionEvent.getEvent(), MediaType.APPLICATION_OCTET_STREAM));
        	    
        	    if(response.getStatus()  != 200)
        	    {
        	    	response.bufferEntity();
        	        
        	        String responseMessage = response.readEntity(String.class);
        	        
        	        logger.error("An error was received trying to resubmit event: " + responseMessage); 
        	        
        	    	Notification.show("An error was received trying to resubmit event: " 
        	    			+ responseMessage, Type.ERROR_MESSAGE);
        	    	
        	    	return;
        	    }
        	    else
        	    {
        	    	container.removeItem(exclusionEvent);
        	    }
    		}
    		
    		Notification.show("Events ignored successfully.");
    	}
	}
	
	/**
	 * Helper method to resubmit all selected excluded events.
	 */
	protected List<ExclusionEvent> getResubmissionEvents()
	{
		Collection<ExclusionEvent> items = (Collection<ExclusionEvent>)container.getItemIds();       	
    	
    	final List<ExclusionEvent> myItems = new ArrayList<ExclusionEvent>(items);
    	
    	// We need to sort so that we can resubmit the oldest events first!
    	Comparator<ExclusionEvent> comparator = new Comparator<ExclusionEvent>() 
    	{
    	    public int compare(ExclusionEvent c1, ExclusionEvent c2) 
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
    	
    	for(ExclusionEvent eo: items)
    	{
    		Item item = container.getItem(eo);
    		
    		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
    		
    		if(cb.getValue() == false)
    		{
    			myItems.remove(eo);
    		}
    	}
    	
    	return myItems;
	}
	
	/**
     * Helper method to get the stream associated with the export of the file.
     * 
     * @return the StreamResource associated with the export.
     */
    private StreamResource getExcelDownloadStream() 
    {
		StreamResource.StreamSource source = new StreamResource.StreamSource() 
		{
		    public InputStream getStream() 
		    {
		    	ByteArrayOutputStream stream = null;
		    	
		        try
		        {
		            stream = getExcelStream();
		        }
		        catch (IOException e)
		        {
		        	logger.error(e.getMessage(), e);
		        }
		        
		        InputStream input = new ByteArrayInputStream(stream.toByteArray());
		        return input;
		    }
		};
            
	    StreamResource resource = new StreamResource ( source,"exclusions.csv");
	    return resource;
    }
    
    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     * 
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getExcelStream() throws IOException
    {
    	ByteArrayOutputStream out = new ByteArrayOutputStream(); 
    	
    	StringBuffer sb = new StringBuffer();
    	
    	for(Object property: container.getContainerPropertyIds())
    	{
    		if(container.getType(property) == String.class)
    		{
    			sb.append(property).append(",");
    		}
    	}
    	
    	sb.append("\r\n");
    	    	
    	for(Object errorOccurrence: container.getItemIds())
    	{
    		Item item = container.getItem(errorOccurrence);
    		
    		
    		for(Object propertyId: container.getContainerPropertyIds())
	    	{		    			
    			if(container.getType(propertyId) == String.class)
	    		{
    				Property property = item.getItemProperty(propertyId);
    				
    				String csvCell = (String)property.getValue();
    				
    				if(csvCell != null && csvCell.contains("\""))
    				{
    					csvCell = csvCell.replaceAll("\"", "\"\"");
    				}
    				
    				// Max length of a CSV cell in EXCEL
    				if(csvCell != null && csvCell.length() > 32760)
    				{
    					csvCell = csvCell.substring(0, 32759);
    				}
    					
    				sb.append("\"").append(csvCell).append("\",");
	    		}
	    	}
    		
    		sb.append("\r\n");
    	}
    	
    	out.write(sb.toString().getBytes());
        
        return out;
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

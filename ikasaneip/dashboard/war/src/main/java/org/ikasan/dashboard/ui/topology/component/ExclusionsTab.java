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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.ExclusionEventViewWindow;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ExclusionsTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(ExclusionsTab.class);
	
	private Table exclusionsTable;
	
	private Table exclusionModules = new Table("Modules");
	private Table exclusionFlows = new Table("Flows");
	
	private PopupDateField fromDate;
	private PopupDateField toDate;
	
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private ErrorReportingService errorReportingService;
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	private HospitalManagementService<ExclusionEventAction> hospitalManagementService;
	private TopologyService topologyService;
	
	
	public ExclusionsTab(ErrorReportingService errorReportingService, ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			HospitalManagementService<ExclusionEventAction> hospitalManagementService, TopologyService topologyService, ComboBox businessStreamCombo)
	{
		this.errorReportingService = errorReportingService;
		this.exclusionManagementService = exclusionManagementService;
		this.hospitalManagementService = hospitalManagementService;
		this.topologyService = topologyService;
		this.businessStreamCombo = businessStreamCombo;
	}
	
	public Layout createLayout()
	{
		this.exclusionsTable = new Table();
		this.exclusionsTable.setSizeFull();
		this.exclusionsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.exclusionsTable.addContainerProperty("Module Name", String.class,  null);
		this.exclusionsTable.addContainerProperty("Flow Name", String.class,  null);
		this.exclusionsTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.exclusionsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	ExclusionEvent exclusionEvent = (ExclusionEvent)itemClickEvent.getItemId();
		    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEvent.getErrorUri());
		    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
		    	ExclusionEventViewWindow exclusionEventViewWindow = new ExclusionEventViewWindow(exclusionEvent, errorOccurrence
		    			, action, hospitalManagementService, topologyService);
		    	
		    	// removing this for the moment as it is causing a performance problem
//		    	exclusionEventViewWindow.addCloseListener(new Window.CloseListener()
//		    	{
//		            // inline close-listener
//		            public void windowClose(CloseEvent e) 
//		            {
//		            	refreshExcludedEventsTable();
//		            }
//		        });
		    
		    	UI.getCurrent().addWindow(exclusionEventViewWindow);
		    }
		});
		
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	refreshExcludedEventsTable();
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.setStyleName(ValoTheme.BUTTON_SMALL);
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	exclusionModules.removeAllItems();
            	exclusionFlows.removeAllItems();
            }
        });

		GridLayout layout = new GridLayout(1, 6);
		layout.setMargin(false);
		layout.setHeight(270 , Unit.PIXELS);
		
		GridLayout listSelectLayout = new GridLayout(2, 1);
		listSelectLayout.setSpacing(true);
		listSelectLayout.setSizeFull();
		
		exclusionModules.setIcon(VaadinIcons.ARCHIVE);
		exclusionModules.addContainerProperty("Module Name", String.class,  null);
		exclusionModules.addContainerProperty("", Button.class,  null);
		exclusionModules.setSizeFull();
		exclusionModules.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		exclusionModules.setDragMode(TableDragMode.ROW);
		exclusionModules.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Module)
				{
					final Module module = (Module) t
							.getItemId();
					
					Button deleteButton = new Button();
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	exclusionModules.removeItem(module);
			            }
			        });
					
					exclusionModules.addItem(new Object[]{module.getName(), deleteButton}, module);

					for(final Flow flow: module.getFlows())
					{
						
						deleteButton = new Button();
						deleteButton.setIcon(VaadinIcons.TRASH);
						deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
						deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	exclusionFlows.removeItem(flow);
				            }
				        });
						
						exclusionFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		listSelectLayout.addComponent(exclusionModules, 0, 0);
		
		exclusionFlows.setIcon(VaadinIcons.AUTOMATION);
		exclusionFlows.addContainerProperty("Flow Name", String.class,  null);
		exclusionFlows.addContainerProperty("", Button.class,  null);
		exclusionFlows.setSizeFull();
		exclusionFlows.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		exclusionFlows.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Flow)
				{
					final Flow flow = (Flow) t
							.getItemId();
				
					Button deleteButton = new Button();
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	exclusionFlows.removeItem(flow);
			            }
			        });
					
					exclusionFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});

		listSelectLayout.addComponent(exclusionFlows, 1, 0);
				
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
		
		CssLayout hErrorTable = new CssLayout();
		hErrorTable.setSizeFull();
		hErrorTable.addComponent(this.exclusionsTable);
		
		vSplitPanel.setSecondComponent(hErrorTable);
		vSplitPanel.setSplitPosition(310, Unit.PIXELS);
		
		GridLayout wrapper = new GridLayout(1, 2);
		wrapper.setRowExpandRatio(0, .01f);
		wrapper.setRowExpandRatio(1, .99f);
		wrapper.setSizeFull();
		wrapper.addComponent(filterButtonLayout);
		wrapper.setComponentAlignment(filterButtonLayout, Alignment.MIDDLE_RIGHT);
		wrapper.addComponent(vSplitPanel);
		
		return wrapper;
	}
	
	public void refreshExcludedEventsTable()
	{
		exclusionsTable.removeAllItems();

    	ArrayList<String> modulesNames = null;
    	
    	if(exclusionModules.getItemIds().size() > 0)
    	{
        	modulesNames = new ArrayList<String>();
        	for(Object module: exclusionModules.getItemIds())
        	{
        		modulesNames.add(((Module)module).getName());
        	}
    	}
    	
    	ArrayList<String> flowNames = null;
    	
    	if(exclusionFlows.getItemIds().size() > 0)
    	{
    		flowNames = new ArrayList<String>();
    		for(Object flow: exclusionFlows.getItemIds())
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
    	
    	if(exclusionEvents == null || exclusionEvents.size() == 0)
    	{
    		Notification.show("The exclusions search returned no results!", Type.ERROR_MESSAGE);
    	}

    	for(ExclusionEvent exclusionEvent: exclusionEvents)
    	{
    		Date date = new Date(exclusionEvent.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    exclusionsTable.addItem(new Object[]{exclusionEvent.getModuleName(), exclusionEvent.getFlowName(),
    				timestamp}, exclusionEvent);
    	}
	}

}

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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.ActionedErrorOccurrencePopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.ActionedErrorOccurrenceViewWindow;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ActionedErrorOccurrenceTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(ActionedErrorOccurrenceTab.class);
	
	private FilterTable errorOccurenceTable;
	
	private PopupDateField errorFromDate;
	private PopupDateField errorToDate;
	
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private Container container = null;
	
	private ErrorReportingService errorReportingService;
	private ErrorReportingManagementService errorReportingManagementService;
	
	public ActionedErrorOccurrenceTab(ErrorReportingService errorReportingService,
			ComboBox businessStreamCombo, ErrorReportingManagementService errorReportingManagementService)
	{
		this.errorReportingService = errorReportingService;
		if(this.errorReportingService == null)
		{
			throw new IllegalArgumentException("errorReportingService cannot be null!");
		}
		this.errorReportingManagementService = errorReportingManagementService;
		if(this.errorReportingManagementService == null)
		{
			throw new IllegalArgumentException("errorReportingManagementService cannot be null!");
		}
		
		this.businessStreamCombo = businessStreamCombo;
	}
	
	protected Container buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Component Name", String.class,  null);
		cont.addContainerProperty("Error Message", String.class,  null);
		cont.addContainerProperty("Action Time", String.class,  null);
		cont.addContainerProperty("Action By", String.class,  null);
		cont.addContainerProperty("N/L", Layout.class,  null);
		cont.addContainerProperty("", Button.class,  null);
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || 
				authentication.hasGrantedAuthority(SecurityConstants.ACTION_ERRORS_AUTHORITY))
		{	
			cont.addContainerProperty("", CheckBox.class,  null);
		}

        return cont;
    }
	
	public void createLayout()
	{
		container = buildContainer();
		this.errorOccurenceTable = new FilterTable();
		this.errorOccurenceTable.setFilterBarVisible(true);
		this.errorOccurenceTable.setSizeFull();
		this.errorOccurenceTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.errorOccurenceTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.errorOccurenceTable.addStyleName("ikasan");
		this.errorOccurenceTable.setContainerDataSource(container);
		this.errorOccurenceTable.setColumnExpandRatio("Module Name", .14f);
		this.errorOccurenceTable.setColumnExpandRatio("Flow Name", .18f);
		this.errorOccurenceTable.setColumnExpandRatio("Component Name", .2f);
		this.errorOccurenceTable.setColumnExpandRatio("Error Message", .33f);
		this.errorOccurenceTable.setColumnExpandRatio("Action Time", .15f);
		this.errorOccurenceTable.setColumnExpandRatio("Action By", .1f);
		this.errorOccurenceTable.setColumnExpandRatio("N/L", .05f);
		
		this.errorOccurenceTable.addStyleName("wordwrap-table");
		
		this.errorOccurenceTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
			    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)itemClickEvent.getItemId();
			    	ActionedErrorOccurrenceViewWindow errorOccurrenceViewWindow 
			    		= new ActionedErrorOccurrenceViewWindow(errorOccurrence, errorReportingManagementService);
			    	
			    	UI.getCurrent().addWindow(errorOccurrenceViewWindow);
		    	}
		    }
		});
				
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	refreshTable(true, null);
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
		
		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSpacing(true);
		listSelectLayout.setSizeFull();
		listSelectLayout.addComponent(super.modules, 0, 0);
		listSelectLayout.addComponent(super.flows, 1, 0);
		listSelectLayout.addComponent(super.components, 2, 0);
				
		GridLayout dateSelectLayout = new GridLayout(2, 1);

		dateSelectLayout.setSizeFull();
		errorFromDate = new PopupDateField("From date");
		errorFromDate.setResolution(Resolution.MINUTE);
		errorFromDate.setValue(this.getMidnightToday());
		errorFromDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(errorFromDate, 0, 0);
		errorToDate = new PopupDateField("To date");
		errorToDate.setResolution(Resolution.MINUTE);
		errorToDate.setValue(this.getTwentyThreeFixtyNineToday());
		errorToDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(errorToDate, 1, 0);
				
		
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
		
		GridLayout buttons = new GridLayout(3, 1);
		buttons.setWidth("80px");
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.addComponent(buttons);
		hl.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || 
				authentication.hasGrantedAuthority(SecurityConstants.ACTION_ERRORS_AUTHORITY))
		{	
			hErrorTable.addComponent(hl);
		}
		
		hErrorTable.addComponent(this.errorOccurenceTable);
		
		vSplitPanel.setSecondComponent(hErrorTable);
		vSplitPanel.setSplitPosition(310, Unit.PIXELS);
		
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
	
	protected void refreshTable(boolean showError, Collection<ErrorOccurrence> myItems)
	{
		errorOccurenceTable.removeAllItems();

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
    	
    	ArrayList<String> componentNames = null;
    	
    	if(components.getItemIds().size() > 0)
    	{
    		componentNames = new ArrayList<String>();
        	for(Object component: components.getItemIds())
        	{
        		componentNames.add(((Component)component).getName());
        	}
    	}
    	
    	if(modulesNames == null && flowNames == null && componentNames == null
    			&& !((BusinessStream)businessStreamCombo.getValue()).getName().equals("All"))
    	{
    		BusinessStream businessStream = ((BusinessStream)businessStreamCombo.getValue());
    		
    		modulesNames = new ArrayList<String>();
    		
    		for(BusinessStreamFlow flow: businessStream.getFlows())
    		{
    			modulesNames.add(flow.getFlow().getModule().getName());
    		}
    	}
    	
		List<ErrorOccurrence> errorOccurrences = errorReportingManagementService
    			.find(modulesNames, flowNames, componentNames, errorFromDate.getValue(), errorToDate.getValue());
    	
    	if((errorOccurrences == null || errorOccurrences.size() == 0) && showError)
    	{
    		Notification.show("The error search returned no results!", Type.ERROR_MESSAGE);
    	}
    	
    	List<String> noteUris =  this.errorReportingManagementService.getAllErrorUrisWithNote();

    	for(ErrorOccurrence errorOccurrence: errorOccurrences)
    	{
    		Date date = new Date(errorOccurrence.getUserActionTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    Item item = container.addItem(errorOccurrence);			            	    
    	    
    	    item.getItemProperty("Module Name").setValue(errorOccurrence.getModuleName());
			item.getItemProperty("Flow Name").setValue(errorOccurrence.getFlowName());
			item.getItemProperty("Component Name").setValue(errorOccurrence.getFlowElementName());
			item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorMessage());
			item.getItemProperty("Action Time").setValue(timestamp);
			item.getItemProperty("Action By").setValue(errorOccurrence.getActionedBy());
						
			HorizontalLayout layout = new HorizontalLayout();
    	    layout.setSpacing(true);
    	    
    	    Label label = new Label(VaadinIcons.COMMENT.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);
			
			if(noteUris.contains(errorOccurrence.getUri()))
			{
				layout.addComponent(label);
			}
			
			label = new Label(VaadinIcons.LINK.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);			
			
			item.getItemProperty("N/L").setValue(layout);
			
			Button popupButton = new Button();
			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			popupButton.setDescription("Open in new tab");
			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			popupButton.setIcon(VaadinIcons.MODAL);

	        BrowserWindowOpener popupOpener = new BrowserWindowOpener(ActionedErrorOccurrencePopup.class);
	        popupOpener.extend(popupButton);
	        
	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportService", this.errorReportingService);
	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportManagementService", this.errorReportingManagementService);
	        // Add a parameter for the error uri.
	        popupOpener.setParameter("errorUri", errorOccurrence.getUri());
	        
	        item.getItemProperty("").setValue(popupButton);
    	    
    	}
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

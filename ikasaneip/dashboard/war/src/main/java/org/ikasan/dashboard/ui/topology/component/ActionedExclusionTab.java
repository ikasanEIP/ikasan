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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.ActionedExcludedEventPopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.icons.AtlassianIcons;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.TextWindow;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.ActionedExclusionEventViewWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceCloseWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceCommentWindow;
import org.ikasan.dashboard.ui.topology.window.ExclusionEventViewWindow;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
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
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window.CloseEvent;
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
public class ActionedExclusionTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(ActionedExclusionTab.class);
	
	private FilterTable actionedExclusionsTable;
	
	private PopupDateField fromDate;
	private PopupDateField toDate;
	
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
	private ErrorReportingService errorReportingService;
	private TopologyService topologyService;
	
	private IndexedContainer container = null;
	
	private PlatformConfigurationService platformConfigurationService;
	
	private HorizontalLayout searchResultsSizeLayout = new HorizontalLayout();
	
	private Label resultsLabel = new Label();
	
	private String jiraClipboard;
	
	
	public ActionedExclusionTab(ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService,ErrorReportingService errorReportingService,
			TopologyService topologyService, ComboBox businessStreamCombo, PlatformConfigurationService platformConfigurationService)
	{
		this.exclusionManagementService = exclusionManagementService;
		this.hospitalManagementService = hospitalManagementService;
		this.errorReportingService = errorReportingService;
		this.topologyService = topologyService;
		this.businessStreamCombo = businessStreamCombo;
		this.platformConfigurationService = platformConfigurationService;
	}
	
	protected IndexedContainer buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Error URI", String.class,  null);
		cont.addContainerProperty("Action", String.class,  null);
		cont.addContainerProperty("Actioned By", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("", Button.class,  null);
		
        return cont;
    }
	
	public void createLayout()
	{	
		this.container = this.buildContainer();
		
		this.actionedExclusionsTable = new FilterTable();
		this.actionedExclusionsTable.setFilterBarVisible(true);
		this.actionedExclusionsTable.setSizeFull();
		this.actionedExclusionsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.actionedExclusionsTable.setContainerDataSource(container);
		this.actionedExclusionsTable.setColumnExpandRatio("Module Name", .18f);
		this.actionedExclusionsTable.setColumnExpandRatio("Flow Name", .18f);
		this.actionedExclusionsTable.setColumnExpandRatio("Error URI", .18f);
		this.actionedExclusionsTable.setColumnExpandRatio("Action", .10f);
		this.actionedExclusionsTable.setColumnExpandRatio("Actioned By", .10f);
		this.actionedExclusionsTable.setColumnExpandRatio("Timestamp", .1f);
		this.actionedExclusionsTable.setColumnExpandRatio(" ", .05f);
		
		this.actionedExclusionsTable.addStyleName("wordwrap-table");
		this.actionedExclusionsTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.actionedExclusionsTable.addStyleName("ikasan");
		
		
		this.actionedExclusionsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
		    		ExclusionEventAction exclusionEventAction = (ExclusionEventAction)itemClickEvent.getItemId();
			    	
			    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEventAction.getErrorUri());
			    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEventAction.getErrorUri());
			    	ActionedExclusionEventViewWindow actionExclusionEventViewWindow = new ActionedExclusionEventViewWindow(errorOccurrence, 
			    			action, hospitalManagementService, topologyService);
			    
			    	UI.getCurrent().addWindow(actionExclusionEventViewWindow);
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
            	actionedExclusionsTable.removeAllItems();

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
            	
            	if(components.getItemIds().size() > 0 
            			&& modules.getItemIds().size() == 0
            			&& flows.getItemIds().size() == 0)
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
            	
            	logger.info("Trying to search for ExclusionEventAction");
            	List<ExclusionEventAction> exclusionEventActions = hospitalManagementService.getActionedExclusions
            			(modulesNames, flowNames, fromDate.getValue(), toDate.getValue(), platformConfigurationService.getSearchResultSetSize());
            	
            	logger.info("Results ExclusionEventAction: " + exclusionEventActions.size());

            	if(exclusionEventActions == null || exclusionEventActions.size() == 0)
            	{
            		Notification.show("The actioned exclusions search returned no results!", Type.ERROR_MESSAGE);
            	}
            	
            	Long resultSize = hospitalManagementService.actionedExclusionsRowCount(modulesNames, 
            			flowNames, fromDate.getValue(), toDate.getValue());
            	
            	searchResultsSizeLayout.removeAllComponents();
            	resultsLabel = new Label("Number of records returned: " + exclusionEventActions.size() + " of " + resultSize);
            	searchResultsSizeLayout.addComponent(resultsLabel);
            	
            	if(resultSize > platformConfigurationService.getSearchResultSetSize())
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
            	
            	for(final ExclusionEventAction exclusionEventAction: exclusionEventActions)
            	{
            		Date date = new Date(exclusionEventAction.getTimestamp());
            		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
            	    String timestamp = format.format(date);
            	    
            	    Item item = container.addItem(exclusionEventAction);	
            	    
            	    item.getItemProperty("Module Name").setValue(exclusionEventAction.getModuleName());
        			item.getItemProperty("Flow Name").setValue(exclusionEventAction.getFlowName());
        			item.getItemProperty("Error URI").setValue(exclusionEventAction.getErrorUri());
        			item.getItemProperty("Action").setValue(exclusionEventAction.getAction());
        			item.getItemProperty("Actioned By").setValue(exclusionEventAction.getActionedBy());
        			item.getItemProperty("Timestamp").setValue(timestamp);
            	    
            	    Button popupButton = new Button();
        			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        			popupButton.setDescription("Open in new tab");
        			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        			popupButton.setIcon(VaadinIcons.MODAL);
        			
        			BrowserWindowOpener popupOpener = new BrowserWindowOpener(ActionedExcludedEventPopup.class);
        			popupOpener.setFeatures("height=600,width=900,resizable");
        	        popupOpener.extend(popupButton);
        	        
        	        popupButton.addClickListener(new Button.ClickListener() 
        	    	{
        	            public void buttonClick(ClickEvent event) 
        	            {
        	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionEventAction", exclusionEventAction);
        	         	    
        	            	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEventAction.getErrorUri());
        	     	    	
        	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorOccurrence", errorOccurrence);
        	     			
        	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("hospitalManagementService", hospitalManagementService);
        	     	 		
        	     			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("topologyService", topologyService);
        	            }
        	        });
            	    
        	        item.getItemProperty("").setValue(popupButton);
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
		
		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSpacing(true);
		listSelectLayout.setSizeFull();
		listSelectLayout.addComponent(super.modules, 0, 0);
		listSelectLayout.addComponent(super.flows, 1, 0);
		listSelectLayout.addComponent(super.components, 2, 0);
						
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
		vSplitPanel.setHeight("100%");
		
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
		
		GridLayout buttons = new GridLayout(2, 1);
		buttons.setWidth("50px");
		
		
		
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
            	StringBuffer sb = new StringBuffer();
		    	
		    	for(Object property: container.getContainerPropertyIds())
		    	{
		    		if(container.getType(property) == String.class)
		    		{
		    			sb.append("||").append(property);
		    		}
		    	}
		    	sb.append("||\n");
		    	
		    	
		    	for(Object actionedExclusion: container.getItemIds())
		    	{
		    		Item item = container.getItem(actionedExclusion);
		    		
		    		
		    		for(Object propertyId: container.getContainerPropertyIds())
			    	{		    			
		    			if(container.getType(propertyId) == String.class)
			    		{
		    				Property property = item.getItemProperty(propertyId);
		    				
		    				sb.append("|").append(property.getValue());
			    		}
			    	}
		    		
		    		sb.append("|\n");
		    	}
		    	
		    	jiraClipboard = sb.toString();
            	
            	TextWindow tw = new TextWindow("Jira Table", jiraClipboard);
                
                UI.getCurrent().addWindow(tw);
            }
        });
		
		Button excelButton = new Button();
		excelButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		excelButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		excelButton.setIcon(FontAwesome.FILE_EXCEL_O);
		excelButton.setImmediate(true);
		excelButton.setDescription("Export Excel table");
		
		FileDownloader fd = new FileDownloader(this.getExcelDownloadStream());
        fd.extend(excelButton);
		
		buttons.addComponent(jiraButton);
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

		VerticalSplitPanel vpanel = new VerticalSplitPanel(gl
				, this.actionedExclusionsTable);
		vpanel.setSplitPosition(30, Unit.PIXELS);
		vpanel.setLocked(true);
		
		vSplitPanel.setSecondComponent(vpanel);
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
            
	    StreamResource resource = new StreamResource ( source,"errors.csv");
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
    		if(container.getType(property) == String.class
    				|| container.getType(property) == byte[].class)
    		{
    			sb.append(property).append(",");
    		}
    	}
    	
    	sb.append("\r\n");
    	
    	
    	for(Object actionedExclusion: container.getItemIds())
    	{
    		Item item = container.getItem(actionedExclusion);
    		
    		
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

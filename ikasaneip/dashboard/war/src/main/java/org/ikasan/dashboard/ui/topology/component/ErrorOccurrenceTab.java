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
import org.ikasan.dashboard.ui.ErrorOccurrencePopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.icons.AtlassianIcons;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.TextWindow;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceCloseWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceCommentWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceViewWindow;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
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
public class ErrorOccurrenceTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(ErrorOccurrenceTab.class);
	
	private FilterTable errorOccurenceTable;

	private PopupDateField errorFromDate;
	private PopupDateField errorToDate;
	
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private IndexedContainer container = null;
	
	private ErrorReportingService errorReportingService;
	private ErrorReportingManagementService errorReportingManagementService;
	private PlatformConfigurationService platformConfigurationService;
	
	private Label resultsLabel = new Label();
	
	private HorizontalLayout searchResultsSizeLayout = new HorizontalLayout();
	
	private String errorClipboard;
	private String jiraClipboard;
	
	public ErrorOccurrenceTab(ErrorReportingService errorReportingService,
			ComboBox businessStreamCombo, ErrorReportingManagementService errorReportingManagementService,
			PlatformConfigurationService platformConfigurationService)
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
		this.platformConfigurationService = platformConfigurationService;
		if(this.platformConfigurationService == null)
		{
			throw new IllegalArgumentException("platformConfigurationService cannot be null!");
		}
		
		this.businessStreamCombo = businessStreamCombo;
	}
	
	protected IndexedContainer buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Component Name", String.class,  null);
		cont.addContainerProperty("Error Message", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("N/L", Layout.class,  null);
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || 
				authentication.hasGrantedAuthority(SecurityConstants.ACTION_ERRORS_AUTHORITY)))
		{	
			cont.addContainerProperty("", CheckBox.class,  null);
		}
		
		
		cont.addContainerProperty(" ", Button.class,  null);

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
		this.errorOccurenceTable.setColumnExpandRatio("Timestamp", .1f);
		this.errorOccurenceTable.setColumnExpandRatio("N/L", .05f);
		this.errorOccurenceTable.setColumnExpandRatio("", .05f);
		
		this.errorOccurenceTable.addStyleName("wordwrap-table");
		
		this.errorOccurenceTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if (itemClickEvent.isDoubleClick())
		    	{
			    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)itemClickEvent.getItemId();
			    	ErrorOccurrenceViewWindow errorOccurrenceViewWindow = new ErrorOccurrenceViewWindow(errorOccurrence, errorReportingManagementService,
			    			platformConfigurationService);
			    	
			    	UI.getCurrent().addWindow(errorOccurrenceViewWindow);
		    	}
		    }
		});
		
		this.errorOccurenceTable.setItemDescriptionGenerator(new ItemDescriptionGenerator() 
		{                             
			@Override
			public String generateDescription(com.vaadin.ui.Component source,
					Object itemId, Object propertyId)
			{
				 return "Double click the table row to view details of error "+ ((ErrorOccurrence)(itemId)).getUri();
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
		
		GridLayout hErrorTable = new GridLayout();
		hErrorTable.setWidth("100%");
		
		GridLayout buttons = new GridLayout(6, 1);
		buttons.setWidth("170px");
		
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
            	Collection<ErrorOccurrence> items = (Collection<ErrorOccurrence>)container.getItemIds();
            	
            	Resource r = selectAllButton.getIcon();
            	
            	if(r.equals(VaadinIcons.CHECK_SQUARE_O))
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE);
            		
            		for(ErrorOccurrence eo: items)
                	{
                		Item item = container.getItem(eo);
                		
                		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
                		
                		cb.setValue(true);
                	}
            	}
            	else
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
            		
            		for(ErrorOccurrence eo: items)
                	{
                		Item item = container.getItem(eo);
                		
                		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
                		
                		cb.setValue(false);
                	}
            	}
            }
        });
		
		Button closeSelectedButton = new Button();
		closeSelectedButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		closeSelectedButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		closeSelectedButton.setIcon(VaadinIcons.CLOSE);
		closeSelectedButton.setImmediate(true);
		closeSelectedButton.setDescription("Close all selected errors below");
		
		closeSelectedButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	            	
            	Collection<ErrorOccurrence> items = (Collection<ErrorOccurrence>)container.getItemIds();
            	
            	final Collection<ErrorOccurrence> myItems = new ArrayList<ErrorOccurrence>(items);
            	
            	for(ErrorOccurrence eo: items)
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
            		Notification.show("You need to select some errors to close.", Type.ERROR_MESSAGE);
            	}
            	else
            	{
	            	final ErrorOccurrenceCloseWindow window = new ErrorOccurrenceCloseWindow(errorReportingManagementService, 
	            			myItems);
	            	
	            	window.addCloseListener(new Window.CloseListener() 
	            	{
	                    public void windowClose(CloseEvent e) 
	                    {
	                    	if(window.getAction().equals(ErrorOccurrenceCloseWindow.CLOSE))
	                    	{
	                    		updateCancel((Collection<ErrorOccurrence>) myItems);
	                    	}
	                    }
	                });
			    	
	            	UI.getCurrent().addWindow(window);
            	}
            }
        });
		
		Button commentSelectedButton = new Button();
		commentSelectedButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		commentSelectedButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		commentSelectedButton.setIcon(VaadinIcons.COMMENT);
		commentSelectedButton.setImmediate(true);
		commentSelectedButton.setDescription("Comment on selected errors below");
		
		commentSelectedButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	Collection<ErrorOccurrence> items = (Collection<ErrorOccurrence>)container.getItemIds();
            	
            	final Collection<ErrorOccurrence> myItems = new ArrayList<ErrorOccurrence>(items);
            	
            	for(ErrorOccurrence eo: items)
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
            		Notification.show("You need to select some errors to comment on!", Type.ERROR_MESSAGE);
            	}
            	else
            	{
	            	final ErrorOccurrenceCommentWindow window = new ErrorOccurrenceCommentWindow(errorReportingManagementService, 
	            			myItems);
	            	
	            	window.addCloseListener(new Window.CloseListener() 
	            	{
	                    public void windowClose(CloseEvent e) 
	                    {
	                    	if(window.getAction().equals(ErrorOccurrenceCommentWindow.COMMENT))
	                    	{
	                    		updateComments(myItems);
	                    	}
	                    }
	                });
			    	
	            	UI.getCurrent().addWindow(window);
            	}
            }
        });
				
		Button copyButton = new Button();
		copyButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		copyButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		copyButton.setIcon(VaadinIcons.LINK);
		copyButton.setImmediate(true);
		copyButton.setDescription("Copy error urls");
		
		copyButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	String dashboardUrl = platformConfigurationService.getConfigurationValue("dashboardBaseUrl");
            	
            	if(dashboardUrl == null || dashboardUrl.length() == 0)
        		{
        			Notification.show("A value for dashboardBaseUrl has not been configured in the Platform Configuration. " +
        					"Cannot display the error URLs!", Type.WARNING_MESSAGE);
        			return;
        		}
            	
		    	StringBuffer sb = new StringBuffer();
		    	
		    	for(ErrorOccurrence errorOccurrence: (List<ErrorOccurrence>)container.getItemIds())
		    	{
					sb.append(buildErrorUrl(dashboardUrl, errorOccurrence)).append("\n");	    	
		    	}
		    	
		    	errorClipboard = sb.toString();
            	
                TextWindow tw = new TextWindow("Error Links", errorClipboard);
                
                UI.getCurrent().addWindow(tw);
            }
        });
		
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
		    	
		    	
		    	for(Object errorOccurrence: container.getItemIds())
		    	{
		    		Item item = container.getItem(errorOccurrence);
		    		
		    		
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
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || 
				authentication.hasGrantedAuthority(SecurityConstants.ACTION_ERRORS_AUTHORITY)))
		{	
			buttons.addComponent(selectAllButton);
			buttons.addComponent(closeSelectedButton);
			buttons.addComponent(commentSelectedButton);
		}
		

		buttons.addComponent(copyButton);
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
				, this.errorOccurenceTable);
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

    	sb.append("Module Name").append(",");
    	sb.append("Flow Name").append(",");
    	sb.append("Component Name").append(",");
    	sb.append("Error Message").append(",");
    	sb.append("Timesamp").append(",");
    	sb.append("Error Url").append("\r\n");
    	
    	
    	String dashboardUrl = platformConfigurationService.getConfigurationValue("dashboardBaseUrl");
    	
    	if(dashboardUrl == null || dashboardUrl.length() == 0)
		{
			Notification.show("A value for dashboardBaseUrl has not been configured in the Platform Configuration. " +
					"Cannot include the error URLs in the downloaded CSV!", Type.WARNING_MESSAGE);
		}
    	
    	for(Object errorOccurrence: container.getItemIds())
    	{    		
    		sb.append("\"").append(((ErrorOccurrence)errorOccurrence).getModuleName()).append("\",");
    		sb.append("\"").append(((ErrorOccurrence)errorOccurrence).getFlowName()).append("\",");
    		sb.append("\"").append(((ErrorOccurrence)errorOccurrence).getFlowElementName()).append("\",");

			if(((ErrorOccurrence)errorOccurrence).getErrorMessage() != null) {
				sb.append("\"").append(((ErrorOccurrence) errorOccurrence).getErrorMessage().length() > 32760 ?
						((ErrorOccurrence) errorOccurrence).getErrorMessage().replaceAll("\"", "\"\"").substring(0, 32759) :
						((ErrorOccurrence) errorOccurrence).getErrorMessage().replaceAll("\"", "\"\"")).append("\",");
			}
			else
			{
				sb.append("\"").append("NULL").append("\",");
			}
    		
    		Date date = new Date(((ErrorOccurrence)errorOccurrence).getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    sb.append("\"").append(timestamp).append("\",");
    	    
    		if(dashboardUrl != null)
    		{
    			sb.append("\"").append(this.buildErrorUrl(dashboardUrl, (ErrorOccurrence)errorOccurrence)).append("\"");
    		}
    		
    		sb.append("\r\n");
    	}
    	
    	out.write(sb.toString().getBytes());
        
        return out;
    }
	
	protected void refreshTable(boolean showError, Collection<ErrorOccurrence> myItems)
	{
		errorOccurenceTable.removeAllItems();
		
		container = buildContainer();
		this.errorOccurenceTable.setContainerDataSource(container);

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
    	
    	if(modulesNames == null && flowNames == null && componentNames == null && businessStreamCombo != null
    			&& !((BusinessStream)businessStreamCombo.getValue()).getName().equals("All"))
    	{
    		BusinessStream businessStream = ((BusinessStream)businessStreamCombo.getValue());
    		
    		modulesNames = new ArrayList<String>();
    		
    		for(BusinessStreamFlow flow: businessStream.getFlows())
    		{
    			modulesNames.add(flow.getFlow().getModule().getName());
    		}
    	}
    	
		List<ErrorOccurrence> errorOccurences = errorReportingService
    			.find(modulesNames, flowNames, componentNames, errorFromDate.getValue(), errorToDate.getValue(),
    					platformConfigurationService.getSearchResultSetSize());
    	
    	if((errorOccurences == null || errorOccurences.size() == 0) && showError)
    	{
    		Notification.show("The error search returned no results!", Type.ERROR_MESSAGE);
    	}
    	
    	List<String> noteUris =  this.errorReportingManagementService.getAllErrorUrisWithNote();
    	
    	Long resultSize = errorReportingService.rowCount(modulesNames, flowNames, componentNames, errorFromDate.getValue(), errorToDate.getValue());
    	
    	searchResultsSizeLayout.removeAllComponents();
    	this.resultsLabel = new Label("Number of records returned: " + errorOccurences.size() + " of " + resultSize);
    	searchResultsSizeLayout.addComponent(this.resultsLabel);
    	
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
    	
    	for(final ErrorOccurrence errorOccurrence: errorOccurences)
    	{
    		Date date = new Date(errorOccurrence.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    Item item = container.addItem(errorOccurrence);			            	    
    	    
    	    item.getItemProperty("Module Name").setValue(errorOccurrence.getModuleName());
			item.getItemProperty("Flow Name").setValue(errorOccurrence.getFlowName());
			item.getItemProperty("Component Name").setValue(errorOccurrence.getFlowElementName());
			if(errorOccurrence.getErrorMessage() != null && errorOccurrence.getErrorMessage().length() > 1000)
			{
				item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorMessage().substring(0, 1000));
			}
			else
			{
				item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorMessage());
			}
			item.getItemProperty("Timestamp").setValue(timestamp);
						
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
			
			
			final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
		        	.getAttribute(DashboardSessionValueConstants.USER);
			
			if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || 
					authentication.hasGrantedAuthority(SecurityConstants.ACTION_ERRORS_AUTHORITY))
			{	
				CheckBox cb = new CheckBox();
			
				if(myItems != null && myItems.contains(errorOccurrence))
				{
					cb.setValue(true);
					item.getItemProperty("").setValue(cb);
				}
				else
				{
					cb.setValue(false);
					item.getItemProperty("").setValue(cb);
				}
			}
			
			Button popupButton = new Button();
			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			popupButton.setDescription("Open in new window");
			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			popupButton.setIcon(VaadinIcons.MODAL);

	        BrowserWindowOpener popupOpener = new BrowserWindowOpener(ErrorOccurrencePopup.class);
	        popupOpener.setFeatures("height=600,width=900,resizable");
	        popupOpener.extend(popupButton);
	        
	        popupButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportService", errorReportingService);
	    	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportManagementService", errorReportingManagementService);
	    	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("platformConfigurationService", platformConfigurationService);
	    	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorOccurrence", errorOccurrence);
	            }
	        });
	        
	        item.getItemProperty(" ").setValue(popupButton);   
    	}
    
	}

	protected void updateComments(Collection<ErrorOccurrence> myItems)
	{
    	List<String> noteUris =  this.errorReportingManagementService.getAllErrorUrisWithNote();
    	
		for(ErrorOccurrence eo: myItems)
		{
			Item item = container.getItem(eo);
		 
		 	HorizontalLayout layout = new HorizontalLayout();
    	    layout.setSpacing(true);
    	    
    	    Label label = new Label(VaadinIcons.COMMENT.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);
			
			if(noteUris.contains(eo.getUri()))
			{
				layout.addComponent(label);
			}
			
			label = new Label(VaadinIcons.LINK.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);
			
			item.getItemProperty("N/L").setValue(layout);
		}
	}
	
	protected void updateCancel(Collection<ErrorOccurrence> myItems)
	{    	
		for(ErrorOccurrence eo: myItems)
		{
			container.removeItem(eo);
		}
	}
	
	protected String buildErrorUrl(String baseUrl, ErrorOccurrence errorOccurrence)
	{
		StringBuffer dashboardUrl = new StringBuffer(baseUrl);
		
		dashboardUrl.append("/?errorUri=").append(errorOccurrence.getUri()).append("&ui=errorOccurrence");
		
		return dashboardUrl.toString();
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

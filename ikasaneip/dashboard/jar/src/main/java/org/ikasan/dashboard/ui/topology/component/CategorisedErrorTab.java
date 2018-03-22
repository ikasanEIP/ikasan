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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.CategorisedErrorOccurrencePopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.icons.AtlassianIcons;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.TextWindow;
import org.ikasan.dashboard.ui.topology.window.CategorisedErrorOccurrenceCloseWindow;
import org.ikasan.dashboard.ui.topology.window.CategorisedErrorOccurrenceCommentWindow;
import org.ikasan.dashboard.ui.topology.window.CategorisedErrorOccurrenceViewWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceCloseWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceCommentWindow;
import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.spec.hospital.service.HospitalManagementService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class CategorisedErrorTab extends TopologyTab
{
	private Logger logger = LoggerFactory.getLogger(CategorisedErrorTab.class);
	
	private FilterTable categorizedErrorOccurenceTable;
	
	private ComboBox errorCategoryCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private ErrorCategorisationService errorCategorisationService;
	
	private IndexedContainer container = null;
	
	private ErrorReportingManagementService errorReportingManagementService;
	
	private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
	
	private TopologyService topologyService;
	
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	
	private Label resultsLabel = new Label();
	
	private HorizontalLayout searchResultsSizeLayout = new HorizontalLayout();
	
	private PlatformConfigurationService platformConfigurationService;
	
	private ArrayList<String> modulesNames;
	private ArrayList<String> flowNames;
	private ArrayList<String> componentNames;
	
	
	private boolean showFilter = true;
	
	private Long resultSize = new Long(0);
	
	private Container.ItemSetChangeListener listener =  new Container.ItemSetChangeListener()
	{			
		@Override
		public void containerItemSetChange(ItemSetChangeEvent event)
		{				
			searchResultsSizeLayout.removeAllComponents();
	    	resultsLabel = new Label("Number of records returned: " + event.getContainer().size() + " of " + resultSize);
	    	searchResultsSizeLayout.addComponent(resultsLabel);
		}
	};
	
	public CategorisedErrorTab(ErrorCategorisationService errorCategorisationService,
			ErrorReportingManagementService errorReportingManagementService,
			HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService,
			TopologyService topologyService, ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			PlatformConfigurationService platformConfigurationService, boolean showFilter)
	{
		this.errorCategorisationService = errorCategorisationService;
		this.errorReportingManagementService = errorReportingManagementService;
		this.hospitalManagementService = hospitalManagementService;
		this.topologyService = topologyService;
		this.exclusionManagementService = exclusionManagementService;
		this.platformConfigurationService = platformConfigurationService;
		this.showFilter = showFilter;
		
		container = this.buildContainer();
	}
	
	protected IndexedContainer buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Error Location", Layout.class,  null);
		cont.addContainerProperty("Error Message", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("N/L", Layout.class,  null);
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
				|| authentication.hasGrantedAuthority(SecurityConstants.CATEGORISED_ERROR_WRITE)
				|| authentication.hasGrantedAuthority(SecurityConstants.CATEGORISED_ERROR_ADMIN)))
		{	
			cont.addContainerProperty("", CheckBox.class,  null);
		}
		
		cont.addContainerProperty(" ", Button.class,  null);

        return cont;
    }
	
	public void createLayout()
	{
		this.categorizedErrorOccurenceTable = new FilterTable();
		this.categorizedErrorOccurenceTable.setSizeFull();
		this.categorizedErrorOccurenceTable.setContainerDataSource(container);
		this.categorizedErrorOccurenceTable.setFilterBarVisible(true);
		this.categorizedErrorOccurenceTable.setColumnExpandRatio("Error Location", .25f);
		this.categorizedErrorOccurenceTable.setColumnExpandRatio("Error Message", .65f);
		this.categorizedErrorOccurenceTable.setColumnExpandRatio("Timestamp", .1f);
		this.categorizedErrorOccurenceTable.setColumnExpandRatio("N/L", .05f);
		
		this.categorizedErrorOccurenceTable.addStyleName("wordwrap-table");
		this.categorizedErrorOccurenceTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
		this.categorizedErrorOccurenceTable.addStyleName("ikasan");
		
		this.categorizedErrorOccurenceTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
			    	CategorisedErrorOccurrence errorOccurrence = (CategorisedErrorOccurrence)itemClickEvent.getItemId();
			    	
			    	CategorisedErrorOccurrenceViewWindow errorOccurrenceViewWindow 
			    		= new CategorisedErrorOccurrenceViewWindow(errorOccurrence, errorReportingManagementService,
			    				hospitalManagementService, topologyService, exclusionManagementService, container);
			    
			    	UI.getCurrent().addWindow(errorOccurrenceViewWindow);
		    	}
		    }
		});
		
		this.categorizedErrorOccurenceTable.setCellStyleGenerator(new CustomTable.CellStyleGenerator() 
		{
			@Override
			public String getStyle(CustomTable source, Object itemId, Object propertyId) 
			{
				
				CategorisedErrorOccurrence categorisedErrorOccurrence = (CategorisedErrorOccurrence)itemId;
				
				if (propertyId == null) {
				// Styling for row			
					
					if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
					{
						return "ikasan-green-small";
					}
					else if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.MAJOR))
					{
						return "ikasan-green-small";
					}
					else if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.CRITICAL))
					{
						return "ikasan-orange-small";
					}
					else if(categorisedErrorOccurrence.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.BLOCKER))
					{
						return "ikasan-red-small";
					}
				}
				
				if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
				{
					return "ikasan-green-small";
				}
				else if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.MAJOR))
				{
					return "ikasan-green-small";
				}
				else if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.CRITICAL))
				{
					return "ikasan-orange-small";
				}
				else if(categorisedErrorOccurrence.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.BLOCKER))
				{
					return "ikasan-red-small";
				}
				
				return "ikasan-small";
			}
		});
				
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	search();
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
		
		errorCategoryCombo = new ComboBox("Error Category");
		errorCategoryCombo.setNullSelectionAllowed(true); 
		errorCategoryCombo.addItem(ErrorCategorisation.TRIVIAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.TRIVIAL, VaadinIcons.ARROW_DOWN);
		errorCategoryCombo.addItem(ErrorCategorisation.MAJOR);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.MAJOR, VaadinIcons.ARROW_UP);
		errorCategoryCombo.addItem(ErrorCategorisation.CRITICAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.CRITICAL, VaadinIcons.EXCLAMATION_CIRCLE_O);
		errorCategoryCombo.addItem(ErrorCategorisation.BLOCKER);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.BLOCKER, VaadinIcons.BAN);
		
		GridLayout dateSelectLayout = new GridLayout(3, 1);
		dateSelectLayout.addComponent(errorCategoryCombo, 2, 0);
		dateSelectLayout.setSizeFull();
		errorFromDate = new PopupDateField("From date");
		errorFromDate.setResolution(Resolution.MINUTE);
		errorFromDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(errorFromDate, 0, 0);
		errorToDate = new PopupDateField("To date");
		errorToDate.setResolution(Resolution.MINUTE);
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
		
		if(this.showFilter)
		{
			hideFilterButton.setVisible(true);
        	showFilterButton.setVisible(false);
        	vSplitPanel.setSplitPosition(310, Unit.PIXELS);
		}
		else
		{
			hideFilterButton.setVisible(false);
        	showFilterButton.setVisible(true);
        	splitPosition = vSplitPanel.getSplitPosition();
        	splitUnit = vSplitPanel.getSplitPositionUnit();
        	vSplitPanel.setSplitPosition(0, Unit.PIXELS);
		}
		
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
		
		GridLayout buttons = new GridLayout(4, 1);
		buttons.setWidth("100px");
		
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
            	Collection<CategorisedErrorOccurrence> items = (Collection<CategorisedErrorOccurrence>)container.getItemIds();
            	
            	Resource r = selectAllButton.getIcon();
            	
            	if(r.equals(VaadinIcons.CHECK_SQUARE_O))
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE);
            		
            		for(CategorisedErrorOccurrence eo: items)
                	{
                		Item item = container.getItem(eo);
                		
                		CheckBox cb = (CheckBox)item.getItemProperty("").getValue();
                		
                		cb.setValue(true);
                	}
            	}
            	else
            	{
            		selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
            		
            		for(CategorisedErrorOccurrence eo: items)
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
		closeSelectedButton.setDescription("Close all selected errors below.");
		
		closeSelectedButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	            	
            	Collection<CategorisedErrorOccurrence> items = (Collection<CategorisedErrorOccurrence>)container.getItemIds();
            	
            	final Collection<CategorisedErrorOccurrence> myItems = new ArrayList<CategorisedErrorOccurrence>(items);
            	
            	for(CategorisedErrorOccurrence eo: items)
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
	            	final CategorisedErrorOccurrenceCloseWindow window = new CategorisedErrorOccurrenceCloseWindow(errorReportingManagementService, 
	            			myItems);
	            	
	            	window.addCloseListener(new Window.CloseListener() 
	            	{
	                    public void windowClose(CloseEvent e) 
	                    {
	                    	if(window.getAction().equals(ErrorOccurrenceCloseWindow.CLOSE))
	                    	{
	                    		updateCancel((Collection<CategorisedErrorOccurrence>) myItems);
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
		commentSelectedButton.setDescription("Comment on selected errors below.");
		
		commentSelectedButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	Collection<CategorisedErrorOccurrence> items = (Collection<CategorisedErrorOccurrence>)container.getItemIds();
            	
            	final Collection<CategorisedErrorOccurrence> myItems = new ArrayList<CategorisedErrorOccurrence>(items);
            	
            	for(CategorisedErrorOccurrence eo: items)
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
	            	final CategorisedErrorOccurrenceCommentWindow window = new CategorisedErrorOccurrenceCommentWindow(errorReportingManagementService, 
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
		    
		    	sb.append("||").append("Module Name");
		    	sb.append("||").append("Flow Name");
		    	sb.append("||").append("Component Name");
		    	sb.append("||").append("Error Message");
		    	sb.append("||").append("Timestamp");
		    	sb.append("||\n");
		    	
		    	
		    	for(Object errorOccurrence: container.getItemIds())
		    	{
		    		CategorisedErrorOccurrence categorisedErrorOccurrence = (CategorisedErrorOccurrence)errorOccurrence;
		    		
		    		sb.append("|").append(categorisedErrorOccurrence.getErrorOccurrence().getModuleName());
		    		sb.append("|").append(categorisedErrorOccurrence.getErrorOccurrence().getFlowName());
		    		sb.append("|").append(categorisedErrorOccurrence.getErrorOccurrence().getFlowElementName());
		    		sb.append("|").append("{code}").append(categorisedErrorOccurrence.getErrorCategorisation().getErrorDescription()
							+ " " + categorisedErrorOccurrence.getErrorOccurrence().getErrorMessage()).append("{code}");
		    		
		    		Date date = new Date(categorisedErrorOccurrence.getErrorOccurrence().getTimestamp());
		    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
		    	    String timestamp = format.format(date);
		    	    
		    	    sb.append("|").append(timestamp);
		    		
		    		sb.append("|\n");
		    	}
		    	
            	
            	TextWindow tw = new TextWindow("Jira Table", sb.toString());
                
                UI.getCurrent().addWindow(tw);
            }
        });
		
		buttons.addComponent(selectAllButton);
		buttons.addComponent(closeSelectedButton);
		buttons.addComponent(commentSelectedButton);
		buttons.addComponent(jiraButton);
		
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
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
				|| authentication.hasGrantedAuthority(SecurityConstants.CATEGORISED_ERROR_WRITE)
				|| authentication.hasGrantedAuthority(SecurityConstants.CATEGORISED_ERROR_ADMIN)))
		{
			selectAllButton.setVisible(true);
			closeSelectedButton.setVisible(true);
			commentSelectedButton.setVisible(true);
		}
		else
		{
			selectAllButton.setVisible(false);
			closeSelectedButton.setVisible(false);
			commentSelectedButton.setVisible(false);
		}
		
		gl.addComponent(hl);
		hErrorTable.addComponent(gl);

		VerticalSplitPanel vpanel = new VerticalSplitPanel(gl
				, this.categorizedErrorOccurenceTable);
		vpanel.setSplitPosition(30, Unit.PIXELS);
		vpanel.setLocked(true);

		vSplitPanel.setSecondComponent(vpanel);

		
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

	protected void updateComments(Collection<CategorisedErrorOccurrence> myItems)
	{
    	List<String> noteUris =  this.errorReportingManagementService.getAllErrorUrisWithNote();
    	
		for(CategorisedErrorOccurrence eo: myItems)
		{
			Item item = container.getItem(eo);
		 
		 	HorizontalLayout layout = new HorizontalLayout();
    	    layout.setSpacing(true);
    	    
    	    Label label = new Label(VaadinIcons.COMMENT.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);
			
			if(noteUris.contains(eo.getErrorOccurrence().getUri()))
			{
				layout.addComponent(label);
			}
			
			label = new Label(VaadinIcons.LINK.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);

			item.getItemProperty("N/L").setValue(layout);
		}
	}
	
	protected void updateCancel(Collection<CategorisedErrorOccurrence> myItems)
	{    	
		for(CategorisedErrorOccurrence eo: myItems)
		{
			container.removeItem(eo);
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.ui.topology.component.TopologyTab#search()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void search()
	{
		logger.debug("Start search!");
		categorizedErrorOccurenceTable.removeAllItems();

    	modulesNames = new ArrayList<String>();
    	
    	this.container.removeItemSetChangeListener(listener);
    	
    	if(modules.getItemIds().size() > 0)
    	{
        	for(Object module: modules.getItemIds())
        	{
        		modulesNames.add(((Module)module).getName());
        	}
    	}
    	
    	flowNames = new ArrayList<String>();
    	
    	if(flows.getItemIds().size() > 0)
    	{
    		for(Object flow: flows.getItemIds())
        	{
        		flowNames.add(((Flow)flow).getName());
        	}
    	}
    	
    	componentNames = new ArrayList<String>();
    	
    	if(components.getItemIds().size() > 0 
    			&& modules.getItemIds().size() == 0
    			&& flows.getItemIds().size() == 0)
    	{
        	for(Object component: components.getItemIds())
        	{
        		componentNames.add(((Component)component).getName());
        	}
    	}
    	
    	String errorCategory = null;
    	
    	if(errorCategoryCombo != null && errorCategoryCombo.getValue() != null)
    	{
    		errorCategory = (String)errorCategoryCombo.getValue();
    	}
 
    	List<CategorisedErrorOccurrence> categorisedErrorOccurrences = errorCategorisationService
    			.findCategorisedErrorOccurences(modulesNames, flowNames, componentNames, "", "", errorCategory,
    					errorFromDate.getValue(), errorToDate.getValue(), 1000);
    	
    	if(categorisedErrorOccurrences == null || categorisedErrorOccurrences.size() == 0)
    	{
    		Notification.show("The categorised error search returned no results!", Type.ERROR_MESSAGE);
    		
    		searchResultsSizeLayout.removeAllComponents();
        	this.resultsLabel = new Label("Number of records returned: 0 of 0");
        	searchResultsSizeLayout.addComponent(this.resultsLabel);
    		
    		return;
    	}
    	
    	resultSize = errorCategorisationService.rowCount(modulesNames, flowNames, componentNames, errorFromDate.getValue(), errorToDate.getValue());
    	
    	searchResultsSizeLayout.removeAllComponents();
    	this.resultsLabel = new Label("Number of records returned: " + categorisedErrorOccurrences.size() + " of " + resultSize);
    	searchResultsSizeLayout.addComponent(this.resultsLabel);
    	
    	List<String> noteUris =  errorReportingManagementService.getAllErrorUrisWithNote();

    	for(final CategorisedErrorOccurrence categorisedErrorOccurrence: categorisedErrorOccurrences)
    	{
    		ErrorOccurrence errorOccurrence = categorisedErrorOccurrence.getErrorOccurrence();
    		
    		Date date = new Date(errorOccurrence.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    VerticalLayout layout = new VerticalLayout();
    	    layout.addComponent(new Label(VaadinIcons.ARCHIVE.getHtml() + " " +  errorOccurrence.getModuleName(), ContentMode.HTML));
    	    layout.addComponent(new Label(VaadinIcons.AUTOMATION.getHtml() + " " +  errorOccurrence.getFlowName(), ContentMode.HTML));
    	    layout.addComponent(new Label(VaadinIcons.COG.getHtml() + " " +  errorOccurrence.getFlowElementName(), ContentMode.HTML));
    	    layout.setSpacing(true);
    	                	    
    	    Item item = container.addItem(categorisedErrorOccurrence);			            	    

    	    item.getItemProperty("Error Location").setValue(layout);
    	    
    	    String errorMessage = categorisedErrorOccurrence.getErrorCategorisation().getErrorDescription()
					+ " " + categorisedErrorOccurrence.getErrorOccurrence().getErrorMessage();
    	    
    	   if(errorMessage.length() > 500)
			{
				item.getItemProperty("Error Message").setValue(errorMessage.substring(0, 500));
			}
			else
			{
				item.getItemProperty("Error Message").setValue(errorMessage);
			}
			
			item.getItemProperty("Timestamp").setValue(timestamp);
			
			HorizontalLayout commentLayout = new HorizontalLayout();
			commentLayout.setSpacing(true);
    	    
    	    Label label = new Label(VaadinIcons.COMMENT.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);
			
			if(noteUris.contains(errorOccurrence.getUri()))
			{
				commentLayout.addComponent(label);
			}
			
			label = new Label(VaadinIcons.LINK.getHtml(), ContentMode.HTML);			
			label.addStyleName(ValoTheme.LABEL_TINY);
			
			
			item.getItemProperty("N/L").setValue(commentLayout);
			
			final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
		        	.getAttribute(DashboardSessionValueConstants.USER);
			
			if(authentication != null && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
					|| authentication.hasGrantedAuthority(SecurityConstants.CATEGORISED_ERROR_WRITE)
					|| authentication.hasGrantedAuthority(SecurityConstants.CATEGORISED_ERROR_ADMIN)))
			{	
				CheckBox cb = new CheckBox();
				cb.setValue(false);
				item.getItemProperty("").setValue(cb);
			}
			
			Button popupButton = new Button();
			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			popupButton.setDescription("Open in new window");
			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			popupButton.setIcon(VaadinIcons.MODAL);
			
			BrowserWindowOpener popupOpener = new BrowserWindowOpener(CategorisedErrorOccurrencePopup.class);
			popupOpener.setFeatures("height=600,width=900,resizable");
	        popupOpener.extend(popupButton);
	        
	        popupButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("categorisedErrorOccurrence", categorisedErrorOccurrence);

	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportManagementService", errorReportingManagementService);
	            	
	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("hospitalManagementService", hospitalManagementService);
	            	
	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("topologyService", topologyService);
	            	
	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("exclusionManagementService", exclusionManagementService);
	            	
	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("container", container);
	            }
	        });
	    	
	        item.getItemProperty(" ").setValue(popupButton);
    	}
    	
    	this.container.addItemSetChangeListener(listener);    	 
    	
    	logger.info("End search!");
    }	
}

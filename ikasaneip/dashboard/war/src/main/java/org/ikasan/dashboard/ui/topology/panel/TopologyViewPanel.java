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
package org.ikasan.dashboard.ui.topology.panel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.IkasanUI;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.event.FlowStateEvent;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.PolicyLinkTypeConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.monitor.component.MonitorIcons;
import org.ikasan.dashboard.ui.topology.component.ActionedErrorOccurrenceTab;
import org.ikasan.dashboard.ui.topology.component.ActionedExclusionTab;
import org.ikasan.dashboard.ui.topology.component.BusinessStreamTab;
import org.ikasan.dashboard.ui.topology.component.CategorisedErrorTab;
import org.ikasan.dashboard.ui.topology.component.ErrorOccurrenceTab;
import org.ikasan.dashboard.ui.topology.component.ExclusionsTab;
import org.ikasan.dashboard.ui.topology.component.FilterManagementTab;
import org.ikasan.dashboard.ui.topology.component.TopologyTab;
import org.ikasan.dashboard.ui.topology.component.WiretapTab;
import org.ikasan.dashboard.ui.topology.util.FilterMap;
import org.ikasan.dashboard.ui.topology.util.FilterUtil;
import org.ikasan.dashboard.ui.topology.window.ComponentConfigurationWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorCategorisationWindow;
import org.ikasan.dashboard.ui.topology.window.ServerWindow;
import org.ikasan.dashboard.ui.topology.window.StartupControlConfigurationWindow;
import org.ikasan.dashboard.ui.topology.window.WiretapConfigurationWindow;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.module.StartupControlService;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.ikasan.wiretap.dao.WiretapDao;
import org.ikasan.wiretap.service.TriggerManagementService;
import org.vaadin.teemu.VaadinIcons;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ikasan.topology.exception.DiscoveryException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ItemStyleGenerator;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class TopologyViewPanel extends Panel implements View, Action.Handler
{
	/** running state string constant */
    private static String RUNNING = "running";
    
    /** stopped state string constant */
    private static String STOPPED = "stopped";
    
    /** recovering state string constant */
    private static String RECOVERING = "recovering";
    
    /** stoppedInError state string constant */
    private static String STOPPED_IN_ERROR = "stoppedInError";
    
    /** paused state string constant */
    private static String PAUSED = "paused";
    
    public static final String BUSINESS_STREAM_TAB = "businessStream";
    public static final String WIRETAP_TAB = "wiretap";
    public static final String ERROR_OCCURRENCE_TAB = "errorOccurrence";
    public static final String ACTIONED_ERROR_TAB = "actionErrorOccurrence";
    public static final String EVENT_EXCLUSION_TAB = "eventExclusion";
    public static final String ACTIONED_EVENT_EXCLUSION_TAB = "actionedEventExclusion";
    public static final String CATEGORISED_ERROR_TAB = "categorisedError";
    
	/**
	 * 
	 */
	private  final long serialVersionUID = -6213301218439409056L;
	
	private Logger logger = Logger.getLogger(TopologyViewPanel.class);
	
	private final Action START = new Action("Start");
    private final Action STOP = new Action("Stop");
    private final Action VIEW_DIAGRAM = new Action("View Diagram");
    private final Action CONFIGURE = new Action("Configure");
    private final Action PAUSE = new Action("Pause");
    private final Action START_PAUSE = new Action("Start/Pause");
    private final Action RESUME = new Action("Resume");
    private final Action RESTART = new Action("Re-start");
    private final Action DISABLE = new Action("Disable");
    private final Action DETAILS = new Action("Details");
    private final Action WIRETAP = new Action("Wiretap");
    private final Action ERROR_CATEGORISATION = new Action("Categorise Error");
    private final Action EDIT = new Action("Edit");
    private final Action STARTUP_CONTROL = new Action("Startup Type");
    private final Action[] serverActions = new Action[] { DETAILS, ERROR_CATEGORISATION, EDIT };
    private final Action[] moduleActions = new Action[] { DETAILS, VIEW_DIAGRAM, ERROR_CATEGORISATION };
    private final Action[] flowActionsStopped = new Action[] { START, START_PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION };
    private final Action[] flowActionsStarted = new Action[] { STOP, PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION };
    private final Action[] flowActionsPaused = new Action[] { STOP, RESUME, STARTUP_CONTROL, ERROR_CATEGORISATION };
    private final Action[] flowActions = new Action[] { ERROR_CATEGORISATION };
    private final Action[] componentActionsConfigurable = new Action[] { CONFIGURE, WIRETAP, ERROR_CATEGORISATION };
    private final Action[] componentActions = new Action[] { WIRETAP, ERROR_CATEGORISATION };
    private final Action[] actionsEmpty = new Action[]{};

	private Panel topologyTreePanel;
	private Tree moduleTree;
	private ComponentConfigurationWindow componentConfigurationWindow;

	private Panel tabsheetPanel;

	private Table systemEventTable;
	
	private ComboBox businessStreamCombo;
	private ComboBox treeViewBusinessStreamCombo;
	
	private WiretapDao wiretapDao;

	private PopupDateField systemEventFromDate;
	private PopupDateField systemEventToDate;
	
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
	private TopologyService topologyService;
	
	private StartupControlService startupControlService;
	private ErrorReportingService errorReportingService;
	private ErrorReportingManagementService errorReportingManagementService;
	
	private BusinessStream businessStream;
	
	private SystemEventService systemEventService;
	private ErrorCategorisationService errorCategorisationService;
	private TriggerManagementService triggerManagementService;
	private SecurityService securityService;
	
	private ConcurrentHashMap<String, String> flowStates = new ConcurrentHashMap<String, String>();
	
	private TopologyStateCache topologyCache;
	
	private TabSheet tabsheet;
	
	private PlatformConfigurationService platformConfigurationService;
	
	private VerticalLayout popupViewLayout = new VerticalLayout();
	private PopupView filtersPopup = new PopupView("Filters", popupViewLayout);
	
	private TopologyTab currentTab;
	
	private HashMap<String, AbstractComponent> tabComponentMap = new HashMap<String, AbstractComponent>();
	
	
	
	public TopologyViewPanel(TopologyService topologyService, ComponentConfigurationWindow componentConfigurationWindow,
			 WiretapDao wiretapDao, ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			 HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService, SystemEventService systemEventService,
			 ErrorCategorisationService errorCategorisationService, TriggerManagementService triggerManagementService, TopologyStateCache topologyCache,
			 StartupControlService startupControlService, ErrorReportingService errorReportingService, ErrorReportingManagementService errorReportingManagementService,
			 PlatformConfigurationService platformConfigurationService, SecurityService securityService)
	{
		this.topologyService = topologyService;
		if(this.topologyService == null)
		{
			throw new IllegalArgumentException("topologyService cannot be null!");
		}
		this.componentConfigurationWindow = componentConfigurationWindow;
		if(this.componentConfigurationWindow == null)
		{
			throw new IllegalArgumentException("componentConfigurationWindow cannot be null!");
		}
		this.wiretapDao = wiretapDao;
		if(this.wiretapDao == null)
		{
			throw new IllegalArgumentException("wiretapDao cannot be null!");
		}
		this.exclusionManagementService = exclusionManagementService;
		if(this.exclusionManagementService == null)
		{
			throw new IllegalArgumentException("exclusionManagementService cannot be null!");
		}
		this.hospitalManagementService = hospitalManagementService;
		if(this.hospitalManagementService == null)
		{
			throw new IllegalArgumentException("hospitalManagementService cannot be null!");
		}
		this.systemEventService = systemEventService;
		if(this.systemEventService == null)
		{
			throw new IllegalArgumentException("systemEventService cannot be null!");
		}
		this.errorCategorisationService = errorCategorisationService;
		if(this.errorCategorisationService == null)
		{
			throw new IllegalArgumentException("errorCategorisationService cannot be null!");
		}
		this.triggerManagementService = triggerManagementService;
		if(this.triggerManagementService == null)
		{
			throw new IllegalArgumentException("triggerManagementService cannot be null!");
		}
		this.topologyCache = topologyCache;
		if(this.topologyCache == null)
		{
			throw new IllegalArgumentException("topologyCache cannot be null!");
		}
		this.startupControlService = startupControlService;
		if(this.startupControlService == null)
		{
			throw new IllegalArgumentException("startupControlService cannot be null!");
		}
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
		this.securityService = securityService;
		if(this.securityService == null)
		{
			throw new IllegalArgumentException("securityService cannot be null!");
		}
		
		init();
	}

	protected void init()
	{
		this.tabsheetPanel = new Panel();
		this.tabsheetPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		this.tabsheetPanel.setSizeFull();
		
		this.createModuleTreePanel();
		
		this.setWidth("100%");
		this.setHeight("100%");
		
		this.businessStreamCombo = new ComboBox();

		HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
		hsplit.setStyleName(ValoTheme.SPLITPANEL_LARGE);

		HorizontalLayout leftLayout = new HorizontalLayout();
		leftLayout.setSizeFull();
		leftLayout.setMargin(true);
		leftLayout.addComponent(this.topologyTreePanel);
		hsplit.setFirstComponent(leftLayout);
		HorizontalLayout rightLayout = new HorizontalLayout();
		rightLayout.setSizeFull();
		rightLayout.setMargin(true);
		rightLayout.addComponent(this.tabsheetPanel);
		hsplit.setSecondComponent(rightLayout);
		hsplit.setSplitPosition(30, Unit.PERCENTAGE);

		this.flowStates = this.topologyCache.getStateMap();
		
		this.setContent(hsplit);
	}
	
	protected void createTabSheet()
	{			
		tabsheet = new TabSheet();
		tabsheet.setSizeFull();

		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
	    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_BUSINESS_STREAM_AUTHORITY)))
    	{			
			BusinessStreamTab businessStreamTab = new BusinessStreamTab(this.topologyService
					, this.businessStreamCombo);
			
			businessStreamTab.createLayout();
			
			tabsheet.addTab(businessStreamTab, "Business Stream");
			
			tabComponentMap.put(BUSINESS_STREAM_TAB, businessStreamTab);
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_WIRETAP_AUTHORITY)))
    	{
       		WiretapTab wiretapTab = new WiretapTab
					(this.wiretapDao, this.treeViewBusinessStreamCombo, this.platformConfigurationService);
       		wiretapTab.createLayout();
			
    		tabsheet.addTab(wiretapTab, "Wiretaps");
    		
    		tabComponentMap.put(WIRETAP_TAB, wiretapTab);
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_ERRORS_AUTHORITY)))
    	{    		
    		ErrorOccurrenceTab errorOccurrenceTab = new ErrorOccurrenceTab
					(this.errorReportingService, this.treeViewBusinessStreamCombo
							, this.errorReportingManagementService, this.platformConfigurationService);
    		errorOccurrenceTab.createLayout();
			
    		tabsheet.addTab(errorOccurrenceTab, "Errors");
    	
    		tabComponentMap.put(ERROR_OCCURRENCE_TAB, errorOccurrenceTab);
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_ERRORS_AUTHORITY)))
    	{    		
    		ActionedErrorOccurrenceTab actionedErrorOccurrenceTab = new ActionedErrorOccurrenceTab
					(this.errorReportingService, this.treeViewBusinessStreamCombo, this.errorReportingManagementService);
			
    		actionedErrorOccurrenceTab.createLayout();
    		
    		tabsheet.addTab(actionedErrorOccurrenceTab, "Actioned Errors");
    		
    		tabComponentMap.put(ACTIONED_ERROR_TAB, actionedErrorOccurrenceTab);
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_EXCLUSION_AUTHORITY)))
    	{
    		final ExclusionsTab exclusionsTab = new ExclusionsTab(this.errorReportingService, 
    				this.exclusionManagementService, this.hospitalManagementService, this.topologyService, 
    				this.treeViewBusinessStreamCombo);
    		
    		exclusionsTab.createLayout();
    		
    		tabsheet.addTab(exclusionsTab, "Exclusions");
    		
    		tabComponentMap.put(EVENT_EXCLUSION_TAB, exclusionsTab);
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_ACTIONED_EXCLUSIONS_AUTHORITY)))
    	{		
			ActionedExclusionTab actionedExclusionTab = new ActionedExclusionTab
					(this.exclusionManagementService, this.hospitalManagementService,
							this.errorReportingService, this.topologyService, this.treeViewBusinessStreamCombo);
			
			actionedExclusionTab.createLayout();
			
			tabsheet.addTab(actionedExclusionTab, "Actioned Exclusions");
			
			tabComponentMap.put(ACTIONED_EVENT_EXCLUSION_TAB, actionedExclusionTab);
    	}
		
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_ACTIONED_EXCLUSIONS_AUTHORITY)))
    	{		
    		final VerticalLayout tab6 = new VerticalLayout();
    		tab6.setSizeFull();
    		tab6.addComponent(this.createSystemEventPanel());
    		tabsheet.addTab(tab6, "System Events");
    	}
				
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_CATEGORISED_ERRORS_AUTHORITY)))
    	{
			CategorisedErrorTab categorisedErrorTab = new CategorisedErrorTab
					(this.errorCategorisationService, this.errorReportingManagementService,
							this.hospitalManagementService, this.topologyService, this.exclusionManagementService,
							this.platformConfigurationService, true);
			
			categorisedErrorTab.createLayout();
			categorisedErrorTab.resetSearchDates();
			
			tabsheet.addTab(categorisedErrorTab, "Categorised Errors");
			
			tabComponentMap.put(CATEGORISED_ERROR_TAB, categorisedErrorTab);
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.MANAGE_FILTERS_AUTHORITY)))
    	{
    		
    		final FilterManagementTab filterManagementTab = new FilterManagementTab
					(this.topologyService, this.securityService);

    		filterManagementTab.createLayout();
			
    		tabsheet.addTab(filterManagementTab, "Filters");
    		
    	}
    	
    	tabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener() 
    	{
		    @Override
		    public void selectedTabChange(SelectedTabChangeEvent event) 
		    {
		    	if(event.getTabSheet().getSelectedTab() instanceof TopologyTab)
		    	{
		    		currentTab = (TopologyTab)event.getTabSheet().getSelectedTab();
		    		currentTab.applyFilter();
		    	}
		    	
		        if(event.getTabSheet().getSelectedTab() instanceof FilterManagementTab)
		        {
		        	((FilterManagementTab)event.getTabSheet().getSelectedTab()).refresh();
		        }
		    }
		});

		this.tabsheetPanel.setContent(tabsheet);
	}

	protected void createModuleTreePanel()
	{
		this.topologyTreePanel = new Panel();
		this.topologyTreePanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		this.topologyTreePanel.setSizeFull();

		this.moduleTree = new Tree();
		this.moduleTree.setImmediate(true);
		this.moduleTree.setSizeFull();
		this.moduleTree.addActionHandler(this);
		this.moduleTree.setDragMode(TreeDragMode.NODE);
		this.moduleTree.setItemStyleGenerator(new ItemStyleGenerator() 
		{
			@Override
			public String getStyle(Tree source, Object itemId)
			{
				if(itemId instanceof Flow)
				{
					Flow flow = (Flow)itemId;
					
					String state = flowStates.get(flow.getModule().getName() + "-" + flow.getName());
	            	
					logger.debug(flow.getModule().getName() + "-" + flow.getName() + " State = " + state);
					
	    			if(state != null && state.equals(RUNNING))
	    			{
	    				return "greenicon";
	    			}
	    			else if(state != null && state.equals(RECOVERING))
	    			{
	    				return "orangeicon";
	    			}
	    			else if (state != null && state.equals(STOPPED))
	    			{
	    				return "redicon";
	    			}
	    			else if (state != null && state.equals(STOPPED_IN_ERROR))
	    			{
	    				return "redicon";
	    			}
	    			else if (state != null && state.equals(PAUSED))
	    			{
	    				return "indigoicon";
	    			}
				}				
				
				return "";
			}
		});
		
		GridLayout layout = new GridLayout(1, 4);
		layout.setSpacing(true);
		layout.setWidth("100%");
		
		Label roleManagementLabel = new Label("Topology");
 		roleManagementLabel.setStyleName(ValoTheme.LABEL_HUGE);
 		layout.addComponent(roleManagementLabel, 0, 0);
		
 		filtersPopup.addPopupVisibilityListener(new PopupView.PopupVisibilityListener()
		{		
			@Override
			public void popupVisibilityChange(PopupVisibilityEvent event)
			{
				if (!event.isPopupVisible()) 
				{
		           	if(currentTab != null)
		           	{
		           		currentTab.applyFilter();
		           	}
		        }
			}
		});
 		
		layout.addComponent(filtersPopup);
		
		this.treeViewBusinessStreamCombo = new ComboBox("Business Stream");
				
		this.treeViewBusinessStreamCombo.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) 
            {
                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                	businessStream  = (BusinessStream)event.getProperty().getValue();
                	
                	logger.debug("Value changed to business stream: " + businessStream.getName());
                
                	moduleTree.removeAllItems();
                	
                	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
            	        	.getAttribute(DashboardSessionValueConstants.USER);
            		
            		if(authentication != null 
                			&& ((authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) || authentication.hasGrantedAuthority(SecurityConstants.VIEW_TOPOLOGY_AUTHORITY))
                			&& businessStream.getName().equals("All")))
                	{
                		List<Server> servers = TopologyViewPanel.this.topologyService.getAllServers();
                		
                		for(Server server: servers)
                		{
                			Set<Module> modules = server.getModules();
                			
                			TopologyViewPanel.this.moduleTree.addItem(server);
                			TopologyViewPanel.this.moduleTree.setItemCaption(server, server.getName());
                			TopologyViewPanel.this.moduleTree.setChildrenAllowed(server, true);
                			TopologyViewPanel.this.moduleTree.setItemIcon(server, VaadinIcons.SERVER);

                	        for(Module module: modules)
                	        {
                	        	TopologyViewPanel.this.moduleTree.addItem(module);
                	        	TopologyViewPanel.this.moduleTree.setItemCaption(module, module.getName());
                	        	TopologyViewPanel.this.moduleTree.setParent(module, server);
                	        	TopologyViewPanel.this.moduleTree.setChildrenAllowed(module, true);
                	        	TopologyViewPanel.this.moduleTree.setItemIcon(module, VaadinIcons.ARCHIVE);
                	            
                	            Set<Flow> flows = module.getFlows();
                	
                	            for(Flow flow: flows)
                	            {
                	            	TopologyViewPanel.this.moduleTree.addItem(flow);
                	            	TopologyViewPanel.this.moduleTree.setItemCaption(flow, flow.getName());
                	            	TopologyViewPanel.this.moduleTree.setParent(flow, module);
                	            	TopologyViewPanel.this.moduleTree.setChildrenAllowed(flow, true);
                	                            	            	                	            	
                	            	TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
                	                
                	                Set<Component> components = flow.getComponents();
                	
                	                for(Component component: components)
                	                {
                	                	TopologyViewPanel.this.moduleTree.addItem(component);
                	                	TopologyViewPanel.this.moduleTree.setParent(component, flow);
                	                	TopologyViewPanel.this.moduleTree.setItemCaption(component, component.getName());
                	                	TopologyViewPanel.this.moduleTree.setChildrenAllowed(component, false);
                	                	
                	                	if(component.isConfigurable())
                	                	{
                	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG);
                	                	}
                	                	else
                	                	{
                	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG_O);
                	                	}
                	                }
                	            }
                	        }
                		}
                	}
                	else if(authentication != null 
                			&& !authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
                			&& businessStream.getName().equals("All"))
                	{
                		List<BusinessStream> businessStreams = topologyService.getAllBusinessStreams();
            			
            			for(BusinessStream businessStream: businessStreams)
            			{
            				if(authentication.canAccessLinkedItem(PolicyLinkTypeConstants.BUSINESS_STREAM_LINK_TYPE, businessStream.getId()))
            				{
            					for(BusinessStreamFlow bsFlow: businessStream.getFlows())
            		        	{
            		        		Server server = bsFlow.getFlow().getModule().getServer();
            		        		Module module = bsFlow.getFlow().getModule();
            		        		Flow flow = bsFlow.getFlow();
            		        		
            		        		if(!moduleTree.containsId(server))
            		        		{
            		            		moduleTree.addItem(server);
            		                    moduleTree.setItemCaption(server, server.getName());
            		                    moduleTree.setChildrenAllowed(server, true);
            		                    moduleTree.setItemIcon(server, VaadinIcons.SERVER);
            		        		}
            		                
            		                moduleTree.addItem(module);
            		                moduleTree.setItemCaption(module, module.getName());
            		                moduleTree.setParent(module, server);
            		                moduleTree.setChildrenAllowed(module, true);
            		                moduleTree.setItemIcon(module, VaadinIcons.ARCHIVE);
            		                
            		                moduleTree.addItem(flow);
            		                moduleTree.setItemCaption(flow, flow.getName());
            		                moduleTree.setParent(flow, module);
            		                moduleTree.setChildrenAllowed(flow, true);
            		                
            		                TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
            		                
            		                Set<Component> components = flow.getComponents();
            		
            		                for(Component component: components)
            		                {
            		                	moduleTree.addItem(component);
            		                	moduleTree.setParent(component, flow);
            		                	moduleTree.setItemCaption(component, component.getName());
            		                	moduleTree.setChildrenAllowed(component, false);
            		                	
            		                	if(component.isConfigurable())
                	                	{
                	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG);
                	                	}
                	                	else
                	                	{
                	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG_O);
                	                	}
            		                }
            		        	}
            	        	}
            			}
                	}
                	else
                	{
	                	for(BusinessStreamFlow bsFlow: businessStream.getFlows())
	                	{
	                		Server server = bsFlow.getFlow().getModule().getServer();
	                		Module module = bsFlow.getFlow().getModule();
	                		Flow flow = bsFlow.getFlow();
	                		
	                		if(!moduleTree.containsId(server))
	                		{
		                		moduleTree.addItem(server);
		                        moduleTree.setItemCaption(server, server.getName());
		                        moduleTree.setChildrenAllowed(server, true);
		                        moduleTree.setItemIcon(server, VaadinIcons.SERVER);
	                		}
	                        
	                        moduleTree.addItem(module);
	    	                moduleTree.setItemCaption(module, module.getName());
	                        moduleTree.setParent(module, server);
	    	                moduleTree.setChildrenAllowed(module, true);
	    	                moduleTree.setItemIcon(module, VaadinIcons.ARCHIVE);
	                        
	                        moduleTree.addItem(flow);
	    	                moduleTree.setItemCaption(flow, flow.getName());
	                        moduleTree.setParent(flow, module);
	    	                moduleTree.setChildrenAllowed(flow, true);
	    	                
	    	                TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
	    	                
	    	                Set<Component> components = flow.getComponents();
	    	
	    	                for(Component component: components)
	    	                {
	    	                	moduleTree.addItem(component);
	    	                	moduleTree.setParent(component, flow);
	    	                	moduleTree.setItemCaption(component, component.getName());
	    	                	moduleTree.setChildrenAllowed(component, false);
	    	                	
	    	                	if(component.isConfigurable())
        	                	{
        	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG);
        	                	}
        	                	else
        	                	{
        	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG_O);
        	                	}
	    	                }
	                	}
                	}        	
                }
                
                for (Iterator<?> it = moduleTree.rootItemIds().iterator(); it.hasNext();) 
        		{
        			moduleTree.expandItemsRecursively(it.next());
        		}
        		
        		for (Iterator<?> it = moduleTree.getItemIds().iterator(); it.hasNext();) 
        		{
        			Object nextItem = it.next();
        			if(nextItem instanceof Module)
        			{
        				moduleTree.collapseItemsRecursively(nextItem);
        			}
        		}
        		
            }
        });

		this.treeViewBusinessStreamCombo.setWidth("250px");
		layout.addComponent(this.treeViewBusinessStreamCombo);
		
		Button discoverButton = new Button("Discover");
		discoverButton.setStyleName(ValoTheme.BUTTON_SMALL);
		
		discoverButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
			        	.getAttribute(DashboardSessionValueConstants.USER);
            	
            	try
				{
					topologyService.discover(authentication);
				}
            	catch (DiscoveryException e)
				{
            		logger.error("An error occurred trying to auto discover modules!", e); 
            		
					Notification.show("An error occurred trying to auto discover modules: " 
							+ e.getMessage(), Type.ERROR_MESSAGE);
				}
            	
            	Notification.show("Auto discovery complete!");
            }
        });
		
		Button refreshButton = new Button("Refresh");
		refreshButton.setStyleName(ValoTheme.BUTTON_SMALL);
		refreshButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
				refreshTree();
            }
        });
		
		Button newServerButton = new Button("New Server");
		newServerButton.setStyleName(ValoTheme.BUTTON_SMALL);
		newServerButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
				UI.getCurrent().addWindow(new ServerWindow(topologyService));
            }
        });
		
		GridLayout buttonLayout = new GridLayout(3, 1);
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(discoverButton);
		buttonLayout.addComponent(refreshButton);
		buttonLayout.addComponent(newServerButton);
		
		layout.addComponent(buttonLayout);
		layout.addComponent(this.moduleTree);

		this.topologyTreePanel.setContent(layout);
	}
	
	protected void createFilterPopupContent()
	{
		FilterMap filterMap = (FilterMap)VaadinService.getCurrentRequest().getWrappedSession()
    		.getAttribute(DashboardSessionValueConstants.FILTERS);
		
		if(filterMap.getFilters().size() == 0)
		{
			popupViewLayout = new VerticalLayout();
			popupViewLayout.setMargin(true);
			popupViewLayout.setSpacing(true);
			popupViewLayout.setWidth("100%");
			
			Label manageFilters = new Label("Manage Filters");
			manageFilters.addStyleName(ValoTheme.LABEL_BOLD);
			manageFilters.addStyleName(ValoTheme.LABEL_LARGE);
			popupViewLayout.addComponent(manageFilters);

			Label filterName = new Label("No filters available!");
			
			popupViewLayout.addComponent(filterName);
		}
		else
		{
			popupViewLayout = new VerticalLayout();
			popupViewLayout.setMargin(true);
			popupViewLayout.setSpacing(true);
			popupViewLayout.setWidth("100%");
			
			Label manageFilters = new Label("Manage Filters");
			manageFilters.addStyleName(ValoTheme.LABEL_BOLD);
			manageFilters.addStyleName(ValoTheme.LABEL_LARGE);
			popupViewLayout.addComponent(manageFilters);
			
			int i=1;
			
			for(FilterUtil filterUtil: filterMap.getFilters())
			{
				BeanItem<FilterUtil> filterItem = new BeanItem<FilterUtil>(filterUtil);
				
				CheckBox filterSelected = new CheckBox(filterUtil.getFilter().getName());
				filterSelected.setPropertyDataSource(filterItem.getItemProperty("selected"));
				
				popupViewLayout.addComponent(filterSelected);
				
				i++;
			}
		}
		
		this.filtersPopup.setImmediate(true);
		this.filtersPopup.setContent(new Content()
		{
			
			@Override
			public com.vaadin.ui.Component getPopupComponent()
			{
				Panel popupPanel = new Panel();
				popupPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
				popupPanel.setHeight("300px");
				popupPanel.setWidth("200px");
				
				popupViewLayout.setImmediate(true);
				popupPanel.setContent(popupViewLayout);
				popupPanel.setImmediate(true);
				
				return popupPanel;
			}
			
			@Override
			public String getMinimizedValueAsHTML()
			{
				return "Filters";
			}
		});
		
		this.filtersPopup.markAsDirty();
	}

	
	protected Layout createSystemEventPanel()
	{
		this.systemEventTable = new Table();
		this.systemEventTable.setSizeFull();
		this.systemEventTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.systemEventTable.addContainerProperty("Subject", String.class,  null);
		this.systemEventTable.setColumnExpandRatio("Subject", .3f);
		this.systemEventTable.addContainerProperty("Action", String.class,  null);
		this.systemEventTable.setColumnExpandRatio("Action", .4f);
		this.systemEventTable.addContainerProperty("Actioned By", String.class,  null);
		this.systemEventTable.setColumnExpandRatio("Actioned By", .15f);
		this.systemEventTable.addContainerProperty("Timestamp", String.class,  null);
		this.systemEventTable.setColumnExpandRatio("Timestamp", .15f);
		
		this.systemEventTable.setStyleName("wordwrap-table");
		
		this.systemEventTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	// Not implemented at the moment but left as a place holder.
		    }
		});
		
		
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	systemEventTable.removeAllItems();
            	
            	PagedSearchResult<SystemEvent> systemEvents = systemEventService.listSystemEvents(0, 10000, "timestamp", true, null, null, systemEventFromDate.getValue()
            			, systemEventToDate.getValue(), null);
            	
            	if(systemEvents.getPagedResults() == null || systemEvents.getPagedResults().size() == 0)
            	{
            		Notification.show("The system events search returned no results!", Type.ERROR_MESSAGE);
            	}
            	
            	for(SystemEvent systemEvent: systemEvents.getPagedResults())
            	{
            		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
            	    String timestamp = format.format(systemEvent.getTimestamp());
            	    
            		systemEventTable.addItem(new Object[]{systemEvent.getSubject(), systemEvent.getAction()
        				, systemEvent.getActor(), timestamp}, systemEvent);
            	}
            }
        });
		

		GridLayout layout = new GridLayout(1, 2);			
		
		GridLayout dateSelectLayout = new GridLayout(2, 2);
		dateSelectLayout.setColumnExpandRatio(0, 0.25f);
		dateSelectLayout.setSpacing(true);
		dateSelectLayout.setWidth("50%");
		this.systemEventFromDate = new PopupDateField("From date");
		this.systemEventFromDate.setResolution(Resolution.MINUTE);
		this.systemEventFromDate.setValue(this.getMidnightToday());
		this.systemEventFromDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(this.systemEventFromDate, 0, 0);
		this.systemEventToDate = new PopupDateField("To date");
		this.systemEventToDate.setResolution(Resolution.MINUTE);
		this.systemEventToDate.setValue(this.getTwentyThreeFixtyNineToday());
		this.systemEventToDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(this.systemEventToDate, 1, 0);
		
		dateSelectLayout.addComponent(searchButton, 0, 1, 1, 1);

		HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(75 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(dateSelectLayout);
		layout.addComponent(hSearchLayout);
		HorizontalLayout hErrorTable = new HorizontalLayout();
		hErrorTable.setWidth("100%");
		hErrorTable.setHeight(600, Unit.PIXELS);
		hErrorTable.addComponent(this.systemEventTable);
		layout.addComponent(hErrorTable);
		layout.setSizeFull();
		
		return layout;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		EventBus eventBus = ((IkasanUI)UI.getCurrent()).getEventBus();   
    	eventBus.register(this);
    	
		refresh();
	}
	
	protected void refresh()
	{
		logger.info("Start refresh!");
		if(this.tabsheet == null)
		{
			logger.info("createTabSheet!");
			this.refreshTree();
			this.createTabSheet();
		}
		
//		logger.info("refreshTree!");
//		this.refreshTree();
		
		logger.info("createFilterPopupContent!");
		createFilterPopupContent();
		
    	String tab = (String)VaadinSession.getCurrent().getAttribute("tab");
    	
    	logger.debug("Got tab: " + tab);
    	
    	if(tab != null)
    	{
	    	AbstractComponent tabComponent = this.tabComponentMap.get(tab);
	    	
	    	logger.debug("Got tabComponent: " + tabComponent);
	    	
	    	if(tabComponent != null)
	    	{
	    		Module module = (Module)VaadinSession.getCurrent().getAttribute("module");
	    		
	    		VaadinSession.getCurrent().setAttribute("tab", null);
	    		VaadinSession.getCurrent().setAttribute("module", null);
	    		
	    		if(tabComponent instanceof TopologyTab)
	    		{
	    			 logger.debug("applyModuleFilter!");
	    			((TopologyTab)tabComponent).applyModuleFilter(module);
	    			logger.debug("search!");
	    			((TopologyTab)tabComponent).search();
	    			logger.debug("resetSearchDates!");
	    			((TopologyTab)tabComponent).resetSearchDates();
	    			logger.debug("applyFilter!");
	    			((TopologyTab)tabComponent).applyFilter();
	    		}
	    		
	    		this.tabsheet.setSelectedTab(tabComponent);
	    	}
    	}
    	
    	VaadinSession.getCurrent().setAttribute("tab", null);
    	VaadinSession.getCurrent().setAttribute("module", null);
    	
    	logger.debug("End refresh!");
	}
	
	protected void refreshTree()
	{
		this.moduleTree.removeAllItems();
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		logger.debug("authentication = " + authentication);
		
		if(authentication != null)
		{
			logger.debug("authentication has all authority " + authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY));
			logger.debug("authentication has topology authority " + authentication.hasGrantedAuthority(SecurityConstants.VIEW_TOPOLOGY_AUTHORITY));
		}
		
		if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
    					authentication.hasGrantedAuthority(SecurityConstants.VIEW_TOPOLOGY_AUTHORITY)))
    	{
			List<Server> servers = this.topologyService.getAllServers();
			
			logger.debug("trying to load tree for " + servers.size());
			
			for(Server server: servers)
			{	
				Set<Module> modules = server.getModules();
	
				this.moduleTree.addItem(server);
				this.moduleTree.setCaptionAsHtml(true);
				
				MonitorIcons icon = MonitorIcons.SERVER;
	        	icon.setSizePixels(14);
	        	icon.setColor("green");
	        	
	            this.moduleTree.setItemCaption(server, icon.getHtml() + " " + server.getName());
	            this.moduleTree.setChildrenAllowed(server, true);
	        	
	
		        for(Module module: modules)
		        {	        	
		            this.moduleTree.addItem(module);
		            this.moduleTree.setItemCaption(module, module.getName());
		            this.moduleTree.setParent(module, server);
		            this.moduleTree.setChildrenAllowed(module, true);
		            this.moduleTree.setItemIcon(module, VaadinIcons.ARCHIVE);
		            
		            Set<Flow> flows = module.getFlows();
		
		            for(Flow flow: flows)
		            {
		                this.moduleTree.addItem(flow);
		                this.moduleTree.setItemCaption(flow, flow.getName());
	                    this.moduleTree.setParent(flow, module);
		                this.moduleTree.setChildrenAllowed(flow, true);
		    			
		                TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
		                
		                Set<Component> components = flow.getComponents();
		                
		                for(Component component: components)
		                {
		                	this.moduleTree.addItem(component);
		                	this.moduleTree.setParent(component, flow);
		                	this.moduleTree.setItemCaption(component, component.getName());
		                	this.moduleTree.setChildrenAllowed(component, false);
		                	
		                	if(component.isConfigurable())
    	                	{
    	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG);
    	                	}
    	                	else
    	                	{
    	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG_O);
    	                	}
		                }
		            }
		        }
			}
			
			List<BusinessStream> businessStreams = this.topologyService.getAllBusinessStreams();
			
			if(this.businessStreamCombo != null)
			{
				this.businessStreamCombo.removeAllItems();
				
				for(BusinessStream businessStream: businessStreams)
				{
					this.businessStreamCombo.addItem(businessStream);
					this.businessStreamCombo.setItemCaption(businessStream, businessStream.getName());
				}
			}
			
			this.treeViewBusinessStreamCombo.removeAllItems();
			
			BusinessStream businessStreamAll = new BusinessStream();
			businessStreamAll.setName("All");
			
			this.treeViewBusinessStreamCombo.addItem(businessStreamAll);
			this.treeViewBusinessStreamCombo.setItemCaption(businessStreamAll, businessStreamAll.getName());
			
			for(BusinessStream businessStream: businessStreams)
			{
				this.treeViewBusinessStreamCombo.addItem(businessStream);
				this.treeViewBusinessStreamCombo.setItemCaption(businessStream, businessStream.getName());
			}
			
			this.treeViewBusinessStreamCombo.setValue(businessStreamAll);
    	}
		else
		{
			List<BusinessStream> businessStreams = this.topologyService.getAllBusinessStreams();
			
			if(this.businessStreamCombo != null)
			{
				this.businessStreamCombo.removeAllItems();
			}
			
			this.treeViewBusinessStreamCombo.removeAllItems();
			
			BusinessStream businessStreamAll = new BusinessStream();
			businessStreamAll.setName("All");
			
			this.treeViewBusinessStreamCombo.addItem(businessStreamAll);
			this.treeViewBusinessStreamCombo.setItemCaption(businessStreamAll, businessStreamAll.getName());			
			this.treeViewBusinessStreamCombo.setValue(businessStreamAll);
			
			for(BusinessStream businessStream: businessStreams)
			{
				if(authentication.canAccessLinkedItem(PolicyLinkTypeConstants.BUSINESS_STREAM_LINK_TYPE, businessStream.getId()))
				{
					for(BusinessStreamFlow bsFlow: businessStream.getFlows())
		        	{
		        		Server server = bsFlow.getFlow().getModule().getServer();
		        		Module module = bsFlow.getFlow().getModule();
		        		Flow flow = bsFlow.getFlow();
		        		
		        		if(!moduleTree.containsId(server))
		        		{
		            		moduleTree.addItem(server);
		                    moduleTree.setItemCaption(server, server.getName());
		                    moduleTree.setChildrenAllowed(server, true);
		                    		        		}
		                
		                moduleTree.addItem(module);
		                moduleTree.setItemCaption(module, module.getName());
		                moduleTree.setParent(module, server);
		                moduleTree.setChildrenAllowed(module, true);
		                moduleTree.setItemIcon(module, VaadinIcons.ARCHIVE);
		                
		                moduleTree.addItem(flow);
		                moduleTree.setItemCaption(flow, flow.getName());
		                moduleTree.setParent(flow, module);
		                moduleTree.setChildrenAllowed(flow, true);

		                TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
		                
		                Set<Component> components = flow.getComponents();
		
		                for(Component component: components)
		                {
		                	moduleTree.addItem(component);
		                	moduleTree.setParent(component, flow);
		                	moduleTree.setItemCaption(component, component.getName());
		                	moduleTree.setChildrenAllowed(component, false);
		                	
		                	if(component.isConfigurable())
    	                	{
    	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG);
    	                	}
    	                	else
    	                	{
    	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG_O);
    	                	}
		                }
		                
		                this.businessStreamCombo.addItem(businessStream);
		    			this.businessStreamCombo.setItemCaption(businessStream, businessStream.getName());
		    			
		    			this.treeViewBusinessStreamCombo.addItem(businessStream);
		    			this.treeViewBusinessStreamCombo.setItemCaption(businessStream, businessStream.getName());
		        	}
	        	}
        	}		
		}
	
		for (Iterator<?> it = this.moduleTree.rootItemIds().iterator(); it.hasNext();) 
		{
			this.moduleTree.expandItemsRecursively(it.next());
		}
		
		for (Iterator<?> it = this.moduleTree.getItemIds().iterator(); it.hasNext();) 
		{
			Object nextItem = it.next();
			if(nextItem instanceof Module)
			{
				this.moduleTree.collapseItemsRecursively(nextItem);
			}
		}
	}

	protected boolean actionFlow(Flow flow, String action)
	{		
		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
    	
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
		
    	String url = flow.getModule().getServer().getUrl() + ":" + flow.getModule().getServer().getPort()
				+ flow.getModule().getContextRoot() 
				+ "/rest/moduleControl/controlFlowState/"
				+ flow.getModule().getName() 
	    		+ "/"
	    		+ flow.getName();
    	
	    WebTarget webTarget = client.target(url);
	    Response response = webTarget.request().put(Entity.entity(action, MediaType.APPLICATION_OCTET_STREAM));
	    
	    if(response.getStatus()  == 200)
	    {
	    	Notification.show(flow.getName() + " flow " + action + "!");
	    }  
	    else
	    {
	    	response.bufferEntity();
	        
	        String responseMessage = response.readEntity(String.class);
	        
	    	Notification.show(responseMessage, Type.ERROR_MESSAGE);
	    	return false;
	    }
	    
	    return true;
	}
	

	
	
	/* (non-Javadoc)
	 * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Action[] getActions(Object target, Object sender)
	{    
		IkasanAuthentication authentication = null;
		
		if(VaadinService.getCurrentRequest() != null
				&& VaadinService.getCurrentRequest().getWrappedSession() != null)
		{
			authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
		        	.getAttribute(DashboardSessionValueConstants.USER);
		}
		
		logger.debug("authentication fom session = " + authentication);
		
		if(authentication != null)
		{
			logger.debug("authentication has all authority " + authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY));
		}

		if(target instanceof Server)
        {
            return serverActions;
        }
		else if(target instanceof Module)
        {
            return moduleActions;
        }
		else if(target instanceof Flow)
        {
			if(authentication != null 
	    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)))
			{
				return this.flowActions;
			}

			Flow flow = ((Flow)target);
			
			String state = this.topologyCache.getState(flow.getModule().getName() + "-" + flow.getName());
			if(state != null && state.equals(RUNNING))
			{
				return this.flowActionsStarted;
			}
			else if(state != null && (state.equals(RUNNING) || state.equals(RECOVERING)))
			{
				return this.flowActionsStarted;
			}
			else if (state != null &&(state.equals(STOPPED) || state.equals(STOPPED_IN_ERROR)))
			{
				return this.flowActionsStopped;
			}
			else if (state != null && state.equals(PAUSED))
			{
				return this.flowActionsPaused;
			}
			else
			{
				return this.flowActions;
			}
        }
		else if(target instanceof Component)
        {
			if(authentication != null 
	    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)))
			{
				return this.componentActions;
			}
	
			if(((Component)target).isConfigurable())
			{
				return componentActionsConfigurable;
			}
			else
			{
				return componentActions;
			}
        }
        
           return actionsEmpty;

	}

	/* (non-Javadoc)
	 * @see com.vaadin.event.Action.Handler#handleAction(com.vaadin.event.Action, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void handleAction(Action action, Object sender, Object target)
	{	
		IkasanAuthentication authentication = null;
		
		if(VaadinService.getCurrentRequest() != null
				&& VaadinService.getCurrentRequest().getWrappedSession() != null)
		{
			authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
		        	.getAttribute(DashboardSessionValueConstants.USER);
		}
		
		if(authentication == null 
    			|| (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)))
		{
			return;
		}
		
        if(target != null && target instanceof Component)
        {
        	if(action.equals(CONFIGURE))
        	{
        		this.componentConfigurationWindow.populate(((Component)target));
        		UI.getCurrent().addWindow(this.componentConfigurationWindow);
        	}
        	if(action.equals(WIRETAP))
        	{
        		UI.getCurrent().addWindow(new WiretapConfigurationWindow((Component)target
        			, triggerManagementService));
        	}
        	if(action.equals(ERROR_CATEGORISATION))
        	{
        		Component component = (Component)target;
        		
        		UI.getCurrent().addWindow(new ErrorCategorisationWindow(component.getFlow().getModule().getServer(),
        				component.getFlow().getModule(), component.getFlow(), component, errorCategorisationService));
        	}
        }
        else if(target != null && target instanceof Flow)
        {
        	Flow flow = ((Flow)target);
        	
	        if(action.equals(START))
	        {
	     		if(this.actionFlow(flow, "start"))
	     		{
	     			TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
	     		}
	        }
	        else if(action.equals(STOP))
	        {
	        	if(this.actionFlow(flow, "stop"))
	        	{
	        		TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
	        	}
	        }
	        else if(action.equals(PAUSE))
	        {
	        	if(this.actionFlow(flow, "pause"))
	        	{
	        		TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
	        	}
	        }
	        else if(action.equals(RESUME))
	        {
	        	if(this.actionFlow(flow, "resume"))
	        	{
	        		TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
	        	}
	        }
	        else if(action.equals(START_PAUSE))
	        {       	
	        	if(this.actionFlow(flow, "startPause"))
	        	{
	        		TopologyViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.AUTOMATION);
	        	}
	        }
	        else if(action.equals(STARTUP_CONTROL))
	        {       	
	        	UI.getCurrent().addWindow(new StartupControlConfigurationWindow(this.startupControlService, flow));
	        }
	        else if(action.equals(ERROR_CATEGORISATION))
        	{
        		
        		UI.getCurrent().addWindow(new ErrorCategorisationWindow(flow.getModule().getServer(),
        				flow.getModule(), flow, null, errorCategorisationService));
        	}
	        
        }
        else if(target != null && target instanceof Module)
        {
        	if(action.equals(ERROR_CATEGORISATION))
        	{
        		Module module = (Module)target;
        		
        		UI.getCurrent().addWindow(new ErrorCategorisationWindow(module.getServer(),
        				module, null, null, errorCategorisationService));
        	}
        }
        else if(target != null && target instanceof Server)
        {
        	if(action.equals(ERROR_CATEGORISATION))
        	{
        		Server server = (Server)target;
        		
        		UI.getCurrent().addWindow(new ErrorCategorisationWindow(server,
        				null, null, null, errorCategorisationService));
        	}
        	else if(action.equals(EDIT))
        	{
        		Server server = (Server)target;
        		
        		UI.getCurrent().addWindow(new ServerWindow(topologyService, server));
        	}
        }
	}
	
	protected Date getMidnightToday()
	{
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTime();
	}
	
	protected Date getTwentyThreeFixtyNineToday()
	{
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTime();
	}
	
	@Subscribe
	public void receiveFlowStateEvent(final FlowStateEvent event)
	{
		UI.getCurrent().access(new Runnable() 
		{
            @Override
            public void run() 
            {
            	VaadinSession.getCurrent().getLockInstance().lock();
        		try 
        		{
        			flowStates = event.getFlowStateMap();
        			moduleTree.markAsDirty();
        		} 
        		finally 
        		{
        			VaadinSession.getCurrent().getLockInstance().unlock();
        		}
            	
            	UI.getCurrent().push();	
            }
        });	
	}

}


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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.PolicyLinkTypeConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanCellStyleGenerator;
import org.ikasan.dashboard.ui.topology.window.ActionedExclusionEventViewWindow;
import org.ikasan.dashboard.ui.topology.window.ComponentConfigurationWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorOccurrenceViewWindow;
import org.ikasan.dashboard.ui.topology.window.ExclusionEventViewWindow;
import org.ikasan.dashboard.ui.topology.window.NewBusinessStreamWindow;
import org.ikasan.dashboard.ui.topology.window.WiretapPayloadViewWindow;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.BusinessStreamFlowKey;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.ikasan.wiretap.dao.WiretapDao;
import org.springframework.security.core.GrantedAuthority;

import com.ikasan.topology.exception.DiscoveryException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

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
    private final Action[] serverActions = new Action[] { DETAILS };
    private final Action[] moduleActions = new Action[] { DETAILS, VIEW_DIAGRAM };
//    private final Action[] flowActions = new Action[] { DETAILS, STOP, START, PAUSE, RESTART, DISABLE };
    private final Action[] flowActionsStopped = new Action[] { START, START_PAUSE };
    private final Action[] flowActionsStarted = new Action[] { STOP, PAUSE };
    private final Action[] flowActionsPaused = new Action[] { STOP, RESUME };
    private final Action[] componentActionsConfigurable = new Action[] { DETAILS, CONFIGURE, WIRETAP };
    private final Action[] componentActions = new Action[] { DETAILS, WIRETAP };
    private final Action[] actionsEmpty = new Action[]{};
	
	private ThemeResource serverResource = new ThemeResource("images/server.jpg");
	private ThemeResource moduleResource = new ThemeResource("images/module.png");
	private ThemeResource flowResource = new ThemeResource("images/flow.png");
	private ThemeResource flowStartedResource = new ThemeResource("images/flow_started.png");
	private ThemeResource flowPausedResource = new ThemeResource("images/flow_paused.png");
	private ThemeResource flowStoppedResource = new ThemeResource("images/flow_stopped.png");
	private ThemeResource flowStoppedInErrorResource = new ThemeResource("images/flow_stopped_in_error.png");
	private ThemeResource componentResource = new ThemeResource("images/component.png");
	private ThemeResource componentConfigurableResource = new ThemeResource("images/component_configurable.png");

	private TopologyService topologyService;
	private Panel topologyTreePanel;
	private Tree moduleTree;
	private ComponentConfigurationWindow componentConfigurationWindow;

	private Panel tabsheetPanel;
	
	private Table businessStreamTable;
	private Table wiretapTable;	
	private Table errorOccurenceTable;
	private Table exclusionsTable;
	private Table actionedExclusionsTable;
	private Table systemEventTable;
	
	private ComboBox businessStreamCombo;
	private ComboBox treeViewBusinessStreamCombo;
	
	private WiretapDao wiretapDao;
	
	private Table wiretapModules = new Table("Modules");
	private Table wiretapFlows = new Table("Flows");
	private Table wiretapComponents = new Table("Components");
	
	private Table errorOccurenceModules = new Table("Modules");
	private Table errorOccurenceFlows = new Table("Flows");
	private Table errorOccurenceComponents = new Table("Components");
	
	private Table actionedExclusionsModules = new Table("Modules");
	private Table actionedExclusionsFlows = new Table("Flows");
	private Table actionedExclusionsComponents = new Table("Components");
	
	private PopupDateField fromDate;
	private PopupDateField toDate;
	private PopupDateField errorFromDate;
	private PopupDateField errorToDate;
	private PopupDateField actionedExclusionFromDate;
	private PopupDateField actionedExclusionToDate;
	private PopupDateField systemEventFromDate;
	private PopupDateField systemEventToDate;
	
	private TextField eventId;
	private TextField payloadContent;
	
	private ErrorReportingService errorReportingService;
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	private HospitalManagementService<ExclusionEventAction> hospitalManagementService;
	
	private SerialiserFactory serialiserFactory;
	
	private BusinessStream businessStream;
	
	private SystemEventService systemEventService;
	
	private HashMap<String, String> flowStates = new HashMap<String, String>();
	
	public TopologyViewPanel(TopologyService topologyService, ComponentConfigurationWindow componentConfigurationWindow,
			 WiretapDao wiretapDao, ErrorReportingService errorReportingService, ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			 SerialiserFactory serialiserFactory, HospitalManagementService<ExclusionEventAction> hospitalManagementService, SystemEventService systemEventService)
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
		this.errorReportingService = errorReportingService;
		if(this.errorReportingService == null)
		{
			throw new IllegalArgumentException("errorReportingService cannot be null!");
		}
		this.exclusionManagementService = exclusionManagementService;
		if(this.exclusionManagementService == null)
		{
			throw new IllegalArgumentException("exclusionManagementService cannot be null!");
		}
		this.serialiserFactory = serialiserFactory;
		if(this.serialiserFactory == null)
		{
			throw new IllegalArgumentException("serialiserFactory cannot be null!");
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

		init();
	}

	protected void init()
	{
		this.tabsheetPanel = new Panel("Topology Stuff");
		this.tabsheetPanel.setStyleName("dashboard");
		this.tabsheetPanel.setSizeFull();
		
		this.createModuleTreePanel();
		
		this.setWidth("100%");
		this.setHeight("100%");

		HorizontalSplitPanel hsplit = new HorizontalSplitPanel();

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

		this.setContent(hsplit);
	}
	
	protected void createTabSheet()
	{			
		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();

		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		Collection<GrantedAuthority> auths = (Collection<GrantedAuthority>)authentication.getAuthorities();
		
		for(GrantedAuthority auth: auths)
		{
			logger.info("Auth: " + auth.getAuthority());
		}
	    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_BUSINESS_STREAM_AUTHORITY)))
    	{
			VerticalLayout tab1 = new VerticalLayout();
			tab1.setSizeFull();
			tab1.addComponent(createBusinessStreamPanel());
			tabsheet.addTab(tab1, "Business Stream");
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_WIRETAP_AUTHORITY)))
    	{
    		VerticalLayout tab2 = new VerticalLayout();
    		tab2.setSizeFull();
    		tab2.addComponent(createWiretapPanel());
    		tabsheet.addTab(tab2, "Wiretaps");
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_ERRORS_AUTHORITY)))
    	{
    		VerticalLayout tab3 = new VerticalLayout();
    		tab3.setSizeFull();
    		tab3.addComponent(createErrorOccurencePanel());
    		tabsheet.addTab(tab3, "Errors");
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_EXCLUSION_AUTHORITY)))
    	{
    		final VerticalLayout tab4 = new VerticalLayout();
    		tab4.setSizeFull();
    		tab4.addComponent(createExclusionPanel());
    		tabsheet.addTab(tab4, "Exclusions");
    	}
		
    	final VerticalLayout tab5 = new VerticalLayout();
		tab5.setSizeFull();
		tab5.addComponent(this.createActionedExclusionsPanel());
		tabsheet.addTab(tab5, "Actioned Exclusions");
		
		final VerticalLayout tab6 = new VerticalLayout();
		tab6.setSizeFull();
		tab6.addComponent(this.createSystemEventPanel());
		tabsheet.addTab(tab6, "System Events");
		
		tabsheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
	           
            public void selectedTabChange(SelectedTabChangeEvent event) 
            {
            	if(authentication != null 
            			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
            					|| authentication.hasGrantedAuthority(SecurityConstants.VIEW_EXCLUSION_AUTHORITY)))
            	{
            		refreshExcludedEventsTable();
            	}
            }
        });

		this.tabsheetPanel.setContent(tabsheet);
	}

	protected void createModuleTreePanel()
	{
		this.topologyTreePanel = new Panel("Topology");
		this.topologyTreePanel.setStyleName("dashboard");
		this.topologyTreePanel.setSizeFull();

		this.moduleTree = new Tree();
		this.moduleTree.setSizeFull();
		this.moduleTree.addActionHandler(this);
		this.moduleTree.setDragMode(TreeDragMode.NODE);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		
		this.treeViewBusinessStreamCombo = new ComboBox("Business Stream");
		this.treeViewBusinessStreamCombo.setHeight(40, Unit.PIXELS);
		
		this.treeViewBusinessStreamCombo.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                	businessStream  = (BusinessStream)event.getProperty().getValue();
                	
                	logger.info("Value changed to business stream: " + businessStream.getName());
                
                	moduleTree.removeAllItems();
                	
                	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
            	        	.getAttribute(DashboardSessionValueConstants.USER);
            		
            		if(authentication != null 
                			&& authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
                			&& businessStream.getName().equals("All"))
                	{
                		List<Server> servers = TopologyViewPanel.this.topologyService.getAllServers();
                		
                		for(Server server: servers)
                		{
                			Set<Module> modules = server.getModules();
                			
                			refreshFlowStates(modules);

                			TopologyViewPanel.this.moduleTree.addItem(server);
                			TopologyViewPanel.this.moduleTree.setItemCaption(server, server.getName());
                			TopologyViewPanel.this.moduleTree.setChildrenAllowed(server, true);
                			TopologyViewPanel.this.moduleTree.setItemIcon(server, serverResource);

                	        for(Module module: modules)
                	        {
                	        	TopologyViewPanel.this.moduleTree.addItem(module);
                	        	TopologyViewPanel.this.moduleTree.setItemCaption(module, module.getName());
                	        	TopologyViewPanel.this.moduleTree.setParent(module, server);
                	        	TopologyViewPanel.this.moduleTree.setChildrenAllowed(module, true);
                	        	TopologyViewPanel.this.moduleTree.setItemIcon(module, moduleResource);
                	            
                	            Set<Flow> flows = module.getFlows();
                	
                	            for(Flow flow: flows)
                	            {
                	            	TopologyViewPanel.this.moduleTree.addItem(flow);
                	            	TopologyViewPanel.this.moduleTree.setItemCaption(flow, flow.getName());
                	            	TopologyViewPanel.this.moduleTree.setParent(flow, module);
                	            	TopologyViewPanel.this.moduleTree.setChildrenAllowed(flow, true);
                	            	
                	            	String state = flowStates.get(flow.getModule().getName() + "-" + flow.getName());
        			    			if(state.equals(RUNNING))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowStartedResource);
        			    			}
        			    			else if(state.equals(RUNNING) || state.equals(RECOVERING))
        			    			{
        			    				moduleTree.setItemIcon(flow,flowStartedResource);
        			    			}
        			    			else if (state.equals(STOPPED))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowStoppedResource);
        			    			}
        			    			else if (state.equals(STOPPED_IN_ERROR))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowStoppedInErrorResource);
        			    			}
        			    			else if (state.equals(PAUSED))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowPausedResource);
        			    			}
                	                
                	                Set<Component> components = flow.getComponents();
                	
                	                for(Component component: components)
                	                {
                	                	TopologyViewPanel.this.moduleTree.addItem(component);
                	                	TopologyViewPanel.this.moduleTree.setParent(component, flow);
                	                	TopologyViewPanel.this.moduleTree.setItemCaption(component, component.getName());
                	                	TopologyViewPanel.this.moduleTree.setChildrenAllowed(component, false);
                	                	
                	                	if(component.isConfigurable())
                	                	{
                	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, TopologyViewPanel.this.componentConfigurableResource);
                	                	}
                	                	else
                	                	{
                	                		TopologyViewPanel.this.moduleTree.setItemIcon(component, TopologyViewPanel.this.componentResource);
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
            		                    moduleTree.setItemIcon(server, serverResource);
            		        		}
            		                
            		                moduleTree.addItem(module);
            		                moduleTree.setItemCaption(module, module.getName());
            		                moduleTree.setParent(module, server);
            		                moduleTree.setChildrenAllowed(module, true);
            		                moduleTree.setItemIcon(module, moduleResource);
            		                
            		                moduleTree.addItem(flow);
            		                moduleTree.setItemCaption(flow, flow.getName());
            		                moduleTree.setParent(flow, module);
            		                moduleTree.setChildrenAllowed(flow, true);
            		                
            		                String state = flowStates.get(flow.getModule().getName() + "-" + flow.getName());
        			    			if(state.equals(RUNNING))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowStartedResource);
        			    			}
        			    			else if(state.equals(RUNNING) || state.equals(RECOVERING))
        			    			{
        			    				moduleTree.setItemIcon(flow,flowStartedResource);
        			    			}
        			    			else if (state.equals(STOPPED))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowStoppedResource);
        			    			}
        			    			else if (state.equals(STOPPED_IN_ERROR))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowStoppedInErrorResource);
        			    			}
        			    			else if (state.equals(PAUSED))
        			    			{
        			    				moduleTree.setItemIcon(flow, flowPausedResource);
        			    			}
            		                
            		                Set<Component> components = flow.getComponents();
            		
            		                for(Component component: components)
            		                {
            		                	moduleTree.addItem(component);
            		                	moduleTree.setParent(component, flow);
            		                	moduleTree.setItemCaption(component, component.getName());
            		                	moduleTree.setChildrenAllowed(component, false);
            		                	
            		                	if(component.isConfigurable())
            		                	{
            		                		moduleTree.setItemIcon(component, componentConfigurableResource);
            		                	}
            		                	else
            		                	{
            		                		moduleTree.setItemIcon(component, componentResource);
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
		                        moduleTree.setItemIcon(server, serverResource);
	                		}
	                        
	                        moduleTree.addItem(module);
	    	                moduleTree.setItemCaption(module, module.getName());
	                        moduleTree.setParent(module, server);
	    	                moduleTree.setChildrenAllowed(module, true);
	    	                moduleTree.setItemIcon(module, moduleResource);
	                        
	                        moduleTree.addItem(flow);
	    	                moduleTree.setItemCaption(flow, flow.getName());
	                        moduleTree.setParent(flow, module);
	    	                moduleTree.setChildrenAllowed(flow, true);
	    	                
	    	                String state = flowStates.get(flow.getModule().getName() + "-" + flow.getName());
			    			if(state.equals(RUNNING))
			    			{
			    				moduleTree.setItemIcon(flow, flowStartedResource);
			    			}
			    			else if(state.equals(RUNNING) || state.equals(RECOVERING))
			    			{
			    				moduleTree.setItemIcon(flow,flowStartedResource);
			    			}
			    			else if (state.equals(STOPPED))
			    			{
			    				moduleTree.setItemIcon(flow, flowStoppedResource);
			    			}
			    			else if (state.equals(STOPPED_IN_ERROR))
			    			{
			    				moduleTree.setItemIcon(flow, flowStoppedInErrorResource);
			    			}
			    			else if (state.equals(PAUSED))
			    			{
			    				moduleTree.setItemIcon(flow, flowPausedResource);
			    			}
	    	                
	    	                Set<Component> components = flow.getComponents();
	    	
	    	                for(Component component: components)
	    	                {
	    	                	moduleTree.addItem(component);
	    	                	moduleTree.setParent(component, flow);
	    	                	moduleTree.setItemCaption(component, component.getName());
	    	                	moduleTree.setChildrenAllowed(component, false);
	    	                	
	    	                	if(component.isConfigurable())
	    	                	{
	    	                		moduleTree.setItemIcon(component, componentConfigurableResource);
	    	                	}
	    	                	else
	    	                	{
	    	                		moduleTree.setItemIcon(component, componentResource);
	    	                	}
	    	                }
	                	}
                	}        	
                }
            }
        });

		layout.addComponent(this.treeViewBusinessStreamCombo);
		layout.setExpandRatio(this.treeViewBusinessStreamCombo, 0.12f);
		
		Button discoverButton = new Button("Discover");
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
					Notification.show("An error occurred trying to auto discover modules: " 
							+ e.getMessage(), Type.ERROR_MESSAGE);
				}
            	
            	Notification.show("Auto discovery complete!");
            }
        });
		
		Button refreshButton = new Button("Refresh");
		refreshButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
				refreshTree();
            }
        });
		
		GridLayout buttonLayout = new GridLayout(2, 1);
		buttonLayout.addComponent(discoverButton);
		buttonLayout.addComponent(refreshButton);
		layout.addComponent(buttonLayout);
		layout.setExpandRatio(buttonLayout, 0.1f);
		
		layout.addComponent(this.moduleTree);
		layout.setExpandRatio(this.moduleTree, 0.78f);

		this.topologyTreePanel.setContent(layout);
	}

	protected Layout createBusinessStreamPanel()
	{
		this.businessStreamTable = new Table();
		this.businessStreamTable.addContainerProperty("Flow Name", String.class,  null);
		this.businessStreamTable.addContainerProperty("", Button.class,  null);
		this.businessStreamTable.setSizeFull();
		this.businessStreamTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.businessStreamTable.setDragMode(TableDragMode.ROW);
		this.businessStreamTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);
				
				final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
			        	.getAttribute(DashboardSessionValueConstants.USER);
				
				if(authentication != null 
		    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
		    					&& !authentication.hasGrantedAuthority(SecurityConstants.MODIFY_BUSINESS_STREAM_AUTHORITY)))
		    	{
					Notification.show("You do not have the privilege to modify a business stream.");
					return;
		    	}

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();

				if(t.getItemId() instanceof Flow)
				{
					final Flow sourceContainer = (Flow) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ sourceContainer.getName());
					
					final BusinessStream businessStream = (BusinessStream)TopologyViewPanel.this.businessStreamCombo.getValue();
					BusinessStreamFlowKey key = new BusinessStreamFlowKey();
					key.setBusinessStreamId(businessStream.getId());
					key.setFlowId(sourceContainer.getId());
					final BusinessStreamFlow businessStreamFlow = new BusinessStreamFlow(key);
					businessStreamFlow.setFlow(sourceContainer);
					businessStreamFlow.setOrder(TopologyViewPanel.this.businessStreamTable.getItemIds().size());
					
					if(!businessStream.getFlows().contains(businessStreamFlow))
					{
						businessStream.getFlows().add(businessStreamFlow);
						
						TopologyViewPanel.this.topologyService.saveBusinessStream(businessStream);
						
						Button deleteButton = new Button();
    					ThemeResource deleteIcon = new ThemeResource(
    							"images/remove-icon.png");
    					deleteButton.setIcon(deleteIcon);
    					deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    					
						Button flowButton = new Button();
						flowButton.setIcon(flowResource);
    					
    					deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    					deleteButton.setData(businessStreamFlow);
    					
    					// Add the delete functionality to each role that is added
    					deleteButton.addClickListener(new Button.ClickListener() 
    			        {
    			            public void buttonClick(ClickEvent event) 
    			            {		
    			            	logger.info("Attempting to remove businessStreamFlow: " + businessStreamFlow);
    			            	logger.info("Number of flows before: " + businessStream.getFlows().size());
    			            	businessStream.getFlows().remove(businessStreamFlow);
    			            	logger.info("Number of flows after: " + businessStream.getFlows().size());
    			            	
    			            	TopologyViewPanel.this.topologyService.deleteBusinessStreamFlow(businessStreamFlow);
    			            	TopologyViewPanel.this.topologyService.saveBusinessStream(businessStream);
    			            	
    			            	businessStreamTable.removeItem(businessStreamFlow.getFlow());
    			            }
    			        });
						
						businessStreamTable.addItem(new Object[]{sourceContainer.getName(), deleteButton}, sourceContainer);
					}
				}
				else if(t.getItemId() instanceof Module)
				{
					final Module sourceContainer = (Module) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ sourceContainer.getName());
					
					for(Flow flow: sourceContainer.getFlows())
					{
						
						final BusinessStream businessStream = (BusinessStream)TopologyViewPanel.this.businessStreamCombo.getValue();
						BusinessStreamFlowKey key = new BusinessStreamFlowKey();
						key.setBusinessStreamId(businessStream.getId());
						key.setFlowId(flow.getId());
						final BusinessStreamFlow businessStreamFlow = new BusinessStreamFlow(key);
						businessStreamFlow.setFlow(flow);
						businessStreamFlow.setOrder(TopologyViewPanel.this.businessStreamTable.getItemIds().size());
						
						if(!businessStream.getFlows().contains(businessStreamFlow))
						{
							businessStream.getFlows().add(businessStreamFlow);
							
							TopologyViewPanel.this.topologyService.saveBusinessStream(businessStream);
							
							Button flowButton = new Button();
							flowButton.setIcon(flowResource);
	    					
							Button deleteButton = new Button();
	    					ThemeResource deleteIcon = new ThemeResource(
	    							"images/remove-icon.png");
	    					deleteButton.setIcon(deleteIcon);
	    					deleteButton.setStyleName(Reindeer.BUTTON_LINK);
	    					deleteButton.setData(businessStreamFlow);
	    					
	    					// Add the delete functionality to each role that is added
	    					deleteButton.addClickListener(new Button.ClickListener() 
	    			        {
	    			            public void buttonClick(ClickEvent event) 
	    			            {		
	    			            	logger.info("Attempting to remove businessStreamFlow: " + businessStreamFlow);
	    			            	logger.info("Number of flows before: " + businessStream.getFlows().size());
	    			            	businessStream.getFlows().remove(businessStreamFlow);
	    			            	logger.info("Number of flows after: " + businessStream.getFlows().size());
	    			            	
	    			            	TopologyViewPanel.this.topologyService.deleteBusinessStreamFlow(businessStreamFlow);
	    			            	TopologyViewPanel.this.topologyService.saveBusinessStream(businessStream);
	    			            	
	    			            	businessStreamTable.removeItem(businessStreamFlow.getFlow());
	    			            }
	    			        });
							
							businessStreamTable.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						}
					}
				}
				else
				{
					Notification.show("Only modules or flows can be dragged to this table.");
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		
		GridLayout controlsLayout = new GridLayout(5, 1);
		controlsLayout.setColumnExpandRatio(0, .2f);
		controlsLayout.setColumnExpandRatio(1, .3f);
		controlsLayout.setColumnExpandRatio(2, .05f);
		controlsLayout.setColumnExpandRatio(3, .05f);
		controlsLayout.setColumnExpandRatio(4, .4f);
		
		controlsLayout.setSizeFull();
		Label businessStreamLabel = new Label("Business Stream");
		this.businessStreamCombo = new ComboBox();
		
		this.businessStreamCombo.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if(event.getProperty() != null && event.getProperty().getValue() != null)
                {
                	final BusinessStream businessStream  = (BusinessStream)event.getProperty().getValue();
                	
                	logger.info("Value changed to business stream: " + businessStream.getName());
                	businessStreamTable.removeAllItems();
                	logger.info("Removed all items from table.");

                	for(final BusinessStreamFlow businessStreamFlow: businessStream.getFlows())
                	{
                		logger.info("Adding flow: " + businessStreamFlow);
                		Button deleteButton = new Button();
    					ThemeResource deleteIcon = new ThemeResource(
    							"images/remove-icon.png");
    					deleteButton.setIcon(deleteIcon);
    					deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    					
    					Button flowButton = new Button();
						flowButton.setIcon(flowResource);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    					
    					// Add the delete functionality to each role that is added
    					deleteButton.addClickListener(new Button.ClickListener() 
    			        {
    			            public void buttonClick(ClickEvent event) 
    			            {		
    			            	logger.info("Attempting to remove businessStreamFlow: " + businessStreamFlow);
    			            	logger.info("Number of flows before: " + businessStream.getFlows().size());
    			            	businessStream.getFlows().remove(businessStreamFlow);
    			            	logger.info("Number of flows after: " + businessStream.getFlows().size());
    			            	
    			            	TopologyViewPanel.this.topologyService.deleteBusinessStreamFlow(businessStreamFlow);
    			            	TopologyViewPanel.this.topologyService.saveBusinessStream(businessStream);
    			            	
    			            	businessStreamTable.removeItem(businessStreamFlow.getFlow());
    			            }
    			        });
    					
    					logger.info("Adding flow: " + businessStreamFlow.getFlow());
    					
    					final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
    				        	.getAttribute(DashboardSessionValueConstants.USER);
    					
    					if(authentication != null 
    			    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    			    					&& !authentication.hasGrantedAuthority(SecurityConstants.MODIFY_BUSINESS_STREAM_AUTHORITY)))
    			    	{
    						deleteButton.setVisible(false);
    			    	}
    					
                		businessStreamTable.addItem(new Object[]{businessStreamFlow.getFlow().getName(), deleteButton}, businessStreamFlow.getFlow());
                	}
                }
            }
        });
		
		controlsLayout.addComponent(businessStreamLabel, 0, 0);
		controlsLayout.addComponent(businessStreamCombo, 1, 0);
		
		Button newButton = new Button("New");
		newButton.setStyleName(Reindeer.BUTTON_LINK);
    	newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final NewBusinessStreamWindow newBusinessStreamWindow = new NewBusinessStreamWindow();
            	UI.getCurrent().addWindow(newBusinessStreamWindow);
            	
            	newBusinessStreamWindow.addCloseListener(new Window.CloseListener() {
                    // inline close-listener
                    public void windowClose(CloseEvent e) {
                    	TopologyViewPanel.this.topologyService.saveBusinessStream(newBusinessStreamWindow.getBusinessStream());
                    	
                    	TopologyViewPanel.this.businessStreamCombo.addItem(newBusinessStreamWindow.getBusinessStream());
                    	TopologyViewPanel.this.businessStreamCombo.setItemCaption(newBusinessStreamWindow.getBusinessStream(), 
                    			newBusinessStreamWindow.getBusinessStream().getName());
                    	
                    	TopologyViewPanel.this.businessStreamCombo.select(newBusinessStreamWindow.getBusinessStream());
                    	
                    	TopologyViewPanel.this.businessStreamTable.removeAllItems();
                    }
                });
            }
        });
    	
    	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null 
    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					&& !authentication.hasGrantedAuthority(SecurityConstants.CREATE_BUSINESS_STREAM_AUTHORITY)))
    	{
			newButton.setVisible(false);
    	}
    	
    	controlsLayout.addComponent(newButton, 2, 0);
    	
    	Button deleteButton = new Button("Delete");
    	deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    	deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	
            }
        });
    	
    	if(authentication != null 
    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					&& !authentication.hasGrantedAuthority(SecurityConstants.DELETE_BUSINESS_STREAM_AUTHORITY)))
    	{
    		deleteButton.setVisible(false);
    	}

    	controlsLayout.addComponent(deleteButton, 3, 0);
    	
    	layout.addComponent(controlsLayout);
    	layout.setExpandRatio(controlsLayout, .07f);
		layout.addComponent(this.businessStreamTable);
		layout.setExpandRatio(this.businessStreamTable, .93f);
		
		return layout;
	}
	
	
	
	protected Layout createWiretapPanel()
	{
		this.wiretapTable = new Table();
		this.wiretapTable.setSizeFull();
		this.wiretapTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.wiretapTable.addContainerProperty("Module Name", String.class,  null);
		this.wiretapTable.addContainerProperty("Flow Name", String.class,  null);
		this.wiretapTable.addContainerProperty("Component Name", String.class,  null);
		this.wiretapTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.wiretapTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) {
		    	WiretapEvent<String> wiretapEvent = (WiretapEvent<String>)itemClickEvent.getItemId();
		    	WiretapPayloadViewWindow wiretapPayloadViewWindow = new WiretapPayloadViewWindow(wiretapEvent);
		    
		    	UI.getCurrent().addWindow(wiretapPayloadViewWindow);
		    }
		});
		
		
		Button searchButton = new Button("Search");
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	wiretapTable.removeAllItems();

            	HashSet<String> modulesNames = null;
            	
            	if(wiretapModules.getItemIds().size() > 0)
            	{
	            	modulesNames = new HashSet<String>();
	            	for(Object module: wiretapModules.getItemIds())
	            	{
	            		modulesNames.add(((Module)module).getName());
	            	}
            	}
            	
            	HashSet<String> flowNames = null;
            	
            	if(wiretapFlows.getItemIds().size() > 0)
            	{
            		flowNames = new HashSet<String>();
            		for(Object flow: wiretapFlows.getItemIds())
                	{
                		flowNames.add(((Flow)flow).getName());
                	}
            	}
            	
            	HashSet<String> componentNames = null;
            	
            	if(wiretapComponents.getItemIds().size() > 0)
            	{
            		componentNames = new HashSet<String>();
	            	for(Object component: wiretapComponents.getItemIds())
	            	{
	            		componentNames.add("before " + ((Component)component).getName());
	            		componentNames.add("after " + ((Component)component).getName());
	            	}
            	}
            	
            	if(modulesNames == null && flowNames == null && componentNames == null
            			&& !((BusinessStream)treeViewBusinessStreamCombo.getValue()).getName().equals("All"))
            	{
            		BusinessStream businessStream = ((BusinessStream)treeViewBusinessStreamCombo.getValue());
            		
            		modulesNames = new HashSet<String>();
            		for(BusinessStreamFlow flow: businessStream.getFlows())
            		{
            			modulesNames.add(flow.getFlow().getModule().getName());
            		}
            	}
         
            	PagedSearchResult<WiretapEvent> events = wiretapDao.findWiretapEvents(0, 10000, "timestamp", false, modulesNames
            			, flowNames, componentNames, null, null, fromDate.getValue(), toDate.getValue(), null);

            	for(WiretapEvent<String> wiretapEvent: events.getPagedResults())
            	{
            		Date date = new Date(wiretapEvent.getTimestamp());
            		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            	    String timestamp = format.format(date);
            	    
            		wiretapTable.addItem(new Object[]{wiretapEvent.getModuleName(), wiretapEvent.getFlowName()
            				, wiretapEvent.getComponentName(), timestamp}, wiretapEvent);
            	}
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	wiretapModules.removeAllItems();
            	wiretapFlows.removeAllItems();
            	wiretapComponents.removeAllItems();
            }
        });

		GridLayout layout = new GridLayout(1, 5);
		layout.setMargin(true);
		
		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSizeFull();
		
		wiretapModules.addContainerProperty("Module Name", String.class,  null);
		wiretapModules.addContainerProperty("", Button.class,  null);
		wiretapModules.setSizeFull();
		wiretapModules.setCellStyleGenerator(new IkasanCellStyleGenerator());
		wiretapModules.setDragMode(TableDragMode.ROW);
		wiretapModules.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Module)
				{
					final Module module = (Module) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ module.getName());
					
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	wiretapModules.removeItem(module);
			            }
			        });
					
					wiretapModules.addItem(new Object[]{module.getName(), deleteButton}, module);

					for(final Flow flow: module.getFlows())
					{
						deleteButton = new Button();
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	wiretapFlows.removeItem(flow);
				            }
				        });
						
						wiretapFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
						for(final Component component: flow.getComponents())
						{
							deleteButton = new Button();
							deleteButton.setIcon(deleteIcon);
							deleteButton.setStyleName(Reindeer.BUTTON_LINK);
							
							// Add the delete functionality to each role that is added
							deleteButton.addClickListener(new Button.ClickListener() 
					        {
					            public void buttonClick(ClickEvent event) 
					            {		
					            	wiretapComponents.removeItem(component);
					            }
					        });
							
							wiretapComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						}
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		listSelectLayout.addComponent(wiretapModules, 0, 0);
		wiretapFlows.addContainerProperty("Flow Name", String.class,  null);
		wiretapFlows.addContainerProperty("", Button.class,  null);
		wiretapFlows.setSizeFull();
		wiretapFlows.setCellStyleGenerator(new IkasanCellStyleGenerator());
		wiretapFlows.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Flow)
				{
					final Flow flow = (Flow) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ flow.getName());
					
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	wiretapFlows.removeItem(flow);
			            }
			        });
					
					wiretapFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
					for(final Component component: flow.getComponents())
					{
						deleteButton = new Button();
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	wiretapComponents.removeItem(component);
				            }
				        });
						
						wiretapComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});

		listSelectLayout.addComponent(wiretapFlows, 1, 0);
		wiretapComponents.setSizeFull();
		wiretapComponents.addContainerProperty("Component Name", String.class,  null);
		wiretapComponents.addContainerProperty("", Button.class,  null);
		wiretapComponents.setCellStyleGenerator(new IkasanCellStyleGenerator());
		wiretapComponents.setSizeFull();
		wiretapComponents.setCellStyleGenerator(new IkasanCellStyleGenerator());
		wiretapComponents.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Component)
				{
					final Component component = (Component) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ component.getName());
					
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	wiretapComponents.removeItem(component);
			            }
			        });
					
					wiretapComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		listSelectLayout.addComponent(this.wiretapComponents, 2, 0);
		
		GridLayout dateSelectLayout = new GridLayout(2, 2);
		dateSelectLayout.setColumnExpandRatio(0, 0.25f);
		dateSelectLayout.setColumnExpandRatio(1, 0.75f);
		dateSelectLayout.setSizeFull();
		this.fromDate = new PopupDateField("From date");
		this.fromDate.setResolution(Resolution.MINUTE);
		this.fromDate.setValue(this.getMidnightToday());
		dateSelectLayout.addComponent(this.fromDate, 0, 0);
		this.toDate = new PopupDateField("To date");
		this.toDate.setResolution(Resolution.MINUTE);
		this.toDate.setValue(this.getTwentyThreeFixtyNineToday());
		dateSelectLayout.addComponent(this.toDate, 0, 1);
		
		this.eventId = new TextField("Event Id");
		this.eventId.setWidth("80%");
		this.payloadContent = new TextField("Payload Content");
		this.payloadContent.setWidth("80%");
		dateSelectLayout.addComponent(this.eventId, 1, 0);
		dateSelectLayout.addComponent(this.payloadContent, 1, 1);
		
		
		GridLayout searchLayout = new GridLayout(2, 1);
		searchLayout.addComponent(searchButton, 0, 0);
		searchLayout.addComponent(clearButton, 1, 0);
		
		
		HorizontalLayout hListSelectLayout = new HorizontalLayout();
		hListSelectLayout.setHeight(100 , Unit.PIXELS);
		hListSelectLayout.setWidth("100%");
		hListSelectLayout.addComponent(listSelectLayout);
		layout.addComponent(hListSelectLayout);
		HorizontalLayout hDateSelectLayout = new HorizontalLayout();
		hDateSelectLayout.setHeight(100 , Unit.PIXELS);
		hDateSelectLayout.setWidth("100%");
		hDateSelectLayout.addComponent(dateSelectLayout);
		layout.addComponent(hDateSelectLayout);
		HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(30 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(searchLayout);
		layout.addComponent(hSearchLayout);
		HorizontalLayout hWiretapTable = new HorizontalLayout();
		hWiretapTable.setWidth("100%");
		hWiretapTable.setHeight(420, Unit.PIXELS);
		hWiretapTable.addComponent(this.wiretapTable);
		layout.addComponent(hWiretapTable);
		layout.setSizeFull();
		
		return layout;
	}
	
	protected Layout createErrorOccurencePanel()
	{
		this.errorOccurenceTable = new Table();
		this.errorOccurenceTable.setSizeFull();
		this.errorOccurenceTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.errorOccurenceTable.addContainerProperty("Module Name", String.class,  null);
		this.errorOccurenceTable.addContainerProperty("Flow Name", String.class,  null);
		this.errorOccurenceTable.addContainerProperty("Component Name", String.class,  null);
		this.errorOccurenceTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.errorOccurenceTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) {
		    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)itemClickEvent.getItemId();
		    	ErrorOccurrenceViewWindow errorOccurrenceViewWindow = new ErrorOccurrenceViewWindow(errorOccurrence);
		    
		    	UI.getCurrent().addWindow(errorOccurrenceViewWindow);
		    }
		});
		
		
		Button searchButton = new Button("Search");
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	errorOccurenceTable.removeAllItems();

            	ArrayList<String> modulesNames = null;
            	
            	if(errorOccurenceModules.getItemIds().size() > 0)
            	{
	            	modulesNames = new ArrayList<String>();
	            	for(Object module: errorOccurenceModules.getItemIds())
	            	{
	            		modulesNames.add(((Module)module).getName());
	            	}
            	}
            	
            	ArrayList<String> flowNames = null;
            	
            	if(errorOccurenceFlows.getItemIds().size() > 0)
            	{
            		flowNames = new ArrayList<String>();
            		for(Object flow: errorOccurenceFlows.getItemIds())
                	{
                		flowNames.add(((Flow)flow).getName());
                	}
            	}
            	
            	ArrayList<String> componentNames = null;
            	
            	if(errorOccurenceComponents.getItemIds().size() > 0)
            	{
            		componentNames = new ArrayList<String>();
	            	for(Object component: errorOccurenceComponents.getItemIds())
	            	{
	            		componentNames.add(((Component)component).getName());
	            	}
            	}
            	
            	if(modulesNames == null && flowNames == null && componentNames == null
            			&& !((BusinessStream)treeViewBusinessStreamCombo.getValue()).getName().equals("All"))
            	{
            		BusinessStream businessStream = ((BusinessStream)treeViewBusinessStreamCombo.getValue());
            		
            		modulesNames = new ArrayList<String>();
            		
            		for(BusinessStreamFlow flow: businessStream.getFlows())
            		{
            			modulesNames.add(flow.getFlow().getModule().getName());
            		}
            	}
         
            	List<ErrorOccurrence> errorOccurences = errorReportingService
            			.find(modulesNames, flowNames, componentNames, errorFromDate.getValue(), errorToDate.getValue());

            	for(ErrorOccurrence errorOccurrence: errorOccurences)
            	{
            		Date date = new Date(errorOccurrence.getTimestamp());
            		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            	    String timestamp = format.format(date);
            	    
            	    errorOccurenceTable.addItem(new Object[]{errorOccurrence.getModuleName(), errorOccurrence.getFlowName()
            				, errorOccurrence.getFlowElementName(), timestamp}, errorOccurrence);
            	}
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	errorOccurenceModules.removeAllItems();
            	errorOccurenceFlows.removeAllItems();
            	errorOccurenceComponents.removeAllItems();
            }
        });

		GridLayout layout = new GridLayout(1, 5);
		layout.setMargin(true);
		
		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSizeFull();
		
		errorOccurenceModules.addContainerProperty("Module Name", String.class,  null);
		errorOccurenceModules.addContainerProperty("", Button.class,  null);
		errorOccurenceModules.setSizeFull();
		errorOccurenceModules.setCellStyleGenerator(new IkasanCellStyleGenerator());
		errorOccurenceModules.setDragMode(TableDragMode.ROW);
		errorOccurenceModules.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Module)
				{
					final Module module = (Module) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ module.getName());
					
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	errorOccurenceModules.removeItem(module);
			            }
			        });
					
					errorOccurenceModules.addItem(new Object[]{module.getName(), deleteButton}, module);

					for(final Flow flow: module.getFlows())
					{
						deleteButton = new Button();
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	errorOccurenceFlows.removeItem(flow);
				            }
				        });
						
						errorOccurenceFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
						for(final Component component: flow.getComponents())
						{
							deleteButton = new Button();
							deleteButton.setIcon(deleteIcon);
							deleteButton.setStyleName(Reindeer.BUTTON_LINK);
							
							// Add the delete functionality to each role that is added
							deleteButton.addClickListener(new Button.ClickListener() 
					        {
					            public void buttonClick(ClickEvent event) 
					            {		
					            	errorOccurenceComponents.removeItem(component);
					            }
					        });
							
							errorOccurenceComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						}
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		listSelectLayout.addComponent(errorOccurenceModules, 0, 0);
		errorOccurenceFlows.addContainerProperty("Flow Name", String.class,  null);
		errorOccurenceFlows.addContainerProperty("", Button.class,  null);
		errorOccurenceFlows.setSizeFull();
		errorOccurenceFlows.setCellStyleGenerator(new IkasanCellStyleGenerator());
		errorOccurenceFlows.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Flow)
				{
					final Flow flow = (Flow) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ flow.getName());
					
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	errorOccurenceFlows.removeItem(flow);
			            }
			        });
					
					errorOccurenceFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
					for(final Component component: flow.getComponents())
					{
						deleteButton = new Button();
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	errorOccurenceComponents.removeItem(component);
				            }
				        });
						
						errorOccurenceComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});

		listSelectLayout.addComponent(errorOccurenceFlows, 1, 0);
		errorOccurenceComponents.setSizeFull();
		errorOccurenceComponents.addContainerProperty("Component Name", String.class,  null);
		errorOccurenceComponents.addContainerProperty("", Button.class,  null);
		errorOccurenceComponents.setCellStyleGenerator(new IkasanCellStyleGenerator());
		errorOccurenceComponents.setSizeFull();
		errorOccurenceComponents.setCellStyleGenerator(new IkasanCellStyleGenerator());
		errorOccurenceComponents.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Component)
				{
					final Component component = (Component) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ component.getName());
					
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	errorOccurenceComponents.removeItem(component);
			            }
			        });
					
					errorOccurenceComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		listSelectLayout.addComponent(this.errorOccurenceComponents, 2, 0);
		
		GridLayout dateSelectLayout = new GridLayout(2, 2);
		dateSelectLayout.setColumnExpandRatio(0, 0.25f);
		dateSelectLayout.setColumnExpandRatio(1, 0.75f);
		dateSelectLayout.setSizeFull();
		errorFromDate = new PopupDateField("From date");
		errorFromDate.setResolution(Resolution.MINUTE);
		errorFromDate.setValue(this.getMidnightToday());
		dateSelectLayout.addComponent(errorFromDate, 0, 0);
		errorToDate = new PopupDateField("To date");
		errorToDate.setResolution(Resolution.MINUTE);
		errorToDate.setValue(this.getTwentyThreeFixtyNineToday());
		dateSelectLayout.addComponent(errorToDate, 1, 0);
				
		
		GridLayout searchLayout = new GridLayout(2, 1);
		searchLayout.addComponent(searchButton, 0, 0);
		searchLayout.addComponent(clearButton, 1, 0);
		
		
		HorizontalLayout hListSelectLayout = new HorizontalLayout();
		hListSelectLayout.setHeight(100 , Unit.PIXELS);
		hListSelectLayout.setWidth("100%");
		hListSelectLayout.addComponent(listSelectLayout);
		layout.addComponent(hListSelectLayout);
		HorizontalLayout hDateSelectLayout = new HorizontalLayout();
		hDateSelectLayout.setHeight(50, Unit.PIXELS);
		hDateSelectLayout.setWidth("100%");
		hDateSelectLayout.addComponent(dateSelectLayout);
		layout.addComponent(hDateSelectLayout);
		HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(30 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(searchLayout);
		layout.addComponent(hSearchLayout);
		HorizontalLayout hErrorTable = new HorizontalLayout();
		hErrorTable.setWidth("100%");
		hErrorTable.setHeight(420, Unit.PIXELS);
		hErrorTable.addComponent(this.errorOccurenceTable);
		layout.addComponent(hErrorTable);
		layout.setSizeFull();
		
		return layout;
	}

	protected Layout createExclusionPanel()
	{
		this.exclusionsTable = new Table();
		this.exclusionsTable.setSizeFull();
		this.exclusionsTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.exclusionsTable.addContainerProperty("Module Name", String.class,  null);
		this.exclusionsTable.addContainerProperty("Flow Name", String.class,  null);
		this.exclusionsTable.addContainerProperty("Action", String.class,  null);
		this.exclusionsTable.addContainerProperty("Actioned By", String.class,  null);
		this.exclusionsTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.exclusionsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	ExclusionEvent exclusionEvent = (ExclusionEvent)itemClickEvent.getItemId();
		    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEvent.getErrorUri());
		    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
		    	ExclusionEventViewWindow exclusionEventViewWindow = new ExclusionEventViewWindow(exclusionEvent, errorOccurrence, serialiserFactory
		    			, action, hospitalManagementService, topologyService);
		    	
		    	exclusionEventViewWindow.addCloseListener(new Window.CloseListener()
		    	{
		            // inline close-listener
		            public void windowClose(CloseEvent e) 
		            {
		            	refreshExcludedEventsTable();
		            }
		        });
		    
		    	UI.getCurrent().addWindow(exclusionEventViewWindow);
		    }
		});
		
		
		Button refreshButton = new Button("Refresh");
		refreshButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	refreshExcludedEventsTable();
            }
        });
		

		GridLayout layout = new GridLayout(1, 2);
		layout.setMargin(true);				
		
		GridLayout searchLayout = new GridLayout(1, 1);
		searchLayout.addComponent(refreshButton, 0, 0);

		HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(30 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(searchLayout);
		layout.addComponent(hSearchLayout);
		HorizontalLayout hErrorTable = new HorizontalLayout();
		hErrorTable.setWidth("100%");
		hErrorTable.setHeight(600, Unit.PIXELS);
		hErrorTable.addComponent(this.exclusionsTable);
		layout.addComponent(hErrorTable);
		layout.setSizeFull();
		
		return layout;
	}
	
	protected Layout createActionedExclusionsPanel()
	{
		this.actionedExclusionsTable = new Table();
		this.actionedExclusionsTable.setSizeFull();
		this.actionedExclusionsTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.actionedExclusionsTable.addContainerProperty("Module Name", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Flow Name", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Action", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Actioned By", String.class,  null);
		this.actionedExclusionsTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.actionedExclusionsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	ExclusionEventAction exclusionEventAction = (ExclusionEventAction)itemClickEvent.getItemId();
		    	
		    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEventAction.getErrorUri());
		    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEventAction.getErrorUri());
		    	ActionedExclusionEventViewWindow actionExclusionEventViewWindow = new ActionedExclusionEventViewWindow(errorOccurrence, serialiserFactory
		    			, action, hospitalManagementService, topologyService);
		    
		    	UI.getCurrent().addWindow(actionExclusionEventViewWindow);
		    }
		});
		
		
		Button searchButton = new Button("Search");
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	actionedExclusionsTable.removeAllItems();

            	         
            	ArrayList<String> modulesNames = null;
            	
            	if(actionedExclusionsModules.getItemIds().size() > 0)
            	{
	            	modulesNames = new ArrayList<String>();
	            	for(Object module: actionedExclusionsModules.getItemIds())
	            	{
	            		modulesNames.add(((Module)module).getName());
	            	}
            	}
            	
            	ArrayList<String> flowNames = null;
            	
            	if(actionedExclusionsFlows.getItemIds().size() > 0)
            	{
            		flowNames = new ArrayList<String>();
            		for(Object flow: actionedExclusionsFlows.getItemIds())
                	{
                		flowNames.add(((Flow)flow).getName());
                	}
            	}
            	
            	ArrayList<String> componentNames = null;
            	
            	if(actionedExclusionsComponents.getItemIds().size() > 0)
            	{
            		componentNames = new ArrayList<String>();
	            	for(Object component: actionedExclusionsComponents.getItemIds())
	            	{
	            		componentNames.add(((Component)component).getName());
	            	}
            	}
            	
            	if(modulesNames == null && flowNames == null && componentNames == null
            			&& !((BusinessStream)treeViewBusinessStreamCombo.getValue()).getName().equals("All"))
            	{
            		BusinessStream businessStream = ((BusinessStream)treeViewBusinessStreamCombo.getValue());
            		
            		modulesNames = new ArrayList<String>();
            		
            		for(BusinessStreamFlow flow: businessStream.getFlows())
            		{
            			modulesNames.add(flow.getFlow().getModule().getName());
            		}
            	}
         
            	List<ExclusionEventAction> exclusionEventActions = hospitalManagementService.getActionedExclusions
            			(modulesNames, flowNames, actionedExclusionFromDate.getValue(), actionedExclusionToDate.getValue());

            	for(ExclusionEventAction exclusionEventAction: exclusionEventActions)
            	{
            		Date date = new Date(exclusionEventAction.getTimestamp());
            		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            	    String timestamp = format.format(date);
            	    
            	    actionedExclusionsTable.addItem(new Object[]{exclusionEventAction.getModuleName(), exclusionEventAction.getFlowName()
            				, exclusionEventAction.getAction(), exclusionEventAction.getActionedBy(), timestamp}, exclusionEventAction);
            	}

            }
        });

		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSizeFull();
		
		actionedExclusionsModules.addContainerProperty("Module Name", String.class,  null);
		actionedExclusionsModules.addContainerProperty("", Button.class,  null);
		actionedExclusionsModules.setSizeFull();
		actionedExclusionsModules.setCellStyleGenerator(new IkasanCellStyleGenerator());
		actionedExclusionsModules.setDragMode(TableDragMode.ROW);
		actionedExclusionsModules.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Module)
				{
					final Module module = (Module) t
							.getItemId();
					logger.info("sourceContainer.getText(): "
							+ module.getName());
					
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	actionedExclusionsModules.removeItem(module);
			            }
			        });
					
					actionedExclusionsModules.addItem(new Object[]{module.getName(), deleteButton}, module);

					for(final Flow flow: module.getFlows())
					{
						deleteButton = new Button();
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	actionedExclusionsFlows.removeItem(flow);
				            }
				        });
						
						actionedExclusionsFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
						for(final Component component: flow.getComponents())
						{
							deleteButton = new Button();
							deleteButton.setIcon(deleteIcon);
							deleteButton.setStyleName(Reindeer.BUTTON_LINK);
							
							// Add the delete functionality to each role that is added
							deleteButton.addClickListener(new Button.ClickListener() 
					        {
					            public void buttonClick(ClickEvent event) 
					            {		
					            	actionedExclusionsComponents.removeItem(component);
					            }
					        });
							
							actionedExclusionsComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						}
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		listSelectLayout.addComponent(actionedExclusionsModules, 0, 0);
		actionedExclusionsFlows.addContainerProperty("Flow Name", String.class,  null);
		actionedExclusionsFlows.addContainerProperty("", Button.class,  null);
		actionedExclusionsFlows.setSizeFull();
		actionedExclusionsFlows.setCellStyleGenerator(new IkasanCellStyleGenerator());
		actionedExclusionsFlows.setDropHandler(new DropHandler()
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
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	actionedExclusionsFlows.removeItem(flow);
			            }
			        });
					
					actionedExclusionsFlows.addItem(new Object[]{flow.getName(), deleteButton}, flow);
						
					for(final Component component: flow.getComponents())
					{
						deleteButton = new Button();
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						// Add the delete functionality to each role that is added
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {		
				            	actionedExclusionsComponents.removeItem(component);
				            }
				        });
						
						actionedExclusionsComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
					}
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});

		listSelectLayout.addComponent(actionedExclusionsFlows, 1, 0);
		actionedExclusionsComponents.setSizeFull();
		actionedExclusionsComponents.addContainerProperty("Component Name", String.class,  null);
		actionedExclusionsComponents.addContainerProperty("", Button.class,  null);
		actionedExclusionsComponents.setCellStyleGenerator(new IkasanCellStyleGenerator());
		actionedExclusionsComponents.setSizeFull();
		actionedExclusionsComponents.setCellStyleGenerator(new IkasanCellStyleGenerator());
		actionedExclusionsComponents.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				final DataBoundTransferable t = (DataBoundTransferable) dropEvent
	                        .getTransferable();
			
				if(t.getItemId() instanceof Component)
				{
					final Component component = (Component) t
							.getItemId();
										
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);

					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	actionedExclusionsComponents.removeItem(component);
			            }
			        });
					
					actionedExclusionsComponents.addItem(new Object[]{component.getName(), deleteButton}, component);
						
				}
				
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		listSelectLayout.addComponent(this.actionedExclusionsComponents, 2, 0);

				
		GridLayout dateSelectLayout = new GridLayout(2, 1);
		dateSelectLayout.setColumnExpandRatio(0, 0.25f);
		dateSelectLayout.setWidth("50%");
		this.actionedExclusionFromDate = new PopupDateField("From date");
		this.actionedExclusionFromDate.setResolution(Resolution.MINUTE);
		this.actionedExclusionFromDate.setValue(this.getMidnightToday());
		dateSelectLayout.addComponent(this.actionedExclusionFromDate, 0, 0);
		this.actionedExclusionToDate = new PopupDateField("To date");
		this.actionedExclusionToDate.setResolution(Resolution.MINUTE);
		this.actionedExclusionToDate.setValue(this.getTwentyThreeFixtyNineToday());
		dateSelectLayout.addComponent(this.actionedExclusionToDate, 1, 0);
				
		
		GridLayout searchLayout = new GridLayout(1, 1);
		searchLayout.addComponent(searchButton, 0, 0);		
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		
		layout.addComponent(listSelectLayout);
		HorizontalLayout hDateSelectLayout = new HorizontalLayout();
		hDateSelectLayout.setHeight(30, Unit.PIXELS);
		hDateSelectLayout.setWidth("100%");
		hDateSelectLayout.addComponent(dateSelectLayout);
		layout.addComponent(hDateSelectLayout);
		HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(30 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(searchLayout);
		layout.addComponent(hSearchLayout);
		HorizontalLayout hActionedExclusionsTableLayout = new HorizontalLayout();
		hActionedExclusionsTableLayout.setWidth("100%");
		hActionedExclusionsTableLayout.setHeight(500, Unit.PIXELS);
		hActionedExclusionsTableLayout.addComponent(this.actionedExclusionsTable);
		layout.addComponent(hActionedExclusionsTableLayout);
		layout.setSizeFull();
		
		return layout;
	}
	
	protected Layout createSystemEventPanel()
	{
		this.systemEventTable = new Table();
		this.systemEventTable.setSizeFull();
		this.systemEventTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.systemEventTable.addContainerProperty("Subject", String.class,  null);
		this.systemEventTable.addContainerProperty("Action", TextArea.class,  null);
		this.systemEventTable.addContainerProperty("Actioned By", String.class,  null);
		this.systemEventTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.systemEventTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
//		    	ExclusionEvent exclusionEvent = (ExclusionEvent)itemClickEvent.getItemId();
//		    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEvent.getErrorUri());
//		    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
//		    	ExclusionEventViewWindow exclusionEventViewWindow = new ExclusionEventViewWindow(exclusionEvent, errorOccurrence, serialiserFactory
//		    			, action, hospitalManagementService, topologyService);
//		    	
//		    	exclusionEventViewWindow.addCloseListener(new Window.CloseListener()
//		    	{
//		            // inline close-listener
//		            public void windowClose(CloseEvent e) 
//		            {
//		            	refreshExcludedEventsTable();
//		            }
//		        });
//		    
//		    	UI.getCurrent().addWindow(exclusionEventViewWindow);
		    }
		});
		
		
		Button searchButton = new Button("Search");
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	systemEventTable.removeAllItems();
            	
            	PagedSearchResult<SystemEvent> systemEvents = systemEventService.listSystemEvents(0, 10000, "timestamp", true, null, null, systemEventFromDate.getValue()
            			, systemEventToDate.getValue(), null);
            	
            	for(SystemEvent systemEvent: systemEvents.getPagedResults())
            	{
            		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            	    String timestamp = format.format(systemEvent.getTimestamp());
            	    
            	    TextArea action = new TextArea();
            	    action.setRows(3);
            	    action.setWordwrap(true);
            	    action.setValue(systemEvent.getAction());
            	    action.setSizeFull();
            	    action.setReadOnly(true);
            	    
            		systemEventTable.addItem(new Object[]{systemEvent.getSubject(), action
        				, systemEvent.getActor(), timestamp}, systemEvent);
            	}
            }
        });
		

		GridLayout layout = new GridLayout(1, 2);
		layout.setMargin(true);				
		
		GridLayout dateSelectLayout = new GridLayout(2, 2);
		dateSelectLayout.setColumnExpandRatio(0, 0.25f);
		dateSelectLayout.setWidth("50%");
		this.systemEventFromDate = new PopupDateField("From date");
		this.systemEventFromDate.setResolution(Resolution.MINUTE);
		this.systemEventFromDate.setValue(this.getMidnightToday());
		dateSelectLayout.addComponent(this.systemEventFromDate, 0, 0);
		this.systemEventToDate = new PopupDateField("To date");
		this.systemEventToDate.setResolution(Resolution.MINUTE);
		this.systemEventToDate.setValue(this.getTwentyThreeFixtyNineToday());
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

	
	protected void refreshExcludedEventsTable()
	{
		exclusionsTable.removeAllItems();
    	
    	List<ExclusionEvent> exclusionEvents = exclusionManagementService.findAll();

    	for(ExclusionEvent exclusionEvent: exclusionEvents)
    	{
    		Date date = new Date(exclusionEvent.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    	    String timestamp = format.format(date);
    	    
    	    ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
    	    
    	    String actionString = "";
    	    String actionedByString = "";
    	    
    	    if(action != null)
    	    {
    	    	actionString = action.getAction();
    	    	actionedByString = action.getActionedBy();
    	    }
    	    
    	    exclusionsTable.addItem(new Object[]{exclusionEvent.getModuleName(), exclusionEvent.getFlowName(), actionString, actionedByString,
    				timestamp}, exclusionEvent);
    	}
	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		refresh();
	}
	
	protected void refresh()
	{
		this.createTabSheet();
		this.refreshTree();
	}
	
	protected void refreshTree()
	{
		this.moduleTree.removeAllItems();
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null 
    			&& authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
    	{
			List<Server> servers = this.topologyService.getAllServers();
			
			for(Server server: servers)
			{
				Set<Module> modules = server.getModules();
	
				this.moduleTree.addItem(server);
	            this.moduleTree.setItemCaption(server, server.getName());
	            this.moduleTree.setChildrenAllowed(server, true);
	            this.moduleTree.setItemIcon(server, serverResource);
	
		        for(Module module: modules)
		        {
		        	refreshFlowStates(modules);
		        	
		            this.moduleTree.addItem(module);
		            this.moduleTree.setItemCaption(module, module.getName());
		            this.moduleTree.setParent(module, server);
		            this.moduleTree.setChildrenAllowed(module, true);
		            this.moduleTree.setItemIcon(module, moduleResource);
		            
		            Set<Flow> flows = module.getFlows();
		
		            for(Flow flow: flows)
		            {
		                this.moduleTree.addItem(flow);
		                this.moduleTree.setItemCaption(flow, flow.getName());
	                    this.moduleTree.setParent(flow, module);
		                this.moduleTree.setChildrenAllowed(flow, true);
		    			
		                String state = flowStates.get(flow.getModule().getName() + "-" + flow.getName());
		    			if(state.equals(RUNNING))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStartedResource);
		    			}
		    			else if(state.equals(RUNNING) || state.equals(RECOVERING))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStartedResource);
		    			}
		    			else if (state.equals(STOPPED_IN_ERROR))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStoppedInErrorResource);
		    			}
		    			else if (state.equals(STOPPED))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStoppedResource);
		    			}
		    			else if (state.equals(PAUSED))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowPausedResource);
		    			}
		                
		                Set<Component> components = flow.getComponents();
		                
		                for(Component component: components)
		                {
		                	this.moduleTree.addItem(component);
		                	this.moduleTree.setParent(component, flow);
		                	this.moduleTree.setItemCaption(component, component.getName());
		                	this.moduleTree.setChildrenAllowed(component, false);
		                	
		                	if(component.isConfigurable())
		                	{
		                		this.moduleTree.setItemIcon(component, this.componentConfigurableResource);
		                	}
		                	else
		                	{
		                		this.moduleTree.setItemIcon(component, this.componentResource);
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
		                    moduleTree.setItemIcon(server, serverResource);
		        		}
		                
		                moduleTree.addItem(module);
		                moduleTree.setItemCaption(module, module.getName());
		                moduleTree.setParent(module, server);
		                moduleTree.setChildrenAllowed(module, true);
		                moduleTree.setItemIcon(module, moduleResource);
		                
		                moduleTree.addItem(flow);
		                moduleTree.setItemCaption(flow, flow.getName());
		                moduleTree.setParent(flow, module);
		                moduleTree.setChildrenAllowed(flow, true);

		                String state = flowStates.get(flow.getModule().getName() + "-" + flow.getName());
		    			if(state.equals(RUNNING))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStartedResource);
		    			}
		    			else if(state.equals(RUNNING) || state.equals(RECOVERING))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStartedResource);
		    			}
		    			else if (state.equals(STOPPED_IN_ERROR))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStoppedInErrorResource);
		    			}
		    			else if (state.equals(STOPPED))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowStoppedResource);
		    			}
		    			else if (state.equals(PAUSED))
		    			{
		    				this.moduleTree.setItemIcon(flow, this.flowPausedResource);
		    			}
		                
		                Set<Component> components = flow.getComponents();
		
		                for(Component component: components)
		                {
		                	moduleTree.addItem(component);
		                	moduleTree.setParent(component, flow);
		                	moduleTree.setItemCaption(component, component.getName());
		                	moduleTree.setChildrenAllowed(component, false);
		                	
		                	if(component.isConfigurable())
		                	{
		                		moduleTree.setItemIcon(component, componentConfigurableResource);
		                	}
		                	else
		                	{
		                		moduleTree.setItemIcon(component, componentResource);
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
			if(nextItem instanceof Flow)
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
		
    	String url = "http://" + flow.getModule().getServer().getUrl() + ":" + flow.getModule().getServer().getPort()
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
	
	
	protected void refreshFlowStates(Set<Module> modules)
	{
		for(Module module: modules)
		{
			this.flowStates.putAll(this.getFlowStates(module));
		}
	}

	protected String getFlowState(Flow flow)
	{
		String url = "http://" + flow.getModule().getServer().getUrl() + ":" + flow.getModule().getServer().getPort() 
				+ flow.getModule().getContextRoot() 
				+ "/rest/moduleControl/flowState/"
				+ flow.getModule().getName() + "/"
				+ flow.getName();
		
		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
    	
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
	    WebTarget webTarget = client.target(url);
	    
	    return webTarget.request().get(String.class);
	}
	
	@SuppressWarnings("unchecked")
	protected HashMap<String, String> getFlowStates(Module module)
	{
		String url = "http://" + module.getServer().getUrl() + ":" + module.getServer().getPort() 
				+ module.getContextRoot() 
				+ "/rest/moduleControl/flowStates/"
				+ module.getName();
		
		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
    	
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
	    WebTarget webTarget = client.target(url);
	    
	    return (HashMap<String, String>)webTarget.request().get(HashMap.class);
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Action[] getActions(Object target, Object sender)
	{     
		logger.info("Getting action: " + target + " " + sender);
		
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
			Flow flow = ((Flow)target);
			
			String state = flowStates.get(flow.getModule().getName() + "-" + flow.getName());
			if(state.equals(RUNNING))
			{
				return this.flowActionsStarted;
			}
			else if(state.equals(RUNNING) || state.equals(RECOVERING))
			{
				return this.flowActionsStarted;
			}
			else if (state.equals(STOPPED) || state.equals(STOPPED_IN_ERROR))
			{
				return this.flowActionsStopped;
			}
			else if (state.equals(PAUSED))
			{
				return this.flowActionsPaused;
			}
        }
		else if(target instanceof Component)
        {
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
        Tree senderTree = ((Tree)sender);
        
        if(target != null && target instanceof Component)
        {
        	if(action.equals(CONFIGURE))
        	{
        		this.componentConfigurationWindow.populate(((Component)target));
        		UI.getCurrent().addWindow(this.componentConfigurationWindow);
        	}
        }
        else if(target != null && target instanceof Flow)
        {
        	Flow flow = ((Flow)target);
        	
	        if(action.equals(START))
	        {
	     		if(this.actionFlow(flow, "start"))
	     		{
	     			senderTree.setItemIcon(flow, this.flowStartedResource);
	     		}
	        }
	        else if(action.equals(STOP))
	        {
	        	if(this.actionFlow(flow, "stop"))
	        	{
	        		senderTree.setItemIcon(flow, this.flowStoppedResource);
	        	}
	        }
	        else if(action.equals(PAUSE))
	        {
	        	if(this.actionFlow(flow, "pause"))
	        	{
	        		senderTree.setItemIcon(flow, this.flowPausedResource);
	        	}
	        }
	        else if(action.equals(RESUME))
	        {
	        	if(this.actionFlow(flow, "resume"))
	        	{
	        		senderTree.setItemIcon(flow, this.flowStartedResource);
	        	}
	        }
	        else if(action.equals(START_PAUSE))
	        {       	
	        	if(this.actionFlow(flow, "startPause"))
	        	{
	        		senderTree.setItemIcon(flow, this.flowPausedResource);
	        	}
	        }
	        
	        this.refreshFlowStates(flow.getModule().getServer().getModules());
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
}

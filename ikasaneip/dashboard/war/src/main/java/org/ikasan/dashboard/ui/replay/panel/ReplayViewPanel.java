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
package org.ikasan.dashboard.ui.replay.panel;

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
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.event.FlowStateEvent;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.replay.component.ReplayAuditTab;
import org.ikasan.dashboard.ui.replay.component.ReplayTab;
import org.ikasan.dashboard.ui.topology.component.TopologyTab;
import org.ikasan.dashboard.ui.topology.util.TopologyTreeActionHelper;
import org.ikasan.dashboard.ui.topology.window.ComponentConfigurationWindow;
import org.ikasan.dashboard.ui.topology.window.ErrorCategorisationWindow;
import org.ikasan.dashboard.ui.topology.window.FlowConfigurationWindow;
import org.ikasan.dashboard.ui.topology.window.ServerWindow;
import org.ikasan.dashboard.ui.topology.window.StartupControlConfigurationWindow;
import org.ikasan.dashboard.ui.topology.window.WiretapConfigurationWindow;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.module.StartupControlService;
import org.ikasan.spec.replay.ReplayManagementService;
import org.ikasan.spec.replay.ReplayService;
import org.ikasan.systemevent.service.SystemEventService;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.ikasan.wiretap.service.TriggerManagementService;
import org.vaadin.teemu.VaadinIcons;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.event.Action;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
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
public class ReplayViewPanel extends Panel implements View, Action.Handler
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
    
	private  final long serialVersionUID = -6213301218439409056L;
	
	private Logger logger = Logger.getLogger(ReplayViewPanel.class);
	
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
    private final Action[] flowActionsStoppedConfigurable = new Action[] { START, START_PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION, CONFIGURE };
    private final Action[] flowActionsStartedConfigurable = new Action[] { STOP, PAUSE, STARTUP_CONTROL, ERROR_CATEGORISATION, CONFIGURE };
    private final Action[] flowActionsPausedConfigurable = new Action[] { STOP, RESUME, STARTUP_CONTROL, ERROR_CATEGORISATION, CONFIGURE };
    private final Action[] flowActionsConfigurable = new Action[] { ERROR_CATEGORISATION, CONFIGURE };
    private final Action[] componentActionsConfigurable = new Action[] { CONFIGURE, WIRETAP, ERROR_CATEGORISATION };
    private final Action[] componentActions = new Action[] { WIRETAP, ERROR_CATEGORISATION };
    private final Action[] actionsEmpty = new Action[]{};

	private Panel topologyTreePanel;
	private Tree moduleTree;
	private ComponentConfigurationWindow componentConfigurationWindow;

	private Panel tabsheetPanel;	

	private TopologyService topologyService;
	
	private StartupControlService startupControlService;

	
	private SystemEventService systemEventService;
	private ErrorCategorisationService errorCategorisationService;
	private TriggerManagementService triggerManagementService;
	private SecurityService securityService;
	
	private ConcurrentHashMap<String, String> flowStates = new ConcurrentHashMap<String, String>();
	
	private TopologyStateCache topologyCache;
	
	private TabSheet tabsheet;
	
	private PlatformConfigurationService platformConfigurationService;
	
	private VerticalLayout popupViewLayout = new VerticalLayout();
	
	private TopologyTab currentTab;
	
	private HashMap<String, AbstractComponent> tabComponentMap = new HashMap<String, AbstractComponent>();
	
	private ReplayManagementService<ReplayEvent, ReplayAudit, ReplayAuditEvent> replayManagementService;
	private ReplayService<ReplayEvent, ReplayAuditEvent> replayService;
	
	private boolean initialised = false;
	
	private FlowConfigurationWindow flowConfigurationWindow;

	private TopologyTreeActionHelper topologyTreeActionHelper;
	
	
	public ReplayViewPanel(TopologyService topologyService, ComponentConfigurationWindow componentConfigurationWindow,
			 SystemEventService systemEventService, ErrorCategorisationService errorCategorisationService, 
			 TriggerManagementService triggerManagementService, TopologyStateCache topologyCache, StartupControlService startupControlService,
			 PlatformConfigurationService platformConfigurationService, SecurityService securityService, ReplayManagementService<ReplayEvent,
			 ReplayAudit, ReplayAuditEvent> replayManagementService, ReplayService<ReplayEvent, ReplayAuditEvent> replayService, FlowConfigurationWindow flowConfigurationWindow)
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
		this.replayManagementService = replayManagementService;
		if(this.replayManagementService == null)
		{
			throw new IllegalArgumentException("replayManagementService cannot be null!");
		}
		this.replayService = replayService;
		if(this.securityService == null)
		{
			throw new IllegalArgumentException("replayService cannot be null!");
		}
		this.flowConfigurationWindow = flowConfigurationWindow;
		if(this.flowConfigurationWindow == null)
		{
			throw new IllegalArgumentException("flowConfigurationWindow cannot be null!");
		}

	}

	protected void init()
	{
		this.tabsheetPanel = new Panel();
		this.tabsheetPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		this.tabsheetPanel.setSizeFull();
		
		this.createModuleTreePanel();
		
		this.setWidth("100%");
		this.setHeight("100%");
		
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
				|| authentication.hasGrantedAuthority(SecurityConstants.REPLAY_ADMIN)
				|| authentication.hasGrantedAuthority(SecurityConstants.REPLAY_WRITE)
				|| authentication.hasGrantedAuthority(SecurityConstants.REPLAY_READ)))
    	{
    		
    		final ReplayTab replayTab = new ReplayTab(this.replayManagementService, this.replayService, 
    				this.platformConfigurationService);

    		replayTab.createLayout();
			
    		tabsheet.addTab(replayTab, "Replay");
    		
    	}
    	
    	if(authentication != null 
    			&& (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
				|| authentication.hasGrantedAuthority(SecurityConstants.REPLAY_ADMIN)
				|| authentication.hasGrantedAuthority(SecurityConstants.REPLAY_WRITE)
				|| authentication.hasGrantedAuthority(SecurityConstants.REPLAY_READ)))
    	{
    		
    		final ReplayAuditTab replayAuditTab = new ReplayAuditTab(this.replayManagementService, this.replayService, 
    				this.platformConfigurationService);

    		replayAuditTab.createLayout();
			
    		tabsheet.addTab(replayAuditTab, "Replay Audit");
    		
    	}

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
 		
 		this.refreshTree();
		
		layout.addComponent(this.moduleTree);

		this.topologyTreePanel.setContent(layout);
	}
	
	

	

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		if(!this.initialised)
		{
			init();
			this.initialised = true;
		}
		
		EventBus eventBus = ((IkasanUI)UI.getCurrent()).getEventBus();   
    	eventBus.register(this);
    	
		refresh();
	}
	
	protected void refresh()
	{
		if(this.tabsheet == null)
		{
			this.refreshTree();
			this.createTabSheet();
		}
	}
	
	protected void refreshTree()
	{
		this.moduleTree.removeAllItems();
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);

		this.topologyTreeActionHelper = new TopologyTreeActionHelper(authentication);

		
		List<Server> servers = this.topologyService.getAllServers();
		
		logger.debug("Trying to load tree for " + servers.size());
		
		for(Server server: servers)
		{	
			Set<Module> modules = server.getModules();

			this.moduleTree.addItem(server);
        	
            this.moduleTree.setItemCaption(server, server.getName());
            this.moduleTree.setItemIcon(server, VaadinIcons.SERVER);
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
	    			
	                if(flow.isConfigurable())
                	{
	            		ReplayViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.ELLIPSIS_CIRCLE);
                	}
                	else
                	{
                		ReplayViewPanel.this.moduleTree.setItemIcon(flow, VaadinIcons.ELLIPSIS_CIRCLE_O);
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
	                		ReplayViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG);
	                	}
	                	else
	                	{
	                		ReplayViewPanel.this.moduleTree.setItemIcon(component, VaadinIcons.COG_O);
	                	}
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

	@Override
	public Action[] getActions(Object target, Object sender)
	{
		if(target instanceof Server)
		{
			return this.topologyTreeActionHelper.getServerActions();
		}
		else if(target instanceof Module)
		{
			return this.topologyTreeActionHelper.getModuleActions();
		}
		else if(target instanceof Flow)
		{
			Flow flow = ((Flow)target);

			String state = this.topologyCache.getState(flow.getModule().getName() + "-" + flow.getName());

			return this.topologyTreeActionHelper.getFlowActions(state, flow.isConfigurable());
		}
		else if(target instanceof Component)
		{
			return this.topologyTreeActionHelper.getComponentActions(((Component)target).isConfigurable()
					, ((Component)target).getFlow().isConfigurable());
		}

		return this.topologyTreeActionHelper.getActionsEmpty();
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
        	else if(action.equals(WIRETAP))
        	{
        		UI.getCurrent().addWindow(new WiretapConfigurationWindow((Component)target
        			, triggerManagementService));
        	}
        	else if(action.equals(ERROR_CATEGORISATION))
        	{
        		Component component = (Component)target;
        		
        		UI.getCurrent().addWindow(new ErrorCategorisationWindow(component.getFlow().getModule().getServer(),
        				component.getFlow().getModule(), component.getFlow(), component, errorCategorisationService));
        	}
        }
        else if(target != null && target instanceof Flow)
        {
        	Flow flow = ((Flow)target);
        	
        	if(action.equals(CONFIGURE))
        	{
        		this.flowConfigurationWindow.populate(flow);
        		UI.getCurrent().addWindow(this.flowConfigurationWindow);
        	}
        	else if(action.equals(START))
	        {
        		this.actionFlow(flow, "start");
	        }
	        else if(action.equals(STOP))
	        {
	        	this.actionFlow(flow, "stop");
	        }
	        else if(action.equals(PAUSE))
	        {
	        	this.actionFlow(flow, "pause");
	        }
	        else if(action.equals(RESUME))
	        {
	        	this.actionFlow(flow, "resume");
	        }
	        else if(action.equals(START_PAUSE))
	        {   
	        	this.actionFlow(flow, "startPause");
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


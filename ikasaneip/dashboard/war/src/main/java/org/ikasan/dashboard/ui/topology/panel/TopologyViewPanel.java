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
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.topology.window.ComponentConfigurationWindow;
import org.ikasan.dashboard.ui.topology.window.NewBusinessStreamWindow;
import org.ikasan.dashboard.ui.topology.window.WiretapPayloadViewWindow;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.BusinessStreamFlowKey;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.ikasan.wiretap.dao.WiretapDao;

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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
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
	/**
	 * 
	 */
	private static final long serialVersionUID = -6213301218439409056L;
	
	private Logger logger = Logger.getLogger(TopologyViewPanel.class);
	
	private static final Action START = new Action("Start");
    private static final Action STOP = new Action("Stop");
    private static final Action VIEW_DIAGRAM = new Action("View Diagram");
    private static final Action CONFIGURE = new Action("Configure");
    private static final Action PAUSE = new Action("Pause");
    private static final Action RESTART = new Action("Re-start");
    private static final Action DISABLE = new Action("Disable");
    private static final Action DETAILS = new Action("Details");
    private static final Action WIRETAP = new Action("Wiretap");
    private static final Action[] serverActions = new Action[] { DETAILS };
    private static final Action[] moduleActions = new Action[] { DETAILS, VIEW_DIAGRAM };
    private static final Action[] flowActions = new Action[] { DETAILS, STOP, START, PAUSE, RESTART, DISABLE };
    private static final Action[] componentActionsConfigurable = new Action[] { DETAILS, CONFIGURE, WIRETAP };
    private static final Action[] componentActions = new Action[] { DETAILS, WIRETAP };
    private static final Action[] actionsEmpty = new Action[]{};
	
	private ThemeResource serverResource = new ThemeResource("images/server.jpg");
	private ThemeResource moduleResource = new ThemeResource("images/module.png");
	private ThemeResource flowResource = new ThemeResource("images/flow.png");
	private ThemeResource componentResource = new ThemeResource("images/component.png");
	private ThemeResource componentConfigurableResource = new ThemeResource("images/component_configurable.png");

	private TopologyService topologyService;
	private Panel topologyTreePanel;
	private Tree moduleTree;
	private ComponentConfigurationWindow componentConfigurationWindow;

	private Panel businessStreamPanel;
	private Table businessStreamTable;
	
	private Panel wiretapPanel;
	private Table wiretapTable;
	
	private ComboBox businessStreamCombo;
	
	private WiretapDao wiretapDao;
	
	public TopologyViewPanel(TopologyService topologyService, ComponentConfigurationWindow componentConfigurationWindow,
			 WiretapDao wiretapDao)
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
			throw new IllegalArgumentException("componentConfigurationWindow cannot be null!");
		}

		init();
	}

	protected void init()
	{
		this.createModuleTreePanel();
		this.createTabSheet();

		this.setWidth("100%");
		this.setHeight("100%");

		GridLayout layout = new GridLayout(3, 1);
		layout.setMargin(true);
		layout.setSizeFull();
		
		layout.addComponent(this.topologyTreePanel, 0, 0);
		layout.addComponent(this.businessStreamPanel, 2, 0);
		layout.setColumnExpandRatio(0, 0.3725f);
		layout.setColumnExpandRatio(1, 0.025f);
		layout.setColumnExpandRatio(2, 0.5725f);

		this.setContent(layout);
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
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(true);
		layout.setSizeFull();

		layout.addComponent(this.moduleTree);

		this.topologyTreePanel.setContent(layout);
	}
	
	protected void createTabSheet()
	{		
		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();

		VerticalLayout tab1 = new VerticalLayout();
		tab1.setSizeFull();
		tab1.addComponent(createBusinessStreamPanel());
		tabsheet.addTab(tab1, "Business Stream");
		
		VerticalLayout tab2 = new VerticalLayout();
		tab2.setSizeFull();
		tab2.addComponent(createWiretapPanel());
		tabsheet.addTab(tab2, "Wiretaps");

		this.businessStreamPanel.setContent(tabsheet);
	}

	protected Layout createBusinessStreamPanel()
	{
		this.businessStreamPanel = new Panel("Topology Stuff");
		this.businessStreamPanel.setStyleName("dashboard");
		this.businessStreamPanel.setSizeFull();

		this.businessStreamTable = new Table();
		this.businessStreamTable.addContainerProperty("Flow Name", String.class,  null);
		this.businessStreamTable.addContainerProperty("", Button.class,  null);
		this.businessStreamTable.setSizeFull();
		
		this.businessStreamTable.setDragMode(TableDragMode.ROW);
		this.businessStreamTable.setDropHandler(new DropHandler()
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
    	
    	controlsLayout.addComponent(newButton, 2, 0);
    	
    	Button deleteButton = new Button("Delete");
    	deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    	deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	
            }
        });

    	controlsLayout.addComponent(deleteButton, 3, 0);
    	
    	layout.addComponent(controlsLayout);
    	layout.setExpandRatio(controlsLayout, .07f);
		layout.addComponent(this.businessStreamTable);
		layout.setExpandRatio(this.businessStreamTable, .93f);
		
		return layout;
	}
	
	protected Layout createWiretapPanel()
	{
		this.wiretapPanel = new Panel("Topology Stuff");
		this.wiretapPanel.setStyleName("dashboard");
		this.wiretapPanel.setSizeFull();

		this.wiretapTable = new Table();
		this.wiretapTable.setSizeFull();
		this.wiretapTable.addContainerProperty("Module Name", String.class,  null);
		this.wiretapTable.addContainerProperty("Flow Name", String.class,  null);
		this.wiretapTable.addContainerProperty("Component Name", String.class,  null);
		this.wiretapTable.addContainerProperty("Timestamp", String.class,  null);
		
		this.wiretapTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) {
		    	WiretapEvent<String> wiretapEvent = (WiretapEvent<String>)itemClickEvent.getItemId();
		    	WiretapPayloadViewWindow wiretapPayloadViewWindow = new WiretapPayloadViewWindow(wiretapEvent.getEvent());
		    
		    	UI.getCurrent().addWindow(wiretapPayloadViewWindow);
		    }
		});
		
		
		Button searchButton = new Button("Search");
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	PagedSearchResult<WiretapEvent> events = wiretapDao.findWiretapEvents(0, 10000, "timestamp", false, null
            			, null, null, null, null, null, null, null);

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

		VerticalLayout layout = new VerticalLayout();
		
		layout.addComponent(searchButton);
		layout.setExpandRatio(searchButton, 0.05f);
		layout.addComponent(wiretapTable);
		layout.setExpandRatio(wiretapTable, 0.95f);
		layout.setSizeFull();
		
		return layout;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		this.moduleTree.removeAllItems();

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
	                this.moduleTree.setItemIcon(flow, flowResource);
	                
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
		
		this.businessStreamCombo.removeAllItems();

		List<BusinessStream> businessStreams = this.topologyService.getAllBusinessStreams();
		
		for(BusinessStream businessStream: businessStreams)
		{
			this.businessStreamCombo.addItem(businessStream);
			this.businessStreamCombo.setItemCaption(businessStream, businessStream.getName());
		}
	}

	/* (non-Javadoc)
	 * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Action[] getActions(Object target, Object sender)
	{        
		if(target instanceof Server)
        {
            return TopologyViewPanel.serverActions;
        }
		else if(target instanceof Module)
        {
            return TopologyViewPanel.moduleActions;
        }
		else if(target instanceof Flow)
        {
            return TopologyViewPanel.flowActions;
        }
		else if(target instanceof Component)
        {
			if(((Component)target).isConfigurable())
			{
				return TopologyViewPanel.componentActionsConfigurable;
			}
			else
			{
				return TopologyViewPanel.componentActions;
			}
        }
        else
        {
            return actionsEmpty;
        }
	}

	/* (non-Javadoc)
	 * @see com.vaadin.event.Action.Handler#handleAction(com.vaadin.event.Action, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void handleAction(Action action, Object sender, Object target)
	{
		logger.info("Action: " + action.getCaption());
        logger.info("Target: " + target.getClass().getName());
        logger.info("Sender: " + sender.getClass().getName());
        
        if(action.equals(TopologyViewPanel.CONFIGURE))
        {
        	this.componentConfigurationWindow.populate(((Component)target).getConfigurationId());
        	UI.getCurrent().addWindow(this.componentConfigurationWindow);
        }
	}
}

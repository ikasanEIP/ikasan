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
package org.ikasan.dashboard.ui.monitor.component;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.IkasanUI;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.ikasan.dashboard.ui.framework.event.FlowStateEvent;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class MonitorPanel extends Panel implements View
{
	private static final long serialVersionUID = -3174124965136021440L;

	private Logger logger = Logger.getLogger(MonitorPanel.class);
	
	private TopologyService topologyService;
	private Server server;
	private FilterTable filterTable;
	private IndexedContainer cont;
	private HashMap<String, String> stateMap;
	private Label statusLabel = new Label();
	
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
	 * @param topologyService
	 */
	public MonitorPanel(TopologyService topologyService, Server server)
	{
		super();
		this.topologyService = topologyService;
		this.server = server;
		
		EventBus eventBus = ((IkasanUI)UI.getCurrent()).getEventBus();    	
    	eventBus.register(this);
	}

	public Component buildServerComponent() 
    {
    	Panel component = new Panel();
    	component.setSizeFull();
//    	component.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
    	GridLayout layout = new GridLayout();
    	layout.setSizeFull();
    	layout.setMargin(true);
    	
    	Label serverNameLabel = new Label(server.getName());
    	serverNameLabel.setStyleName(ValoTheme.LABEL_LARGE);
    	serverNameLabel.setWidth("100%");
    	Label serverDescriptionLabel = new Label(server.getDescription());
    	serverDescriptionLabel.setWidth("100%");
    	serverDescriptionLabel.setStyleName(ValoTheme.LABEL_LARGE);
    	Label serverUrlLabel = new Label(server.getUrl() + ":" + server.getPort());
    	serverUrlLabel.setStyleName(ValoTheme.LABEL_LARGE);
    	serverUrlLabel.setWidth("100%");
    	
    	layout.addComponent(serverNameLabel);
    	layout.addComponent(serverDescriptionLabel);
    	layout.addComponent(serverUrlLabel);
    	
    	statusLabel.setCaptionAsHtml(true);
    	
    	
    	
    	layout.addComponent(statusLabel);
    	layout.setComponentAlignment(statusLabel, Alignment.MIDDLE_CENTER);
    	
    	buildFilterTable(); 
    	    	
    	component.setContent(layout);
    	
        Component contentWrapper = createContentWrapper(component, buildFilterTable());
        
        contentWrapper.addStyleName("top10-revenue");
        return contentWrapper;
    }
	
	protected void setStatusLabel()
	{
		MonitorIcons icon = MonitorIcons.CHECK_CIRCLE_O;
    	icon.setSizePixels(64);
    	icon.setColor("green");
    	
		for(String key: stateMap.keySet())
		{
			if(key.startsWith(server.getName()))
			{
				String state = this.stateMap.get(key);
				
				if(state.equals(STOPPED) || state.equals(RECOVERING) || state.equals(STOPPED_IN_ERROR))
				{
					icon = MonitorIcons.EXCLAMATION_CIRCLE_O;
					icon.setSizePixels(64);
			    	icon.setColor("red");
			    	
			    	return;
				}
			}
		}
		
    	
    	statusLabel.setCaption(icon.getHtml());
	}
	
	protected Component createContentWrapper(final Component small, final Component large) 
    {
        final CssLayout slot = new CssLayout();
        slot.setWidth("100%");
        slot.addStyleName("monitor-panel-slot");

        final CssLayout card1 = new CssLayout();
        card1.setWidth("100%");
        card1.addStyleName(ValoTheme.LAYOUT_CARD);
        
        final CssLayout card2 = new CssLayout();
        card2.setWidth("100%");
        card2.addStyleName(ValoTheme.LAYOUT_CARD);

        final HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(large.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        large.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuItem max = tools.addItem("", VaadinIcons.EXPAND, new Command() 
        {

            @Override
            public void menuSelected(final MenuItem selectedItem)
            {
                if (!slot.getStyleName().contains("max")) 
                {
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    slot.removeAllComponents();
                    card2.removeAllComponents();
                    card2.addComponents(toolbar, large);
                    slot.addComponents(card2);
                    toggleMaximized(slot, true);
                } else 
                {
                    slot.removeStyleName("max");
                    selectedItem.setIcon(FontAwesome.EXPAND);
                    toggleMaximized(slot, false);
                    card1.removeAllComponents();
                    card1.addComponents(toolbar, small);
                    slot.removeAllComponents();
                    slot.addComponents(card1);
                }
            }
        });
        max.setStyleName("icon-only");
        MenuItem root = tools.addItem("", VaadinIcons.COG, null);
        root.addItem("Configure", new Command() 
        {
            @Override
            public void menuSelected(final MenuItem selectedItem) 
            {
                Notification.show("Not implemented in this demo");
            }
        });
        root.addSeparator();
        root.addItem("Close", new Command() 
        {
            @Override
            public void menuSelected(final MenuItem selectedItem) 
            {
                Notification.show("Not implemented in this demo");
            }
        });

        toolbar.addComponents(caption, tools);
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        card1.addComponents(toolbar, small);
//        card2.addComponents(toolbar, large);
        slot.addComponent(card1);
        return slot;
    }
	
	protected FilterTable buildFilterTable() 
	{
		this.filterTable = new FilterTable(this.server.getName() + " Flow States");
        filterTable.setSizeFull();
        filterTable.setContainerDataSource(buildContainer());
        filterTable.setFilterBarVisible(true);
        
        filterTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        filterTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        filterTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        filterTable.addStyleName(ValoTheme.TABLE_SMALL);
        
        filterTable.setSizeFull();
        
        filterTable.setCellStyleGenerator(new CustomTable.CellStyleGenerator() 
        {
			@Override
			public String getStyle(CustomTable source, Object itemId, Object propertyId) 
			{
				
				Flow flow = (Flow)itemId;
				
				String state = stateMap.get(flow.getModule().getName() + "-" + flow.getName());
				
				if (propertyId == null) 
				{
				// Styling for row			
					if(state != null && state.equals(RUNNING))
	    			{
						return "ikasan-green-small";
	    			}
	    			else if(state != null && state.equals(RECOVERING))
	    			{
	    				return "ikasan-orange-small";
	    			}
	    			else if (state != null && state.equals(STOPPED))
	    			{
	    				return "ikasan-red-small";
	    			}
	    			else if (state != null && state.equals(STOPPED_IN_ERROR))
	    			{
	    				return "ikasan-red-small";
	    			}
	    			else if (state != null && state.equals(PAUSED))
	    			{
	    				return "ikasan-orange-small";
	    			}
				}
				
				if(state != null && state.equals(RUNNING))
    			{
					return "ikasan-green-small";
    			}
    			else if(state != null && state.equals(RECOVERING))
    			{
    				return "ikasan-orange-small";
    			}
    			else if (state != null && state.equals(STOPPED))
    			{
    				return "ikasan-red-small";
    			}
    			else if (state != null && state.equals(STOPPED_IN_ERROR))
    			{
    				return "ikasan-red-small";
    			}
    			else if (state != null && state.equals(PAUSED))
    			{
    				return "ikasan-orange-small";
    			}
				
				return "ikasan-small";
			}
		});
        
        return filterTable;
    }
	
	protected Container buildContainer() {
        cont = new IndexedContainer();

        cont.addContainerProperty("Module Name", String.class, null);
        cont.addContainerProperty("Flow Name", String.class, null);
        cont.addContainerProperty("Flow State", String.class, null);

        return cont;
    }
	
	protected void toggleMaximized(final Component panel, final boolean maximized) 
    {
        if (maximized) 
        {

        	panel.setVisible(true);
            panel.addStyleName("max");
            
        } 
        else 
        {
            panel.removeStyleName("max");
        }
    }
	
	public void populate(HashMap<String, String> stateMap)
	{		
		for(Module module: server.getModules())
		{
			for(Flow flow: module.getFlows())
			{
				String state = stateMap.get(flow.getModule().getName() + "-" + flow.getName());
				
				if(state == null)
				{
					state = "unknown";
				}
				
				Item item = this.cont.getItem(flow);
							
				if(item != null)
				{
					item.getItemProperty("Flow State").setValue(state);
				}
				else
				{
					item = this.cont.addItem(flow);
					
					if(item != null && flow != null)
					{
						item.getItemProperty("Module Name").setValue(flow.getModule().getName());
						item.getItemProperty("Flow Name").setValue(flow.getName());
						item.getItemProperty("Flow State").setValue(state);
					}
				}
			}
		}
	}	
	
	@Subscribe
	public void receiveAlertEvent(final FlowStateEvent event)
	{
		logger.info("received event: " + event);
		UI.getCurrent().access(new Runnable() 
		{
            @Override
            public void run() 
            {
            	VaadinSession.getCurrent().getLockInstance().lock();
        		try 
        		{
        			stateMap = event.getFlowStateMap();
        			setStatusLabel();
        			populate(event.getFlowStateMap());
        		} 
        		finally 
        		{
        			VaadinSession.getCurrent().getLockInstance().unlock();
        		}
            	
            	UI.getCurrent().push();	
            }
        });	
	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		TopologyStateCache cache = (TopologyStateCache)VaadinSession.getCurrent().getAttribute
			(DashboardSessionValueConstants.TOPOLOGY_STATE_CACHE);
		
		stateMap = cache.getStateMap();
		setStatusLabel();
		populate(stateMap);
		
	}

}

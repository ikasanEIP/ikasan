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
package org.ikasan.dashboard.ui.framework.component;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.IkasanUI;
import org.ikasan.dashboard.ui.framework.event.FlowStateEvent;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class FlowStateTable extends DashboardTable
{
	private Logger logger = Logger.getLogger(FlowStateTable.class);
	
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
    
	private TopologyService topologyService;
	private HashMap<String, String> stateMap;
	
	/**
	 * Constructor
	 * 
	 * @param caption
	 */
	public FlowStateTable(String caption, TopologyService topologyService)
	{
		super(caption);
		
		this.topologyService = topologyService;
		if(topologyService == null)
		{
			throw new IllegalArgumentException("topologyService cannot be null!");
		}
		
		init();
	}
	
	protected void init()
	{
		addContainerProperty("Module Name", String.class,  null);
        addContainerProperty("Flow Name", String.class,  null);
        addContainerProperty("State", String.class,  null);
                
        this.setCellStyleGenerator(new Table.CellStyleGenerator() 
        {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) 
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
	    				return "ikasan-indigo-small";
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
    				return "ikasan-indigo-small";
    			}
				
				return "ikasan-small";
			}
		});
                
        EventBus eventBus = ((IkasanUI)UI.getCurrent()).getEventBus();
    	
    	eventBus.register(this);
	}
	
	public void populate(HashMap<String, String> stateMap)
	{
		this.stateMap = stateMap;
		this.removeAllItems();
		
		List<Server> servers = topologyService.getAllServers();
		
		for(Server server: servers)
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
					
					this.addItem(new Object[] {flow.getModule().getName(),
							flow.getName(), state}, flow);
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
}

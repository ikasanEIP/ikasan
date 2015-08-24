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
package org.ikasan.dashboard.ui.framework.cache;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.Broadcaster;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class TopologyStateCache
{
	private Logger logger = Logger.getLogger(TopologyStateCache.class);
	
	private TopologyService topologyService;
	private HashMap<String, String> stateMap;
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private TopologyCacheRefreshTask task = new TopologyCacheRefreshTask();
	
	
	
	/**
	 * @param topologyService
	 */
	public TopologyStateCache(TopologyService topologyService)
	{
		super();
		this.topologyService = topologyService;
		
		logger.info("TopologyStateCache constructor");
		executor.scheduleAtFixedRate(task, 30, 60, TimeUnit.SECONDS);
		
	}

	public String getState(String key)
	{
		return this.stateMap.get(key);
	}



	private class TopologyCacheRefreshTask implements Runnable
	{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			update();
		}
		
	}
	
	protected void update()
	{
		stateMap = new HashMap<String, String>();
				
		logger.info("Synchronising topology state cache.");
		List<Server> servers = topologyService.getAllServers();
		
		logger.info("Number of servers to synch: " + servers.size());
		for(Server server: servers)
		{
			logger.info("Synchronising server: " + server.getName());
			for(Module module: server.getModules())
			{
				logger.info("Synchronising module: " + module.getName());
				
				HashMap<String, String> results = getFlowStates(module);
				
				for(String key: results.keySet())
				{
					stateMap.put(key, results.get(key));
				}
			}
		}
		
		Broadcaster.broadcast(stateMap);
		
		logger.info("Finished synchronising topology state cache.");
	}
	
	public void update(String key, String value)
	{
		stateMap.put(key, value);
		
		Broadcaster.broadcast(stateMap);
	}
	
	@SuppressWarnings("unchecked")
	protected HashMap<String, String> getFlowStates(Module module)
	{
		HashMap<String, String> results = new HashMap<String, String>();
		try
		{
			String url = "http://" + module.getServer().getUrl() + ":" + module.getServer().getPort() 
					+ module.getContextRoot() 
					+ "/rest/moduleControl/flowStates/"
					+ module.getName();
			
	    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");
	    	
	    	ClientConfig clientConfig = new ClientConfig();
	    	clientConfig.register(feature) ;
	    	
	    	Client client = ClientBuilder.newClient(clientConfig);
	    	
	    	logger.info("Calling URL: " + url);
	    	WebTarget webTarget = client.target(url);
		    
	    	results = (HashMap<String, String>)webTarget.request().get(HashMap.class);
	    	
	    	logger.info("results: " + results);
		}
		catch(Exception e)
		{
			logger.info("caught exception: " + e.getMessage());
			e.printStackTrace();
			return new HashMap<String, String>();
		}
	    
	    return results;
	}

	/**
	 * @return the stateMap
	 */
	public HashMap<String, String> getStateMap()
	{
		return stateMap;
	}
}

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
package org.ikasan.topology.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.dao.TopologyDao;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.model.FilterComponent;
import org.ikasan.topology.model.FilterComponentKey;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Notification;
import org.ikasan.topology.model.RoleFilter;
import org.ikasan.topology.model.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikasan.topology.exception.DiscoveryException;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class TopologyServiceImpl implements TopologyService
{
	private static Logger logger = Logger.getLogger(TopologyServiceImpl.class);
	
	private TopologyDao topologyDao;

	/**
	 * Constructor
	 * 
	 * @param topologyDao
	 */
	public TopologyServiceImpl(TopologyDao topologyDao)
	{
		this.topologyDao = topologyDao;
		if(this.topologyDao == null)
		{
			throw new IllegalArgumentException("topologyDao cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllServers()
	 */
	@Override
	public List<Server> getAllServers()
	{
		return this.topologyDao.getAllServers();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#save(org.ikasan.topology.model.Server)
	 */
	@Override
	public void save(Server server)
	{
		this.topologyDao.save(server);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllModules()
	 */
	@Override
	public List<Module> getAllModules()
	{
		return this.topologyDao.getAllModules();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#save(org.ikasan.topology.model.Module)
	 */
	@Override
	public void save(Module module)
	{
		this.topologyDao.save(module);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllFlows()
	 */
	@Override
	public List<Flow> getAllFlows()
	{
		return this.topologyDao.getAllFlows();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#save(org.ikasan.topology.model.Flow)
	 */
	@Override
	public void save(Flow flow)
	{
		this.topologyDao.save(flow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllBusinessStreams()
	 */
	@Override
	public List<BusinessStream> getAllBusinessStreams()
	{
		return this.topologyDao.getAllBusinessStreams();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#saveBusinessStream(org.ikasan.topology.model.BusinessStream)
	 */
	@Override
	public void saveBusinessStream(BusinessStream businessStream)
	{
		this.topologyDao.saveBusinessStream(businessStream);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getBusinessStreamsByUserId(java.lang.Long)
	 */
	@Override
	public List<BusinessStream> getBusinessStreamsByUserId(Long userId)
	{
		return this.topologyDao.getBusinessStreamsByUserId(userId);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getFlowsByServerIdAndModuleId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<Flow> getFlowsByServerIdAndModuleId(Long serverId, Long moduleId)
	{
		return this.topologyDao.getFlowsByServerIdAndModuleId(serverId, moduleId);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#deleteBusinessStreamFlow(org.ikasan.topology.model.BusinessStreamFlow)
	 */
	@Override
	public void deleteBusinessStreamFlow(BusinessStreamFlow businessStreamFlow)
	{
		this.topologyDao.deleteBusinessStreamFlow(businessStreamFlow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getModuleByName(java.lang.String)
	 */
	@Override
	public Module getModuleByName(String name)
	{
		return this.topologyDao.getModuleByName(name);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getBusinessStreamsByUserId(java.util.List)
	 */
	@Override
	public List<BusinessStream> getBusinessStreamsByUserId(List<Long> ids)
	{
		return this.topologyDao.getBusinessStreamsByUserId(ids);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#discover()
	 */
	@Override
	public void discover(IkasanAuthentication authentication) throws DiscoveryException
	{
		List<Server> servers =  this.topologyDao.getAllServers();
		
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	// Firstly sort out the server module relationships
		for(Server server: servers)
		{
			List<Module> modules = this.topologyDao.getAllModules();	
			
			Set<Module> moduleSet = new HashSet<Module>();
			
			for(Module module: modules)
			{	
				List<String> discoveredFlowNames = new ArrayList<String>();
				
				String url = server.getUrl() + ":" + server.getPort() 
						+ module.getContextRoot() 
						+ "/rest/discovery/flows/"
						+ module.getName();
				
			    WebTarget webTarget = client.target(url);
			    
			    JsonArray flowResponse = null;
			    
			    try
			    {
			    	flowResponse = webTarget.request().get(JsonArray.class);
			    }
			    catch(Exception e)
			    {
			    	// We may not find the module on the server so just move on to the next module.
			    	logger.debug("Caught exception attempting to discover module with the following URL: " + url 
			    			+ ". Ignoring and moving on to next module. Exception message: " + e.getMessage());
			    	continue;
			    }
			    
			    logger.debug("Successfully discovered module using URL: " + url 
		    			+ ". Server =  " + server);
			    
			    module.setServer(server);

			    moduleSet.add(module);
			    
			    server.setModules(moduleSet);
			    
			    this.topologyDao.save(server);
			}
		}
		
		// Now sort out the flows on servers
		for(Server server: servers)
		{
			List<Module> modules = this.topologyDao.getAllModules();	
			
			Set<Module> moduleSet = new HashSet<Module>();
			
			for(Module module: modules)
			{	
				List<String> discoveredFlowNames = new ArrayList<String>();
				
				String url = server.getUrl() + ":" + server.getPort() 
						+ module.getContextRoot() 
						+ "/rest/discovery/flows/"
						+ module.getName();
				
			    WebTarget webTarget = client.target(url);
			    
			    JsonArray flowResponse = null;
			    
			    try
			    {
			    	flowResponse = webTarget.request().get(JsonArray.class);
			    }
			    catch(Exception e)
			    {
			    	// We may not find the module on the server so just move on to the next module.
			    	logger.debug("Caught exception attempting to discover module with the following URL: " + url 
			    			+ ". Ignoring and moving on to next module. Exception message: " + e.getMessage());
			    	continue;
			    }
			    
			    logger.debug("Successfully discovered module using URL: " + url 
		    			+ ". Server =  " + server);
			    
			    Set<Flow> flowSet = new HashSet<Flow>();
			    
			    for(JsonValue flowValue: flowResponse)
			    { 
			    	List<String> discoveredComponentNames = new ArrayList<String>();
			    	
		    		Flow flow;
		    		
					try
					{
						flow = mapper.readValue(
								 flowValue.toString(), Flow.class);
						
						discoveredFlowNames.add(flow.getName());
						
						for(Component component: flow.getComponents())
						{
							discoveredComponentNames.add(component.getName());
						}
					} 
					catch (Exception e)
					{
						throw new DiscoveryException(e);
					}
					
					Set<Component> components = flow.getComponents();
					
					Flow dbFlow = this.topologyDao.getFlowByServerIdModuleIdAndFlowname
						(server.getId(), module.getId(), flow.getName());
					
					logger.info("Loading dbFlow using: serverId= " + server.getId() + " moduleId = " + module.getId()
							+ " flow name = " + flow.getName());
					logger.info("Loaded dbFlow: " + dbFlow);
					
					if(dbFlow != null)
					{
						dbFlow.setOrder(flow.getOrder());
						flow = dbFlow;
					}
					
					flow.setModule(module);
					
					flowSet.add(flow);
					
					logger.debug("Saving flow: " + flow);
					this.topologyDao.save(flow);
					
					Set<Component> componentSet = new HashSet<Component>();
					
					for(Component component: components)
					{
						component = getComponent(flow.getComponents(), component);
						component.setFlow(flow);
						
						logger.debug("Saving component: " + component.getName());

						this.topologyDao.save(component);
						
						componentSet.add(component);
					}	
					
					flow.setComponents(componentSet);
					
					this.topologyDao.save(flow);
					this.cleanUpComponents(server.getId(), module.getId(), flow.getName(), discoveredComponentNames);
			    }
			    
			    module.setFlows(flowSet);
			    
			    this.topologyDao.save(module);
			    
			    this.cleanUpFlows(module, server.getId(), module.getId(), discoveredFlowNames);
			}
		}
	}
	
	
	protected void cleanUpFlows(Module module, Long serverId, Long moduleId, List<String> discoveredFlowNames)
	{
		List<Flow> dbFlows = this.topologyDao.getFlowsByServerIdAndModuleId(serverId, moduleId);
				
		for(Flow flow: dbFlows)
		{
			if(!discoveredFlowNames.contains(flow.getName()))
			{
				Set<Component> copyComponents =  new HashSet<Component>();
		
				for(Component component: flow.getComponents())
				{
					copyComponents.add(component);
				}
				
				for(Component component: copyComponents)
				{	
					flow.getComponents().remove(component);
			
					component.setFlow(null);
					this.topologyDao.delete(component);
				}
				
				module.getFlows().remove(flow);
				flow.getModule().getFlows().remove(flow);
				flow.setModule(null);
				
				// we need to delete any references to the flow before deleting it. 
				this.topologyDao.deleteBusinessStreamFlowByFlowId(flow.getId());
				
				this.topologyDao.delete(flow);
			}
		}
	}
	
	protected void cleanUpComponents(Long serverId, Long moduleId, String flowName, List<String> discoveredComponentNames)
	{
		logger.debug("Getting components to delete:" + serverId + " " + moduleId + " " + flowName );
		
		for (String s : discoveredComponentNames)
		{
			logger.debug(s);
		}
		
		List<Component> components = this.topologyDao.getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn
				(serverId, moduleId, flowName, discoveredComponentNames);
				
		Set<Component> copyComponents =  new HashSet<Component>();
		
		for(Component component: components)
		{
			copyComponents.add(component);
		}
		
		for(Component component: copyComponents)
		{
			logger.debug("Cleaning up component:" + component);
			
			component.getFlow().getComponents().remove(component);
			component.setFlow(null);
			
			// we need to delete any references to the component before deleting it. 
			this.topologyDao.deleteFilterComponentsByComponentId(component.getId());
			
			this.topologyDao.delete(component);
		}
	}
	
	protected Component getComponent(Set<Component> components, Component component)
	{
		Iterator<Component> componentsItr = components.iterator();
		
		while(componentsItr.hasNext())
		{
			Component c = componentsItr.next();
			
			if(c.getName().trim().equals(component.getName().trim()))
			{
				c.setConfigurationId(component.getConfigurationId());
				return c;
			}
		}
		
		return component;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#deleteBusinessStream(org.ikasan.topology.model.BusinessStream)
	 */
	@Override
	public void deleteBusinessStream(BusinessStream businessStream)
	{
		this.topologyDao.deleteBusinessStream(businessStream);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#createFilter(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public Filter createFilter(String name, String description, String createdBy, Set<Component> components)
	{
		Filter filter = new Filter(name, description, createdBy);
		filter.setName(name);
		filter.setDescription(description);
		
		this.topologyDao.saveFilter(filter);
		
		Set<FilterComponent> filterComponents = new HashSet<FilterComponent>();
		
		for(Component component: components)
		{
			FilterComponent fc = new FilterComponent(new FilterComponentKey(filter.getId(), component.getId()));
			fc.setComponent(component);
			
			filterComponents.add(fc);
		}
		
		filter.setComponents(filterComponents);
		
		this.topologyDao.saveFilter(filter);
		
		return filter;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllFilters()
	 */
	@Override
	public List<Filter> getAllFilters()
	{
		return this.topologyDao.getAllFilters();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#saveFilter(org.ikasan.topology.model.Filter)
	 */
	@Override
	public void saveFilter(Filter filter)
	{
		filter.setUpdatedDateTime(new Date(System.currentTimeMillis()));
		this.topologyDao.saveFilter(filter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#deleteFilter(org.ikasan.topology.model.Filter)
	 */
	@Override
	public void deleteFilter(Filter filter)
	{
		this.topologyDao.deleteFilter(filter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#saveRoleFilter(org.ikasan.topology.model.RoleFilter)
	 */
	@Override
	public void saveRoleFilter(RoleFilter roleFilter)
	{
		this.topologyDao.saveRoleFilter(roleFilter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getRoleFilters(java.lang.Long)
	 */
	@Override
	public List<RoleFilter> getRoleFilters(List<Long> roleIds)
	{
		return this.topologyDao.getRoleFiltersByRoleId(roleIds);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#deleteRoleFilter(org.ikasan.topology.model.RoleFilter)
	 */
	@Override
	public void deleteRoleFilter(RoleFilter roleFilter)
	{
		this.topologyDao.deleteRoleFilter(roleFilter);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getRoleFilterByFilterId(java.lang.Long)
	 */
	@Override
	public RoleFilter getRoleFilterByFilterId(Long filterId)
	{
		return this.topologyDao.getRoleFilterByFilterId(filterId);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#deleteFilterComponents(java.lang.Long)
	 */
	@Override
	public void deleteFilterComponents(Long filterId)
	{
		this.topologyDao.deleteFilterComponentsByFilterId(filterId);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getFilterByName(java.lang.String)
	 */
	@Override
	public Filter getFilterByName(String name)
	{
		return this.topologyDao.getFilterByName(name);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#save(org.ikasan.topology.model.Notification)
	 */
	@Override
	public void save(Notification notification)
	{
		this.topologyDao.save(notification);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#delete(org.ikasan.topology.model.Notification)
	 */
	@Override
	public void delete(Notification notification)
	{
		this.topologyDao.delete(notification);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getNotificationByName(java.lang.String)
	 */
	@Override
	public Notification getNotificationByName(String name)
	{
		return this.topologyDao.getNotificationByName(name);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllNotifications()
	 */
	@Override
	public List<Notification> getAllNotifications()
	{
		return this.topologyDao.getAllNotifications();
	}
}

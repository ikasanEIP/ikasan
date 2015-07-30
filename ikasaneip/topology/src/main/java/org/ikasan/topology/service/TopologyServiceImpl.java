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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.ws.rs.NotFoundException;
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
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
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
    	
		for(Server server: servers)
		{
			List<Module> modules = this.topologyDao.getAllModules();
			
			for(Module module: modules)
			{
				String url = "http://" + server.getUrl() + ":" + server.getPort() 
						+ module.getContextRoot() 
						+ "/rest/discovery/flows/"
						+ module.getName();
				
			    WebTarget webTarget = client.target(url);
			    
			    JsonArray flowResponse = null;
			    
			    try
			    {
			    	flowResponse = webTarget.request().get(JsonArray.class);
			    }
			    catch(NotFoundException e)
			    {
			    	// We may not find the module on the server so just move on to the next module.
			    	continue;
			    }
			    
			    module.setServer(server);
			    
			    this.topologyDao.save(module);
			    
//		    	List<Flow> dbFlows = topologyDao.getFlowsByServerIdAndModuleId
//						(server.getId(), module.getId());
//		    	
//				for(Flow dbFlow: dbFlows)
//				{
//					for(Component component: dbFlow.getComponents())
//					{
//						this.topologyDao.delete(component);
//					}
//					
//					this.topologyDao.delete(dbFlow);
//				}

			    
			    for(JsonValue flowValue: flowResponse)
			    { 
		    		Flow flow;
		    		
					try
					{
						flow = mapper.readValue(
								 flowValue.toString(), Flow.class);
					} 
					catch (Exception e)
					{
						throw new DiscoveryException(e);
					}
					
					Set<Component> components = flow.getComponents();
					
					Flow dbFlow = this.topologyDao.getFlowsByServerIdModuleIdAndFlowname
						(server.getId(), module.getId(), flow.getName());
					
					if(dbFlow != null)
					{
						flow = dbFlow;
					}
					
					flow.setModule(module);
					
					this.topologyDao.save(flow);
											
					for(Component component: components)
					{
						component = getComponent(flow.getComponents(), component);
						component.setFlow(flow);
						
						logger.info("Saving component: " + component.getName());

						this.topologyDao.save(component);
					}								    	
			    }
			}
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
}

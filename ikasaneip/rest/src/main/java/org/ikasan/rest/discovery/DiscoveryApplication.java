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
package org.ikasan.rest.discovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.ikasan.rest.IkasanRestApplication;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Path("/discovery")
public class DiscoveryApplication extends IkasanRestApplication
{
	private static Logger logger = Logger.getLogger(DiscoveryApplication.class);
	
	@Autowired
	private ModuleContainer moduleContainer;

	/**
	 * @param hospitalService
	 */
	public DiscoveryApplication() 
	{
		super();
	}

	/**
	 * Method to get the flows associated with a module.
	 * 
	 * @param context
	 * @param moduleName
	 * @return List of Flows
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/flows/{moduleName}")
	@Produces(MediaType.APPLICATION_JSON)	
	public List<org.ikasan.topology.model.Flow> getFlows(@Context SecurityContext context, @PathParam("moduleName") String moduleName)
	{
		if(!context.isUserInRole("WebServiceAdmin"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
		
		List<org.ikasan.topology.model.Flow> flows = new ArrayList<org.ikasan.topology.model.Flow>();
		
		Module<Flow> module = moduleContainer.getModule(moduleName);
		
		org.ikasan.topology.model.Module topologyModule = new org.ikasan.topology.model.Module(moduleName, moduleName, module.getDescription(), 
				"", null, "");
		
		
		for(Flow flow: module.getFlows())
		{
			
			org.ikasan.topology.model.Flow topologyFlow = new org.ikasan.topology.model.Flow(flow.getName(), "description", topologyModule);
			
			flows.add(topologyFlow);
			
			Set<org.ikasan.topology.model.Component> components 
				= new HashSet<org.ikasan.topology.model.Component>();
			
			int order = 0;
			
			for(FlowElement<?> flowElement: flow.getFlowElements())
			{
				
				org.ikasan.topology.model.Component component = new org.ikasan.topology.model.Component();
				component.setName(flowElement.getComponentName());
				if(flowElement.getDescription() != null)
				{
					component.setDescription(flowElement.getDescription());
				}
				else
				{
					component.setDescription("No description.");
				}
				
				if(flowElement.getFlowComponent() instanceof ConfiguredResource)
				{
					component.setConfigurationId(((ConfiguredResource)flowElement.getFlowComponent()).getConfiguredResourceId());
					component.setConfigurable(true);
				}
				else
				{
					component.setConfigurable(false);
				}

				component.setOrder(order++);
				components.add(component);
			}
			
			topologyFlow.setComponents(components);
		}
		
		return flows;	
	}

	/**
	 * Method to get the flows associated with a module.
	 * 
	 * @param context
	 * @param moduleName
	 * @return List of Flows
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/components/{moduleName}/{flowName}")
	@Produces(MediaType.APPLICATION_JSON)	
	public List<org.ikasan.topology.model.Component> getComponents(@Context SecurityContext context, @PathParam("moduleName") String moduleName,
			 @PathParam("flowName") String flowName)
	{
		if(!context.isUserInRole("WebServiceAdmin") || !context.isUserInRole("ALL"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
		
		List<org.ikasan.topology.model.Component> components 
			= new ArrayList<org.ikasan.topology.model.Component>();
		
		Module<Flow> module = moduleContainer.getModule(moduleName);
		
		Flow flow = module.getFlow(flowName);
		
		for(FlowElement<?> flowElement: flow.getFlowElements())
		{
			org.ikasan.topology.model.Component component = new org.ikasan.topology.model.Component();
			component.setName(flowElement.getComponentName());
			if(flowElement.getDescription() != null)
			{
				component.setDescription(flowElement.getDescription());
			}
			else
			{
				component.setDescription("No description.");
			}
			
			if(flowElement.getFlowComponent() instanceof ConfiguredResource)
			{
				component.setConfigurationId(((ConfiguredResource)flowElement.getFlowComponent()).getConfiguredResourceId());
				component.setConfigurable(true);
			}
			else
			{
				component.setConfigurable(false);
			}

			components.add(component);
		}
		
		return components;	
	}
}

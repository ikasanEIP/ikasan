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

import org.ikasan.module.converter.ModuleConverter;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
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
	private static Logger logger = LoggerFactory.getLogger(DiscoveryApplication.class);
	
	@Autowired
	private ModuleContainer moduleContainer;

    private ModuleConverter converter = new ModuleConverter();

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
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}

		Module<Flow> module = moduleContainer.getModule(moduleName);

        return new ArrayList<>(converter.convert(module).getFlows());

    }

	/**
	 * Method to get the components associated with a flow.
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
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
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

			if(flowElement.getFlowElementInvoker() instanceof  ConfiguredResource)
            {
                component.setInvokerConfigurationId(((ConfiguredResource)flowElement.getFlowElementInvoker()).getConfiguredResourceId());
                component.setInvokerConfigurable(true);
            }
            else
            {
                component.setInvokerConfigurable(false);
            }

			components.add(component);
		}
		
		return components;	
	}
}

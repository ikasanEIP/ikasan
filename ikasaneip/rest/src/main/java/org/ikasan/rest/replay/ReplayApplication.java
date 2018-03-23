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
package org.ikasan.rest.replay;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.rest.IkasanRestApplication;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.Serialiser;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Path("/replay")
public class ReplayApplication extends IkasanRestApplication
{
	private static Logger logger = LoggerFactory.getLogger(ReplayApplication.class);
	
	/** stopped state string constant */
    private static String STOPPED = "stopped";
    
    /** stoppedInError state string constant */
    private static String STOPPED_IN_ERROR = "stoppedInError";
	
	@Autowired
	private ModuleContainer moduleContainer;

	/**
	 * @param
	 */
	public ReplayApplication()
	{
		super();
	}

	/**
	 * TODO: work out how to get annotation security working.
	 * 
	 * @param context
	 * @param moduleName
	 * @param flowName
	 * @param event
	 * @return
	 */
	@PUT
	@Path("/eventReplay/{moduleName}/{flowName}")
	@Consumes("application/octet-stream")	
	public Response replay(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
			byte[] event)
	{
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			return Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build();
		}
		
		try
		{
			Module<Flow> module = moduleContainer.getModule(moduleName);
			
			if(module == null)
			{
				throw new RuntimeException("Could not get module from module container using name:  " + moduleName);
			}
			
			Flow flow = module.getFlow(flowName);
			
			if(flow == null)
			{
				throw new RuntimeException("Could not get flow from module container using name:  " + flowName);
			}
			
			if(flow.getState().equals(STOPPED) || flow.getState().equals(STOPPED_IN_ERROR))
			{
				throw new RuntimeException("Events cannot be replayed when the flow that is being replayed to is in a " +
						flow.getState() + " state.  Module[" + moduleName +"] Flow[" + flowName + "]");
			}
			
			FlowConfiguration flowConfiguration = flow.getFlowConfiguration();
			
			ResubmissionService resubmissionService = flowConfiguration.getResubmissionService();
			
			if(resubmissionService == null)
			{
				throw new RuntimeException("The resubmission service on the flow you are resubmitting to is null. This is most likely due to " +
						"the resubmission service not being set on the flow factory for the flow you are resubmitting to.");
			}
			
			Serialiser serialiser = flow.getSerialiserFactory().getDefaultSerialiser();
				
			Object deserialisedEvent = serialiser.deserialise(event);
			
			logger.debug("deserialisedEvent " + deserialisedEvent);
			
			resubmissionService.onResubmission(deserialisedEvent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			logger.error("An error has occurred trying to replay an event: ", e);

			return Response.status(Response.Status.NOT_FOUND).type("text/plain")
	                .entity("An error has occurred on the server when trying to replay the event. " + e.getMessage()).build();
		}
		
		return Response.ok("Event replayed!").build();
	}
	
}

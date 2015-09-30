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
package org.ikasan.rest.wiretap;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.ikasan.rest.IkasanRestApplication;
import org.ikasan.trigger.model.Trigger;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Module application implementing the REST contract
 */
@Path("/wiretap")
public class WiretapApplication extends IkasanRestApplication
{
	private static Logger logger = Logger.getLogger(WiretapApplication.class);
	
	@Autowired
    private JobAwareFlowEventListener jobAwareFlowEventListener;
    
    public WiretapApplication()
    {
    }

    @PUT
	@Path("/createTrigger/{moduleName}/{flowName}/{flowElementName}/{relationship}/{jobType}")
    @Consumes("application/octet-stream")
    public Response createTrigger(@Context SecurityContext context, @PathParam("moduleName") String moduleName, 
    		@PathParam("flowName") String flowName, @PathParam("flowElementName") String flowElementName, @PathParam("relationship") String relationship, 
    		@PathParam("jobType") String jobType, String timeToLive)
    {
    	if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
    	
    	HashMap<String, String> params = new HashMap<String, String>();
    	
    	if(timeToLive != null && timeToLive.length() > 0)
    	{
    		params.put("timeToLive", timeToLive);
    	}
    	
        Trigger trigger = new Trigger(moduleName, flowName, relationship, jobType, flowElementName, params);
        
        try
        {
        	this.jobAwareFlowEventListener.addDynamicTrigger(trigger);
        }
        catch (Exception e)
        {
        	throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("An error has occurred trying to create a new trigger: " + e.getMessage()).build());
        }
        
        return Response.ok("Trigger successfully created!").build();
    }

    @PUT
	@Path("/deleteTrigger")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTrigger(@Context SecurityContext context, Long triggerId)
    {
    	if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
    	
    	try
        {
        	this.jobAwareFlowEventListener.deleteDynamicTrigger(triggerId);
        }
        catch (Exception e)
        {
        	throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("An error has occurred trying to delete a trigger: " + e.getMessage()).build());
        }
        
        return Response.ok("Trigger successfully deleted!").build();
    }

}

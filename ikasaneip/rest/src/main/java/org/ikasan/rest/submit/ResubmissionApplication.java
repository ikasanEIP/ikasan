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
package org.ikasan.rest.submit;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.ikasan.hospital.service.HospitalService;
import org.ikasan.rest.IkasanRestApplication;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Path("/resubmission")
public class ResubmissionApplication extends IkasanRestApplication
{
	private static Logger logger = Logger.getLogger(ResubmissionApplication.class);
	
	@Autowired
	private HospitalService hospitalService;

	/**
	 * Constructor
     */
	public ResubmissionApplication()
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
	@Path("/resubmit/{moduleName}/{flowName}/{errorUri}")
	@Consumes("application/octet-stream")	
	public Response resubmit(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
			@PathParam("errorUri") String errorUri, byte[] event)
	{
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			return Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build();
		}
		
		try
		{
			logger.debug("Re-submitting event " + errorUri);
			this.hospitalService.resubmit(moduleName, flowName, errorUri, event, context.getUserPrincipal());
		}
		catch (Exception e)
		{
				e.printStackTrace();

			return Response.status(Response.Status.NOT_FOUND).type("text/plain")
	                .entity("An error has occurred on the server when trying to resubmit the event. " + e.getMessage()).build();
		}
		
		return Response.ok("Event resubmitted!").build();
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
	@Path("/ignore/{moduleName}/{flowName}/{errorUri}")
	@Consumes("application/octet-stream")	
	public Response ignore(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
			@PathParam("errorUri") String errorUri, byte[] event)
	{
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			return Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build();
		}
		
		try
		{
			logger.debug("Ignoring event " + errorUri);
			this.hospitalService.ignore(moduleName, flowName, errorUri, event, context.getUserPrincipal());
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return Response.status(Response.Status.NOT_FOUND).type("text/plain")
	                .entity("An error has occurred on the server when trying to ignore the event. " + e.getMessage()).build();
		}
		
		return Response.ok("Event resubmitted!").build();
	}
}

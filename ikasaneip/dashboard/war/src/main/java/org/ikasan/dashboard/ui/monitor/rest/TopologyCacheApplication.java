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
package org.ikasan.dashboard.ui.monitor.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Module application implementing the REST contract
 */
@Path("/topologyCache")
public class TopologyCacheApplication
{
	private static Logger logger = Logger.getLogger(TopologyCacheApplication.class);
    
	@Autowired
	private TopologyStateCache topologyStateCache;
	
    /**
     * Registers the applications we implement and the Spring-Jersey glue
     */
    public TopologyCacheApplication()
    {
//    	this.topologyStateCache = topologyStateCache;
//    	if(this.topologyStateCache == null)
//    	{
//    		throw new IllegalArgumentException("topologyStateCache cannot be null!!");
//    	}
    }

    @PUT
	@Path("/updateCache/{moduleName}/{flowName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTrigger(@PathParam("moduleName") String moduleName, 
    		@PathParam("flowName") String flowName, String state)
    {
        logger.info("Updating topology state cache: ModuleName: " 
        		+ moduleName + ", FlowName: " + flowName + ", State: " + state);
        
        String key = moduleName + "-" + flowName;
        
        this.topologyStateCache.update(key, state);

        return Response.ok("State cache successfully updated!").build();
    }
    
    @GET
	@Path("/test")
	public Response savePayment() {

		String result = "test";

		return Response.status(200).entity(result).build();

	}
    
    public static final void main(String[] arge)
    {
    	try
		{
			String url = "http://svc-stewmi:8380/ikasan-dashboard/rest/topologyCache/updateCache/Sample Scheduled Module/Demo Exclusion Scheduled Flow";
		
	    	
	    	ClientConfig clientConfig = new ClientConfig();
//	    	clientConfig.register(feature) ;
	    	
	    	Client client = ClientBuilder.newClient(clientConfig);
	    	
	    	logger.info("Calling URL: " + url);
	    	WebTarget webTarget = client.target(url);
		    
	    	Response response = webTarget.request().put(Entity.entity("stopped",MediaType.APPLICATION_JSON));

	    	System.out.println(response);
		}
		catch(Exception e)
		{
			logger.info("caught exception: " + e.getMessage());
			e.printStackTrace();
		}
    }

}

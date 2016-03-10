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
package org.ikasan.replay.service;

import java.net.MalformedURLException;

import javax.annotation.Resource;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.ikasan.replay.dao.ReplayDao;
import org.ikasan.replay.model.ReplayEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/replay-conf.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
public class ReplayServiceTest extends JerseyTest
{

	@Resource ReplayDao replayDao;
	
	@Before
	public void addReplayEvents()
	{
		for(int i=0; i<100; i++)
		{
			ReplayEvent replayEvent = new ReplayEvent("errorUri", "event".getBytes(), "moduleName", "flowName");
			
	        
			this.replayDao.saveOrUpdate(replayEvent);
		}
	}
	
	@Path("rest/replay/{moduleName}/{flowName}/")
    public static class HelloResource 
    {
        @PUT
        public Response getHello(@PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName, byte[] data) 
        {
        	System.out.println(new String(data));
        	System.out.println(moduleName);
        	System.out.println(flowName);
        	
        	return Response.ok("Event resubmitted!").build();
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(HelloResource.class).property("contextConfigLocation", "classpath:replay-conf.xml")
        		.property("contextConfigLocation", "classpath:substitute-components.xml")
        		.property("contextConfigLocation", "classpath:mock-components.xml")
        		.property("contextConfigLocation", "classpath:hsqldb-config.xml");
    }

    @Test
    public void test() throws MalformedURLException 
    {
//        Response response = target("hello").request().get();
//        String hello = response.readEntity(String.class);
//        Assert.assertEquals("Hello World!", hello);
//        response.close();
    	
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("", "");
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
    	System.out.println(super.getBaseUri().toURL() + "rest/replay");
		
		
	    WebTarget webTarget = client.target(super.getBaseUri().toURL() + "rest/replay/moduleName/flowName");
	    Response response = webTarget.request().put(Entity.entity("test data".getBytes(), MediaType.APPLICATION_OCTET_STREAM));
    	
	    System.out.println(response.getStatus());
    }


}

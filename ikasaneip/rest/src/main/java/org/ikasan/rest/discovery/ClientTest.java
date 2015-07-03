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

import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ClientTest
{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException
	{
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("stewmi", "Mum&Dad2015");
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
		String url = "http://svc-stewmi:8380/gloss-referencemarketDataTgt/rest/discovery/flows/gloss-referencemarketDataTgt";

		ObjectMapper mapper = new ObjectMapper();
		
		try
    	{
	    WebTarget webTarget = client.target(url);
	    JsonArray response = webTarget.request().get(JsonArray.class);
	    	
	    for(JsonValue value: response)
	    {
	    	 
	    	Flow flow =  mapper.readValue(
	    			 value.toString(), Flow.class);
	    	System.out.println(flow);
	    	
//	    	url = "http://svc-stewmi:8380/gloss-referencemarketDataTgt/rest/discovery/components/gloss-referencemarketDataTgt/" + flow.getName();
//	    	System.out.println("Flow: " + flow);
//	    	
//	    	webTarget = client.target(url);	    	
//	    	
//    		JsonArray componentResponse = webTarget.request().get(JsonArray.class);
//    		
//    		for(JsonValue componentValue: componentResponse)
//		    {
//		    	 
//		    	Component component =  mapper.readValue(
//		    			componentValue.toString(), Component.class);
//		    	System.out.println(component);
//		    } 
	    	
	    	for(Component component: flow.getComponents())
	    	{
	    		System.out.println(component);
	    	}
	    }
	    
    	}
    	catch(NotFoundException e)
    	{
    		System.out.println("Caught exception: " + e);
    	}
	}
		
		

}

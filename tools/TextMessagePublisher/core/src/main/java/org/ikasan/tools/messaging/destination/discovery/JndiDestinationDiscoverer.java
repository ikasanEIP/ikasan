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
package org.ikasan.tools.messaging.destination.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.ikasan.tools.messaging.destination.DestinationHandle;

import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class JndiDestinationDiscoverer implements
		DestinationDiscoverer {

	private Properties jndiEnvironment;
    private List<String> parentPaths;
    
    private Logger logger = Logger.getLogger(JndiDestinationDiscoverer.class);
    
	public JndiDestinationDiscoverer(Properties jndiEnvironment,
			List<String> parentPaths) {
		super();
		this.jndiEnvironment = jndiEnvironment;
		this.parentPaths = parentPaths;
	}

	public List<DestinationHandle> findDestinations() {
		List<DestinationHandle> result = new ArrayList<DestinationHandle>();
		
		try{
			Context ctx = new InitialContext(jndiEnvironment);
			for (String parentPath : parentPaths){
				NamingEnumeration<NameClassPair> list = ctx.list(parentPath);
				while(list.hasMore()){
	                NameClassPair next = list.next();
	                String name = next.getName();
	                
	                String fullPath = parentPath+"/"+name;
	                Object lookup = ctx.lookup(parentPath+"/"+name);
	                
	                fullPath = fullPath.replaceAll("/"," ").trim().replaceAll(" ", ".");
	               
	                
	                
	                if (!(lookup instanceof javax.jms.Destination)){
	                    throw new RuntimeException("Only expecting to find Destination under ["+parentPath+"]");
	                }
	                
	                result.add(new DestinationHandle(fullPath, (Destination)lookup));
	                Collections.sort(result);
	            }
			}
		} catch (NamingException namingException){
			logger.error(namingException);
		}
		
		
		return result;
	}

    
}

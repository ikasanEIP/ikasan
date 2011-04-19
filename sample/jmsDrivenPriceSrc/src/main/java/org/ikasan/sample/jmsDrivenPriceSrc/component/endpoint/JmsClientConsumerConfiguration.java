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
package org.ikasan.sample.jmsDrivenPriceSrc.component.endpoint;

/**
 * Implementation of a consumer which manages the tech and 
 * receives messages via the tech listener.
 *
 * @author Ikasan Development Team
 */
public class JmsClientConsumerConfiguration
{
    protected static String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";
    protected static String PROVIDER_URL = "java.naming.provider.url";
    protected static String FACTORY_URL_PKGS = "java.naming.factory.url.pkgs";

    private String initialContextFactory = "org.jnp.interfaces.NamingContextFactory";
    private String providerUrl = "svc-trade01JMSd:15104";
    private String factoryUrl = "org.jnp.interfaces:org.jboss.naming";
    private String connectionFactory = "ConnectionFactory";
    private boolean durable = true;
    private String username = "defaultJMSAdmin";
    private String password = "cm12Trade01";
    private String destination = "/topic/cmi2.submit.trax.gateway";
    
    
    public String getConnectionFactory()
    {
        return connectionFactory;
    }
    public void setConnectionFactory(String connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getDestination()
    {
        return destination;
    }
    public void setDestination(String destination)
    {
        this.destination = destination;
    }
    public String getInitialContextFactory()
    {
        return initialContextFactory;
    }
    public void setInitialContextFactory(String initialContextFactory)
    {
        this.initialContextFactory = initialContextFactory;
    }
    public String getProviderUrl()
    {
        return providerUrl;
    }
    public void setProviderUrl(String providerUrl)
    {
        this.providerUrl = providerUrl;
    }
    public String getFactoryUrl()
    {
        return factoryUrl;
    }
    public void setFactoryUrl(String factoryUrl)
    {
        this.factoryUrl = factoryUrl;
    }
    public boolean isDurable()
    {
        return durable;
    }
    public void setDurable(boolean durable)
    {
        this.durable = durable;
    }
    
    
}

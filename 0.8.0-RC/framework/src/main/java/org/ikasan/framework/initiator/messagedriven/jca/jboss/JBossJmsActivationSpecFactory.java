/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.initiator.messagedriven.jca.jboss;

import javax.resource.spi.ResourceAdapter;

import org.springframework.beans.BeanWrapper;
import org.springframework.jms.listener.endpoint.DefaultJmsActivationSpecFactory;
import org.springframework.jms.listener.endpoint.JmsActivationSpecConfig;

/**
 * Implement a JBoss specific version of the activation spec population.
 * 
 * @author Ikasan Development Team
 */
public class JBossJmsActivationSpecFactory extends DefaultJmsActivationSpecFactory
{
    /** JBoss activation spec class name */
    private final String jbossDefaultActivationSpecClassName 
        = "org.jboss.resource.adapter.jms.inflow.JmsActivationSpec";

    /**
     * This implementation specifically tries to load the JBoss JMSActivationSpec.
     * @param adapter
     * @return Class - for the JMS activation spec
     */
    @Override
    protected Class<?> determineActivationSpecClass(ResourceAdapter adapter) 
    {
        try 
        {
            return adapter.getClass().getClassLoader().loadClass(jbossDefaultActivationSpecClassName);
        }
        catch (ClassNotFoundException ex) 
        {
            throw new IllegalStateException("Failed to load ActivationSpec class [" 
                + jbossDefaultActivationSpecClassName + "]", ex);
        }
    }

    /**
     * This implementation detects and applies JBoss extended 
     * activation spec settings.
     * @param BeanWrapper 
     * @param JmsActivationSpecConfig 
     */
    @Override
    protected void populateActivationSpecProperties(BeanWrapper bw, JmsActivationSpecConfig config)
    {
        super.populateActivationSpecProperties(bw, config);
        if(config instanceof JBossJmsActivationSpecConfig)
        {
            populateJBossActivationSpecProperties(bw, (JBossJmsActivationSpecConfig) config);
        }
    }

    /**
     * JBoss specific JMS activation spec settings.
     * 
     * @param bw
     * @param config
     */
    private void populateJBossActivationSpecProperties(BeanWrapper bw, JBossJmsActivationSpecConfig config)
    {
        if(bw.isWritableProperty("providerAdapterJNDI"))
        {
            bw.setPropertyValue("providerAdapterJNDI", config.getProviderAdapterJNDI());
        }
        
        if(bw.isWritableProperty("user"))
        {
            bw.setPropertyValue("user", config.getUser());
        }

        if(bw.isWritableProperty("password"))
        {
            bw.setPropertyValue("password", config.getPassword());
        }

        if(bw.isWritableProperty("maxSession"))
        {
            bw.setPropertyValue("maxSession", new Integer(config.getMaxConcurrency()));
        }

        if(bw.isWritableProperty("maxMessages"))
        {
            bw.setPropertyValue("maxMessages", new Integer(config.getPrefetchSize()));
        }

        if(bw.isWritableProperty("keepAlive"))
        {
            bw.setPropertyValue("keepAlive", Integer.valueOf(config.getKeepAlive()));
        }

        if(bw.isWritableProperty("reconnectInterval"))
        {
            bw.setPropertyValue("reconnectInterval", Integer.valueOf(config.getReconnectInterval()));
        }

        if(config.isSubscriptionDurable())
        {
            if(bw.isWritableProperty("subscriptionDurability"))
            {
                bw.setPropertyValue("subscriptionDurability", "Durable");
            }
        }

        if(bw.isWritableProperty("subscriptionName"))
        {
            bw.setPropertyValue("subscriptionName", config.getDurableSubscriptionName());
        }

        if(bw.isWritableProperty("useDLQ"))
        {
            bw.setPropertyValue("useDLQ", Boolean.valueOf(config.isUseDLQ()));
        }

        if(bw.isWritableProperty("dlqHandler"))
        {
            bw.setPropertyValue("dlqHandler", config.getDlqHandler());
        }

        if(bw.isWritableProperty("dlqUser"))
        {
            bw.setPropertyValue("dlqUser", config.getDlqUser());
        }

        if(bw.isWritableProperty("dlqPassword"))
        {
            bw.setPropertyValue("dlqPassword", config.getDlqPassword());
        }

        if(bw.isWritableProperty("dlqClientId"))
        {
            bw.setPropertyValue("dlqClientId", config.getDlqClientId());
        }

        if(bw.isWritableProperty("forceTransacted"))
        {
            bw.setPropertyValue("forceTransacted", Boolean.valueOf(config.isForceTransacted()));
        }
    }

}

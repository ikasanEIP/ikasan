/*
 * $Id
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

import static org.junit.Assert.*;

import javax.jms.Session;

import org.junit.Test;

/**
 * This test class supports the <code>JBossJmsActicationSpecConfig</code> class.
 * 
 * @author Ikasan Development Team
 */
public class JBossJmsActivationSpecConfigTest
{
    /**
     * Test all expected setters and getters for JBossJmsActivationSpecConfig.
     */
    @Test
    public void test_standardSettersAndGetters()
    {
        //
        // create instance
        JBossJmsActivationSpecConfig spec = new JBossJmsActivationSpecConfig();
        
        //
        // invoke all setters
        
        // standard JMS attributes
        spec.setDestinationName("destinationName");
        spec.setMessageSelector("messageSelector");
        spec.setAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        spec.setClientId("clientId");
        spec.setSubscriptionDurable(true);
        spec.setDurableSubscriptionName("durableSubscriptionName");
        
        // Spring JMS attributes
        spec.setPubSubDomain(true);
        spec.setMaxConcurrency(1);
        spec.setPrefetchSize(1);
        
        // JBoss specific JMS attributes
        spec.setProviderAdapterJNDI("providerAdapterJNDI");
        spec.setUser("user");
        spec.setPassword("password");
        spec.setKeepAlive(60000);
        spec.setReconnectInterval(10);
        spec.setUseDLQ(false);
        spec.setDlqHandler("dlqHandler");
        spec.setDlqUser("dlqUser");
        spec.setDlqPassword("dlqPassword");
        spec.setDlqClientId("dlqClientId");
        spec.setForceTransacted(false);
        
        //
        // test all getters
        assertEquals(spec.getDestinationName(), "destinationName");
        assertEquals(spec.isPubSubDomain(), true);
        assertEquals(spec.isSubscriptionDurable(), true);
        assertEquals(spec.getDurableSubscriptionName(), "durableSubscriptionName");
        assertEquals(spec.getClientId(), "clientId");
        assertEquals(spec.getMessageSelector(), "messageSelector");
        assertEquals(spec.getAcknowledgeMode(), Session.AUTO_ACKNOWLEDGE);
        assertEquals(spec.getMaxConcurrency(), 1);
        assertEquals(spec.getPrefetchSize(), 1);
        assertEquals(spec.getProviderAdapterJNDI(), "providerAdapterJNDI");
        assertEquals(spec.getUser(), "user");
        assertEquals(spec.getPassword(), "password");
        assertEquals(spec.getKeepAlive(), 60000);
        assertEquals(spec.getReconnectInterval(), 10);
        assertEquals(spec.isUseDLQ(), false);
        assertEquals(spec.getDlqHandler(), "dlqHandler");
        assertEquals(spec.getDlqUser(), "dlqUser");
        assertEquals(spec.getDlqClientId(), "dlqClientId");
        assertEquals(spec.isForceTransacted(), false);
    }

}

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

import javax.jms.Session;
import javax.resource.spi.ResourceAdapter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;

/**
 * This test class supports the <code>JBossJmsActivationSpecFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class JBossJmsActivationSpecFactoryTest
{

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Mock objects
     */

    // Spring beanWrapper
    private final BeanWrapper beanWrapper = mockery.mock(BeanWrapper.class);
    
    // Resource Adapter
    private final ResourceAdapter resourceAdapter = mockery.mock(ResourceAdapter.class);

    // Ikasan extended JmsActivationSpecConfig which supports JBoss settings
    private final JBossJmsActivationSpecConfig config = mockery.mock(JBossJmsActivationSpecConfig.class);
    
    /**
     * Test successful population of the activation spec properties.
     */
    @Test
    public void test_successul_populateActivationSpecProperties()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                //
                // Spring StandardJmsActivationSpecFactory expectations
                //
                exactly(1).of(config).getDestinationName();
                will(returnValue("anyDestinationName"));
                exactly(1).of(config).isPubSubDomain();
                will(returnValue(Boolean.FALSE));

                // set destination
                exactly(1).of(beanWrapper).setPropertyValue(with(any(String.class)), with(any(String.class)));

                // set destinationType
                exactly(1).of(beanWrapper).setPropertyValue(with(any(String.class)), with(any(String.class)));

                // set subscription durability
                exactly(1).of(beanWrapper).isWritableProperty(with(any(String.class)));
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).isSubscriptionDurable();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(beanWrapper).setPropertyValue(with(any(String.class)), with(any(String.class)));

                // durable subscription name
                exactly(2).of(config).getDurableSubscriptionName();
                will(returnValue("durableSubscriptionName"));
                exactly(1).of(beanWrapper).setPropertyValue(with(any(String.class)), with(any(String.class)));
                
                // client id
                exactly(2).of(config).getClientId();
                will(returnValue("clientId"));
                exactly(1).of(beanWrapper).setPropertyValue(with(any(String.class)), with(any(String.class)));
                
                // message selector
                exactly(1).of(config).getMessageSelector();
                will(returnValue(null));

                // acknowledge mode
                exactly(1).of(config).getAcknowledgeMode();
                will(returnValue(Session.AUTO_ACKNOWLEDGE));
                exactly(1).of(beanWrapper).isWritableProperty(with(any(String.class)));
                will(returnValue(Boolean.TRUE));
                exactly(1).of(beanWrapper).setPropertyValue(with(any(String.class)), with(any(String.class)));

                //
                // Spring DefaultJmsActivationSpecFactory expectations
                //

                // concurrency
                exactly(1).of(config).getMaxConcurrency();
                will(returnValue(-1));
                
                // batch (prefetch) size
                exactly(1).of(config).getPrefetchSize();
                will(returnValue(-1));

            
                //
                // Ikasan/JBoss specific JBossJmsActivationSpecFactory expectations
                //

                // keep alive
                exactly(1).of(beanWrapper).isWritableProperty("keepAlive");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getKeepAlive();
                will(returnValue(new Integer(60000)));
                exactly(1).of(beanWrapper).setPropertyValue("keepAlive", new Integer(60000));

                // reconnect interval
                exactly(1).of(beanWrapper).isWritableProperty("reconnectInterval");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getReconnectInterval();
                will(returnValue(new Integer(10)));
                exactly(1).of(beanWrapper).setPropertyValue("reconnectInterval", new Integer(10));

                // maxSession
                exactly(1).of(beanWrapper).isWritableProperty("maxSession");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getMaxConcurrency();
                will(returnValue(1));
                exactly(1).of(beanWrapper).setPropertyValue("maxSession", new Integer(1));

                // prefetch size (max messages)
                exactly(1).of(beanWrapper).isWritableProperty("maxMessages");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getPrefetchSize();
                will(returnValue(1));
                exactly(1).of(beanWrapper).setPropertyValue("maxMessages", new Integer(1));

                // user name
                exactly(1).of(beanWrapper).isWritableProperty("user");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getUser();
                will(returnValue("username"));
                exactly(1).of(beanWrapper).setPropertyValue("user", "username");

                // password
                exactly(1).of(beanWrapper).isWritableProperty("password");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getPassword();
                will(returnValue("password"));
                exactly(1).of(beanWrapper).setPropertyValue("password", "password");

                // subscription durability
                exactly(1).of(beanWrapper).isWritableProperty("subscriptionDurability");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).isSubscriptionDurable();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(beanWrapper).setPropertyValue("subscriptionDurability", "Durable");

                // subscription name
                exactly(1).of(beanWrapper).isWritableProperty("subscriptionName");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getDurableSubscriptionName();
                will(returnValue("subscriptionName"));
                exactly(1).of(beanWrapper).setPropertyValue("subscriptionName", "subscriptionName");

                // use DLQ
                exactly(1).of(beanWrapper).isWritableProperty("useDLQ");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).isUseDLQ();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(beanWrapper).setPropertyValue("useDLQ", Boolean.FALSE);

                // set provider Adapter JNDI
                exactly(1).of(beanWrapper).isWritableProperty("providerAdapterJNDI");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getProviderAdapterJNDI();
                will(returnValue("java:/DefaultJMSProvider"));
                exactly(1).of(beanWrapper).setPropertyValue("providerAdapterJNDI", "java:/DefaultJMSProvider");

                // DLQ handler
                exactly(1).of(beanWrapper).isWritableProperty("dlqHandler");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getDlqHandler();
                will(returnValue("dlqHandler"));
                exactly(1).of(beanWrapper).setPropertyValue("dlqHandler", "dlqHandler");

                // DLQ user
                exactly(1).of(beanWrapper).isWritableProperty("dlqUser");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getDlqUser();
                will(returnValue("dlqUser"));
                exactly(1).of(beanWrapper).setPropertyValue("dlqUser", "dlqUser");

                // DLQ password
                exactly(1).of(beanWrapper).isWritableProperty("dlqPassword");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getDlqPassword();
                will(returnValue("dlqPassword"));
                exactly(1).of(beanWrapper).setPropertyValue("dlqPassword", "dlqPassword");

                // DLQ clientId
                exactly(1).of(beanWrapper).isWritableProperty("dlqClientId");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).getDlqClientId();
                will(returnValue("dlqClientId"));
                exactly(1).of(beanWrapper).setPropertyValue("dlqClientId", "dlqClientId");

                // force Transacted
                exactly(1).of(beanWrapper).isWritableProperty("forceTransacted");
                will(returnValue(Boolean.TRUE));
                exactly(1).of(config).isForceTransacted();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(beanWrapper).setPropertyValue("forceTransacted", Boolean.FALSE);
            }
        });
        
        //
        // run test
        JBossJmsActivationSpecFactory specFactory = new JBossJmsActivationSpecFactory();
        specFactory.populateActivationSpecProperties(beanWrapper, config);

        //
        // everything happy?
        mockery.assertIsSatisfied();
    }
}

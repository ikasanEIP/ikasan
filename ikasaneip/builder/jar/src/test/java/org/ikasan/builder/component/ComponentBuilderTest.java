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
package org.ikasan.builder.component;

import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.endpoint.jms.spring.listener.ArjunaIkasanMessageListenerContainer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class ComponentBuilderTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock scheduler
     */
    final ApplicationContext applicationContext = mockery.mock(ApplicationContext.class, "mockApplicationContext");

    @Before
    public void setup()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                ignoring(applicationContext);
            }
        });
    }

    /**
     * Test successful flow creation.
     */
    @Test
    @Ignore
    public void test_successful_scheduledConsumer() {

        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);
        Consumer scheduledConsumer = componentBuilder.scheduledConsumer().setCronExpression("121212").setEager(true).setIgnoreMisfire(true).setTimezone("UTC").build();

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        ScheduledConsumerConfiguration configuration = ((ConfiguredResource<ScheduledConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertTrue("cronExpression should be '121212'", configuration.isEager() == true);
        assertTrue("eager should be 'true'", configuration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", configuration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", configuration.getTimezone() == "UTC");
    }


    /**
     * Test successful flow creation.
     */
    @Test
    @Ignore
    public void test_successful_jmsConsumer() {
        ArjunaIkasanMessageListenerContainer listenerContainer = new ArjunaIkasanMessageListenerContainer();
        ComponentBuilder componentBuilder = new ComponentBuilder(applicationContext);

        HashMap<String, String> properties = new HashMap<>();
        properties.put("jndi", "test");
        Consumer jmsConsumer = componentBuilder.jmsConsumer().setMessageProvider(listenerContainer)
                .setDestinationJndiName("jms.queue.test")
                .setDestinationJndiProperties(properties)
                .setDurableSubscriptionName("testDurableSubscription")
                .setDurable(true)
                .setConnectionFactoryJndiProperties(properties)
                .setConnectionFactoryName("TestConnectionFactory")
                .setConnectionFactoryUsername("TestUsername")
                .setConnectionFactoryPassword("TestPassword")
                .setAutoContentConversion(true)
                .setAutoSplitBatch(false)
                .setBatchMode(true)
                .setBatchSize(2)
                .setCacheLevel(2)
                .setConcurrentConsumers(2)
                .setMaxConcurrentConsumers(2)
                .setSessionAcknowledgeMode(2)
                .setSessionTransacted(true)
                .setPubSubDomain(true)
                .build();

        assertTrue("instance should be a JmsConsumer", jmsConsumer instanceof JmsContainerConsumer);
        SpringMessageConsumerConfiguration configuration = (
                (ConfiguredResource<SpringMessageConsumerConfiguration>) jmsConsumer).getConfiguration();
        assertEquals("DestinationJndiName should be 'jms.queue.test'", "jms.queue.test",
                configuration.getDestinationJndiName());
        assertEquals("DurableSubscriptionName should be 'testDurableSubscription'", "testDurableSubscription",
                configuration.getDurableSubscriptionName());
        assertTrue("Durable should be 'true'", configuration.getDurable());
        assertEquals("DestinationJndiProperties should be 'pro'", properties,
                configuration.getDestinationJndiProperties());
        assertEquals("ConnectionFactoryJndiProperties should be 'pro'", properties,
                configuration.getConnectionFactoryJndiProperties());
        assertEquals("ConnectionFactoryName should be 'TestConnectionFactory'", "TestConnectionFactory",
                configuration.getConnectionFactoryName());
        assertEquals("ConnectionFactoryUsername should be 'TestUsername'", "TestUsername",
                configuration.getConnectionFactoryUsername());
        assertEquals("ConnectionFactoryPassword should be 'TestPassword'", "TestPassword",
                configuration.getConnectionFactoryPassword());
        assertTrue("AutoContentConversion should be 'true'",
                configuration.isAutoContentConversion());
        assertFalse("AutoSplitBatch should be 'false'",
                configuration.isAutoSplitBatch());
        assertTrue("BatchMode should be 'true'",
                configuration.isBatchMode());
        assertEquals("BatchSize should be '2'", 2,
                configuration.getBatchSize());
        assertEquals("CacheLevel should be '2'", 2,
                configuration.getCacheLevel());
        assertEquals("ConcurrentConsumers should be '2'", 2,
                configuration.getConcurrentConsumers());
        assertEquals("MaxConcurrentConsumers should be '2'", 2,
                configuration.getMaxConcurrentConsumers());
        assertEquals("SessionAcknowledgeMode should be '2'", 2,
                configuration.getSessionAcknowledgeMode().intValue());
        assertTrue("SessionTransacted should be 'true'",
                configuration.getSessionTransacted());
        assertTrue("PubSubDomain should be 'true'",
                configuration.getPubSubDomain());

    }


}

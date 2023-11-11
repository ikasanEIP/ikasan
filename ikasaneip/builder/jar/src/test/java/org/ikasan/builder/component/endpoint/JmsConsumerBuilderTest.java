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
package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.endpoint.jms.spring.listener.ArjunaIkasanMessageListenerContainer;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.naming.Context;
import jakarta.transaction.TransactionManager;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
class JmsConsumerBuilderTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocks
     */
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final TransactionManager transactionManager = mockery.mock(TransactionManager.class, "mockTransactionManager");
    final JtaTransactionManager jtaTransactionManager = mockery.mock(JtaTransactionManager.class, "mockJtaTransactionManager");

    /**
     * Test successful jms consumer creation.
     */
    @Test
    void test_successful_jmsConsumer_when_messageProvider_set() {
        ArjunaIkasanMessageListenerContainer listenerContainer = new ArjunaIkasanMessageListenerContainer();

        final JmsContainerConsumer jmsConsumerEmpty = new JmsContainerConsumer();

        mockery.checking(new Expectations()
        {
            {
                oneOf(jtaTransactionManager).getTransactionManager();
                will(returnValue(transactionManager));
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("jmsConsumer"),with(jmsConsumerEmpty));
                will(returnValue(jmsConsumerEmpty));

            }
        });

        JmsConsumerBuilder jmsConsumerBuilder = new JmsConsumerBuilderImpl(jmsConsumerEmpty,
                jtaTransactionManager,aopProxyProvider);



        Consumer jmsConsumer = jmsConsumerBuilder.setMessageProvider(listenerContainer)
                .setDestinationJndiName("jms.queue.test")
                .setDurableSubscriptionName("testDurableSubscription")
                .setDurable(true)
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

        assertTrue(jmsConsumer instanceof JmsContainerConsumer, "instance should be a JmsConsumer");
        SpringMessageConsumerConfiguration configuration = (
                (ConfiguredResource<SpringMessageConsumerConfiguration>) jmsConsumer).getConfiguration();
        assertEquals("jms.queue.test",
                configuration.getDestinationJndiName(),
                "DestinationJndiName should be 'jms.queue.test'");
        assertEquals("testDurableSubscription",
                configuration.getDurableSubscriptionName(),
                "DurableSubscriptionName should be 'testDurableSubscription'");
        assertTrue(configuration.getDurable(), "Durable should be 'true'");
        assertEquals("TestConnectionFactory",
                configuration.getConnectionFactoryName(),
                "ConnectionFactoryName should be 'TestConnectionFactory'");
        assertEquals("TestUsername",
                configuration.getConnectionFactoryUsername(),
                "ConnectionFactoryUsername should be 'TestUsername'");
        assertEquals("TestPassword",
                configuration.getConnectionFactoryPassword(),
                "ConnectionFactoryPassword should be 'TestPassword'");
        assertTrue(configuration.isAutoContentConversion(),
                "AutoContentConversion should be 'true'");
        assertFalse(configuration.isAutoSplitBatch(),
                "AutoSplitBatch should be 'false'");
        assertTrue(configuration.isBatchMode(),
                "BatchMode should be 'true'");
        assertEquals(2,
                configuration.getBatchSize(),
                "BatchSize should be '2'");
        assertEquals(2,
                configuration.getCacheLevel(),
                "CacheLevel should be '2'");
        assertEquals(2,
                configuration.getConcurrentConsumers(),
                "ConcurrentConsumers should be '2'");
        assertEquals(2,
                configuration.getMaxConcurrentConsumers(),
                "MaxConcurrentConsumers should be '2'");
        assertEquals(2,
                configuration.getSessionAcknowledgeMode().intValue(),
                "SessionAcknowledgeMode should be '2'");
        assertTrue(configuration.getSessionTransacted(),
                "SessionTransacted should be 'true'");
        assertTrue(configuration.getPubSubDomain(),
                "PubSubDomain should be 'true'");

    }

    /**
     * Test successful flow creation.
     */
    @Test
    void test_successful_jmsConsumer_when_messageProvider_not_set_verify_properties() {
        ArjunaIkasanMessageListenerContainer listenerContainer = new ArjunaIkasanMessageListenerContainer();

        final JmsContainerConsumer jmsConsumerEmpty = new JmsContainerConsumer();

        mockery.checking(new Expectations()
        {
            {
                oneOf(jtaTransactionManager).getTransactionManager();
                will(returnValue(transactionManager));
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("jmsConsumer"),with(jmsConsumerEmpty));
                will(returnValue(jmsConsumerEmpty));

            }
        });
        JmsConsumerBuilder jmsConsumerBuilder = new JmsConsumerBuilderImpl(jmsConsumerEmpty,
            jtaTransactionManager,aopProxyProvider);

        Consumer jmsConsumer = jmsConsumerBuilder.setMessageProvider(listenerContainer)
                .setDestinationJndiPropertyFactoryInitial("testinitialFactory")
                .setDestinationJndiPropertyUrlPkgPrefixes("testurlpkg")
                .setDestinationJndiPropertyProviderUrl("testiurl")
                .setDestinationJndiPropertySecurityCredentials("testicredentails")
                .setDestinationJndiPropertySecurityPrincipal("testprinciple")
                .setConnectionFactoryJndiPropertyFactoryInitial("testinitialFactory")
                .setConnectionFactoryJndiPropertyProviderUrl("testiurl")
                .setConnectionFactoryJndiPropertySecurityCredentials("testicredentails")
                .setConnectionFactoryJndiPropertySecurityPrincipal("testprinciple")
                .setConnectionFactoryJndiPropertyUrlPkgPrefixes("testurlpkg")
                .build();

        assertTrue(jmsConsumer instanceof JmsContainerConsumer, "instance should be a JmsConsumer");
        SpringMessageConsumerConfiguration configuration = (
                (ConfiguredResource<SpringMessageConsumerConfiguration>) jmsConsumer).getConfiguration();
        assertEquals("testinitialFactory",
                configuration.getConnectionFactoryJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY),
                "ConnectionFactoryJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'");
        assertEquals("testurlpkg",
                configuration.getConnectionFactoryJndiProperties().get(Context.URL_PKG_PREFIXES),
                "ConnectionFactoryJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'");
        assertEquals("testiurl",
                configuration.getConnectionFactoryJndiProperties().get(Context.PROVIDER_URL),
                "ConnectionFactoryJndiProperties(PROVIDER_URL) should be 'testiurl'");
        assertEquals("testicredentails",
                configuration.getConnectionFactoryJndiProperties().get(Context.SECURITY_CREDENTIALS),
                "ConnectionFactoryJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'");
        assertEquals("testprinciple",
                configuration.getConnectionFactoryJndiProperties().get(Context.SECURITY_PRINCIPAL),
                "ConnectionFactoryJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'");
        assertEquals("testinitialFactory",
                configuration.getDestinationJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY),
                "DestinationJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'");
        assertEquals("testurlpkg",
                configuration.getDestinationJndiProperties().get(Context.URL_PKG_PREFIXES),
                "DestinationJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'");
        assertEquals("testiurl",
                configuration.getDestinationJndiProperties().get(Context.PROVIDER_URL),
                "DestinationJndiProperties(java.naming.provider.url) should be 'testurl'");
        assertEquals("testicredentails",
                configuration.getDestinationJndiProperties().get(Context.SECURITY_CREDENTIALS),
                "DestinationJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'");
        assertEquals("testprinciple",
                configuration.getDestinationJndiProperties().get(Context.SECURITY_PRINCIPAL),
                "DestinationJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'");

    }

    /**
     * Test successful flow creation.
     */
    @Test
    void test_successful_jmsConsumer_when_consumer_configuration_is_set_messageProvider_set() {
        ArjunaIkasanMessageListenerContainer listenerContainer = new ArjunaIkasanMessageListenerContainer();

        final JmsContainerConsumer jmsConsumerEmpty = new JmsContainerConsumer();

        mockery.checking(new Expectations()
        {
            {
                oneOf(jtaTransactionManager).getTransactionManager();
                will(returnValue(transactionManager));

                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("jmsConsumer"),with(jmsConsumerEmpty));
                will(returnValue(jmsConsumerEmpty));

            }
        });

        JmsConsumerBuilder jmsConsumerBuilder = new JmsConsumerBuilderImpl(jmsConsumerEmpty,
            jtaTransactionManager,aopProxyProvider);


        Map<String, String> jndiProps = new HashMap<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "testinitialFactory");
        jndiProps.put(Context.URL_PKG_PREFIXES, "testurlpkg");
        jndiProps.put(Context.PROVIDER_URL, "testiurl");
        jndiProps.put(Context.SECURITY_PRINCIPAL, "testprinciple");
        jndiProps.put(Context.SECURITY_CREDENTIALS, "testicredentails");


        SpringMessageConsumerConfiguration configuration = new SpringMessageConsumerConfiguration();
        configuration.setConnectionFactoryJndiProperties(jndiProps);
        configuration.setDestinationJndiProperties(jndiProps);

        Consumer jmsConsumer = jmsConsumerBuilder
            .setMessageProvider(listenerContainer)
            .setConfiguration(configuration)
            .build();

        assertTrue(jmsConsumer instanceof JmsContainerConsumer, "instance should be a JmsConsumer");
        SpringMessageConsumerConfiguration returnedConfiguration = (
            (ConfiguredResource<SpringMessageConsumerConfiguration>) jmsConsumer).getConfiguration();
        assertEquals("testinitialFactory",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY),
            "ConnectionFactoryJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'");
        assertEquals("testurlpkg",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.URL_PKG_PREFIXES),
            "ConnectionFactoryJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'");
        assertEquals("testiurl",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.PROVIDER_URL),
            "ConnectionFactoryJndiProperties(PROVIDER_URL) should be 'testiurl'");
        assertEquals("testicredentails",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.SECURITY_CREDENTIALS),
            "ConnectionFactoryJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'");
        assertEquals("testprinciple",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.SECURITY_PRINCIPAL),
            "ConnectionFactoryJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'");
        assertEquals("testinitialFactory",
            returnedConfiguration.getDestinationJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY),
            "DestinationJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'");
        assertEquals("testurlpkg",
            returnedConfiguration.getDestinationJndiProperties().get(Context.URL_PKG_PREFIXES),
            "DestinationJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'");
        assertEquals("testiurl",
            returnedConfiguration.getDestinationJndiProperties().get(Context.PROVIDER_URL),
            "DestinationJndiProperties(java.naming.provider.url) should be 'testurl'");
        assertEquals("testicredentails",
            returnedConfiguration.getDestinationJndiProperties().get(Context.SECURITY_CREDENTIALS),
            "DestinationJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'");
        assertEquals("testprinciple",
            returnedConfiguration.getDestinationJndiProperties().get(Context.SECURITY_PRINCIPAL),
            "DestinationJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'");
    }

    /**
     * Test successful flow creation.
     */
    @Test
    void test_successful_jmsConsumer_when_consumer_configuration_is_set_messageProvider_not_set() {
        final JmsContainerConsumer jmsConsumerEmpty = new JmsContainerConsumer();

        mockery.checking(new Expectations()
        {
            {
                oneOf(jtaTransactionManager).getTransactionManager();
                will(returnValue(transactionManager));
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("jmsConsumer"),with(jmsConsumerEmpty));
                will(returnValue(jmsConsumerEmpty));

            }
        });

        JmsConsumerBuilder jmsConsumerBuilder = new JmsConsumerBuilderImpl(jmsConsumerEmpty,
            jtaTransactionManager,aopProxyProvider);

        Map<String, String> jndiProps = new HashMap<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "testinitialFactory");
        jndiProps.put(Context.URL_PKG_PREFIXES, "testurlpkg");
        jndiProps.put(Context.PROVIDER_URL, "testiurl");
        jndiProps.put(Context.SECURITY_PRINCIPAL, "testprinciple");
        jndiProps.put(Context.SECURITY_CREDENTIALS, "testicredentails");


        SpringMessageConsumerConfiguration configuration = new SpringMessageConsumerConfiguration();
        configuration.setConnectionFactoryJndiProperties(jndiProps);
        configuration.setDestinationJndiProperties(jndiProps);

        Consumer jmsConsumer = jmsConsumerBuilder.setConfiguration(configuration)
            .build();

        assertTrue(jmsConsumer instanceof JmsContainerConsumer, "instance should be a JmsConsumer");
        SpringMessageConsumerConfiguration returnedConfiguration = (
            (ConfiguredResource<SpringMessageConsumerConfiguration>) jmsConsumer).getConfiguration();
        assertEquals("testinitialFactory",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY),
            "ConnectionFactoryJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'");
        assertEquals("testurlpkg",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.URL_PKG_PREFIXES),
            "ConnectionFactoryJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'");
        assertEquals("testiurl",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.PROVIDER_URL),
            "ConnectionFactoryJndiProperties(PROVIDER_URL) should be 'testiurl'");
        assertEquals("testicredentails",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.SECURITY_CREDENTIALS),
            "ConnectionFactoryJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'");
        assertEquals("testprinciple",
            returnedConfiguration.getConnectionFactoryJndiProperties().get(Context.SECURITY_PRINCIPAL),
            "ConnectionFactoryJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'");
        assertEquals("testinitialFactory",
            returnedConfiguration.getDestinationJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY),
            "DestinationJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'");
        assertEquals("testurlpkg",
            returnedConfiguration.getDestinationJndiProperties().get(Context.URL_PKG_PREFIXES),
            "DestinationJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'");
        assertEquals("testiurl",
            returnedConfiguration.getDestinationJndiProperties().get(Context.PROVIDER_URL),
            "DestinationJndiProperties(java.naming.provider.url) should be 'testurl'");
        assertEquals("testicredentails",
            returnedConfiguration.getDestinationJndiProperties().get(Context.SECURITY_CREDENTIALS),
            "DestinationJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'");
        assertEquals("testprinciple",
            returnedConfiguration.getDestinationJndiProperties().get(Context.SECURITY_PRINCIPAL),
            "DestinationJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'");
    }

    /**
     * Test successful flow creation.
     */
    @Test
    void test_successful_jmsConsumer_when_messageProvider_not_set() {
        final JmsContainerConsumer jmsConsumerEmpty = new JmsContainerConsumer();

        mockery.checking(new Expectations()
        {
            {
                oneOf(jtaTransactionManager).getTransactionManager();
                will(returnValue(transactionManager));
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("jmsConsumer"),with(jmsConsumerEmpty));
                will(returnValue(jmsConsumerEmpty));

            }
        });

        JmsConsumerBuilder jmsConsumerBuilder = new JmsConsumerBuilderImpl(jmsConsumerEmpty,
            jtaTransactionManager,aopProxyProvider);

        HashMap<String, String> properties = new HashMap<>();
        properties.put("jndi", "test");
        Consumer jmsConsumer = jmsConsumerBuilder
                .setDestinationJndiName("jms.queue.test")
                .build();

        assertTrue(jmsConsumer instanceof JmsContainerConsumer, "instance should be a JmsConsumer");
        SpringMessageConsumerConfiguration configuration = (
                (ConfiguredResource<SpringMessageConsumerConfiguration>) jmsConsumer).getConfiguration();
        assertEquals("jms.queue.test",
                configuration.getDestinationJndiName(),
                "DestinationJndiName should be 'jms.queue.test'");
    }
}

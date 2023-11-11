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

import org.ikasan.component.endpoint.jms.producer.PostProcessor;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.IkasanJmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.transaction.jta.JtaTransactionManager;

import jakarta.jms.ConnectionFactory;
import javax.naming.Context;
import jakarta.transaction.TransactionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
class JmsProducerBuilderTest {

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    final ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class, "mockConnectionFactory");
    final MessageConverter messageConverter = mockery.mock(MessageConverter.class, "mockMessageConverter");
    final IkasanJmsTemplate mockIkasanJmsTemplate = mockery.mock(IkasanJmsTemplate.class, "mockIkasanJmsTemplate");
    final PostProcessor postProcessor = mockery.mock(PostProcessor.class, "mockPostProcessor");
    final JtaTransactionManager jtaTransactionManager = mockery.mock(JtaTransactionManager.class, "mockJtaTransactionManager");
    final TransactionManager transactionManager = mockery.mock(TransactionManager.class, "mockTransactionManager");
    final UserCredentialsConnectionFactoryAdapter mockedUserCredentialsConnectionFactoryAdapter = mockery.mock(UserCredentialsConnectionFactoryAdapter.class, "mockUserCredentialsConnectionFactoryAdapter");

    @Test
    void test_jmsproducerbuilder_build() {

        mockery.checking(new Expectations()
        {{
            oneOf(jtaTransactionManager).getTransactionManager();
            will(returnValue(transactionManager));
            oneOf(mockIkasanJmsTemplate).setPostProcessor(postProcessor);
            oneOf(mockIkasanJmsTemplate).getConnectionFactory();
            will(returnValue(connectionFactory));
            oneOf(mockIkasanJmsTemplate).setConnectionFactory(connectionFactory);
        }});

        JmsProducerBuilder jmsProducerBuilder = new JmsProducerBuilderImpl(mockIkasanJmsTemplate, jtaTransactionManager);

        Producer jmsProducer = jmsProducerBuilder
                .setConfiguredResourceId("crid")
                .setDestinationJndiName("jms.queue.test")
                .setConnectionFactoryName("TestConnectionFactory")
                .setConnectionFactoryUsername("TestUsername")
                .setConnectionFactoryPassword("TestPassword")
                .setSessionAcknowledgeMode(2)
                .setSessionAcknowledgeModeName("TRANSACTED")
                .setSessionTransacted(true)
                .setPubSubDomain(true)
                .setDeliveryPersistent(true)
                .setDeliveryMode(2)
                .setPriority(2)
                .setExplicitQosEnabled(true)
                .setMessageIdEnabled(true)
                .setMessageTimestampEnabled(true)
                .setPubSubNoLocal(true)
                .setReceiveTimeout(2000l)
                .setTimeToLive(2000l)
                .setPostProcessor(postProcessor).build();

        assertTrue(jmsProducer instanceof JmsTemplateProducer, "instance should be a JmsProducer");
        SpringMessageProducerConfiguration configuration = (
                (ConfiguredResource< SpringMessageProducerConfiguration>) jmsProducer).getConfiguration();
        assertEquals("jms.queue.test",
                configuration.getDestinationJndiName(),
                "DestinationJndiName should be 'jms.queue.test'");
        assertEquals("TestConnectionFactory",
                configuration.getConnectionFactoryName(),
                "ConnectionFactoryName should be 'TestConnectionFactory'");
        assertEquals("TestUsername",
                configuration.getConnectionFactoryUsername(),
                "ConnectionFactoryUsername should be 'TestUsername'");
        assertEquals("TestPassword",
                configuration.getConnectionFactoryPassword(),
                "ConnectionFactoryPassword should be 'TestPassword'");
        assertEquals(2,
                configuration.getSessionAcknowledgeMode().intValue(),
                "SessionAcknowledgeMode should be '2'");
        assertEquals("TRANSACTED",
                configuration.getSessionAcknowledgeModeName(),
                "SessionAcknowledgeModeName should be 'TRANSACTED'");
        assertTrue(configuration.getSessionTransacted(),
                "SessionTransacted should be 'true'");
        assertTrue(configuration.getPubSubDomain(),
                "PubSubDomain should be 'true'");
        assertTrue(configuration.getDeliveryPersistent(),
                "DeliveryPersistent should be 'true'");
        assertEquals(2,
                configuration.getDeliveryMode().intValue(),
                "DeliveryMode should be 'true'");
        assertEquals(2,
                configuration.getDeliveryMode().intValue(),
                "DeliveryMode should be 'true'");
        assertEquals(2,
                configuration.getPriority().intValue(),
                "Priority should be 'true'");
        assertTrue(configuration.getExplicitQosEnabled(),
                "ExplicitQosEnabled should be 'true'");
        assertTrue(configuration.getMessageIdEnabled(),
                "MessageIdEnabled should be 'true'");
        assertTrue(configuration.getMessageTimestampEnabled(),
                "MessageTimestampEnabled should be 'true'");
        assertTrue(configuration.getPubSubNoLocal(),
                "PubSubNoLocal should be 'true'");
        assertEquals(2000l,
                configuration.getReceiveTimeout().longValue(),
                "ReceiveTimeout should be 'true'");
        assertEquals(2000l,
                configuration.getTimeToLive().longValue(),
                "TimeToLive should be 'true'");

        this.mockery.assertIsSatisfied();
    }

    @Test
    void test_jmsproducerbuilder_with_connection_credentials_build() {

        mockery.checking(new Expectations()
        {{
            oneOf(jtaTransactionManager).getTransactionManager();
            will(returnValue(transactionManager));
            oneOf(mockIkasanJmsTemplate).setPostProcessor(postProcessor);
            oneOf(mockIkasanJmsTemplate).getConnectionFactory();
            will(returnValue(connectionFactory));
            oneOf(mockIkasanJmsTemplate).setConnectionFactory(with(any(UserCredentialsConnectionFactoryAdapter.class)));
            oneOf(mockedUserCredentialsConnectionFactoryAdapter).setTargetConnectionFactory(connectionFactory);
            oneOf(mockedUserCredentialsConnectionFactoryAdapter).setUsername("username");
            oneOf(mockedUserCredentialsConnectionFactoryAdapter).setPassword("password");
        }});

        JmsProducerBuilder jmsProducerBuilder = new ExtendedJmsProducerBuilderImpl(mockIkasanJmsTemplate, jtaTransactionManager);

        Producer jmsProducer = jmsProducerBuilder
                .setConfiguredResourceId("crid")
                .setDestinationJndiName("jms.queue.test")
                .setConnectionFactoryName("TestConnectionFactory")
                .setConnectionFactoryUsername("TestUsername")
                .setConnectionFactoryPassword("TestPassword")
                .setSessionAcknowledgeMode(2)
                .setSessionAcknowledgeModeName("TRANSACTED")
                .setSessionTransacted(true)
                .setPubSubDomain(true)
                .setDeliveryPersistent(true)
                .setDeliveryMode(2)
                .setPriority(2)
                .setExplicitQosEnabled(true)
                .setMessageIdEnabled(true)
                .setMessageTimestampEnabled(true)
                .setPubSubNoLocal(true)
                .setReceiveTimeout(2000l)
                .setTimeToLive(2000l)
                .setConnectionUsername("username")
                .setConnectionPassword("password")
                .setPostProcessor(postProcessor)
                .build();

        assertTrue(jmsProducer instanceof JmsTemplateProducer, "instance should be a JmsProducer");
        SpringMessageProducerConfiguration configuration = (
                (ConfiguredResource< SpringMessageProducerConfiguration>) jmsProducer).getConfiguration();
        assertEquals("jms.queue.test",
                configuration.getDestinationJndiName(),
                "DestinationJndiName should be 'jms.queue.test'");
        assertEquals("TestConnectionFactory",
                configuration.getConnectionFactoryName(),
                "ConnectionFactoryName should be 'TestConnectionFactory'");
        assertEquals("TestUsername",
                configuration.getConnectionFactoryUsername(),
                "ConnectionFactoryUsername should be 'TestUsername'");
        assertEquals("TestPassword",
                configuration.getConnectionFactoryPassword(),
                "ConnectionFactoryPassword should be 'TestPassword'");
        assertEquals(2,
                configuration.getSessionAcknowledgeMode().intValue(),
                "SessionAcknowledgeMode should be '2'");
        assertEquals("TRANSACTED",
                configuration.getSessionAcknowledgeModeName(),
                "SessionAcknowledgeModeName should be 'TRANSACTED'");
        assertTrue(configuration.getSessionTransacted(),
                "SessionTransacted should be 'true'");
        assertTrue(configuration.getPubSubDomain(),
                "PubSubDomain should be 'true'");
        assertTrue(configuration.getDeliveryPersistent(),
                "DeliveryPersistent should be 'true'");
        assertEquals(2,
                configuration.getDeliveryMode().intValue(),
                "DeliveryMode should be 'true'");
        assertEquals(2,
                configuration.getDeliveryMode().intValue(),
                "DeliveryMode should be 'true'");
        assertEquals(2,
                configuration.getPriority().intValue(),
                "Priority should be 'true'");
        assertTrue(configuration.getExplicitQosEnabled(),
                "ExplicitQosEnabled should be 'true'");
        assertTrue(configuration.getMessageIdEnabled(),
                "MessageIdEnabled should be 'true'");
        assertTrue(configuration.getMessageTimestampEnabled(),
                "MessageTimestampEnabled should be 'true'");
        assertTrue(configuration.getPubSubNoLocal(),
                "PubSubNoLocal should be 'true'");
        assertEquals(2000l,
                configuration.getReceiveTimeout().longValue(),
                "ReceiveTimeout should be 'true'");
        assertEquals(2000l,
                configuration.getTimeToLive().longValue(),
                "TimeToLive should be 'true'");

        this.mockery.assertIsSatisfied();
    }

    @Test
    void test_jmsproducerbuilder_build_verify_properties()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(jtaTransactionManager).getTransactionManager();
            will(returnValue(transactionManager));
        }});

        JmsProducerBuilder jmsProducerBuilder = new JmsProducerBuilderImpl(new IkasanJmsTemplate(),jtaTransactionManager);

        Producer jmsProducer = jmsProducerBuilder
                .setConfiguredResourceId("crid")
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

        assertTrue(jmsProducer instanceof JmsTemplateProducer, "instance should be a JmsProducer");
        SpringMessageProducerConfiguration configuration = (
                (ConfiguredResource< SpringMessageProducerConfiguration>) jmsProducer).getConfiguration();
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

    @Test
    void test_jmsproducerbuilder_build_with_cf()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(jtaTransactionManager).getTransactionManager();
            will(returnValue(transactionManager));
        }});

        JmsProducerBuilder jmsProducerBuilder = new JmsProducerBuilderImpl(new IkasanJmsTemplate(),jtaTransactionManager);

        Producer jmsProducer = jmsProducerBuilder
                .setConfiguredResourceId("crid")
                .setConnectionFactory(connectionFactory)
                .build();

        assertTrue(jmsProducer instanceof JmsTemplateProducer, "instance should be a JmsProducer");
    }

    @Test
    void test_jmsproducerbuilder_build_with_messageConverter()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(jtaTransactionManager).getTransactionManager();
            will(returnValue(transactionManager));
        }});

        JmsProducerBuilder jmsProducerBuilder = new JmsProducerBuilderImpl(new IkasanJmsTemplate(),jtaTransactionManager);

        Producer jmsProducer = jmsProducerBuilder
            .setConfiguredResourceId("crid")
            .setMessageConverter(messageConverter)
            .build();

        assertTrue(jmsProducer instanceof JmsTemplateProducer, "instance should be a JmsProducer");
    }

    class ExtendedJmsProducerBuilderImpl extends JmsProducerBuilderImpl
    {

        /**
         * Constructor
         *
         * @param ikasanJmsTemplate
         * @param transactionManager
         */
        public ExtendedJmsProducerBuilderImpl(IkasanJmsTemplate ikasanJmsTemplate, JtaTransactionManager transactionManager) {
            super(ikasanJmsTemplate, transactionManager);
        }

        @Override
        protected UserCredentialsConnectionFactoryAdapter getUserCredentialsConnectionFactoryAdapter()
        {
            return mockedUserCredentialsConnectionFactoryAdapter;
        }
    }
}

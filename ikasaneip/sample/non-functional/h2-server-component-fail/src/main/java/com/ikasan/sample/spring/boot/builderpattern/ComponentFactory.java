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
package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Session;

import static org.springframework.jms.listener.DefaultMessageListenerContainer.*;

/**
 * Sample component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${jms.producer.configuredResourceId}")
    String jmsProducerConfiguredResourceId;

    @Value("${jms.provider.url}")
    private String jmsProviderUrl;

    private String destinationName = "jms.topic.test";

    Consumer getJmsConsumer(String clientId)
    {
        ActiveMQXAConnectionFactory connectionFactory =
                new ActiveMQXAConnectionFactory(jmsProviderUrl);
        connectionFactory.setClientID(clientId);
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(-1);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);

       return builderFactory.getComponentBuilder().jmsConsumer()
                .setConnectionFactory(connectionFactory)
                .setDestinationJndiName(destinationName)
                .setDurableSubscriptionName("testDurableSubscription")
                .setDurable(true)
                .setAutoContentConversion(true)
                .setAutoSplitBatch(true)
                .setBatchMode(false)
                .setBatchSize(1)
                .setCacheLevel(CACHE_AUTO)
                .setConcurrentConsumers(1)
                .setMaxConcurrentConsumers(1)
                .setSessionAcknowledgeMode(Session.SESSION_TRANSACTED)
                .setSessionTransacted(true)
                .setPubSubDomain(true)
                .build();
    }

    Broker getDbBroker()
    {
        return new DbBroker();
    }

    Producer getJmsProducer()
    {
        ActiveMQXAConnectionFactory connectionFactory =
                new ActiveMQXAConnectionFactory(jmsProviderUrl);
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(-1);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);

        return builderFactory.getComponentBuilder().jmsProducer()
                .setConfiguredResourceId(jmsProducerConfiguredResourceId)
                .setDestinationJndiName(destinationName)
                .setConnectionFactory(connectionFactory)
                .setSessionAcknowledgeMode(Session.SESSION_TRANSACTED)
                .setSessionTransacted(true)
                .setPubSubDomain(true)
                .setDeliveryPersistent(true)
                .setDeliveryMode(DeliveryMode.PERSISTENT)
                .setExplicitQosEnabled(true)
                .setMessageIdEnabled(true)
                .setMessageTimestampEnabled(true)
                .build();
    }

}

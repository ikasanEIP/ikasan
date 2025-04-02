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

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.event.ManagedEventIdentifierException;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.Resource;
import java.io.IOException;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig
{
    @Value("${jms.provider.url}")
    private String brokerUrl;
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    public IBigQueue outboundQueue;
    @Resource
    public IBigQueue inboundQueue;
    @Resource
    public ApplicationContext applicationContext;

    @Resource
    private JtaTransactionManager transactionManager;

    public Consumer bigQueueConsumer(IBigQueue inboundQueue, ExtendedBigQueueConsumerBuilder extendedBigQueueConsumerBuilder)  {
        return extendedBigQueueConsumerBuilder
            .setInboundQueue(inboundQueue)
            .setPutErrorsToBackOfQueue(false)
            .setSerialiser(new BigQueueMessageJsonSerialiser())
            .build();
    }

    public Producer bigQueueProducer(IBigQueue outboundQueue) {
        return builderFactory.getComponentBuilder().bigQueueProducer()
            .setOutboundQueue(outboundQueue)
            .setSerialiser(new BigQueueMessageJsonSerialiser())
            .build();
    }

    @Bean
    public ExtendedBigQueueConsumerBuilder extendedBigQueueConsumerBuilder() {
        return new ExtendedBigQueueConsumerBuilder(this.applicationContext.getBean(AopProxyProvider.class),
            this.applicationContext.getBean(JtaTransactionManager.class).getTransactionManager());
    }

    @Bean
    public Module getModule(ExtendedBigQueueConsumerBuilder extendedBigQueueConsumerBuilder) throws IOException, SystemException, RollbackException {

        ModuleBuilder mb = builderFactory.getModuleBuilder("nft-recovery-manager");

        FlowBuilder fb = mb.getFlowBuilder("BigQueue Sample Flow");

        Flow flow = fb
                .withDescription("Flow demonstrates usage of BigQueue Consumer and BigQueue Producer")
                .consumer("BigQueue Consumer", this.bigQueueConsumer(this.inboundQueue, extendedBigQueueConsumerBuilder))
                .broker( "Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker( "Delay Generating Broker", new DelayGenerationBroker())
                .producer("BigQueue Producer", this.bigQueueProducer(this.outboundQueue))
                .build();

        Module module = mb.withDescription("Sample Module")
            .addFlow(flow)
            .addFlow(this.jmsFlow(mb))
            .addFlow(this.multiThreadedJmsFlow(mb))
            .addFlow(this.getScheduledToJmsFlow(mb, builderFactory.getComponentBuilder()))
            .build();
        return module;
    }

    public Flow jmsFlow(ModuleBuilder mb) {
        FlowBuilder fb = mb.getFlowBuilder("Jms Sample Flow");

        ActiveMQXAConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        consumerConnectionFactory.setMaxThreadPoolSize(100);
        Consumer jmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
            .setConnectionFactory(consumerConnectionFactory)
            .setDestinationJndiName("source")
            .setAutoContentConversion(true)
            .setConfiguredResourceId("jmsConsumer")
            .build();


        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("target")
            .setConfiguredResourceId("jmsProducer")
            .build();

        return fb
            .withDescription("Flow demonstrates usage of JMS Concumer and JMS Producer")
            .consumer("JMS Consumer", jmsConsumer)
            .broker( "Exception Generating Broker", new ExceptionGeneratingBroker())
            .broker( "Delay Generating Broker", new DelayGenerationBroker())
            .producer("JMS Producer", jmsProducer)
            .build();
    }

    public Flow multiThreadedJmsFlow(ModuleBuilder mb) {
        FlowBuilder fb = mb.getFlowBuilder("Multi Threaded Jms Sample Flow");

        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        Consumer multiThreadedJmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
            .setConnectionFactory(consumerConnectionFactory)
            .setManagedIdentifierService(new ManagedRelatedEventIdentifierService() {
                @Override
                public void setRelatedEventIdentifier(Object relatedIdentifier, Object o) throws ManagedEventIdentifierException {

                }

                @Override
                public Object getRelatedEventIdentifier(Object o) throws ManagedEventIdentifierException {
                    try {
                        return ((ActiveMQTextMessage)o).getText();
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void setEventIdentifier(Object o, Object o2) throws ManagedEventIdentifierException {

                }

                @Override
                public Object getEventIdentifier(Object o) throws ManagedEventIdentifierException {
                    try {
                        return ((ActiveMQTextMessage)o).getText();
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }
            })
            .setDestinationJndiName("source")
            .setAutoContentConversion(true)
            .setConfiguredResourceId("multiThreadedJmsConsumer")
            .setConcurrentConsumers(5)
            .setMaxConcurrentConsumers(5)
            .build();


        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("target")
            .setConfiguredResourceId("jmsProducer")
            .build();

        return fb
            .withDescription("Flow demonstrates usage of JMS Concumer and JMS Producer")
            .consumer("Multi Threaded JMS Consumer", multiThreadedJmsConsumer)
            .broker( "Exception Generating Broker", new ScheduledExceptionGeneratingBroker())
            .broker( "Delay Generating Broker", new DelayGenerationBroker())
            .producer("JMS Producer", jmsProducer)
            .build();
    }

    public Flow getScheduledToJmsFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("sftp.private.jms.queue")
            .setConfiguredResourceId("sftpJmsProducer")
            .build();

        SampleMessageProvider sampleMessageProvider = new SampleMessageProvider();
        sampleMessageProvider.setTransactionManager(this.transactionManager);

        FlowBuilder sftpToLogFlowBuilder = moduleBuilder.getFlowBuilder("Scheduled To Jms Flow");
        Flow sftpToJmsFlow = sftpToLogFlowBuilder
            .withDescription("Scheduled To Jms")
            .consumer("Scheduled Consumer", componentBuilder.scheduledConsumer()
                .setMessageProvider(sampleMessageProvider)
                .setManagedEventIdentifierService(new SampleIdentifierService())
                .setConfiguration(new ScheduledConsumerConfiguration())
                .setConfiguredResourceId("scheduled-consumer")
                .build())
            .broker("Exception Generating Broker", new ScheduledExceptionGeneratingBroker())
            .producer("Scheduled Jms Producer", jmsProducer)
            .build();
        return sftpToJmsFlow;
    }
}

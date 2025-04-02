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

    /**
     * Creates a Consumer for fetching messages from a specified IBigQueue using the provided ExtendedBigQueueConsumerBuilder.
     *
     * @param inboundQueue the IBigQueue from which to consume messages
     * @param extendedBigQueueConsumerBuilder the ExtendedBigQueueConsumerBuilder to configure the Consumer
     * @return a Consumer for fetching messages from the IBigQueue
     */
    public Consumer bigQueueConsumer(IBigQueue inboundQueue, ExtendedBigQueueConsumerBuilder extendedBigQueueConsumerBuilder)  {
        return extendedBigQueueConsumerBuilder
            .setInboundQueue(inboundQueue)
            .setPutErrorsToBackOfQueue(false)
            .setSerialiser(new BigQueueMessageJsonSerialiser())
            .build();
    }

    /**
     * Creates a Producer instance for distributing messages to an endpoint using the provided IBigQueue as the outbound queue.
     *
     * @param outboundQueue the outbound queue to publish messages to
     * @return Producer instance for distributing messages to an endpoint
     */
    public Producer bigQueueProducer(IBigQueue outboundQueue) {
        return builderFactory.getComponentBuilder().bigQueueProducer()
            .setOutboundQueue(outboundQueue)
            .setSerialiser(new BigQueueMessageJsonSerialiser())
            .build();
    }


    /**
     * Returns a new instance of ExtendedBigQueueConsumerBuilder configured with the AopProxyProvider and
     * TransactionManager obtained from the application context.
     *
     * @return a new ExtendedBigQueueConsumerBuilder instance
     */
    @Bean
    public ExtendedBigQueueConsumerBuilder extendedBigQueueConsumerBuilder() {
        return new ExtendedBigQueueConsumerBuilder(this.applicationContext.getBean(AopProxyProvider.class),
            this.applicationContext.getBean(JtaTransactionManager.class).getTransactionManager());
    }

    /**
     * Retrieves a Module based on the provided ExtendedBigQueueConsumerBuilder.
     *
     * @param extendedBigQueueConsumerBuilder the ExtendedBigQueueConsumerBuilder used to build the Module
     * @return the retrieved Module
     * @throws IOException if an I/O error occurs
     * @throws SystemException if a system exception occurs
     * @throws RollbackException if a rollback exception occurs
     */
    @Bean
    public Module getModule(ExtendedBigQueueConsumerBuilder extendedBigQueueConsumerBuilder) throws IOException, SystemException, RollbackException {
        ModuleBuilder mb = builderFactory.getModuleBuilder("nft-recovery-manager");

        Module module = mb.withDescription("Sample Module")
            .addFlow(this.bigQueueFlow(mb, extendedBigQueueConsumerBuilder))
            .addFlow(this.jmsFlow(mb))
            .addFlow(this.multiThreadedJmsFlow(mb))
            .addFlow(this.getScheduledToJmsFlow(mb, builderFactory.getComponentBuilder()))
            .build();
        return module;
    }

    /**
     * Constructs a Flow representing a business path using a BigQueue Consumer and Producer.
     *
     * @param mb the ModuleBuilder used to build the Flow
     * @param extendedBigQueueConsumerBuilder the ExtendedBigQueueConsumerBuilder instance for configuration
     * @return the constructed Flow object
     */
    private Flow bigQueueFlow(ModuleBuilder mb, ExtendedBigQueueConsumerBuilder extendedBigQueueConsumerBuilder) {
        FlowBuilder fb = mb.getFlowBuilder("BigQueue Sample Flow");

        return fb
            .withDescription("Flow demonstrates usage of BigQueue Consumer and BigQueue Producer")
            .consumer("BigQueue Consumer", this.bigQueueConsumer(this.inboundQueue, extendedBigQueueConsumerBuilder))
            .broker( "Exception Generating Broker", new ExceptionGeneratingBroker())
            .broker( "Delay Generating Broker", new DelayGenerationBroker())
            .producer("BigQueue Producer", this.bigQueueProducer(this.outboundQueue))
            .build();
    }

    /**
     * Constructs and returns a Flow instance representing a JMS sample flow.
     *
     * @param mb the ModuleBuilder instance to use for constructing the flow
     * @return a Flow instance representing the JMS sample flow
     */
    private Flow jmsFlow(ModuleBuilder mb) {
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

    /**
     * Creates a multi-threaded JMS flow with a JMS consumer and producer.
     *
     * @param mb the ModuleBuilder to use for building the flow
     * @return a Flow representing the multi-threaded JMS flow
     */
    private Flow multiThreadedJmsFlow(ModuleBuilder mb) {
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

    /**
     * Retrieves a Flow representing a business path from a scheduling component to a JMS producer.
     *
     * @param moduleBuilder The ModuleBuilder used to build the Flow.
     * @param componentBuilder The ComponentBuilder used to build the JMS producer.
     * @return The constructed Flow representing the path from scheduling to JMS.
     */
    private Flow getScheduledToJmsFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
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

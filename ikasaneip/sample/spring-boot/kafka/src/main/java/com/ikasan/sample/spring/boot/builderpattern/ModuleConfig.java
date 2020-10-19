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

import liquibase.integration.spring.SpringLiquibase;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.*;
import org.ikasan.component.endpoint.kafka.consumer.KafkaConsumer;
import org.ikasan.component.endpoint.kafka.consumer.KafkaConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-messageProcessor.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private ErrorReportingServiceFactory errorReportingServiceFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    private KafkaConsumerConfiguration consumerConfiguration;

    @Resource
    private AopProxyProvider aopProxyProvider;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-jms");

        FlowBuilder fb = mb.getFlowBuilder("Jms Sample Flow");

        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
                .setConnectionFactory(producerConnectionFactory)
                .setDestinationJndiName("target")
                .setConfiguredResourceId("jmsProducer")
                .build();

        Flow flow = fb
                .withDescription("Flow demonstrates usage of JMS Concumer and JMS Producer")
                .withExceptionResolver(builderFactory.getExceptionResolverBuilder()
                        .addExceptionToAction(SampleGeneratedException.class, OnException.excludeEvent())
                        .addExceptionToAction(EndpointException.class, OnException.retryIndefinitely(1000)).build())
                .withErrorReportingServiceFactory(errorReportingServiceFactory)
                .consumer("Kafka Consumer", getConsumer())
                .broker( "Exception Generating Broker", new ExceptionGenerationgBroker())
                .broker( "Delay Generating Broker", new DelayGenerationBroker())
                .producer("JMS Producer", jmsProducer)
                .build();

        Module module = mb.withDescription("Sample Module")
            .addFlow(flow)
            .build();
        return module;
    }

    private KafkaConsumer getConsumer() {
        KafkaConsumer<Integer, String> kafkaConsumer = new KafkaConsumer<>();
        this.initialiseKafkaConsumerConfigutation();
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setConfiguredResourceId("kafka-consumer");

        kafkaConsumer.setMessageProcessor(this.aopProxyProvider.applyPointcut("Kafka Consumer", kafkaConsumer));

        return kafkaConsumer;
    }

    private void initialiseKafkaConsumerConfigutation() {
        this.consumerConfiguration = new KafkaConsumerConfiguration();
        this.consumerConfiguration.setAutoCommitIntervalMillis(10);
        this.consumerConfiguration.setSessionTimeoutMillis(60000);
        this.consumerConfiguration.setKeyDeserializer("org.apache.kafka.common.serialization.IntegerDeserializer");
        this.consumerConfiguration.setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfiguration.setEnableAutoCommit(false);
    }
}

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
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import jakarta.jms.ConnectionFactory;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-jms");

        FlowBuilder fb = mb.getFlowBuilder("Jms Sample Flow");

        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
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

        Flow flow = fb
                .withDescription("Flow demonstrates usage of JMS Concumer and JMS Producer")
                .consumer("JMS Consumer", jmsConsumer)
                .broker( "Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker( "Delay Generating Broker", new DelayGenerationBroker())
                .producer("JMS Producer", jmsProducer)
                .build();

        Module module = mb.withDescription("Sample Module")
            .addFlow(flow)
            .build();
        return module;
    }

}

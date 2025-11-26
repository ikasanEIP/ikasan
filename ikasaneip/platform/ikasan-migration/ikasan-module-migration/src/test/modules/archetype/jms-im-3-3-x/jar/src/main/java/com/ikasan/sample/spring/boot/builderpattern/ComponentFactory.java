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

import org.ikasan.builder.BuilderFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import javax.jms.ConnectionFactory;
import com.ikasan.sample.spring.boot.builderpattern.component.cdw.*;
import com.ikasan.sample.spring.boot.builderpattern.component.broker.*;

import javax.annotation.Resource;

@Configuration
public class ComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    public CdwClient cdwClient() {
        return new CdwClient(brokerUrl);
    }

    @Bean
    public CdwBroker cdwBroker(CdwClient cdwClient) {
        return new CdwBroker(cdwClient);
    }

    @Bean
    public Consumer jmsConsumer() {
        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        return builderFactory.getComponentBuilder().jmsConsumer()
            .setConnectionFactory(consumerConnectionFactory)
            .setDestinationJndiName("source")
            .setAutoContentConversion(true)
            .setConfiguredResourceId("jmsConsumer")
            .build();
    }

    @Bean
    public Producer jmsProducer() {
        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        return builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("target")
            .setConfiguredResourceId("jmsProducer")
            .build();
    }


}

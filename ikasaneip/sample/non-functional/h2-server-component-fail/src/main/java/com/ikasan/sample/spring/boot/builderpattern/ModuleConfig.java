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

import jakarta.annotation.Resource;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.OnException;
import org.ikasan.builder.invoker.Configuration;
import org.ikasan.component.endpoint.consumer.api.spec.EndpointEventProvider;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Ikasan Development Team
 */
@org.springframework.context.annotation.Configuration
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    public static int REPEAT = 2;  // Change this to up the number of events
    public static int EVENTS_PER_CYCLE = 10;
    public static int EVENT_GENERATOR_COUNT = 20;

    @Bean
    public Module getModule()
    {
        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("Transaction Test Module");

        // event generating flow
        Flow sourceFlow = moduleBuilder
                .getFlowBuilder("eventGeneratorToJMSFlow")
                .withDescription("Event generating flow.")
                .consumer("Event Generating Consumer", builderFactory.getComponentBuilder()
                        .eventGeneratingConsumer().setEndpointEventProvider( new TechEndpointEventProvider() ))
                .producer("JMS Producer", componentFactory.getJmsProducer()).build();

        // dynamic configuration update flow
        Flow configurationUpdaterFlow = moduleBuilder.getFlowBuilder("configurationUpdaterFlow")
                .withDescription("Flow which constantly updates dynamic configuration and saves back to the DB on each invocation.")
                .withExceptionResolver(builderFactory.getExceptionResolverBuilder().addExceptionToAction(RuntimeException.class, OnException.retryIndefinitely(100)))
                .consumer("Event Generating Consumer", builderFactory.getComponentBuilder()
                        .eventGeneratingConsumer().setEndpointEventProvider( new TechEndpointEventProvider() ))
                .broker("Configuration Updater", componentFactory.getDbBroker(), Configuration.brokerInvoker().withDynamicConfiguration(true))
                .producer("Dev Null Producer", builderFactory.getComponentBuilder().devNullProducer().build()).build();

        // jms consumer flow 1
        Flow jmsConsumer1Flow = moduleBuilder.getFlowBuilder("jmsToDevNullFlow1")
                .withDescription("First JMS Consuming flow.")
                .consumer("JMS Consumer", componentFactory.getJmsConsumer("jmsConsumer1"))
                .producer("Dev Null Producer", builderFactory.getComponentBuilder().devNullProducer().build())
                .build();

        // jms consumer flow 2
        Flow jmsConsumer2Flow = moduleBuilder.getFlowBuilder("jmsToDevNullFlow2")
                .withDescription("Second JMS Consuming flow.")
                .consumer("JMS Consumer", componentFactory.getJmsConsumer("jmsConsumer2"))
                .producer("Dev Null Producer", builderFactory.getComponentBuilder().devNullProducer().build())
                .build();

        Module module = moduleBuilder.withDescription("Test DB module.")
                .addFlow(sourceFlow)
                .addFlow(configurationUpdaterFlow)
                .addFlow(jmsConsumer1Flow)
                .addFlow(jmsConsumer2Flow)
                .build();

        return module;
    }

    class TechEndpointEventProvider implements EndpointEventProvider<String>
    {
        long count;
        String event;
        boolean rollback;

        @Override
        public String getEvent()
        {
            if(rollback)
            {
                rollback = false;
                return event;
            }

            event = ( (count < EVENT_GENERATOR_COUNT) ? "Test Message " + ++count : null);
            return event;
        }

        @Override
        public void rollback()
        {
            this.rollback = true;
        }
    }
}

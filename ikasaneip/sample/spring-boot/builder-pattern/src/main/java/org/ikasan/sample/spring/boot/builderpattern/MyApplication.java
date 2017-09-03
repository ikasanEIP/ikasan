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
package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.*;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.resubmission.ResubmissionService;

/**
 * Sample standalone bootstrap application using the builder pattern.
 *
 * @author Ikasan Development Team
 */
public class MyApplication
{
    public static void main(String[] args) throws Exception
    {
        new MyApplication().executeIM(args);
    }


    public void executeIM(String[] args)
    {
        // get an ikasanApplication instance
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);

        // get a builderFactory
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        // get a module builder from the ikasanApplication
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("moduleName").withDescription("Example module with pattern builder");

        // get an instance of flowBuilder from the moduleBuilder and create a flow
        Flow scheduledFlow = getScheduledFlow(moduleBuilder, builderFactory.getComponentBuilder());

        // get an instance of flowBuilder from the moduleBuilder and create a flow
        Flow jmsFlow = getJmsFlow(moduleBuilder, builderFactory.getComponentBuilder());

        // add flows to the module
        Module module = moduleBuilder.addFlow(scheduledFlow).addFlow(jmsFlow).build();

        // pass the module to Ikasan to run
        ikasanApplication.run(module);

    }

    public Flow getScheduledFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Scheduled Flow Name");
        return flowBuilder.withDescription("scheduled flow description")
                .consumer("consumer", componentBuilder.scheduledConsumer().setCronExpression("0/5 * * * * ?").setConfiguredResourceId("configuredResourceId").build())
                .producer("producer", new MyProducer()).build();
    }

    public Flow getJmsFlow(ModuleBuilder moduleBuilder,ComponentBuilder componentBuilder) {
        FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Jms Flow Name");

        return flowBuilder.withDescription("Jms flow description")
                .consumer("consumer", componentBuilder.jmsConsumer().setConfiguredResourceId("configuredResourceId")
                        .setDestinationJndiName("dynamicQueues/source")
                        .setConnectionFactoryName("ConnectionFactory")
                        .setConnectionFactoryJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
                        .setConnectionFactoryJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)")
                        .setDestinationJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
                        .setDestinationJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)")
                        .setAutoContentConversion(true)
                        .build()
                )
                .producer("producer", componentBuilder.jmsProducer()
                        .setDestinationJndiName("dynamicQueues/target")
                        .setConnectionFactoryName("ConnectionFactory")
                        .setConnectionFactoryJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
                        .setConnectionFactoryJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)")
                        .setDestinationJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
                        .setDestinationJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)").build()
                )
                .build();

    }

    public Flow getSampleFlow(ModuleBuilder moduleBuilder)
    {
        FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Jms Flow Name");
        return flowBuilder.withDescription("Jms flow description")
                .consumer("consumer", new MyConsumer())
                .producer("producer", new MyProducer()).build();
    }

    private class MyConsumer implements Consumer,ResubmissionService
    {

        private boolean isRunning;
        @Override
        public void setListener(Object o) {

        }

        @Override
        public void setEventFactory(Object o) {

        }

        @Override
        public Object getEventFactory() {
            return null;
        }

        @Override
        public void start()
        {
            this.isRunning = true;
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void stop() {
            this.isRunning = false;
        }

//        @Override public void onMessage(Message message)
//        {
//            System.out.print("Message");
//        }

        @Override
        public void submit(Object o) {

        }
    }

    private class MyProducer implements Producer
    {

        @Override
        public void invoke(Object payload) throws EndpointException {

        }
    }
}
/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */

package org.ikasan.component.factory.spring.jms.consumer;


import jakarta.annotation.Resource;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.factory.spring.common.NonConfiguredResourceBaseComponentFactory;
import org.ikasan.component.factory.spring.jms.JmsComponentFactory;

/**
 * Easily create Jms Consumer passing the configuration prefix. Will handle all different types of jms consumer
 * (batching, concurrent, topic, queue etc)
 *
 * You will need to define the following properties in your modules .properties file :-
 *
 * consumer.flow.jms.consumer.destination=dynamicQueues/your.queue
 * consumer.flow.jms.consumer.type=broker1
 *
 * In your shared .properties file you will need to define these properties for each type / broker
 *
 *  * broker1.provider.url=
 *  * broker1.connectionFactory.user=
 *  * broker1.connectionFactory.password=
 *  * broker1.connectionFactory.name=
 *  * broker1.java.naming.factory.initial=
 *  * broker1.java.naming.factory.url.pkg=java.naming.factory.url.pkgs
 *
 *  Note in the above example the flow prefix "consumer.flow" and the type prefix "broker1" are arbitarily chosen
 *  this will be specific to your flow and broker names you choose. Then to create the jms consumer you need
 *
 * jmsConsumerComponentFactory.create("consumerFlowJmsConsumerComponent", "consumer.flow"))
 *
 * The first field - the suffix "consumerFlowJmsConsumerComponent" is used to identify the configuration when it is
 * saved. The second field is used to extract the correct properties for your component from the properties file
 * - in this case all properties with the prefix "consumer.flow" will be bound to the component.
 */
public class JmsConsumerComponentFactory extends NonConfiguredResourceBaseComponentFactory<JmsContainerConsumer,
        JmsConsumerConfiguration> {

    @Resource
    private JmsComponentFactory jmsComponentFactory;


    @Override
    public JmsContainerConsumer create(String nameSuffix, String configPrefix) {
        JmsConsumerConfiguration configuration = configuration(configPrefix,
                JmsConsumerConfiguration.class);

        //Topics cannot have concurrent consumers as each consumer requires unique durable subscription name
        if(isTopic(configuration.getDestination())){
            return configuration.isBatching()?
                createBatchConsumer(nameSuffix,configuration) :createConsumer(nameSuffix, configuration);
            }

        if(configuration.isConcurrent()){
            return createConcurrentConsumer(nameSuffix, configuration);
        }

        if(configuration.isBatching()){
            return createBatchConsumer(nameSuffix, configuration);
        }

        return createConsumer(nameSuffix, configuration);

    }


    private JmsContainerConsumer createConsumer(String nameSuffix, JmsConsumerConfiguration configuration ){
        return (JmsContainerConsumer) jmsComponentFactory.getJMSConsumer(
                appendClassToNameSuffix(nameSuffix, JmsContainerConsumer.class.getSimpleName()),
                configuration.getDestination(),
                configuration.isAutoConversion(), configuration.getType());

    }

    private JmsContainerConsumer createBatchConsumer(String nameSuffix, JmsConsumerConfiguration configuration ){
        return (JmsContainerConsumer) jmsComponentFactory.getJMSConsumerWithBatching(
                appendClassToNameSuffix(nameSuffix, JmsContainerConsumer.class.getSimpleName()),
                configuration.getDestination(),
                configuration.isAutoConversion(), configuration.getType(), configuration.getBatchSize());

    }

    private JmsContainerConsumer createConcurrentConsumer(String nameSuffix, JmsConsumerConfiguration configuration ){
        return (JmsContainerConsumer) jmsComponentFactory.getJMSConsumerConcurrent(
                appendClassToNameSuffix(nameSuffix, JmsContainerConsumer.class.getSimpleName()),
                configuration.getDestination(),
                configuration.isAutoConversion(), configuration.getType(), configuration.getNumberOfThreads(), configuration.isBatching(), configuration.getBatchSize());

    }

    private boolean isTopic( String destinationName){
        return destinationName.startsWith("dynamicTopics")
                || destinationName.startsWith("/jms/topic");
    }
}

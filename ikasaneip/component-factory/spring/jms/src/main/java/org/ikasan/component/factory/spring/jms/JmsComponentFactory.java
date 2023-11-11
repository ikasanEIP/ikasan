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

package org.ikasan.component.factory.spring.jms;

import jakarta.annotation.Resource;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Easy way to create jms components. This assumes that you have specified properties in your *shared*
 * application properties of the form :-
 *
 * type.provider.url=
 * type.connectionFactory.user=
 * type.connectionFactory.password=
 * type.connectionFactory.name=
 * type.java.naming.factory.initial=
 * type.java.naming.factory.url.pkg=java.naming.factory.url.pkgs
 *
 * Where type is the prefix you provide to differentiate between different brokers
 */
public class JmsComponentFactory {
    @Resource
    private BuilderFactory builderFactory;
    @Value("${module.name}")
    private String moduleName;
    @Autowired
    private Environment env;

    public JmsComponentFactory() {
    }

    private Map<String, String> getJndiProperties(String clientId, String type, boolean useClientIdPrefix) {
        Map<String, String> properties = new HashMap();
        properties.put("java.naming.factory.initial", this.env.getProperty(type + ".java.naming.factory.initial"));
        properties.put("java.naming.security.principal", this.env.getProperty(type + ".connectionFactory.user"));
        properties.put("java.naming.security.credentials", this.env.getProperty(type + ".connectionFactory.password"));
        properties.put("java.naming.provider.url", this.getBroker(this.env.getProperty(type + ".provider.url"), clientId, useClientIdPrefix));
        return properties;
    }

    private String getBroker(String brokerUrl, String clientId, boolean useClientIdPrefix) {
        if (!brokerUrl.startsWith("vm") && !brokerUrl.startsWith("remote") && !brokerUrl.startsWith("jnp")) {
            String symbol;
            if (brokerUrl.contains("?")) {
                symbol = "&";
            } else {
                symbol = "?";
            }

            return useClientIdPrefix ? brokerUrl + symbol + "jms.clientIDPrefix=" + clientId : brokerUrl + symbol + "jms.clientID=" + clientId;
        } else {
            return brokerUrl;
        }
    }

    public Consumer getJMSConsumer(String nameSuffix, String destination, boolean isAutoConvertion, String type) {
        boolean pubSub = false;
        Map jndiProperties;
        if (!destination.startsWith("dynamicTopics") && !destination.startsWith("/jms/topic")) {
            jndiProperties = this.getJndiProperties(this.moduleName + "-" + nameSuffix, type, true);
        } else {
            pubSub = true;
            jndiProperties = this.getJndiProperties(this.moduleName + "-" + nameSuffix, type, false);
        }

        Consumer jmsConsumer = (Consumer)this.builderFactory.getComponentBuilder().jmsConsumer().setDestinationJndiProperties(jndiProperties).setConnectionFactoryJndiProperties(jndiProperties).setConnectionFactoryName(this.env.getProperty(type + ".connectionFactory.name")).setConnectionFactoryUsername(this.env.getProperty(type + ".connectionFactory.user")).setConnectionFactoryPassword(this.env.getProperty(type + ".connectionFactory.password")).setDestinationJndiName(destination).setDurable(true).setPubSubDomain(pubSub).setDurableSubscriptionName(this.moduleName + "-" + nameSuffix).setAutoContentConversion(isAutoConvertion).setConfiguredResourceId(this.moduleName + "-" + nameSuffix).build();
        return jmsConsumer;
    }

    public Consumer getJMSConsumerWithBatching(String nameSuffix, String destination, boolean isAutoConvertion, String type, int batchSize) {
        boolean pubSub = false;
        Map jndiProperties;
        if (!destination.startsWith("dynamicTopics") && !destination.startsWith("/jms/topic")) {
            jndiProperties = this.getJndiProperties(this.moduleName + "-" + nameSuffix, type, true);
        } else {
            pubSub = true;
            jndiProperties = this.getJndiProperties(this.moduleName + "-" + nameSuffix, type, false);
        }

        Consumer jmsConsumer = (Consumer)this.builderFactory.getComponentBuilder().jmsConsumer().setDestinationJndiProperties(jndiProperties).setConnectionFactoryJndiProperties(jndiProperties).setConnectionFactoryName(this.env.getProperty(type + ".connectionFactory.name")).setConnectionFactoryUsername(this.env.getProperty(type + ".connectionFactory.user")).setConnectionFactoryPassword(this.env.getProperty(type + ".connectionFactory.password")).setDestinationJndiName(destination).setDurable(true).setPubSubDomain(pubSub).setBatchMode(true).setBatchSize(batchSize).setAutoSplitBatch(true).setDurableSubscriptionName(this.moduleName + "-" + nameSuffix).setAutoContentConversion(isAutoConvertion).setConfiguredResourceId(this.moduleName + "-" + nameSuffix).build();
        return jmsConsumer;
    }

    public Consumer getJMSConsumerConcurrent(String nameSuffix, String destination, boolean isAutoConvertion, String type, int numberOfThreads, boolean isBatchingEnabled, int batchSize) {
        Map<String, String> jndiProperties = this.getJndiProperties(this.moduleName + "-" + nameSuffix, type, true);
        Consumer jmsConsumer = (Consumer)this.builderFactory.getComponentBuilder().jmsConsumer().setDestinationJndiProperties(jndiProperties).setConnectionFactoryJndiProperties(jndiProperties).setConnectionFactoryName(this.env.getProperty(type + ".connectionFactory.name")).setConnectionFactoryUsername(this.env.getProperty(type + ".connectionFactory.user")).setConnectionFactoryPassword(this.env.getProperty(type + ".connectionFactory.password")).setDestinationJndiName(destination).setDurable(true).setPubSubDomain(false).setAutoContentConversion(isAutoConvertion).setCacheLevel(1).setConcurrentConsumers(numberOfThreads).setMaxConcurrentConsumers(numberOfThreads).setBatchMode(isBatchingEnabled).setAutoSplitBatch(isBatchingEnabled).setBatchSize(batchSize).setConfiguredResourceId(this.moduleName + "-" + nameSuffix).build();
        return jmsConsumer;
    }

    public Producer getJMSProducer(String nameSuffix, String destination, String type) {
        return this.getJMSProducer(nameSuffix, destination, type, true);
    }

    public Producer getJMSProducer(String nameSuffix, String destination, String type, boolean useClientIdPrefix) {
        Map<String, String> jndiProperties = this.getJndiProperties(this.moduleName + "-" + nameSuffix, type, useClientIdPrefix);
        boolean pubSub = false;
        if (destination.startsWith("dynamicTopics") || destination.startsWith("/jms/topic")) {
            pubSub = true;
        }

        Producer jmsProducer = (Producer)this.builderFactory.getComponentBuilder().jmsProducer().setDestinationJndiProperties(jndiProperties).setConnectionFactoryJndiProperties(jndiProperties).setConnectionFactoryName(this.env.getProperty(type + ".connectionFactory.name")).setConnectionFactoryUsername(this.env.getProperty(type + ".connectionFactory.user")).setConnectionFactoryPassword(this.env.getProperty(type + ".connectionFactory.password")).setDestinationJndiName(destination).setPubSubDomain(pubSub).setDeliveryPersistent(true).setSessionTransacted(true).setMessageIdEnabled(true).setConfiguredResourceId(this.moduleName + "-" + nameSuffix).build();
        return jmsProducer;
    }
}

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
package org.ikasan.builder.component;

import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.endpoint.jms.spring.listener.ArjunaIkasanMessageListenerContainer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.IkasanMessageListenerContainer;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.MessageListener;
import javax.naming.Context;
import javax.transaction.TransactionManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Ikasan provided jms consumer default implementation.
 * This implementation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
class JmsConsumerBuilderImpl implements JmsConsumerBuilder, RequiresAopProxy<MessageListener>, RequiresComponentName, RequiresFlowName, RequiresModuleName {

    /**
     * default jms consumer instance
     */
    JmsContainerConsumer jmsConsumer;

    /**
     * Message provider
     */
    MessageProvider messageProvider;

    @Autowired
    JtaTransactionManager transactionManager;

    @Autowired
    TransactionManager arjunaTransactionManager;

    /**
     * configuration consumer
     */
    SpringMessageConsumerConfiguration configuration;
    /**
     * proxy of scheduled consumer with transaction pointcuts
     */
    MessageListener aopProxiedMessageListener;

    /**
     * default value for component name - will be overridden at runtime
     */
    String componentName = "unspecifiedScheduledComponentName";

    /**
     * default value for module name - will be overridden at runtime
     */
    String moduleName = "unspecifiedModuleName";

    /**
     * default value for flow name - will be overridden at runtime
     */
    String flowName = "unspecifiedFlowName";


    /**
     * Constructor
     */
    public JmsConsumerBuilderImpl(JmsContainerConsumer jmsConsumer) {
        this.jmsConsumer = jmsConsumer;
        if (jmsConsumer == null) {
            throw new IllegalArgumentException("jmsConsumer cannot be 'null'");
        }

        this.aopProxiedMessageListener = jmsConsumer;
    }

    /**
     * ConfigurationService identifier for this component configuration.
     *
     * @param configuredResourceId
     * @return
     */
    public JmsConsumerBuilder setConfiguredResourceId(String configuredResourceId) {
        this.jmsConsumer.setConfiguredResourceId(configuredResourceId);
        return this;
    }

    /**
     * Actual runtime configuration
     *
     * @param jmsConsumerConfiguration
     * @return
     */
    public JmsConsumerBuilder setConfiguration(SpringMessageConsumerConfiguration jmsConsumerConfiguration) {
        this.jmsConsumer.setConfiguration(jmsConsumerConfiguration);
        return this;
    }

    /**
     * Underlying tech providing the message event
     *
     * @param messageProvider
     * @return
     */
    public JmsConsumerBuilder setMessageProvider(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
        return this;
    }

    /**
     * Implementation of the managed event identifier service - sets the life identifier based on the incoming event.
     *
     * @param managedRelatedEventIdentifierService
     * @return
     */
    public JmsConsumerBuilder setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService) {
        this.jmsConsumer.setManagedIdentifierService(managedRelatedEventIdentifierService);
        return this;
    }


    /**
     * Override default event factory
     *
     * @param eventFactory
     * @return
     */
    public JmsConsumerBuilder setEventFactory(EventFactory eventFactory) {
        this.jmsConsumer.setEventFactory(eventFactory);
        return this;
    }

    @Override
    public JmsConsumerBuilder setDestinationJndiProperties(Map<String, String> destinationJndiProperties) {
        getConfiguration().setDestinationJndiProperties(destinationJndiProperties);
        return this;
    }

    @Override
    public JmsConsumerBuilder setDestinationJndiName(String destinationJndiName) {
        getConfiguration().setDestinationJndiName(destinationJndiName);
        return this;
    }

    @Override
    public JmsConsumerBuilder setDurableSubscriptionName(String durableSubscriptionName) {
        getConfiguration().setDurableSubscriptionName(durableSubscriptionName);
        return this;
    }

    @Override
    public JmsConsumerBuilder setDurable(Boolean durable) {
        getConfiguration().setDurable(durable);
        return this;
    }

    @Override
    public JmsConsumerBuilder setConnectionFactoryJndiProperties(Map<String, String> connectionFactoryJndiProperties) {
        getConfiguration().setConnectionFactoryJndiProperties(connectionFactoryJndiProperties);
        return this;
    }

    @Override
    public JmsConsumerBuilder setConnectionFactoryJndiPropertyProviderUrl(String providerUrl) {
        if (getConfiguration().getConnectionFactoryJndiProperties() == null) {
            getConfiguration().setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getConnectionFactoryJndiProperties().put(Context.PROVIDER_URL, providerUrl);

        return this;
    }

    @Override
    public JmsConsumerBuilder setConnectionFactoryJndiPropertyFactoryInitial(String initialFactory) {
        if (getConfiguration().getConnectionFactoryJndiProperties() == null) {
            getConfiguration().setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getConnectionFactoryJndiProperties().put(Context.INITIAL_CONTEXT_FACTORY, initialFactory);

        return this;
    }

    @Override
    public JmsConsumerBuilder setDestinationJndiPropertyProviderUrl(String providerUrl) {
        if (getConfiguration().getDestinationJndiProperties() == null) {
            getConfiguration().setDestinationJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getDestinationJndiProperties().put(Context.PROVIDER_URL, providerUrl);

        return this;
    }

    @Override
    public JmsConsumerBuilder setDestinationJndiPropertyFactoryInitial(String initialFactory) {
        if (getConfiguration().getDestinationJndiProperties() == null) {
            getConfiguration().setDestinationJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getDestinationJndiProperties().put(Context.INITIAL_CONTEXT_FACTORY, initialFactory);

        return this;
    }

    @Override
    public JmsConsumerBuilder setConnectionFactoryUsername(String username) {
        getConfiguration().setConnectionFactoryUsername(username);
        return this;
    }

    @Override
    public JmsConsumerBuilder setConnectionFactoryPassword(String password) {
        getConfiguration().setConnectionFactoryPassword(password);
        return this;
    }

    @Override
    public JmsConsumerBuilder setConnectionFactoryName(String connectionFactoryName) {
        getConfiguration().setConnectionFactoryName(connectionFactoryName);
        return this;
    }

    @Override
    public JmsConsumerBuilder setAutoContentConversion(boolean autoContentConversion) {
        getConfiguration().setAutoContentConversion(autoContentConversion);
        return this;
    }

    @Override
    public JmsConsumerBuilder setAutoSplitBatch(boolean autoSplitBatch) {
        getConfiguration().setAutoSplitBatch(autoSplitBatch);
        return this;
    }

    @Override
    public JmsConsumerBuilder setBatchMode(boolean batchMode) {
        getConfiguration().setBatchMode(batchMode);
        return this;
    }

    @Override
    public JmsConsumerBuilder setCacheLevel(int cacheLevel) {
        getConfiguration().setCacheLevel(cacheLevel);
        return this;
    }

    @Override
    public JmsConsumerBuilder setConcurrentConsumers(int concurrentConsumers) {
        getConfiguration().setConcurrentConsumers(concurrentConsumers);
        return this;
    }

    @Override
    public JmsConsumerBuilder setBatchSize(int batchSize) {
        getConfiguration().setBatchSize(batchSize);
        return this;
    }

    @Override
    public JmsConsumerBuilder setMaxConcurrentConsumers(int maxConcurrentConsumers) {
        getConfiguration().setMaxConcurrentConsumers(maxConcurrentConsumers);
        return this;
    }

    @Override
    public JmsConsumerBuilder setPubSubDomain(Boolean pubSubDomain) {
        getConfiguration().setPubSubDomain(pubSubDomain);
        return this;
    }

    @Override
    public JmsConsumerBuilder setSessionTransacted(Boolean sessionTransacted) {
        getConfiguration().setSessionTransacted(sessionTransacted);
        return this;
    }

    @Override
    public JmsConsumerBuilder setSessionAcknowledgeMode(Integer sessionAcknowledgeMode) {
        getConfiguration().setSessionAcknowledgeMode(sessionAcknowledgeMode);
        return this;
    }

    private SpringMessageConsumerConfiguration getConfiguration() {
        if (configuration == null) {
            configuration = new SpringMessageConsumerConfiguration();
        }

        return configuration;
    }

    /**
     * Set the raw component proxied object
     *
     * @param messageListener
     */
    public void setAopProxyTarget(MessageListener messageListener) {
        this.aopProxiedMessageListener = messageListener;
    }

    /**
     * Get the raw component for proxying
     *
     * @return
     */
    public MessageListener getAopProxyTarget() {
        return (MessageListener) this.jmsConsumer;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     *
     * @return
     */

    public JmsContainerConsumer build() {
        if (this.jmsConsumer.getConfiguration() == null) {
            this.jmsConsumer.setConfiguration(new SpringMessageConsumerConfiguration());
        }

        if (this.jmsConsumer.getConfiguredResourceId() == null) {
            this.jmsConsumer.setConfiguredResourceId(this.componentName + flowName + moduleName);
        }

        if (messageProvider != null) {

            if (messageProvider instanceof IkasanMessageListenerContainer) {
                ((ArjunaIkasanMessageListenerContainer) messageProvider).setMessageListener(aopProxiedMessageListener);
                ((ArjunaIkasanMessageListenerContainer) messageProvider).setErrorHandler(jmsConsumer);
                ((ArjunaIkasanMessageListenerContainer) messageProvider).setExceptionListener(jmsConsumer);
            }

            this.jmsConsumer.setMessageProvider(messageProvider);

        } else {
            ArjunaIkasanMessageListenerContainer messageListenerContainer = new ArjunaIkasanMessageListenerContainer();
            messageListenerContainer.setMessageListener(aopProxiedMessageListener);
            messageListenerContainer.setErrorHandler(jmsConsumer);
            messageListenerContainer.setExceptionListener(jmsConsumer);
            messageListenerContainer.setTransactionManager(transactionManager);
            messageListenerContainer.setLocalTransactionManager(arjunaTransactionManager);
            this.jmsConsumer.setMessageProvider(messageListenerContainer);
        }

        if (this.jmsConsumer.getConfiguration() == null) {
            this.jmsConsumer.setConfiguration(configuration);
        }


        return this.jmsConsumer;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }


}


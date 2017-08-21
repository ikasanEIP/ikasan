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

import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import javax.naming.Context;
import java.util.HashMap;
import java.util.Map;

/**
 * Ikasan provided jms producer default implementation.
 * 
 * @author Ikasan Development Team
 */
class JmsProducerBuilderImpl implements JmsProducerBuilder, RequiresComponentName, RequiresFlowName, RequiresModuleName {

    /**
     * default jms consumer instance
     */
    JmsTemplateProducer jmsProducer;

    /**
     * configuration consumer
     */
    SpringMessageProducerConfiguration configuration;

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
    public JmsProducerBuilderImpl(JmsTemplateProducer jmsProducer) {
        this.jmsProducer = jmsProducer;
        if (jmsProducer == null) {
            throw new IllegalArgumentException("jmsProducer cannot be 'null'");
        }

    }

    /**
     * ConfigurationService identifier for this component configuration.
     *
     * @param configuredResourceId
     * @return
     */
    public JmsProducerBuilder setConfiguredResourceId(String configuredResourceId) {
        this.jmsProducer.setConfiguredResourceId(configuredResourceId);
        return this;
    }

    /**
     * Actual runtime configuration
     *
     * @param jmsProducerConfiguration
     * @return
     */
    public JmsProducerBuilder setConfiguration(SpringMessageProducerConfiguration jmsProducerConfiguration) {
        this.jmsProducer.setConfiguration(jmsProducerConfiguration);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertyProviderUrl(String providerUrl) {
        if (getConfiguration().getConnectionFactoryJndiProperties() == null) {
            getConfiguration().setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getConnectionFactoryJndiProperties().put(Context.PROVIDER_URL, providerUrl);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertyFactoryInitial(String initialFactory) {
        if (getConfiguration().getConnectionFactoryJndiProperties() == null) {
            getConfiguration().setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getConnectionFactoryJndiProperties().put(Context.INITIAL_CONTEXT_FACTORY, initialFactory);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertyUrlPkgPrefixes(String urlPackage) {
        if (getConfiguration().getConnectionFactoryJndiProperties() == null) {
            getConfiguration().setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getConnectionFactoryJndiProperties().put(Context.URL_PKG_PREFIXES, urlPackage);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertySecurityCredentials(String securityCredentials) {
        if (getConfiguration().getConnectionFactoryJndiProperties() == null) {
            getConfiguration().setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getConnectionFactoryJndiProperties().put(Context.SECURITY_CREDENTIALS, securityCredentials);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertySecurityPrincipal(String securityPrincipal) {
        if (getConfiguration().getConnectionFactoryJndiProperties() == null) {
            getConfiguration().setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getConnectionFactoryJndiProperties().put(Context.SECURITY_PRINCIPAL, securityPrincipal);

        return this;
    }


    @Override
    public JmsProducerBuilder setDestinationJndiPropertyProviderUrl(String providerUrl) {
        if (getConfiguration().getDestinationJndiProperties() == null) {
            getConfiguration().setDestinationJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getDestinationJndiProperties().put(Context.PROVIDER_URL, providerUrl);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertyFactoryInitial(String initialFactory) {
        if (getConfiguration().getDestinationJndiProperties() == null) {
            getConfiguration().setDestinationJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getDestinationJndiProperties().put(Context.INITIAL_CONTEXT_FACTORY, initialFactory);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertyUrlPkgPrefixes(String initialFactory) {
        if (getConfiguration().getDestinationJndiProperties() == null) {
            getConfiguration().setDestinationJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getDestinationJndiProperties().put(Context.URL_PKG_PREFIXES, initialFactory);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertySecurityCredentials(String securityCredentials) {
        if (getConfiguration().getDestinationJndiProperties() == null) {
            getConfiguration().setDestinationJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getDestinationJndiProperties().put(Context.SECURITY_CREDENTIALS, securityCredentials);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertySecurityPrincipal(String securityPrincipal) {
        if (getConfiguration().getDestinationJndiProperties() == null) {
            getConfiguration().setDestinationJndiProperties(new HashMap<String, String>());
        }
        getConfiguration().getDestinationJndiProperties().put(Context.SECURITY_PRINCIPAL, securityPrincipal);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiProperties(Map<String, String> destinationJndiProperties) {
        getConfiguration().setDestinationJndiProperties(destinationJndiProperties);
        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiName(String destinationJndiName) {
        getConfiguration().setDestinationJndiName(destinationJndiName);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiProperties(Map<String, String> connectionFactoryJndiProperties) {
        getConfiguration().setConnectionFactoryJndiProperties(connectionFactoryJndiProperties);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryName(String connectionFactoryName) {
        getConfiguration().setConnectionFactoryName(connectionFactoryName);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryUsername(String connectionFactoryUsername) {
        getConfiguration().setConnectionFactoryUsername(connectionFactoryUsername);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryPassword(String connectionFactoryPassword) {
        getConfiguration().setConnectionFactoryPassword(connectionFactoryPassword);
        return this;
    }

    @Override
    public JmsProducerBuilder setPubSubDomain(Boolean pubSubDomain) {
        getConfiguration().setPubSubDomain(pubSubDomain);
        return this;
    }

    @Override
    public JmsProducerBuilder setDeliveryPersistent(Boolean deliveryPersistent) {
        getConfiguration().setDeliveryPersistent(deliveryPersistent);
        return this;
    }

    @Override
    public JmsProducerBuilder setDeliveryMode(Integer deliveryMode) {
        getConfiguration().setDeliveryMode(deliveryMode);
        return this;
    }

    @Override
    public JmsProducerBuilder setSessionTransacted(Boolean sessionTransacted) {
        getConfiguration().setSessionTransacted(sessionTransacted);
        return this;
    }

    @Override
    public JmsProducerBuilder setExplicitQosEnabled(Boolean explicitQosEnabled) {
        getConfiguration().setExplicitQosEnabled(explicitQosEnabled);
        return this;
    }

    @Override
    public JmsProducerBuilder setMessageIdEnabled(Boolean messageIdEnabled) {
        getConfiguration().setMessageIdEnabled(messageIdEnabled);
        return this;
    }

    @Override
    public JmsProducerBuilder setMessageTimestampEnabled(Boolean messageTimestampEnabled) {
        getConfiguration().setMessageTimestampEnabled(messageTimestampEnabled);
        return this;
    }

    @Override
    public JmsProducerBuilder setPriority(Integer priority) {
        getConfiguration().setPriority(priority);
        return this;
    }

    @Override
    public JmsProducerBuilder setPubSubNoLocal(Boolean pubSubNoLocal) {
        getConfiguration().setPubSubNoLocal(pubSubNoLocal);
        return this;
    }

    @Override
    public JmsProducerBuilder setReceiveTimeout(Long receiveTimeout) {
        getConfiguration().setReceiveTimeout(receiveTimeout);
        return this;
    }

    @Override
    public JmsProducerBuilder setSessionAcknowledgeMode(Integer sessionAcknowledgeMode) {
        getConfiguration().setSessionAcknowledgeMode(sessionAcknowledgeMode);
        return this;
    }

    @Override
    public JmsProducerBuilder setSessionAcknowledgeModeName(String sessionAcknowledgeModeName) {
        getConfiguration().setSessionAcknowledgeModeName(sessionAcknowledgeModeName);
        return this;
    }

    @Override
    public JmsProducerBuilder setTimeToLive(Long timeToLive) {
        getConfiguration().setTimeToLive(timeToLive);
        return this;
    }

    private SpringMessageProducerConfiguration getConfiguration() {
        if (configuration == null) {
            configuration = new SpringMessageProducerConfiguration();
        }

        return configuration;
    }


    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     *
     * @return
     */

    public JmsTemplateProducer build() {

        if (this.jmsProducer.getConfiguredResourceId() == null) {
            this.jmsProducer.setConfiguredResourceId(this.componentName + flowName + moduleName);
        }

        if (configuration!=null && this.jmsProducer.getConfiguration() == null) {
            this.jmsProducer.setConfiguration(configuration);
        } else if(this.jmsProducer.getConfiguration() == null){
            this.jmsProducer.setConfiguration(new SpringMessageProducerConfiguration());
        }


        return this.jmsProducer;
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


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
package org.ikasan.builder.component.endpoint;

import org.ikasan.component.endpoint.jms.producer.PostProcessor;
import org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.IkasanJmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.transaction.TransactionManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Ikasan provided jms producer default implementation.
 * 
 * @author Ikasan Development Team
 */
public class JmsProducerBuilderImpl implements JmsProducerBuilder
{
    /**
     * jms template
     */
    IkasanJmsTemplate ikasanJmsTemplate;

    /**
     * Local Transaction manager
     */
    TransactionManager arjunaTransactionManager;

    /**
     * configuration consumer
     */
    SpringMessageProducerConfiguration configuration = new SpringMessageProducerConfiguration();

    /**
     * Configuration resource id
     */
    String configuredResourceId;

    /**
     * Optional username to authenticate on connection
     */
    String username;

    /**
     * Optional password to authenticate on connection
     */
    String password;

    /**
     * Constructor
     */
    public JmsProducerBuilderImpl(IkasanJmsTemplate ikasanJmsTemplate, TransactionManager arjunaTransactionManager)
    {
        this.ikasanJmsTemplate = ikasanJmsTemplate;
        if (ikasanJmsTemplate == null)
        {
            throw new IllegalArgumentException("ikasanJmsTemplate cannot be 'null'");
        }
        this.arjunaTransactionManager = arjunaTransactionManager;

    }

    /**
     * ConfigurationService identifier for this component configuration.
     *
     * @param configuredResourceId
     * @return
     */
    public JmsProducerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
        return this;
    }

    /**
     * Actual runtime configuration
     *
     * @param jmsProducerConfiguration
     * @return
     */
    public JmsProducerBuilder setConfiguration(SpringMessageProducerConfiguration jmsProducerConfiguration)
    {
        this.configuration = jmsProducerConfiguration;
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertyProviderUrl(String providerUrl)
    {
        if (this.configuration.getConnectionFactoryJndiProperties() == null) {
            this.configuration.setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getConnectionFactoryJndiProperties().put(Context.PROVIDER_URL, providerUrl);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertyFactoryInitial(String initialFactory) {
        if (this.configuration.getConnectionFactoryJndiProperties() == null) {
            this.configuration.setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getConnectionFactoryJndiProperties().put(Context.INITIAL_CONTEXT_FACTORY, initialFactory);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertyUrlPkgPrefixes(String urlPackage) {
        if (this.configuration.getConnectionFactoryJndiProperties() == null) {
            this.configuration.setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getConnectionFactoryJndiProperties().put(Context.URL_PKG_PREFIXES, urlPackage);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertySecurityCredentials(String securityCredentials) {
        if (this.configuration.getConnectionFactoryJndiProperties() == null) {
            this.configuration.setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getConnectionFactoryJndiProperties().put(Context.SECURITY_CREDENTIALS, securityCredentials);

        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiPropertySecurityPrincipal(String securityPrincipal) {
        if (this.configuration.getConnectionFactoryJndiProperties() == null) {
            this.configuration.setConnectionFactoryJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getConnectionFactoryJndiProperties().put(Context.SECURITY_PRINCIPAL, securityPrincipal);

        return this;
    }


    @Override
    public JmsProducerBuilder setDestinationJndiPropertyProviderUrl(String providerUrl) {
        if (this.configuration.getDestinationJndiProperties() == null) {
            this.configuration.setDestinationJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getDestinationJndiProperties().put(Context.PROVIDER_URL, providerUrl);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertyFactoryInitial(String initialFactory) {
        if (this.configuration.getDestinationJndiProperties() == null) {
            this.configuration.setDestinationJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getDestinationJndiProperties().put(Context.INITIAL_CONTEXT_FACTORY, initialFactory);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertyUrlPkgPrefixes(String initialFactory) {
        if (this.configuration.getDestinationJndiProperties() == null) {
            this.configuration.setDestinationJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getDestinationJndiProperties().put(Context.URL_PKG_PREFIXES, initialFactory);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertySecurityCredentials(String securityCredentials) {
        if (this.configuration.getDestinationJndiProperties() == null) {
            this.configuration.setDestinationJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getDestinationJndiProperties().put(Context.SECURITY_CREDENTIALS, securityCredentials);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiPropertySecurityPrincipal(String securityPrincipal) {
        if (this.configuration.getDestinationJndiProperties() == null) {
            this.configuration.setDestinationJndiProperties(new HashMap<String, String>());
        }
        this.configuration.getDestinationJndiProperties().put(Context.SECURITY_PRINCIPAL, securityPrincipal);

        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiProperties(Map<String, String> destinationJndiProperties) {
        this.configuration.setDestinationJndiProperties(destinationJndiProperties);
        return this;
    }

    @Override
    public JmsProducerBuilder setDestinationJndiName(String destinationJndiName) {
        this.configuration.setDestinationJndiName(destinationJndiName);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryJndiProperties(Map<String, String> connectionFactoryJndiProperties) {
        this.configuration.setConnectionFactoryJndiProperties(connectionFactoryJndiProperties);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryName(String connectionFactoryName) {
        this.configuration.setConnectionFactoryName(connectionFactoryName);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryUsername(String connectionFactoryUsername) {
        this.configuration.setConnectionFactoryUsername(connectionFactoryUsername);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactoryPassword(String connectionFactoryPassword) {
        this.configuration.setConnectionFactoryPassword(connectionFactoryPassword);
        return this;
    }

    @Override
    public JmsProducerBuilder setPubSubDomain(Boolean pubSubDomain) {
        this.configuration.setPubSubDomain(pubSubDomain);
        return this;
    }

    @Override
    public JmsProducerBuilder setDeliveryPersistent(Boolean deliveryPersistent) {
        this.configuration.setDeliveryPersistent(deliveryPersistent);
        return this;
    }

    @Override
    public JmsProducerBuilder setDeliveryMode(Integer deliveryMode) {
        this.configuration.setDeliveryMode(deliveryMode);
        return this;
    }

    @Override
    public JmsProducerBuilder setSessionTransacted(Boolean sessionTransacted) {
        this.configuration.setSessionTransacted(sessionTransacted);
        return this;
    }

    @Override
    public JmsProducerBuilder setExplicitQosEnabled(Boolean explicitQosEnabled) {
        this.configuration.setExplicitQosEnabled(explicitQosEnabled);
        return this;
    }

    @Override
    public JmsProducerBuilder setMessageIdEnabled(Boolean messageIdEnabled) {
        this.configuration.setMessageIdEnabled(messageIdEnabled);
        return this;
    }

    @Override
    public JmsProducerBuilder setMessageTimestampEnabled(Boolean messageTimestampEnabled) {
        this.configuration.setMessageTimestampEnabled(messageTimestampEnabled);
        return this;
    }

    @Override
    public JmsProducerBuilder setPriority(Integer priority) {
        this.configuration.setPriority(priority);
        return this;
    }

    @Override
    public JmsProducerBuilder setPubSubNoLocal(Boolean pubSubNoLocal) {
        this.configuration.setPubSubNoLocal(pubSubNoLocal);
        return this;
    }

    @Override
    public JmsProducerBuilder setReceiveTimeout(Long receiveTimeout) {
        this.configuration.setReceiveTimeout(receiveTimeout);
        return this;
    }

    @Override
    public JmsProducerBuilder setSessionAcknowledgeMode(Integer sessionAcknowledgeMode) {
        this.configuration.setSessionAcknowledgeMode(sessionAcknowledgeMode);
        return this;
    }

    @Override
    public JmsProducerBuilder setSessionAcknowledgeModeName(String sessionAcknowledgeModeName) {
        this.configuration.setSessionAcknowledgeModeName(sessionAcknowledgeModeName);
        return this;
    }

    @Override
    public JmsProducerBuilder setTimeToLive(Long timeToLive) {
        this.configuration.setTimeToLive(timeToLive);
        return this;
    }

    @Override
    public JmsProducerBuilder setPostProcessor(PostProcessor<?, ?> postProcessor)
    {
        this.ikasanJmsTemplate.setPostProcessor(postProcessor);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionFactory(ConnectionFactory connectionFactory) {
        this.ikasanJmsTemplate.setConnectionFactory(connectionFactory);
        return this;
    }

    @Override
    public JmsProducerBuilder setMessageConverter(MessageConverter messageConverter) {
        this.ikasanJmsTemplate.setMessageConverter(messageConverter);
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public JmsProducerBuilder setConnectionPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     *
     * @return
     */

    public JmsTemplateProducer build()
    {
        JmsTemplateProducer jmsProducer = getJmsTemplateProducer(this.ikasanJmsTemplate);
        jmsProducer.setConfiguration(configuration);
        jmsProducer.setConfiguredResourceId(configuredResourceId);

        this.ikasanJmsTemplate.setConnectionFactory( getConnectionFactory(this.ikasanJmsTemplate.getConnectionFactory(), username, password) );
        if(jmsProducer instanceof ArjunaJmsTemplateProducer)
        {
            ((ArjunaJmsTemplateProducer) jmsProducer).setLocalTransactionManager(arjunaTransactionManager);
        }
        return jmsProducer;
    }

    /**
     * Get the connectionFactory based on set properties.
     * @param connectionFactory
     * @param username
     * @param password
     * @return ConnectionFactory
     */
    private ConnectionFactory getConnectionFactory(ConnectionFactory connectionFactory, String username, String password)
    {
        // if connectionFactory and username specified then return a credentials based CF
        if(connectionFactory != null && username != null)
        {
            UserCredentialsConnectionFactoryAdapter cfCredentialsAdapter = getUserCredentialsConnectionFactoryAdapter();
            cfCredentialsAdapter.setTargetConnectionFactory(connectionFactory);
            cfCredentialsAdapter.setUsername(username);
            cfCredentialsAdapter.setPassword(password);
            return cfCredentialsAdapter;
        }

        return connectionFactory;
    }

    /**
     * Factory method to aid testing of this class
     * @return
     */
    protected UserCredentialsConnectionFactoryAdapter getUserCredentialsConnectionFactoryAdapter()
    {
        return new UserCredentialsConnectionFactoryAdapter();
    }

    /**
     * Factory method to get an instance of JmsTemplateProducer (and to aid testing).
     * @param ikasanJmsTemplate
     * @return
     */
    protected JmsTemplateProducer getJmsTemplateProducer(IkasanJmsTemplate ikasanJmsTemplate)
    {
        return new ArjunaJmsTemplateProducer(ikasanJmsTemplate);
    }
}


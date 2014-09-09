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
package org.ikasan.component.endpoint.jms.spring.producer;

import org.ikasan.component.endpoint.jms.AuthenticatedConnectionFactory;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.springframework.jms.core.IkasanJmsTemplate;
import org.springframework.jms.util.JndiUtils;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * Default JMS Producer component based on Spring JMS template.
 * @author Ikasan Development Team
 */
public class JmsTemplateProducer<T>
        implements Producer<T>, ConfiguredResource<SpringMessageProducerConfiguration>, ManagedResource
{
    /** instantiated jms template */
    private IkasanJmsTemplate jmsTemplate;

    /** configured resource Id handle */
    private String configuredResourceId;

    /** configuration */
    private SpringMessageProducerConfiguration configuration;

    /** critical to start of flow - default is false */
    private boolean isCritical;

    /**
     * Constructor
     * @param jmsTemplate
     */
    public JmsTemplateProducer(IkasanJmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
        if(jmsTemplate == null)
        {
            throw new IllegalArgumentException("jmsTemplate cannot be 'null'");
        }
    }

    @Override
    public void invoke(T message) throws EndpointException
    {
        try
        {
            this.jmsTemplate.convertAndSend(message);
        }
        catch(RuntimeException e)
        {
            throw new EndpointException(e);
        }
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public SpringMessageProducerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public void setConfiguration(SpringMessageProducerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public void startManagedResource()
    {
        try
        {
            // get connection factory
            if(configuration.getConnectionFactoryUsername() == null)
            {
                ConnectionFactory connectionFactory = JndiUtils.getConnectionFactory(configuration.getConnectionFactoryJndiProperties(), configuration.getConnectionFactoryName());
                this.jmsTemplate.setConnectionFactory(connectionFactory);
            }
            else
            {
                ConnectionFactory connectionFactory = JndiUtils.getAuthenicatedConnectionFactory(configuration.getConnectionFactoryJndiProperties(), configuration.getConnectionFactoryName(), configuration.getConnectionFactoryUsername(), configuration.getConnectionFactoryPassword());
                this.jmsTemplate.setConnectionFactory(connectionFactory);
            }
        }
        catch(IllegalArgumentException e)
        {
            throw new RuntimeException("Check the configuration ConnectionFactoryName [" + configuration.getConnectionFactoryName() + "]", e);
        }

        try
        {
            // get destination
            Destination destination = JndiUtils.getDestination(configuration.getDestinationJndiProperties(), configuration.getDestinationJndiName());
            this.jmsTemplate.setDefaultDestination(destination);
        }
        catch(IllegalArgumentException e)
        {
            throw new RuntimeException("Check the configuration DestinationJndiName [" + configuration.getDestinationJndiName() + "]", e);
        }

        this.jmsTemplate.setPubSubDomain(configuration.getPubSubDomain());

        if(configuration.getDeliveryPersistent() != null)
        {
            this.jmsTemplate.setDeliveryPersistent(configuration.getDeliveryPersistent());
        }

        if(configuration.getDeliveryMode() != null)
        {
            this.jmsTemplate.setDeliveryMode(configuration.getDeliveryMode());
        }

        if(configuration.getSessionTransacted() != null)
        {
            this.jmsTemplate.setSessionTransacted(configuration.getSessionTransacted());
        }

        if(configuration.getExplicitQosEnabled() != null)
        {
            this.jmsTemplate.setExplicitQosEnabled(configuration.getExplicitQosEnabled());
        }

        if(configuration.getMessageIdEnabled() != null)
        {
            this.jmsTemplate.setMessageIdEnabled(configuration.getMessageIdEnabled());
        }

        if(configuration.getMessageTimestampEnabled() != null)
        {
            this.jmsTemplate.setMessageTimestampEnabled(configuration.getMessageTimestampEnabled());
        }

        if(configuration.getPriority() != null)
        {
            this.jmsTemplate.setPriority(configuration.getPriority());
        }

        if(configuration.getPubSubNoLocal() != null)
        {
            this.jmsTemplate.setPubSubNoLocal(configuration.getPubSubNoLocal());
        }

        if(configuration.getReceiveTimeout() != null)
        {
            this.jmsTemplate.setReceiveTimeout(configuration.getReceiveTimeout());
        }

        if(configuration.getSessionAcknowledgeMode() != null)
        {
            this.jmsTemplate.setSessionAcknowledgeMode(configuration.getSessionAcknowledgeMode());
        }

        if(configuration.getSessionAcknowledgeModeName() != null)
        {
            this.jmsTemplate.setSessionAcknowledgeModeName(configuration.getSessionAcknowledgeModeName());
        }

        if(configuration.getTimeToLive() != null)
        {
            this.jmsTemplate.setTimeToLive(configuration.getTimeToLive());
        }

        this.jmsTemplate.afterPropertiesSet();
    }

    @Override
    public void stopManagedResource()
    {
        // nothing to stop
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        // don't care about this here
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return this.isCritical;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.isCritical = criticalOnStartup;
    }

}

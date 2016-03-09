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
package org.springframework.jms.listener;

import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.IkasanListMessage;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.exclusion.service.IsExclusionServiceAware;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.exclusion.ExclusionService;
import org.springframework.jms.util.JndiUtils;

import javax.jms.*;

/**
 * Extend DefaultMessageListenerContainer to ensure standard defaults are set on the container being instantiated.
 *
 * @author Ikasan Development Team
 */
public class IkasanMessageListenerContainer extends DefaultMessageListenerContainer implements MessageProvider, Configured<SpringMessageConsumerConfiguration>, IsExclusionServiceAware
{
    /** configuration instance */
    SpringMessageConsumerConfiguration configuration;
    private ExclusionService exclusionService;

    /**
     * Constructor with preferred defaults.
     */
    public IkasanMessageListenerContainer()
    {
        super();
        setAutoStartup(false);
        setMaxConcurrentConsumers(1);
        setConcurrentConsumers(1);
        setCacheLevel(CACHE_CONNECTION);
    }

    /**
     * Stop Spring from failing on deployment if we dont have an initial configuration - that's ok.
     */
    @Override
    public void afterPropertiesSet()
    {
        try
        {
            super.afterPropertiesSet();
        }
        catch(IllegalArgumentException e)
        {
            logger.debug("Ignoring failed afterPropertiesSet()", e);
        }
    }

    @Override
    public SpringMessageConsumerConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(SpringMessageConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public void start()
    {
        try
        {
            // get connection factory
            if(configuration.getConnectionFactoryUsername() == null)
            {
                ConnectionFactory connectionFactory = JndiUtils.getConnectionFactory(configuration.getConnectionFactoryJndiProperties(), configuration.getConnectionFactoryName());
                setConnectionFactory(connectionFactory);
            }
            else
            {
                ConnectionFactory connectionFactory = JndiUtils.getAuthenicatedConnectionFactory(configuration.getConnectionFactoryJndiProperties(), configuration.getConnectionFactoryName(), configuration.getConnectionFactoryUsername(), configuration.getConnectionFactoryPassword());
                setConnectionFactory(connectionFactory);
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
            setDestination(destination);
        }
        catch(IllegalArgumentException e)
        {
            throw new RuntimeException("Check the configuration DestinationJndiName [" + configuration.getDestinationJndiName() + "]", e);
        }

        // get other stuff
        setPubSubDomain(configuration.getPubSubDomain());
        setDurableSubscriptionName(configuration.getDurableSubscriptionName());

        if(configuration.getDurable() != null)
        {
            setSubscriptionDurable(configuration.getDurable());
        }

        if(configuration.getSessionTransacted() != null)
        {
            setSessionTransacted(configuration.getSessionTransacted());
        }

        afterPropertiesSet();

        super.start();
    }

    /**
     * Attempt to recover a shared connection, if its used
     * @see #refreshSharedConnection()
     * @throws javax.jms.JMSException if the underlying provider cannot re-establish itself
     */
    public void recoverSharedConnection() throws JMSException
    {
        if (sharedConnectionEnabled())
        {
            refreshSharedConnection();
        }
    }

    /**
     * Build up a faux JMS message containing multiple instances of JMS messages consumed from the destination.
     * @param consumer
     * @return
     * @throws JMSException
     */
    @Override
    protected Message receiveMessage(MessageConsumer consumer) throws JMSException
    {
        if(this.configuration.isBatchMode() && exclusionService.isBlackListEmpty())
        {
            IkasanListMessage listMessage = new IkasanListMessage();
            while ( !append(listMessage, super.receiveMessage(consumer)) );

            if(listMessage.size() == 0) return null;

            // base the jms id off of the first messages jms id + batch size
            listMessage.setJMSMessageID(listMessage.get(0).getJMSMessageID() + ":" + this.configuration.getBatchSize());
            return listMessage;
        }

        return super.receiveMessage(consumer);
    }

    /**
     * Consumer messages until no more or batch limit is reached.
     * @param listMessage
     * @param message
     * @return
     */
    public boolean append(IkasanListMessage listMessage, Message message)
    {
        if (message != null)
        {
            listMessage.add(message);
        }

        return message == null || listMessage.size() >= configuration.getBatchSize();
    }

    @Override
    public void setExclusionService(ExclusionService exclusionService) {

        this.exclusionService = exclusionService;
    }
}

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
package org.ikasan.component.endpoint.jms.spring.listener;

import org.ikasan.component.endpoint.jms.AuthenticatedConnectionFactory;
import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.util.JndiUtils;
import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.IkasanListMessage;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.exclusion.service.IsExclusionServiceAware;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.exclusion.ExclusionService;

import javax.jms.*;

import javax.transaction.TransactionManager;

/**
 * Extend DefaultMessageListenerContainer to ensure standard defaults are set on the container being instantiated.
 * And uses Arjun transaction Manager to ensuer JMS enroling to XA transaction by usage of ConnectionFactoryProxy
 * provided by Arjuna.
 *
 * @author Ikasan Development Team
 */
public class ArjunaIkasanMessageListenerContainer extends DefaultMessageListenerContainer
        implements MessageProvider, Configured<SpringMessageConsumerConfiguration>, IsExclusionServiceAware
{
    /** configuration instance */
    private SpringMessageConsumerConfiguration configuration;
    private ExclusionService exclusionService;
    private TransactionManager localTransactionManager;

    /**
     * Constructor with preferred defaults.
     */
    public ArjunaIkasanMessageListenerContainer()
    {
        super();
        setAutoStartup(false);
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

    @Override public void start()
    {
        try
        {
            // get connection factory
            if (getConfiguration().getConnectionFactoryUsername() == null)
            {
                ConnectionFactory connectionFactory = JndiUtils
                        .getConnectionFactory(getConfiguration().getConnectionFactoryJndiProperties(),
                                getConfiguration().getConnectionFactoryName());
//                ConnectionFactory connectionFactoryProxy = new ConnectionFactoryProxy(connectionFactory,
//                        new TransactionHelperImpl(getTransactionManager()));
                if(connectionFactory instanceof XAConnectionFactory){
                    ConnectionFactory connectionFactoryProxy = new ConnectionFactoryProxy(
                            (XAConnectionFactory) connectionFactory,
                            new TransactionHelperImpl(localTransactionManager)
                    );

                    setConnectionFactory(connectionFactoryProxy);
                }
                else
                {
                    setConnectionFactory(connectionFactory);
                }

            }
            else
            {

                ConnectionFactory connectionFactory = JndiUtils.getConnectionFactory(getConfiguration().getConnectionFactoryJndiProperties(),
                        getConfiguration().getConnectionFactoryName());

                if(connectionFactory instanceof XAConnectionFactory){
                    ConnectionFactory connectionFactoryProxy = new ConnectionFactoryProxy(
                            (XAConnectionFactory) connectionFactory,
                            new TransactionHelperImpl(localTransactionManager)
                            );
                    AuthenticatedConnectionFactory authenticatedConnectionFactory = new AuthenticatedConnectionFactory();
                    authenticatedConnectionFactory.setConnectionFactory(connectionFactoryProxy);
                    authenticatedConnectionFactory.setUsername(getConfiguration().getConnectionFactoryUsername());
                    authenticatedConnectionFactory.setPassword(getConfiguration().getConnectionFactoryPassword());
                    setConnectionFactory(authenticatedConnectionFactory);
                }
                else
                {
                    setConnectionFactory(connectionFactory);
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(
                    "Check the configuration ConnectionFactoryName [" + getConfiguration().getConnectionFactoryName() + "]",
                    e);
        }
        try
        {
            // get destination
            Destination destination = JndiUtils.getDestination(getConfiguration().getDestinationJndiProperties(),
                    getConfiguration().getDestinationJndiName());
            setDestination(destination);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(
                    "Check the configuration DestinationJndiName [" + getConfiguration().getDestinationJndiName() + "]", e);
        }
        // get other stuff
        setPubSubDomain(getConfiguration().getPubSubDomain());
        setDurableSubscriptionName(getConfiguration().getDurableSubscriptionName());
        setConcurrentConsumers(getConfiguration().getConcurrentConsumers());
        setMaxConcurrentConsumers(getConfiguration().getMaxConcurrentConsumers());
        setCacheLevel(getConfiguration().getCacheLevel());
        if (getConfiguration().getDurable() != null)
        {
            setSubscriptionDurable(getConfiguration().getDurable());
        }
        if (getConfiguration().getSessionTransacted() != null)
        {
            setSessionTransacted(getConfiguration().getSessionTransacted());
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
        if(this.configuration.isBatchMode())
        {
            // if we are batching we consistently need to return an IkasanListMessage
            IkasanListMessage listMessage = new IkasanListMessage();

            if(exclusionService.isBlackListEmpty())
            {
                // no exclusions so batch as normal
                // batch msgs until no more available or batch size limit hit
                while ( !append(listMessage, super.receiveMessage(consumer)) );
            }
            else
            {
                // we have exclusions, and we auto split then send single events until exclusion is cleared
                if(this.configuration.isAutoSplitBatch())
                {
                    Message msg = super.receiveMessage(consumer);
                    if(msg != null)
                    {
                        listMessage.add(msg);
                    }
                }
                else
                {
                    // we have exclusions, but are we treating the batch as one event which should not be auto split

                    // batch msgs until no more available or batch size limit hit or it matches an id registered in the exclusion service
                    while ( !append(listMessage, super.receiveMessage(consumer)) && !exclusionService.isBlackListed(listMessage.get(0).getJMSMessageID() + ":" + listMessage.size()) );
                }
            }

            if(listMessage.size() == 0) return null;

            // IMPORTANT - base the jms id (which will potentially be the eventLifeIdentifier) off of something which is repeatably constructable
            // in this case the first messages jms id + the number of messages in the batch
            // if we are batching and auto-splitting (regardless of exclusion) then it doesn't matter what is set on the listMessage as that is not used later by the invoker
            listMessage.setJMSMessageID(listMessage.get(0).getJMSMessageID() + ":" + listMessage.size());

            return listMessage;
        }

        // batching not turned on so just return a single JMS message instance
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
    public TransactionManager getLocalTransactionManager()
    {
        return localTransactionManager;
    }

    public void setLocalTransactionManager(TransactionManager localTransactionManager)
    {
        this.localTransactionManager = localTransactionManager;
    }
}


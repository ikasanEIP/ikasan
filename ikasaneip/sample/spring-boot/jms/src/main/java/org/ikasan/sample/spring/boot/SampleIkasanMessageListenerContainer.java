package org.ikasan.sample.spring.boot;

import org.ikasan.component.endpoint.jms.AuthenticatedConnectionFactory;
import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.IkasanListMessage;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.exclusion.service.IsExclusionServiceAware;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.exclusion.ExclusionService;
import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.util.JndiUtils;

import javax.jms.*;
import javax.transaction.TransactionManager;

/**
 * Created by majean on 03/11/2016.
 */
public class SampleIkasanMessageListenerContainer extends DefaultMessageListenerContainer
        implements MessageProvider, Configured<SpringMessageConsumerConfiguration>, IsExclusionServiceAware
{
    /**
     * configuration instance
     */
    private SpringMessageConsumerConfiguration configuration;

    private ExclusionService exclusionService;

    private TransactionManager localTransactionManager;

    /**
     * Constructor with preferred defaults.
     */
    public SampleIkasanMessageListenerContainer()
    {
        super();
        setAutoStartup(false);
    }

    /**
     * Stop Spring from failing on deployment if we dont have an initial configuration - that's ok.
     */
    @Override public void afterPropertiesSet()
    {
        try
        {
            super.afterPropertiesSet();
        }
        catch (IllegalArgumentException e)
        {
            logger.debug("Ignoring failed afterPropertiesSet()", e);
        }
    }

    @Override public SpringMessageConsumerConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override public void setConfiguration(SpringMessageConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override public void start()
    {
        try
        {
            // get connection factory
            if (configuration.getConnectionFactoryUsername() == null)
            {
                ConnectionFactory connectionFactory = JndiUtils
                        .getConnectionFactory(configuration.getConnectionFactoryJndiProperties(),
                                configuration.getConnectionFactoryName());
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

                ConnectionFactory connectionFactory = JndiUtils.getConnectionFactory(configuration.getConnectionFactoryJndiProperties(),
                        configuration.getConnectionFactoryName());

                if(connectionFactory instanceof XAConnectionFactory){
                    ConnectionFactory connectionFactoryProxy = new ConnectionFactoryProxy(
                            (XAConnectionFactory) connectionFactory,
                            new TransactionHelperImpl(localTransactionManager)
                            );
                    AuthenticatedConnectionFactory authenticatedConnectionFactory = new AuthenticatedConnectionFactory();
                    authenticatedConnectionFactory.setConnectionFactory(connectionFactoryProxy);
                    authenticatedConnectionFactory.setUsername(configuration.getConnectionFactoryUsername());
                    authenticatedConnectionFactory.setPassword(configuration.getConnectionFactoryPassword());
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
                    "Check the configuration ConnectionFactoryName [" + configuration.getConnectionFactoryName() + "]",
                    e);
        }
        try
        {
            // get destination
            Destination destination = JndiUtils.getDestination(configuration.getDestinationJndiProperties(),
                    configuration.getDestinationJndiName());
            setDestination(destination);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(
                    "Check the configuration DestinationJndiName [" + configuration.getDestinationJndiName() + "]", e);
        }
        // get other stuff
        setPubSubDomain(configuration.getPubSubDomain());
        setDurableSubscriptionName(configuration.getDurableSubscriptionName());
        setConcurrentConsumers(configuration.getConcurrentConsumers());
        setMaxConcurrentConsumers(configuration.getMaxConcurrentConsumers());
        setCacheLevel(configuration.getCacheLevel());
        if (configuration.getDurable() != null)
        {
            setSubscriptionDurable(configuration.getDurable());
        }
        if (configuration.getSessionTransacted() != null)
        {
            setSessionTransacted(configuration.getSessionTransacted());
        }
        afterPropertiesSet();
        super.start();
    }

    /**
     * Attempt to recover a shared connection, if its used
     *
     * @throws JMSException if the underlying provider cannot re-establish itself
     * @see #refreshSharedConnection()
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
     *
     * @param consumer
     * @return
     * @throws JMSException
     */
    @Override protected Message receiveMessage(MessageConsumer consumer) throws JMSException
    {
        if (this.configuration.isBatchMode() && exclusionService.isBlackListEmpty())
        {
            IkasanListMessage listMessage = new IkasanListMessage();
            while (!append(listMessage, super.receiveMessage(consumer)))
                ;
            if (listMessage.size() == 0)
                return null;
            // base the jms id off of the first messages jms id + batch size
            listMessage.setJMSMessageID(listMessage.get(0).getJMSMessageID() + ":" + this.configuration.getBatchSize());
            return listMessage;
        }
        return super.receiveMessage(consumer);
    }

    /**
     * Consumer messages until no more or batch limit is reached.
     *
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

    @Override public void setExclusionService(ExclusionService exclusionService)
    {
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


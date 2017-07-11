package org.ikasan.component.endpoint.jms.spring.listener;

import org.ikasan.component.endpoint.jms.AuthenticatedConnectionFactory;
import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.springframework.jms.listener.IkasanMessageListenerContainer;
import org.springframework.jms.util.JndiUtils;

import javax.jms.*;
import javax.transaction.TransactionManager;

/**
 * Created by majean on 03/11/2016.
 */
public class ArjunaIkasanMessageListenerContainer extends IkasanMessageListenerContainer
{

    private TransactionManager localTransactionManager;

    /**
     * Constructor with preferred defaults.
     */
    public ArjunaIkasanMessageListenerContainer()
    {
        super();
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

    public TransactionManager getLocalTransactionManager()
    {
        return localTransactionManager;
    }

    public void setLocalTransactionManager(TransactionManager localTransactionManager)
    {
        this.localTransactionManager = localTransactionManager;
    }
}


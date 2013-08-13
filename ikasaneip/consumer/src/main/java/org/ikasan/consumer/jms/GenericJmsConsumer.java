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
package org.ikasan.consumer.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.log4j.Logger;
import org.ikasan.consumer.EndpointListener;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.event.ManagedEventIdentifierException;
import org.ikasan.spec.management.ManagedIdentifierService;

/**
 * Implementation of a consumer based on the JMS specification.
 *
 * @author Ikasan Development Team
 */
public class GenericJmsConsumer 
    implements Consumer<EventListener<?>,EventFactory>, ManagedIdentifierService<ManagedEventIdentifierService>, EndpointListener<Message>,
    ConfiguredResource<GenericJmsConsumerConfiguration>
{
    /** class logger */
    private static Logger logger = Logger.getLogger(GenericJmsConsumer.class);

    /** JMS Connection Factory */
    protected ConnectionFactory connectionFactory;

    /** JMS Destination instance */
    protected Destination destination;

    /** JMS Connection */
    protected Connection connection;

    /** session has to be closed prior to connection being closed */
    protected Session session;

    /** consumer event factory */
    protected EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** consumer event listener */
    protected EventListener eventListener;

    /** default event identifier service - can be overridden via the setter */
    protected ManagedEventIdentifierService<?,Message> managedEventIdentifierService = new JmsEventIdentifierServiceImpl();

    /** configured resource id */
    protected String configuredResourceId;
    
    /** JMS consumer configuration - default to vanilla instance */
    protected GenericJmsConsumerConfiguration configuration = new GenericJmsConsumerConfiguration();
    
    /** tech endpoint listener for callbacks from the endpoint */
    protected MessageListener messageListener;
    
    /** destination resolver for locating and returning the configured destination instance */
    protected DestinationResolver destinationResolver;
    
    /** session message consumer */
    protected MessageConsumer messageConsumer;

    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     * @param flowEventFactory
     */
    public GenericJmsConsumer(ConnectionFactory connectionFactory, Destination destination)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
        
        this.destination = destination;
        if(destination == null)
        {
            throw new IllegalArgumentException("destination cannot be 'null'");
        }
    }

    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     * @param flowEventFactory
     */
    public GenericJmsConsumer(ConnectionFactory connectionFactory, DestinationResolver destinationResolver)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
        
        this.destinationResolver = destinationResolver;
        if(destinationResolver == null)
        {
            throw new IllegalArgumentException("destinationResolver cannot be 'null'");
        }
    }

    public void setMessageListener(MessageListener messageListener)
    {
        this.messageListener = messageListener;
    }
    
    public void setEventFactory(EventFactory flowEventFactory)
    {
        this.flowEventFactory = flowEventFactory;
    }
    
    /**
     * Start the underlying JMS
     */
    public void start()
    {
        try
        {
            if(this.configuration.getUsername() != null && this.configuration.getUsername().trim().length() > 0)
            {
                connection = connectionFactory.createConnection(this.configuration.getUsername(), this.configuration.getPassword());
            }
            else
            {
                connection = connectionFactory.createConnection();
            }

            connection.setClientID(this.configuration.getClientId());
            if(messageListener instanceof ExceptionListener)
            {
                connection.setExceptionListener( (ExceptionListener)messageListener );
            }

            this.session = connection.createSession(this.configuration.isTransacted(), this.configuration.getAcknowledgement());

            if(destination == null)
            {
                destination = destinationResolver.getDestination();
            }
            
            if(destination instanceof Topic && this.configuration.isDurable())
            {
                if(this.configuration.getSelector() != null)
                {
                    messageConsumer = session.createDurableSubscriber((Topic)destination, this.configuration.getSubscriberId(), this.configuration.getSelector(), this.configuration.isNoLocal());
                }
                else
                {
                    messageConsumer = session.createDurableSubscriber((Topic)destination, this.configuration.getSubscriberId());
                }
            }
            else
            {
                if(this.configuration.getSelector() != null)
                {
                    messageConsumer = session.createConsumer(destination, this.configuration.getSelector(), this.configuration.isNoLocal());
                }
                else
                {
                    messageConsumer = session.createConsumer(destination);
                }
            }
            
            messageConsumer.setMessageListener(messageListener);
            connection.start();
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop the underlying JMS
     */
    public void stop()
    {
        if(messageConsumer != null)
        {
            try
            {
                messageConsumer.close();
                messageConsumer = null;
            }
            catch (JMSException e)
            {
                logger.error("Failed to close session", e);
            }
        }
        
        if(session != null)
        {
            try
            {
                session.close();
                session = null;
            }
            catch (JMSException e)
            {
                logger.error("Failed to close session", e);
            }
        }

        if(connection != null)
        {
            try
            {
                connection.stop();
                connection = null;
            }
            catch (JMSException e)
            {
                throw new RuntimeException(e);
            }
        }

		// clear the destination if the resolver is available
        if(destinationResolver != null)
        {
            destination = null;
        }
    }

    /**
     * TODO - find a better way to ascertain if underlying JMS is running?
     * Is the underlying JMS actively running
     * @return boolean
     */
    public boolean isRunning()
    {
        return connection != null;
    }

    /**
     * Set the consumer event listener
     * @param eventListener
     */
    public void setListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    /**
     * Override the default consumer event life identifier service
     * @param eventLifeIdentifierService
     */
    public void setManagedIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.managedEventIdentifierService = managedEventIdentifierService;
    }

    /**
     * Callback method from the underlying JMS tech.
     * On invocation this method creates a flowEvent from the tech specific
     * message and invokes the event listener.
     */
    public void onMessage(Message message)
    {
        if(this.eventListener == null)
        {
            throw new RuntimeException("No active eventListeners registered!");
        }
        
        try
        {
            FlowEvent<?,?> flowEvent = flowEventFactory.newEvent( this.managedEventIdentifierService.getEventIdentifier(message), message);
            this.eventListener.invoke(flowEvent);
        }
        catch (ManagedEventIdentifierException e)
        {
            this.eventListener.invoke(e);
        }
    }

    /**
     * Callback method from the JMS connector for exception reporting.
     * @param JMSException
     */
    public void onException(Throwable jmsException)
    {
        this.eventListener.invoke(jmsException);
    }

    public GenericJmsConsumerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    public void setConfiguration(GenericJmsConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.component.endpoint.Consumer#getEventFactory()
     */
    public EventFactory getEventFactory()
    {
        return this.flowEventFactory;
    }

}

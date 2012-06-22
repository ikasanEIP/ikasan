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
import org.ikasan.spec.endpoint.EndpointListener;
import org.ikasan.spec.endpoint.ManagedEndpoint;

/**
 * Implementation of a JMS implemented endpoint consumer.
 *
 * @author Ikasan Development Team
 */
public class GenericJmsEndpointConsumer 
    implements MessageListener, ExceptionListener, ManagedEndpoint<GenericJmsConsumerConfiguration>
{
    /** class logger */
    private static Logger logger = Logger.getLogger(GenericJmsEndpointConsumer.class);

    /** JMS Connection Factory */
    protected ConnectionFactory connectionFactory;

    /** JMS Destination instance */
    protected Destination destination;

    /** JMS Connection */
    protected Connection connection;

    /** session has to be closed prior to connection being closed */
    protected Session session;

    /** JMS consumer configuration - default to vanilla instance */
    protected GenericJmsConsumerConfiguration configuration = new GenericJmsConsumerConfiguration();
    
    /** registered endpoint listener */
    private EndpointListener<Message> endpointListener;
    
    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     * @param flowEventFactory
     */
    public GenericJmsEndpointConsumer(ConnectionFactory connectionFactory, Destination destination)
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
     * Start the underlying JMS
     */
    public void startManagedEndpoint()
    {
        MessageConsumer messageConsumer;

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
            connection.setExceptionListener(this);

            this.session = connection.createSession(this.configuration.isTransacted(), this.configuration.getAcknowledgement());

            if(destination instanceof Topic && this.configuration.isDurable())
            {
                messageConsumer = session.createDurableSubscriber((Topic)destination, this.configuration.getSubscriberId());
            }
            else
            {
                messageConsumer = session.createConsumer(destination, this.configuration.getSubscriberId());
            }
            
            messageConsumer.setMessageListener(this);
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
    public void stopManagedEndpoint()
    {
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
    }

    /**
     * TODO - find a better way to ascertain if underlying JMS is running?
     * Is the underlying JMS actively running
     * @return boolean
     */
    public boolean isActive()
    {
        return connection != null;
    }

    /**
     * Set the consumer event listener
     * @param eventListener
     */
    public void setListener(EndpointListener endpointListener)
    {
        this.endpointListener = endpointListener;
    }

    /**
     * Callback method from the underlying JMS tech.
     * On invocation this method creates a flowEvent from the tech specific
     * message and invokes the event listener.
     */
    public void onMessage(Message message)
    {
        endpointListener.onMessage(message);
    }

    /**
     * Callback method from the JMS connector for exception reporting.
     * @param JMSException
     */
    public void onException(JMSException jmsException)
    {
        endpointListener.onException(jmsException);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.management.ManagedEndpoint#setEndpointConfiguration(java.lang.Object)
     */
    public void setEndpointConfiguration(GenericJmsConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }
}

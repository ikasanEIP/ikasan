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

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Implementation of a consumer based on the JMS specifiation.
 *
 * @author Ikasan Development Team
 */
public class GenericJmsConsumer 
    implements Consumer<EventListener<?>>, MessageListener, ExceptionListener, ConfiguredResource<GenericJmsConsumerConfiguration>
{
    /** JMS Connection Factory */
    private ConnectionFactory connectionFactory;

    /** JMS Destination instance */
    private Destination destination;

    /** JMS Connection */
    private Connection connection;

    /** consumer event factory */
    private EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** consumer event listener */
    private EventListener eventListener;

    /** configured resource id */
    private String configuredResourceId;
    
    /** JMS consumer configuration */
    private GenericJmsConsumerConfiguration configuration;
    
    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     * @param flowEventFactory
     */
    public GenericJmsConsumer(ConnectionFactory connectionFactory, Destination destination, 
            EventFactory<FlowEvent<?,?>> flowEventFactory)
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
        
        this.flowEventFactory = flowEventFactory;
        if(flowEventFactory == null)
        {
            throw new IllegalArgumentException("flowEventFactory cannot be 'null'");
        }
    }
    
    /**
     * Start the underlying JMS
     */
    public void start()
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

            Session session = connection.createSession(this.configuration.isTransacted(), this.configuration.getAcknowledgement());

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
    public void stop()
    {
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
     * Callback method from the underlying JMS tech.
     * On invocation this method creates a flowEvent from the tech specific
     * message and invokes the event listener.
     */
    public void onMessage(Message message)
    {
        String uniqueId = null;
        try
        {
            uniqueId = message.getJMSMessageID();
            FlowEvent<?,?> flowEvent = flowEventFactory.newEvent(uniqueId, message);
            this.eventListener.invoke(flowEvent);
        }
        catch (JMSException e)
        {
            this.eventListener.invoke(e);
        }
    }

    /**
     * Callback method from the JMS connector for exception reporting.
     * @param JMSException
     */
    public void onException(JMSException jmsException)
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

}

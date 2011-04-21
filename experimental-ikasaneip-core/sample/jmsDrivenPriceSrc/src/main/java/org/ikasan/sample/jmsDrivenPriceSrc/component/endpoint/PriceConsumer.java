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
package org.ikasan.sample.jmsDrivenPriceSrc.component.endpoint;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechListener;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechMessage;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Implementation of a consumer which manages the tech and 
 * receives messages via the tech listener.
 *
 * @author Ikasan Development Team
 */
public class PriceConsumer implements Consumer<EventListener>, MessageListener, ConfiguredResource<JmsClientConsumerConfiguration>
{
    /** */
    private ConnectionFactory connectionFactory;

    /** */
    private Destination destination;

    /** */
    private Connection connection;

    /** */
    private Session session;
    
    /** */
    private MessageConsumer messageConsumer;
    
    /** consumer event factory */
    private EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** consumer event listener */
    private EventListener eventListener;

    /** configured resource id */
    private String configuredResourceId;
    
    /** configuration */
    private JmsClientConsumerConfiguration configuration;
    
    /**
     * Constructor
     * @param stubbedTechImpl
     * @param flowEventFactory
     */
    public PriceConsumer(ConnectionFactory connectionFactory, Destination destination, EventFactory<FlowEvent<?,?>> flowEventFactory)
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
    
    protected void handleConsumer(TopicConnectionFactory topicConnectionFactory, Topic topic) throws JMSException
    {
        if(this.configuration.getUsername().trim().length() == 0)
        {
            connection = topicConnectionFactory.createConnection();
        }
        else
        {
            connection = topicConnectionFactory.createConnection(this.configuration.getUsername(), this.configuration.getPassword());
        }

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        TopicSession session = ((TopicConnection)connection).createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        if(this.configuration.isDurable())
        {
            messageConsumer = session.createDurableSubscriber(topic, this.configuration.getSubscriberId());
        }
        else
        {
            messageConsumer = session.createConsumer(topic, this.configuration.getSubscriberId());
        }
        
        messageConsumer.setMessageListener(this);
        connection.start();
    }
    
    protected void handleConsumer(QueueConnectionFactory queueConnectionFactory, Queue queue)
    {
        
    }
    
    /**
     * Start the underlying tech
     */
    public void start()
    {
        try
        {
            if(destination instanceof Topic)
            {
                handleConsumer((TopicConnectionFactory)connectionFactory, (Topic)destination);
            }
            else
            {
                handleConsumer((QueueConnectionFactory)connectionFactory, (Queue)destination);
            }
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop the underlying tech
     */
    public void stop()
    {
        if(connection != null)
        {
            try
            {
                connection.stop();
            }
            catch (JMSException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Is the underlying tech actively running
     * @return isRunning
     */
    public boolean isRunning()
    {
        return false;
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
        }
        catch (JMSException e)
        {
            this.eventListener.invoke(e);
        }

        FlowEvent<?,?> flowEvent = flowEventFactory.newEvent(uniqueId, message);
        this.eventListener.invoke(flowEvent);
    }

    public JmsClientConsumerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    public void setConfiguration(JmsClientConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }
}

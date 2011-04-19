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
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
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
    /** consumer managed stubbed tech */
    private PriceTechImpl priceTechImpl;

    /** consumer event factory */
    private EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** consumer event listener */
    private EventListener eventListener;

    /** configured resource id */
    private String configuredResourceId;
    
    /** configuration */
    private JmsClientConsumerConfiguration configuration;
    
    private TopicConnection connection;
    
    /**
     * Constructor
     * @param stubbedTechImpl
     * @param flowEventFactory
     */
    public PriceConsumer(EventFactory<FlowEvent<?,?>> flowEventFactory)
    {
        this.flowEventFactory = flowEventFactory;
    }
    
    /**
     * Start the underlying tech
     */
    public void start()
    {
        try
        {
            Context ctx = createContext();
            Object connectionFactory = ctx.lookup(this.configuration.getConnectionFactory());
            TopicConnectionFactory tcf = (TopicConnectionFactory)connectionFactory;

            Object destinationName = ctx.lookup(this.configuration.getDestination());
            Topic destination = (Topic)destinationName;

//            connection = tcf.createTopicConnection();
            connection = tcf.createTopicConnection(this.configuration.getUsername(), this.configuration.getPassword());
            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSubscriber subscriber = session.createSubscriber(destination);
            subscriber.setMessageListener(this);
            connection.start();
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e);
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

    
    private Context createContext() throws NamingException
    {
        Hashtable hashTable = new Hashtable();
        hashTable.put(this.configuration.INITIAL_CONTEXT_FACTORY, this.configuration.getInitialContextFactory());
        hashTable.put(this.configuration.PROVIDER_URL, this.configuration.getProviderUrl());
        hashTable.put(this.configuration.FACTORY_URL_PKGS, this.configuration.getFactoryUrl());
        
        return new InitialContext(hashTable);
    }
}

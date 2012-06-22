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

import javax.jms.JMSException;
import javax.jms.Message;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.endpoint.EndpointListener;
import org.ikasan.spec.endpoint.ManagedEndpoint;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Implementation of a consumer based on the JMS specification.
 *
 * @author Ikasan Development Team
 */
public class GenericConsumer 
    implements Consumer<EventListener<?>>, EndpointListener<Message>
{
    /** consumer event factory */
    protected EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** consumer event listener */
    protected EventListener eventListener;

    /** configured resource id */
    protected String configuredResourceId;
    
    /** JMS consumer configuration - default to vanilla instance */
    protected GenericJmsConsumerConfiguration configuration = new GenericJmsConsumerConfiguration();
    
    /** actual tech endpoint being managed */
    private ManagedEndpoint managedEndpoint;

    /** tech endpoint listener for callbacks from the endpoint */
    private EndpointListener<Message> endpointListener;
    
    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     * @param flowEventFactory
     */
    public GenericConsumer(EventFactory<FlowEvent<?,?>> flowEventFactory, ManagedEndpoint managedEndpoint)
    {
        this.flowEventFactory = flowEventFactory;
        if(flowEventFactory == null)
        {
            throw new IllegalArgumentException("flowEventFactory cannot be 'null'");
        }
        
        this.managedEndpoint = managedEndpoint;
        if(managedEndpoint == null)
        {
            throw new IllegalArgumentException("managedEndpoint cannot be 'null'");
        }
    }

    public void setEndpointListener(EndpointListener endpointListener)
    {
        this.endpointListener = endpointListener;
    }
    
    /**
     * Start the underlying JMS
     */
    public void start()
    {
        managedEndpoint.setEndpointConfiguration(configuration);
        managedEndpoint.setListener(endpointListener);
        managedEndpoint.startManagedEndpoint();
    }

    /**
     * Stop the underlying JMS
     */
    public void stop()
    {
        managedEndpoint.stopManagedEndpoint();
        managedEndpoint.setListener(null);
    }

    /**
     * TODO - find a better way to ascertain if underlying JMS is running?
     * Is the underlying JMS actively running
     * @return boolean
     */
    public boolean isRunning()
    {
        return managedEndpoint.isActive();
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

}

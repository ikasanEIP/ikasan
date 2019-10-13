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
package org.ikasan.component.endpoint.jms.producer;

import org.ikasan.component.endpoint.jms.JmsEventIdentifierServiceImpl;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.flow.FlowEvent;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Map;

/**
 * Implementation for converting an object to a JMS message.
 *
 * @author Ikasan Development Team
 */
public class JmsPostProcessor<T> implements PostProcessor<T, Message>, Configured<GenericJmsProducerConfiguration>
{
    /** default event identifier service - can be overridden via the setter */
    private ManagedRelatedEventIdentifierService<String,Message> managedEventIdentifierService = new JmsEventIdentifierServiceImpl();

    /** configuration for this post processor */
    private GenericJmsProducerConfiguration configuration;

    /**
     * Allow override of the default managed identifier service
     * @param managedEventIdentifierService
     */
    public void setManagedEventIdentifierService(ManagedRelatedEventIdentifierService<String, Message> managedEventIdentifierService)
    {
        this.managedEventIdentifierService = managedEventIdentifierService;
    }

    @Override
    public void invoke(T originalMessage, Message jmsMessage)
    {
        if(originalMessage instanceof FlowEvent)
        {
            FlowEvent<String,?> flowEvent = (FlowEvent<String,?>)originalMessage;
            // carry the event identifier
            this.managedEventIdentifierService.setEventIdentifier(flowEvent.getIdentifier(), jmsMessage);
            // carry the related event identifier if available
            this.managedEventIdentifierService.setRelatedEventIdentifier(flowEvent.getRelatedIdentifier(), jmsMessage);

        }

        if(this.configuration != null)
        {
            try
            {
                setMessageProperties(jmsMessage, this.configuration.getProperties());
            }
            catch(JMSException e)
            {
                throw new TransformationException(e);
            }
        }
    }

    /**
     * Set the specified properties in the message.
     * @param properties
     * @throws javax.jms.JMSException
     */
    protected void setMessageProperties(Message message, Map<String,?> properties) throws JMSException
    {
        if(properties != null)
        {
            for(Map.Entry<String,?> entry : properties.entrySet())
            {
                Object value = entry.getValue();
                if(value instanceof String)
                {
                    message.setStringProperty(entry.getKey(), (String)value);
                }
                else if(value instanceof Integer)
                {
                    message.setIntProperty(entry.getKey(), (Integer)value);
                }
                else if(value instanceof Boolean)
                {
                    message.setBooleanProperty(entry.getKey(), (Boolean)value);
                }
                else if(value instanceof Byte)
                {
                    message.setByteProperty(entry.getKey(), (Byte)value);
                }
                else if(value instanceof Double)
                {
                    message.setDoubleProperty(entry.getKey(), (Double)value);
                }
                else if(value instanceof Float)
                {
                    message.setFloatProperty(entry.getKey(), (Float)value);
                }
                else if(value instanceof Long)
                {
                    message.setLongProperty(entry.getKey(), (Long)value);
                }
                else if(value instanceof Short)
                {
                    message.setShortProperty(entry.getKey(), (Short)value);
                }
                else
                {
                    message.setObjectProperty(entry.getKey(), value);
                }
            }
        }

    }

    @Override
    public GenericJmsProducerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public void setConfiguration(GenericJmsProducerConfiguration configuration)
    {
        this.configuration = configuration;
    }
}

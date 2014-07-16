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
package org.ikasan.component.endpoint.jms.spring.producer;

import org.ikasan.component.endpoint.jms.producer.PostProcessor;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.FlowEvent;
import org.springframework.jms.core.IkasanJmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * Default JMS Producer component based on Spring JMS template.
 * @author Ikasan Development Team
 */
public class JmsTemplateProducer<T>
        implements Producer<T>
{
    /** instantiated jms template */
    private IkasanJmsTemplate jmsTemplate;

    /** target destination */
    private Destination destination;

    /**
     * Constructor
     * @param connectionFactory
     * @param destination
     */
    public JmsTemplateProducer(ConnectionFactory connectionFactory, Destination destination)
    {
        this.jmsTemplate = new IkasanJmsTemplate(connectionFactory);
        if(jmsTemplate == null)
        {
            throw new IllegalArgumentException("jmsTemplate cannot be 'null'");
        }

        this.destination = destination;
        if(destination == null)
        {
            throw new IllegalArgumentException("destination cannot be 'null'");
        }
    }

    /**
     * Setter for the post processor
     * @param postProcessor
     */
    public void setPostProcessor(PostProcessor postProcessor)
    {
        this.jmsTemplate.setPostProcessor(postProcessor);
    }

    /**
     * Override default message converter
     * @param messageConverter
     */
    public void setMessageConverter(MessageConverter messageConverter)
    {
        this.jmsTemplate.setMessageConverter(messageConverter);
    }

    @Override
    public void invoke(T message) throws EndpointException
    {
        try
        {
        	
            this.jmsTemplate.convertAndSend(this.destination, message); 
        }
        catch(RuntimeException e)
        {
            throw new EndpointException(e);
        }
    }

    /**
     * Extract the payload based on event coming in.
     * @param message
     * @return
     */
    protected Object getPayload(Object message)
    {
        if(message instanceof FlowEvent)
        {
            return ((FlowEvent) message).getPayload();
        }

        return message;
    }
}

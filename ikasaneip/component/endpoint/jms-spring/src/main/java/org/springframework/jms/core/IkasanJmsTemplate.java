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
package org.springframework.jms.core;

import org.ikasan.component.endpoint.jms.producer.JmsPostProcessor;
import org.ikasan.component.endpoint.jms.producer.PostProcessor;
import org.ikasan.spec.flow.FlowEvent;
import org.springframework.jms.JmsException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.*;
import javax.jms.IllegalStateException;

/**
 * Ikasan based implementation around the standard Spring JMS template.
 *
 * @author Ikasan Development Team
 */
public class IkasanJmsTemplate extends JmsTemplate
{
    /** specific post processor implementation to be applied - ensure a default post-processor */
    private PostProcessor<Object,Message> postProcessor = new JmsPostProcessor();

    /**
     * Default constructor
     */
    public IkasanJmsTemplate()
    {
        // nothing to do here
    }

    /**
     * Setter for the postProcessor
     * @param postProcessor
     */
    public void setPostProcessor(PostProcessor postProcessor)
    {
        this.postProcessor = postProcessor;
    }

    /**
     * Stop Spring from failing on deployment if we dont have an initial configuration - that's ok.
     */
    @Override
    public void afterPropertiesSet()
    {
        try
        {
            super.afterPropertiesSet();
        }
        catch(IllegalArgumentException e)
        {
            logger.debug("Ignoring failed afterPropertiesSet()", e);
        }
    }

    @Override
    public void convertAndSend(final Object message) throws JmsException
    {
        send(new MessageCreator()
        {
            public Message createMessage(Session session) throws JMSException
            {
            	
                Message msg = getRequiredMessageConverter().toMessage(getPayload(message), session);
                if(postProcessor != null)
                {
                    postProcessor.invoke(message, msg);
                }
                return msg;
            }
        });
    }

    private MessageConverter getRequiredMessageConverter() throws IllegalStateException {
        MessageConverter converter = getMessageConverter();
        if (converter == null) {
            throw new IllegalStateException("No 'messageConverter' specified. Check configuration of JmsTemplate.");
        }
        return converter;
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

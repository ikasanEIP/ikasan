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
package org.ikasan.tools.messaging.publisher;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.NamingException;

import org.ikasan.tools.messaging.destination.resolution.DestinationResolver;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TextMessagePublisherImpl implements TextMessagePublisher
{

    private JmsTemplate jmsTemplate;

    private DestinationResolver jndiDestinationResolver;

    public TextMessagePublisherImpl(ConnectionFactory connectionFactory, DestinationResolver jndiDestinationResolver)
    {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
        this.jndiDestinationResolver = jndiDestinationResolver;
    }

    public void publishTextMessage(String destinationPath, final String messageText, final int priority)
    {
        try
        {
            // explicit QoS must be set when defined priority, deliveryMode, or timeToLive
            jmsTemplate.setPriority(priority);
            jmsTemplate.setExplicitQosEnabled(true);

            jmsTemplate.send(jndiDestinationResolver.getDestination(destinationPath), new MessageCreator()
            {

                public Message createMessage(Session session) throws JMSException
                {
                    return session.createTextMessage(messageText);
                }
            });
        }
        catch (JmsException e)
        {
            throw new RuntimeException(e);
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e);
        }

    }

}

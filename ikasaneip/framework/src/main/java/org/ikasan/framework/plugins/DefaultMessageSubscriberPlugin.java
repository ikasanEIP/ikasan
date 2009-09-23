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
package org.ikasan.framework.plugins;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.Spec;
import org.ikasan.common.component.UnknownMessageContentException;
import org.ikasan.common.factory.EnvelopeFactory;
import org.ikasan.common.factory.JMSMessageFactory;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.FrameworkException;
import org.ikasan.framework.ResourceLoader;
import org.ikasan.framework.component.Event;

/**
 * Default plugin that deals with subscribing to messages that are payload
 * messages
 * 
 * @author Ikasan Development Team
 */
public class DefaultMessageSubscriberPlugin
{

    /**
     * Factory for instantiating <code>Payload</code>s
     */
    private PayloadFactory payloadFactory;
   
    /**
     * Factory for <code>Envelope</code>s
     */
    private EnvelopeFactory envelopeFactory;
    
    /**
     * Factory for deserialising Payloads
     */
    private JMSMessageFactory jmsMessageFactory;
    
    
    /** Logger */
    private static Logger logger = Logger.getLogger(DefaultMessageSubscriberPlugin.class);
    
    /**
     * Constructor
     * 
     * @param envelopeFactory - factory for <code>Envelope</code>s
     * @param payloadFactory - factory for <code>Payload</code>s
     * @param jmsMessageFactory - factory for <code>JMSMessage</code>s
     */
    public DefaultMessageSubscriberPlugin(EnvelopeFactory envelopeFactory, PayloadFactory payloadFactory, JMSMessageFactory jmsMessageFactory)
    {
        super();
        this.envelopeFactory = envelopeFactory;
        this.payloadFactory = payloadFactory;
        this.jmsMessageFactory = jmsMessageFactory;
    }
    
    /**
     * Constructor
     */
    public DefaultMessageSubscriberPlugin(){
        //hack because the old Plugin Handler cannot inject dependencies
        this(ResourceLoader.getInstance().getEnvelopeFactory(), ResourceLoader.getInstance().getPayloadFactory(), ResourceLoader.getInstance().getJMSMessageFactory()); 
    }

    /**
     * @param event 
     * @param message
     * @throws FrameworkException 
     */
    public void handle(Event event, Message message) 
        throws FrameworkException
    {
        Envelope envelope = null;
        try
        {
            envelope = envelopeFactory.fromMessage(message);
            List<Payload> payloads = envelope.getPayloads();
            event.setPayloads(payloads);
            event.setEventAttribsFromEnvelope(envelope);
            logger.debug("Returned an event created via an envelope."); //$NON-NLS-1$
        }
        catch (UnknownMessageContentException e)
        {
            // Is it a payload / list of payloads?
            try
            {
                List<Payload> payloads = jmsMessageFactory.fromMessage(message);
                event.setPayloads(payloads);
                logger.debug("Returned an event with the payloads from the message."); //$NON-NLS-1$
            }
            catch (UnknownMessageContentException e1)
            {
                // Last chance if its a TextMessages
                // we can only use content as we know nothing else
                if (message instanceof TextMessage)
                {
                    TextMessage tm = (TextMessage) message;
                    try
                    {
                        // TODO - we can only guess at spec
                        Payload payload = payloadFactory.newPayload("textMessage", Spec.TEXT_XML,
                            MetaDataInterface.UNDEFINED, tm.getText().getBytes());
                        event.setPayload(payload);
                        logger.debug("Returned an event with the payload from the text message."); //$NON-NLS-1$
                    }
                    catch (JMSException e2)
                    {
                        throw new FrameworkException(e2);
                    }
                }
                throw new FrameworkException(e1);
            }
            catch (PayloadOperationException e1)
            {
                throw new FrameworkException(e1);
            }
            catch (JMSException e1)
            {
                throw new FrameworkException(e1);
            }
        }
        catch (EnvelopeOperationException e)
        {
            throw new FrameworkException(e);
        }
        catch (PayloadOperationException e)
        {
            throw new FrameworkException(e);
        }
        catch (JMSException e1)
        {
            throw new FrameworkException(e1);
        }
    }

}

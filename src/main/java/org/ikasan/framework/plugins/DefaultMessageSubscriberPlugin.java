/*
 * $Id: DefaultMessageSubscriberPlugin.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/plugins/DefaultMessageSubscriberPlugin.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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

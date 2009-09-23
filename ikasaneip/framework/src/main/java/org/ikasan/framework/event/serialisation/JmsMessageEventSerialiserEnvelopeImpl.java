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
package org.ikasan.framework.event.serialisation;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.UnknownMessageContentException;
import org.ikasan.common.factory.EnvelopeFactory;
import org.ikasan.common.factory.JMSMessageFactory;
import org.ikasan.framework.component.Event;

/**
 * Implementation of <code>jmsMessageEventSerialiser</code> that serialises and deserialises through the use of the
 * Envelope class.
 * 
 * Such usage of Envelope is being deprecated.
 * 
 * TODO replace me with an implementation that does not rely on Envelope
 * 
 * @author Ikasan Development Team
 */
public class JmsMessageEventSerialiserEnvelopeImpl implements JmsMessageEventSerialiser
{
    /** The envelope factory */
    private EnvelopeFactory envelopeFactory;

    /** The JMS message factory */
    private JMSMessageFactory jmsMessageFactory;

    /**
     * Constructor
     * 
     * @param envelopeFactory The envelope factory to use
     * @param jmsMessageFactory The JMS message factory to use
     */
    public JmsMessageEventSerialiserEnvelopeImpl(EnvelopeFactory envelopeFactory, JMSMessageFactory jmsMessageFactory)
    {
        super();
        this.envelopeFactory = envelopeFactory;
        this.jmsMessageFactory = jmsMessageFactory;
    }

    public Event fromMapMessage(MapMessage mapMessage, String moduleName, String componentName)
            throws EventSerialisationException
    {
        Event event = new Event(moduleName, componentName);
        try
        {
            Envelope envelope = envelopeFactory.fromMessage(mapMessage);
            List<Payload> payloads = envelope.getPayloads();
            event.setPayloads(payloads);
            event.setEventAttribsFromEnvelope(envelope);
        }
        catch (EnvelopeOperationException e)
        {
            throw new EventSerialisationException(e);
        }
        catch (UnknownMessageContentException e)
        {
            throw new EventSerialisationException(e);
        }
        catch (PayloadOperationException e)
        {
            throw new EventSerialisationException(e);
        }
        catch (JMSException e)
        {
            throw new EventSerialisationException(e);
        }
        return event;
    }

    public MapMessage toMapMessage(Event event, Session session) throws EventSerialisationException
    {
        Envelope envelope = event.getEnvelope(envelopeFactory);
        MapMessage result = null;
        try
        {
            result = jmsMessageFactory.envelopeToMapMessage(envelope, session);
        }
        catch (EnvelopeOperationException e)
        {
            throw new EventSerialisationException(e);
        }
        catch (PayloadOperationException e)
        {
            throw new EventSerialisationException(e);
        }
        return result;
    }
}

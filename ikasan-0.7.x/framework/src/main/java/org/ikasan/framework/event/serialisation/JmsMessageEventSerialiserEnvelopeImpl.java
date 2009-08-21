/*
 * $Id$
 * $URL$
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

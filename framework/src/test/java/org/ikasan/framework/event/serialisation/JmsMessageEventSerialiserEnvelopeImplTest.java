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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import junit.framework.Assert;

import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.Spec;
import org.ikasan.common.component.UnknownMessageContentException;
import org.ikasan.common.factory.EnvelopeFactory;
import org.ikasan.common.factory.JMSMessageFactory;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for JmsMessageEventSerialiserEnvelopeImpl
 * @author Ikasan Development Team
 */
public class JmsMessageEventSerialiserEnvelopeImplTest
{
    /**
     * Mockery for classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Envelope Factory
     */
    EnvelopeFactory envelopeFactory = mockery.mock(EnvelopeFactory.class);

    /**
     * JMS Message Factory
     */
    JMSMessageFactory jmsMessageFactory = mockery.mock(JMSMessageFactory.class);

    /**
     * System Under Test
     */
    private JmsMessageEventSerialiserEnvelopeImpl jmsMessageEventSerialiserEnvelopeImpl = new JmsMessageEventSerialiserEnvelopeImpl(envelopeFactory,
        jmsMessageFactory);

    /**
     * Tests the successful deserialisation
     * 
     * @throws EventSerialisationException
     * @throws UnknownMessageContentException
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws JMSException
     */
    @Test
    public void testFromMapMessage() throws EventSerialisationException, UnknownMessageContentException, EnvelopeOperationException, PayloadOperationException,
            JMSException
    {
        final MapMessage mapMessage = mockery.mock(MapMessage.class);
        final Envelope envelope = mockery.mock(Envelope.class);
        final Payload payload1 = mockery.mock(Payload.class, "payload1");
        final Payload payload2 = mockery.mock(Payload.class, "payload2");
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload1);
        payloads.add(payload2);
        String moduleName = "moduleName";
        String componentName = "componentName";
        mockery.checking(new Expectations()
        {
            {
                one(envelopeFactory).fromMessage(mapMessage);
                will(returnValue(envelope));
                one(envelope).getPayloads();
                will(returnValue(payloads));
                one(payload1).getName();
                will(returnValue("payload1Name"));
                one(payload1).getSpec();
                will(returnValue(Spec.TEXT_XML.toString()));
                one(payload1).getSrcSystem();
                will(returnValue("srcSystem1"));
                one(payload2).getName();
                will(returnValue("payload2Name"));
                one(payload2).getSpec();
                will(returnValue(Spec.TEXT_XML.toString()));
                one(payload2).getSrcSystem();
                will(returnValue("srcSystem2"));
                one(envelope).getId();
                will(returnValue("envelopeId"));
                one(envelope).getTimestamp();
                will(returnValue(1000l));
                one(envelope).getTimestampFormat();
                will(returnValue("envelopeTimestampFormat"));
                one(envelope).getTimezone();
                will(returnValue("envelopeTimezone"));
                one(envelope).getPriority();
                will(returnValue(99));
                one(envelope).getName();
                will(returnValue("envelopeName"));
                one(envelope).getSpec();
                will(returnValue(Spec.TEXT_HTML.toString()));
                one(envelope).getEncoding();
                will(returnValue("envelopEncoding"));
                one(envelope).getFormat();
                will(returnValue("envelopeFormat"));
                one(envelope).getCharset();
                will(returnValue("envelopeCharset"));
                one(envelope).getSize();
                will(returnValue(9999l));
                one(envelope).getChecksum();
                will(returnValue("envelopeChecksum"));
                one(envelope).getChecksumAlg();
                will(returnValue("envelopeChecksumAlg"));
                one(envelope).getSrcSystem();
                will(returnValue("envelopeSrcSystem"));
                one(envelope).getTargetSystems();
                will(returnValue("envelopeTargetSystems"));
                one(envelope).getProcessIds();
                will(returnValue("envelopeProcessIds"));
            }
        });
        Event event = jmsMessageEventSerialiserEnvelopeImpl.fromMapMessage(mapMessage, moduleName, componentName);
        Assert.assertEquals(moduleName, event.getComponentGroupName());
        Assert.assertEquals(componentName, event.getComponentName());
    }

    /**
     * Tests the deserialisation method throws a EventSerialisationException for
     * an underlying JMSException
     * 
     * @throws UnknownMessageContentException
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws JMSException
     */
    @Test
    public void testFromMapMessageThrowsEventSerialisationExceptionForJmsException() throws UnknownMessageContentException, EnvelopeOperationException,
            PayloadOperationException, JMSException
    {
        final MapMessage mapMessage = mockery.mock(MapMessage.class);
        mockery.checking(new Expectations()
        {
            {
                one(envelopeFactory).fromMessage(mapMessage);
                will(throwException(new JMSException("")));
            }
        });
        EventSerialisationException eventSerialisationException = null;
        try
        {
            jmsMessageEventSerialiserEnvelopeImpl.fromMapMessage(mapMessage, "", "");
            fail("should have thrown EventSerialisationException");
        }
        catch (EventSerialisationException e)
        {
            eventSerialisationException = e;
        }
        Assert.assertNotNull(eventSerialisationException);
    }

    /**
     * Tests the deserialisation method throws a EventSerialisationException for
     * an underlying PayloadOperationException
     * 
     * @throws UnknownMessageContentException
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws JMSException
     */
    @Test
    public void testFromMapMessageThrowsEventSerialisationExceptionForPayloadOperationException() throws UnknownMessageContentException,
            EnvelopeOperationException, PayloadOperationException, JMSException
    {
        final MapMessage mapMessage = mockery.mock(MapMessage.class);
        mockery.checking(new Expectations()
        {
            {
                one(envelopeFactory).fromMessage(mapMessage);
                will(throwException(new PayloadOperationException("")));
            }
        });
        EventSerialisationException eventSerialisationException = null;
        try
        {
            jmsMessageEventSerialiserEnvelopeImpl.fromMapMessage(mapMessage, "", "");
            fail("should have thrown EventSerialisationException");
        }
        catch (EventSerialisationException e)
        {
            eventSerialisationException = e;
        }
        Assert.assertNotNull(eventSerialisationException);
    }

    /**
     * Tests the deserialisation method throws a EventSerialisationException for
     * an underlying EnvelopeOperationException
     * 
     * @throws UnknownMessageContentException
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws JMSException
     */
    @Test
    public void testFromMapMessageThrowsEventSerialisationExceptionForEnvelopeOperationException() throws UnknownMessageContentException,
            EnvelopeOperationException, PayloadOperationException, JMSException
    {
        final MapMessage mapMessage = mockery.mock(MapMessage.class);
        mockery.checking(new Expectations()
        {
            {
                one(envelopeFactory).fromMessage(mapMessage);
                will(throwException(new EnvelopeOperationException("")));
            }
        });
        EventSerialisationException eventSerialisationException = null;
        try
        {
            jmsMessageEventSerialiserEnvelopeImpl.fromMapMessage(mapMessage, "", "");
            fail("should have thrown EventSerialisationException");
        }
        catch (EventSerialisationException e)
        {
            eventSerialisationException = e;
        }
        Assert.assertNotNull(eventSerialisationException);
    }

    /**
     * Tests the deserialisation method throws a EventSerialisationException for
     * an underlying UnknownMessageContentException
     * 
     * @throws UnknownMessageContentException
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws JMSException
     */
    @Test
    public void testFromMapMessageThrowsEventSerialisationExceptionForUnknownMessageContentException() throws UnknownMessageContentException,
            EnvelopeOperationException, PayloadOperationException, JMSException
    {
        final MapMessage mapMessage = mockery.mock(MapMessage.class);
        mockery.checking(new Expectations()
        {
            {
                one(envelopeFactory).fromMessage(mapMessage);
                will(throwException(new UnknownMessageContentException("")));
            }
        });
        EventSerialisationException eventSerialisationException = null;
        try
        {
            jmsMessageEventSerialiserEnvelopeImpl.fromMapMessage(mapMessage, "", "");
            fail("should have thrown EventSerialisationException");
        }
        catch (EventSerialisationException e)
        {
            eventSerialisationException = e;
        }
        Assert.assertNotNull(eventSerialisationException);
    }

    /**
     * Tests the happy path of generating a MapMessage from an Event
     * 
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws EventSerialisationException
     */
    @Test
    public void testToMapMessage() throws EnvelopeOperationException, PayloadOperationException, EventSerialisationException
    {
        final Session session = mockery.mock(Session.class);
        final Envelope envelope = mockery.mock(Envelope.class);
        final MapMessage mapMessage = mockery.mock(MapMessage.class);
        final Event event = mockery.mock(Event.class);
        mockery.checking(new Expectations()
        {
            {
                one(event).getEnvelope(envelopeFactory);
                will(returnValue(envelope));
                one(jmsMessageFactory).envelopeToMapMessage(envelope, session);
                will(returnValue(mapMessage));
            }
        });
        MapMessage result = jmsMessageEventSerialiserEnvelopeImpl.toMapMessage(event, session);
        Assert.assertEquals("should return mapMessage as from jmsMessageFactory", mapMessage, result);
    }

    /**
     * Tests unhappy path
     * 
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     */
    @Test
    public void testToMapMessageThrowsEventSerialisationExceptionForEnvelopeException() throws EnvelopeOperationException, PayloadOperationException
    {
        final Session session = mockery.mock(Session.class);
        final Envelope envelope = mockery.mock(Envelope.class);
        final Event event = mockery.mock(Event.class);
        mockery.checking(new Expectations()
        {
            {
                one(event).getEnvelope(envelopeFactory);
                will(returnValue(envelope));
                one(jmsMessageFactory).envelopeToMapMessage(envelope, session);
                will(throwException(new EnvelopeOperationException("")));
            }
        });
        EventSerialisationException eventSerialisationException = null;
        try
        {
            jmsMessageEventSerialiserEnvelopeImpl.toMapMessage(event, session);
            fail("should have thrown EventSerialisationException");
        }
        catch (EventSerialisationException e)
        {
            eventSerialisationException = e;
        }
        Assert.assertNotNull(eventSerialisationException);
    }

    /**
     * Tests unhappy path
     * 
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     */
    @Test
    public void testToMapMessageThrowsEventSerialisationExceptionForPayloadException() throws EnvelopeOperationException, PayloadOperationException
    {
        final Session session = mockery.mock(Session.class);
        final Envelope envelope = mockery.mock(Envelope.class);
        final Event event = mockery.mock(Event.class);
        mockery.checking(new Expectations()
        {
            {
                one(event).getEnvelope(envelopeFactory);
                will(returnValue(envelope));
                one(jmsMessageFactory).envelopeToMapMessage(envelope, session);
                will(throwException(new PayloadOperationException("")));
            }
        });
        EventSerialisationException eventSerialisationException = null;
        try
        {
            jmsMessageEventSerialiserEnvelopeImpl.toMapMessage(event, session);
            fail("should have thrown EventSerialisationException");
        }
        catch (EventSerialisationException e)
        {
            eventSerialisationException = e;
        }
        Assert.assertNotNull(eventSerialisationException);
    }
}

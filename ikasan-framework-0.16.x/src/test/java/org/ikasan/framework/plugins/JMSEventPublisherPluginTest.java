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
package org.ikasan.framework.plugins;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;
import org.ikasan.common.security.IkasanSecurityConf;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.messaging.jms.JndiDestinationFactory;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * @author Ikasan Development Team
 * 
 */
public class JMSEventPublisherPluginTest extends TestCase
{
    /**
     * Mockery for classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();

    /**
     * jms username
     */
    private final String jmsUsername = "jmsUsername";

    /**
     * jms password
     */
    private final String jmsPassword = "jmsPassword";

    /**
     * mock of the destination
     */
    final Destination destination = mockery.mock(Destination.class);

    /**
     * mock of the connection factory
     */
    final ConnectionFactory jmsConnectionFactory = mockery.mock(ConnectionFactory.class);

    /**
     * mock of the serialiser
     */
    final JmsMessageEventSerialiser jmsMessageEventSerialiser = mockery.mock(JmsMessageEventSerialiser.class);

    /**
     * mock of the security conf
     */
    final IkasanSecurityConf ikasanSecurityConf = mockery.mock(IkasanSecurityConf.class);

    /**
     * mock of the payload
     */
    final Payload payload = mockery.mock(Payload.class);

    /**
     * mock of the payloads
     */
    final List<Payload> payloads = new ArrayList<Payload>();

    /**
     * mock of the envelope
     */
    final Envelope envelope = mockery.mock(Envelope.class);

    /**
     * mock of the connection
     */
    final Connection connection = mockery.mock(Connection.class);

    /**
     * mock of the jms session
     */
    final Session session = mockery.mock(Session.class);

    /**
     * mock of the map message
     */
    final MapMessage mapMessage = mockery.mock(MapMessage.class);

    /**
     * mock of the message producer
     */
    final MessageProducer messageProducer = mockery.mock(MessageProducer.class);

    /**
     * mock of the event
     */
    final Event event = classMockery.mock(Event.class);

    /**
     * JMSException
     */
    final JMSException jmsException = new JMSException(null);

    /**
     * EventSerialisationException
     */
    final EventSerialisationException eventSerialisationException = new EventSerialisationException(null);

    /**
     * mocked Destination factory
     */
    final JndiDestinationFactory jndiDestinationFactory = mockery.mock(JndiDestinationFactory.class);
    /**
     * Constructor
     */
    public JMSEventPublisherPluginTest()
    {
        payloads.add(payload);
    }

    /**
     * Test method for
     * {@link org.ikasan.framework.plugins.JMSEventPublisherPlugin#invoke(org.ikasan.framework.component.Event)}
     * .
     * 
     * @throws PluginInvocationException
     * @throws JMSException
     * @throws EventSerialisationException
     */
    public void testInvoke_withSecurityPublishesWithSecureConnection() throws PluginInvocationException, JMSException, EventSerialisationException
    {
        classMockery.checking(new Expectations()
        {
            {
                // event has some payloads
                one(event).idToString();
                will(returnValue("dummy id list"));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(ikasanSecurityConf).getJMSUsername();
                will(returnValue(jmsUsername));
                one(ikasanSecurityConf).getJMSPassword();
                will(returnValue(jmsPassword));
                one(jmsConnectionFactory).createConnection(jmsUsername, jmsPassword);
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(returnValue(session));
                one(jmsMessageEventSerialiser).toMapMessage(event, session);
                will(returnValue(mapMessage));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(mapMessage);
                one(connection).close();
            }
        });
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(destination, jmsConnectionFactory, jmsMessageEventSerialiser,
            ikasanSecurityConf);
        eventPublisherPlugin.invoke(event);
    }

    /**
     * Test method for
     * {@link org.ikasan.framework.plugins.JMSEventPublisherPlugin#invoke(org.ikasan.framework.component.Event)}
     * with invocation of setPriority(Integer).
     * 
     * @throws PluginInvocationException
     * @throws JMSException
     * @throws EventSerialisationException
     */
    public void testInvoke_withSecurityWithPrioritySetter() throws PluginInvocationException, JMSException, EventSerialisationException
    {
        classMockery.checking(new Expectations()
        {
            {
                // event has some payloads
                one(event).idToString();
                will(returnValue("dummy id list"));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(ikasanSecurityConf).getJMSUsername();
                will(returnValue(jmsUsername));
                one(ikasanSecurityConf).getJMSPassword();
                will(returnValue(jmsPassword));
                one(jmsConnectionFactory).createConnection(jmsUsername, jmsPassword);
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(returnValue(session));
                one(jmsMessageEventSerialiser).toMapMessage(event, session);
                will(returnValue(mapMessage));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).setPriority(with(any(Integer.class)));
                one(messageProducer).send(mapMessage);
                one(connection).close();
            }
        });
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(destination, jmsConnectionFactory, jmsMessageEventSerialiser,
            ikasanSecurityConf);
        final Integer priority = new Integer(1);
        eventPublisherPlugin.setPriority(priority);
        eventPublisherPlugin.invoke(event);
    }

    /**
     * Test method for
     * {@link org.ikasan.framework.plugins.JMSEventPublisherPlugin#invoke(org.ikasan.framework.component.Event)}
     * .
     * 
     * @throws PluginInvocationException
     * @throws JMSException
     * @throws EventSerialisationException
     */
    public void testInvoke_withoutSecurityPublishesWithUnsecuredConnection() throws PluginInvocationException, JMSException, EventSerialisationException
    {
        classMockery.checking(new Expectations()
        {
            {
                one(event).idToString();
                will(returnValue("dummy id list"));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(jmsConnectionFactory).createConnection();
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(returnValue(session));
                one(jmsMessageEventSerialiser).toMapMessage(event, session);
                will(returnValue(mapMessage));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(mapMessage);
                one(connection).close();
            }
        });
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(destination, jmsConnectionFactory, jmsMessageEventSerialiser, null);
        eventPublisherPlugin.invoke(event);
    }

    /**
     * Test method for
     * {@link org.ikasan.framework.plugins.JMSEventPublisherPlugin#invoke(org.ikasan.framework.component.Event)}
     * .
     * 
     * @throws JMSException
     */
    public void testInvoke_throwsPluginInvocationExceptionWhenConnectionFactoryThrowsJMSException() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                one(jmsConnectionFactory).createConnection();
                will(throwException(jmsException));
                one(connection).close();
            }
        });
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(destination, jmsConnectionFactory, jmsMessageEventSerialiser, null);
        try
        {
            eventPublisherPlugin.invoke(event);
            fail("Exception should have been thrown");
        }
        catch (PluginInvocationException p)
        {
            assertTrue("underlyingException should be the JMSException", jmsException.equals(p.getCause()));
        }
    }

    /**
     * Test method for
     * {@link org.ikasan.framework.plugins.JMSEventPublisherPlugin#invoke(org.ikasan.framework.component.Event)}
     * .
     * 
     * @throws JMSException
     */
    public void testInvoke_throwsPluginInvocationExceptionWhenConnectionThrowsJMSException() throws JMSException
    {
        mockery.checking(new Expectations()
        {
            {
                one(jmsConnectionFactory).createConnection();
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(throwException(jmsException));
                one(connection).close();
            }
        });
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(destination, jmsConnectionFactory, jmsMessageEventSerialiser, null);
        try
        {
            eventPublisherPlugin.invoke(event);
            fail("Exception should have been thrown");
        }
        catch (PluginInvocationException p)
        {
            assertTrue("underlyingException should be the JMSException", jmsException.equals(p.getCause()));
        }
    }

    /**
     * Test method for
     * {@link org.ikasan.framework.plugins.JMSEventPublisherPlugin#invoke(org.ikasan.framework.component.Event)}
     * .
     * 
     * @throws JMSException
     * @throws EventSerialisationException
     */
    public void testInvoke_throwsPluginInvocationExceptionWhenEventSerialiserThrowsException() throws JMSException, EventSerialisationException
    {
        mockery.checking(new Expectations()
        {
            {
                one(jmsConnectionFactory).createConnection();
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(returnValue(session));
                one(jmsMessageEventSerialiser).toMapMessage(event, session);
                will(throwException(eventSerialisationException));
                one(connection).close();
            }
        });
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(destination, jmsConnectionFactory, jmsMessageEventSerialiser, null);
        try
        {
            eventPublisherPlugin.invoke(event);
            fail("Exception should have been thrown");
        }
        catch (PluginInvocationException p)
        {
            assertTrue("underlyingException should be the EnvelopeOperationException", eventSerialisationException.equals(p.getCause()));
        }
    }

    /**
     * Test method for
     * {@link org.ikasan.framework.plugins.JMSEventPublisherPlugin#invoke(org.ikasan.framework.component.Event)}
     * .
     * 
     * @throws JMSException
     * @throws EventSerialisationException
     */
    public void testInvoke_throwsPluginInvocationExceptionWhenClosingConnectionThrowsJMSException() throws JMSException, EventSerialisationException
    {
        mockery.checking(new Expectations()
        {
            {
                one(ikasanSecurityConf).getJMSUsername();
                will(returnValue(jmsUsername));
                one(ikasanSecurityConf).getJMSPassword();
                will(returnValue(jmsPassword));
                one(jmsConnectionFactory).createConnection(jmsUsername, jmsPassword);
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(returnValue(session));
                one(jmsMessageEventSerialiser).toMapMessage(event, session);
                will(returnValue(mapMessage));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(mapMessage);
                one(connection).close();
                will(throwException(jmsException));
            }
        });
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(destination, jmsConnectionFactory, jmsMessageEventSerialiser,
            ikasanSecurityConf);
        try
        {
            eventPublisherPlugin.invoke(event);
            fail("Exception should have been thrown");
        }
        catch (PluginInvocationException p)
        {
            assertTrue("underlyingException should be the JMSException", jmsException.equals(p.getCause()));
        }
    }
    
    public void testInvoke_willUtiliseDestinationFactoryWhenSupplied() throws PluginInvocationException, JMSException, EventSerialisationException, NamingException{

        classMockery.checking(new Expectations()
        {
            {
                one(event).idToString();
                will(returnValue("dummy id list"));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(jndiDestinationFactory).getDestination(true);
                will(returnValue(destination));
                one(jmsConnectionFactory).createConnection();
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(returnValue(session));
                one(jmsMessageEventSerialiser).toMapMessage(event, session);
                will(returnValue(mapMessage));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(mapMessage);
                one(connection).close();
            }
        });
        
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(jndiDestinationFactory, jmsConnectionFactory, jmsMessageEventSerialiser,
            null);
        eventPublisherPlugin.invoke(event);
        
        mockery.assertIsSatisfied();
    }
}

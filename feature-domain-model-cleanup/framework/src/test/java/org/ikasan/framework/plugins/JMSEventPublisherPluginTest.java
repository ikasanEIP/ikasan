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

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import junit.framework.TestCase;

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
    final JmsMessageEventSerialiser<? extends Message> jmsMessageEventSerialiser = mockery.mock(JmsMessageEventSerialiser.class);

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
    final Message message = mockery.mock(Message.class);

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
    	
    	final int eventPriority = 7;
        classMockery.checking(new Expectations()
        {
            {
                // event has some payloads
                one(event).idToString();
                will(returnValue("dummy id list"));
                one(event).getPriority();
                will(returnValue(eventPriority));
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
                one(jmsMessageEventSerialiser).toMessage(event, session);
                will(returnValue(message));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(message);
                one(messageProducer).setPriority(eventPriority);
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
                one(jmsMessageEventSerialiser).toMessage(event, session);
                will(returnValue(message));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).setPriority(with(any(Integer.class)));
                one(messageProducer).send(message);
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
    	final int eventPriority = 7;
        classMockery.checking(new Expectations()
        {
            {
                // event has some payloads
                one(event).idToString();
                will(returnValue("dummy id list"));
                one(event).getPriority();
                will(returnValue(eventPriority));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(jmsConnectionFactory).createConnection();
                will(returnValue(connection));
                one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
                will(returnValue(session));
                one(jmsMessageEventSerialiser).toMessage(event, session);
                will(returnValue(message));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(message);
                one(messageProducer).setPriority(eventPriority);
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
                one(jmsMessageEventSerialiser).toMessage(event, session);
                will(returnValue(message));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(message);
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
    	final int eventPriority =7;
        classMockery.checking(new Expectations()
        {
            {
                one(event).idToString();
                will(returnValue("dummy id list"));
                one(event).getPriority();
                will(returnValue(eventPriority));
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
                one(jmsMessageEventSerialiser).toMessage(event, session);
                will(returnValue(message));
                one(session).createProducer(destination);
                will(returnValue(messageProducer));
                one(messageProducer).send(message);
                one(messageProducer).setPriority(eventPriority);
                one(connection).close();
            }
        });
        
        final JMSEventPublisherPlugin eventPublisherPlugin = new JMSEventPublisherPlugin(jndiDestinationFactory, jmsConnectionFactory, jmsMessageEventSerialiser,
            null);
        eventPublisherPlugin.invoke(event);
        
        mockery.assertIsSatisfied();
    }
}

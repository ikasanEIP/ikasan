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
package org.ikasan.framework.initiator.messagedriven;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.FlowInvocationContext;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for EventMessageDrivenInitiator
 * 
 * @author Ikasan Development Team
 *
 */
public class EventMessageDrivenInitiatorTest {

	String moduleName = "moduleName";
	
	String name ="name";
	
	private static Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	
	JmsMessageEventSerialiser<MapMessage> jmsMessageEventSerialiser = mockery.mock(JmsMessageEventSerialiser.class);
	
	Flow flow = mockery.mock(Flow.class);
	
	IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);
	
	TextMessage textMessage = mockery.mock(TextMessage.class);
	
	MapMessage mapMessage = mockery.mock(MapMessage.class);
	
	/**
	 * Tests that MapMessages are supported
	 * 
	 * @throws JMSException
	 * @throws EventSerialisationException 
	 */
	@Test
	public void testOnMessageHandlesMapMessage() throws JMSException, EventSerialisationException {
		final Event event = mockery.mock(Event.class);
		
        mockery.checking(new Expectations()
        {
            {
            	allowing(mapMessage).getJMSMessageID();will(returnValue("messageId"));
            	
                one(jmsMessageEventSerialiser).fromMessage(mapMessage, moduleName, name);
                will(returnValue(event));
                one(event).getId();will(returnValue("eventId"));
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event)));
                will(returnValue(null));
            }
        });
        EventMessageDrivenInitiator eventMessageDrivenInitiator = new EventMessageDrivenInitiator(moduleName, name, flow,exceptionHandler, jmsMessageEventSerialiser);
    	
		eventMessageDrivenInitiator.onMessage(mapMessage);
	
	}

	
	/**
	 * Tests that TextMessages are not supported
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testOnMessageDoesNotHandleTextMessage() throws JMSException {
		final IkasanExceptionAction stopAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);
		final MessageListenerContainer messageListenerContainer = mockery.mock(MessageListenerContainer.class);

        final EventMessageDrivenInitiator eventMessageDrivenInitiator = new EventMessageDrivenInitiator(moduleName, name, flow,exceptionHandler, jmsMessageEventSerialiser);
		
		
        mockery.checking(new Expectations()
        {
            {
            	allowing(messageListenerContainer).setListenerSetupExceptionListener(eventMessageDrivenInitiator);

            	allowing(textMessage).getJMSMessageID();will(returnValue("messageId"));
            	
            	one(exceptionHandler).invoke(with(equal(name)), (Throwable) with(an(UnsupportedOperationException.class)));
            	will(returnValue(stopAction));
            	
            	one(messageListenerContainer).stop();
            }
        });
        eventMessageDrivenInitiator.setMessageListenerContainer(messageListenerContainer);
    	
        AbortTransactionException exception = null;
		try{
			eventMessageDrivenInitiator.onMessage(textMessage);
			Assert.fail("should have thrown AbortTransactionException");
		} catch(AbortTransactionException abortTransactionException){
				exception = abortTransactionException;
		}
		
		Assert.assertNotNull("should have thrown AbortTransactionException", exception);
		
		mockery.assertIsSatisfied();
	}


}

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
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.flow.Flow;
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
	
	JmsMessageEventSerialiser jmsMessageEventSerialiser = mockery.mock(JmsMessageEventSerialiser.class);
	
	Flow flow = mockery.mock(Flow.class);
	
	
	
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
            	
                one(jmsMessageEventSerialiser).fromMapMessage(mapMessage, moduleName, name);
                will(returnValue(event));
                
                one(flow).invoke(event);
                will(returnValue(null));
            }
        });
        EventMessageDrivenInitiator eventMessageDrivenInitiator = new EventMessageDrivenInitiator(moduleName, name, flow, jmsMessageEventSerialiser);
    	
		eventMessageDrivenInitiator.onMessage(mapMessage);
	
	}

	
	/**
	 * Tests that TextMessages are not supported
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testOnMessageDoesNotHandleTextMessage() throws JMSException {
		UnsupportedOperationException exception = null;
        mockery.checking(new Expectations()
        {
            {
            	allowing(textMessage).getJMSMessageID();will(returnValue("messageId"));
            }
        });
        EventMessageDrivenInitiator eventMessageDrivenInitiator = new EventMessageDrivenInitiator(moduleName, name, flow, jmsMessageEventSerialiser);
    	
		try{
			eventMessageDrivenInitiator.onMessage(textMessage);
			Assert.fail("should have thrown UnsupportedOperationException");
		} catch(UnsupportedOperationException unsupportedOperationException){
				exception = unsupportedOperationException;
		}
		
		Assert.assertNotNull("should have thrown UnsupportedOperationException", exception);
	}


}

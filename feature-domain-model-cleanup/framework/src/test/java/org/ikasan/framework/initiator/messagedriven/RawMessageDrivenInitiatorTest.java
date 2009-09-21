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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.Flow;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Test class for RawMessageDrivenInitiator
 * 
 * @author Ikasan Development Team
 *
 */
public class RawMessageDrivenInitiatorTest {

	private static final int DEFAULT_PRIORITY = 4;

	private String moduleName = "moduleName";
	
	private String name ="name";
	
	private Mockery mockery = new Mockery();
	
	PayloadFactory payloadFactory = mockery.mock(PayloadFactory.class);
	
	Flow flow = mockery.mock(Flow.class);
		
	
	TextMessage textMessage = mockery.mock(TextMessage.class);
	
	MapMessage mapMessage = mockery.mock(MapMessage.class);
	
	/**
	 * Tests that TextMessages are supported
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testOnMessageHandlesTextMessage() throws JMSException {
		createExpectations(false, DEFAULT_PRIORITY);
        
        RawMessageDrivenInitiator rawDrivenInitiator = new RawMessageDrivenInitiator(moduleName, name, flow, payloadFactory);
		rawDrivenInitiator.onMessage(textMessage);
	
	}

	
	
	/**
	 * Tests that JMS Message priority is propogated to the Event if configured
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testOnMessageRespectsPriorityWhenConfigured() throws JMSException {

		
		final int messagePriority = 8;
		
        createExpectations(true, messagePriority);
        
        RawMessageDrivenInitiator rawDrivenInitiator = new RawMessageDrivenInitiator(moduleName, name, flow, payloadFactory);
        rawDrivenInitiator.setRespectPriority(true);
        rawDrivenInitiator.onMessage(textMessage);
	
	}



	private void createExpectations(final boolean respectingPriority, final int messagePriority)
			throws JMSException {
		
		final String textMessageText = "textMessageText";
		final Payload payload = mockery.mock(Payload.class);
		
		mockery.checking(new Expectations()
        {
            {
            	
            	
            	allowing(textMessage).getJMSMessageID();will(returnValue("messageId"));
            	
                one(textMessage).getText();
                will(returnValue(textMessageText));
                
                if (respectingPriority){
	                one(textMessage).getJMSPriority();
	                will(returnValue(messagePriority));
                }
                one(payloadFactory).newPayload("messageId",  Spec.TEXT_XML,  textMessageText.getBytes());
                will(returnValue(payload));
                
                one(payload).getSpec();
                will(returnValue(Spec.TEXT_XML.toString()));
                
                one(flow).invoke((Event) with(new EventMatcher(messagePriority)));
                will(returnValue(null));
            }
        });
	}

	
	/**
	 * Tests that MapMessages are not supported
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testOnMessageDoesNotHandleMapMessage() throws JMSException {
		UnsupportedOperationException exception = null;
		
        mockery.checking(new Expectations()
        {
            {
            	allowing(mapMessage).getJMSMessageID();will(returnValue("messageId"));
            }
        });
        RawMessageDrivenInitiator rawDrivenInitiator = new RawMessageDrivenInitiator(moduleName, name, flow, payloadFactory);
		try{
			rawDrivenInitiator.onMessage(mapMessage);
			Assert.fail("should have thrown UnsupportedOperationException");
		} catch(UnsupportedOperationException unsupportedOperationException){
				exception = unsupportedOperationException;
		}
		
		Assert.assertNotNull("should have thrown UnsupportedOperationException", exception);
	}
	
	class EventMatcher extends TypeSafeMatcher<Event>{

		private int priority;
			
		
		public EventMatcher(int priority) {
			super();
			this.priority = priority;
		}

		@Override
		public boolean matchesSafely(Event event) {
			boolean result = true;
			
			if (event.getPriority()!=priority){
				result=false;
			}
			
			return result;
		}

		public void describeTo(Description arg0) {
			
		}
		
	}


}

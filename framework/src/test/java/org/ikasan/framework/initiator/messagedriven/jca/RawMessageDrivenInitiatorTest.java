/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.initiator.messagedriven.jca;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.core.flow.Flow;
import org.ikasan.core.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.initiator.messagedriven.ListenerSetupFailureListener;
import org.ikasan.framework.initiator.messagedriven.MessageListenerContainer;
import org.ikasan.framework.initiator.messagedriven.RawMessageDrivenInitiator;
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
		
	IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);
	
	MessageListenerContainer messageListenerContainer = mockery.mock(MessageListenerContainer.class);

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
        
        RawMessageDrivenInitiator rawDrivenInitiator = new RawMessageDrivenInitiator(moduleName, name, flow, exceptionHandler, payloadFactory);
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
        
        RawMessageDrivenInitiator rawDrivenInitiator = new RawMessageDrivenInitiator(moduleName, name, flow, exceptionHandler, payloadFactory);
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
                one(payloadFactory).newPayload("messageId",    textMessageText.getBytes());
                will(returnValue(payload));
                
                
                one(flow).invoke((FlowInvocationContext) (with(a(FlowInvocationContext.class))), (with(new EventMatcher(messagePriority))));            }
        });
	}

	
	/**
	 * Tests that MapMessages are not supported
	 * 
	 * @throws JMSException
	 */
	@Test(expected = AbortTransactionException.class)
	public void testOnMessageDoesNotHandleMapMessage() throws JMSException {
		AbortTransactionException exception = null;
		
        mockery.checking(new Expectations()
        {
            {
            	one(messageListenerContainer).setListenerSetupExceptionListener((ListenerSetupFailureListener) with(anything()));
            	
            	allowing(mapMessage).getJMSMessageID();will(returnValue("messageId"));
            	one(messageListenerContainer).stop();
            	one(flow).stop();
            }
        });
        RawMessageDrivenInitiator rawDrivenInitiator = new RawMessageDrivenInitiator(moduleName, name, flow, exceptionHandler, payloadFactory);
		rawDrivenInitiator.setMessageListenerContainer(messageListenerContainer);
		rawDrivenInitiator.onMessage(mapMessage);
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

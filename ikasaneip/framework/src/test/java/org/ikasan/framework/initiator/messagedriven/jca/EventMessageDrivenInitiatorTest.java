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

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.serialisation.EventDeserialisationException;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.initiator.messagedriven.EventMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.ListenerSetupFailureListener;
import org.ikasan.framework.initiator.messagedriven.MessageListenerContainer;
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
	
	IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);
	
	MessageListenerContainer messageListenerContainer = mockery.mock(MessageListenerContainer.class);
	
	/**
	 * Tests that MapMessages are supported
	 * 
	 * @throws JMSException
	 * @throws EventDeserialisationException 
	 * @throws EventSerialisationException 
	 */
	@Test
	public void testOnMessageHandlesMapMessage() throws JMSException, EventDeserialisationException {
		final Event event = mockery.mock(Event.class);
		final MapMessage mapMessage = mockery.mock(MapMessage.class);
		
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
        EventMessageDrivenInitiator eventMessageDrivenInitiator = new EventMessageDrivenInitiator(moduleName, name, flow, exceptionHandler, jmsMessageEventSerialiser);
    	
		eventMessageDrivenInitiator.onMessage(mapMessage);
	
	}

	
	/**
	 * Tests that TextMessages are not supported
	 * 
	 * @throws JMSException
	 */
	@Test(expected = AbortTransactionException.class)
	public void testOnMessageDoesNotHandleTextMessage() throws JMSException {
		UnsupportedOperationException exception = null;
		final TextMessage textMessage = mockery.mock(TextMessage.class);
		
        mockery.checking(new Expectations()
        {
            {
            	one(messageListenerContainer).setListenerSetupExceptionListener((ListenerSetupFailureListener) with(anything()));

            	allowing(textMessage).getJMSMessageID();
            	will(returnValue("messageId"));
            	
            	one(messageListenerContainer).stop();
                one(flow).stopManagedResources();
            }
        });
        EventMessageDrivenInitiator eventMessageDrivenInitiator = new EventMessageDrivenInitiator(moduleName, name, flow, exceptionHandler, jmsMessageEventSerialiser);
    	eventMessageDrivenInitiator.setMessageListenerContainer(messageListenerContainer);
		eventMessageDrivenInitiator.onMessage(textMessage);
	}


}

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
package org.ikasan.framework.error.service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ikasan.common.xml.serializer.XMLSerializer;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for <code>ErrorOccurrenceTextMessagePublisher</code>
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unchecked")
public class ErrorOccurrenceTextMessagePublisherTest {

    /** Mockery */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	
	private XMLSerializer<ErrorOccurrence> errorOccurrenceSerialiser = mockery.mock(XMLSerializer.class);
	private Destination errorOccurrenceChannel = mockery.mock(Destination.class);
	private ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class);
	private ErrorOccurrence errorOccurrence = mockery.mock(ErrorOccurrence.class);
	private Connection connection = mockery.mock(Connection.class);
	private Session session = mockery.mock(Session.class);
	private TextMessage textMessage = mockery.mock(TextMessage.class);
	private MessageProducer messageProducer = mockery.mock(MessageProducer.class);
	private String serialisedErrorOccurrence = "dummyXml";
	private Long errorOccurrenceId = 1l;
	private Long timeToLive = 1000l;
	private JMSException jmsException = new JMSException("no reason");

	
	@Test
	public void testNotifyErrorOccurrence_willSerialiseErrorOccurrenceAndPublishToErrorOccurrenceChannel() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		mockery.checking(new Expectations() {
			{
				allowing(errorOccurrence).getId();will(returnValue(errorOccurrenceId));
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(returnValue(session));
				one(errorOccurrenceSerialiser).toXml(errorOccurrence);will(returnValue(serialisedErrorOccurrence));
				one(session).createTextMessage(serialisedErrorOccurrence);will(returnValue(textMessage));
				one(session).createProducer(errorOccurrenceChannel);will(returnValue(messageProducer));
				one(messageProducer).send(textMessage);
				one(connection).close();
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willRespectTimeToLiveIfSet() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		errorOccurrenceTextMessagePublisher.setTimeToLive(timeToLive);
		mockery.checking(new Expectations() {
			{
				allowing(errorOccurrence).getId();will(returnValue(errorOccurrenceId));
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(returnValue(session));
				one(errorOccurrenceSerialiser).toXml(errorOccurrence);will(returnValue(serialisedErrorOccurrence));
				one(session).createTextMessage(serialisedErrorOccurrence);will(returnValue(textMessage));
				one(session).createProducer(errorOccurrenceChannel);will(returnValue(messageProducer));
				one(messageProducer).setTimeToLive(timeToLive);
				one(messageProducer).send(textMessage);
				one(connection).close();
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willSwallowJmsExceptionFromClose() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		mockery.checking(new Expectations() {
			{
				allowing(errorOccurrence).getId();will(returnValue(errorOccurrenceId));
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(returnValue(session));				
				one(errorOccurrenceSerialiser).toXml(errorOccurrence);will(returnValue(serialisedErrorOccurrence));
				one(session).createTextMessage(serialisedErrorOccurrence);will(returnValue(textMessage));
				one(session).createProducer(errorOccurrenceChannel);will(returnValue(messageProducer));
				one(messageProducer).send(textMessage);
				one(connection).close();will(throwException(jmsException));
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willSwallowJmsExceptionFromCreateConnection() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		mockery.checking(new Expectations() {
			{

				one(connectionFactory).createConnection();will(throwException(jmsException));
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willSwallowJmsExceptionFromCreateSession() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		mockery.checking(new Expectations() {
			{
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(throwException(jmsException));
				one(connection).close();
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willSwallowJmsExceptionFromCreateTextMessage() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		mockery.checking(new Expectations() {
			{
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(returnValue(session));				
				one(errorOccurrenceSerialiser).toXml(errorOccurrence);will(returnValue(serialisedErrorOccurrence));
				one(session).createTextMessage(serialisedErrorOccurrence);will(throwException(jmsException));
				one(connection).close();
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willSwallowJmsExceptionFromCreateProducer() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		mockery.checking(new Expectations() {
			{
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(returnValue(session));				
				one(errorOccurrenceSerialiser).toXml(errorOccurrence);will(returnValue(serialisedErrorOccurrence));
				one(session).createTextMessage(serialisedErrorOccurrence);will(returnValue(textMessage));
				one(session).createProducer(errorOccurrenceChannel);will(throwException(jmsException));
				one(connection).close();
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willSwallowJmsExceptionFromSetTimeToLive() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		errorOccurrenceTextMessagePublisher.setTimeToLive(timeToLive);
		mockery.checking(new Expectations() {
			{
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(returnValue(session));				
				one(errorOccurrenceSerialiser).toXml(errorOccurrence);will(returnValue(serialisedErrorOccurrence));
				one(session).createTextMessage(serialisedErrorOccurrence);will(returnValue(textMessage));
				one(session).createProducer(errorOccurrenceChannel);will(returnValue(messageProducer));
				one(messageProducer).setTimeToLive(timeToLive);will(throwException(jmsException));
				one(connection).close();
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testNotifyErrorOccurrence_willSwallowJmsExceptionFromSend() throws JMSException {
		ErrorOccurrenceTextMessagePublisher errorOccurrenceTextMessagePublisher = new ErrorOccurrenceTextMessagePublisher(connectionFactory, errorOccurrenceChannel,errorOccurrenceSerialiser);
		mockery.checking(new Expectations() {
			{
				one(connectionFactory).createConnection();will(returnValue(connection));
				one(connection).createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);will(returnValue(session));				
				one(errorOccurrenceSerialiser).toXml(errorOccurrence);will(returnValue(serialisedErrorOccurrence));
				one(session).createTextMessage(serialisedErrorOccurrence);will(returnValue(textMessage));
				one(session).createProducer(errorOccurrenceChannel);will(returnValue(messageProducer));
				one(messageProducer).send(textMessage);will(throwException(jmsException));
				one(connection).close();
			}
		});	
		
		errorOccurrenceTextMessagePublisher.notifyErrorOccurrence(errorOccurrence);
		
		mockery.assertIsSatisfied();
	}
	
	

}

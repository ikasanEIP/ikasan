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

import static org.junit.Assert.fail;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import junit.framework.Assert;

import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.flow.Flow;
import org.jmock.Mockery;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Test class for <code>MessageDrivenInitiatorFactoryBean</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class MessageDrivenInitiatorFactoryBeanTest
{
    private static final String MODULE_NAME = "moduleName";
    private Mockery mockery = new Mockery();
    private Flow flow = mockery.mock(Flow.class);
    private IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);
    private PayloadFactory payloadFactory = mockery.mock(PayloadFactory.class);
    private ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class);
    private Topic topic = mockery.mock(Topic.class);
    private PlatformTransactionManager transactionManager = mockery.mock(PlatformTransactionManager.class);
    private JmsMessageEventSerialiser jmsMessageEventSerialiser = mockery.mock(JmsMessageEventSerialiser.class);

    /**
     * Test mandatory field checking for moduleName
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullModuleNameWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        String fieldName = MODULE_NAME;
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
    }

    /**
     * Test mandatory field checking for name
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullNameWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        String fieldName = "name";
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
    }

    /**
     * Test mandatory field checking for flow
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullFlowWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        String fieldName = "flow";
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
    }

    /**
     * Test mandatory field checking for exceptionHandler
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullExceptionHandlerWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        String fieldName = "exceptionHandler";
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
    }
    
    /**
     * Test mandatory field checking for payloadFactory when event serialiser
     * not supplied
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullPayloadFactoryWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        messageDrivenInitiatorFactoryBean.setExceptionHandler(exceptionHandler);
        String fieldName = "payloadFactory";
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
        mockery.assertIsSatisfied();
    }

    /**
     * Test mandatory field checking for connectionFactory
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullConnectionFactoryWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        messageDrivenInitiatorFactoryBean.setExceptionHandler(exceptionHandler);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        String fieldName = "connectionFactory";
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
        mockery.assertIsSatisfied();
    }

    /**
     * Test mandatory field checking for destination
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullDestinationWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        messageDrivenInitiatorFactoryBean.setExceptionHandler(exceptionHandler);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        messageDrivenInitiatorFactoryBean.setConnectionFactory(connectionFactory);
        String fieldName = "destination";
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
        mockery.assertIsSatisfied();
    }

    /**
     * Test mandatory field checking for transactionManager
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_withNullTransactionManagerWillThrowException() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        messageDrivenInitiatorFactoryBean.setExceptionHandler(exceptionHandler);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        messageDrivenInitiatorFactoryBean.setConnectionFactory(connectionFactory);
        messageDrivenInitiatorFactoryBean.setDestination(topic);
        String fieldName = "transactionManager";
        testManadatoryFieldCheck(messageDrivenInitiatorFactoryBean, fieldName);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful creation of an EventMessageDrivenInitiator
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_returnsAnEventMessageDrivenInitiatorWhenSerialiserSupplied() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        messageDrivenInitiatorFactoryBean.setExceptionHandler(exceptionHandler);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        messageDrivenInitiatorFactoryBean.setConnectionFactory(connectionFactory);
        messageDrivenInitiatorFactoryBean.setDestination(topic);
        messageDrivenInitiatorFactoryBean.setTransactionManager(transactionManager);
        messageDrivenInitiatorFactoryBean.setEventDeserialiser(jmsMessageEventSerialiser);
        JmsMessageDrivenInitiator initiator = (JmsMessageDrivenInitiator) messageDrivenInitiatorFactoryBean.getObject();
        Assert.assertTrue("EventMessageDrivenInitiator should be returned if serialiser supplied", (initiator instanceof EventMessageDrivenInitiator));
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful creation of a RawMessageDrivenInitiator
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_returnsARawMessageDrivenInitiatorWhenPayloadFactorySupplied() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        messageDrivenInitiatorFactoryBean.setExceptionHandler(exceptionHandler);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        messageDrivenInitiatorFactoryBean.setConnectionFactory(connectionFactory);
        messageDrivenInitiatorFactoryBean.setDestination(topic);
        messageDrivenInitiatorFactoryBean.setTransactionManager(transactionManager);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        JmsMessageDrivenInitiator initiator = (JmsMessageDrivenInitiator) messageDrivenInitiatorFactoryBean.getObject();
        Assert.assertTrue("RawMessageDrivenInitiator should be returned if serialiser supplied", (initiator instanceof RawMessageDrivenInitiator));
        mockery.assertIsSatisfied();
    }

    /**
     * @param messageDrivenInitiatorFactoryBean
     * @param fieldName
     * @throws Exception
     */
    private void testManadatoryFieldCheck(MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean, String fieldName) throws Exception
    {
        IllegalArgumentException illegalArgumentException = null;
        try
        {
            messageDrivenInitiatorFactoryBean.getObject();
            fail("illegalArgumentException should have been thrown if getObject called without " + fieldName + " being set");
        }
        catch (IllegalArgumentException e)
        {
            illegalArgumentException = e;
        }
        Assert.assertNotNull("IllegalStateException should have been thrown if getObject called without " + fieldName + " being set", illegalArgumentException);
        Assert.assertTrue("Exception message should refer to field " + fieldName, (illegalArgumentException.getMessage().indexOf(fieldName) > -1));
    }
}

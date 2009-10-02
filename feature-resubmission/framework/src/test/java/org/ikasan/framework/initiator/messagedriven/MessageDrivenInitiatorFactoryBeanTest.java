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
package org.ikasan.framework.initiator.messagedriven;

import static org.junit.Assert.fail;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import junit.framework.Assert;

import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
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
    private ErrorLoggingService errorLoggingService = mockery.mock(ErrorLoggingService.class);

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
        messageDrivenInitiatorFactoryBean.setErrorLoggingService(errorLoggingService);
        JmsMessageDrivenInitiator initiator = (JmsMessageDrivenInitiator) messageDrivenInitiatorFactoryBean.getObject();
        Assert.assertTrue("RawMessageDrivenInitiator should be returned if serialiser supplied", (initiator instanceof RawMessageDrivenInitiator));
        
        Assert.assertFalse("RawMessageDrivenInitiator should not respect message priority by default", ((RawMessageDrivenInitiator)initiator).isRespectPriority());
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test creation of a RawMessageDrivenInitiator configured to respect priority
     * 
     * @throws Exception
     */
    @Test
    public void testGetObject_returnsAPriorityRespectingRawMessageDrivenInitiatorWhenPayloadFactorySuppliedAndPriorityRespectEnabled() throws Exception
    {
        MessageDrivenInitiatorFactoryBean messageDrivenInitiatorFactoryBean = new MessageDrivenInitiatorFactoryBean();
        messageDrivenInitiatorFactoryBean.setModuleName(MODULE_NAME);
        messageDrivenInitiatorFactoryBean.setBeanName("name");
        messageDrivenInitiatorFactoryBean.setFlow(flow);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        messageDrivenInitiatorFactoryBean.setConnectionFactory(connectionFactory);
        messageDrivenInitiatorFactoryBean.setDestination(topic);
        messageDrivenInitiatorFactoryBean.setTransactionManager(transactionManager);
        messageDrivenInitiatorFactoryBean.setPayloadFactory(payloadFactory);
        messageDrivenInitiatorFactoryBean.setExceptionHandler(exceptionHandler);
        
        //tell the factory bean to respect priority
        messageDrivenInitiatorFactoryBean.setRespectPriority(true);
        JmsMessageDrivenInitiator initiator = (JmsMessageDrivenInitiator) messageDrivenInitiatorFactoryBean.getObject();
        Assert.assertTrue("RawMessageDrivenInitiator should be returned if serialiser supplied", (initiator instanceof RawMessageDrivenInitiator));
        
        Assert.assertTrue("RawMessageDrivenInitiator should respect message priority as configured", ((RawMessageDrivenInitiator)initiator).isRespectPriority());
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

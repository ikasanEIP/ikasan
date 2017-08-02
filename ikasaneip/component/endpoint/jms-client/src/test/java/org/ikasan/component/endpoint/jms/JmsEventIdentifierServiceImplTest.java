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
package org.ikasan.component.endpoint.jms;

import org.ikasan.spec.event.ManagedEventIdentifierException;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;

/**
 * Test class for JmsEventIdentifierServiceImpl.
 *
 * @author Ikasan Development Team
 */
public class JmsEventIdentifierServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    private final Message message = mockery.mock(Message.class);

    // unit under test
    private JmsEventIdentifierServiceImpl jmsEventIdentifierService = new JmsEventIdentifierServiceImpl();

    @Test
    public void test_getEventIdentifier_id_present() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).getStringProperty(ManagedEventIdentifierService.EVENT_LIFE_ID);
            will(returnValue("id1"));
        }});
        String id = jmsEventIdentifierService.getEventIdentifier(message);
        Assert.assertEquals("id1", id);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_getEventIdentifier_id_not_present() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).getStringProperty(ManagedEventIdentifierService.EVENT_LIFE_ID);
            will(returnValue(null));
            oneOf(message).getJMSMessageID();
            will(returnValue("jmsMessageId"));
        }});
        String id = jmsEventIdentifierService.getEventIdentifier(message);
        Assert.assertEquals("jmsMessageId", id);
        mockery.assertIsSatisfied();
    }

    @Test(expected = ManagedEventIdentifierException.class)
    public void test_getEventIdentifier_jms_exception() throws JMSException
    {
        try
        {
            mockery.checking(new Expectations()
            {{
                oneOf(message).getStringProperty(ManagedEventIdentifierService.EVENT_LIFE_ID);
                will(throwException(new JMSException("bang")));
            }});

            jmsEventIdentifierService.getEventIdentifier(message);
            Assert.fail("exception should be thrown");
        }
        finally
        {
            mockery.assertIsSatisfied();
        }
    }

    @Test
    public void test_setEventIdentifier_id_present() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).setStringProperty(ManagedEventIdentifierService.EVENT_LIFE_ID, "id1");
        }});
        jmsEventIdentifierService.setEventIdentifier("id1", message);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_setEventIdentifier_message_not_writeable() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).setStringProperty(ManagedEventIdentifierService.EVENT_LIFE_ID, "id1");
            will(throwException(new MessageNotWriteableException("cannot write id")));
        }});
        jmsEventIdentifierService.setEventIdentifier("id1", message);
        mockery.assertIsSatisfied();
    }

    @Test(expected = ManagedEventIdentifierException.class)
    public void test_setEventIdentifier_jms_exception() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).setStringProperty(ManagedEventIdentifierService.EVENT_LIFE_ID, "id1");
            will(throwException(new JMSException("cannot write id")));
        }});
        try
        {
            jmsEventIdentifierService.setEventIdentifier("id1", message);
            Assert.fail("exception should be thrown");
        }
        finally
        {
            mockery.assertIsSatisfied();
        }
    }


    @Test
    public void test_getRelatedEventIdentifier_id_present() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).propertyExists(ManagedRelatedEventIdentifierService.RELATED_EVENT_LIFE_ID);
            will(returnValue(true));
            oneOf(message).getStringProperty(ManagedRelatedEventIdentifierService.RELATED_EVENT_LIFE_ID);
            will(returnValue("relatedId1"));
        }});
        String id = jmsEventIdentifierService.getRelatedEventIdentifier(message);
        Assert.assertEquals("relatedId1", id);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_getRelatedEventIdentifier_id_not_present() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).propertyExists(ManagedRelatedEventIdentifierService.RELATED_EVENT_LIFE_ID);
            will(returnValue(false));
        }});
        String id = jmsEventIdentifierService.getRelatedEventIdentifier(message);
        Assert.assertNull("should be null", id);
        mockery.assertIsSatisfied();
    }


    @Test
    public void test_setRelatedEventIdentifier_id_present() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).setStringProperty(ManagedRelatedEventIdentifierService.RELATED_EVENT_LIFE_ID, "id2");
        }});
        jmsEventIdentifierService.setRelatedEventIdentifier("id2", message);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_setRelatedEventIdentifier_id_null() throws JMSException
    {
        jmsEventIdentifierService.setRelatedEventIdentifier(null, message);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_setRelatedEventIdentifier_message_not_writeable() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).setStringProperty(ManagedRelatedEventIdentifierService.RELATED_EVENT_LIFE_ID, "id1");
            will(throwException(new MessageNotWriteableException("cannot write related id")));
        }});
        jmsEventIdentifierService.setRelatedEventIdentifier("id1", message);
        mockery.assertIsSatisfied();
    }

    @Test(expected = ManagedEventIdentifierException.class)
    public void test_setRelatedEventIdentifier_jms_exception() throws JMSException
    {
        mockery.checking(new Expectations()
        {{
            oneOf(message).setStringProperty(ManagedRelatedEventIdentifierService.RELATED_EVENT_LIFE_ID, "id1");
            will(throwException(new JMSException("cannot write related id")));
        }});
        try
        {
            jmsEventIdentifierService.setRelatedEventIdentifier("id1", message);
            Assert.fail("exception should be thrown");
        }
        finally
        {
            mockery.assertIsSatisfied();
        }
    }
}

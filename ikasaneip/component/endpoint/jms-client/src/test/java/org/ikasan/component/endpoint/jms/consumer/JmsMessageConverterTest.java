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
package org.ikasan.component.endpoint.jms.consumer;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;

import jakarta.jms.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JmsMessageConverter.
 * 
 * @author Ikasan Development Team
 */
class JmsMessageConverterTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
    }};

    private TextMessage textMessage= mockery.mock(TextMessage.class);
    private MapMessage mapMessage= mockery.mock(MapMessage.class);
    private ObjectMessage objectMessage= mockery.mock(ObjectMessage.class);
    private BytesMessage bytesMessage= mockery.mock(BytesMessage.class);
    private StreamMessage streamMessage= mockery.mock(StreamMessage.class);
    private Message message= mockery.mock(Message.class);

    /**
     * Test TextMessage converter
     */
    @Test
    void test_convert_textMessage() throws JMSException
    {

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(textMessage).getText();
                will(returnValue("text payload content"));
            }
        });

        Object result = JmsMessageConverter.extractContent(textMessage);
        assertTrue(result instanceof String);
        assertEquals("text payload content", result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test MapMessage converter
     */
    @Test
    void test_convert_mapMessage() throws JMSException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mapMessage).getMapNames();
                will(returnEnumeration("name1", "name2", "name3"));

                exactly(1).of(mapMessage).getObject("name1");
                will(returnValue("value1"));

                exactly(1).of(mapMessage).getObject("name2");
                will(returnValue(Long.valueOf(2)));

                exactly(1).of(mapMessage).getObject("name3");
                will(returnValue(Double.valueOf(3)));
            }
        });

        Object result = JmsMessageConverter.extractContent(mapMessage);
        assertTrue(result instanceof Map);
        assertEquals(3, ((Map) result).size());
        assertEquals("value1", ((Map) result).get("name1"));
        assertEquals(((Map) result).get("name2"), Long.valueOf(2));
        assertEquals(((Map) result).get("name3"), Double.valueOf(3));
        mockery.assertIsSatisfied();
    }

    /**
     * Test ObjectMessage converter
     */
    @Test
    void test_convert_objectMessage() throws JMSException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(objectMessage).getObject();
                will(returnValue("object"));
            }
        });

        Object result = JmsMessageConverter.extractContent(objectMessage);
        assertTrue(result instanceof String);
        mockery.assertIsSatisfied();
    }

    /**
     * Test BytesMessage converter
     */
    @Test
    void test_convert_bytesMessage() throws JMSException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(bytesMessage).getBodyLength();
                will(returnValue(100L));
                exactly(1).of(bytesMessage).readBytes(with(any(byte[].class)));
            }
        });

        Object result = JmsMessageConverter.extractContent(bytesMessage);
        assertTrue(result instanceof byte[]);
        assertEquals(100, ((byte[]) result).length);
        mockery.assertIsSatisfied();
    }

    /**
     * Test BytesMessage converter no content
     */
    @Test
    void test_convert_bytesMessage_no_content() throws JMSException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(bytesMessage).getBodyLength();
                will(returnValue(0L));
            }
        });

        Object result = JmsMessageConverter.extractContent(bytesMessage);
        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test StreamMessage converter
     */
    @Test
    void test_convert_streamMessage() throws JMSException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // nothing
            }
        });

        Object result = JmsMessageConverter.extractContent(streamMessage);
        assertTrue(result instanceof StreamMessage);
        mockery.assertIsSatisfied();
    }

    /**
     * Test Message converter
     */
    @Test
    void test_convert_Message() throws JMSException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // nothing
            }
        });

        Object result = JmsMessageConverter.extractContent(message);
        assertTrue(result instanceof Message);
        mockery.assertIsSatisfied();
    }
}

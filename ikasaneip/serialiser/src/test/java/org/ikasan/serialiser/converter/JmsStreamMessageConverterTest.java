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
package org.ikasan.serialiser.converter;

import org.apache.activemq.command.ActiveMQStreamMessage;
import org.junit.jupiter.api.Test;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test JmsStreamMessageConverter
 * @author Ikasan Development Team
 */
class JmsStreamMessageConverterTest
{

    // Unit under test
    private JmsStreamMessageConverter uut = new JmsStreamMessageConverter();

    @Test
    void convert_when_streamMessage_has_string_and_bytes() throws JMSException {

        StreamMessage message = new ActiveMQStreamMessage();
        message.writeString("This is a test message.");
        message.writeBytes("This is another test message.".getBytes());
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        Object object = result.readObject();
        assertTrue(object instanceof String);
        assertEquals("This is a test message.", new String((String)object));

        object = result.readObject();
        assertTrue(object instanceof byte[]);
        assertEquals("This is another test message.", new String((byte[])object));
    }

    @Test
    void convert_when_streamMessage_has_bytes() throws JMSException {

        StreamMessage message = new ActiveMQStreamMessage();
        message.writeBytes("This is a test message.".getBytes());
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        Object object = result.readObject();
        assertTrue(object instanceof byte[]);
        assertEquals("This is a test message.", new String((byte[])object));
    }

    @Test
    void convert_when_streamMessage_has_JMSCorrelationID() throws JMSException {

        String jmsCorrelationID = "TestJMSCorrelationID";
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSCorrelationID(jmsCorrelationID);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsCorrelationID, result.getJMSCorrelationID());

    }

    @Test
    void convert_when_streamMessage_has_JMSCorrelationIDAsBytes() throws JMSException {

        String jmsCorrelationID = "TestJMSCorrelationID";
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSCorrelationID(jmsCorrelationID);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsCorrelationID, new String(result.getJMSCorrelationIDAsBytes()));

    }

    @Test
    void convert_when_streamMessage_has_JMSDeliveryModes() throws JMSException {

        int jmsDeliveryModes = Session.AUTO_ACKNOWLEDGE;
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSDeliveryMode(jmsDeliveryModes);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsDeliveryModes, result.getJMSDeliveryMode());

    }

    @Test
    void convert_when_streamMessage_has_jmsExpiration() throws JMSException {

        long jmsExpiration = 3600l;
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSExpiration(jmsExpiration);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsExpiration, result.getJMSExpiration());

    }

    @Test
    void convert_when_bytesMessage_has_headerProperties() throws JMSException {

        String BOOLEAN = "boolean";
        String BYTE = "byte";
        String DOUBLE = "double";
        String FLOAT = "float";
        String INT = "int";
        String LONG = "long";
        String OBJECT = "object";
        String SHORT = "short";
        String STRING = "string";

        StreamMessage message = new ActiveMQStreamMessage();
        message.setBooleanProperty(BOOLEAN, true);
        message.setByteProperty(BYTE, (byte)'b');
        message.setDoubleProperty(DOUBLE, Double.valueOf(10));
        message.setFloatProperty(FLOAT, Float.valueOf(12));
        message.setIntProperty(INT, Integer.valueOf(14));
        message.setLongProperty(LONG, Long.valueOf(16));
        message.setObjectProperty(OBJECT, new String("test string"));
        message.setShortProperty(SHORT, Short.valueOf((short)18));
        message.setStringProperty(STRING, "testStringAgain");
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertTrue(result.getBooleanProperty(BOOLEAN));
        assertEquals('b', result.getByteProperty(BYTE));
        assertEquals(Double.valueOf(10).doubleValue(), result.getDoubleProperty(DOUBLE));
        assertEquals(Float.valueOf(12).floatValue(), result.getFloatProperty(FLOAT));
        assertEquals(Integer.valueOf(14).intValue(), result.getIntProperty(INT));
        assertEquals(Long.valueOf(16).longValue(), result.getLongProperty(LONG));
        assertEquals("test string", result.getObjectProperty(OBJECT));
        assertEquals(Short.valueOf((short)18).shortValue(), result.getShortProperty(SHORT));
        assertEquals("testStringAgain", result.getStringProperty(STRING));
    }

    @Test
    void convert_when_streamMessage_has_JMSmessageId() throws JMSException {

        String jmsMessageId = "TestJMSMessageID";
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSMessageID(jmsMessageId);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        // TODO - find better workaround for the activeMQ ID: prefix
        assertEquals("ID:" + jmsMessageId, result.getJMSMessageID());
    }

    @Test
    void convert_when_streamMessage_has_JMSPriority() throws JMSException {

        int jmsPriority = 1;
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSPriority(jmsPriority);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsPriority, result.getJMSPriority());

    }

    @Test
    void convert_when_streamMessage_has_JMSRedelivery() throws JMSException {

        boolean jmsRedelivered = true;
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSRedelivered(jmsRedelivered);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsRedelivered, result.getJMSRedelivered());

    }

    @Test
    void convert_when_streamMessage_has_jmsTimestamp() throws JMSException {

        long jmsTimestamp = 3600l;
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSTimestamp(jmsTimestamp);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsTimestamp, result.getJMSTimestamp());

    }

    @Test
    void convert_when_streamMessage_has_JMSType() throws JMSException {

        String jmsType = "TestJMSType";
        StreamMessage message = new ActiveMQStreamMessage();
        message.setJMSType(jmsType);
        message.reset();

        // test
        StreamMessage result = uut.convert(message);

        //assert
        assertEquals(jmsType, result.getJMSType());

    }

}

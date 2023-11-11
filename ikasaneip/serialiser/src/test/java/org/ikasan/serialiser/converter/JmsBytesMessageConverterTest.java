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

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.jupiter.api.Test;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test JmsBytesMessageConverter
 * @author Ikasan Development Team
 */
class JmsBytesMessageConverterTest
{

    // Unit under test
    private JmsBytesMessageConverter uut = new JmsBytesMessageConverter();


    @Test
    void convert_when_bytesMessage_has_bytes() throws JMSException {

        BytesMessage message = new ActiveMQBytesMessage();
        message.writeBytes("This is a test message.".getBytes());
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(23, result.getBodyLength(), "expected " + result.getBodyLength());
        byte[] byteResults = new byte[23];
        assertEquals(23, result.readBytes(byteResults));
        assertEquals("This is a test message.", new String(byteResults));
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

        BytesMessage message = new ActiveMQBytesMessage();
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
        BytesMessage result = uut.convert(message);

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
    void convert_when_bytesMessage_has_JMSCorrelationID() throws JMSException {

        String jmsCorrelationID = "TestJMSCorrelationID";
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSCorrelationID(jmsCorrelationID);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsCorrelationID, result.getJMSCorrelationID());

    }

    @Test
    void convert_when_bytesMessage_has_JMSCorrelationIDAsBytes() throws JMSException {

        String jmsCorrelationID = "TestJMSCorrelationID";
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSCorrelationID(jmsCorrelationID);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsCorrelationID, new String(result.getJMSCorrelationIDAsBytes()));

    }

    @Test
    void convert_when_bytesMessage_has_JMSDeliveryModes() throws JMSException {

        int jmsDeliveryModes = Session.AUTO_ACKNOWLEDGE;
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSDeliveryMode(jmsDeliveryModes);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsDeliveryModes, result.getJMSDeliveryMode());

    }

    @Test
    void convert_when_bytesMessage_has_jmsExpiration() throws JMSException {

        long jmsExpiration = 3600l;
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSExpiration(jmsExpiration);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsExpiration, result.getJMSExpiration());

    }

    @Test
    void convert_when_bytesMessage_has_JMSmessageId() throws JMSException {

        String jmsMessageId = "TestJMSMessageID";
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSMessageID(jmsMessageId);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        // TODO - find better workaround for the activeMQ ID: prefix
        assertEquals("ID:" + jmsMessageId, result.getJMSMessageID());
    }

    @Test
    void convert_when_bytesMessage_has_JMSPriority() throws JMSException {

        int jmsPriority = 1;
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSPriority(jmsPriority);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsPriority, result.getJMSPriority());

    }

    @Test
    void convert_when_bytesMessage_has_JMSRedelivery() throws JMSException {

        boolean jmsRedelivered = true;
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSRedelivered(jmsRedelivered);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsRedelivered, result.getJMSRedelivered());

    }

    @Test
    void convert_when_bytesMessage_has_jmsTimestamp() throws JMSException {

        long jmsTimestamp = 3600l;
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSTimestamp(jmsTimestamp);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsTimestamp, result.getJMSTimestamp());

    }

    @Test
    void convert_when_bytesMessage_has_JMSType() throws JMSException {

        String jmsType = "TestJMSType";
        BytesMessage message = new ActiveMQBytesMessage();
        message.setJMSType(jmsType);
        message.reset();

        // test
        BytesMessage result = uut.convert(message);

        //assert
        assertEquals(jmsType, result.getJMSType());

    }

}

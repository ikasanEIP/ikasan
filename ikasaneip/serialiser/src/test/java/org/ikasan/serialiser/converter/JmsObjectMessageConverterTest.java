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

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.ikasan.serialiser.model.JmsObjectMessageDefaultImpl;
import org.junit.jupiter.api.Test;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test JmsObjectMessageConverter
 */
class JmsObjectMessageConverterTest {

    // Unit under test
    private JmsObjectMessageConverter uut = new JmsObjectMessageConverter();


    @Test
    void convert_when_objectMessage_has_object() throws JMSException {

        ObjectMessage message = new ActiveMQObjectMessage();
        message.setObject(new SimplePojo());

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertTrue(result.getObject() instanceof SimplePojo);

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

        ObjectMessage message = new ActiveMQObjectMessage();
        message.setBooleanProperty(BOOLEAN, true);
        message.setByteProperty(BYTE, (byte)'b');
        message.setDoubleProperty(DOUBLE, Double.valueOf(10));
        message.setFloatProperty(FLOAT, Float.valueOf(12));
        message.setIntProperty(INT, Integer.valueOf(14));
        message.setLongProperty(LONG, Long.valueOf(16));
        message.setObjectProperty(OBJECT, new String("test string"));
        message.setShortProperty(SHORT, Short.valueOf((short)18));
        message.setStringProperty(STRING, "testStringAgain");

        // test
        ObjectMessage result = uut.convert(message);

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
    void convert_when_objectMessage_has_JMSCorrelationID() throws JMSException {

        String jmsCorrelationID = "TestJMSCorrelationID";
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSCorrelationID(jmsCorrelationID);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsCorrelationID, result.getJMSCorrelationID());

    }

    @Test
    void convert_when_objectMessage_has_JMSCorrelationIDAsBytes() throws JMSException {

        String jmsCorrelationID = "TestJMSCorrelationID";
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSCorrelationID(jmsCorrelationID);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsCorrelationID, new String(result.getJMSCorrelationIDAsBytes()));

    }

    @Test
    void convert_when_objectMessage_has_JMSDeliveryModes() throws JMSException {

        int jmsDeliveryModes = Session.AUTO_ACKNOWLEDGE;
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSDeliveryMode(jmsDeliveryModes);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsDeliveryModes, result.getJMSDeliveryMode());

    }

    @Test
    void convert_when_objectMessage_has_jmsExpiration() throws JMSException {

        long jmsExpiration = 3600l;
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSExpiration(jmsExpiration);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsExpiration, result.getJMSExpiration());

    }

    @Test
    void convert_when_objectMessage_has_JMSmessageId() throws JMSException {

        String jmsMessageId = "TestJMSMessageID";
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSMessageID(jmsMessageId);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        // TODO - find better workaround for the activeMQ ID: prefix
        assertEquals("ID:" + jmsMessageId, result.getJMSMessageID());

    }

    @Test
    void convert_when_objectMessage_has_JMSPriority() throws JMSException {

        int jmsPriority = 1;
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSPriority(jmsPriority);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsPriority, result.getJMSPriority());

    }

    @Test
    void convert_when_objectMessage_has_JMSRedelivery() throws JMSException {

        boolean jmsRedelivered = true;
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSRedelivered(jmsRedelivered);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsRedelivered, result.getJMSRedelivered());

    }

    @Test
    void convert_when_objectMessage_has_jmsTimestamp() throws JMSException {

        long jmsTimestamp = 3600l;
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSTimestamp(jmsTimestamp);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsTimestamp, result.getJMSTimestamp());

    }

    @Test
    void convert_when_objectMessage_has_JMSType() throws JMSException {

        String jmsType = "TestJMSType";
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setJMSType(jmsType);

        // test
        ObjectMessage result = uut.convert(message);

        //assert
        assertEquals(jmsType, result.getJMSType());

    }

}

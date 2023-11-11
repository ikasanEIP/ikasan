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
package org.ikasan.component.endpoint.jms.spring.consumer;

import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional unit test cases for <code>IkasanListMessage</code>.
 * 
 * @author Ikasan Developmnet Team
 */
class IkasanListMessageTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    // mocked Destination
    Destination destination = mockery.mock(Destination.class);

    /**
     * Test
     */
    @Test
    void test_successful_ikasanListMessage() throws JMSException
    {
        IkasanListMessage ikasanListMessage = new IkasanListMessage();
        ikasanListMessage.setJMSMessageID("jmsMessageId");
        assertEquals("jmsMessageId", ikasanListMessage.getJMSMessageID());

        ikasanListMessage.setJMSTimestamp(100l);
        assertEquals(100l, ikasanListMessage.getJMSTimestamp());

        ikasanListMessage.setJMSCorrelationIDAsBytes("correlationId".getBytes());
        assertArrayEquals( ikasanListMessage.getJMSCorrelationIDAsBytes(), "correlationId".getBytes() );

        ikasanListMessage.setJMSCorrelationID("correlationId");
        assertEquals("correlationId", ikasanListMessage.getJMSCorrelationID());

        ikasanListMessage.setJMSReplyTo(destination);
        assertEquals(ikasanListMessage.getJMSReplyTo(), destination);

        ikasanListMessage.setJMSDestination(destination);
        assertEquals(ikasanListMessage.getJMSDestination(), destination);

        ikasanListMessage.setJMSDeliveryMode(1);
        assertEquals(1, ikasanListMessage.getJMSDeliveryMode());

        ikasanListMessage.setJMSRedelivered(true);
        assertTrue( ikasanListMessage.getJMSRedelivered() );

        ikasanListMessage.setJMSType("jmsType");
        assertEquals("jmsType", ikasanListMessage.getJMSType());

        ikasanListMessage.setJMSExpiration(1000);
        assertEquals(1000, ikasanListMessage.getJMSExpiration());

        ikasanListMessage.setJMSPriority(10);
        assertEquals(10, ikasanListMessage.getJMSPriority());

        ikasanListMessage.setBooleanProperty("booleanProperty", true);
        assertTrue( ikasanListMessage.getBooleanProperty("booleanProperty") );

        ikasanListMessage.setByteProperty("byteProperty", (byte)'a');
        assertEquals(ikasanListMessage.getByteProperty("byteProperty"), (byte) 'a');

        ikasanListMessage.setShortProperty("shortProperty", (short)1);
        assertEquals(ikasanListMessage.getShortProperty("shortProperty"), (short) 1);

        ikasanListMessage.setIntProperty("intProperty", (int)2);
        assertTrue( ikasanListMessage.getBooleanProperty("booleanProperty") );

        ikasanListMessage.setLongProperty("longProperty", (long)3);
        assertEquals(ikasanListMessage.getLongProperty("longProperty"), (long) 3);

        ikasanListMessage.setFloatProperty("floatProperty", (float)4);
        assertEquals(ikasanListMessage.getFloatProperty("floatProperty"), (float) 4);

        ikasanListMessage.setDoubleProperty("doubleProperty", (double)5);
        assertEquals(ikasanListMessage.getDoubleProperty("doubleProperty"), (double) 5);

        ikasanListMessage.setStringProperty("stringProperty", "stringProperty");
        assertEquals("stringProperty", ikasanListMessage.getStringProperty("stringProperty"));

        ikasanListMessage.setObjectProperty("objectProperty", Integer.valueOf(5));
        assertEquals(ikasanListMessage.getObjectProperty("objectProperty"), Integer.valueOf(5));

        ikasanListMessage.clearProperties();
    }
}
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

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Functional unit test cases for <code>IkasanListMessage</code>.
 * 
 * @author Ikasan Developmnet Team
 */
public class IkasanListMessageTest
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
    public void test_successful_ikasanListMessage() throws JMSException
    {
        IkasanListMessage ikasanListMessage = new IkasanListMessage();
        ikasanListMessage.setJMSMessageID("jmsMessageId");
        Assert.assertTrue( ikasanListMessage.getJMSMessageID().equals("jmsMessageId") );

        ikasanListMessage.setJMSTimestamp(100l);
        Assert.assertTrue( ikasanListMessage.getJMSTimestamp() == 100l );

        ikasanListMessage.setJMSCorrelationIDAsBytes("correlationId".getBytes());
        Assert.assertArrayEquals( ikasanListMessage.getJMSCorrelationIDAsBytes(), "correlationId".getBytes() );

        ikasanListMessage.setJMSCorrelationID("correlationId");
        Assert.assertTrue( ikasanListMessage.getJMSCorrelationID().equals("correlationId") );

        ikasanListMessage.setJMSReplyTo(destination);
        Assert.assertTrue( ikasanListMessage.getJMSReplyTo().equals(destination) );

        ikasanListMessage.setJMSDestination(destination);
        Assert.assertTrue( ikasanListMessage.getJMSDestination().equals(destination) );

        ikasanListMessage.setJMSDeliveryMode(1);
        Assert.assertTrue( ikasanListMessage.getJMSDeliveryMode() == 1);

        ikasanListMessage.setJMSRedelivered(true);
        Assert.assertTrue( ikasanListMessage.getJMSRedelivered() );

        ikasanListMessage.setJMSType("jmsType");
        Assert.assertTrue( ikasanListMessage.getJMSType().equals("jmsType") );

        ikasanListMessage.setJMSExpiration(1000);
        Assert.assertTrue( ikasanListMessage.getJMSExpiration() == 1000 );

        ikasanListMessage.setJMSPriority(10);
        Assert.assertTrue( ikasanListMessage.getJMSPriority() == 10 );

        ikasanListMessage.setBooleanProperty("booleanProperty", true);
        Assert.assertTrue( ikasanListMessage.getBooleanProperty("booleanProperty") );

        ikasanListMessage.setByteProperty("byteProperty", (byte)'a');
        Assert.assertTrue( ikasanListMessage.getByteProperty("byteProperty") == (byte)'a' );

        ikasanListMessage.setShortProperty("shortProperty", (short)1);
        Assert.assertTrue( ikasanListMessage.getShortProperty("shortProperty") == (short)1 );

        ikasanListMessage.setIntProperty("intProperty", (int)2);
        Assert.assertTrue( ikasanListMessage.getBooleanProperty("booleanProperty") );

        ikasanListMessage.setLongProperty("longProperty", (long)3);
        Assert.assertTrue( ikasanListMessage.getLongProperty("longProperty") == (long)3 );

        ikasanListMessage.setFloatProperty("floatProperty", (float)4);
        Assert.assertTrue( ikasanListMessage.getFloatProperty("floatProperty") == (float)4 );

        ikasanListMessage.setDoubleProperty("doubleProperty", (double)5);
        Assert.assertTrue( ikasanListMessage.getDoubleProperty("doubleProperty") == (double)5);

        ikasanListMessage.setStringProperty("stringProperty", "stringProperty");
        Assert.assertTrue( ikasanListMessage.getStringProperty("stringProperty").equals("stringProperty") );

        ikasanListMessage.setObjectProperty("objectProperty", Integer.valueOf(5));
        Assert.assertTrue( ikasanListMessage.getObjectProperty("objectProperty").equals( Integer.valueOf(5) ) );

        ikasanListMessage.clearProperties();
    }
}
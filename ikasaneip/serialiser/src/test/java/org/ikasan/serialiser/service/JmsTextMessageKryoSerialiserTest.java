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
package org.ikasan.serialiser.service;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Test class for JmsTextMessageKryoSerialiser.
 *
 * @author Ikasan Development Team
 */
@Ignore
public class JmsTextMessageKryoSerialiserTest
{
    private JmsTextMessageKryoSerialiser uut = new JmsTextMessageKryoSerialiser();

    /**
     * The context that the tests run in, allows for mocking actual concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final Kryo kryo = mockery.mock(Kryo.class);

    private final Output output = mockery.mock(Output.class);

    private final Input input = mockery.mock(Input.class);

    private final TextMessage textMessage = mockery.mock(TextMessage.class);

    @Test
    public void write_when_input_is_textMessage() throws JMSException
    {
        // Expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(textMessage).getText();
                will(returnValue("Test"));
                exactly(1).of(kryo).writeClassAndObject(output, "Test");
            }
        });
        //do test
        uut.write(kryo, output, textMessage);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void write_when_textMessage_throws_JMSException()
            throws JMSException
    {
        // Expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(textMessage).getText();
                will(throwException(new JMSException("Test JMS")));
            }
        });
        //do test
        uut.write(kryo, output, textMessage);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test
    public void read_when_input_is_a_string() throws JMSException
    {
        final String payload ="testPayload";
        // Expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(kryo).readClassAndObject(input);
                will(returnValue(payload));
            }
        });
        //do test
        TextMessage result = uut.read(kryo, input, TextMessage.class);
        // assert
        mockery.assertIsSatisfied();

        Assert.assertEquals(payload,result.getText());
    }

}


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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for JmsMapMessageKryoSerialiser.
 *
 * @author Ikasan Development Team
 */
public class JmsMapMessageKryoSerialiserTest
{
    private JmsMapMessageKryoSerialiser uut = new JmsMapMessageKryoSerialiser();

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

    private final MapMessage textMessage = mockery.mock(MapMessage.class);

    @Test public void write_when_input_is_mapMessage_has_no_keys() throws JMSException
    {
        // Expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(textMessage).getMapNames();
                will(returnValue(null));
                exactly(1).of(kryo).writeClassAndObject(with(any(Output.class)),with(any(Map.class)) );
            }
        });
        //do test
        uut.write(kryo, output, textMessage);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test public void write_when_input_is_mapMessage_has_single_value_in_map() throws JMSException
    {
        HashMap<Object,Object> hashMap = new HashMap<Object,Object>();
        hashMap.put("key","value");
        final Enumeration enumeration = new IteratorEnumeration(hashMap.keySet().iterator());

        // Expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(textMessage).getMapNames();
                will(returnValue(enumeration));

                exactly(1).of(textMessage).getObject("key");
                will(returnValue("value"));

                exactly(1).of(kryo).writeClassAndObject(with(any(Output.class)),with(any(Map.class)) );
            }
        });
        //do test
        uut.write(kryo, output, textMessage);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class) public void write_when_mapMessage_throws_JMSException() throws JMSException
    {
        // Expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(textMessage).getMapNames();
                will(throwException(new JMSException("Test JMS")));
            }
        });
        //do test
        uut.write(kryo, output, textMessage);
        // assert
        mockery.assertIsSatisfied();
    }
}

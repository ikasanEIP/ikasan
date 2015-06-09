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
package org.ikasan.compatibility.component.converter;

import junit.framework.Assert;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * Functional unit test cases for
 * <code>JmsMapSinglePayloadConverter</code>.
 * 
 * @author Ikasan Development Team
 */
public class JmsMapSinglePayloadTextConverterTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mocked mapMessage */
    MapMessage mapMessage = mockery.mock(MapMessage.class);

    /**
     * Test successful invocation of the converter
     */
    @Test
    public void test_successful_convert_payload_present() throws JMSException
    {
        final String payload = "payload";

        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mapMessage).getString("PAYLOAD_0_CONTENT");
                will(returnValue(payload));
            }
        });

        Converter<MapMessage,String> converter = new JmsMapSinglePayloadTextConverter();
        Assert.assertEquals("payload", converter.convert(mapMessage));

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation of the converter
     */
    @Test(expected = TransformationException.class)
    public void test_successful_convert_payload_completely_missing() throws JMSException
    {
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                // try i8 first
                exactly(1).of(mapMessage).getString("PAYLOAD_0_CONTENT");
                will(returnValue(null));

                // try i7 next
                exactly(1).of(mapMessage).getString("payload_0_content");
                will(returnValue(null));
            }
        });

        Converter<MapMessage,String> converter = new JmsMapSinglePayloadTextConverter();
        converter.convert(mapMessage);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation of the converter
     */
    @Test
    public void test_successful_convert_i8_payload_missing() throws JMSException
    {
        final String payload = "payload";

        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                // try i8 first
                exactly(1).of(mapMessage).getString("PAYLOAD_0_CONTENT");
                will(returnValue(null));

                // try i7 next
                exactly(1).of(mapMessage).getString("payload_0_content");
                will(returnValue(payload));
            }
        });

        Converter<MapMessage,String> converter = new JmsMapSinglePayloadTextConverter();
        converter.convert(mapMessage);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation of the converter
     */
    @Test(expected = TransformationException.class)
    public void test_successful_convert_failed_with_JMSException() throws JMSException
    {
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mapMessage).getString("PAYLOAD_0_CONTENT");
                will(throwException(new JMSException("test")));
            }
        });

        Converter<MapMessage,String> converter = new JmsMapSinglePayloadTextConverter();
        converter.convert(mapMessage);

        mockery.assertIsSatisfied();
    }

}
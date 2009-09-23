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
package org.ikasan.framework.component.transformation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.FormatExceptionTransformer;
import org.ikasan.framework.component.transformation.TransformationException;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.exception.user.ExceptionTransformer;
import org.ikasan.framework.exception.user.ExternalExceptionDefinition;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>FormatExceptionTransformer</code>
 * class.
 * 
 * @author Ikasan Development Team
 */
public class FormatExceptionTransformerTest
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

    /** Mock event */
    final Event event = mockery.mock(Event.class);

    /** Mock payload */
    final Payload payload = mockery.mock(Payload.class);

    /** Mock exceptionTransformer */
    final ExceptionTransformer exceptionTransformer = mockery.mock(ExceptionTransformer.class);

    /** Mock externalExceptionDef */
    final ExternalExceptionDefinition externalExceptionDef = mockery.mock(ExternalExceptionDefinition.class);
    
    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test failed constructor due to 'null' exceptionTransformer.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullExceptionTransformer()
    {
        new RealFormatExceptionTransformer(null, null, "payloadName");
    }

    /**
     * Test failed constructor due to 'null' externalExceptionDef.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullExternalExceptionDef()
    {
        new RealFormatExceptionTransformer(exceptionTransformer, null, "payloadName");
    }

    /**
     * Test successful formatException Transform.
     * @throws TransformerException 
     * @throws TransformationException 
     * 
     */
    @Test
    public void test_successful_formatExceptionTransform() throws TransformerException, TransformationException 
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);

        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // get event payloads
                one(event).getPayloads();
                will(returnValue(payloads));

                // get event payloads
                one(exceptionTransformer).transform(with(any(ExceptionContext.class)), with(any(ExternalExceptionDefinition.class)));
                will(returnValue("Formatted exception as a String"));

                // set new payload attributes
                for(Payload payload:payloads)
                {
                    one(payload).setContent(with(any(byte[].class)));
                    one(payload).setName(with(any(String.class)));
                    one(payload).setSpec(with(any(String.class)));
                    one(payload).setEncoding(with(any(String.class)));

                    // update event for new payload
                    one(event).setPayload(payload);
                }

            }
        });

        // run tests
        RealFormatExceptionTransformer formatExceptionTransformer = new RealFormatExceptionTransformer(exceptionTransformer, externalExceptionDef, "payloadName");
        formatExceptionTransformer.onEvent(event);
    }

    /**
     * Test failed formatException Transform due to XML transformer error.
     * @throws TransformerException 
     * @throws TransformationException 
     * 
     */
    @Test(expected = TransformationException.class)
    public void test_failed_formatExceptionTransform() throws TransformerException, TransformationException 
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);

        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // get event payloads
                one(event).getPayloads();
                will(returnValue(payloads));

                // get event payloads
                one(exceptionTransformer).transform(with(any(ExceptionContext.class)), with(any(ExternalExceptionDefinition.class)));
                will(throwException(new javax.xml.transform.TransformerException("failed transformer test")));
            }
        });

        // run tests
        RealFormatExceptionTransformer formatExceptionTransformer = new RealFormatExceptionTransformer(exceptionTransformer, externalExceptionDef, "payloadName");
        formatExceptionTransformer.onEvent(event);
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        // check all expectations were satisfied
        mockery.assertIsSatisfied();
    }
    
    /**
     * Inner class used to test the Abstract class of FormatExceptionTransformer.
     * @author Iksaan Developmnet Team
     *
     */
    public class RealFormatExceptionTransformer
        extends FormatExceptionTransformer
    {
        /**
         * Constructor
         * @param exceptionTransformer
         * @param externalExceptionDef
         * @param payloadName 
         */
        public RealFormatExceptionTransformer(ExceptionTransformer exceptionTransformer, 
                ExternalExceptionDefinition externalExceptionDef, String payloadName)
        {
            super(exceptionTransformer, externalExceptionDef, payloadName);
        }

        /* (non-Javadoc)
         * @see org.ikasan.framework.component.transformation.FormatExceptionTransformer#getException(org.ikasan.framework.component.Event)
         */
        @Override
        public Throwable getException(Event event) throws TransformationException
        {
            return new Exception("Test");
        }
    }

}

/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.component.transformation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.component.Event;
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
                    one(payload).setSpec(with(any(Spec.class)));


//                    // update event for new payload
//                    one(event).setPayload(payload);
                    //dont think this is needed as payload is already on event -RJD
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
     * @author Ikasan Development Team
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

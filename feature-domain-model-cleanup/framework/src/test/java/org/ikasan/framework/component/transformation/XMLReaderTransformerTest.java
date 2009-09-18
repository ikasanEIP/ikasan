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

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.XMLReader;

/**
 * Test class for <code>XMLReaderTransformer</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class XMLReaderTransformerTest
{
    /** Mockery */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /** XML Reader */
    final XMLReader xmlReader = mockery.mock(XMLReader.class);
    /** Payload */
    final Payload payload = mockery.mock(Payload.class);
    /** Event */
    final Event event = mockery.mock(Event.class);
    /** Transformer factory */
    final TransformerFactory transformerFactory = mockery.mock(TransformerFactory.class);
    /** Transformer */
    final Transformer transformer = mockery.mock(Transformer.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing
    }

    /**
     * Test happy constructor.
     * 
     * @throws TransformerException
     */
    @Test
    public void test_happyConstructor() 
        throws TransformerException
    {
        new XMLReaderTransformer(transformerFactory, xmlReader);
    }

    /**
     * Test failed constructor due to null TransformerFactory.
     * 
     * @throws TransformerException
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorWithNullTransformationFactory() 
        throws TransformerException
    {
        new XMLReaderTransformer(null, xmlReader);
    }

    /**
     * Test failed constructor due to null XMLReader.
     * 
     * @throws TransformerException
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorWithNullXmlReader() 
        throws TransformerException
    {
        new XMLReaderTransformer(transformerFactory, null);
    }

    /**
     * Test happy the invocation of the transform method with InputStream and
     * OutputStream args
     * 
     * @throws TransformerException
     * @throws TransformationException
     */
    @Test
    public void testTransformInputStreamOutputStream() 
        throws TransformerException, TransformationException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        mockery.checking(new Expectations()
        {
            {
                one(transformerFactory).newTransformer();
                will(returnValue(transformer));
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));

                one(event).getPayloads();
                will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue("content".getBytes()));
                one(payload).setContent((byte[]) with(a(byte[].class)));
                one(payload).setSpec(Spec.TEXT_XML);
            }
        });

        XMLReaderTransformer xmlReaderTransformer = new XMLReaderTransformer(transformerFactory, xmlReader);
        xmlReaderTransformer.onEvent(event);
    }

    /**
     * Test failed transformer within the invocation of the transform method.
     * 
     * @throws TransformerException
     * @throws TransformationException 
     */
    @SuppressWarnings({"unqualified-field-access"})
    @Test(expected = TransformationException.class)
    public void testTransformInputStreamOutputStream_throwsTransformationExcetpionForTransformerException() 
        throws TransformerException, TransformationException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(this.payload);
        mockery.checking(new Expectations()
        {
            {
                one(transformerFactory).newTransformer();
                will(returnValue(transformer));
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
                will(throwException(new TransformerException("test")));

                one(event).getPayloads();
                will(returnValue(payloads));
                one(payload).getContent();
            }
        });

        XMLReaderTransformer xmlReaderTransformer = new XMLReaderTransformer(transformerFactory, xmlReader);
        xmlReaderTransformer.onEvent(event);
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        mockery.assertIsSatisfied();
    }
    
}

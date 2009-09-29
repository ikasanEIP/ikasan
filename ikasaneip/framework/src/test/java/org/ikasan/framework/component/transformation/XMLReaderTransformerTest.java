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

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.ikasan.common.Payload;
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

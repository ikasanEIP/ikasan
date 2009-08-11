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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.component.Event;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.xml.sax.XMLReader;

/**
 * Test class for ExtendedXsltTransformer
 * 
 * @author Ikasan Development Team
 */
public class ExtendedXsltTransformerTest
{
    /**
     * Constructor
     * 
     * @throws URISyntaxException - Exception if URI is invalid
     */
    public ExtendedXsltTransformerTest() throws URISyntaxException
    {
        super();
        this.xslUri = new URI("blah");
    }

    /** Payload time stamp */
    final Long PAYLOAD_TIMESTAMP = 1218726802809l;

    /** Mockery for interfaces */
    Mockery mockery = new Mockery();

    /** Mockery for classes */
    private Mockery classMockery = new Mockery()
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
    final Event event = classMockery.mock(Event.class);

    /** The XSL URI */
    final URI xslUri;

    /** Transformer factory */
    final TransformerFactory transformerFactory = classMockery.mock(TransformerFactory.class);

    /** Templates */
    final Templates templates = classMockery.mock(Templates.class);

    /** Transformer */
    final Transformer transformer = classMockery.mock(Transformer.class);

    /** Transformer Exception */
    final TransformerException transformerException = new TransformerException((String) null);

    /**
     * Test the invocation of the transform method with InputStream and
     * OutputStream args
     * 
     * @throws TransformerException
     * @throws TransformationException
     */
    @Test
    public void testTransformInputStreamOutputStream() throws TransformerException, TransformationException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        classMockery.checking(new Expectations()
        {
            {
                one(transformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformerFactory).newTemplates((Source) with(a(Source.class)));
                will(returnValue(templates));
                one(event).getPayloads();
                will(returnValue(payloads));
                one(templates).newTransformer();
                will(returnValue(transformer));
                one(transformer).setOutputProperty("encoding", "");
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(payload).getCharset();
                one(payload).getContent();
                will(returnValue("content".getBytes()));
                one(payload).setContent((byte[]) with(a(byte[].class)));
                one(payload).setSpec(Spec.TEXT_XML.toString());
            }
        });
        ExtendedXsltTransformer flatFileTransformer = new ExtendedXsltTransformer(transformerFactory, xslUri, true, null, null, xmlReader);
        flatFileTransformer.onEvent(event);
        mockery.assertIsSatisfied();
        classMockery.assertIsSatisfied();
    }

    /**
     * Test the invocation of the transform method with InputStream and
     * OutputStream args
     * 
     * @throws TransformerException
     */
    @SuppressWarnings( { "unqualified-field-access" })
    @Test
    public void testTransformInputStreamOutputStream_throwsTransformationExceptionForTransformerException() throws TransformerException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(this.payload);
        this.classMockery.checking(new Expectations()
        {
            {
                one(transformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformerFactory).newTemplates((Source) with(a(Source.class)));
                will(returnValue(templates));
                one(event).getPayloads();
                will(returnValue(payloads));
                one(templates).newTransformer();
                will(returnValue(transformer));
                one(transformer).setOutputProperty("encoding", "");
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
                will(throwException(transformerException));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(payload).getCharset();
                one(payload).getContent();
                will(returnValue("content".getBytes()));
            }
        });
        ExtendedXsltTransformer flatFileTransformer = new ExtendedXsltTransformer(transformerFactory, xslUri, true, null, null, xmlReader);
        TransformationException transformationException = null;
        try
        {
            flatFileTransformer.onEvent(event);
            Assert.fail("TransformationException should have been thrown");
        }
        catch (TransformationException t)
        {
            transformationException = t;
        }
        Assert.assertNotNull("transformationException should have been thrown", transformationException);
        Assert.assertEquals("underlying cause should have been the TransformerException", transformerException, transformationException.getCause());
        mockery.assertIsSatisfied();
        classMockery.assertIsSatisfied();
    }

    /**
     * Test the invocation of the transform method with InputStream and
     * OutputStream args, constructing without specifying an XMLReader
     * 
     * @throws TransformerException
     * @throws TransformationException
     */
    @Test
    public void testTransformInputStreamOutputStreamWithDefaultXmlReader() throws TransformerException, TransformationException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        classMockery.checking(new Expectations()
        {
            {
                one(transformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformerFactory).newTemplates((Source) with(a(Source.class)));
                will(returnValue(templates));
                one(event).getPayloads();
                will(returnValue(payloads));
                one(templates).newTransformer();
                will(returnValue(transformer));
                one(transformer).setOutputProperty("encoding", "");
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(payload).getCharset();
                one(payload).getContent();
                will(returnValue("content".getBytes()));
                one(payload).setContent((byte[]) with(a(byte[].class)));
                one(payload).setSpec(Spec.TEXT_XML.toString());
            }
        });
        ExtendedXsltTransformer flatFileTransformer = new ExtendedXsltTransformer(transformerFactory, xslUri, true, null, null);
        flatFileTransformer.onEvent(event);
        mockery.assertIsSatisfied();
        classMockery.assertIsSatisfied();
    }
}

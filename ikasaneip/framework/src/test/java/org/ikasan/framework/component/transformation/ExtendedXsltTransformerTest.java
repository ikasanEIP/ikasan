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
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.xml.sax.XMLReader;

/**
 * Test class for FlatFileTransformer
 * 
 * @author Ikasan Development Team
 * 
 */
public class ExtendedXsltTransformerTest
{
    /**
     * Constructor
     * @throws URISyntaxException 
     */
    public ExtendedXsltTransformerTest() throws URISyntaxException
    {
        super();
        this.xslUri = new URI("blah");
    }

    /** Payload time stamp */
    final Long PAYLOAD_TIMESTAMP = 1218726802809l;
//    /** default XSL */
//    final String xsl = "<?xml version=" + '"' + "1.0" + '"' + " encoding=" + '"' + "UTF-8" + '"' + "?>\n" + "<xsl:stylesheet version=" + '"' + "1.0" + '"'
//            + " xmlns:xsl=" + '"' + "http://www.w3.org/1999/XSL/Transform" + '"' + " xmlns:fo=" + '"' + "http://www.w3.org/1999/XSL/Format" + '"' + ">"
//            + "</xsl:stylesheet>";
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
    /** Paylaod */
    final Payload payload = mockery.mock(Payload.class);
    /** Event */
    final Event event = classMockery.mock(Event.class);
    
    /** The XSL URI */
    final URI xslUri; 
//    /** InputStream */
//    final InputStream inputStream = new ByteArrayInputStream(xsl.getBytes());
//    /** Stream Source */
//    final StreamSource streamSource = new StreamSource(inputStream);
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
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(payload).getContent();
                will(returnValue("content".getBytes()));
                one(payload).setContent((byte[]) with(a(byte[].class)));
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
    @SuppressWarnings({"unqualified-field-access"})
    @Test
    public void testTransformInputStreamOutputStream_throwsTransformationExcetpionForTransformerException() throws TransformerException
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
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
                will(throwException(transformerException));
            }
        });
        mockery.checking(new Expectations()
        {
            {
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
                one(transformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));
                one(transformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });
        mockery.checking(new Expectations()
        {
            {
                one(payload).getContent();
                will(returnValue("content".getBytes()));
                one(payload).setContent((byte[]) with(a(byte[].class)));
            }
        });
        ExtendedXsltTransformer flatFileTransformer = new ExtendedXsltTransformer(transformerFactory, xslUri, true, null, null);
        flatFileTransformer.onEvent(event);
        mockery.assertIsSatisfied();
        classMockery.assertIsSatisfied();
    }
}

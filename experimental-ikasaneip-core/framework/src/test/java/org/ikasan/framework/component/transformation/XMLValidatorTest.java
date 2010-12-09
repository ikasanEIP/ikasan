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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.Payload;
import org.ikasan.core.component.transformation.TransformationException;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * This test class supports the <code>XMLValidator</code> class.
 * 
 * @author Ikasan Development Team
 */
public class XMLValidatorTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Document builder factory mockery */
    final DocumentBuilderFactory factory = this.classMockery.mock(DocumentBuilderFactory.class, "documentBuilderFactory");

    /** Document builder mockery */
    final DocumentBuilder builder = this.classMockery.mock(DocumentBuilder.class, "documentBuilder");

    /** class to be tested */
    private XMLValidator xmlValidator = null;

    /** mock event */
    final Event event = this.classMockery.mock(Event.class);
    /** mock payload */
    final Payload payload = this.classMockery.mock(Payload.class);

    /** payload list */
    List<Payload> payloads = new ArrayList<Payload>();

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing
    }

    /**
     * Test successful XML validation.
     * @throws TransformationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    @Test
    public void test_successfulXmlValidation()
        throws TransformationException, ParserConfigurationException, 
        SAXException, IOException
    {
        // create the class to be tested
        this.xmlValidator = new XMLValidator(this.factory);
        
        /** real payload list */
        this.payloads = new ArrayList<Payload>();
        this.payloads.add(this.payload);
        this.payloads.add(this.payload);
        final byte[] payloadContent = 
            ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<rootName/>").getBytes();
        final InputStream is = new ByteArrayInputStream(payloadContent);
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(event).idToString();
                exactly(2).of(payload).getId();will(returnValue("payloadId"));
                exactly(2).of(payload).getContent();
                will(returnValue(payloadContent));

                exactly(2).of(XMLValidatorTest.this.factory).newDocumentBuilder();
                will(returnValue(XMLValidatorTest.this.builder));
                exactly(2).of(XMLValidatorTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(2).of(XMLValidatorTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });

        this.xmlValidator.onEvent(this.event);
    }

    /**
     * Test failed XML validation.
     * @throws TransformationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    @Test(expected = TransformationException.class)
    public void test_failedXmlValidationDueToNonXmlPayload()
        throws TransformationException, ParserConfigurationException, 
        SAXException, IOException
    {
        // create the class to be tested
        this.xmlValidator = new XMLValidator(this.factory);
        
        /** real payload list */
        this.payloads = new ArrayList<Payload>();
        this.payloads.add(this.payload);
        final byte[] payloadContent = ("Im not XML!").getBytes();
        
        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(event).idToString();
                exactly(1).of(payload).getId();will(returnValue("payloadId"));
                exactly(1).of(payload).getContent();
                will(returnValue(payloadContent));
                exactly(2).of(XMLValidatorTest.this.factory).newDocumentBuilder();
                will(returnValue(XMLValidatorTest.this.builder));
                exactly(2).of(XMLValidatorTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(2).of(XMLValidatorTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(throwException(new SAXException("Not a valid XML document!")));
            }
        });

        this.xmlValidator.onEvent(this.event);
    }

    /**
     * Test failed constructor based on a 'null' parser.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructorFailureNullParser()
    {
        // create the class to be tested
        this.xmlValidator = new XMLValidator(null);
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        // empty
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(XMLValidatorTest.class);
    }
}

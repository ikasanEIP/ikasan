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
                exactly(2).of(payload).idToString();
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
                exactly(1).of(payload).idToString();
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

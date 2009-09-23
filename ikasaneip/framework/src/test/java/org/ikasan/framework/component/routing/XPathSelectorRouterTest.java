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
package org.ikasan.framework.component.routing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.Router;
import org.ikasan.framework.component.routing.RouterException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * This test class supports the <code>XPathSelectorRouter</code> class.
 * 
 * @author Ikasan Development Team
 */
public class XPathSelectorRouterTest
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

    /** Mocked Document builder factory */
    final DocumentBuilderFactory factory = this.mockery.mock(DocumentBuilderFactory.class, "documentBuilderFactory");

    /** Mocked Document builder */
    final DocumentBuilder builder = this.mockery.mock(DocumentBuilder.class, "documentBuilder");

    /** Mocked event */
    final Event event = this.mockery.mock(Event.class);
    
    /** Mocked payload */
    final Payload payload = this.mockery.mock(Payload.class);

    /** Mocked list of payloads */
    final List<Payload> payloads = new ArrayList<Payload>();
    
    /** Xpath expression */
    private String xpathExpression = new String("//*/element");
    
    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // populate the list with a mock payload
        this.payloads.add(this.payload);
    }

    /**
     * Test routeable xpath expressions without default transition.
     * @throws RouterException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    @Test
    public void test_validRouteWithNoDefaultOnConstructor()
        throws RouterException, SAXException, IOException, ParserConfigurationException
    {
        final byte[] payloadContent = new String("<?xml version=\"1.0\"?>"
                + "<root><element>one</element><element>two</element>" 
                + "<element>one</element></root>").getBytes();

        final InputStream is = new ByteArrayInputStream(payloadContent);
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

        this.mockery.checking(new Expectations()
        {
            {
                // get payloads, although we are only interested in primary
                one(event).getPayloads();
                will(returnValue(payloads));

                // get primary payload content
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathSelectorRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathSelectorRouterTest.this.builder));

                one(XPathSelectorRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathSelectorRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        // create the class to be tested
        Router xpathSelectorRouter = new XPathSelectorRouter(this.factory, this.xpathExpression);
        List<String> result = xpathSelectorRouter.onEvent(event);
        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.get(0).equals("one"));
    }

    /**
     * Test routeable xpath expressions without default transition.
     * @throws RouterException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    @Test
    public void test_validRouteWithDefaultExplicitlyTurnedOff()
        throws RouterException, SAXException, IOException, ParserConfigurationException
    {
        final byte[] payloadContent = new String("<?xml version=\"1.0\"?>"
                + "<root><element>one</element><element>two</element>" 
                + "<element>one</element></root>").getBytes();

        final InputStream is = new ByteArrayInputStream(payloadContent);
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

        boolean useDefault = false;
        
        this.mockery.checking(new Expectations()
        {
            {
                // get payloads, although we are only interested in primary
                one(event).getPayloads();
                will(returnValue(payloads));

                // get primary payload content
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathSelectorRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathSelectorRouterTest.this.builder));

                one(XPathSelectorRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathSelectorRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        // create the class to be tested
        Router xpathSelectorRouter = new XPathSelectorRouter(this.factory, this.xpathExpression, useDefault);
        List<String> result = xpathSelectorRouter.onEvent(event);
        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.get(0).equals("one"));
    }

    /**
     * Test routeable xpath expressions with default transition.
     * @throws RouterException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    @Test
    public void test_validRouteWithDefaultExplicitlyTurnedOn()
        throws RouterException, SAXException, IOException, ParserConfigurationException
    {
        final byte[] payloadContent = new String("<?xml version=\"1.0\"?>"
                + "<root><element>one</element><element>two</element>" 
                + "<element>one</element></root>").getBytes();

        final InputStream is = new ByteArrayInputStream(payloadContent);
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

        boolean useDefault = true;
        
        this.mockery.checking(new Expectations()
        {
            {
                // get payloads, although we are only interested in primary
                one(event).getPayloads();
                will(returnValue(payloads));

                // get primary payload content
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathSelectorRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathSelectorRouterTest.this.builder));

                one(XPathSelectorRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathSelectorRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        // create the class to be tested
        Router xpathSelectorRouter = new XPathSelectorRouter(this.factory, this.xpathExpression, useDefault);
        List<String> result = xpathSelectorRouter.onEvent(event);
        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.get(0).equals("one"));
    }

    /**
     * Test default routable based on no matching xpath expressions.
     * @throws RouterException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    @Test
    public void test_defaultRouteableWithoutMatch()
        throws RouterException, SAXException, IOException, ParserConfigurationException
    {
        final byte[] payloadContent = new String("<?xml version=\"1.0\"?>"
                + "<root/>").getBytes();

        final InputStream is = new ByteArrayInputStream(payloadContent);
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        boolean useDefault = true;
        
        this.mockery.checking(new Expectations()
        {
            {
                // get payloads, although we are only interested in primary
                one(event).getPayloads();
                will(returnValue(payloads));

                // get primary payload content
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathSelectorRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathSelectorRouterTest.this.builder));

                one(XPathSelectorRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathSelectorRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        // create the class to be tested
        Router xpathSelectorRouter = new XPathSelectorRouter(this.factory, this.xpathExpression, useDefault);
        List<String> result = xpathSelectorRouter.onEvent(event);
        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.get(0).equals("default"));
    }

    /**
     * Test unrouteable xpath expressions without default and subsequently throwing
     * an unroutable exception.
     * @throws RouterException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    @Test(expected = UnroutableEventException.class) 
    public void test_validUnrouteable()
        throws RouterException, SAXException, IOException, ParserConfigurationException
    {
        final byte[] payloadContent = new String("<?xml version=\"1.0\"?>"
                + "<root/>").getBytes();

        final InputStream is = new ByteArrayInputStream(payloadContent);
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        
        this.mockery.checking(new Expectations()
        {
            {
                // get payloads, although we are only interested in primary
                one(event).getPayloads();
                will(returnValue(payloads));

                // get primary payload content
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathSelectorRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathSelectorRouterTest.this.builder));

                one(XPathSelectorRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathSelectorRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
                
                one(event).idToString();
                will(returnValue("event ids"));
            }
        });
        
        Router xpathSelectorRouter = new XPathSelectorRouter(this.factory, this.xpathExpression);
        xpathSelectorRouter.onEvent(event);
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
}

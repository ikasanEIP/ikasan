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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.Payload;
import org.ikasan.core.component.routing.Router;
import org.ikasan.core.component.routing.RouterException;
import org.ikasan.core.component.routing.UnroutableEventException;
import org.ikasan.framework.component.Event;
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
 * This test class supports the <code>XpathBooleanRouter</code> class.
 * 
 * @author Ikasan Development Team
 */
public class XPathBooleanRouterTest
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

    /** Xpath expression to transition map */
    private Map<String,String> xpathExpressionToTransitions =
        new LinkedHashMap<String,String>();
    
    /** Mocked event */
    final Event event = this.classMockery.mock(Event.class);
    
    /** Mocked payload */
    final Payload payload = this.classMockery.mock(Payload.class);

    /** Mocked list of payloads */
    final List<Payload> payloads = new ArrayList<Payload>();
    
    /** XPath bool router */
    private Router xpathBooleanRouter;
    
    /** Event ids */
    private final String eventIds = "example event ids";
    
    /** First Match */
    private String firstMatch = "oneMatch";
    
    /** 2nd Match */
    private String secondMatch = "twoMatch";

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // populate the list with a mock payload
        this.payloads.add(this.payload);

        // populate the xpath expression:transition map
        this.xpathExpressionToTransitions.put("//*/element = 'one'", this.firstMatch);
        this.xpathExpressionToTransitions.put("//*/element = 'two'", this.secondMatch);
        this.xpathExpressionToTransitions.put("//*/element = 'burp'", "noMatch");
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

        // create the class to be tested
        this.xpathBooleanRouter = new XPathBooleanRouter(this.factory, 
                this.xpathExpressionToTransitions);

        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathBooleanRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathBooleanRouterTest.this.builder));

                one(XPathBooleanRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathBooleanRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        List<String> results = this.xpathBooleanRouter.route(event);
        Assert.assertTrue(results.size() == 2);
        Assert.assertEquals("Should return [" + this.firstMatch + "]", this.firstMatch, results.get(0));
        Assert.assertEquals("Should return [" + this.secondMatch + "]", this.secondMatch, results.get(1));
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
        
        // create the class to be tested
        this.xpathBooleanRouter = new XPathBooleanRouter(this.factory, 
                this.xpathExpressionToTransitions, useDefault);

        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathBooleanRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathBooleanRouterTest.this.builder));

                one(XPathBooleanRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathBooleanRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        List<String> results = this.xpathBooleanRouter.route(event);
        Assert.assertTrue(results.size() == 2);
        Assert.assertEquals("Should return [" + this.firstMatch + "]", this.firstMatch, results.get(0));
        Assert.assertEquals("Should return [" + this.secondMatch + "]", this.secondMatch, results.get(1));
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
        
        // create the class to be tested
        this.xpathBooleanRouter = new XPathBooleanRouter(this.factory, 
                this.xpathExpressionToTransitions, useDefault);

        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathBooleanRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathBooleanRouterTest.this.builder));

                one(XPathBooleanRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathBooleanRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        List<String> results = this.xpathBooleanRouter.route(event);
        Assert.assertTrue(results.size() == 2);
        Assert.assertEquals("Should return [" + this.firstMatch + "]", this.firstMatch, results.get(0));
        Assert.assertEquals("Should return [" + this.secondMatch + "]", this.secondMatch, results.get(1));
    }

    /**
     * Test default routable based on no matching xpath expressions.
     * @throws RouterException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    @Test
    public void test_defaultRouteable()
        throws RouterException, SAXException, IOException, ParserConfigurationException
    {
        final byte[] payloadContent = new String("<?xml version=\"1.0\"?>"
                + "<root/>").getBytes();

        final InputStream is = new ByteArrayInputStream(payloadContent);
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

        boolean useefault = true;
        
        // create the class to be tested
        this.xpathBooleanRouter = new XPathBooleanRouter(this.factory, 
                this.xpathExpressionToTransitions, useefault);

        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(event).idToString();
                will(returnValue(eventIds));
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathBooleanRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathBooleanRouterTest.this.builder));

                one(XPathBooleanRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathBooleanRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        List<String> results = this.xpathBooleanRouter.route(event);
        Assert.assertTrue(results.size() == 1);
        Assert.assertEquals("Should return [" + Router.DEFAULT_RESULT + "]", Router.DEFAULT_RESULT, results.get(0));
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

        boolean noDefault = false;
        
        // create the class to be tested
        this.xpathBooleanRouter = new XPathBooleanRouter(this.factory, 
                this.xpathExpressionToTransitions, noDefault);

        this.classMockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(event).idToString();
                will(returnValue(eventIds));
                one(payload).getContent();
                will(returnValue(payloadContent));

                one(XPathBooleanRouterTest.this.factory).newDocumentBuilder();
                will(returnValue(XPathBooleanRouterTest.this.builder));

                one(XPathBooleanRouterTest.this.builder).setErrorHandler(with(any(ErrorHandler.class)));
                one(XPathBooleanRouterTest.this.builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });
        
        this.xpathBooleanRouter.route(event);
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        this.xpathBooleanRouter = null;
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(XPathBooleanRouterTest.class);
    }
}

/*
 * $Id: DefaultEntityResolverTest.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/test/java/org/ikasan/common/xml/parser/DefaultEntityResolverTest.java $
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
package org.ikasan.common.xml.parser;

// Imported java classes
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;

// Imported classes
//import org.ikasan.common.CommonXMLParser;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.util.ResourceUtils;

// Imported log4j classes
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * This test class supports the <code>DefaultEntityResolver</code>.
 *
 * Make sure CLASSPATH contains the following,
 * test/src/conf - for router_1_0.xsd
 * test/src/conf/routerInJar.jar - for routerInJar_1_0.xsd
 *
 * @author Jeff Mitchell
 */
public class DefaultEntityResolverTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(DefaultEntityResolverTest.class);

    /**
     * Setup runs before each test
     */
    @Before public void setUp()
    {
        // Do Nothing
    }

    /**
     * Test entityResolver based on resolving to a file in the classpath
     * 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws IOException 
     */
    @Test public void testDefaultEntityResolverViaFile()
        throws ParserConfigurationException, SAXException, IOException
    {
    	boolean exceptionOnFail = true;
    	
        // test XML string
        URL testXML = ResourceUtils.getAsUrl("router.xml", exceptionOnFail);
        
        // instantiate parser
//        CommonXMLParser xmlParser = ResourceLoader.getInstance().newXMLParser();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(Boolean.TRUE);
        dbf.setNamespaceAware(Boolean.TRUE);
        dbf.setAttribute(org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE,
            org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA);
        
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new DefaultEntityResolver());
        
//        xmlParser.setValidation(true, XMLConstants.W3C_XML_SCHEMA_NS_URI);
        
        // if using entityResolver we must also useNamespace
//        xmlParser.setEntityResolver();
//        xmlParser.setNamespaceAware(new Boolean(true));
        
        // parse it
        db.parse(testXML.toString());

        // if we're here it must be ok
        Assert.assertTrue(true);
    }
    
    /**
     * Test entityResolver based on resolving to a file in the jar
     * in the classpath
     * 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws IOException 
     */
    @Test public void testDefaultEntityResolverViaJar()
        throws ParserConfigurationException, SAXException, IOException
    {
    	boolean exceptionOnFail = true;
    	
        // test XML string
        URL testXML = ResourceUtils.getAsUrl("routerInJar.xml", exceptionOnFail);
        
        // instantiate parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(Boolean.TRUE);
        dbf.setNamespaceAware(Boolean.TRUE);
        dbf.setAttribute(org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE,
            org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA);
        
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new DefaultEntityResolver());
//        CommonXMLParser xmlParser = ResourceLoader.getInstance().newXMLParser();
//
//        xmlParser.setValidation(true, XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        
//        // if using entityResolver we must also useNamespace
//        xmlParser.setEntityResolver();
//        xmlParser.setNamespaceAware(new Boolean(true));
        
        // parse it
        db.parse(testXML.toString());

        // if we're here it must be ok
        Assert.assertTrue(true);
    }
    
    /**
     * Teardown after each test
     */
    @After public void tearDown()
    {
        // nothing to tear down
        logger.info("tearDown");
    }

    /**
     * The suite is this class
     * @return JUnit Test class
     */
    public static junit.framework.Test suite() 
    {
        return new JUnit4TestAdapter(DefaultEntityResolverTest.class);
    }    
    
}

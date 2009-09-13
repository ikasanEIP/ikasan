/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
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
 * @author Ikasan Development Team
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

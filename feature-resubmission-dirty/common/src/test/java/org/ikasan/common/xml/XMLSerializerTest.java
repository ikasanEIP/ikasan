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
package org.ikasan.common.xml;

import java.io.StringWriter;

import junit.framework.JUnit4TestAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the XMLSerializer
 */
public class XMLSerializerTest
{
    /** The logger */
    private static Logger logger = Logger.getLogger(XMLSerializerTest.class);

    /**
     * Setup
     */
    @Before public void setUp()
    {
        logger.info("setUp"); //$NON-NLS-1$
    }
    
    /**
     * Test the class
     */
    @Test public void testXMLSerializer()
    {
        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
            + "<BOOK><AUTHOR>Bachelard.Gaston</AUTHOR><TITLE>The Poetics of Reverie</TITLE><TRANSLATOR>Daniel Russell</TRANSLATOR></BOOK>";
        
        try
        {
            Document doc = new DocumentImpl();
    
            // Create Root Element
            Element root = doc.createElement("BOOK");
    
            // Create 2nd level Element and attach to the Root Element
            Element item = doc.createElement("AUTHOR");
            item.appendChild(doc.createTextNode("Bachelard.Gaston"));
            root.appendChild(item);

            // Create one more Element
            item = doc.createElement("TITLE");
            item.appendChild(doc.createTextNode
                    ("The Poetics of Reverie"));
            root.appendChild(item);

            item = doc.createElement("TRANSLATOR");
            item.appendChild(doc.createTextNode("Daniel Russell"));
            root.appendChild(item);
            // Add the Root Element to Document
            doc.appendChild(root);
            // Serialize DOM
            OutputFormat format    = new OutputFormat (doc); 
            // as a String
            StringWriter stringOut = new StringWriter ();    
            XMLSerializer serial   = new XMLSerializer (stringOut, format);
            serial.serialize(doc);
            String result = stringOut.toString();
            // Display the XML
            Assert.assertTrue(result.equals(expectedResult));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tear down
     */
    @After public void tearDown()
    {
        logger.info("tearDown"); //$NON-NLS-1$
    }

    /**
     * JUnit suite
     * @return Test
     */
    public static junit.framework.Test suite() 
    {
        return new JUnit4TestAdapter(XMLSerializerTest.class);
    }    
    
    
}

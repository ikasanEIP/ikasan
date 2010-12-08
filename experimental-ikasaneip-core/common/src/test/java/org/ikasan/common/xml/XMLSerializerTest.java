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
package org.ikasan.common.xml;

import java.io.StringWriter;

import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

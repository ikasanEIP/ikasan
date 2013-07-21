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
package org.ikasan.framework.component.transformation.xslt.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Test class for {@link XsltUtils} class.
 * 
 * @author Ikasan Development Team
 *
 */
public class XsltUtilsTest
{
    /**
     * Test for {@link XsltUtils#serialize(org.w3c.dom.NodeList)}
     * 
     * @throws SAXException if an error creating test <code>nodeset</code> occurs.
     * @throws IOException if an error creating test <code>nodeset</code> occurs.
     * @throws ParserConfigurationException if an error creating test <code>NodeList</code> occurs.
     * @throws TransformerFactoryConfigurationError if an error serializing <code>nodeset</code> occurs.
     * @throws TransformerException if an error serializing <code>nodeset</code> occurs.
     */
    @Test
    public void test_serialize() throws SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException
    {
        String eol = System.getProperty("line.separator");
        String incomingXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test><group><value>whatever</value></group></test>";
        String expected = "<test>" + eol + "<group>" + eol + "<value>whatever</value>" + eol + "</group>" + eol + "</test>" + eol;

        InputStream is = new ByteArrayInputStream(incomingXml.getBytes());
        Document testXmlDom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        String result = XsltUtils.serialize(testXmlDom.getChildNodes());

        Assert.assertEquals(expected, result);
    }
}

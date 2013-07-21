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

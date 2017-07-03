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
package org.ikasan.marshaller.xml;

import org.junit.Assert;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.custommonkey.xmlunit.*;
import org.ikasan.marshaller.Marshaller;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Functional unit test cases for
 * <code>XmlStringJsonMarhsller</code>.
 * 
 * @author Ikasan Development Team
 */
public class XmlStringJsonMarshallerTest extends XMLTestCase
{
    static {{ XMLUnit.setIgnoreWhitespace(true); }}

    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rootName><elementOne>valueOne</elementOne></rootName>";

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test successful marshalling of XML to JSON
     */
    @Test
    public void test_successful_marshall_xmlStringToJson()
    {
        Marshaller<String,JSON> marshaller = new XmlStringJsonMarshaller(new XMLSerializer());
        JSON json = marshaller.marshall(xml);

        String result = json.toString();
        Assert.assertEquals("XML to JSON failed", "{" + '"' + "elementOne" + '"' + ":" + '"' + "valueOne" + '"' + "}", result);
    }

    /**
     * Test successful marshalling (XML to JSON) and unmarshalling (JSON to XML)
     */
    @Test
    public void test_successful_unmarshall_jsonToXml()
            throws IOException, SAXException
    {
        XmlJsonMarshaller marshaller = new XmlStringJsonMarshaller(new XMLSerializer());

        marshaller.getSerialiser().setRootName("rootName");
        marshaller.getSerialiser().setTypeHintsEnabled(false);

        JSONObject json = new JSONObject();
        json.put("elementOne", "valueOne");

        Diff diff = new Diff(xml, marshaller.unmarshall(json));
        assertTrue(diff.toString(), diff.similar());
    }

    /**
     * Difference listener implementation for ignoring certain dynamic element
     * content such as timestamps.
     *
     * @author Ikasan Development Team
     *
     */
    private class IgnoreNamedElementsDifferenceListener implements DifferenceListener
    {
        private Set<String> blackList = new HashSet<String>();

        /**
         * Constructor
         *
         * @param elementNames
         */
        public IgnoreNamedElementsDifferenceListener(String... elementNames)
        {
            for (String name : elementNames)
            {
                blackList.add(name);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see org.custommonkey.xmlunit.DifferenceListener#differenceFound(org.
         * custommonkey.xmlunit.Difference)
         */
        public int differenceFound(Difference difference)
        {
            if (difference.getId() == DifferenceConstants.TEXT_VALUE_ID)
            {
                if (blackList.contains(difference.getControlNodeDetail().getNode().getParentNode().getNodeName()))
                {
                    return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
            }

            return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.custommonkey.xmlunit.DifferenceListener#skippedComparison(org
         * .w3c.dom.Node, org.w3c.dom.Node)
         */
        public void skippedComparison(Node arg0, Node arg1)
        {
            // nothing to do
        }
    }
}

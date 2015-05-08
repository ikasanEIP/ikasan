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
package org.ikasan.mapping.keyQueryProcessor.impl;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessor;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorException;
import org.w3c.dom.Document;


/**
 * @author Ikasan Development Team
 *
 */
public class XPathKeyLocationQueryProcessor implements KeyLocationQueryProcessor
{
    /** The XPath Factory that we will use to evaluate the various XPaths */
    private final XPathFactory xpathFactory = XPathFactory.newInstance();

    /** The DocumentBuilderFactory used to resolve the payload from byte[] into a Document  */
    private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.keyQueryProcessor.KeyLocationQueryProcessor#getKeyValueFromPayload(java.lang.String, byte[])
     */
    @Override
    public String getKeyValueFromPayload(String keyLocation, byte[] payload) throws KeyLocationQueryProcessorException
    {
        try
        {
            DocumentBuilder parser = this.documentBuilderFactory.newDocumentBuilder();

            Document document = parser.parse(new ByteArrayInputStream(payload));
            XPath xpath = this.xpathFactory.newXPath();

            // Evaluate the XPath
            return (String)xpath.evaluate(keyLocation, document, XPathConstants.STRING);
        }
        catch (Exception e)
        {
            throw new KeyLocationQueryProcessorException(e);
        }
    }
}

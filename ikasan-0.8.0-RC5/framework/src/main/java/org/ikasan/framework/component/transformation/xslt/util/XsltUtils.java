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

import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides a range of static XSLT utility methods.
 * Currently, this class has the following methods:-
 * <p>
 *
 * <ol>
 *   <li>serialize -
 *     This method returns string of the specified node set.
 *   </li>
 * </ol>
 * 
 * @author Ikasan Development Team
 */
public class XsltUtils
{
    /** Logger instance */
    private static final Logger logger = Logger.getLogger(XsltUtils.class);

    /**
     * Returns string by serializing the specified node set.
     * 
     *<pre>
     * # =============================================
     * #    Stylesheet for serialize()
     * # =============================================
     * &lt;xsl:stylesheet
     *   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     *   xmlns:xalan="http://xml.apache.org/xslt"
     *   xmlns:utils="xalan://com.mizuho.cmi.london.core.shared.xalan.utils.XsltUtils"
     *   version="1.0"
     *   exclude-result-prefixes="xalan utils"&gt;
     *
     * &lt;xsl:output method="xml" encoding="UTF-8"
     *      cdata-section-elements="item" indent="yes" xalan:indent-amount="2"/&gt;
     *
     * &lt;xsl:template match="/"&gt;
     *   &lt;xsl:element name="root"&gt;
     *     &lt;xsl:for-each select="/root/data"&gt;
     *       &lt;xsl:element name="item"&gt;
     *         &lt;xsl:value-of select="utils:serialize(.)"/&gt;
     *       &lt;/xsl:element&gt;
     *     &lt;/xsl:for-each&gt;
     *   &lt;/xsl:element&gt;
     * &lt;/xsl:template&gt;
     *</pre>
     * @param nodeset      is the node set to be serialized.
     * @return             the string.
     * @throws TransformerFactoryConfigurationError if an error creating <code>Transformer</code> occurs.
     * @throws TransformerException if an error while serializing <code>nodeset</code> occurs.
     *
     */
    public static String serialize(NodeList nodeset) throws TransformerFactoryConfigurationError, TransformerException
    {
        Transformer serializer = TransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < nodeset.getLength(); i++)
        {
            Node node = nodeset.item(i);
            if (node != null)
            {
                serializer.transform(new DOMSource(node), new StreamResult(outputStream));
            }
        }

        String serializedXml = new String(outputStream.toByteArray());
        logger.debug("Serialized xml = [" + serializedXml + "].");
        return serializedXml;
    }
}

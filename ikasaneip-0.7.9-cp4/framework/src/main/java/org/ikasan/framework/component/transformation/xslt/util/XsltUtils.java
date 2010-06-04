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

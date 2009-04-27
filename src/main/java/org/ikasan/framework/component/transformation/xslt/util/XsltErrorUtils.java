/* 
 * $Id: XsltErrorUtils.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/xslt/util/XsltErrorUtils.java $
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

// Imported java classes
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.transformation.TrivialTransformerException;

/**
 * This class provides a range of static XSLT utility methods for error
 * handling.
 * 
 * @author Ikasan Development Team
 */
public class XsltErrorUtils
{
    /**
     * The logger instance.
     */
    private static final Logger logger = Logger.getLogger(XsltErrorUtils.class);

    /**
     * Throws an Exception with the specified detail message.
     *  *
     * 
     * <pre>
     * # =============================================
     * #    Stylesheet for throwException()
     * # =============================================
     * &lt;?xml version=&quot;1.0&quot;?&gt;
     * &lt;xsl:stylesheet
     *   xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot;
     *   xmlns:xalan=&quot;http://xml.apache.org/xslt&quot;
     *   xmlns:error=&quot;xalan://org.ikasan.framework.component.xslt.util.XsltErrorUtils&quot;
     *   version=&quot;1.0&quot;
     *   exclude-result-prefixes=&quot;xalan error&quot;&gt;
     * 
     * &lt;xsl:output method=&quot;xml&quot; encoding=&quot;UTF-8&quot; indent=&quot;yes&quot; xalan:indent-amount=&quot;2&quot;/&gt;
     * 
     * &lt;xsl:template match=&quot;/&quot;&gt;
     *   &lt;xsl:element name=&quot;xyz&quot;&gt;
     *     &lt;xsl:element name=&quot;newElement&quot;&gt;
     *       &lt;xsl:choose&gt;
     *         &lt;xsl:when test=&quot;/root/data[1]/j&quot;&gt;
     *           &lt;xsl:value-of select =&quot;/root/data[1]/j&quot;/&gt;
     *         &lt;/xsl:when&gt;
     *         &lt;xsl:otherwise&gt;
     *           &lt;xsl:value-of select =&quot;error:throwException('Element /root/data[1]/j not found.')&quot;/&gt;
     *         &lt;/xsl:otherwise&gt;
     *       &lt;/xsl:choose&gt;
     *     &lt;/xsl:element&gt;
     *   &lt;/xsl:element&gt;
     * &lt;/xsl:template&gt;
     * 
     * &lt;/xsl:stylesheet&gt;
     * </pre>
     * 
     * @param message is the detail message.
     * @throws TransformerException
     */
    public static void throwException(String message) throws TransformerException
    {
        logger.debug("Exception detail =[" + message + "].");
        throw new TransformerException(message);
    }

    /**
     * Utility method for handling XSLT errors that we dont want to stop things
     * 
     * @param message
     * @throws TrivialTransformerException
     */
    public static void throwTrivialException(String message) throws TrivialTransformerException
    {
        logger.debug("Exception detail =[" + message + "].");
        throw new TrivialTransformerException(message);
    }
}

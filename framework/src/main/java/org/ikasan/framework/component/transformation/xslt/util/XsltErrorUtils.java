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

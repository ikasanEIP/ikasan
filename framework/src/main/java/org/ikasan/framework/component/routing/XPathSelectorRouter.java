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
package org.ikasan.framework.component.routing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.core.component.routing.Router;
import org.ikasan.core.component.routing.RouterException;
import org.ikasan.core.component.routing.UnroutableEventException;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.ExceptionThrowingErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * XPath router evaluation based on evaluating an incoming XPath expression into the node value.
 * The resulting node value is returned.
 * 
 * The pre-configured documentBuilderFactory and XPath selector expression
 * are passed to this class on construction.
 * 
 * @author Ikasan Development Team
 */
public class XPathSelectorRouter extends SingleResultRouter
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(XPathSelectorRouter.class);

    /** Pre-configured document builder factory */
    private DocumentBuilderFactory factory;

    /** ErrorHandler instance */
    private ErrorHandler errorHandler = new ExceptionThrowingErrorHandler();

    /** Single xpath expression to evaluate */
    private String xpathExpression;

    /**
     * Flag, if set to true, will allow default result to be returned on non matches,<br>
     * otherwise exception will be thrown
     */
    private boolean returnsDefaultForNonMatches = false;

    /**
     * Constructor
     * 
     * @param factory - configured <code>DocumentBuilderFactory</code>
     * @param xpathExpression - configured XPath expression to evaluate
     * @param returnsDefaultForNonMatches - use the 'default' transition if no other transitions are matched
     */
    public XPathSelectorRouter(final DocumentBuilderFactory factory,
            final String xpathExpression, final boolean returnsDefaultForNonMatches)
    {
        this.factory = factory;
        if (this.factory == null)
        {
            throw new IllegalArgumentException("factory cannot be 'null'.");
        }
        this.xpathExpression = xpathExpression;
        if (this.xpathExpression == null || this.xpathExpression.length() == 0)
        {
            throw new IllegalArgumentException("xpath expression cannot be 'null' or empty.");
        }
        this.returnsDefaultForNonMatches = returnsDefaultForNonMatches;
    }

    /**
     * Constructor with no default transition supported.
     * 
     * @param factory - configured <code>DocumentBuilderFactory</code>
     * @param xpathExpression - configured XPath expression
     */
    public XPathSelectorRouter(final DocumentBuilderFactory factory,
            final String xpathExpression)
    {
        // Invoke constructor without (false) default routing
        this(factory, xpathExpression, false);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.component.routing.SingleResultRouter#evaluate(org.ikasan.framework.component.Event)
     */
    @Override
    public String evaluate(Event event) throws RouterException
    {
        List<Payload> payloads = event.getPayloads();
        Payload primaryPayload = payloads.get(0);
        byte[] content = primaryPayload.getContent();

        try
        {
            DocumentBuilder builder = this.factory.newDocumentBuilder();
            builder.setErrorHandler(this.errorHandler);
            InputStream payloadContentAsInputStream = new ByteArrayInputStream(content);
            Document document = builder.parse(payloadContentAsInputStream);
            XPath xpathInst = XPathFactory.newInstance().newXPath();
            String result = (String) xpathInst.evaluate(xpathExpression, document, XPathConstants.STRING);
            if (logger.isDebugEnabled())
            {
                logger.debug("xpath[" + xpathExpression + "] result[" + result + "]");
            }
            if(result == null || result.length() == 0)
            {
                if (this.returnsDefaultForNonMatches)
                {
                    return Router.DEFAULT_RESULT;
                }
                
                throw new UnroutableEventException("Unable to resolve xpath expression [" 
                    + xpathExpression + "] to a value for event "
                    + event.idToString());
            }
            return result;
        }
        catch (XPathExpressionException e)
        {
            throw new RouterException(e);
        }
        catch (SAXException e)
        {
            logger.warn("Failed on content[" + new String(content) + "]");
            throw new RouterException(e);
        }
        catch (IOException e)
        {
            throw new RouterException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new RouterException(e);
        }
    }
}

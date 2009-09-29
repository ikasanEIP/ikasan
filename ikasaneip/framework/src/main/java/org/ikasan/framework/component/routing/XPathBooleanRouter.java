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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.ExceptionThrowingErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * XPath router evaluation based on evaluating the incoming XPath expressions into Boolean results. Where these results
 * are TRUE the associated XPath expression transition is added to the returned List for subsequent routing.
 * 
 * The pre-configured parser and a map of XPath expressions and their associated transition name are passed to this
 * class on construction.
 * 
 * @author Ikasan Development Team
 */
public class XPathBooleanRouter implements Router
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(XPathBooleanRouter.class);

    /** Pre-configured document builder factory */
    private DocumentBuilderFactory factory;

    /** ErrorHandler instance */
    private ErrorHandler errorHandler = new ExceptionThrowingErrorHandler();

    /** Map of xpath expressions:transistions */
    private Map<String, String> xpathExpressionToTransition;

    /**
     * Flag, if set to true, will allow default result to be returned on non matches,<br>
     * otherwise exception will be thrown
     */
    private boolean returnsDefaultForNonMatches = false;

    /**
     * Constructor
     * 
     * @param factory - configured <code>DocumentBuilderFactory</code>
     * @param xpathExpressionToTransition - configured XPath expressions and associated transitions
     * @param returnsDefaultForNonMatches - use the 'default' transition if no other transitions are matched
     */
    public XPathBooleanRouter(final DocumentBuilderFactory factory,
            final Map<String, String> xpathExpressionToTransition, final boolean returnsDefaultForNonMatches)
    {
        this.factory = factory;
        if (this.factory == null)
        {
            throw new IllegalArgumentException("factory cannot be 'null'.");
        }
        this.xpathExpressionToTransition = xpathExpressionToTransition;
        if (this.xpathExpressionToTransition == null || this.xpathExpressionToTransition.isEmpty())
        {
            throw new IllegalArgumentException("xpath expression transition map cannot be 'null' or empty.");
        }
        this.returnsDefaultForNonMatches = returnsDefaultForNonMatches;
    }

    /**
     * Constructor with no default transition supported.
     * 
     * @param factory - configured <code>DocumentBuilderFactory</code>
     * @param xpathExpressionToTransition - configured XPath expressions and associated transitions
     */
    public XPathBooleanRouter(final DocumentBuilderFactory factory,
            final Map<String, String> xpathExpressionToTransition)
    {
        // Invoke constructor without (false) default routing
        this(factory, xpathExpressionToTransition, false);
    }

    /**
     * Implementation of the onEvent XPathBooleanRoute
     * 
     * @param event The event to route
     * @return List
     * @throws RouterException Exception if we could not route the event
     */
    public List<String> onEvent(Event event) throws RouterException
    {
        List<String> xpathResultTransitions = new ArrayList<String>();
        StringBuilder infoMsg = new StringBuilder();
        try
        {
            List<Payload> payloads = event.getPayloads();
            Payload primaryPayload = payloads.get(0);
            DocumentBuilder builder = this.factory.newDocumentBuilder();
            builder.setErrorHandler(this.errorHandler);
            InputStream payloadContentAsInputStream = new ByteArrayInputStream(primaryPayload.getContent());
            Document document = builder.parse(payloadContentAsInputStream);
            XPath xpathInst = XPathFactory.newInstance().newXPath();
            Iterator<Map.Entry<String, String>> it = this.xpathExpressionToTransition.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<String, String> pairs = it.next();
                String xpathExpression = pairs.getKey();
                Boolean result = (Boolean) xpathInst.evaluate(xpathExpression, document, XPathConstants.BOOLEAN);
                if (result != null && result.equals(Boolean.TRUE))
                {
                    xpathResultTransitions.add(pairs.getValue());
                    if (logger.isDebugEnabled())
                        logger.debug("xpath [" + xpathExpression + "] [" + result + "]. Transition ["
                                + pairs.getValue() + "] added to list.");
                }
                else
                {
                    infoMsg.append(" xpath [");
                    infoMsg.append(xpathExpression);
                    infoMsg.append("] [");
                    infoMsg.append(result);
                    infoMsg.append("]. Transition [");
                    infoMsg.append(pairs.getValue());
                    infoMsg.append("] NOT added to list.");
                    if (logger.isDebugEnabled()) logger.debug(infoMsg);
                }
            }
        }
        catch (XPathExpressionException e)
        {
            throw new RouterException(e);
        }
        catch (SAXException e)
        {
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
        // Do we have any matches
        if (xpathResultTransitions.size() < 1)
        {
            // Add default transition if configured to do so
            if (this.returnsDefaultForNonMatches)
            {
                xpathResultTransitions.add(Router.DEFAULT_RESULT);
            }
            else
            {
                throw new UnroutableEventException("Unable to resolve to a routing transition for event. "
                        + event.idToString() + infoMsg);
            }
        }
        return xpathResultTransitions;
    }
}

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
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.Router;
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

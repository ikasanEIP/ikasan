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

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.ExceptionThrowingErrorHandler;
import org.ikasan.framework.component.transformation.XMLValidator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * Implementation of {@link Router} that returns a single result:valid or invalid based on validation of the incoming payload(s).
 * 
 * @author Ikasan Development Team
 *
 */
public class XMLValidatorRouter extends SingleResultRouter
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(XMLValidator.class);

    /** Result where xml is valid */
    private static final String XML_VALID = "valid";

    /** Result where xml is invalid */
    private static final String XML_INVALID = "invalid";

    /** Pre-configured document builder factory */
    private DocumentBuilderFactory factory;

    /** ErrorHandler instance */
    private ErrorHandler errorHandler = new ExceptionThrowingErrorHandler();

    /**
     * Constructor
     * 
     * @param factory - pre configured document builder factory
     */
    public XMLValidatorRouter(final DocumentBuilderFactory factory)
    {
        this.factory = factory;
        if (this.factory == null)
        {
            throw new IllegalArgumentException("document builder factory cannot be 'null'.");
        }
    }

    @Override
    protected String evaluate(Event event) throws RouterException
    {
        String result =null;
        List<Payload> payloads = event.getPayloads();
        try
        {
            for (Payload payload : payloads)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug(payload.idToString());
                }
                DocumentBuilder builder = this.factory.newDocumentBuilder();
                builder.setErrorHandler(this.errorHandler);
                InputStream payloadContentAsInputStream = new ByteArrayInputStream(payload.getContent());
                builder.parse(payloadContentAsInputStream);
                result = XML_VALID;
            }
        }
        catch (SAXException e)
        {
            event.setException(e);
            result = XML_INVALID;
        }
        catch (IOException e)
        {
            event.setException(e);
            result = XML_INVALID;
        }
        catch (ParserConfigurationException e)
        {
            event.setException(e);
            result = XML_INVALID;
        }
        if (result == null)
        {
            throw new RouterException("Unable to resolve to a routing transition for event.");
        }
        return result;
    }
}

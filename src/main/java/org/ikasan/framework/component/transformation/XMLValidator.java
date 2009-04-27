/*
 * $Id: XMLValidator.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/XMLValidator.java $
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
package org.ikasan.framework.component.transformation;

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
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * XML Validator uses an injected XML parser to validate each incoming payload content.
 * 
 * The required parser must be pre-configured for validation.
 * 
 * @author Ikasan Development Team
 */
public class XMLValidator implements Transformer
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(XMLValidator.class);

    /** Pre-configured document builder factory */
    private DocumentBuilderFactory factory;

    /** ErrorHandler instance */
    private ErrorHandler errorHandler = new ExceptionThrowingErrorHandler();

    /**
     * Constructor
     * 
     * @param factory - pre configured document builder factory
     */
    public XMLValidator(final DocumentBuilderFactory factory)
    {
        this.factory = factory;
        if (this.factory == null)
        {
            throw new IllegalArgumentException("document builder factory cannot be 'null'.");
        }
    }

    /**
     * Implementation of the onEvent XMLValidation
     * 
     * @param event - Event containing payload(s) to validated
     * @throws TransformationException - Thrown if error parsing payload content
     */
    public void onEvent(Event event) throws TransformationException
    {
        List<Payload> payloads = event.getPayloads();
        if (logger.isDebugEnabled())
        {
            logger.debug(event.idToString());
        }
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
            }
        }
        catch (SAXException e)
        {
            throw new TransformationException(e);
        }
        catch (IOException e)
        {
            throw new TransformationException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new TransformationException(e);
        }
    }
}

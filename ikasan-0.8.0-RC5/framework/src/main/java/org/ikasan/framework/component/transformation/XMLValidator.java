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
                    logger.debug("Payload ["+payload.getId()+"]");
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

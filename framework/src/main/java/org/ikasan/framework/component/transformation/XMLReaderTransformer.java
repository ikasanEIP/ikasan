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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.core.component.transformation.TransformationException;
import org.ikasan.core.component.transformation.Transformer;
import org.ikasan.framework.component.Event;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * This class is a Transformer that acts on incoming <code>Payload</code>s within an <code>Event</code> and transforms
 * the content to XML based on the supplied XMLReader instance.
 * <p>
 * This is a copy transform working off of the SAX call backs on the reader to provide an output XML.
 * </p>
 * <b>NOTE: This is not a stylesheet transformer.</b> This implementation is notable for the following reasons:
 * <p>
 * 1) It is intended to be thread safe with respect to the underlying <code>javax.xml.transform.Transformer</code> as a
 * new instance is created every time the business method (onEvent) is called.
 * </p>
 * <p>
 * 2) Its intended use is to transform non-xml <code>Payload</code>s to XML through the configuration of a content
 * specific <code>XMLReader</code>. e.g. an <code>XMLReader</code> implementation capable of reading fixed length flat
 * files can be injected on construction thus allowing flat file (fixed length) payloads to be transformed to XML.
 * </p>
 * <p>
 * 3) Rather than relying on the default <code>ErrorListener</code> this transformer supplies its own implementation
 * designed to propagate the exceptions thrown for parse time errors and warnings
 * </p>
 * 
 * @author Ikasan Development Team
 */
public class XMLReaderTransformer implements Transformer
{
    /** Reader class used to consume incoming content */
    private XMLReader xmlReader;

    /** <code>TransformerFactory</code> instance */
    private TransformerFactory transformerFactory;

    /**
     * A very sensitive ErrorListener that will barf on errors.<br>
     * This replaces the default ErrorListener that simply logs all sorts of things that should really cause a failure
     */
    private ErrorListener exceptionThrowingErrorListener = new ExceptionThrowingErrorListener();

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(XMLReaderTransformer.class);

    /**
     * Constructor
     * 
     * @param transformerFactory The transformer factory that provides the transformers
     * @param xmlReader The XML REader to use
     */
    public XMLReaderTransformer(TransformerFactory transformerFactory, XMLReader xmlReader)
    {
        this.transformerFactory = transformerFactory;
        if (this.transformerFactory == null)
        {
            throw new IllegalArgumentException("transformerFactory cannot be 'null'");
        }
        this.xmlReader = xmlReader;
        if (this.xmlReader == null)
        {
            throw new IllegalArgumentException("xmlReader cannot be 'null'");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.transformation.Transformer#onEvent(org .ikasan.framework.component.Event)
     */
    public void onEvent(Event event) throws TransformationException
    {
        List<Payload> payloads = event.getPayloads();
        for (Payload payload : payloads)
        {
            InputStream untransformedDataStream = new ByteArrayInputStream(payload.getContent());
            ByteArrayOutputStream transformedDataStream = new ByteArrayOutputStream();
            try
            {
                InputSource inputSource = new InputSource(untransformedDataStream);
                SAXSource saxSource = new SAXSource(this.xmlReader, inputSource);
                // Create the transformer instance
                javax.xml.transform.Transformer transformer = this.transformerFactory.newTransformer();
                transformer.setErrorListener(this.exceptionThrowingErrorListener);
                // Transform away...
                transformer.transform(saxSource, new StreamResult(transformedDataStream));
            }
            catch (TransformerException e)
            {
                throw new TransformationException(e);
            }
            // Set the transformed data back onto the Payload
            byte[] transformedData = transformedDataStream.toByteArray();
            logger.debug("setting payload content [" + new String(transformedData) + "]");
            payload.setContent(transformedData);
            logger.debug(new String(transformedData));
        }
    }
}

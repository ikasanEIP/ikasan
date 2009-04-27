/*
 * $Id: XMLReaderTransformer.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/XMLReaderTransformer.java $
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
import org.ikasan.common.component.Spec;
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
            payload.setSpec(Spec.TEXT_XML.toString());
            logger.debug(new String(transformedData));
        }
    }
}

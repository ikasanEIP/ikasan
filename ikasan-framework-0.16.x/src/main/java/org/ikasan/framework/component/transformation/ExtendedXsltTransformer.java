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
package org.ikasan.framework.component.transformation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.component.Event;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * This class is an XSLT Transformer component that acts on all an <code>Event</code>'s <code>Payload</code>s,
 * transforming them using the supplied style sheet.
 * 
 * This implementation is notable for the following reasons:
 * 
 * 1) It is intended to be threadsafe with respect to the underlying <code>javax.xml.transform.Transformer</code> as a
 * new instance is created every time the business method (onEvent) is called. Each instance of this class will
 * associate to one and only one style sheet during its life, and as such instances will be good for only one type of
 * transformation only.
 * 
 * 2) Is is intended to be capable of transforming non-xml <code>Payload</code>s through the configuration of a content
 * specific <code>XMLReader</code>.  E.g. an <code>XMLReader</code> implementation capable of reading fixed length flat
 * files can be injected on construction thus allowing flat file (fixed length) payloads to be directly transformed with
 * XSLT
 * 
 * 3) It is designed to allow a set of externally sourced (injected) java beans to be supplied scoped to the underlying
 * transformer. This allows for such function as database calls from the XSLT to be supported indirectly through the
 * injection of externally managed supporting beans
 * 
 * 4) Rather than relying on the default <code>ErrorListener</code> this transformer supplies its own implementation
 * designed to propagate the exceptions thrown for parse time errors and warnings
 * 
 * @author Ikasan Development Team
 */
public class ExtendedXsltTransformer implements Transformer
{
    /** Reader class used to consume incoming content */
    private XMLReader xmlReader;

    /** XSLTC templates, if we are using translets */
    private Templates templates;

    /** StylesheetUri for the transformation */
    private URI styleSheetUri;

    /** <code>TransformerFactory</code> instance */
    private TransformerFactory transformerFactory;

    /** Additional Java beans to be made available to the transformer at transform time */
    private Map<String, Object> externalDataBeans;

    /**
     * A very sensitive ErrorListener that will throw errors.  This replaces the default ErrorListener that simply logs
     * all sorts of things that should really cause a failure
     */
    private ErrorListener exceptionThrowingErrorListener = new ExceptionThrowingErrorListener();

    /**
     * Any transformation parameters that do not change on a per transformation/payload basis This can be configured and
     * set once up front.
     */
    private Map<String, String> transformationParameters;

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(ExtendedXsltTransformer.class);

    /** A New PayloadName to set on the transformed Payloads */
    private String payloadName;

    /**
     * Constructor
     * 
     * @param transformerFactory - Transformer Factory to use
     * @param styleSheetUri - URI for stylesheet for transformation
     * @param useTranslets - flag controlling whether or not we should compile the stylesheet up front
     * @param externalDataBeans - map of named beans that are made available to the SLT
     * @param transformationParameters - any parameters used by the stylesheet that do not change on a per invocation
     *            basis
     * @param xmlReader - used for reading source on transformation            
     * 
     * @throws TransformerConfigurationException - Exception if the transformation fails badly
     */
    public ExtendedXsltTransformer(TransformerFactory transformerFactory, URI styleSheetUri, boolean useTranslets,
            Map<String, Object> externalDataBeans, Map<String, String> transformationParameters, XMLReader xmlReader)
            throws TransformerConfigurationException
    {
        // Make sure that any errors in the style sheet result in an exception
        transformerFactory.setErrorListener(this.exceptionThrowingErrorListener);
        this.xmlReader = xmlReader;
        this.styleSheetUri = styleSheetUri;
        this.transformerFactory = transformerFactory;
        this.externalDataBeans = externalDataBeans;
        this.transformationParameters = transformationParameters;
        logger.info("styleSheetUri=[" + styleSheetUri + "]");
        if (useTranslets)
        {
            logger.debug("using translets!");
            StreamSource streamSource = new StreamSource(styleSheetUri.toString());
            this.templates = transformerFactory.newTemplates(streamSource);
        }
    }

    /**
     * Constructor
     * 
     * @param transformerFactory - Transformer Factory to use
     * @param styleSheetUri - URI for stylesheet for transformation
     * @param useTranslets - flag controlling whether or not we should compile the stylesheet upfront
     * @param externalDataBeans - map of named beans that are made available to the xslt
     * @param transformationParameters - any parameters used by the stylesheet that do not change on a per invocation
     *            basis
     * 
     * @throws TransformerConfigurationException - Exception if the transformation fails badly
     */
    public ExtendedXsltTransformer(TransformerFactory transformerFactory, URI styleSheetUri, boolean useTranslets,
            Map<String, Object> externalDataBeans, Map<String, String> transformationParameters)
            throws TransformerConfigurationException
    {
        this(transformerFactory, styleSheetUri, useTranslets, externalDataBeans, transformationParameters, null);
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
            try
            {
                this.transform(payload);
            }
            catch (TransformerException e)
            {
                throw new TransformationException(e);
            }
            finally
            {
                // Tidiness, cleanup the ThreadLocal
                ThreadLocalBeansWrapper.remove();
            }
        }
    }

    /**
     * Resolves a new <code>javax.xml.transform.Transformer</code> instance for this transformation. This will either be
     * a product of the templates (if we are using translets) or a direct product of the transformerFactory
     * 
     * In either case there are standard Payload fields that are exposed as parameters
     * 
     * @return A Transformer
     * @throws TransformerConfigurationException - Exception if the transformation fails badly
     */
    private javax.xml.transform.Transformer getTransformer() throws TransformerConfigurationException
    {
        javax.xml.transform.Transformer transformer = null;
        if (this.templates != null)
        {
            transformer = this.templates.newTransformer();
        }
        else
        {
            StreamSource streamSource = new StreamSource(this.styleSheetUri.toString());
            transformer = this.transformerFactory.newTransformer(streamSource);
        }
        // Set our custom error listener to ensure errors aren't ignored
        transformer.setErrorListener(this.exceptionThrowingErrorListener);
        // Set standard set of payload fields as parameters on the transformer
        setPayloadParameters(transformer);
        // Set any other parameters on the transformer
        addTransformationParameters(transformer, this.transformationParameters);
        return transformer;
    }

    /**
     * Creates a parameter list of known fields derived from the <code>Payload</code> and makes these available to the
     * transformer
     * 
     * @param transformer - Transformer to set the parameters on
     */
    protected void setPayloadParameters(javax.xml.transform.Transformer transformer)
    {
        // TODO - going forward this could become a method on Payload like
        // payload.getTransformationParameters();
        Map<String, String> payloadTransformationParamters = new HashMap<String, String>();
        addTransformationParameters(transformer, payloadTransformationParamters);
    }

    /**
     * Adds a map of parameters to the specified transformer
     * 
     * @param transformer - Transformer to add the parameters to
     * @param parameters - Parameters to set
     */
    private void addTransformationParameters(javax.xml.transform.Transformer transformer, Map<String, String> parameters)
    {
        if (parameters != null)
        {
            for (String parameterName : parameters.keySet())
            {
                transformer.setParameter(parameterName, parameters.get(parameterName));
            }
        }
    }

    /**
     * Adds a map of parameters to the list of parameters already set, typically 
     * used by children of this class.
     * 
     * @param parameters - Parameters to set
     */
    protected void addTransformationParameters(Map<String, String> parameters)
    {
        this.transformationParameters.putAll(parameters);
    }
    
    /**
     * Accessor for optional payloadName
     * 
     * @return payloadName or null if it has not been set
     */
    public String getPayloadName()
    {
        return this.payloadName;
    }

    /**
     * Mutator for payloadName
     * 
     * @param payloadName - The payload name to set
     */
    public void setPayloadName(String payloadName)
    {
        this.payloadName = payloadName;
    }

    /**
     * Transforms the payload content.
     * @param payload The payload to be transformed
     * @throws TransformerException if an error occurs while transforming.
     */
    protected void transform(Payload payload) throws TransformerException
    {
        // Make the external data beans available to later execution within
        // this thread.
        ThreadLocalBeansWrapper.setBeans(this.externalDataBeans);
        InputStream untransformedDataStream = new ByteArrayInputStream(payload.getContent());
        ByteArrayOutputStream transformedDataStream = new ByteArrayOutputStream();
        InputSource inputSource = new InputSource(untransformedDataStream);
        //Setup the saxSource using our xmlReader if we have one
        SAXSource saxSource = null;
        if (this.xmlReader == null)
        {
            saxSource = new SAXSource(inputSource);
        }
        else
        {
            saxSource = new SAXSource(this.xmlReader, inputSource);
        }
        // Get a handle to a new Transformer
        javax.xml.transform.Transformer transformer = getTransformer();
        // Make sure we're using the correct character set encoding
        transformer.setOutputProperty(OutputKeys.ENCODING, payload.getCharset());
        //Transform away...
        transformer.transform(saxSource, new StreamResult(transformedDataStream));
        //set the transformed data back onto the Payload
        byte[] transformedData = transformedDataStream.toByteArray();
        logger.debug("Setting payload content [" + new String(transformedData) + "]");
        payload.setContent(transformedData);
        //Update the payload name with something new if we have configured a new value
        /*
         * TODO - this is a hangover from the XSLTransformer/DefualtXslTransformer which always sets the payloadName
         * as the rootName from the DefaultXsltTransformer every time. as we do not set the rootName explicitly
         * here, we have the option of setting the payloadName as anything arbitrary we want. This is arguably an
         * entirely separate transformation and therefore doesn't belong here. There is an implied constraint here
         * that the paylodName of an XML payload is always the same as the root of the XML doc. This needs to be
         * reconsidered, as we sometimes use the payloadName for other things like the fileName if we are later
         * delivering this as a file
         */
        if (this.payloadName != null)
        {
            payload.setName(this.payloadName);
        }
        payload.setSpec(Spec.TEXT_XML.toString());
        logger.debug(new String(transformedData));
    }
}

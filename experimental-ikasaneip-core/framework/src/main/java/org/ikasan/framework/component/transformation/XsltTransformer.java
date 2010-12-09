/*
 * $Id$
 * $URL$
 *
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.component.transformation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.hibernate.property.Setter;
import org.ikasan.common.Payload;
import org.ikasan.core.component.transformation.TransformationException;
import org.ikasan.core.component.transformation.Transformer;
import org.ikasan.core.configuration.ConfiguredResource;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.configuration.XsltConfiguration;
import org.ikasan.framework.component.transformation.xslt.util.ClasspathURIResolver;
import org.ikasan.framework.flow.ManagedResource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * This class is an XSLT Transformer component that acts on all an <code>Event</code>'s <code>Payload</code>s,
 * transforming them using the supplied style sheet.
 * 
 * This implementation is notable for the following reasons:
 * 
 * <ol>
 * <li>It is intended to be threadsafe with respect to the underlying {@link javax.xml.transform.Transformer} as a
 * new instance is created for every payload. Each instance of this class will
 * associate to one and only one style sheet during its life, and as such instances will be good for only one type of
 * transformation only.</li>
 * <br><br>
 * <li>Is is intended to be capable of transforming non-xml <code>Payload</code>s through the configuration of a content
 * specific {@link XMLReader}; e.g. an <code>XMLReader</code> implementation capable of reading fixed length flat
 * files can be setter-injected thus allowing flat file (fixed length) payloads to be directly transformed with
 * XSLT. See {@link XsltTransformer#setXmlReader(XMLReader)}</li>
 * <br><br>
 * <li>It is designed to allow a set of externally sourced (injected) java objects to be supplied scoped to the underlying
 * transformer. This allows for such function as database calls from the XSLT to be supported indirectly through the
 * injection of externally managed supporting beans. See {@link XsltTransformer#setExternalResources(Map)}</li>
 * <br><br>
 * <li>Rather than relying on the default <code>ErrorListener</code> this transformer supplies its own implementation
 * designed to propagate the exceptions thrown for parse time errors and warnings. This can be overridden by using
 * {@link Setter} {@link #setErrorListener(ErrorListener)}</li>.
 * <br><br>
 * <li>The ability to configure its properties at runtime through implementation of {@link ConfiguredResource} contract.
 * The configuration object allows for configuring use of translets (compiling a stylesheet) and the stylesheet's location</li>
 * <br><br>
 * <li>Configured stylesheets can either be loaded off of application's classpath, file system, web server ..etc.
 * However, mixing them is not possible.</li>
 * </ol>
 * 
 * <p>
 * <b>Gotchas to be aware of...</b><br>
 * <ul>
 * <li>When loading stylehsheets off of classpath, if the stylesheet tries to embed other stylesheets via <code>xsl:import</code> and/or
 * <code>xsl:include</code> elements, then a custom {@link URIResolver} implementation capable of loading resources from classpath 
 * must be set on constructor-injected {@link TransformerFactory}. Also, if any of stylesheets load files using
 * <code>document()</code>function, the custome {@link URIResolver} must also be set on the {@link javax.xml.transform.Transformer}
 * object created. This dictated by <code>javax.xml.transform</code> API peculiar design!</li>
 * </ul>
 * 
 * @see ClasspathURIResolver
 * @see XsltConfiguration
 * @see ExceptionThrowingErrorListener
 * 
 * @author Ikasan Development Team
 */
public class XsltTransformer implements Transformer, ManagedResource, ConfiguredResource<XsltConfiguration>
{
    /**
     * Classpath URL prefix <code>classpath:</code> expected in stylesheet locations
     * to be picked up off of classpath
     */
    private final static String CLASSPATH_URL_PREFIX = "classpath:";

    /** Configuration of resource in this component*/
    private XsltConfiguration configuration;

    /** Unique id for configured resource in this component */
    private String configuredResourceId;

    /** Reader class used to consume incoming content */
    private XMLReader xmlReader;

    /** XSLTC templates, if we are using translets */
    private Templates templates;

    /** <code>TransformerFactory</code> instance for creating {@link javax.xml.transform.Transformer}*/
    private TransformerFactory transformerFactory;

    /**
     * A very sensitive ErrorListener that will throw errors.  This replaces the default ErrorListener that simply logs
     * all sorts of things that should really cause a failure
     */
    private ErrorListener errorListener = new ExceptionThrowingErrorListener();

    /**
     * Any transformation parameters that do not change on a per transformation/payload basis This can be configured and
     * set once up front.
     */
    private Map<String, String> transformationParameters;

    /** Additional Java resources to be made available to the transformer at transform time */
    private Map<String, Object> externalResources;

    /** A New PayloadName to set on the transformed Payloads */
    private String payloadName;

    /** A custom implementation of URIResolver */
    private URIResolver uriResolver;

    /**
     * Constructor
     * 
     * @param transformerFactory - Transformer Factory to use
     * 
     */
    public XsltTransformer(final TransformerFactory transformerFactory)
    {
        this.transformerFactory = transformerFactory;
        if (this.transformerFactory == null)
        {
            throw new IllegalArgumentException("The TransformerFactory cannot be null.");
        }
        this.transformerFactory.setErrorListener(this.errorListener);
    }

    /**
     * Accessor
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
     * Override the default {@link URIResolver} provided
     * by transformer library.
     * 
     * @param resolver custom {@link URIResolver} implementation
     */
    public void setURIResolver(URIResolver resolver)
    {
        this.uriResolver = resolver;
    }

    /**
     * Accessor
     * @return the xmlReader
     */
    public XMLReader getXmlReader()
    {
        return this.xmlReader;
    }

    /**
     * Mutator
     * @param xmlReader the xmlReader to set
     */
    public void setXmlReader(XMLReader xmlReader)
    {
        this.xmlReader = xmlReader;
    }

    /**
     * Accessor
     * @return the transformationParameters
     */
    public Map<String, String> getTransformationParameters()
    {
        return this.transformationParameters;
    }

    /**
     * Mutator
     * @param transformationParameters the transformationParameters to set
     */
    public void setTransformationParameters(Map<String, String> transformationParameters)
    {
        this.transformationParameters = transformationParameters;
    }

    /**
     * Accessor
     * @return the externalDataBeans
     */
    public Map<String, Object> getExternalResources()
    {
        return this.externalResources;
    }

    /**
     * Mutator
     * @param externalResources the externalDataBeans to set
     */
    public void setExternalResources(Map<String, Object> externalResources)
    {
        this.externalResources = externalResources;
    }

    /**
     * Accessor
     * @return the errorListener
     */
    public ErrorListener getErrorListener()
    {
        return this.errorListener;
    }

    /**
     * Mutator
     * @param errorListener the errorListener to set
     */
    public void setErrorListener(ErrorListener errorListener)
    {
        this.errorListener = errorListener;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.ConfiguredResource#getConfiguredResourceId()
     */
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.ConfiguredResource#setConfiguredResourceId(java.lang.String)
     */
    public void setConfiguredResourceId(String id)
    {
        this.configuredResourceId = id;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.ConfiguredResource#getConfiguration()
     */
    public XsltConfiguration getConfiguration()
    {
        return this.configuration;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.ConfiguredResource#setConfiguration(java.lang.Object)
     */
    public void setConfiguration(XsltConfiguration configuration)
    {
        this.configuration = configuration;
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
    private javax.xml.transform.Transformer createNewConfiguredTransformer() throws TransformerConfigurationException
    {
        javax.xml.transform.Transformer transformer = null;
        if (this.templates != null)
        {
            transformer = this.templates.newTransformer();
        }
        else
        {
            StreamSource streamSource = new StreamSource(this.configuration.getStylesheetLocation());
            transformer = this.transformerFactory.newTransformer(streamSource);
        }
        this.configureTransformer(transformer);
        return transformer;
    }

    /**
     * Configure {@link javax.xml.transform.Transformer} instance created with any extra
     * parameters and/or optional custom attributes, e.g.: URIResolver, ErrorListener ..etc.
     * 
     * @param transformer Transformer to be configured
     */
    private void configureTransformer(javax.xml.transform.Transformer transformer)
    {
        // Set our custom error listener to ensure errors aren't ignored
        transformer.setErrorListener(this.errorListener);

        // Set standard set of payload fields as parameters on the transformer
        this.setPayloadParameters(transformer);

        // Set any other parameters on the transformer
        this.addTransformationParameters(transformer, this.transformationParameters);

        // Set custom URIResolver if any
        if (this.uriResolver != null)
        {
            transformer.setURIResolver(this.uriResolver);
        }
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
        this.addTransformationParameters(transformer, payloadTransformationParamters);
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
     * Transforms the payload content.
     * 
     * @param payload The payload to be transformed
     * @throws TransformerException if an error occurs while transforming.
     */
    protected void transform(Payload payload) throws TransformerException
    {
        // Make the external data beans available to later execution within this thread.
        ThreadLocalBeansWrapper.setBeans(this.externalResources);

        // The input xml
        Source sourceXml = this.createSourceXml(payload);

        // The output xml
        ByteArrayOutputStream transformedDataStream = new ByteArrayOutputStream();

        // Get a new Transformer
        javax.xml.transform.Transformer transformer = this.createNewConfiguredTransformer();

        //Transform away...
        transformer.transform(sourceXml, new StreamResult(transformedDataStream));

        // Set the transformed data back onto the Payload
        byte[] transformedData = transformedDataStream.toByteArray();
        payload.setContent(transformedData);

//      See jira IKASAN-534
        //Update the payload name with something new if we have configured a new value
//        /*
//         * TODO - this is a hangover from the XSLTransformer/DefualtXslTransformer which always sets the payloadName
//         * as the rootName from the DefaultXsltTransformer every time. as we do not set the rootName explicitly
//         * here, we have the option of setting the payloadName as anything arbitrary we want. This is arguably an
//         * entirely separate transformation and therefore doesn't belong here. There is an implied constraint here
//         * that the paylodName of an XML payload is always the same as the root of the XML doc. This needs to be
//         * reconsidered, as we sometimes use the payloadName for other things like the fileName if we are later
//         * delivering this as a file
//         */
//        if (this.payloadName != null)
//        {
//            payload.setName(this.payloadName);
//        }
//        payload.setSpec(Spec.TEXT_XML.toString());
    }

    /**
     * Extract a payload's content into a XML source
     * 
     * @param payload a payload whose content is valid xml
     * @return input xml as {@link SAXSource}
     */
    private Source createSourceXml(final Payload payload)
    {
        InputStream untransformedDataStream = new ByteArrayInputStream(payload.getContent());
        InputSource inputSource = new InputSource(untransformedDataStream);
        //Setup the saxSource using our xmlReader if we have one
        Source source = null;
        if (this.xmlReader == null)
        {
            source = new SAXSource(inputSource);
        }
        else
        {
            source = new SAXSource(this.xmlReader, inputSource);
        }
        return source;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.ManagedResource#startManagedResource()
     */
    public void startManagedResource()
    {
        /*
         * Given available runtime configurations in XsltConfiguration, we can
         * only manage the creation of templates at the moment.
         */
        if (this.configuration.isUseTranslets())
        {
            try
            {
                this.templates = this.transformerFactory.newTemplates(this.createTransformationInstructions());
                
            }
            catch (TransformerConfigurationException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Representation of stylesheet as {@link StreamSource}
     * 
     * @return 
     */
    private Source createTransformationInstructions()
    {
        Source xsltSource = null;
        String xslLocation = this.configuration.getStylesheetLocation();
        if (xslLocation.startsWith(CLASSPATH_URL_PREFIX))
        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream xslIS = classLoader.getResourceAsStream(this.stripClasspathScheme(xslLocation));
            xsltSource = new StreamSource(xslIS);
        }
        else
        {
            xsltSource = new StreamSource(xslLocation);
        }
        return xsltSource;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.ManagedResource#stopManagedResource()
     */
    public void stopManagedResource()
    {
        // Get rid of existing Templates if any
        this.templates = null;
    }

    /**
     * Utility method to remove the <code>classpath:</code> prefix from injected
     * stylesheet location.
     * @see ClassLoader#getResource(String)
     * 
     * @param xslLocation raw stylesheet location
     * @return stylesheet location without <code>classpath:</code> prefix.
     * 
     */
    private String stripClasspathScheme(final String xslLocation)
    {
        int index = xslLocation.indexOf(CLASSPATH_URL_PREFIX) + CLASSPATH_URL_PREFIX.length();
        return xslLocation.substring(index);
    }
}

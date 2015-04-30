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
package org.ikasan.component.converter.xml;

// NOTE THIS IS A TEMPORARY PORT OF AN IKASAN 0.8x component that is not yet available in 0.9.x
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
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

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


/**
 *
 /** This class is an XSLT Transformer component that acts on all an <code>Event</code>'s <code>Payload</code>s,
 * transforming them using the supplied style sheet.
 *
 * This implementation is notable for the following reasons:
 *
 * <ol>
 * <li>It is intended to be threadsafe with respect to the underlying {@link javax.xml.transform.Transformer} as a new
 * instance is created for every payload. Each instance of this class will associate to one and only one style sheet
 * during its life, and as such instances will be good for only one type of transformation only.</li>
 * <br>
 * <br>
 * <li>Is is intended to be capable of transforming non-xml <code>Payload</code>s through the configuration of a content
 * specific {@link XMLReader}; e.g. an <code>XMLReader</code> implementation capable of reading fixed length flat files
 * can be setter-injected thus allowing flat file (fixed length) payloads to be directly transformed with XSLT. See
 * {@link XsltConverter#setXmlReader(XMLReader)}</li>
 * <br>
 * <br>
 * <li>It is designed to allow a set of externally sourced (injected) java objects to be supplied scoped to the
 * underlying transformer. This allows for such function as database calls from the XSLT to be supported indirectly
 * through the injection of externally managed supporting beans. See {@link XsltConverter#setExternalResources(Map)}</li>
 * <br>
 * <br>
 * <li>Rather than relying on the default <code>ErrorListener</code> this transformer supplies its own implementation
 * designed to propagate the exceptions thrown for parse time errors and warnings. This can be overridden by using
 * {@link #setErrorListener(ErrorListener)}</li>. <br>
 * <br>
 * <li>The ability to configure its properties at runtime through implementation of {@link ConfiguredResource} contract.
 * The configuration object allows for configuring use of translets (compiling a stylesheet) and the stylesheet's
 * location</li>
 * <br>
 * <br>
 * <li>Configured stylesheets can either be loaded off of application's classpath, file system, web server ..etc.
 * However, mixing them is not possible.</li>
 * </ol>
 *
 * <p>
 * <b>Gotchas to be aware of...</b><br>
 * <ul>
 * <li>When loading stylehsheets off of classpath, if the stylesheet tries to embed other stylesheets via
 * <code>xsl:import</code> and/or <code>xsl:include</code> elements, then a custom {@link URIResolver} implementation
 * capable of loading resources from classpath must be set on constructor-injected {@link TransformerFactory}. Also, if
 * any of stylesheets load files using <code>document()</code>function, the custome {@link URIResolver} must also be set
 * on the {@link javax.xml.transform.Transformer} object created. This dictated by <code>javax.xml.transform</code> API
 * peculiar design!</li>
 * </ul>
 *
 * @see XsltConverterConfiguration
 * @see ExceptionThrowingErrorListener
 *
 * @author Ikasan Development Team
 */
public class XsltConverter<SOURCE, TARGET> implements Converter<SOURCE, TARGET>,
        ConfiguredResource<XsltConverterConfiguration>, ManagedResource
{

    /**
     * Classpath URL prefix <code>classpath:</code> expected in stylesheet locations to be picked up off of classpath
     */
    private final static String CLASSPATH_URL_PREFIX = "classpath:";

    /** Configuration of resource in this component */
    private XsltConverterConfiguration configuration;

    /** Unique id for configured resource in this component */
    private String configuredResourceId;

    /** Reader class used to consume incoming content */
    private XMLReader xmlReader;

    /** XSLTC templates, if we are using translets */
    private Templates templates;

    /** <code>TransformerFactory</code> instance for creating {@link javax.xml.transform.Transformer} */
    private final TransformerFactory transformerFactory;

    /**
     * Any transformation parameters that do not change on a per transformation/payload basis This can be configured and
     * set once up front.
     */
    private Map<String, String> transformationParameters;

    /** A custom implementation of URIResolver */
    private URIResolver uriResolver;

    /** Additional Java resources to be made available to the transformer at transform time */
    private Map<String, Object> externalResources;

    /**
     * A very sensitive ErrorListener that will throw errors. This replaces the default ErrorListener that simply logs
     * all sorts of things that should really cause a failure
     */
    private ErrorListener errorListener = new ExceptionThrowingErrorListener();

    /**
     * The target which to send out
     */
    private TargetCreator<SOURCE, TARGET> targetCreator;

    private Converter<Object, String> xmlExtractor;

    private Converter<Object, Map<String, String>> parameterExtractor;

    /**
     * Constructor
     *
     * @param transformerFactory - Transformer Factory to use
     *
     */
    public XsltConverter(final TransformerFactory transformerFactory)
    {
        this.transformerFactory = transformerFactory;
        if (this.transformerFactory == null)
        {
            throw new IllegalArgumentException("The TransformerFactory cannot be null.");
        }
        this.transformerFactory.setErrorListener(this.errorListener);
    }

    /**
     * Override the default {@link URIResolver} provided by transformer library.
     *
     * @param resolver custom {@link URIResolver} implementation
     */
    public void setURIResolver(URIResolver resolver)
    {
        this.uriResolver = resolver;
    }

    /**
     * Accessor
     *
     * @return the xmlReader
     */
    public XMLReader getXmlReader()
    {
        return this.xmlReader;
    }

    /**
     * Mutator
     *
     * @param xmlReader the xmlReader to set
     */
    public void setXmlReader(XMLReader xmlReader)
    {
        this.xmlReader = xmlReader;
    }

    /**
     * Accessor
     *
     * @return the transformationParameters
     */
    public Map<String, String> getTransformationParameters()
    {
        return this.transformationParameters;
    }

    /**
     * Mutator
     *
     * @param transformationParameters the transformationParameters to set
     */
    public void setTransformationParameters(Map<String, String> transformationParameters)
    {
        this.transformationParameters = transformationParameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.configuration.ConfiguredResource#setConfiguration(java.lang.Object)
     */
    @Override
    public void setConfiguration(XsltConverterConfiguration configuration)
    {
        this.configuration = configuration;
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
     * Configure {@link javax.xml.transform.Transformer} instance created with any extra parameters and/or optional
     * custom attributes, e.g.: URIResolver, ErrorListener ..etc.
     *
     * @param transformer Transformer to be configured
     */
    private void configureTransformer(javax.xml.transform.Transformer transformer)
    {
        // Set our custom error listener to ensure errors aren't ignored
        transformer.setErrorListener(this.errorListener);
        // Set any other parameters on the transformer
        this.addTransformationParameters(transformer, this.transformationParameters);
        if (this.uriResolver != null)
        {
            transformer.setURIResolver(this.uriResolver);
        }
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
     * @param source The payload to be transformed
     * @throws TransformerException if an error occurs while transforming.
     */
    @Override
    public TARGET convert(SOURCE source) throws TransformationException
    {
        try
        {
            ThreadLocalBeansWrapper.setBeans(this.externalResources);
            Source sourceXml = this.createSourceXml(extractXml(source));
            Map<String, String> sourceParameters = extractSourceParameters(source);
            // The output xml
            ByteArrayOutputStream transformedDataStream = new ByteArrayOutputStream();
            // Get a new Transformer
            javax.xml.transform.Transformer transformer;
            try
            {
                transformer = this.createNewConfiguredTransformer();
                // add transformation parameters set against the source
                addTransformationParameters(transformer, sourceParameters);
                // Transform away...
                transformer.transform(sourceXml, new StreamResult(transformedDataStream));
            }
            catch (TransformerException e)
            {
                throw new TransformationException(e);
            }
            // Set the transformed data back onto the Payload
            String transformedData = transformedDataStream.toString();
            return createTarget(source, transformedData);
        }
        finally
        {
            ThreadLocalBeansWrapper.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private TARGET createTarget(SOURCE src, String transformedData)
    {
        if (targetCreator == null)
        {
            return (TARGET) transformedData;
        }
        return targetCreator.createTarget(src, transformedData);
    }

    private Map<String, String> extractSourceParameters(SOURCE source)
    {
        if (parameterExtractor != null)
        {
            return parameterExtractor.convert(source);
        }
        return null;
    }

    private String extractXml(SOURCE xml)
    {
        if (xmlExtractor == null && xml instanceof String)
        {
            return (String) xml;
        }
        else
        {
            return xmlExtractor.convert(xml);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.flow.ManagedResource#stopManagedResource()
     */
    @Override
    public void stopManagedResource()
    {
        // Get rid of existing Templates if any
        this.templates = null;
    }

    /**
     * Extract a payload's content into a XML source
     *
     * @param xml a payload whose content is valid xml
     * @return input xml as {@link SAXSource}
     */
    protected Source createSourceXml(final String xml)
    {
        Reader stringReader = new StringReader(xml);
        InputSource inputSource = new InputSource(stringReader);
        // Setup the saxSource using our xmlReader if we have one
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

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.flow.ManagedResource#startManagedResource()
     */
    @Override
    public void startManagedResource()
    {
        /*
         * Given available runtime configurations in XsltConfiguration, we can only manage the creation of templates at
         * the moment.
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

    /**
     * Utility method to remove the <code>classpath:</code> prefix from injected stylesheet location.
     *
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

    @Override
    public XsltConverterConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean arg0)
    {
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager arg0)
    {
    }


    public void setXmlExtractor(Converter<Object, String> xmlExtractor)
    {
        this.xmlExtractor = xmlExtractor;
    }

    /**
     * Accessor
     *
     * @return the errorListener
     */
    public ErrorListener getErrorListener()
    {
        return this.errorListener;
    }

    /**
     * Mutator
     *
     * @param errorListener the errorListener to set
     */
    public void setErrorListener(ErrorListener errorListener)
    {
        this.errorListener = errorListener;
    }

    public Map<String, Object> getExternalResources()
    {
        return externalResources;
    }

    public void setExternalResources(Map<String, Object> externalResources)
    {
        this.externalResources = externalResources;
    }

    public void setParameterExtractor(Converter<Object, Map<String, String>> parameterExtractor)
    {
        this.parameterExtractor = parameterExtractor;
    }

    public TargetCreator<SOURCE, TARGET> getTargetCreator()
    {
        return targetCreator;
    }

    public void setTargetCreator(TargetCreator<SOURCE, TARGET> targetCreator)
    {
        this.targetCreator = targetCreator;
    }
}
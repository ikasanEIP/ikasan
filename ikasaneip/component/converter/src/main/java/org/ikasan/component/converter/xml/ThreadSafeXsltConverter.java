package org.ikasan.component.converter.xml;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.xml.sax.XMLReader;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 /** This class is a Thread Safe XSLT Transformer component that acts on all an <code>Event</code>'s <code>Payload</code>s,
 * transforming them using the supplied style sheet.
 *
 * This implementation is notable for the following reasons:
 *
 * <ol>
 * <li>This implementaiton is threadsafe! It create a converter per thread.It also creates a {@link javax.xml.transform.Transformer} as a new
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
public class ThreadSafeXsltConverter<SOURCE, TARGET> implements Converter<SOURCE, TARGET>,
        ConfiguredResource<XsltConverterConfiguration>, ManagedResource
{
    /** Configuration of resource in this component */
    private XsltConverterConfiguration configuration;

    /** Unique id for configured resource in this component */
    private String configuredResourceId;

    /** <code>TransformerFactory</code> instance for creating {@link javax.xml.transform.Transformer} */
    private final TransformerFactory transformerFactory;

    /** Reader class used to consume incoming content */
    private XMLReader xmlReader = null;

    /**
     * A very sensitive ErrorListener that will throw errors. This replaces the default ErrorListener that simply logs
     * all sorts of things that should really cause a failure
     */
    private ErrorListener errorListener = null;

    /** Additional Java resources to be made available to the transformer at transform time */
    private Map<String, Object> externalResources;

    private ManagedResourceRecoveryManager managedResourceRecoveryManager = null;

    /** A custom implementation of URIResolver */
    private URIResolver uriResolver = null;

    private volatile static int COUNTER = 0;


    /**
     * Any transformation parameters that do not change on a per transformation/payload basis This can be configured and
     * set once up front.
     */
    private Map<String, String> transformationParameters;

    /**
     * The target which to send out
     */
    private Converter<XsltConverterConfiguration, Map<String, String>> configurationParameterConverter;

    private TargetCreator<SOURCE, TARGET> targetCreator;

    private Converter<Object, String> xmlExtractor;

    private Converter<Object, Map<String, String>> parameterExtractor;



    /**
     * A map of converters that are keyed on context. In this case the context is the thread id.
     */
    private Map<Long, XsltConverter> converters;

    /**
     * Constructor
     *
     * @param transformerFactory
     */
    public ThreadSafeXsltConverter(TransformerFactory transformerFactory)
    {
        this.transformerFactory = transformerFactory;

        this.converters = new ConcurrentHashMap<Long, XsltConverter>();
    }

    @Override
    public TARGET convert(SOURCE s) throws TransformationException
    {
        XsltConverter<SOURCE, TARGET> converter = this.converters.get(Thread.currentThread().getId());

        if(converter == null)
        {
            converter = this.createNewXsltConverter();

            this.converters.put(Thread.currentThread().getId(), converter);
        }

        return converter.convert(s);
    }

    protected XsltConverter<SOURCE, TARGET> createNewXsltConverter()
    {
        XsltConverter converter = null;

        try
        {
            converter = new XsltConverter(transformerFactory);
            converter.setURIResolver(this.uriResolver);

            if (this.errorListener != null)
            {
                converter.setErrorListener(this.errorListener);
            }

            converter.setConfiguration(this.configuration);
            converter.setTransformationParameters(this.transformationParameters);
            converter.setManagedResourceRecoveryManager(this.managedResourceRecoveryManager);
            converter.setConfigurationParameterConverter(this.configurationParameterConverter);
            converter.setTargetCreator(this.targetCreator);
            converter.setXmlExtractor(this.xmlExtractor);

            converter.setParameterExtractor(this.parameterExtractor);
            converter.setExternalResources(this.externalResources);

            converter.startManagedResource();
        }
        catch (Exception e)
        {
            throw new TransformationException("Failed to create new ThreadSafeXsltConverter", e);
        }

        return converter;
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
    public XsltConverterConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(XsltConverterConfiguration xsltConverterConfiguration)
    {
        this.configuration = xsltConverterConfiguration;
    }

    @Override
    public void startManagedResource()
    {
        this.converters = new HashMap<Long, XsltConverter>();

        // Let's do this so we can pick up any converter
        // creation issues at startup. It will be discarded.
        this.createNewXsltConverter();
    }

    @Override
    public void stopManagedResource()
    {
        if(this.converters != null)
        {
            for (XsltConverter converter : this.converters.values())
            {
                converter.stopManagedResource();
                converter = null;
            }
        }
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean b)
    {

    }

    public Map<String, String> getTransformationParameters()
    {
        return transformationParameters;
    }

    public void setTransformationParameters(Map<String, String> transformationParameters)
    {
        this.transformationParameters = transformationParameters;
    }

    public XMLReader getXmlReader()
    {
        return xmlReader;
    }

    public void setXmlReader(XMLReader xmlReader)
    {
        this.xmlReader = xmlReader;
    }

    public ErrorListener getErrorListener()
    {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener)
    {
        this.errorListener = errorListener;
    }

    public URIResolver getURIResolver()
    {
        return uriResolver;
    }

    public void setURIResolver(URIResolver uriResolver)
    {
        this.uriResolver = uriResolver;
    }

    public Converter<XsltConverterConfiguration, Map<String, String>> getConfigurationParameterConverter()
    {
        return configurationParameterConverter;
    }

    public void setConfigurationParameterConverter(Converter<XsltConverterConfiguration, Map<String, String>> configurationParameterConverter)
    {
        this.configurationParameterConverter = configurationParameterConverter;
    }

    public Map<String, Object> getExternalResources()
    {
        return externalResources;
    }

    public void setExternalResources(Map<String, Object> externalResources)
    {
        this.externalResources = externalResources;
    }

    public void setXmlExtractor(Converter<Object, String> xmlExtractor)
    {
        this.xmlExtractor = xmlExtractor;
    }

    public void setParameterExtractor(Converter<Object, Map<String, String>> parameterExtractor) 
    {
        this.parameterExtractor = parameterExtractor;
    }
}

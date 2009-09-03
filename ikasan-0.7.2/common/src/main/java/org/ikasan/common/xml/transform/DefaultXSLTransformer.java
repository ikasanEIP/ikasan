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
package org.ikasan.common.xml.transform;

// Imported java classes
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

// Imported trax classes
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Imported w3c classes
import org.w3c.dom.Document;

// Imported apache commons classes
import org.apache.commons.io.FileUtils;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.ikasan.common.CommonXSLTransformer;

/**
 * This class wraps TrAX API for ease of transformation.
 * It also provides XSLTC implementation of transformation.
 * <p>
 * XSLTC is typically used for
 * one-compilation-multiple-concurrent-transformations.
 * Once a new <code>DefaultXSLTransformer</code> instance is created,
 * a given object (holding stylesheet) will be compiled into a translet
 * (i.e. <code>javax.xml.transform.Templates</code> that contains a set of Java
 * class(es) and the translet be stored into an internal static hash table.
 * This enables us to create new <code>javax.xml.transform.Transformer</code>
 * instances for multiple transformations.
 * <p>
 * <pre>
 *    Usage Example:
 *
 *    String stylesheet = "/a/b/c/myStylesheet.xsl";
 *
 *    DefaultXSLTransformer xslt = null;
 *    try
 *    {
 *        // Compile the stylesheet into a translet
 *        // and keep it in the internal static table
 *        xslt = new DefaultXSLTransformer(stylesheet);
 *
 *        // Set parameters and perform the transformation
 *        xslt.setURIResolver(uriXMLMaps);
 *        xslt.setParameters(paramMap);
 *        xslt.setParameter("name1", "value1");
 *        String xmlStringOut = xslt.transformToString(xmlDOMIn);
 *    }
 *    catch (Exception e)
 *    {
 *        e.printStackTrace();
 *    }
 * </pre>
 *
 * @author Ikasan Development Team
 *
 */
public class DefaultXSLTransformer
    implements CommonXSLTransformer
{
    /**
     * The logger instance.
     */
    private static Logger logger =
        Logger.getLogger(DefaultXSLTransformer.class);

    /**
     * The quite handy debugging flag to get more information
     * about XSLT processing.
     */
    public static String XSLT_DEBUG = "ikasan.xslt.debug";

    /**
     * The TransformerFactory system property key to generate a translator.
     */
    protected static final String TX_FACTORY_KEY
        = "javax.xml.transform.TransformerFactory";

    /**
     * The TransformerFactory system property value to generate a translator.
     */
    protected static final String TX_FACTORY_VALUE
        = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";

    /**
     * The mapping table that maps stylesheet names
     * to <code>Templates</code> instances.
     */
    static Hashtable<String, Templates> translets = null;

    /**
     * This flag indicating whether this instance has been initialized.
     */
    static boolean isInitialized = false;

    /**
     * The default file encoding.
     */
    private final static String FILE_ENCODING =
        System.getProperty("file.encoding");

    /**
     * The Transformer instance to perform the transformation with.
     * It will be rest after the transformation.
     */
    private Transformer transformer = null;

    /**
     * Creates a new <code>DefaultXSLTransformer</code> instance
     * with the XSLTC flag on. This currently has no stylesheet associated.
     */
    public DefaultXSLTransformer()
    {
        this.init();
    }

    /**
     * Creates a new <code>DefaultXSLTransformer</code> instance
     * with the specified XSL stylesheet and XSLTC flag on.
     *
     * @param xslURI - an XSL stylesheet to apply to.
     *
     * @throws TransformerConfigurationException
     */
    public DefaultXSLTransformer(String xslURI)
        throws TransformerConfigurationException
    {
        this(xslURI, true);
    }

    /**
     * Creates a new <code>DefaultXSLTransformer</code> instance
     * with the specified XSL stylesheet and default trace level.
     *
     * @param xslURI    - an XSL stylesheet to apply to.
     * @param useXSLTC  - the flag indicating whether to use XSLTC.
     *
     * @throws TransformerConfigurationException
     */
    public DefaultXSLTransformer(String xslURI, Boolean useXSLTC)
        throws TransformerConfigurationException
    {
        this.init();
        this.registerTemplates(xslURI, this.createSource(xslURI), useXSLTC.booleanValue());
    }

    /**
     * Creates a new <code>DefaultXSLTransformer</code> instance
     * with the specified source holding XSL stylesheet and XSLTC flag on.
     *
     * @param name      - the unique name being mapped to this source instance
     *                    holding XSL stylesheet in the internal mapping table.
     * @param xslSource - the <code>Source</code> object that holds
     *                    an XSL stylesheet URI, input stream, etc.
     *
     * @throws TransformerConfigurationException
     */
    public DefaultXSLTransformer(String name, Source xslSource)
        throws TransformerConfigurationException
    {
        this(name, xslSource, true);
    }

    /**
     * Creates a new <code>DefaultXSLTransformer</code> instance
     * with the specified source holding XSL stylesheet
     * and default trace level.
     *
     * @param name      - the unique name being mapped to this source instance
     *                    holding XSL stylesheet in the internal mapping table.
     * @param xslSource - the <code>Source</code> object that holds
     *                    an XSL stylesheet URI, input stream, etc.
     * @param useXSLTC  - the flag indicating whether to use XSLTC.
     *
     * @throws TransformerConfigurationException
     */
    public DefaultXSLTransformer(String name, Source xslSource, boolean useXSLTC)
        throws TransformerConfigurationException
    {
        this.init();
        this.registerTemplates(name, xslSource, useXSLTC);
    }

    /**
     * Specifies the stylesheet URL to apply. By default this method will
     * compile the stylesheet to a translet.
     * @param xslURI 
     * @throws TransformerConfigurationException 
     */
    public void setStylesheet(String xslURI)
        throws TransformerConfigurationException
    {
        this.init();
        this.registerTemplates(xslURI, this.createSource(xslURI), true);
    }

    /**
     * Specifies the stylesheet URL to apply and also whether the stylesheet
     * should be compiled to a translet.
     * @param xslURI 
     * @param useXSLTC 
     * @throws TransformerConfigurationException 
     */
    public void setStylesheet(String xslURI, boolean useXSLTC)
        throws TransformerConfigurationException
    {
        this.init();
        this.registerTemplates(xslURI, this.createSource(xslURI), useXSLTC);
    }

    /**
     * Initializes the internal structures.
     */
    private synchronized void init()
    {
        // Only do this process once
        if (isInitialized == true)
        {
            return;
        }

        // Initialize the hash table
        logger.debug("Initializing translet mapping table...");
        translets = new Hashtable<String, Templates>();

        isInitialized = true;
    }

    /**
     * Creates and returns a new instance of <code>StreamSource</code>
     * based on the specified stylesheet.
     *
     * @param xslURI is the XSL stylesheet to apply to.
     * @return Source
     */
    private Source createSource(String xslURI)
    {
        if (xslURI == null)
            throw new NullPointerException("XSL stylesheet URI can't be null");

        Source xslSource = new StreamSource(xslURI);

        // If we don't do this, the transformer won't know how to
        // resolve relative URLs in the stylesheet.
        xslSource.setSystemId(xslURI);

        return xslSource;
    }

    /**
     * Compiles the specified XSL stylesheet into translets and
     * registers them using the internal mapping table.
     *
     * @param name      - the unique name being mapped to the source instance
     *                     holding XSL stylesheet in the internal
     *                     mapping table.
     * @param xslSource - the source object that holds an XSL stylesheet URI,
     *                     input stream, etc.
     * @param useXSLTC  - the flag indicating whether to use XSLTC.
     * @throws TransformerConfigurationException 
     */
    private synchronized void registerTemplates(String name, Source xslSource,
                                                boolean useXSLTC)
        throws TransformerConfigurationException
    {
        if (name == null)
            throw new NullPointerException(
                "Translet reference name can't be null");

        if (name.trim().length() == 0)
            throw new IllegalArgumentException(
                "Translet reference name can't be empty");

        if (xslSource == null)
            throw new NullPointerException(
                "XSL stylesheet source can't be null");

        if (useXSLTC == true)
        {
            // Try to get the translet instance associated with
            // the stylesheet name from the translet mapping table
            logger.debug("Trying to get translet for [" + name + "].");
            Templates translet = translets.get(name);

            // First time, compile the stylesheet to create a new translet
            // Add it to the internal mapping table for the later use
            if (translet == null)
            {
                logger.debug("First time, compiling stylesheet...");
                logger.debug("XSL stylesheet URI =[" + xslSource.getSystemId() +
                            "].");

                translet = this.newTemplates(xslSource);
                translets.put(new String(name), translet);
                logger.debug("Name:[" + name + "] => XSLTC:[" + translet +
                            "].");
            }
            else
            {
                // Log it to ensure we are using the same translet
                logger.debug("Name:[" + name + "] => XSLTC:[" + translet
                               + "].");
            }

            // Create a new transformation context for this translet object
            // Set it as current Transformer instance
            this.transformer = translet.newTransformer();
        }
        else
        {
            // Get a new TransformerFactory instance
            TransformerFactory tFactory = this.newTransformerFactory();
            this.transformer = tFactory.newTransformer(xslSource);
        }

        // Also clear all parameters just in case
        logger.debug("Got a new Transformer.");
        this.transformer.clearParameters();
    }

    /**
     * Creates a new <code>Templates</code> instance based on
     * the specified stylesheet.
     *
     * @param xslSource - the source object that holds an XSL stylesheet URI,
     *                     input stream, etc.
     * @return Templates
     * @throws TransformerConfigurationException
     */
    private Templates newTemplates(Source xslSource)
        throws TransformerConfigurationException
    {
        // Set the TransformerFactory system property to utilise translets
        // For more flexibility, load properties from a properties file
        // This will do the job for the time being
        logger.debug("Current TransformerFactory =["
                   + System.getProperty(TX_FACTORY_KEY) + "].");

        logger.debug("Setting a new TransformerFactory [" + TX_FACTORY_VALUE
                    + "].");
        Properties props = System.getProperties();
        props.put(TX_FACTORY_KEY, TX_FACTORY_VALUE);
        System.setProperties(props);
        logger.debug("New TransformerFactory =["
                   + System.getProperty(TX_FACTORY_KEY) + "].");

        // Get a new TransformerFactory instance
        TransformerFactory tFactory = this.newTransformerFactory();

        // Create a new translet as a Templates object
        return tFactory.newTemplates(xslSource);
    }

    /**
     * Creates a new <code>TransformerFactory</code> instance,
     * sets the default error listener, sets debugging flag on if necessary
     * and finally returns the instance.
     * 
     * @return new TransformerFactory instance
     */
    private TransformerFactory newTransformerFactory()
    {
        // Get a new TransformerFactory instance
        TransformerFactory tFactory = TransformerFactory.newInstance();

        //logger.debug("Setting auto-translet to true...");
        //tFactory.setAttribute("auto-translet", Boolean.TRUE);
        if (System.getProperty(XSLT_DEBUG, "false").equalsIgnoreCase("true"))
            tFactory.setAttribute("debug", Boolean.TRUE);
        logger.debug("Setting error event listener to ErrorListener...");
        tFactory.setErrorListener(new DefaultErrorListener());

        return tFactory;
    }

    /**
     * Sets the error event listener in effect for the transformation.
     *
     * @param listener - a new error listener.
     */
    public void setErrorListener(ErrorListener listener)
    {
        // No need to scream as we've already got ErrorListener
        if (listener == null) return;

        this.transformer.setErrorListener(listener);
    }

    /**
     * Sets an object that will be used to resolve URIs used in
     * a stylesheet later using <code>xsl:include</code> or
     * <code>xsl:import</code> or <code>document()</code> function.
     * This method is called from <code>transform()</code> method.
     *
     * @param resolver - an object that implements the <code>URIResolver</code>
     *                   interface or null.
     */
    // This method is to conform to the way javax.xml.transform.Transformer
    // sets URIResolver
    public void setURIResolver(URIResolver resolver)
    {
        this.transformer.setURIResolver(resolver);
    }

    /**
     * Creates a new <code>URIResolver</code> instance
     * (if not already created) that maps each URI to its corresponding
     * XML document.
     * This allows us to access any xml documents loaded in memory
     * by referring to corresponding URIs in a stylesheet later
     * using <code>xsl:include</code> or <code>xsl:import</code> or
     * <code>document()</code> function.
     * This method is called from <code>transform()</code> method.
     *
     * @param resolverMap - the mapping table containing a set of
     *                      URI-to-XML mappings.
     */
    public void setURIResolver(Map<String, Source> resolverMap)
    {
        if (resolverMap == null || resolverMap.isEmpty()) return;

        // Operation error, possibly setURIResolver() method has been called
        // with different implementation of URIResolver
        URIResolver resolver = this.transformer.getURIResolver();
        if ((resolver != null) && !(resolver instanceof DefaultURIResolver))
        {
            throw new IllegalArgumentException("Operation error! " +
                "The following URIResolver instance has been already set to " +
                "this transformer '" + resolver.getClass().getName() + "'." +
                "Ensure to call either setURIResolver(URIResolver) or " +
                "setURIResolver(Map)/setURIResolver(String, Source) method");
        }

        DefaultURIResolver uriResolver = (resolver == null) ?
            new DefaultURIResolver() :
            (DefaultURIResolver)this.transformer.getURIResolver();

        // Grab each URI and its corresponding source object
        // (that is representing XML document)
        // then register the mapping through URIResolver
        String uri = null; Source xmlSource = null;
        for (Map.Entry<String, Source> entry: resolverMap.entrySet())
        {
            uri = entry.getKey();
            if (uri == null || uri.trim().length() == 0)
            {
                logger.warn("URI is null or empty, skipping...");
                continue;
            }

            xmlSource = entry.getValue();
            if (xmlSource != null)
                uriResolver.mapURIToSource(uri, xmlSource);
            else
                logger.warn("XML source object for URI [" + uri
                             + "] is null, skipping...");
        }

        this.transformer.setURIResolver(uriResolver);
    }

    /**
     * Creates a new <code>URIResolver</code> instance
     * (if not already created) to map the specified URI to
     * the object representing XML document.
     *
     * @param uri - the unique URI name.
     * @param xml - the XML document.
     */
    public void setURIResolver(String uri, Source xml)
    {
        if (uri == null || uri.trim().length() == 0) return;
        if (xml == null) return;

        Map<String, Source> resolverMap = new HashMap<String, Source>(1);
        resolverMap.put(uri, xml);
        this.setURIResolver(resolverMap);
    }

    /**
     * Adds an output property for the transformation.
     * @param name 
     * @param value 
     *
     */
    public void setOutputProperty(String name, String value)
    {
        if (name == null || value == null) return;
        this.transformer.setOutputProperty(name, value);
    }

    /**
     * Sets the output properties for the transformation.
     *
     * @param outputProps - the set of output properties that will be used to
     *                      override any of the same properties in affect
     *                      for the transformation.
     */
    public void setOutputProperties(Properties outputProps)
    {
        if (outputProps == null || outputProps.isEmpty()) return;
        this.transformer.setOutputProperties(outputProps);
    }

    /**
     * Sets a parameter for the transformation.
     * @param name 
     * @param value 
     */
    public void setParameter(String name, String value)
    {
        if (name == null)
            throw new NullPointerException("The parameter name can't be null");
        if (value == null)
            throw new NullPointerException("The parameter value can't be null");

        this.transformer.setParameter(name, value);
    }

    /**
     * Sets parameters for the transformation.
     *
     * @param parameterMap - parameter mappings.
     */
    public void setParameters(Map<String, String> parameterMap)
    {
        if (parameterMap == null || parameterMap.isEmpty())
        {
            return;
        }

        String name = null, value = null;
        for (Map.Entry<String, String> entry: parameterMap.entrySet())
        {
            name  = entry.getKey();
            value = entry.getValue();
            if (name == null || name.length() == 0)
            {
                logger.warn("Parameter name is null or empty, skipping...");
                continue;
            }
            if (value != null)
            {
                this.setParameter(name, value);
            }
            else
            {
                logger.warn("Parameter value is null, skipping...");
            }
        }
    }

    /**
     * Performs the transformation.
     *
     * @param in  - the XML document.
     * @param out - the transformation result.
     * @throws TransformerException 
     */
    public void transform(Source in, Result out)
        throws TransformerException
    {
        if (in == null)
            throw new NullPointerException("XML source can't be null");
        if (out == null)
            throw new NullPointerException("Output target can't be null");

        // Perform the transformation from the source XML to the Result
        logger.debug("Transforming...");
        this.transformer.transform(in, out);
        logger.debug("Transformation completed.");

        // Reset the Transformer instance
        // for the next transformation
        
        // JM - 21/7/08
        // commented out as this doesn't allow re-use of the transformer instance
        // this.transformer = null;
    }

    /**
     * Performs the transformation to the output result as String.
     *
     * @param in - the XML document.
     * @return String
     * @throws TransformerException 
     * @throws IOException 
     */
    public String transformToString(Source in)
        throws TransformerException, IOException
    {
        // Create a Writer instance to hold transformation result
        StringWriter writer = new StringWriter();
        StreamResult out = new StreamResult(writer);

        // Transformation
        this.transform(in, out);

        // Flush and close the stream
        writer.flush();
        writer.close();

        // Here is the newly transformed string
        return new String(writer.getBuffer().toString());
    }

    /**
     * Given the DOM object, performs the transformation
     * and returns the output result as String.
     *
     * @param xmlIn - the XML document as DOM object.
     * @return String
     * @throws TransformerException 
     * @throws IOException 
     */
    public String transformToString(Document xmlIn)
        throws TransformerException, IOException
    {
        Source in = new DOMSource(xmlIn);
        return this.transformToString(in);
    }

    /**
     * Given the XML string, performs the transformation
     * and returns the output result as String.
     *
     * @param xmlIn - the XML document as <code>String</code> object.
     * @return String
     * @throws TransformerException 
     * @throws IOException 
     */
    public String transformToString(String xmlIn)
        throws TransformerException, IOException
    {
        Source in = new StreamSource(new StringReader(xmlIn));
        return this.transformToString(in);
    }

    /**
     * Performs the transformation to the output result as Document.
     *
     * @param in - the XML document as <code>Source</code>.
     * @return String
     * @throws TransformerException 
     * @throws IOException 
     */
    public Document transformToDocument(Source in)
        throws TransformerException, IOException
    {
        // Create a Writer instance to hold transformation result
        DOMResult out = new DOMResult();

        // Transformation
        this.transform(in, out);

        // Here is the newly transformed document
        return (Document)out.getNode();
    }

    /**
     * Given the DOM object, performs the transformation
     * and returns the output result as Document.
     *
     * @param xmlIn - the XML document as DOM object.
     * @return String
     * @throws TransformerException 
     * @throws IOException 
     */
    public Document transformToDocument(Document xmlIn)
        throws TransformerException, IOException
    {
        Source in = new DOMSource(xmlIn);
        return this.transformToDocument(in);
    }

    /**
     * Given the XML string, performs the transformation
     * and returns the output result as Document.
     *
     * @param xmlIn - the XML document as <code>String</code> object.
     * @return String
     * @throws TransformerException 
     * @throws IOException 
     */
    public Document transformToDocument(String xmlIn)
        throws TransformerException, IOException
    {
        Source in = new StreamSource(new StringReader(xmlIn));
        return this.transformToDocument(in);
    }

// main() method
///////////////////////////////////////////////////////////////////////////////

    /**
     * Runs this class for testing.
     * TODO Unit Test
     * @param args 
     */
    public static void main(String args[])
    {
        String dummyXML = "<?xml version='1.0'?><doc/>";
        String xmlInURI  = null;
        String xslInURI  = null;
        String xmlOutURI = null;

        Map<String, String> params = new HashMap<String, String>();
        Map<String, InputStream> uriResolverMap = new HashMap<String, InputStream>();
        InputStream xmlIn = new ByteArrayInputStream(
            "<?xml version='1.0'?><AAA><BBB id='b1'/></AAA>".getBytes());
        uriResolverMap.put("ref.xml", xmlIn);

        boolean useXSLTC   = true;
        boolean indent     = false;
        int numOfXforms    = 1;
        int maxNumOfXforms = 9999;
        boolean diffOutXml = false;

        // Parse the command-line parameters
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equalsIgnoreCase("-help"))
            {
                usage();
                System.exit(1);
            }
            // Input XML URI
            else if (args[i].equalsIgnoreCase("-in"))
            {
                xmlInURI = args[++i].trim();
            }
            // Input XSL URI
            else if (args[i].equalsIgnoreCase("-xsl"))
            {
                xslInURI = args[++i].trim();
            }
            // Output file name
            else if (args[i].equalsIgnoreCase("-out"))
            {
                xmlOutURI = args[++i].trim();
            }
            // Parameters to stylesheet
            else if (args[i].equalsIgnoreCase("-param"))
            {
                params.put(new String(args[++i].trim()),
                           new String(args[++i].trim()));
            }
            // Parameters to stylesheet
            // Its parameter value is read from the specified file
            else if (args[i].equalsIgnoreCase("-fileParam"))
            {
                String name  = new String(args[++i].trim());
                String filename = new String(args[++i].trim());
                try
                {
                    String fileContentStr =
                        FileUtils.readFileToString(new File(filename),
                                                   FILE_ENCODING);
                    params.put(name, fileContentStr);
                }
                catch (Exception e)
                {
                    System.err.println("Error while reading file ["
                                     + filename + "], skipping...");
                    e.printStackTrace(System.err);
                }
            }
            // URI resolver mappings used to get in-memory XML from stylesheet
            // via document() function
            // Set URI name and XML filename
            else if (args[i].equalsIgnoreCase("-uriResolver"))
            {
                String uri  = new String(args[++i].trim());
                String filename = new String(args[++i].trim());
                try
                {
                    String fileContentStr =
                        FileUtils.readFileToString(new File(filename),
                                                   FILE_ENCODING);
                    InputStream is =
                        new ByteArrayInputStream(fileContentStr.getBytes());
                    uriResolverMap.put(uri, is);
                }
                catch (Exception e)
                {
                    System.err.println("Error while reading file ["
                                     + filename + "], skipping...");
                    e.printStackTrace(System.err);
                }
            }
            // Use XSLTC?
            else if (args[i].equalsIgnoreCase("-usexsltc"))
            {
                String value = args[++i].trim();
                useXSLTC = (value.equalsIgnoreCase("true")) ? true : false;
            }
            // Generate output XML with indentation
            else if (args[i].equalsIgnoreCase("-indent"))
            {
                indent = true;
            }
            // A number of loop to generate output XML files
            else if (args[i].equalsIgnoreCase("-loop"))
            {
                try
                {
                    numOfXforms = Integer.parseInt(args[++i].trim());
                    if (numOfXforms > maxNumOfXforms)
                        numOfXforms = maxNumOfXforms;
                }
                catch (Exception e)
                {
                    numOfXforms = 1;
                }
            }
            // Generate different output XML files
            // when a number of loop > 0
            else if (args[i].equalsIgnoreCase("-diff"))
            {
                diffOutXml = true;
            }
            // Set XSLT debuging flag on
            else if (args[i].equalsIgnoreCase("-debug"))
            {
                System.setProperty(XSLT_DEBUG, "true");
            }
            // Don't know what to do
            else
            {
                System.err.println("Invalid option - [" + args[i] + "].");
                usage();
                System.exit(1);
            }
        }

        System.out.println("");
        System.out.println("XML input  file   =[" + xmlInURI + "].");
        System.out.println("XSL input  file   =[" + xslInURI + "].");
        System.out.println("XML output file   =[" + xmlOutURI + "].");
        System.out.println("XSL parameters    =[" + params + "].");
        System.out.println("Use XSLTC         =[" + useXSLTC + "].");
        System.out.println("XML output indent =[" + indent + "].");
        System.out.println("No. of transforms =[" + numOfXforms + "].");
        System.out.println("Diff output XML   =[" + diffOutXml + "].");
        System.out.println("");

        boolean isXSLInURI  = (xslInURI != null  && xslInURI.length() > 0);
        boolean isXMLInURI  = (xmlInURI != null  && xmlInURI.length() > 0);
        boolean isXmlOutURI = (xmlOutURI != null && xmlOutURI.length() > 0);
        String xmlOutURIPrefix = (isXmlOutURI && xmlOutURI != null && xmlOutURI.indexOf('.') > 0)
            ? xmlOutURI.substring(0, xmlOutURI.indexOf('.')) : xmlOutURI;
        String xmlOutURISuffix = (isXmlOutURI && xmlOutURI != null && xmlOutURI.indexOf('.') > 0)
            ?  xmlOutURI.substring(xmlOutURI.indexOf('.')) : "";
        try
        {
            // Use connection pool instance to query database
            // via the specified stylesheet
            int counter = 0;
            while (counter++ < numOfXforms)
            {
                // Create a new DefaultXSLTransformer instance
                // No matter how many times we create the instance
                // using the same stylesheet, no harm at all
                DefaultXSLTransformer xslt = null;
                if (isXSLInURI)
                    xslt = new DefaultXSLTransformer(xslInURI, useXSLTC);
                else
                    xslt = new DefaultXSLTransformer("default.xsl",
                        new StreamSource(
                            new StringReader(defaultXSLStr.toString())),
                                useXSLTC);

                // Set URIResolver for the transformation
                // e.g. 1 String
                //InputStream xmlIn = new ByteArrayInputStream(
                //    "<?xml version='1.0'?><AAA><BBB id='b1'/></AAA>".getBytes());
                //xslt.setURIResolver("ref.xml", new StreamSource(xmlIn));
                // e.g. 2 File
                // xslt.setURIResolver("ref.xml",
                //                     new StreamSource(new File("test.xml")));
                // e.g. 3 InputSource
                // xslt.setURIResolver("ref.xml", new InputSource("test.xml"));
                for (Map.Entry<String, InputStream> entry: uriResolverMap.entrySet())
                {
                    String uri = entry.getKey();
                    InputStream is = entry.getValue();
                    xslt.setURIResolver(uri, new StreamSource(is));
                }

                // Set output properties for the transformation
                if (indent)
                {
                    xslt.setOutputProperty(
                        javax.xml.transform.OutputKeys.INDENT, "yes");
                    xslt.setOutputProperty(
                        "{http://xml.apache.org/xalan}:indent-amount", "2");
                }

                // Set parameters for the transformation if any
                xslt.setParameter("param1", "data" + counter);
                xslt.setParameters(params);

                if (isXmlOutURI && diffOutXml && numOfXforms > 1)
                    xmlOutURI = xmlOutURIPrefix
                              + intToString(counter, maxNumOfXforms)
                              + xmlOutURISuffix;

                StreamSource xmlSource = (isXMLInURI)
                    ? new StreamSource(xmlInURI)
                    : new StreamSource(new StringReader(dummyXML));

                StreamResult xmlResult = (isXmlOutURI)
                                       ? new StreamResult(xmlOutURI)
                                       : new StreamResult(System.out);

                System.out.println("\n[" + counter + "] Transforming...");
                xslt.transform(xmlSource, xmlResult);

                if (xmlOutURI != null && xmlOutURI.length() > 0)
                    System.out.println("Generated [" + xmlOutURI + "].");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }

        System.exit(0);
    }

    /**
     * Turns the specified integer into a <code>String</code>
     * in <code>dddd</code> format, where a number of <code>d</code>s varies
     * based on the number of digits passed in,
     * for example, intToString(1, 4) returns '0001'.
     * 
     * @param integer 
     * @param numOfDigits 
     * @return String version of the integer  
     */
    private static String intToString(int integer, int numOfDigits)
    {
        int intLength = String.valueOf(integer).length();
        int maxLength = String.valueOf(numOfDigits).length();
        String intStr = new String("");
        for (int i = intLength; i < maxLength; i++)
            intStr += "0";
        return (intStr + String.valueOf(integer));
    }

    /**
     * Displays the usage for main() method.
     *
     */
    private static void usage()
    {
        String fqClassName = DefaultXSLTransformer.class.getName();
        System.err.println("");
        System.err.println("Usage:");
        System.err.println("java " + fqClassName + " [-options]");
        System.err.println("");
        System.err.println("where options include:");
        System.err.println("    -in <input XML>");
        System.err.println("                   to specify input XML file name (empty XML)");
        System.err.println("    -xsl <input XSL>");
        System.err.println("                   to specify input stylesheet file name");
        System.err.println("                   (See below for default XSL)");
        System.err.println("    -out <output>  to specify output file name (System.out)");
        System.err.println("    -param <name> <value>");
        System.err.println("                   to set a name-value paired parameter");
        System.err.println("                   The pair must be delimited by a space");
        System.err.println("                   Set parameters as many as you can");
        System.err.println("    -fileParam <name> <filename>");
        System.err.println("                   to set a name-value paired parameter");
        System.err.println("                   The pair must be delimited by a space");
        System.err.println("                   The parameter value is read from the specified file location in string format");
        System.err.println("                   Set parameters as many as you can");
        System.err.println("    -uriResolver <uri> <filename>");
        System.err.println("                   to set a URI resolver mapping");
        System.err.println("                   The pair must be delimited by a space");
        System.err.println("                   URI can be anything as long as it is matched to the one in the stylesheet");
        System.err.println("                   Specify an XML document file location in string format");
        System.err.println("                   Set as many URI resolver mappings as you can");
        System.err.println("    -useXSLTC <true|false>");
        System.err.println("                   to use XSLTC - stylesheet compilation (true)");
        System.err.println("    -indent        to generate output XML with indentation (false)");
        System.err.println("    -loop <number> to specify number of times you want to loop");
        System.err.println("                   while applying styleshhet (1)");
        System.err.println("    -diff          if you want to generate different output files (false)");
        System.err.println("                   This is only valid if the loop number > 1");
        System.err.println("    -debug         to set XSLT debugging flag on");
        System.err.println("");
        System.err.println("Note that the following default XSL stylesheet will be used if XSL URI is not specified:-");
        System.err.println(defaultXSLStr.toString());
        System.err.println("");
    }

    /** Default XSL stylesheet used in main() method */
    private static StringBuffer defaultXSLStr = new StringBuffer("");
    
    static
    {
        defaultXSLStr.append("<?xml version='1.0'?>\n");
        defaultXSLStr.append("<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n");
        defaultXSLStr.append("  <xsl:output method='xml' indent='yes'/>\n");
        defaultXSLStr.append("  <xsl:variable name='myDoc' select=\"document('ref.xml')\"/>\n");
        defaultXSLStr.append("  <xsl:template match='/'>\n");
        defaultXSLStr.append("    <xsl:element name='out'>\n");
        defaultXSLStr.append("      <xsl:copy-of select='$myDoc'/>\n");
        defaultXSLStr.append("    </xsl:element>\n");
        defaultXSLStr.append("  </xsl:template>\n");
        defaultXSLStr.append("</xsl:stylesheet>\n");
    }

}

/***********************************************************************************
===============================
 in.xsl
===============================
<?xml version="1.0"?>
<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:xalan="http://xml.apache.org/xalan"
     version="1.0"
     exclude-result-prefixes="xalan">

  <xsl:output method="xml" encoding="UTF-8" indent="yes" xalan:indent-amount="2"/>
  <xsl:param name="param1" select="'data'"/>
<!--
  <xsl:variable name="myDoc" select="document('ref.xml')"/>
-->
  <xsl:template match="/">
    <xsl:element name="out">
      <xsl:element name="tag1">
        <xsl:value-of select="'content1'"/>
      </xsl:element>
      <xsl:element name="tag2">
        <xsl:value-of select="'content2'"/>
      </xsl:element>
      <xsl:element name="tag3">
        <xsl:attribute name="ATTR3">
          <xsl:value-of select="'ATTR3_DATA'"/>
        </xsl:attribute>
        <xsl:value-of select="$param1"/>
      </xsl:element>
<!--
      <xsl:copy-of select="$myDoc"/>
-->
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>

===============================
 test.xml
===============================
<?xml version="1.0" encoding="UTF-8"?>
<AAA>
  <BBB id="b1"/>
  <BBB name="bbb"/>
  <BBB name="bbb"/>
</AAA>
************************************************************************************/

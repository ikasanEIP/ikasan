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
package org.ikasan.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ikasan.common.factory.ClassInstantiationUtils;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.common.security.IkasanSecurityService;
import org.ikasan.common.security.IkasanSecurityServiceImpl;
import org.ikasan.common.security.SecurityNotConfiguredException;
import org.ikasan.common.util.ResourceUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jndi.JndiTemplate;

/**
 * Singleton for loading common implementation classes behind the interfaces.
 * 
 * @author Ikasan Development Team
 */
public class ResourceLoader implements ServiceLocator
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(ResourceLoader.class);

    /** The resource name, e.g. location of resource properties file */
    protected static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /** Resource string */
    protected static String RESOURCE_NAME;

    /** instance of the singleton */
    private static ResourceLoader instance = null;

    // Class keys
    /** The context implementation class key */
    protected static String CONTEXT_IMPL_CLASS = "contextImpl.class"; //$NON-NLS-1$

    /** The persistence implementation class key */
    protected static String PERSISTENCE_IMPL_CLASS = "persistenceImpl.class"; //$NON-NLS-1$

    /** The XML parser implementation class key */
    protected static String XMLPARSER_IMPL_CLASS = "xmlParserImpl.class"; //$NON-NLS-1$

    /** The XML transformer implementation class key */
    protected static String XMLTRANSFORMER_IMPL_CLASS = "xmlTransformerImpl.class"; //$NON-NLS-1$

    /** The string transformer implementation class key */
    protected static String STRINGTRANSFORMER_IMPL_CLASS = "stringTransformerImpl.class"; //$NON-NLS-1$

    /** The XSL transformer implementation class key */
    protected static String XSLTRANSFORMER_IMPL_CLASS = "xslTransformerImpl.class"; //$NON-NLS-1$

    /** The environment implementation class key */
    protected static String ENVIRONMENT_IMPL_CLASS = "environmentImpl.class"; //$NON-NLS-1$

    /** resource properties */
    protected Properties resources;

    // concrete class names
    /** Context Implementation Class */
    protected String contextImplClass;

    /** Persistence Implementation Class */
    protected String persistenceImplClass;

    /** XML Parser Implementation Class */
    protected String xmlParserImplClass;

    /** XML Transformer Implementation Class */
    protected String xmlTransformerImplClass;

    /** String Transformer Implementation Class */
    protected String stringTransformerImplClass;

    /** XSL Parser Implementation Class */
    protected String xslTransformerImplClass;

    /** Environment Implementation Class */
    protected String environmentImplClass;

    // concrete classes
    /** Concrete class for payload */
    protected Class<? extends Payload> payloadClass;


    /** instance of the ikasan platform */
    protected IkasanEnv ikasanEnv = null;

    // Services
    /** instance of the ikasan platform */
    private IkasanSecurityService ikasanSecurityService = null;

    /** A payload Factory */
    private PayloadFactory payloadFactory;

    /** JNDITemplate for accessing JNDI resources from the JMS server */
    private JndiTemplate jmsJndiTemplate;

    /** A commonly used XML parser */
    private CommonXMLParser commonXmlParser;

    /**
     * Singleton constructor
     * 
     * @return ResourceLoader
     */
    public static ResourceLoader getInstance()
    {
        if (instance != null)
        {
            return instance;
        }
        synchronized (ResourceLoader.class)
        {
            if (instance == null)
            {
                ResourceLoader.RESOURCE_NAME = "commonResource.xml";
                instance = new ResourceLoader();
            }
            return instance;
        }
    }

    /**
     * Private constructor
     */
    private ResourceLoader()
    {
        this(RESOURCE_NAME);
    }

    /**
     * Default constructor
     * 
     * @param resourceName The resource name so we can get resource properties
     */
    protected ResourceLoader(final String resourceName)
    {
        try
        {
            this.resources = ResourceUtils.getAsProperties(resourceName);
            this.loadAllResources(this.resources);
            // Get the Ikasan configuration properties and set as system props
            this.ikasanEnv = IkasanEnvImpl.getInstance(this.newEnvironment());
            // Load optional security properties
            if (this.ikasanEnv.getIkasanSecurityResource() != null)
            {
                ikasanSecurityService = new IkasanSecurityServiceImpl(this.ikasanEnv.getIkasanSecurityResource(), this
                    .newEnvironment());
            }
        }
        catch (IOException e)
        {
            String failMsg = "Failed to load [" + resourceName + "]. Nothing will work! ";
            throw new CommonRuntimeException(failMsg, e);
        }
    }

    /**
     * Convenience method to load all resources from a given properties.
     * 
     * @param props Properties for this resource
     */
    public void loadAllResources(Properties props)
    {
        this.setContextImplClass(props.getProperty(CONTEXT_IMPL_CLASS));
        this.setPersistenceImplClass(props.getProperty(PERSISTENCE_IMPL_CLASS));
        this.setXmlParserImplClass(props.getProperty(XMLPARSER_IMPL_CLASS));
        this.setXmlTransformerImplClass(props.getProperty(XMLTRANSFORMER_IMPL_CLASS));
        this.setStringTransformerImplClass(props.getProperty(STRINGTRANSFORMER_IMPL_CLASS));
        this.setXslTransformerImplClass(props.getProperty(XSLTRANSFORMER_IMPL_CLASS));
        this.setEnvironmentImplClass(props.getProperty(ENVIRONMENT_IMPL_CLASS));
    }

    /**
     * Provide the caller with an instance of the Ikasan Security Service.
     * 
     * @return IkasanSecurityService
     * @throws SecurityNotConfiguredException Exception if security is not configured
     */
    public IkasanSecurityService getIkasanSecurityService() throws SecurityNotConfiguredException
    {
        if (ikasanSecurityService == null)
        {
            throw new SecurityNotConfiguredException("Ikasan security service must be configured before invoking "
                    + "security operations. Check your ikasan.xml entries for "
                    + ikasanEnv.getIkasanSecurityResourceMetaData() + ".");
        }
        return this.ikasanSecurityService;
    }

    /**
     * Get the context concrete implementation class name.
     * 
     * @return the contextImplClass
     */
    public String getContextImplClass()
    {
        logger.debug("Getting contextImplClass [" + this.contextImplClass + "]");
        return this.contextImplClass;
    }

    /**
     * Set the context concrete implementation class name.
     * 
     * @param contextImplClass the contextImplClass to set
     */
    public void setContextImplClass(final String contextImplClass)
    {
        this.contextImplClass = contextImplClass;
        logger.debug("Setting contextImplClass [" + this.contextImplClass //$NON-NLS-1$
                + "]"); //$NON-NLS-1$
    }

    /**
     * Get the environment concrete implementation class name.
     * 
     * @return the environmentImplClass
     */
    public String getEnvironmentImplClass()
    {
        logger.debug("Getting environmentImplClass [" //$NON-NLS-1$
                + this.environmentImplClass + "]"); //$NON-NLS-1$
        return this.environmentImplClass;
    }

    /**
     * Set the environment concrete implementation class name.
     * 
     * @param environmentImplClass the environmentImplClass to set
     */
    public void setEnvironmentImplClass(final String environmentImplClass)
    {
        this.environmentImplClass = environmentImplClass;
        logger.debug("Setting environmentImplClass [" //$NON-NLS-1$
                + this.environmentImplClass + "]"); //$NON-NLS-1$
    }

    /**
     * Get the persistence concrete implementation class name.
     * 
     * @return the persistenceImplClass
     */
    public String getPersistenceImplClass()
    {
        logger.debug("Getting persistenceImplClass [" //$NON-NLS-1$
                + this.persistenceImplClass + "]"); //$NON-NLS-1$
        return this.persistenceImplClass;
    }

    /**
     * Set the persistence concrete implementation class name.
     * 
     * @param persistenceImplClass the persistenceImplClass to set
     */
    public void setPersistenceImplClass(final String persistenceImplClass)
    {
        this.persistenceImplClass = persistenceImplClass;
        logger.debug("Setting persistenceImplClass [" //$NON-NLS-1$
                + this.persistenceImplClass + "]"); //$NON-NLS-1$
    }

    /**
     * Get the XML parser concrete implementation class name.
     * 
     * @return the xmlParserImplClass
     */
    public String getXmlParserImplClass()
    {
        logger.debug("Getting xmlParserImplClass [" + this.xmlParserImplClass //$NON-NLS-1$
                + "]"); //$NON-NLS-1$
        return this.xmlParserImplClass;
    }

    /**
     * Set the XML parser concrete implementation class name.
     * 
     * @param xmlParserImplClass the xmlParserImplClass to set
     */
    public void setXmlParserImplClass(final String xmlParserImplClass)
    {
        this.xmlParserImplClass = xmlParserImplClass;
        logger.debug("Setting xmlParserImplClass [" + this.xmlParserImplClass //$NON-NLS-1$
                + "]"); //$NON-NLS-1$
    }

    /**
     * Get the XML transformer concrete implementation class name.
     * 
     * @return the xmlTransformerImplClass
     */
    public String getXmlTransformerImplClass()
    {
        logger.debug("Getting xmlTransformerImplClass [" //$NON-NLS-1$
                + this.xmlTransformerImplClass + "]"); //$NON-NLS-1$
        return this.xmlTransformerImplClass;
    }

    /**
     * Set the XML transformer concrete implementation class name.
     * 
     * @param xmlTransformerImplClass the xmlTransformerImplClass to set
     */
    public void setXmlTransformerImplClass(final String xmlTransformerImplClass)
    {
        this.xmlTransformerImplClass = xmlTransformerImplClass;
        logger.debug("Setting xmlTransformerImplClass [" //$NON-NLS-1$
                + this.xmlTransformerImplClass + "]"); //$NON-NLS-1$
    }

    /**
     * Get the String transformer concrete implementation class name.
     * 
     * @return the stringTransformerImplClass
     */
    public String getStringTransformerImplClass()
    {
        logger.debug("Getting stringTransformerImplClass [" //$NON-NLS-1$
                + this.stringTransformerImplClass + "]"); //$NON-NLS-1$
        return this.stringTransformerImplClass;
    }

    /**
     * Set the String transformer concrete implementation class name.
     * 
     * @param stringTransformerImplClass the xmlTransformerImplClass to set
     */
    public void setStringTransformerImplClass(final String stringTransformerImplClass)
    {
        this.stringTransformerImplClass = stringTransformerImplClass;
        logger.debug("Setting stringTransformerImplClass [" //$NON-NLS-1$
                + this.stringTransformerImplClass + "]"); //$NON-NLS-1$
    }

    /**
     * Get the XSL transformer concrete implementation class name.
     * 
     * @return the xslTransformerImplClass
     */
    public String getXslTransformerImplClass()
    {
        logger.debug("Getting xslTransformerImplClass [" //$NON-NLS-1$
                + this.xslTransformerImplClass + "]"); //$NON-NLS-1$
        return this.xslTransformerImplClass;
    }

    /**
     * Set the XSL transformer concrete implementation class name.
     * 
     * @param xslTransformerImplClass the xslTransformerImplClass to set
     */
    public void setXslTransformerImplClass(final String xslTransformerImplClass)
    {
        this.xslTransformerImplClass = xslTransformerImplClass;
        logger.debug("Setting xslTransformerImplClass [" //$NON-NLS-1$
                + this.xslTransformerImplClass + "]"); //$NON-NLS-1$
    }

    /**
     * Get the specified property from the underlying resources.
     * 
     * @param propertyName The name of the property to retrieve
     * @return the contextImplClass
     */
    public String getProperty(final String propertyName)
    {
        logger.debug("Getting property [" + propertyName //$NON-NLS-1$
                + "]"); //$NON-NLS-1$
        return this.resources.getProperty(propertyName);
    }

    /**
     * String presentation of the properties settings
     * 
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\n"); //$NON-NLS-1$
        sb.append("To Be Implemented\n"); //$NON-NLS-1$
        sb.append("\n"); //$NON-NLS-1$
        return sb.toString();
    }

    /**
     * Load resource from the given name. Try loading this resource in the following order, (2) load from the classpath;
     * (4) load from the file system; If all above fail then throw IOException.
     * 
     * @param name - resource name
     * @return byte[]
     * @throws IOException Exception if reading from the File System fails
     */
    public byte[] getAsByteArray(final String name) throws IOException
    {
        byte[] buffer;
        // try loading via URL from the CLASSPATH
        URL url = ResourceUtils.getAsUrl(name);
        if (url != null)
        {
            InputStream is = url.openStream();
            buffer = readInputStream(is);
            is.close();
            return buffer;
        }
        // Default else nothing on the CLASSPATH, lets try the file system
        FileInputStream fis = new FileInputStream(name);
        buffer = readInputStream(fis);
        fis.close();
        return buffer;
    }

    /**
     * Read the input stream and return the content as a byte array.
     * 
     * @param is The InputStream to read from
     * @return byte[]
     * @throws IOException Exception if we fail to read from the InputStream
     */
    private byte[] readInputStream(final InputStream is) throws IOException
    {
        int c;
        StringBuffer sb = new StringBuffer();
        while ((c = is.read()) != -1)
            sb.append((char) c);
        return sb.toString().getBytes();
    }

    /**
     * Get a new instance of the CommonContext
     * 
     * @return CommonContext
     */
    public CommonContext newContext()
    {
        logger.debug("Instantiating context based on class [" //$NON-NLS-1$
                + this.contextImplClass + "]"); //$NON-NLS-1$
        return (CommonContext) ClassInstantiationUtils.instantiate(this.contextImplClass);
    }

    /**
     * Create a new CommonContext based on the incoming properties.
     * 
     * @param properties The properties for this context
     * @return CommonContext
     */
    public CommonContext newContext(final Properties properties)
    {
        logger.debug("Instantiating context based on class [" //$NON-NLS-1$
                + this.contextImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { Properties.class };
        Object[] params = { properties };
        return (CommonContext) ClassInstantiationUtils.instantiate(this.contextImplClass, paramTypes, params);
    }



    /**
     * Get a new instance of the CommonXMLParser
     * 
     * @return CommonXMLParser - this maybe 'null' if it failed to create.
     */
    public CommonXMLParser newXMLParser()
    {
        logger.debug("Instantiating xmlParser based on class [" //$NON-NLS-1$
                + this.xmlParserImplClass + "]"); //$NON-NLS-1$
        return (CommonXMLParser) ClassInstantiationUtils.instantiate(this.xmlParserImplClass);
    }

    /**
     * Get a new instance of the CommonXMLTransformer
     * 
     * @return CommonXMLTransformer - this maybe 'null' if it failed to create.
     */
    public CommonXMLTransformer newXMLTransformer()
    {
        logger.debug("Instantiating xmlTransformer based on class [" //$NON-NLS-1$
                + this.xmlTransformerImplClass + "]"); //$NON-NLS-1$
        return (CommonXMLTransformer) ClassInstantiationUtils.instantiate(this.xmlTransformerImplClass);
    }

    /**
     * Get a new instance of the CommonXSLTransformer
     * 
     * @return CommonXSLTransformer - this maybe 'null' if it failed to create.
     */
    public CommonXSLTransformer newXSLTransformer()
    {
        logger.debug("Instantiating xslTransformer based on class [" //$NON-NLS-1$
                + this.xslTransformerImplClass + "]"); //$NON-NLS-1$
        return (CommonXSLTransformer) ClassInstantiationUtils.instantiate(this.xslTransformerImplClass);
    }

    /**
     * Get a new instance of the CommonStringTransformer
     * 
     * @return CommonStringTransformer - this maybe 'null' if it failed to create.
     */
    public CommonStringTransformer newStringTransformer()
    {
        logger.debug("Instantiating stringTransformer based on class [" //$NON-NLS-1$
                + this.stringTransformerImplClass + "]"); //$NON-NLS-1$
        return (CommonStringTransformer) ClassInstantiationUtils.instantiate(this.stringTransformerImplClass);
    }

    /**
     * Get a new instance of the CommonEnvironment
     * 
     * @return CommonEnvironment - this maybe 'null' if it failed to create.
     */
    public CommonEnvironment newEnvironment()
    {
        logger.debug("Instantiating environment based on class [" //$NON-NLS-1$
                + this.environmentImplClass + "]"); //$NON-NLS-1$
        return (CommonEnvironment) ClassInstantiationUtils.instantiate(this.environmentImplClass);
    }












    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.ServiceLocator#getCommonXmlParser()
     */
    public CommonXMLParser getCommonXmlParser()
    {
        if (commonXmlParser == null)
        {
            commonXmlParser = (CommonXMLParser) ClassInstantiationUtils.instantiate(this.xmlParserImplClass);
        }
        return commonXmlParser;
    }



    public JndiTemplate getJMSJndiTemplate()
    {
        if (jmsJndiTemplate == null)
        {
            ApplicationContext context = new FileSystemXmlApplicationContext(FILE_SEPARATOR
                    + ikasanEnv.getIkasanConfDir() + FILE_SEPARATOR + "jmsJndiContext.xml");
            jmsJndiTemplate = (JndiTemplate) context.getBean("jmsJndiTemplate");
        }
        return jmsJndiTemplate;
    }

    public File getIkasanConfigurationDirectory()
    {
        return new File(ikasanEnv.getIkasanConfDir());
    }
}

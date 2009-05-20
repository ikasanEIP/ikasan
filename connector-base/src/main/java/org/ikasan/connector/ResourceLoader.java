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
package org.ikasan.connector;

import org.apache.log4j.Logger;
import org.ikasan.common.factory.ClassInstantiationUtils;

/**
 * Singleton for loading connector environment resources
 *
 * @author Ikasan Development Team
 */
public class ResourceLoader
    extends org.ikasan.common.ResourceLoader
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(ResourceLoader.class);
    
    /** 
     * The resource name, e.g The properties file that holds the resource
     */
//    protected static String RESOURCE_NAME = "connectorResource.properties"; //$NON-NLS-1$

    /** instance of the singleton */
    private static ResourceLoader instance = null;

    /**
     * Singleton constructor
     */
    public static ResourceLoader getInstance()
    {
        if(instance == null)
        {
        	org.ikasan.common.ResourceLoader.RESOURCE_NAME = "connectorResource.xml";
            instance = new ResourceLoader();
        }
        return instance;
    }
    
    /**
     * Constructor
     */
    private ResourceLoader()
    {
        super(RESOURCE_NAME);
    }

    /**
     * String presentation of the properties settings
     * TODO Complete this method
     *
     * @return String presentation of the properties settings
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
     * Get a new instance of the ConnectorContext
     * @return ConnectorContext
     */
    @Override
    public ConnectorContext newContext()
    {
        logger.debug("Instantiating context based on class ["  //$NON-NLS-1$
                + this.contextImplClass + "]"); //$NON-NLS-1$
        return (ConnectorContext)ClassInstantiationUtils.instantiate(this.contextImplClass);
    }

    /**
     * Get a new instance of the ConnectorPersistence
     * @return ConnectorPersistence
     */
    @Override
    public ConnectorPersistenceFactory getPersistenceFactory() 
    {
        logger.debug("Get persistence singleton based on class ["  //$NON-NLS-1$
                + this.persistenceImplClass + "]"); //$NON-NLS-1$
        return (ConnectorPersistenceFactory)ClassInstantiationUtils.getSingleton(this.persistenceImplClass);
    }

    /**
     * Get a new instance of the ConnectorXMLParser
     * @return ConnectorXMLParser
     */
    @Override
    public ConnectorXMLParser newXMLParser()
    {
        logger.debug("Instantiating xmlParser based on class ["  //$NON-NLS-1$
                + this.xmlParserImplClass + "]"); //$NON-NLS-1$
        return (ConnectorXMLParser)ClassInstantiationUtils.instantiate(this.xmlParserImplClass);
    }

    /**
     * Get a new instance of the ConnectorXMLTransformer
     * @return ConnectorXMLTransformer
     */
    @Override
    public ConnectorXMLTransformer newXMLTransformer() 
    {
        logger.debug("Instantiating xmlTransformer based on class ["  //$NON-NLS-1$
                + this.xmlTransformerImplClass + "]"); //$NON-NLS-1$
        return (ConnectorXMLTransformer)ClassInstantiationUtils.instantiate(this.xmlTransformerImplClass);
    }

    /**
     * Get a new instance of the ConnectorXSLTransformer
     * @return ConnectorXSLTransformer
     */
    @Override
    public ConnectorXSLTransformer newXSLTransformer() 
    {
        logger.debug("Instantiating xslTransformer based on class ["  //$NON-NLS-1$
                + this.xslTransformerImplClass + "]"); //$NON-NLS-1$
        return (ConnectorXSLTransformer)ClassInstantiationUtils.instantiate(this.xslTransformerImplClass);        
    }
  
    /**
     * Get a new instance of the FrameworkStringTransformer
     * @return FrameworkStringTransformer
     */
    @Override
    public ConnectorStringTransformer newStringTransformer() 
    {
        logger.debug("Instantiating stringTransformer based on class ["  //$NON-NLS-1$
                + this.stringTransformerImplClass + "]"); //$NON-NLS-1$
        return (ConnectorStringTransformer)ClassInstantiationUtils.instantiate(this.stringTransformerImplClass);        
    }
    
    /**
     * Get a new instance of the ConnectorEnvironment
     * @return ConnectorEnvironment
     */
    @Override
    public ConnectorEnvironment newEnvironment() 
    {
        logger.debug("Instantiating environment based on class ["  //$NON-NLS-1$
                + this.environmentImplClass + "]"); //$NON-NLS-1$
        return (ConnectorEnvironment)ClassInstantiationUtils.instantiate(this.environmentImplClass);        
    }
    
}

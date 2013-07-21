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
package org.ikasan.framework;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.ikasan.common.factory.ClassInstantiationUtils;

/**
 * Singleton for loading framework environment resources
 * 
 * @author Ikasan Development Team
 */
public class ResourceLoader extends org.ikasan.common.ResourceLoader
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(ResourceLoader.class);

    /** Flow implementation class */
    protected static String FLOW_IMPL_CLASS = "flowImpl.class"; //$NON-NLS-1$

    /** concrete class names */
    protected String flowImplClass;

    /** instance of the singleton */
    private static ResourceLoader instance = null;

    /**
     * Singleton constructor
     * 
     * @return ResourceLoader
     */
    public static ResourceLoader getInstance()
    {
        if (instance == null)
        {
            org.ikasan.common.ResourceLoader.RESOURCE_NAME = "frameworkResource.xml"; //$NON-NLS-1$
            instance = new ResourceLoader();
        }
        return instance;
    }

    /**
     * ResourceLoader constructor
     * 
     */
    private ResourceLoader()
    {
        super(RESOURCE_NAME);
    }

    /**
     * Convenience method to load all resources from a given properties.
     * 
     * @param props Properties of this resource
     */
    @Override
    public void loadAllResources(final Properties props)
    {
        super.loadAllResources(props);
        this.setFlowImplClass(props.getProperty(FLOW_IMPL_CLASS));
    }

    /**
     * String presentation of the properties settings<br>
     * TODO Needs implementation
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
     * Set the flow implementation class
     * 
     * @param flowImplClass the flowImplClass to set
     */
    public void setFlowImplClass(final String flowImplClass)
    {
        this.flowImplClass = flowImplClass;
        logger.debug("Setting flowImplClass [" + this.flowImplClass + "]"); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Get a new instance of the FrameworkEnvironment
     * 
     * @return FrameworkEnvironment
     */
    @Override
    public FrameworkEnvironment newEnvironment()
    {
        logger.debug("Instantiating environment based on class [" //$NON-NLS-1$
                + this.environmentImplClass + "]"); //$NON-NLS-1$
        return (FrameworkEnvironment) ClassInstantiationUtils.instantiate(this.environmentImplClass);
    }
}

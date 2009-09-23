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

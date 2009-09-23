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
package org.ikasan.connector;

import org.apache.log4j.Logger;
import org.ikasan.common.factory.ClassInstantiationUtils;

/**
 * Singleton for loading connector environment resources
 * 
 * @author Ikasan Development Team
 */
public class ResourceLoader extends org.ikasan.common.ResourceLoader
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(ResourceLoader.class);

    /** instance of the singleton */
    private static ResourceLoader instance = null;

    /**
     * Singleton constructor
     * 
     * @return The ResourceLoader singleton
     */
    public static ResourceLoader getInstance()
    {
        if (instance == null)
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
     * String presentation of the properties settings TODO Complete this method
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
     * 
     * @return ConnectorContext
     */
    @Override
    public ConnectorContext newContext()
    {
        logger.debug("Instantiating context based on class [" //$NON-NLS-1$
                + this.contextImplClass + "]"); //$NON-NLS-1$
        return (ConnectorContext) ClassInstantiationUtils.instantiate(this.contextImplClass);
    }

    /**
     * Get a new instance of the ConnectorXMLParser
     * 
     * @return ConnectorXMLParser
     */
    @Override
    public ConnectorXMLParser newXMLParser()
    {
        logger.debug("Instantiating xmlParser based on class [" //$NON-NLS-1$
                + this.xmlParserImplClass + "]"); //$NON-NLS-1$
        return (ConnectorXMLParser) ClassInstantiationUtils.instantiate(this.xmlParserImplClass);
    }

    /**
     * Get a new instance of the ConnectorXMLTransformer
     * 
     * @return ConnectorXMLTransformer
     */
    @Override
    public ConnectorXMLTransformer newXMLTransformer()
    {
        logger.debug("Instantiating xmlTransformer based on class [" //$NON-NLS-1$
                + this.xmlTransformerImplClass + "]"); //$NON-NLS-1$
        return (ConnectorXMLTransformer) ClassInstantiationUtils.instantiate(this.xmlTransformerImplClass);
    }

    /**
     * Get a new instance of the ConnectorXSLTransformer
     * 
     * @return ConnectorXSLTransformer
     */
    @Override
    public ConnectorXSLTransformer newXSLTransformer()
    {
        logger.debug("Instantiating xslTransformer based on class [" //$NON-NLS-1$
                + this.xslTransformerImplClass + "]"); //$NON-NLS-1$
        return (ConnectorXSLTransformer) ClassInstantiationUtils.instantiate(this.xslTransformerImplClass);
    }

    /**
     * Get a new instance of the FrameworkStringTransformer
     * 
     * @return FrameworkStringTransformer
     */
    @Override
    public ConnectorStringTransformer newStringTransformer()
    {
        logger.debug("Instantiating stringTransformer based on class [" //$NON-NLS-1$
                + this.stringTransformerImplClass + "]"); //$NON-NLS-1$
        return (ConnectorStringTransformer) ClassInstantiationUtils.instantiate(this.stringTransformerImplClass);
    }

    /**
     * Get a new instance of the ConnectorEnvironment
     * 
     * @return ConnectorEnvironment
     */
    @Override
    public ConnectorEnvironment newEnvironment()
    {
        logger.debug("Instantiating environment based on class [" //$NON-NLS-1$
                + this.environmentImplClass + "]"); //$NON-NLS-1$
        return (ConnectorEnvironment) ClassInstantiationUtils.instantiate(this.environmentImplClass);
    }
}

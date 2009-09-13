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
package org.ikasan.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.common.configuration.Ikasan;
import org.ikasan.common.util.ResourceUtils;
import org.ikasan.common.configuration.Entry;

/**
 * Singleton for loading the base Ikasan runtime environment configuration.
 * The Ikasan env configuration is the base configuration for the environment
 * within which Ikasan is running, so this must always work, or be very vocal 
 * where exceptions are encountered.
 * 
 * @author Ikasan Development Team
 */
public class IkasanEnvImpl
    implements IkasanEnv
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(IkasanEnvImpl.class);
    /** Ikasan base configuration name */
    protected static String IKASAN_BASE = "ikasan.xml"; //$NON-NLS-1$
    /** instance of the singleton */
    private static IkasanEnv instance = null;
    /** static constants for Ikasan runtime locations */
    /** runtime conf dir */
    private static final String IKASAN_CONF_DIR = "ikasan.conf.dir";
    /** runtime security conf dir */
    private static final String IKASAN_SECURITY_CONF_DIR = "ikasan.secure.conf.dir";
    /** runtime security resource URL */
    private static final String IKASAN_SECURITY_RESOURCE = "ikasan.security.resource";
    /** runtime web server resource URL */
    private static final String IKASAN_WEB_RESOURCE = "ikasan.web.resource";
    
    /** Mandatory Ikasan configuration */
    private Ikasan ikasan = null;
    /** Map version of the properties for convenience of access */
    private Map<String,String> ikasanMap = new HashMap<String,String>();
    
    /**
     * Singleton constructor
     * @param env 
     * 
     * @return IkasanEnv
     */
    public static IkasanEnv getInstance(final CommonEnvironment env)
    {
        if (instance != null)
            return instance;

        synchronized(IkasanEnvImpl.class)
        {
            if(instance == null)
                instance = new IkasanEnvImpl(IKASAN_BASE, env);

            return instance;
        }
    }

    /**
     * Default constructor
     * 
     * @param resource The resource name so we can get resource properties
     * @param env 
     */
    private IkasanEnvImpl(final String resource, final CommonEnvironment env)
    {
        try
        {
            this.ikasan = Ikasan.fromXML(ResourceUtils.loadResource(resource));

            // resolve embedded environment variables and populate the map
            for(Entry entry : this.ikasan.getEntries())
            {
                entry.setValue( env.expandEnvVar(entry.getValue()) );
                this.ikasanMap.put(entry.getKey(), entry.getValue());
                logger.info("Ikasan environment setting [" + entry.getKey()
                        + "][" + entry.getValue() + "]");

                // TODO - replace this with changes to the Env class
                // and dont set System props directly
                System.setProperty(entry.getKey(), entry.getValue());
            }
            
            logger.info("Successfully loaded " + resource);
        }
        catch (Exception e)
        {
            // Make sure the world sees this log entry.
            // Only time I would advocate logging and throwing...
            String failMsg = "Failed to load [" 
                + resource 
                + "]. Nothing will work! ";
            logger.fatal(failMsg, e);
            throw new CommonRuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanConfDir()
     */
    public String getIkasanConfDir()
    {
        return this.ikasanMap.get(IkasanEnvImpl.IKASAN_CONF_DIR);
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanConfDirMetaData()
     */
    public String getIkasanConfDirMetaData()
    {
        return IkasanEnvImpl.IKASAN_CONF_DIR;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanSecureConfDir()
     */
    public String getIkasanSecureConfDir()
    {
        return this.ikasanMap.get(IkasanEnvImpl.IKASAN_SECURITY_CONF_DIR);
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanSecureConfDirMetaData()
     */
    public String getIkasanSecureConfDirMetaData()
    {
        return IkasanEnvImpl.IKASAN_SECURITY_CONF_DIR;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanSecurityResource()
     */
    public String getIkasanSecurityResource()
    {
        return this.ikasanMap.get(IkasanEnvImpl.IKASAN_SECURITY_RESOURCE);
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanSecurityResourceMetaData()
     */
    public String getIkasanSecurityResourceMetaData()
    {
        return IkasanEnvImpl.IKASAN_SECURITY_RESOURCE;
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanWebResource()
     */
    public String getIkasanWebResource()
    {
        return this.ikasanMap.get(IkasanEnvImpl.IKASAN_WEB_RESOURCE);
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.IkasanPlatform#getIkasanWebResourceMetaData()
     */
    public String getIkasanWebResourceMetaData()
    {
        return IkasanEnvImpl.IKASAN_WEB_RESOURCE;
    }
}

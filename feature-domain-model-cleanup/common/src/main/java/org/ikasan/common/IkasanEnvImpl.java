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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.common.configuration.Entry;
import org.ikasan.common.configuration.Ikasan;
import org.ikasan.common.util.ResourceUtils;

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

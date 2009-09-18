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
package org.ikasan.common.security;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonEnvironment;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.configuration.Entry;
import org.ikasan.common.configuration.IkasanSecurity;
import org.ikasan.common.util.ResourceUtils;

/**
 * Singleton for loading the Ikasan runtime security configuration. The Ikasan security configuration is an optional
 * part of Ikasan allowing authentication and authorisation of Ikasan resources. This configuration provides the basis
 * for this security.
 * 
 * @author Ikasan Development Team
 */
public class IkasanSecurityConfImpl implements IkasanSecurityConf
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(IkasanSecurityConfImpl.class);

    /** Runtime encryption policies URL */
    private static final String IKASAN_ENCRYPTION_POLICIES_RESOURCE = "ikasan.encryptionPolicies.resource";

    /** Runtime actual encryption policy */
    private static final String IKASAN_JMS_ENCRYPTION_POLICY_NAME = "ikasan.jms.encryption.policy.name";

    /** JMS username */
    private static final String IKASAN_JMS_USERNAME = "ikasan.jms.username";

    /** JMS password */
    private static final String IKASAN_JMS_PASSWORD = "ikasan.jms.password";

    /** JMS Security Datasource JNDI name */
    private static final String IKASAN_JMS_SECURITY_DS_JNDI = "ikasan.jms.security.ds.jndi";

    /** Mandatory properties for Ikasan security */
    private IkasanSecurity ikasanSecurity = null;

    /** Map version of the properties for convenience of access */
    private Map<String, String> ikasanMap = new HashMap<String, String>();

    /**
     * Constructor
     * 
     * @param resource The resource to set
     * @param env The environment to set the resource in
     */
    public IkasanSecurityConfImpl(final String resource, final CommonEnvironment env)
    {
        try
        {
            InputStream is = ResourceUtils.loadResource(env.expandEnvVar(resource));
            this.ikasanSecurity = IkasanSecurity.fromXML(is);
            // resolve embedded environment variables and populate the map
            for (Entry entry : this.ikasanSecurity.getEntries())
            {
                entry.setValue(env.expandEnvVar(entry.getValue()));
                this.ikasanMap.put(entry.getKey(), entry.getValue());
                logger.info("Ikasan security conf setting [" + entry.getKey() + "][" + entry.getValue() + "]");
            }
            logger.info("Successfully loaded " + resource);
        }
        catch (Exception e)
        {
            // Make sure the world sees this log entry.
            // Only time I would advocate logging and throwing...
            String failMsg = "Failed to load [" + resource + "]. Security will not will work! ";
            logger.fatal(failMsg, e);
            throw new CommonRuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getEncryptionPoliciesResource()
     */
    public String getEncryptionPoliciesResource()
    {
        return this.ikasanMap.get(IkasanSecurityConfImpl.IKASAN_ENCRYPTION_POLICIES_RESOURCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getEncryptionPoliciesResourceMetaData()
     */
    public String getEncryptionPoliciesResourceMetaData()
    {
        return IkasanSecurityConfImpl.IKASAN_ENCRYPTION_POLICIES_RESOURCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getJMSEncryptionPolicyName()
     */
    public String getJMSEncryptionPolicyName()
    {
        return this.ikasanMap.get(IkasanSecurityConfImpl.IKASAN_JMS_ENCRYPTION_POLICY_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getEncryptionPolicyNameMetaData()
     */
    public String getJMSEncryptionPolicyNameMetaData()
    {
        return IkasanSecurityConfImpl.IKASAN_JMS_ENCRYPTION_POLICY_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getJMSPassword()
     */
    public String getJMSPassword()
    {
        return this.ikasanMap.get(IkasanSecurityConfImpl.IKASAN_JMS_PASSWORD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getJMSPasswordMetaData()
     */
    public String getJMSPasswordMetaData()
    {
        return IkasanSecurityConfImpl.IKASAN_JMS_PASSWORD;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getJMSUsername()
     */
    public String getJMSUsername()
    {
        return this.ikasanMap.get(IkasanSecurityConfImpl.IKASAN_JMS_USERNAME);
    }

    public String getJMSSecurityDataSourceJNDIName()
    {
        return ikasanMap.get(IkasanSecurityConfImpl.IKASAN_JMS_SECURITY_DS_JNDI);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityConf#getJMSUsernameMetaData()
     */
    public String getJMSUsernameMetaData()
    {
        return IkasanSecurityConfImpl.IKASAN_JMS_USERNAME;
    }
}

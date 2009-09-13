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
package org.ikasan.common.security;

import java.io.*;
import java.util.*;

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

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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonEnvironment;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.security.policy.EncryptionPolicies;
import org.ikasan.common.security.policy.EncryptionPolicy;
import org.ikasan.common.util.ResourceUtils;

/**
 * Implementation of the Ikasan Security Service.
 * 
 * @author Ikasan Development Team
 */
public class IkasanSecurityServiceImpl implements IkasanSecurityService
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(IkasanSecurityServiceImpl.class);

    /** Ikasan Security Configuration */
    private IkasanSecurityConf ikasanSecurityConf = null;

    /**
     * Constructor
     * 
     * @param resource - The resource
     * @param env - The environment
     */
    public IkasanSecurityServiceImpl(final String resource, final CommonEnvironment env)
    {
        this.ikasanSecurityConf = new IkasanSecurityConfImpl(resource, env);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityService#getJMSEncryptionPolicy()
     */
    public EncryptionPolicy getJMSEncryptionPolicy() throws EncryptionPolicyNotFoundException
    {
        return this.getEncryptionPolicy(ikasanSecurityConf.getJMSEncryptionPolicyName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.security.IkasanSecurityService#getEncryptionPolicy(final String encryptionPolicyName)
     */
    public EncryptionPolicy getEncryptionPolicy(final String encryptionPolicyName)
            throws EncryptionPolicyNotFoundException
    {
        logger.debug("About to load [" + encryptionPolicyName + "]...");
        String encryptionPoliciesResource = this.ikasanSecurityConf.getEncryptionPoliciesResource();
        try
        {
            // Load the valid encryption policies
            InputStream is = ResourceUtils.loadResource(encryptionPoliciesResource);
            // Create a Policy object from incoming XML
            EncryptionPolicies encryptionPolicies = EncryptionPolicies.fromXML(is);
            List<EncryptionPolicy> encryptionPolicyList = encryptionPolicies.getEncryptionPolicies();
            for (EncryptionPolicy encryptionPolicy : encryptionPolicyList)
            {
                if (encryptionPolicy.getName().equals(encryptionPolicyName))
                {
                    logger.debug("Matching policy found  for [" + encryptionPolicyName + ']');
                    return encryptionPolicy;
                }
            }
            logger.warn("Application Policy [" + encryptionPolicyName + "] not found in [" + encryptionPoliciesResource
                    + "]. ");
            throw new EncryptionPolicyNotFoundException(encryptionPolicyName + " not found in "
                    + encryptionPoliciesResource + ".");
        }
        catch (IOException e)
        {
            logger.error("IOException trying to load a resource, catastrphic error.");
            throw new CommonRuntimeException(e);
        }
    }

    public String getJMSSecurityDataSourceJNDIName()
    {
        return ikasanSecurityConf.getJMSSecurityDataSourceJNDIName();
    }

    public IkasanSecurityConf getIkasanSecurityConf()
    {
        return ikasanSecurityConf;
    }
}

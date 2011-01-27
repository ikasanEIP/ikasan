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

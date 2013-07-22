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

// Imported commons classes
import java.util.HashMap;
import java.util.Map;

import org.ikasan.common.ResourceLoader;

/**
 * This class represents the '<code>AuthenticationPolicyImpl</code>'.
 * 
 * @author Ikasan Development Team
 */
public class AuthenticationPolicyImpl implements AuthenticationPolicy
{
    /** Encryption policy name */
    private String policyName;

    /** Set encryption policies URL - default is to pick up from classpath */
    private String encryptionPoliciesURL = AuthenticationPolicyConst.DEFAULT_ENCRYPTION_POLICIES_URL;

    /**
     * Default constructor
     * 
     * @param options The authentication policy options
     */
    public AuthenticationPolicyImpl(final Map<String, String> options)
    {
        // TODO - is this worth xstreaming?
        this.policyName = options.get(AuthenticationPolicyConst.POLICY_NAME_LITERAL);
        // Only populate if not null
        if (options.get(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL) != null)
        {
            this.encryptionPoliciesURL = options.get(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL);
        }
    }

    /**
     * Get the policy name
     * 
     * @return the policyName
     */
    public String getPolicyName()
    {
        return this.policyName;
    }

    /**
     * Set the policy name
     * 
     * @param policyName the policyName to set
     */
    public void setPolicyName(final String policyName)
    {
        this.policyName = policyName;
    }

    /**
     * Get the the encryption policies url
     * 
     * @return the getEncryptionPoliciesURL
     */
    public String getEncryptionPoliciesURL()
    {
        return this.encryptionPoliciesURL;
    }

    /**
     * Set the encryption policies url
     * 
     * @param encryptionPoliciesURL the encryptionPoliciesURL to set
     */
    public void setEncryptionPoliciesURL(final String encryptionPoliciesURL)
    {
        this.encryptionPoliciesURL = ResourceLoader.getInstance().newEnvironment().expandEnvVar(encryptionPoliciesURL);
    }

    /**
     * Return the authentication policy as a map
     * 
     * @return the authentication policy as a map
     */
    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(AuthenticationPolicyConst.POLICY_NAME_LITERAL, this.getPolicyName());
        map.put(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL, this.getEncryptionPoliciesURL());
        return map;
    }
}

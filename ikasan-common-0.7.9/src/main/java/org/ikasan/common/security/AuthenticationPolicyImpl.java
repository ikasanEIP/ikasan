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

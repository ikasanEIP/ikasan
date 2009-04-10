/*
 * $Id: AuthenticationPolicyImpl.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/security/AuthenticationPolicyImpl.java $
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
 * @author <a href="mailto:info@ikasan.org">Madhu Konda</a>
 */
public class AuthenticationPolicyImpl
    implements AuthenticationPolicy
{
    /** encryption policy name */
    private String policyName;

    /** set encryption policies URL - default is to pick up from classpath */
    private String encryptionPoliciesURL = AuthenticationPolicyConst.DEFAULT_ENCRYPTION_POLICIES_URL;
    
    /** 
     * Default constructor
     *  
     * @param options The authentication policy options
     */
    public AuthenticationPolicyImpl(final Map<String,String> options)
    {
        // TODO - is this worth xstreaming?
        this.policyName = options.get(AuthenticationPolicyConst.POLICY_NAME_LITERAL);

        // only populate if not null
        if(options.get(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL) != null)
            this.encryptionPoliciesURL = options.get(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL);
    }
    
    /**
     * @return the policyName
     */
    public String getPolicyName()
    {
        return this.policyName;
    }

    /**
     * @param policyName the policyName to set
     */
    public void setPolicyName(final String policyName)
    {
        this.policyName = policyName;
    }

    /**
     * @return the getEncryptionPoliciesURL
     */
    public String getEncryptionPoliciesURL()
    {
        return this.encryptionPoliciesURL;
    }

    /**
     * 
     * @param encryptionPoliciesURL the encryptionPoliciesURL to set
     */
    public void setEncryptionPoliciesURL(final String encryptionPoliciesURL)
    {
        this.encryptionPoliciesURL = ResourceLoader.getInstance().newEnvironment()
            .expandEnvVar(encryptionPoliciesURL);
    }

    /**
     * @return the IPC instance as a map
     */
    public Map<String, String> toMap()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(AuthenticationPolicyConst.POLICY_NAME_LITERAL, this.getPolicyName());
        map.put(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL, this.getEncryptionPoliciesURL());
        return map;
    }

}

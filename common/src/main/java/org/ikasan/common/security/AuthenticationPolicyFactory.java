/*
 * $Id: AuthenticationPolicyFactory.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/security/AuthenticationPolicyFactory.java $
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

import java.util.HashMap;
import java.util.Map;

/**
 * A factory class to create the AuthenticationPolicy instances.
 *
 * @author <a href="mailto:info@ikasan.org">Madhu Konda</a>
 * TODO - make this a singleton
 */
public class AuthenticationPolicyFactory
{
    /** Constructor */
    public AuthenticationPolicyFactory()
    {
        // Do Nothing
    }
    
    /**
     * Get the new instance of the AuthenticationPolicy
     * 
     * @param options
     * @return AuthenticationPolicy
     */
    public static AuthenticationPolicy getAuthenticationPolicy(final Map<String, String> options)
    {
        return new AuthenticationPolicyImpl(options);
    }

    /**
     * Get the new instance of the AuthenticationPolicy
     * 
     * @param policyName
     * @param encryptionPoliciesURL
     * @return AuthenticationPolicy
     */
    public static AuthenticationPolicy getAuthenticationPolicy(final String policyName, 
            final String encryptionPoliciesURL)
    {
        Map<String,String> map = new HashMap<String,String>();
        map.put(AuthenticationPolicyConst.POLICY_NAME_LITERAL, policyName);
        map.put(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL, encryptionPoliciesURL);
        return new AuthenticationPolicyImpl(map);
    }
    
    /**
     * Get the new instance of the AuthenticationPolicy
     * 
     * @param policyName
     * @return AuthenticationPolicy
     */
    public static AuthenticationPolicy getAuthenticationPolicy(final String policyName)
    {
        return getAuthenticationPolicy(policyName, null);
    }    
}

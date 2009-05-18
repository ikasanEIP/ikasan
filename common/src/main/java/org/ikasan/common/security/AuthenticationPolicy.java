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

/**
 * Interface defining the operations on an AuthenticationPolicy.
 * 
 * @author <a href="mailto:info@ikasan.org">Madhu Konda</a>
 */
public interface AuthenticationPolicy
{
    /**
     * Getter for policyName
     * @return the policyName
     */
    String getPolicyName();

    /**
     * Setter for policyName
     * @param policy the policyName to set
     */
    void setPolicyName(final String policy);

    /**
     * Getter for encryptionPoliciesURL. This defines the URL 
     * for supported encryption policies.
     * @return the encryptionPoliciesURL
     */
    String getEncryptionPoliciesURL();

    /**
     * Setter to override the default encryptionPoliciesURL for supported
     * encryption policies.
     * @param encryptionPoliciesURL the encryptionPoliciesURL to set
     */
    void setEncryptionPoliciesURL(final String encryptionPoliciesURL);
}

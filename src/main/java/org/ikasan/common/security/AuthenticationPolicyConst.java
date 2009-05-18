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
 * AuthenticationPolicy constants defining defaults and literals
 * as the AuthenticationPolicy often gets instantiated from a Map.
 * 
 * @author <a href="mailto:info@ikasan.org">Madhu Konda</a>
 */
public class AuthenticationPolicyConst
{
  
  /** policy name literal const */
  public static final String POLICY_NAME_LITERAL = "policyName";
  
  /** encryption policies URL literal const */
  public static final String ENCRYPTION_POLICIES_URL_LITERAL = "encryptionPoliciesURL";
  
  /** default encryption policies URL */
  public static final String DEFAULT_ENCRYPTION_POLICIES_URL = "encryptionPolicies.xml";
  
}

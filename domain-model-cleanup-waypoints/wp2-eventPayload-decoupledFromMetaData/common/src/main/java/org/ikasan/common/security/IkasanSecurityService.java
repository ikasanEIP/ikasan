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

import org.ikasan.common.security.policy.EncryptionPolicy;

/**
 * Interface defining the Ikasan Security Service operations.
 * 
 * @author Ikasan Development Team
 */
public interface IkasanSecurityService
{
    /**
     * Gets the Ikasan runtime platform JMS encryption policy.
     * 
     * @return EncryptionPolicy
     * @throws EncryptionPolicyNotFoundException - If Encryption Policy is not found
     */
    public EncryptionPolicy getJMSEncryptionPolicy() throws EncryptionPolicyNotFoundException;

    /**
     * Gets the Ikasan runtime platform encryption policy by the given name.
     * 
     * @param encryptionPolicyName - The encryption policy name
     * @return EncryptionPolicy
     * @throws EncryptionPolicyNotFoundException - If Encryption Policy is not found
     */
    public EncryptionPolicy getEncryptionPolicy(final String encryptionPolicyName)
            throws EncryptionPolicyNotFoundException;

    /**
     * Gets the JNDI name for the JMS Security DataSource
     * 
     * @return String - JNDI value
     */
    public String getJMSSecurityDataSourceJNDIName();

    /**
     * Gets the IkasanSecurityConf
     * 
     * @return IkasanSecurityConf
     */
    public IkasanSecurityConf getIkasanSecurityConf();
}

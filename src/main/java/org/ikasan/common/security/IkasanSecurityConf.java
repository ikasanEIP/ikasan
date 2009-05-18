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
 * Interface defining the Ikasan runtime security configuration.
 * 
 * @author <a href="mailto:info@ikasan.org">Ikasan Development Team</a>
 */
public interface IkasanSecurityConf
{
    /**
     * Gets the Ikasan runtime platform encryption policies resource.
     * 
     * @return String
     */
    public String getEncryptionPoliciesResource();

    /**
     * Gets the Ikasan runtime platform encryption policies resource meta data via which the actual value is associated.
     * 
     * @return String
     */
    public String getEncryptionPoliciesResourceMetaData();

    /**
     * Gets the Ikasan runtime platform JMS encryption policy name.
     * 
     * @return String
     */
    public String getJMSEncryptionPolicyName();

    /**
     * Gets the Ikasan runtime platform JMS encryption policy name meta data via which the actual value is associated.
     * 
     * @return String
     */
    public String getJMSEncryptionPolicyNameMetaData();

    /**
     * Gets the Ikasan runtime platform JMS username.
     * 
     * @return String
     */
    public String getJMSUsername();

    /**
     * Gets the Ikasan runtime platform JMS username meta data via which the actual value is associated.
     * 
     * @return String
     */
    public String getJMSUsernameMetaData();

    /**
     * Gets the Ikasan runtime platform JMS password.
     * 
     * @return String
     */
    public String getJMSPassword();

    /**
     * Gets the JNDI name for the JMS Security DataSource
     * 
     * @return String - JNDI value
     */
    public String getJMSSecurityDataSourceJNDIName();

    /**
     * Gets the Ikasan runtime platform JMS password meta data via which the actual value is associated.
     * 
     * @return String
     */
    public String getJMSPasswordMetaData();
}

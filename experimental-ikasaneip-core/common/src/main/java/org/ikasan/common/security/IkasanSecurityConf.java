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

/**
 * Interface defining the Ikasan runtime security configuration.
 * 
 * @author Ikasan Development Team
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

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
package org.ikasan.common;

/**
 * Interface defining the Ikasan runtime environment configuration.
 * 
 * @author Ikasan Development Team
 */
public interface IkasanEnv
{
    /**
     * Gets the Ikasan runtime platform configuration directory.
     * @return String
     */
    public String getIkasanConfDir();

    /**
     * Gets the Ikasan runtime platform configuration directory meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanConfDirMetaData();

    /**
     * Gets the Ikasan runtime platform secure configuration directory.
     * @return String
     */
    public String getIkasanSecureConfDir();

    /**
     * Gets the Ikasan runtime platform secure configuration directory meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanSecureConfDirMetaData();

    /**
     * Gets the Ikasan runtime platform security configuration resource.
     * @return String
     */
    public String getIkasanSecurityResource();

    /**
     * Gets the Ikasan runtime platform security configuration resource meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanSecurityResourceMetaData();

    /**
     * Gets the Ikasan runtime Web Server configuration resource.
     * @return String
     */
    public String getIkasanWebResource();

    /**
     * Gets the Ikasan runtime Web Server configuration resource meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanWebResourceMetaData();

}

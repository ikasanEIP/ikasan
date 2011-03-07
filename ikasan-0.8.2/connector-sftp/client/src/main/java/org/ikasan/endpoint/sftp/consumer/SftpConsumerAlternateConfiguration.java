/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.endpoint.sftp.consumer;

/**
 * SFTP Consumer Configuration model.
 * 
 * @author Ikasan Development Team
 */
public class SftpConsumerAlternateConfiguration extends SftpConsumerConfiguration
{
    /** SFTP default Remote host */
    private String alternateRemoteHost = String.valueOf("localhost");

    /** SFTP private key hosts */
    private String alternatePrivateKeyFilename;

    /** SFTP max retry attempts */
    private Integer alternateMaxRetryAttempts = Integer.valueOf(3);

    /** SFTP default remote port */
    private Integer alternateRemotePort = Integer.valueOf(22);

    /** SFTP known hosts */
    private String alternateKnownHostsFilename;

    /** SFTP user */
    private String alternateUsername;

    /** SFTP password/passphrase */
    private String alternatePassword;

    /** SFTP remote port */
    private Integer alternateConnectionTimeout = Integer.valueOf(60000);

    /**
     * @return the alternateRemoteHost
     */
    public String getAlternateRemoteHost()
    {
        return this.alternateRemoteHost;
    }

    /**
     * @param alternateRemoteHost the alternateRemoteHost to set
     */
    public void setAlternateRemoteHost(String alternateRemoteHost)
    {
        this.alternateRemoteHost = alternateRemoteHost;
    }

    /**
     * @return the alternatePrivateKeyFilename
     */
    public String getAlternatePrivateKeyFilename()
    {
        return this.alternatePrivateKeyFilename;
    }

    /**
     * @param alternatePrivateKeyFilename the alternatePrivateKeyFilename to set
     */
    public void setAlternatePrivateKeyFilename(String alternatePrivateKeyFilename)
    {
        this.alternatePrivateKeyFilename = alternatePrivateKeyFilename;
    }

    /**
     * @return the alternateMaxRetryAttempts
     */
    public Integer getAlternateMaxRetryAttempts()
    {
        return this.alternateMaxRetryAttempts;
    }

    /**
     * @param alternateMaxRetryAttempts the alternateMaxRetryAttempts to set
     */
    public void setAlternateMaxRetryAttempts(Integer alternateMaxRetryAttempts)
    {
        this.alternateMaxRetryAttempts = alternateMaxRetryAttempts;
    }

    /**
     * @return the alternateRemotePort
     */
    public Integer getAlternateRemotePort()
    {
        return this.alternateRemotePort;
    }

    /**
     * @param alternateRemotePort the alternateRemotePort to set
     */
    public void setAlternateRemotePort(Integer alternateRemotePort)
    {
        this.alternateRemotePort = alternateRemotePort;
    }

    /**
     * @return the alternateKnownHostsFilename
     */
    public String getAlternateKnownHostsFilename()
    {
        return this.alternateKnownHostsFilename;
    }

    /**
     * @param alternateKnownHostsFilename the alternateKnownHostsFilename to set
     */
    public void setAlternateKnownHostsFilename(String alternateKnownHostsFilename)
    {
        this.alternateKnownHostsFilename = alternateKnownHostsFilename;
    }

    /**
     * @return the alternateUsername
     */
    public String getAlternateUsername()
    {
        return this.alternateUsername;
    }

    /**
     * @param alternateUsername the alternateUsername to set
     */
    public void setAlternateUsername(String alternateUsername)
    {
        this.alternateUsername = alternateUsername;
    }

    /**
     * @return the alternatePassword
     */
    public String getAlternatePassword()
    {
        return this.alternatePassword;
    }

    /**
     * @param alternatePassword the alternatePassword to set
     */
    public void setAlternatePassword(String alternatePassword)
    {
        this.alternatePassword = alternatePassword;
    }

    /**
     * @return the alternateConnectionTimeout
     */
    public Integer getAlternateConnectionTimeout()
    {
        return this.alternateConnectionTimeout;
    }

    /**
     * @param alternateConnectionTimeout the alternateConnectionTimeout to set
     */
    public void setAlternateConnectionTimeout(Integer alternateConnectionTimeout)
    {
        this.alternateConnectionTimeout = alternateConnectionTimeout;
    }

}

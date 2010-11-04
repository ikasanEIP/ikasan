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
package org.ikasan.connector.sftp.producer;

/**
 * SFTP Producer Configuration model.
 * 
 * @author Ikasan Development Team
 */
public class SftpProducerConfiguration
{
    /** SFTP unqiue clientId */
    protected String clientID;

    /** SFTP cleanup journal on completion */
    protected Boolean cleanupJournalOnComplete = Boolean.TRUE;

    /** SFTP default Remote host */
    protected String remoteHost = String.valueOf("localhost");

    /** SFTP private key hosts */
    protected String privateKeyFilename;

    /** SFTP max retry attempts */
    protected Integer maxRetryAttempts = Integer.valueOf(3);

    /** SFTP default remote port */
    protected Integer remotePort = Integer.valueOf(22);

    /** SFTP known hosts */
    protected String knownHostsFilename;

    /** SFTP user */
    protected String username;

    /** SFTP password/passphrase */
    protected String password;

    /** SFTP remote port */
    protected Integer connectionTimeout = Integer.valueOf(60000);

    /** SFTP output directory */
    protected String outputDirectory;

    /** SFTP default rename extension */
    protected String renameExtension = String.valueOf(".tmp");

    /** SFTP overwrite */
    protected Boolean overwrite = Boolean.FALSE;

    /** SFTP unzip */
    protected Boolean unzip = Boolean.FALSE;

    /** SFTP generate and deliver a checksum */
    protected Boolean checksumDelivered = Boolean.FALSE;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getClientID()
    {
        return clientID;
    }

    public void setClientID(String clientID)
    {
        this.clientID = clientID;
    }

    public Boolean getCleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
    }

    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    public String getRemoteHost()
    {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    public String getPrivateKeyFilename()
    {
        return privateKeyFilename;
    }

    public void setPrivateKeyFilename(String privateKeyFilename)
    {
        this.privateKeyFilename = privateKeyFilename;
    }

    public Integer getMaxRetryAttempts()
    {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public Integer getRemotePort()
    {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
    }

    public String getKnownHostsFilename()
    {
        return knownHostsFilename;
    }

    public void setKnownHostsFilename(String knownHostsFilename)
    {
        this.knownHostsFilename = knownHostsFilename;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Integer getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    public String getRenameExtension()
    {
        return renameExtension;
    }

    public void setRenameExtension(String renameExtension)
    {
        this.renameExtension = renameExtension;
    }

    public Boolean getOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(Boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    public Boolean getUnzip()
    {
        return unzip;
    }

    public void setUnzip(Boolean unzip)
    {
        this.unzip = unzip;
    }

    public Boolean getChecksumDelivered()
    {
        return checksumDelivered;
    }

    public void setChecksumDelivered(Boolean checksumDelivered)
    {
        this.checksumDelivered = checksumDelivered;
    }

}

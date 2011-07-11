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
package org.ikasan.endpoint.sftp.producer;

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

    protected String tempFileName;

    /** SFTP overwrite */
    protected Boolean overwrite = Boolean.FALSE;

    /** SFTP unzip */
    protected Boolean unzip = Boolean.FALSE;

    /** SFTP generate and deliver a checksum */
    protected Boolean checksumDelivered = Boolean.FALSE;

    /** Creates any missing parent directory in the fully qualified filename of the file to be delivered */
    protected Boolean createParentDirectory = Boolean.FALSE;

    /**
     * @return the clientID
     */
    public String getClientID()
    {
        return this.clientID;
    }

    /**
     * @param clientID the clientID to set
     */
    public void setClientID(String clientID)
    {
        this.clientID = clientID;
    }

    /**
     * @return the cleanupJournalOnComplete
     */
    public Boolean getCleanupJournalOnComplete()
    {
        return this.cleanupJournalOnComplete;
    }

    /**
     * @param cleanupJournalOnComplete the cleanupJournalOnComplete to set
     */
    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    /**
     * @return the remoteHost
     */
    public String getRemoteHost()
    {
        return this.remoteHost;
    }

    /**
     * @param remoteHost the remoteHost to set
     */
    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    /**
     * @return the privateKeyFilename
     */
    public String getPrivateKeyFilename()
    {
        return this.privateKeyFilename;
    }

    /**
     * @param privateKeyFilename the privateKeyFilename to set
     */
    public void setPrivateKeyFilename(String privateKeyFilename)
    {
        this.privateKeyFilename = privateKeyFilename;
    }

    /**
     * @return the maxRetryAttempts
     */
    public Integer getMaxRetryAttempts()
    {
        return this.maxRetryAttempts;
    }

    /**
     * @param maxRetryAttempts the maxRetryAttempts to set
     */
    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    /**
     * @return the remotePort
     */
    public Integer getRemotePort()
    {
        return this.remotePort;
    }

    /**
     * @param remotePort the remotePort to set
     */
    public void setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
    }

    /**
     * @return the knownHostsFilename
     */
    public String getKnownHostsFilename()
    {
        return this.knownHostsFilename;
    }

    /**
     * @param knownHostsFilename the knownHostsFilename to set
     */
    public void setKnownHostsFilename(String knownHostsFilename)
    {
        this.knownHostsFilename = knownHostsFilename;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the connectionTimeout
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the outputDirectory
     */
    public String getOutputDirectory()
    {
        return this.outputDirectory;
    }

    /**
     * @param outputDirectory the outputDirectory to set
     */
    public void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return the renameExtension
     */
    public String getRenameExtension()
    {
        return this.renameExtension;
    }

    /**
     * @param renameExtension the renameExtension to set
     */
    public void setRenameExtension(String renameExtension)
    {
        this.renameExtension = renameExtension;
    }

    /**
     * @return the tempFileName
     */
    public String getTempFileName()
    {
        return this.tempFileName;
    }

    /**
     * @param tempFileName the tempFileName to set
     */
    public void setTempFileName(String tempFileName)
    {
        this.tempFileName = tempFileName;
    }

    /**
     * @return the overwrite
     */
    public Boolean getOverwrite()
    {
        return this.overwrite;
    }

    /**
     * @param overwrite the overwrite to set
     */
    public void setOverwrite(Boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    /**
     * @return the unzip
     */
    public Boolean getUnzip()
    {
        return this.unzip;
    }

    /**
     * @param unzip the unzip to set
     */
    public void setUnzip(Boolean unzip)
    {
        this.unzip = unzip;
    }

    /**
     * @return the checksumDelivered
     */
    public Boolean getChecksumDelivered()
    {
        return this.checksumDelivered;
    }

    /**
     * @param checksumDelivered the checksumDelivered to set
     */
    public void setChecksumDelivered(Boolean checksumDelivered)
    {
        this.checksumDelivered = checksumDelivered;
    }

    /**
     * @return the createParentDirectory
     */
    public Boolean getCreateParentDirectory()
    {
        return this.createParentDirectory;
    }

    /**
     * @param createParentDirectory the createParentDirectory to set
     */
    public void setCreateParentDirectory(Boolean createParentDirectory)
    {
        this.createParentDirectory = createParentDirectory;
    }

}

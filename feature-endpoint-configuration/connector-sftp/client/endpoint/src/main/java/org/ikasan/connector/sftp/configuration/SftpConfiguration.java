 /* 
 * $Id$
 * $URL$
 * 
 * ====================================================================
 *
 * Copyright (c) 2000-2010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 */
package org.ikasan.connector.sftp.configuration;

/**
 * SFTP Configuration model.
 * 
 * @author Jeff Mitchell
 */
public class SftpConfiguration
{
    /** SFTP unqiue clientId */
    protected String clientID;

    /** SFTP cleanup journal on completion */
    protected Boolean cleanupJournalOnComplete = Boolean.TRUE;

    /** SFTP default Remote host */
    protected String remoteHost = "localhost";

    /** SFTP private key hosts */
    protected String privateKeyFilename;

    /** SFTP max retry attempts */
    protected Integer maxRetryAttempts = new Integer(3);

    /** SFTP default remote port */
    protected Integer remotePort = new Integer(22);

    /** SFTP known hosts */
    protected String knownHostsFilename;

    /** SFTP user */
    protected String username;

    /** SFTP remote port */
    protected Integer connectionTimeout = new Integer(60000);

    /** SFTP output directory */
    protected String outputDirectory;

    /** SFTP default rename extension */
    protected String renameExtension = ".tmp";

    /** SFTP overwrite */
    protected Boolean overwrite = Boolean.FALSE;

    /** SFTP unzip */
    protected Boolean unzip = Boolean.FALSE;

    /** SFTP generate and deliver a checksum */
    protected Boolean checksumDelivered = Boolean.FALSE;

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

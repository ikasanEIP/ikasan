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
package org.ikasan.connector.sftp.outbound;

import java.util.ResourceBundle;

import org.ikasan.connector.base.outbound.EISConnectionSpec;

/**
 * This is an class representing the connection specific application properties passed to the getConnection method.
 * 
 * It can contain properties that override what is in the ra.xml file and add additional connection properties that are
 * at a logical level.
 * 
 * @author Ikasan Development Team
 */
public class SFTPConnectionSpec extends EISConnectionSpec
{
    /** The Remote Host */
    private String remoteHostname = null;

    /** The known hosts file name (fully qualified path) */
    private String knownHostsFilename = null;

    /** The maximum amount of retries - defaults to 1 */
    private Integer maxRetryAttempts = 1;

    /** The Port (default 22) */
    private Integer remotePort = 22;

    /** The private key file name (fully qualified path) */
    private String privateKeyFilename = null;

    /** Connection timeout in milliseconds (0 default means no timeout) */
    private Integer connectionTimeout = 0;

    /** The username */
    private String username = null;

    /** The authentication order */
    private String preferredAuthentications = new String();

    /** Cleanup Transaction Journal On Complete - default True */
    private Boolean cleanupJournalOnComplete = Boolean.TRUE;

    /** Initial poll time for source system only */
    private Integer pollTime = 0;

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public SFTPConnectionSpec()
    {
        // empty
    }

    /**
     * Default Constructor
     * 
     * @param bundle
     */
    public SFTPConnectionSpec(ResourceBundle bundle)
    {
        super(bundle);
        String prop = null;
        prop = bundle.getString(SFTPConnectionProperties.KNOWN_HOSTS.toString());
        this.setKnownHostsFilename(prop);
        prop = bundle.getString(SFTPConnectionProperties.MAX_RETRIES.toString());
        this.setMaxRetryAttempts(new Integer(prop));
        prop = bundle.getString(SFTPConnectionProperties.PRIVATE_KEY.toString());
        this.setPrivateKeyFilename(prop);
        prop = bundle.getString(SFTPConnectionProperties.USERNAME.toString());
        this.setUsername(prop);
        prop = bundle.getString(SFTPConnectionProperties.HOST.toString());
        this.setRemoteHostname(prop);
        prop = bundle.getString(SFTPConnectionProperties.PORT.toString());
        this.setRemotePort(new Integer(prop));
        prop = bundle.getString(SFTPConnectionProperties.CLEANUP_JOURNAL_ON_COMPLETE.toString());
        this.setCleanupJournalOnComplete(new Boolean(prop));
        prop = bundle.getString(SFTPConnectionProperties.POLLTIME.toString());
        if (prop != null)
        {
            this.setPollTime(new Integer(prop));
        }
    }

    /**
     * Getter for cleanupJournalOnComplete
     * 
     * @return cleanupJournalOnComplete
     */
    public Boolean getCleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
    }

    /**
     * Setter for cleanupJournalOnComplete
     * 
     * @param cleanupJournalOnComplete
     */
    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    /**
     * Get the host
     * 
     * @return the host
     */
    public String getRemoteHostname()
    {
        return remoteHostname;
    }

    /**
     * Set the host
     * 
     * @param remoteHostname - the host
     */
    public void setRemoteHostname(String remoteHostname)
    {
        this.remoteHostname = remoteHostname;
    }

    /**
     * Get the known hosts file name
     * 
     * @return the known hosts file name
     */
    public String getKnownHostsFilename()
    {
        return knownHostsFilename;
    }

    /**
     * Set the known hosts file name
     * 
     * @param knownHostsFilename
     */
    public void setKnownHostsFilename(String knownHostsFilename)
    {
        this.knownHostsFilename = knownHostsFilename;
    }

    /**
     * Get the max retry attempts
     * 
     * @return the max retry attempts
     */
    public Integer getMaxRetryAttempts()
    {
        return maxRetryAttempts;
    }

    /**
     * Set the max retry attempts
     * 
     * @param maxRetryAttempts
     */
    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    /**
     * Get the port
     * 
     * @return the port
     */
    public Integer getRemotePort()
    {
        return remotePort;
    }

    /**
     * Set the port
     * 
     * @param port - the port
     */
    public void setRemotePort(Integer port)
    {
        this.remotePort = port;
    }

    /**
     * Get the private key file name
     * 
     * @return the private key file name
     */
    public String getPrivateKeyFilename()
    {
        return privateKeyFilename;
    }

    /**
     * Set the private key file name
     * 
     * @param privateKeyFilename
     */
    public void setPrivateKeyFilename(String privateKeyFilename)
    {
        this.privateKeyFilename = privateKeyFilename;
    }

    /**
     * Get the user name
     * 
     * @return the user name
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Set the user name
     * 
     * @param username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Getter for pollTime
     * 
     * @return Integer
     */
    public Integer getPollTime()
    {
        return pollTime;
    }

    /**
     * Setter for pollTime
     * 
     * @param pollTime
     */
    public void setPollTime(Integer pollTime)
    {
        this.pollTime = pollTime;
    }

    /**
     * Setter for authentication order
     * 
     * @param preferredAuthentications
     */
    public void setPreferredAuthentications(String preferredAuthentications)
    {
        this.preferredAuthentications = preferredAuthentications;
    }

    /**
     * Getter for authentication order
     * 
     * @return String
     */
    public String getPreferredAuthentications()
    {
        return this.preferredAuthentications;
    }

    /**
     * Setter for the connection timeout
     * 
     * @param connectionTimeout
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Getter for the connection timeout
     * 
     * @return String
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * String representation of the ConnectionSpec
     */
    @Override
    public String toString()
    {
        String specAsString;
        specAsString = "CleanupJournalOnComplete:         [" + cleanupJournalOnComplete + "]\n"
                + "Client Id:                        [" + super.getClientID() + "]\n"
                + "Connection Timeout:               [" + connectionTimeout + "]\n"
                + "Host:                             [" + remoteHostname + "]\n"
                + "Known hosts file name:            [" + knownHostsFilename + "]\n"
                + "Maximum amount of retries:        [" + maxRetryAttempts + "]\n"
                + "Port:                             [" + remotePort + "]\n" 
                + "Poll time:                        [" + pollTime + "]\n" 
                + "Private key file name:            [" + privateKeyFilename + "]\n"
                + "Preferred authentication order:   [" + this.preferredAuthentications + "]\n"
                + "Username:                         [" + username + "]";
        return specAsString;
    }
}

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

import org.ikasan.connector.base.outbound.EISConnectionRequestInfo;

/**
 * SFTPConnectionRequestInfo provides SFTP EIS specific connection properties.
 * It gets passed into the SFTPManagedConnectionFactory
 *
 * This class itself gets populated by the client calling the connector
 *
 * Typically this is used to override the defaults provided by the ra.xml
 * file.
 *
 * @author Ikasan Development Team
 */
public class SFTPConnectionRequestInfo extends EISConnectionRequestInfo
{

    /** The SFTP host name */
    private String remoteHostname = null;

    /** The SFTP port */
    private Integer remotePort = null;

    /** The known hosts file name (fully qualified path) */
    private String knownHostsFilename = null;

    /** The maximum amount of retries */
    private Integer maxRetryAttempts = null;

    /** How often to poll for files */
    private Integer pollTime = null;

    /** Connection timeout in ms*/
    private Integer connectionTimeout = 0;

    /** The private key file name (fully qualified path) */
    private String privateKeyFilename = null;

    /** The username */
    private String username = null;

    /** Cleanup TransactionJournal on Complete */
    private Boolean cleanupJournalOnComplete;

    /** Authentication order*/
    private String preferredAuthentications = new String();

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    /////////////////////////////
    // Mandatory override methods
    /////////////////////////////

    @Override
    public boolean equals(Object object)
    {
        if(object instanceof SFTPConnectionRequestInfo)
        {
            SFTPConnectionRequestInfo cri = (SFTPConnectionRequestInfo)object;
            if(this.cleanupJournalOnComplete.equals(cri.cleanupJournalOnComplete()) &&
               this.remoteHostname.equalsIgnoreCase(cri.getRemoteHostname()) &&
               this.getClientID().equalsIgnoreCase(cri.getClientID()) &&
               this.knownHostsFilename.equalsIgnoreCase(knownHostsFilename) &&
               this.maxRetryAttempts.equals(cri.getMaxRetryAttempts()) &&
               this.pollTime.equals(cri.getPollRate()) &&
               this.remotePort.equals(cri.getRemotePort()) &&
               this.privateKeyFilename.equalsIgnoreCase(cri.getPrivateKeyFilename()) &&
               this.preferredAuthentications.equals(cri.getPreferredAuthentications()) &&
               this.connectionTimeout.equals(cri.getConnectionTimeout()) &&
               this.username.equals(cri.getUsername()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.cleanupJournalOnComplete.hashCode()
             + this.remoteHostname.hashCode()
             + this.getClientID().hashCode()
             + this.knownHostsFilename.hashCode()
             + this.maxRetryAttempts.hashCode()
             + this.pollTime.hashCode()
             + this.remotePort.hashCode()
             + this.privateKeyFilename.hashCode()
             + this.username.hashCode()
             + this.connectionTimeout.hashCode()
             + this.preferredAuthentications.hashCode();
    }

    //////////////////////////////////////////////////////////////////
    // Getters/Setters
    // properties provided via the ra.xml which are required to
    // establish the physical (managed) connection
    // These are defined by the ra.xml file
    //////////////////////////////////////////////////////////////////

    /**
     * Getter for cleanupJournalOnComplete
     * @return cleanupJournalOnComplete
     */
    public Boolean cleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
    }

    /**
     * Setter for cleanupJournalOnComplete
     * @param cleanupJournalOnComplete flag
     */
    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    /**
     * Get the remote host name
     * @return the remote host name
     */
    public String getRemoteHostname()
    {
        return remoteHostname;
    }

    /**
     * Set the remote host name
     * @param remoteHostname string
     */
    public void setRemoteHostname(String remoteHostname)
    {
        this.remoteHostname = remoteHostname;
    }

    /**
     * Get the known hosts file name
     * @return the known hosts file name
     */
    public String getKnownHostsFilename()
    {
        return knownHostsFilename;
    }

    /**
     * Set the known hosts file name
     * @param knownHostsFilename -
     */
    public void setKnownHostsFilename(String knownHostsFilename)
    {
        this.knownHostsFilename = knownHostsFilename;
    }

    /**
     * Get the max retry attempts
     * @return the max retry attempts
     */
    public Integer getMaxRetryAttempts()
    {
        return maxRetryAttempts;
    }

    /**
     * Set the max retry attempts
     * @param maxRetryAttempts -
     */
    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    /**
     * Get the poll rate
     * @return poll rate
     */
    public Integer getPollRate()
    {
        return pollTime;
    }

    /**
     * Set the poll rate
     * @param pollTime -
     */
    public void setPollTime(Integer pollTime)
    {
        this.pollTime = pollTime;
    }

    /**
     * Get the remote port
     * @return remotePort
     */
    public Integer getRemotePort()
    {
        return remotePort;
    }

    /**
     * Set the remote port
     * @param remotePort number
     */
    public void setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
    }

    /**
     * Get the private key file name
     * @return the private key file name
     */
    public String getPrivateKeyFilename()
    {
        return privateKeyFilename;
    }

    /**
     * Set the private key file name
     * @param privateKeyFilename -
     */
    public void setPrivateKeyFilename(String privateKeyFilename)
    {
        this.privateKeyFilename = privateKeyFilename;
    }

    /**
     * Get the user name
     * @return the user name
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Set the user name
     * @param username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Set the authentication order
     * 
     * @param preferredAuthentications 
     */
    public void setPreferredAuthentications(String preferredAuthentications)
    {
        this.preferredAuthentications = preferredAuthentications;
    }

    /**
     * Get the authentication order
     *@return String
     */
    public String getPreferredAuthentications()
    {
        return this.preferredAuthentications;
    }

    /**
     * Set the connection timeout
     * @param connectionTimeout
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get the socket connection timeout
     * @return String 
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }
    
    /**
     * String representation of the ConnectionRequestInfo
     */
    @Override
    public String toString()
    {
        String requestInfoAsString;

        requestInfoAsString =
              "Cleanup jounral on complete:      [" + cleanupJournalOnComplete + "]\n"
            + "Client Id:                        [" + super.getClientID() + "]\n"
            + "Connection Timeout:               [" + connectionTimeout + "]\n"
            + "Remote Host:                      [" + remoteHostname + "]\n"
            + "Known hosts file name:            [" + knownHostsFilename + "]\n"
            + "Maximum amount of retries:        [" + maxRetryAttempts + "]\n"
            + "Remote Port:                      [" + remotePort + "]\n"
            + "Poll time:                        [" + pollTime + "]\n"
            + "Private key file name:            [" + privateKeyFilename + "]\n"
            + "Preferred authentication order:   [" + this.preferredAuthentications + "]\n"
            + "Username:                         [" + username + "]";

            return requestInfoAsString;
    }

}

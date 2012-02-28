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
package org.ikasan.connector.ftp.outbound;

import org.ikasan.connector.base.outbound.EISConnectionRequestInfo;

/**
 * FTPConnectionRequestInfo provides FTP EIS specific connection properties. It
 * gets passed into the FTPManagedConnectionFactory
 * 
 * This class itself gets populated by the client calling the connector
 * 
 * Typically this is used to override the defaults provided by the ra.xml file.
 * 
 * @author Ikasan Development Team
 */
public class FTPConnectionRequestInfo extends EISConnectionRequestInfo
{
    /** Whether it is active transfer mode */
    private Boolean active;

    /** Cleanup TransactionJournal on Complete */
    private Boolean cleanupJournalOnComplete;

    /** The remote FTP host name */
    private String remoteHostname = null;

    /** The maximum amount of retries */
    private Integer maxRetryAttempts = null;

    /** The password */
    private String password = null;

    /** How often to poll for files */
    private Integer pollTime = null;

    /** The remote FTP port */
    private Integer remotePort = null;

    /** The username */
    private String username = null;
    
    /** The systemKey */
    private String systemKey = new String();

    /** The connection timeout */
    private Integer connectionTimeout = 0;

    /** The data timeout */
    private Integer dataTimeout = 0;

    /** The socket timeout */
    private Integer socketTimeout = 0;

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    // ///////////////////////////
    // Mandatory override methods
    // ///////////////////////////

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof FTPConnectionRequestInfo)
        {
            FTPConnectionRequestInfo cri = (FTPConnectionRequestInfo) object;
            if (this.active.equals(cri.getActive()) && 
                this.getClientID().equalsIgnoreCase(cri.getClientID()) &&
                this.cleanupJournalOnComplete.equals(cri.cleanupJournalOnComplete()) &&
                this.remoteHostname.equalsIgnoreCase(cri.getRemoteHostname()) &&
                this.maxRetryAttempts.equals(cri.getMaxRetryAttempts()) &&
                this.password.equals(cri.getPassword()) && 
                this.pollTime.equals(cri.getPollTime())&&
                this.remotePort.equals(cri.getRemotePort()) &&
                this.username.equals(cri.getUsername())&&
                this.connectionTimeout.equals(cri.getConnectionTimeout()) &&
                this.dataTimeout.equals(cri.getDataTimeout()) &&
                this.socketTimeout.equals(cri.getSocketTimeout()) &&
                this.systemKey.equals(cri.getSystemKey()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hashCode = this.active.hashCode() 
        + this.cleanupJournalOnComplete.hashCode() 
        + this.remoteHostname.hashCode()
        + this.getClientID().hashCode()
        + this.maxRetryAttempts.hashCode() 
        + this.pollTime.hashCode() 
        + this.password.hashCode()
        + this.remotePort.hashCode() 
        + this.username.hashCode()
        + this.connectionTimeout.hashCode()
        + this.dataTimeout.hashCode()
        + this.socketTimeout.hashCode()
        + this.systemKey.hashCode();
        return hashCode;
    }

    // ////////////////////////////////////////////////////////////////
    // Getters/Setters
    // properties provided via the ra.xml which are required to
    // establish the physical (managed) connection
    // These are defined by the ra.xml file
    // ////////////////////////////////////////////////////////////////
    /**
     * Get active
     * 
     * @return active
     */
    public Boolean getActive()
    {
        return active;
    }

    /**
     * Set active
     * 
     * @param active New value for <code>active</code> flag.
     */
    public void setActive(Boolean active)
    {
        this.active = active;
    }

    /**
     * Getter for cleanupJournalOnComplete
     * 
     * @return cleanupJournalOnComplete
     */
    public Boolean cleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
    }

    /**
     * Setter for cleanupJournalOnComplete
     * 
     * @param cleanupJournalOnComplete New value for <code>cleanupJournalOnComplete</code> parameter flag.
     */
    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    /**
     * Get the host name
     * 
     * @return the host name
     */
    public String getRemoteHostname()
    {
        return remoteHostname;
    }

    /**
     * Set the host name
     * 
     * @param remoteHostname New value for <code>hostname</code>.
     */
    public void setRemoteHostname(String remoteHostname)
    {
        this.remoteHostname = remoteHostname;
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
     * @param maxRetryAttempts New value for <code>maxRetryAttempts</code>.
     */
    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    /**
     * Get the password
     * 
     * @return password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Set the password
     * 
     * @param password New value for <code>password</code>.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Get the poll rate
     * 
     * @return poll rate
     */
    public Integer getPollTime()
    {
        return pollTime;
    }

    /**
     * Set the poll rate
     * 
     * @param pollTime New value for <code>pollTime</code>.
     */
    public void setPollTime(Integer pollTime)
    {
        this.pollTime = pollTime;
    }

    /**
     * Get the port
     * 
     * @return port
     */
    public Integer getRemotePort()
    {
        return remotePort;
    }

    /**
     * Set the port
     * 
     * @param remotePort New value for <code>port</code>.
     */
    public void setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
    }

    /**
     * Get the username
     * 
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Set the username
     * 
     * @param username New value for <code>username</code>.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    /**
     * Get the systemKey
     * 
     * @return the systemKey
     */
    public String getSystemKey()
    {
        return systemKey;
    }

    /**
     * Set the systemKey
     * 
     * @param systemKey The system key to set
     */
    public void setSystemKey(String systemKey)
    {
        this.systemKey = systemKey;
    }

    /**
     * Set the connection timeout
     * @param connectionTimeout The connection timeout to set 
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get the connection timeout
     * @return int - connectionT
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * Set the data timeout
     * @param dataTimeout The data timeout to set 
     */
    public void setDataTimeout(Integer dataTimeout)
    {
        this.dataTimeout = dataTimeout;
    }

    /**
     * Get the data timeout
     * @return Integer - dataTimeout
     */
    public Integer getDataTimeout()
    {
        return this.dataTimeout;
    }

    /**
     * Set the socket timeout
     * @param socketTimeout The connection timeout to set 
     */
    public void setSocketTimeout(Integer socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }

    /**
     * Get the connection timeout
     * @return Integer - socketTimeout
     */
    public Integer getSocketTimeout()
    {
        return this.socketTimeout;
    }

    /**
     * String representation of the ConnectionRequestInfo
     */
    @Override
    public String toString()
    {
        String requestInfoAsString;

        requestInfoAsString = 
                  "Active:                           [" + this.active + "]\n"
                + "Cleanup journal on complete:      [" + this.cleanupJournalOnComplete + "]\n"
                + "Client Id:                        [" + super.getClientID() + "]\n"
                + "Remote Host:                      [" + this.remoteHostname + "]\n"
                + "Maximum amount of retries:        [" + this.maxRetryAttempts + "]\n"
                + "Connection timeout:               [" + this.connectionTimeout + "]\n"
                + "Data connection Timeout:          [" + this.dataTimeout + "]\n"
                + "Socket connection Timeout:        [" + this.socketTimeout + "]\n"
                + "Password:                         [" + this.password + "]\n"
                + "Remote Port:                      [" + this.remotePort + "]\n"
                + "Poll Time                         [" + this.pollTime + "]\n"
                + "Username:                         [" + this.username + "]\n"
                + "SystemKey:                        [" + this.systemKey + "]";

        return requestInfoAsString;
    }
}

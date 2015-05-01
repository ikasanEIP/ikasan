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

    // FTPS connection details
    private Boolean isFTPS = false;
    private Integer ftpsPort = 21;
    private String  ftpsProtocol = "SSL";
    private Boolean ftpsIsImplicit = false;
    private String  ftpsKeyStoreFilePath = "";
    private String  ftpsKeyStoreFilePassword = "";

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    // ///////////////////////////
    // Mandatory override methods
    // ///////////////////////////


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FTPConnectionRequestInfo that = (FTPConnectionRequestInfo) o;

        if (active != null ? !active.equals(that.active) : that.active != null) return false;
        if (cleanupJournalOnComplete != null ? !cleanupJournalOnComplete.equals(that.cleanupJournalOnComplete) : that.cleanupJournalOnComplete != null)
            return false;
        if (connectionTimeout != null ? !connectionTimeout.equals(that.connectionTimeout) : that.connectionTimeout != null)
            return false;
        if (dataTimeout != null ? !dataTimeout.equals(that.dataTimeout) : that.dataTimeout != null) return false;
        if (ftpsIsImplicit != null ? !ftpsIsImplicit.equals(that.ftpsIsImplicit) : that.ftpsIsImplicit != null)
            return false;
        if (ftpsKeyStoreFilePassword != null ? !ftpsKeyStoreFilePassword.equals(that.ftpsKeyStoreFilePassword) : that.ftpsKeyStoreFilePassword != null)
            return false;
        if (ftpsKeyStoreFilePath != null ? !ftpsKeyStoreFilePath.equals(that.ftpsKeyStoreFilePath) : that.ftpsKeyStoreFilePath != null)
            return false;
        if (ftpsPort != null ? !ftpsPort.equals(that.ftpsPort) : that.ftpsPort != null) return false;
        if (ftpsProtocol != null ? !ftpsProtocol.equals(that.ftpsProtocol) : that.ftpsProtocol != null) return false;
        if (isFTPS != null ? !isFTPS.equals(that.isFTPS) : that.isFTPS != null) return false;
        if (maxRetryAttempts != null ? !maxRetryAttempts.equals(that.maxRetryAttempts) : that.maxRetryAttempts != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (pollTime != null ? !pollTime.equals(that.pollTime) : that.pollTime != null) return false;
        if (remoteHostname != null ? !remoteHostname.equals(that.remoteHostname) : that.remoteHostname != null)
            return false;
        if (remotePort != null ? !remotePort.equals(that.remotePort) : that.remotePort != null) return false;
        if (socketTimeout != null ? !socketTimeout.equals(that.socketTimeout) : that.socketTimeout != null)
            return false;
        if (systemKey != null ? !systemKey.equals(that.systemKey) : that.systemKey != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = active != null ? active.hashCode() : 0;
        result = 31 * result + (cleanupJournalOnComplete != null ? cleanupJournalOnComplete.hashCode() : 0);
        result = 31 * result + (remoteHostname != null ? remoteHostname.hashCode() : 0);
        result = 31 * result + (maxRetryAttempts != null ? maxRetryAttempts.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (pollTime != null ? pollTime.hashCode() : 0);
        result = 31 * result + (remotePort != null ? remotePort.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (systemKey != null ? systemKey.hashCode() : 0);
        result = 31 * result + (connectionTimeout != null ? connectionTimeout.hashCode() : 0);
        result = 31 * result + (dataTimeout != null ? dataTimeout.hashCode() : 0);
        result = 31 * result + (socketTimeout != null ? socketTimeout.hashCode() : 0);
        result = 31 * result + (isFTPS != null ? isFTPS.hashCode() : 0);
        result = 31 * result + (ftpsPort != null ? ftpsPort.hashCode() : 0);
        result = 31 * result + (ftpsProtocol != null ? ftpsProtocol.hashCode() : 0);
        result = 31 * result + (ftpsIsImplicit != null ? ftpsIsImplicit.hashCode() : 0);
        result = 31 * result + (ftpsKeyStoreFilePath != null ? ftpsKeyStoreFilePath.hashCode() : 0);
        result = 31 * result + (ftpsKeyStoreFilePassword != null ? ftpsKeyStoreFilePassword.hashCode() : 0);
        return result;
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

    public Boolean getIsFTPS() {
        return isFTPS;
    }

    public void setIsFTPS(Boolean isFTPS) {
        this.isFTPS = isFTPS;
    }

    public Integer getFtpsPort() {
        return ftpsPort;
    }

    public void setFtpsPort(Integer ftpsPort) {
        this.ftpsPort = ftpsPort;
    }

    public String getFtpsProtocol() {
        return ftpsProtocol;
    }

    public void setFtpsProtocol(String ftpsProtocol) {
        this.ftpsProtocol = ftpsProtocol;
    }

    public Boolean getFtpsIsImplicit() {
        return ftpsIsImplicit;
    }

    public void setFtpsIsImplicit(Boolean ftpsIsImplicit) {
        this.ftpsIsImplicit = ftpsIsImplicit;
    }

    public String getFtpsKeyStoreFilePath() {
        return ftpsKeyStoreFilePath;
    }

    public void setFtpsKeyStoreFilePath(String ftpsKeyStoreFilePath) {
        this.ftpsKeyStoreFilePath = ftpsKeyStoreFilePath;
    }

    public String getFtpsKeyStoreFilePassword() {
        return ftpsKeyStoreFilePassword;
    }

    public void setFtpsKeyStoreFilePassword(String ftpsKeyStoreFilePassword) {
        this.ftpsKeyStoreFilePassword = ftpsKeyStoreFilePassword;
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
                + "SystemKey:                        [" + this.systemKey + "]\n"
                + "Is FTPS                           [" + this.isFTPS + "]\n"
                + "FTPS Protocol                     [" + this.ftpsProtocol + "]\n"
                + "FTPS Port                         [" + this.ftpsPort + "]\n"
                + "FTPS isImplicit                   [" + this.ftpsIsImplicit + "]\n"
                + "FTPS keyStoreFilePath             [" + this.ftpsKeyStoreFilePath + "]\n"
                + "FTPS keyStoreFilePassword         [" + this.ftpsKeyStoreFilePassword + "]";

        return requestInfoAsString;
    }
}

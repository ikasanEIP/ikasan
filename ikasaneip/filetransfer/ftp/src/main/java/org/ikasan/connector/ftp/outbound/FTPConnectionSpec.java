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

import org.ikasan.connector.base.outbound.EISConnectionSpec;

/**
 * This is an class representing the connection specific application
 * properties passed to the getConnection method.
 *
 * @author Ikasan Development Team
 */
public class FTPConnectionSpec extends EISConnectionSpec
{
    /** Whether it is active transfer mode - default False */
    private Boolean active = Boolean.FALSE;

    /** Cleanup Transaction Journal On Complete - default True */
    private Boolean cleanupJournalOnComplete = Boolean.TRUE;

    /** The Remote Host */
    private String remoteHostname = null;

    /** The maximum amount of retries - defaults to 1 */
    private Integer maxRetryAttempts = Integer.valueOf(1);

    /** The Password */
    private String password = null;

    /** The Remote Port */
    private Integer remotePort = Integer.valueOf(0);

    /** The username */
    private String username = null;

    /** Initial poll time for source system only */
    private Integer pollTime = Integer.valueOf(0);

    /** Connection timeout, default is 0 (infinite) */
    private Integer connectionTimeout = Integer.valueOf(0);

    /** Data connection timeout, default is 0 (infinite) */
    private Integer dataTimeout = Integer.valueOf(0);
    
    /** Socket connection timeout, default is 0 (infinite) */
    private Integer socketTimeout = Integer.valueOf(0);

    /** The systemKey */
    private String systemKey = new String();

    // FTPS connection details
    private Boolean isFTPS = false;
    private Integer ftpsPort = 21;
    private String  ftpsProtocol = "SSL";
    private Boolean ftpsIsImplicit = false;
    private String  ftpsKeyStoreFilePath = "";
    private String  ftpsKeyStoreFilePassword = "";

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    /** Default Constructor. */
    public FTPConnectionSpec()
    {
        // empty
    }
    //////////////////////////////////////////////////////////////////
    // Getters/Setters
    //////////////////////////////////////////////////////////////////

    /**
     * Getter for active
     * @return active
     */
    public Boolean getActive()
    {
        return this.active;
    }

    /**
     * Set active
     * @param active active/passive mode.
     */
    public void setActive(Boolean active)
    {
        this.active = active;
    }

    /**
     * Getter for cleanupJournalOnComplete
     * @return cleanupJournalOnComplete
     */
    public Boolean getCleanupJournalOnComplete()
    {
        return this.cleanupJournalOnComplete;
    }

    /**
     * Setter for cleanupJournalOnComplete
     * @param cleanupJournalOnComplete Flag to clean transaction journal after complete.
     */
    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    /**
     * Get the host
     * @return the host
     */
    public String getRemoteHostname()
    {
        return this.remoteHostname;
    }

    /**
     * Set the host
     * @param remoteHostname - the host
     */
    public void setRemoteHostname(String remoteHostname)
    {
        this.remoteHostname = remoteHostname;
    }

    /**
     * Get the max retry attempts
     * @return the max retry attempts
     */
    public Integer getMaxRetryAttempts()
    {
        return this.maxRetryAttempts;
    }

    /**
     * Set the max retry attempts
     * @param maxRetryAttempts Maximum connection attempts before failure.
     */
    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    /**
     * Get the password
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Set the password
     * @param password User password for FTP host.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Gets remote port number.
     * @return <code>remotePort</code>
     */
    public Integer getRemotePort()
    {
        return this.remotePort;
    }

    /**
     * Sets the value of <code>remotePort</code>.
     * @param remotePort The remote port number.
     */
    public void setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
    }

    /**
     * Get the username
     * @return the username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * Set the username
     * @param username User with privilege to connect to FTP host.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Getter for pollTime
     * @return Integer
     */
    public Integer getPollTime()
    {
        return this.pollTime;
    }

    /**
     * Setter for pollTime
     * @param pollTime Initial polling time
     */
    public void setPollTime(Integer pollTime )
    {
        this.pollTime = pollTime;
    }
    
    /**
     * Getter for systemKey
     * @return systemKey
     */
    public String getSystemKey()
    {
        return this.systemKey;
    }

    /**
     * Setter for systemKey
     * @param systemKey The system key to set
     */
    public void setSystemKey(String systemKey )
    {
        this.systemKey = systemKey;
    }

    /**
     * Get connection timeout
     * @return Integer - connectionTimeout
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * Set connection timeout
     * @param connectionTimeout -
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get data connection timeout
     * @return Integer - dataTimeout
     */
    public Integer getDataTimeout()
    {
        return this.dataTimeout;
    }

    /**
     * Set data timeout 
     * @param dataTimeout The data timeout in ms
     */
    public void setDataTimeout(Integer dataTimeout)
    {
        this.dataTimeout = dataTimeout;
    }

    /**
     * Get socket connection timeout
     * @return Integer - socketTimeout
     */
    public Integer getSocketTimeout()
    {
        return this.socketTimeout;
    }

    /**
     * Set socket timeout 
     * @param socketTimeout The socket timeout in ms
     */
    public void setSocketTimeout(Integer socketTimeout)
    {
        this.socketTimeout = socketTimeout;
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
     * String representation of the ConnectionSpec
     */
    @Override
    public String toString()
    {
        String specAsString;

        specAsString =
              "Active FTP mode:                  [" + this.active + "]\n"
            + "CleanupJournalOnComplete:         [" + this.cleanupJournalOnComplete + "]\n"
            + "Client Id:                        [" + super.getClientID() + "]\n"
            + "Remote Host:                      [" + this.remoteHostname + "]\n"
            + "Remote Port:                      [" + this.remotePort + "]\n"
            + "Connection Timeout:                [" + this.connectionTimeout + "]\n"
            + "Data connection Timeout:          [" + this.dataTimeout + "]\n"
            + "Socket connection Timeout:        [" + this.socketTimeout + "]\n"
            + "Maximum amount of retries:        [" + this.maxRetryAttempts + "]\n"
            + "Password:                         [" + this.password + "]\n"
            + "Poll Time                         [" + this.pollTime + "]\n"
            + "System Key                        [" + this.systemKey + "]\n"
            + "Username:                         [" + this.username + "]\n"
            + "Is FTPS                           [" + this.isFTPS + "]\n"
            + "FTPS Protocol                     [" + this.ftpsProtocol + "]\n"
            + "FTPS Port                         [" + this.ftpsPort + "]\n"
            + "FTPS isImplicit                   [" + this.ftpsIsImplicit + "]\n"
            + "FTPS keyStoreFilePath             [" + this.ftpsKeyStoreFilePath + "]\n"
            + "FTPS keyStoreFilePassword         [" + this.ftpsKeyStoreFilePassword + "]";

        return specAsString;
    }
}

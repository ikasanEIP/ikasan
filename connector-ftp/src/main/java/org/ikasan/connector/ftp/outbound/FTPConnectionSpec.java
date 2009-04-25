/*
 * $Id: FTPConnectionSpec.java 16785 2009-04-24 10:56:05Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-ftp/src/main/java/org/ikasan/connector/ftp/outbound/FTPConnectionSpec.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany
 * or see the FSF site: http://www.fsfeurope.org/.
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

    /** The maximum amount of retries - default none */
    private Integer maxRetryAttempts = 0;

    /** The Password */
    private String password = null;

    /** The Remote Port */
    private Integer remotePort = 0;

    /** The username */
    private String username = null;

    /** Initial poll time for source system only */
    private Integer pollTime = 0;

    /** Connection timeout, default is 0 (infinite) */
    private Integer connectionTimeout = 0;

    /** Data connection timeout, default is 0 (infinite) */
    private Integer dataTimeout = 0;
    
    /** Socket connection timeout, default is 0 (infinite) */
    private Integer socketTimeout = 0;

    /** The systemKey */
    private String systemKey = new String();

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
        return active;
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
        return cleanupJournalOnComplete;
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
        return remoteHostname;
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
        return maxRetryAttempts;
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
        return password;
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
        return remotePort;
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
        return username;
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
        return pollTime;
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
        return systemKey;
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
            + "Connetion Timeout:                [" + this.connectionTimeout + "]\n"
            + "Data connection Timeout:          [" + this.dataTimeout + "]\n"
            + "Socket connection Timeout:        [" + this.socketTimeout + "]\n"
            + "Maximum amount of retries:        [" + this.maxRetryAttempts + "]\n"
            + "Password:                         [" + this.password + "]\n"
            + "Poll Time                         [" + this.pollTime + "]\n"
            + "System Key                        [" + this.systemKey + "]\n"
            + "Username:                         [" + this.username + "]";

        return specAsString;
    }
}

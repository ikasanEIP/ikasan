/*
 * $Id: SFTPConnectionRequestInfo.java 16794 2009-04-24 13:27:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-sftp/src/main/java/org/ikasan/connector/sftp/outbound/SFTPConnectionRequestInfo.java $
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

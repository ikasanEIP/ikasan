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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.security.auth.Subject;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonEnvironment;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalImpl;
import org.ikasan.connector.base.outbound.EISManagedConnectionFactory;
import org.ikasan.connector.basefiletransfer.DataAccessUtil;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;

/**
 * This class is the factory class for obtaining physical connections to the FTP EIS. The attributes are set by the
 * default attributes in the resource adapter's deployment descriptor (ra.xml) and can be overridden by client supplied
 * properties (CRI).
 * 
 * Each connection produced from this factory is a handle to the actual physical connection (ManagedConnection) to the
 * underlying EIS.
 * 
 * On start up, the Application server first of all calls all of the setter methods (populating them with the values
 * from the ra.xml file) and then calls createConnectionFactory(ConnectionManager)
 * 
 * NOTE: Each <config-property-name> element in ra.xml must have a matching private variable name in this class
 * 
 * NOTE: Defaults of null are dealt with by calling classes
 * 
 * NOTE: Setters are initially called by the App Server
 * 
 * TODO Max Retry attempts is here because our 3rd party library needs to have the value set for when openSession is
 * called on the FTPManagedConnection
 * 
 * @author Ikasan Development Team
 */
public class FTPManagedConnectionFactory extends EISManagedConnectionFactory
{
    /** Whether the FTP mode is active (passive by default) */
    private boolean active = false;

    /** Whether we clean up the journal after a complete or not */
    private boolean cleanupJournalOnComplete = false;

    /** The FTP host name */
    private String remoteHostname = null;

    /** The Local host name */
    private String localHostname = null;

    /** Maximum retry attempts, can only be set by the CRI, not the ra.xml */
    private Integer maxRetryAttempts = null;

    /** The password */
    private String password = null;

    /** The remote FTP port */
    private Integer remotePort = null;

    /** The username */
    private String username = null;

    /**
     * FTP Client systemKey, if set, expected to be in the FTPClientConfig.SYST_* range
     */
    private String systemKey = null;

    /** Connection timeout in ms */
    private Integer connectionTimeout;

    /** Data connection timeout in ms */
    private Integer dataTimeout;

    /** Socket connection timeout in ms */
    private Integer socketTimeout;

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(FTPManagedConnectionFactory.class);

    /** Journal for logging activity of this connector */
    private TransactionJournal transactionJournal = null;

    /**
     * Create the connection factory with no connection manager, e.g. This is the version called when not invoked by the
     * Application Server
     */
    @Override
    public Object createConnectionFactory()
    {
        logger.debug("Called createConnectionFactory()");
        return new FTPConnectionFactory(this, null);
    }

    /**
     * This version of createConnectionFactory is invoked by the Application Server by passing its own implemented
     * version of connection manager.
     */
    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager)
    {
        logger.debug("Called createConnectionFactory(connectionManager)");
        return new FTPConnectionFactory(this, connectionManager);
    }

    /**
     * This can be called in two ways, but is initiated by a client need for a Connection.
     * 
     * In our case FTPConnectionManager's allocateConnection calls this method. (although the Application Server's
     * ConnectionManager can also in theory call this)
     */
    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cri)
            throws ResourceException
    {
        logger.debug("Called createManagedConnection");
        // Create the new Managed Connection
        FTPManagedConnection ftpManagedConnection = new FTPManagedConnection(this, (FTPConnectionRequestInfo) cri);
        ftpManagedConnection.setTransactionJournal(getTransactionJournal());
        // Open a session on the managed connection
        ftpManagedConnection.openSession();
        // Return the managed connection (with an open session)
        return ftpManagedConnection;
    }

    /**
     * This method is called by the application server when the client asks for a new connection. The application server
     * passes in a Set of all the active managed connections, and this object must pick one that is currently handling a
     * physical connection that can be shared to support the new client request. Typically this sharing will be allowed
     * if the security attributes and properties of the new request match an existing physical connection.
     * 
     * If nothing is available, the method must return null, so that the application server knows it has to create a new
     * physical connection.
     */
    @SuppressWarnings("unchecked")
    @Override
    public ManagedConnection matchManagedConnections(@SuppressWarnings("rawtypes") Set connections, Subject subject,
            ConnectionRequestInfo info)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Called matchManagedConnection()");
            logger.debug("Number of connections considered = [" + connections.size() + "].");
        }
        FTPConnectionRequestInfo fcri = (FTPConnectionRequestInfo) info;
        Iterator<?> it = connections.iterator();
        while (it.hasNext())
        {
            Object obj = it.next();
            logger.debug("Considering object " + obj.getClass().getName());
            if (obj instanceof FTPManagedConnection)
            {
                logger.debug("Object is a FTPManagedConnection instance.");
                FTPManagedConnection fmc = (FTPManagedConnection) obj;
                logger.debug("Connection considered [" + fmc + "].");
                FTPConnectionRequestInfo currentFcri = fmc.getConnectionRequestInfo();
                // TODO We may also want to check that the fmcf is equal to
                // 'this'.
                if (currentFcri.equals(fcri))
                {
                    logger.debug("Found matched Connection.");
                    // This should never occur if the track-connection-by-tx
                    // property is set in your connection factory ds file
                    if (fmc.transactionInProgress())
                    {
                        logger.warn("Managed Connection already involved in transaction.");
                        logger.warn("Connection = [" + fmc + "] [" + fmc.getConnectionState().getDescription() + "].");
                    }
                    else
                    {
                        return fmc;
                    }
                }
            }
        }
        logger.debug("No matched Connection for object (or at least none that weren't already involved in a transaction).");
        return null;
    }

    /** Generate a hash code */
    @Override
    public int hashCode()
    {
        logger.debug("Called hashCode()");
        // If the hostname is null then this is not a valid, so return 0
        if (this.remoteHostname == null)
        {
            return 0;
        }
        return this.clientID.hashCode() + this.remoteHostname.hashCode() + this.password.hashCode()
                + this.remotePort.hashCode() + this.username.hashCode();
    }

    /**
     * FTP specific equality implementation. This is used by the matchManagedConnections when called by the Application
     * Server to determine if the incoming object is the same as the instantiated object.
     * 
     * The check currently consists of host, port and username equality
     */
    @Override
    public boolean equals(Object object)
    {
        logger.debug("Called equals");
        // Valid object check
        if (object == null)
        {
            logger.debug("Object is null. Returning [false].");
            return false;
        }
        if (object instanceof FTPManagedConnectionFactory)
        {
            logger.debug("Object is a FTPManagedConnectionFactory");
            FTPManagedConnectionFactory fmcf = (FTPManagedConnectionFactory) object;
            return compareFMCF(fmcf);
        }
        // default else
        logger.debug("Object is not valid, returning [false].");
        return false;
    }

    /**
     * Return true if the FMCF matches this connection
     * 
     * @param fmcf Incoming <code>ManagedConnectionFactory</code> instance from application server.
     * @return true if the FMCF matches this connection
     */
    private boolean compareFMCF(FTPManagedConnectionFactory fmcf)
    {
        if (this.remoteHostname == null || this.password == null || this.remotePort == null || this.username == null
                || this.clientID == null)
        {
            logger.warn("One of the mandatory managed connection factory variables is null.");
            logger.warn("Hostname = [" + this.remoteHostname + "]\n" + "Password = [" + this.password + "]\n"
                    + "Port = [" + this.remotePort + "]\n" + "Username = [" + this.username + "]\n" + "ClientID = ["
                    + this.clientID + "].\n");
            return false;
        }
        // Connection specific properties check
        if (this.remoteHostname.equalsIgnoreCase(fmcf.remoteHostname) && this.password == fmcf.password
                && this.remotePort == fmcf.remotePort && this.username.equals(fmcf.username)
                && this.clientID.equals(fmcf.clientID))
        {
            logger.debug("Object is equal. Returning [true].");
            return true;
        }
        // Default else
        logger.debug("Object is not equal. Returning [false].");
        return false;
    }

    /**
     * Lazily instantiates the TransactionJournal
     * 
     * @return TransactionJournal
     */
    protected TransactionJournal getTransactionJournal()
    {
        if (transactionJournal == null)
        {
            TransactionalResourceCommandDAO dao = DataAccessUtil.getTransactionalResourceCommandDAO();
            FileChunkDao fileChunkDao = DataAccessUtil.getFileChunkDao();
            Map<String, Object> beanFactory = new HashMap<String, Object>();
            beanFactory.put("fileChunkDao", fileChunkDao);
            transactionJournal = new TransactionJournalImpl(dao, clientID, beanFactory);
        }
        return transactionJournal;
    }

    /**
     * Returns whether or not this is an active FTP transfer. There is no corresponding setter as we only want this to
     * be a client property, not a ra.xml property
     * 
     * @return true if this is an active transfer
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Returns whether or not we clean up the journal on commit. There is no corresponding setter as we only want this
     * to be a client property, not a ra.xml property
     * 
     * @return true if we clean up the journal on commit
     */
    public boolean isCleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
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
     * @param remoteHostname remote host name
     */
    public void setRemoteHostname(String remoteHostname)
    {
        logger.debug("ra.xml setting remote hostname to: [" + remoteHostname + "]");
        this.remoteHostname = remoteHostname;
    }

    /**
     * Get the local host name
     * 
     * @return local host name
     */
    public String getLocalHostname()
    {
        return localHostname;
    }

    /**
     * Set the systemKey
     * 
     * @param systemKey system key
     */
    public void setSystemKey(String systemKey)
    {
        logger.debug("ra.xml setting systemKey to: [" + systemKey + "]");
        this.systemKey = systemKey;
    }

    /**
     * Get the systemKey
     * 
     * @return systemKey
     */
    public String getSystemKey()
    {
        return systemKey;
    }

    /**
     * Get the max retry attempts, as open session requires it. There is no corresponding setter as we only want this to
     * be a client property, not a ra.xml property
     * 
     * @return maxRetryAttempts
     */
    public Integer getMaxRetryAttempts()
    {
        return maxRetryAttempts;
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
     * @param password password
     */
    public void setPassword(String password)
    {
        logger.debug("ra.xml setting password to: [" + password + "]");
        this.password = password;
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
     * @param remotePort The remote port
     */
    public void setRemotePort(Integer remotePort)
    {
        logger.debug("ra.xml setting port to: [" + remotePort + "]");
        this.remotePort = remotePort;
    }

    /**
     * Set the local host name
     * 
     * @param rtLocalHost The local host name
     */
    public void setLocalHostname(String rtLocalHost)
    {
        CommonEnvironment env = new org.ikasan.common.util.Env();
        String localHost = env.expandEnvVar(rtLocalHost);
        logger.debug("Setting localhost to [" + localHost + "].");
        if (localHost != null && localHost.length() > 0)
        {
            this.localHostname = localHost;
        }
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
     * @param username The user name
     */
    public void setUsername(String username)
    {
        logger.debug("ra.xml setting username to: [" + username + "]");
        this.username = username;
    }

    /**
     * Get the connection timeout
     * 
     * @return Integer - connectionTimeout
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * Sets the connection timeout
     * 
     * @param connectionTimeout The connection timeout
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get the data connection timeout
     * 
     * @return Integer - dataTimeout
     */
    public Integer getDataTimeout()
    {
        return this.dataTimeout;
    }

    /**
     * Sets the data connection timeout
     * 
     * @param dataTimeout The data timeout
     */
    public void setDataTimeout(Integer dataTimeout)
    {
        this.dataTimeout = dataTimeout;
    }

    /**
     * Get the socket timeout
     * 
     * @return Integer - socketTimeout
     */
    public Integer getSocketTimeout()
    {
        return this.socketTimeout;
    }

    /**
     * Sets the socket timeout
     * 
     * @param socketTimeout The socket timeout
     */
    public void setSocketTimeout(Integer socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }
}

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
 * This class is the factory class for obtaining physical connections to the SFTP EIS. The attributes are set by the
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
 * called on the SFTPManagedConnection. We need to extract that at some stage
 * 
 * TODO When everything is happy, reduced some logging back to debug level.
 * 
 * @author Ikasan Development Team
 */
public class SFTPManagedConnectionFactory extends EISManagedConnectionFactory
{
    /** Whether we clean up the journal after a complete or not (commit, rollback etc) */
    private boolean cleanupJournalOnComplete = false;

    /** The remote SFTP host name */
    private String remoteHostname = null;

    /** The local host name */
    private String localHostname = null;

    /** The known hosts file name (fully qualified path) */
    private String knownHostsFilename = null;

    /** Maximum retry attempts, can only be set by the CRI, not the ra.xml */
    private Integer maxRetryAttempts = null;

    /** The remote SFTP port */
    private Integer remotePort = null;

    /** The private key file name (fully qualified path) */
    private String privateKeyFilename = null;

    /** Connection timeout in milliseconds */
    private Integer connectionTimeout;

    /** The user name */
    private String username = null;

    /** authentication order */
    private String preferredAuthentications = null;

    /** Default serial version uid */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(SFTPManagedConnectionFactory.class);

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
        return new SFTPConnectionFactory(this, null);
    }

    /**
     * This version of createConnectionFactory is invoked by the Application Server by passing its own implemented
     * version of connection manager.
     */
    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager)
    {
        logger.debug("Called createConnectionFactory(connectionManager)");
        return new SFTPConnectionFactory(this, connectionManager);
    }

    /**
     * This can be called in two ways, but is initiated by a client need for a Connection.
     * 
     * In our case SFTPConnectionManager's allocateConnection calls this method. (although the Application Server's
     * ConnectionManager can also in theory call this)
     */
    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cri)
            throws ResourceException
    {
        logger.debug("Called createManagedConnection");
        // Create the new Managed Connection
        SFTPManagedConnection sftpManagedConnection = new SFTPManagedConnection(this, (SFTPConnectionRequestInfo) cri);
        sftpManagedConnection.setTransactionJournal(getTransactionJournal());
        // Open a session on the managed connection
        sftpManagedConnection.openSession();
        // Return the managed connection (with an open session)
        return sftpManagedConnection;
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
        logger.debug("Called matchManagedConnection()");
        int size = connections.size();
        logger.debug("Number of connections considered = [" + size + "].");
        Iterator<?> it = connections.iterator();
        SFTPConnectionRequestInfo scri = (SFTPConnectionRequestInfo) info;
        while (it.hasNext())
        {
            Object obj = it.next();
            logger.debug("Considering object " + obj.getClass().getName());
            if (obj instanceof SFTPManagedConnection)
            {
                logger.debug("Object is a SFTPManagedConnection instance.");
                SFTPManagedConnection smc = (SFTPManagedConnection) obj;
                SFTPConnectionRequestInfo currentScri = smc.getConnectionRequestInfo();
                // TODO We may also want to check that the smcf is equal to 'this'.
                if (currentScri.equals(scri))
                {
                    logger.debug("Found matched Connection.");
                    // This should never occur if the track-connection-by-tx property is set in your connection factory
                    // ds file
                    if (smc.transactionInProgress())
                    {
                        logger.warn("Matched a managed connection, but it's already involved in transaction, "
                                + "if <track-connection-by-tx/> property is specified on your "
                                + "connection factory then this should not be occurring.");
                    }
                    else
                    {
                        return smc;
                    }
                }
            }
        }
        logger.info("No matched Connection for object (or at least none that were not already involved in a txn).");
        return null;
    }

    /**
     * Generate a hash code, this is used by the application server as part of its management of the connection pool
     */
    @Override
    public int hashCode()
    {
        logger.debug("Called hashCode()");
        int hashCode = this.remoteHostname.hashCode() + this.remotePort.hashCode() + this.username.hashCode()
                + this.knownHostsFilename.hashCode() + this.privateKeyFilename.hashCode() + this.clientID.hashCode();
        logger.debug("HashCode = [" + hashCode + "].");
        return hashCode;
    }

    /**
     * SFTPManagedConnetionFactory specific equality implementation. Together with hashCode method, it is used by the
     * Application Server to structure the connection pool (lifted from JCA Spec section 6.5.3.2).
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
        if (object instanceof SFTPManagedConnectionFactory)
        {
            logger.debug("Object is a SFTPManagedConnectionFactory");
            SFTPManagedConnectionFactory smcf = (SFTPManagedConnectionFactory) object;
            return compareSMCF(smcf);
        }
        // default else
        logger.debug("Object is not valid, returning [false].");
        return false;
    }

    /**
     * Return true if the SMCF matches this connection
     * 
     * @param smcf Incoming <code>ManagedConnectionFactory</code> instance from application server.
     * @return true if the <code>smcf</code> matches this connection
     */
    private boolean compareSMCF(SFTPManagedConnectionFactory smcf)
    {
        if (this.remoteHostname == null || this.remotePort == null || this.username == null
                || this.knownHostsFilename == null || this.privateKeyFilename == null || this.clientID == null)
        {
            logger.warn("One of the mandatory managed connection factory variables is null.");
            logger.warn("Hostname = [" + this.remoteHostname + "]");
            logger.warn("Port = [" + this.remotePort + "]");
            logger.warn("Username = [" + this.username + "]");
            logger.warn("KnonwHostFileName = [" + this.knownHostsFilename + "]");
            logger.warn("PrivateKeyFileName = [" + this.privateKeyFilename + "]");
            logger.warn("ClientID = [" + this.clientID + "].");
            return false;
        }
        // Connection specific properties check
        if (this.remoteHostname.equalsIgnoreCase(smcf.remoteHostname) && this.remotePort == smcf.remotePort
                && this.username.equals(smcf.username) && this.knownHostsFilename.equals(smcf.knownHostsFilename)
                && this.privateKeyFilename.equals(smcf.privateKeyFilename) && this.clientID.equals(smcf.clientID))
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
     * Get the remote host name
     * 
     * @return the remote host name
     */
    public String getRemoteHostname()
    {
        return remoteHostname;
    }

    /**
     * Set the host name
     * 
     * @param remoteHostname The host name to connect to.
     */
    public void setRemoteHostname(String remoteHostname)
    {
        logger.debug("ra.xml setting hostname to: [" + remoteHostname + "]");
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
     * Set the local host name
     * 
     * @param rtLocalHost
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
     * @param knownHostsFilename The known hosts file path.
     */
    public void setKnownHostsFilename(String knownHostsFilename)
    {
        logger.debug("ra.xml setting knownHostsFilename to: [" + knownHostsFilename + "]");
        this.knownHostsFilename = knownHostsFilename;
    }

    /**
     * Get the remote port
     * 
     * @return remote port
     */
    public Integer getRemotePort()
    {
        return remotePort;
    }

    /**
     * Set the remote port
     * 
     * @param remotePort The port number to connect to.
     */
    public void setRemotePort(Integer remotePort)
    {
        logger.debug("ra.xml setting port to: [" + remotePort + "]");
        this.remotePort = remotePort;
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
     * @param privateKeyFilename The private key.
     */
    public void setPrivateKeyFilename(String privateKeyFilename)
    {
        logger.debug("ra.xml setting privateKeyFilename to: [" + privateKeyFilename + "]");
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
     * @param username The user name used to log on to EIS.
     */
    public void setUsername(String username)
    {
        logger.debug("ra.xml setting username to: [" + username + "]");
        this.username = username;
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
     * Returns whether or not we clean up the journal on complete. There is no corresponding setter as we only want this
     * to be a client property, not a ra.xml property
     * 
     * @return true if we clean up the journal on complete
     */
    public boolean isCleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
    }

    /**
     * Sets the authentication order
     * 
     * @param preferredAuthentications
     */
    public void setPreferredAuthentications(String preferredAuthentications)
    {
        this.preferredAuthentications = preferredAuthentications;
    }

    /**
     * Get the authentication order
     * 
     * @return String
     */
    public String getPreferredAuthentications()
    {
        return this.preferredAuthentications;
    }

    /**
     * Set the connection timeout
     * 
     * @param connectionTimeout
     */
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get socket connection time out.
     * 
     * @return Integer
     */
    public Integer getConnectionTimeout()
    {
        return this.connectionTimeout;
    }
}

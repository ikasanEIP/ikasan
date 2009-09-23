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

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.command.TransactionalResource;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientInitialisationException;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;
import org.ikasan.connector.sftp.net.SFTPClient;

/**
 * This EJB implements the ManagedConnection for the SFTP resource adapter. This
 * is a representation of a real, physical connection to the server, so it has
 * an object instance variable which remains allocated to a server for the life
 * of this object. This class is responsible for creating virtual connections,
 * of class EISConnection, when the application server calls getConnection()
 * 
 * It extends a (1 Phase Commit and 2 Phase commit) managed connection
 * 
 * TODO Max retry attempts is really a client parameter, which brings us the
 * question of whether we even want to open a connection at this stage
 * 
 * @author Ikasan Development Team
 */
public class SFTPManagedConnection extends TransactionalCommandConnection implements Serializable
{

    /** Generated GUID */
    private static final long serialVersionUID = -4346795065043603050L;

    /** The logger instance. */
    public static Logger logger = Logger.getLogger(SFTPManagedConnection.class);

    /** Common library used by both inbound and outbound connectors */
    private SFTPClient sftpClient;

    /**
     * Handle to the managed connection factory, overrides the default
     * ManagedConnectipnFactory provided by the EISManagedConnection so that we
     * don't have to cast back lots of times
     */
    protected SFTPManagedConnectionFactory managedConnectionFactory;

    /**
     * The client specific connection spec used to override the MFC values where 
     * necessary.
     */
    private SFTPConnectionRequestInfo scri;

    /**
     * Constructor, sets the managed connection factory and the hibernate filter
     * table
     * 
     * client ID sits on EISManagedConnection
     * 
     * @param managedConnectionFactory
     */
    public SFTPManagedConnection(SFTPManagedConnectionFactory managedConnectionFactory, SFTPConnectionRequestInfo scri)
    {
        logger.debug("Called constructor."); //$NON-NLS-1$
        this.managedConnectionFactory = managedConnectionFactory;
        this.scri = scri;
        this.clientID = this.scri.getClientID();

        instanceCount++;
        instanceOrdinal = instanceCount;
    }

    /**
     * Set the managed connection factory
     * 
     * @param managedConnectionFactory
     */
    @Override
    public void setManagedConnectionFactory(ManagedConnectionFactory managedConnectionFactory)
    {
        this.managedConnectionFactory = (SFTPManagedConnectionFactory) managedConnectionFactory;
    }

    /**
     * Get the SFTP managed connection factory
     */
    @Override
    public SFTPManagedConnectionFactory getManagedConnectionFactory()
    {
        logger.debug("Called getManagedConnectionFactory"); //$NON-NLS-1$
        return this.managedConnectionFactory;
    }

    /**
     * Set the connection request info
     * @param scri
     */
    public void setConnectionRequestInfo(SFTPConnectionRequestInfo scri)
    {
        this.scri = scri;
    }

    /**
     * GEt the SFTP connection request info
     * @return SFTPConnectionRequestInfo
     */
    public SFTPConnectionRequestInfo getConnectionRequestInfo()
    {
        return this.scri;
    }

    /**
     * Create a virtual connection (a BaseFileTransferConnection object) and 
     * add it to the list of managed instances before returning it to the client.
     */
    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
    {
        logger.debug("Called getConnection()"); //$NON-NLS-1$
        BaseFileTransferConnection connection = new SFTPConnectionImpl(this);
        addConnection(connection);
        return connection;
    }

    /**
     * This method is called by the application server when it wants to remove
     * this resource adapter completely.
     * 
     * It must free any physical resources. So this is where we will shut down
     * the real connection to the EIS.
     */
    @Override
    public void destroy() throws ResourceException
    {
        logger.debug("Called destroy()"); //$NON-NLS-1$

        // Don't destroy twice
        if (this.destroyed)
        {
            logger.debug("Already destroyed, returning"); //$NON-NLS-1$
            return;
        }

        // TODO Doesn't the app server already call this first?
        cleanup();

        // TODO: This is a badly named worded method, closeSession kills the
        // physical connection
        closeSession();

        this.destroyed = true;
    }

    /**
     * This method is called by the application server to inform this
     * ManagedConnection that it is about to be repooled. It won't do this while
     * it has active virtual connections, but it may do it at any other time. We
     * must indicate that our virtual connections are defunct.
     * 
     * One of situations this gets called in is when close() is called on a
     * virtual connection and it is the last virtual connection in the pool
     */
    @Override
    public void cleanup() throws ResourceException
    {
        logger.debug("Called cleanup()"); //$NON-NLS-1$
        throwIfDestroyed();
        Iterator<?> it = this.connections.iterator();
        while (it.hasNext())
        {
            BaseFileTransferConnection sc = (BaseFileTransferConnection) it.next();
            // We have no specific implementation for invalidate at this stage
            // It therefore defaults to EISManagedConnection.invalidate()
            // which sets the managedConnection to null
            sc.invalidate();
        }
        this.connections.clear();
    }

    // ////////////////////////////////////////
    // Connection API Calls
    // ////////////////////////////////////////

    /**
     * openSession initiates the physical connection to the server and logs us
     * in. This method is called by SFTPManagedConnectionFactory immediately
     * after creating the instance of this class.
     * 
     * In this implementation of an SFTP connector there is no real concept of a
     * session (a connection is made per method call), openSession is left here
     * as it initialises the sftpClient and also provides a starting point for
     * true session functionality to be added if required
     * 
     * @throws ResourceException
     */
    public void openSession() throws ResourceException
    {
        logger.debug("Called openSession."); //$NON-NLS-1$
        createSFTPClient();
        this.sftpClient.echoConfig(Level.INFO);
    }

    /* **************************************************************************
     * Helper Methods /*
     * *************************************************************************/

    /**
     * Close the SFTPClient session
     */
    protected void closeSession()
    {
        if (this.sftpClient == null)
        {
            logger.info("SFTPClient is null.  Closing Session aborted."); //$NON-NLS-1$
        }
        else 
        {
            if (this.sftpClient.isConnected())
            {
                logger.debug("Closing SFTP connection!"); //$NON-NLS-1$
                this.sftpClient.disconnect();
                logger.debug("Disconnected from SFTP host."); //$NON-NLS-1$
            }
            else
            {
                logger.debug("Client was already disconnected.  Closing Session aborted."); //$NON-NLS-1$
            }
        }
    }

    /**
     * Creates the SFTPClient based off the properties from the
     * ConnectionRequestInfo, and opens the connection
     * 
     * @throws ResourceException
     */
    private void createSFTPClient() throws ResourceException
    {
        logger.debug("Called createSFTPClient \n"
                + "host     [" + this.scri.getRemoteHostname() + "]\n"
                + "port     [" + this.scri.getRemotePort() + "]\n"
                + "pvkey    [" + this.scri.getPrivateKeyFilename() + "]\n"
                + "kwnhost  [" + this.scri.getKnownHostsFilename() + "]\n"
                + "maxretry [" + this.scri.getMaxRetryAttempts() + "]\n"
                + "user     [" + this.scri.getUsername() + "]");

        // Private key file
        File privateKey = null;
        try
        {
            privateKey = new File(this.scri.getPrivateKeyFilename());
        }
        catch (NullPointerException e)
        {
            throw new ResourceException("privateKeyFilename is null", e); //$NON-NLS-1$
        }
        // Known Hosts file
        File knownHosts = null;
        try
        {
            knownHosts = new File(this.scri.getKnownHostsFilename());
        }
        catch (NullPointerException e)
        {
            throw new ResourceException("knownHostsFilename is null", e); //$NON-NLS-1$
        }
        // Username
        String username = null;
        if (this.scri.getUsername() != null)
        {
            username = this.scri.getUsername();
        }
        else
        {
            throw new ResourceException("username is null"); //$NON-NLS-1$
        }
        // Remote hostname
        String remoteHostname = null;
        if (this.scri.getRemoteHostname() != null)
        {
            remoteHostname = this.scri.getRemoteHostname();
        }
        else
        {
            throw new ResourceException("remote hostname is null"); //$NON-NLS-1$
        }
        // Remote port (Integer unboxes to int)
        int remotePort;
        if (this.scri.getRemotePort() != null)
        {
            remotePort = this.scri.getRemotePort();
        }
        else
        {
            throw new ResourceException("port is null"); //$NON-NLS-1$
        }
        // Max retry attempts (Integer unboxes to int)
        int maxRetryAttempts;
        if (this.scri.getMaxRetryAttempts() != null)
        {
            maxRetryAttempts = this.scri.getMaxRetryAttempts();
        }
        else
        {
            throw new ResourceException("max retry attempts is null"); //$NON-NLS-1$
        }
        //Local hostname
        String localHostname = null;
        if(this.managedConnectionFactory.getLocalHostname() != null)
        {
            localHostname = this.managedConnectionFactory.getLocalHostname();
        }

        String preferredAuthentications = this.scri.getPreferredAuthentications();

        Integer connectionTimeout = this.scri.getConnectionTimeout();

        //Create a SFTPClient
        this.sftpClient = new SFTPClient(privateKey, knownHosts, username, remoteHostname, remotePort, localHostname, maxRetryAttempts, preferredAuthentications, connectionTimeout);
        try
        {
            this.sftpClient.validateConstructorArgs();
        }
        catch (ClientInitialisationException e)
        {
            throw new ResourceException(e);
        }

        // attempts to open the connection
        try
        {
            sftpClient.connect();
        }
        catch (ClientConnectionException e)
        {
            throw new ResourceException("Failed to open connection when creating SFTPManagedConnection", e); //$NON-NLS-1$
        }
    }

    // /////////////////////////////////////
    // TXN API calls
    // /////////////////////////////////////

    /**
     * Get any MetaData associated with this managed connection, e.g. The
     * maximum number of connections allowed
     */
    @Override
    public SFTPManagedConnectionMetaData getMetaData()
    {
        logger.debug("Called getMetaData()"); //$NON-NLS-1$
        return new SFTPManagedConnectionMetaData(this);
    }

    /**
     * Associate a connection with this managed connection
     */
    @Override
    public void associateConnection(Object connection) throws ResourceException
    {
        logger.debug("Called associateConnection()"); //$NON-NLS-1$
        this.throwIfDestroyed();
        if (connection instanceof BaseFileTransferConnection)
        {
            BaseFileTransferConnection sc = (BaseFileTransferConnection) connection;
            TransactionalCommandConnection smc = sc.getManagedConnection();
            // If it's already associated with us then leave
            if (smc == this)
            {
                return;
            }
            smc.removeConnection(sc);
            addConnection(sc);
            sc.setManagedConnection(this);
        }
        else
        {
            throw new javax.resource.spi.IllegalStateException("Invalid connection object [" + connection + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Deal with forgetting this unti of work as a txn, in this case do nothing
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#forget(javax.transaction.xa.Xid)
     */
    @Override
    public void forget(Xid arg0)
    {
        logger.debug("in forget"); //$NON-NLS-1$
    }

    /**
     * Return the Transaction timeout, always set to 0
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#getTransactionTimeout()
     * @return 0
     */
    @Override
    public int getTransactionTimeout()
    {
        logger.debug("in getTransactionTimeout"); //$NON-NLS-1$
        return 0;
    }

    /**
     * Get the XA resource for this managed connection, in this case, itself.
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#getXAResource()
     */
    @Override
    public XAResource getXAResource()
    {
        logger.debug("in getXAResource"); //$NON-NLS-1$
        return this;
    }

    /**
     * Return whether or not this resource manager is the same, always return
     * false
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#isSameRM(javax.transaction.xa.XAResource)
     * @return false
     */
    @Override
    public boolean isSameRM(XAResource arg0)
    {
        logger.debug("in isSameRM"); //$NON-NLS-1$
        return false;
    }

    /**
     * Set the txn timeout, always return false
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#setTransactionTimeout(int)
     * @return false
     */
    @Override
    public boolean setTransactionTimeout(int arg0)
    {
        logger.debug("in setTransactionTimeout"); //$NON-NLS-1$
        return false;
    }

    @Override
    protected TransactionalResource getTransactionalResource()
    {
        return sftpClient;
    }

    /**
     * Hook method to allow any connector specific post rollback functionality
     * 
     * @param arg0
     */
    @Override
    protected void postRollback(Xid arg0)
    {
        logger.debug("in postRollback"); //$NON-NLS-1$
    }

    /**
     * Hook method to allow any connector specific post commit functionality
     * 
     * @param arg0
     */
    @Override
    protected void postCommit(Xid arg0)
    {
        logger.debug("in postCommit"); //$NON-NLS-1$
    }

    @Override
    protected boolean cleanupJournalOnComplete()
    {
        return scri.cleanupJournalOnComplete();
    }
    
}

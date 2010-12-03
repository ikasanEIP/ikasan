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
import org.ikasan.connector.ftp.net.FileTransferProtocolClient;

/**
 * This EJB implements the ManagedConnection for the FTP resource adapter. This
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
public class FTPManagedConnection extends TransactionalCommandConnection implements Serializable
{
    /** Generated GUID */
    private static final long serialVersionUID = 8623781620439053263L;

    /** The logger instance. */
    public static Logger logger = Logger.getLogger(FTPManagedConnection.class);

    /** Common library used by both inbound and outbound connectors */
    private FileTransferProtocolClient ftpClient;

    /**
     * Handle to the managed connection factory, overrides the default
     * ManagedConnectipnFactory provided by the EISManagedConnection so that we
     * don't have to cast back lots of times
     */
    protected FTPManagedConnectionFactory managedConnectionFactory;

    /**
     * The client specific connection spec used to override the MFC values where 
     * necessary.
     */
    private FTPConnectionRequestInfo fcri;

    /**
     * Constructor, sets the managed connection factory and the hibernate filter
     * table
     * 
     * client ID sits on EISManagedConnection
     * 
     * @param managedConnectionFactory The managed connection factory providing
     *            this managed connection
     */
    public FTPManagedConnection(FTPManagedConnectionFactory managedConnectionFactory, FTPConnectionRequestInfo fcri)
    {
        logger.debug("Called constructor."); //$NON-NLS-1$
        this.managedConnectionFactory = managedConnectionFactory;
        this.fcri = fcri;
        this.clientID = this.fcri.getClientID();
        instanceCount++;
        instanceOrdinal = instanceCount;
    }

    /**
     * Set the managed connection factory
     * 
     * @param managedConnectionFactory Set the managed connection factory
     *            providing this managed connection
     */
    @Override
    public void setManagedConnectionFactory(ManagedConnectionFactory managedConnectionFactory)
    {
        this.managedConnectionFactory = (FTPManagedConnectionFactory) managedConnectionFactory;
    }

    /**
     * Get the FTP managed connection factory
     */
    @Override
    public FTPManagedConnectionFactory getManagedConnectionFactory()
    {
        logger.debug("Called getManagedConnectionFactory"); //$NON-NLS-1$
        return this.managedConnectionFactory;
    }

    /**
     * Set the connection request info
     * @param fcri
     */
    public void setConnectionRequestInfo(FTPConnectionRequestInfo fcri)
    {
        this.fcri = fcri;
    }

    /**
     * Get the FTP connection request info
     * @return fcri
     */
    public FTPConnectionRequestInfo getConnectionRequestInfo()
    {
        return this.fcri;
    }

    /**
     * Create a virtual connection (a FTPConnection object) and add it to the
     * list of managed instances before returning it to the client.
     */
    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
    {
        logger.debug("Called getConnection()"); //$NON-NLS-1$
        BaseFileTransferConnection connection = new FTPConnectionImpl(this);
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
        if(this.destroyed)
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
            BaseFileTransferConnection fc = (BaseFileTransferConnection) it.next();
            // We have no specific implementation for invalidate at this stage
            // It therefore defaults to EISManagedConnection.invalidate()
            // which sets the managedConnection to null
            fc.invalidate();
        }
        this.connections.clear();
    }

    // ////////////////////////////////////////
    // Connection API Calls
    // ////////////////////////////////////////
    /**
     * openSession initiates the physical connection to the server and logs us
     * in. This method is called by FTPManagedConnectionFactory immediately
     * after creating the instance of this class.
     * 
     * In this implementation of an FTP connector there is no real concept of a
     * session (a connection is made per method call), openSession is left here
     * as it initialises the ftpClient and also provides a starting point for
     * true session functionality to be added if required
     * 
     * @throws ResourceException Exception thrown by Connector
     */
    public void openSession() throws ResourceException
    {
        logger.debug("Called openSession."); //$NON-NLS-1$
        createFTPClient();
        this.ftpClient.echoConfig(Level.DEBUG);
    }

    /*
     * Helper Methods /
     */
    /**
     * Close the FileTransferProtocolClient session
     */
    protected void closeSession()
    {
        if(this.ftpClient == null)
        {
            logger.debug("FTPClient is null.  Closing Session aborted."); //$NON-NLS-1$
        }
        else
        {
            if(this.ftpClient.isConnected())
            {
                logger.debug("Closing FTP connection!"); //$NON-NLS-1$
                this.ftpClient.disconnect();
                logger.debug("Disconnected from FTP host."); //$NON-NLS-1$
            }
            else
            {
                logger.info("Client was already disconnected.  Closing Session aborted."); //$NON-NLS-1$
            }
        }
    }

    /**
     * Creates the FileTransferProtocolClient based off the properties from the
     * ConnectionRequestInfo, and opens the connection
     * 
     * @throws ResourceException Exception thrown by connector
     */
    private void createFTPClient() throws ResourceException
    {
        logger.debug("Called createFTPClient \n"
                + "active   [" + this.fcri.getActive() + "]\n"
                + "host     [" + this.fcri.getRemoteHostname() + "]\n"
                + "maxretry [" + this.fcri.getMaxRetryAttempts() + "]\n"
                + "password [" + this.fcri.getPassword() + "]\n"
                + "port     [" + this.fcri.getRemotePort() + "]\n"
                + "user     [" + this.fcri.getUsername() + "]");
        // Active
        boolean active = this.fcri.getActive();
        // Hostname
        String remoteHostname = null;
        if(this.fcri.getRemoteHostname() != null)
        {
            remoteHostname = this.fcri.getRemoteHostname();
        }
        else
        {
            throw new ResourceException("Remote hostname is null."); //$NON-NLS-1$
        }
        // Max retry attempts (Integer unboxes to int)
        int maxRetryAttempts;
        if(this.fcri.getMaxRetryAttempts() != null)
        {
            maxRetryAttempts = this.fcri.getMaxRetryAttempts();
        }
        else
        {
            throw new ResourceException("max retry attempts is null"); //$NON-NLS-1$
        }
        // Password
        String password;
        if(this.fcri.getPassword() != null)
        {
            password = this.fcri.getPassword();
        }
        else
        {
            throw new ResourceException("password is null"); //$NON-NLS-1$
        }
        // Port (Integer unboxes to int)
        int remotePort;
        if(this.fcri.getRemotePort() != null)
        {
            remotePort = this.fcri.getRemotePort();
        }
        else
        {
            throw new ResourceException("Remote port is null"); //$NON-NLS-1$
        }
        // Username
        String username = null;
        if(this.fcri.getUsername() != null)
        {
            username = this.fcri.getUsername();
        }
        else
        {
            throw new ResourceException("username is null"); //$NON-NLS-1$
        }
        String localHostname = null;
        if(this.managedConnectionFactory.getLocalHostname() != null)
        {
            localHostname = this.managedConnectionFactory.getLocalHostname();
        }
        String systemKey = this.fcri.getSystemKey();
        Integer connectionTimeout = this.fcri.getConnectionTimeout();
        Integer dataTimeout = this.fcri.getDataTimeout();
        Integer soTimeout = this.fcri.getSocketTimeout();

        // Create a FileTransferProtocolClient
        this.ftpClient = new FileTransferProtocolClient(active, remoteHostname, localHostname, maxRetryAttempts, password, remotePort, username, systemKey,
            connectionTimeout, dataTimeout, soTimeout);
        try
        {
            this.ftpClient.validateConstructorArgs();
        }
        catch (ClientInitialisationException e)
        {
            throw new ResourceException(e);
        }
        // attempts to open the connection
        try
        {
            ftpClient.connect();
        }
        catch (ClientConnectionException e)
        {
            throw new ResourceException("Failed to open connection when creating FTPManagedConnection", e); //$NON-NLS-1$
        }
        // attempts to login
        try
        {
            ftpClient.login();
        }
        catch (ClientConnectionException e)
        {
            throw new ResourceException("Failed to login when creating FTPManagedConnection", e); //$NON-NLS-1$
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
    public FTPManagedConnectionMetaData getMetaData()
    {
        logger.debug("Called getMetaData()"); //$NON-NLS-1$
        return new FTPManagedConnectionMetaData(this);
    }

    /**
     * Associate a connection with this managed connection
     */
    @Override
    public void associateConnection(Object connection) throws ResourceException
    {
        logger.debug("Called associateConnection()"); //$NON-NLS-1$
        this.throwIfDestroyed();
        if(connection instanceof FTPConnection)
        {
            BaseFileTransferConnection fc = (BaseFileTransferConnection) connection;
            TransactionalCommandConnection fmc = fc.getManagedConnection();
            // If it's already associated with us then leave
            if(fmc == this)
            {
                return;
            }
            fmc.removeConnection(fc);
            addConnection(fc);
            fc.setManagedConnection(this);
        }
        else
        {
            throw new javax.resource.spi.IllegalStateException("Invalid connection object [" + connection + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Deal with forgetting this unit of work as a txn, in this case do nothing
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
        return ftpClient;
    }

    /**
     * Hook method to allow any connector specific post rollback functionality
     * 
     * @param arg0 Transaction Id
     */
    @Override
    protected void postRollback(Xid arg0)
    {
        logger.debug("in postRollback"); //$NON-NLS-1$
    }

    /**
     * Hook method to allow any connector specific post commit functionality
     * 
     * @param arg0 Transaction Id
     */
    @Override
    protected void postCommit(Xid arg0)
    {
        logger.debug("in postCommit"); //$NON-NLS-1$
    }

    @Override
    protected boolean cleanupJournalOnComplete()
    {
        return fcri.cleanupJournalOnComplete();
    }
}

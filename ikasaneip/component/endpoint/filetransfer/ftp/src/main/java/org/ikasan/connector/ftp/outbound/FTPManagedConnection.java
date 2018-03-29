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

import javax.resource.ResourceException;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.command.TransactionalResource;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientInitialisationException;
import org.ikasan.connector.BaseFileTransferConnection;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.ftp.net.FileTransferProtocol;
import org.ikasan.connector.ftp.net.FileTransferProtocolClient;
import org.ikasan.connector.ftp.net.FileTransferProtocolSSLClient;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.slf4j.event.Level;

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
    public static Logger logger = LoggerFactory.getLogger(FTPManagedConnection.class);

    /** Common library used by both inbound and outbound connectors */
    private FileTransferProtocol ftpClient;

    /**
     * The client specific connection spec used to override the MFC values where 
     * necessary.
     */
    private FTPConnectionRequestInfo fcri;

    private  String clientID;

    /**
     * Constructor, sets the managed connection factory and the hibernate filter
     * table
     * 
     * client ID sits on EISManagedConnection
     * 
     * @param fcri connection request info
     */
    public FTPManagedConnection(FTPConnectionRequestInfo fcri)
    {
        logger.debug("Called constructor."); //$NON-NLS-1$
        this.fcri = fcri;
        this.clientID = this.fcri.getClientID();
        instanceCount++;
        instanceOrdinal = instanceCount;
    }

    /**
     * Create a virtual connection (a BaseFileTransferConnection object) and
     * add it to the list of managed instances before returning it to the client.
     */
    public Object getConnection( FileChunkDao fileChunkDao, BaseFileTransferDao baseFileTransferDao)
    {
        logger.debug("Called getConnection()"); //$NON-NLS-1$
        BaseFileTransferConnection connection = new FTPConnectionImpl(this, fileChunkDao, baseFileTransferDao);
        return connection;
    }

    public String getClientID()
    {
        return clientID;
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
        this.ftpClient.echoConfig();
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
        String localHostname = "localhost";

        String systemKey = this.fcri.getSystemKey();
        Integer connectionTimeout = this.fcri.getConnectionTimeout();
        Integer dataTimeout = this.fcri.getDataTimeout();
        Integer soTimeout = this.fcri.getSocketTimeout();

        // Create a FileTransferProtocolClient
        if (fcri.getFTPS()) {
            Boolean FTPS = true;
            Integer ftpsPort = fcri.getFtpsPort();
            String  ftpsProtocol = fcri.getFtpsProtocol();
            Boolean ftpsIsImplicit = fcri.getFtpsIsImplicit();
            String  ftpsKeyStoreFilePath = fcri.getFtpsKeyStoreFilePath();
            String  ftpsKeyStoreFilePassword = fcri.getFtpsKeyStoreFilePassword();


            this.ftpClient = new FileTransferProtocolSSLClient(active, remoteHostname, localHostname, maxRetryAttempts, password, remotePort, username, systemKey,
                                                               connectionTimeout, dataTimeout, soTimeout,
                                                               FTPS, ftpsPort, ftpsProtocol, ftpsIsImplicit, ftpsKeyStoreFilePath, ftpsKeyStoreFilePassword);
        } else {
            this.ftpClient = new FileTransferProtocolClient(active, remoteHostname, localHostname, maxRetryAttempts, password, remotePort, username, systemKey,
                                                            connectionTimeout, dataTimeout, soTimeout);
        }

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
     * Deal with forgetting this unit of work as a txn, in this case do nothing
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#forget(javax.transaction.xa.Xid)
     */
    @Override
    public void forget(Xid arg0)
    {
        logger.info("in forget"); //$NON-NLS-1$
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
        logger.info("in postRollback"); //$NON-NLS-1$
    }

    /**
     * Hook method to allow any connector specific post commit functionality
     * 
     * @param arg0 Transaction Id
     */
    @Override
    protected void postCommit(Xid arg0)
    {
        logger.info("in postCommit"); //$NON-NLS-1$
    }

    @Override
    protected boolean cleanupJournalOnComplete()
    {
        return fcri.cleanupJournalOnComplete();
    }
}

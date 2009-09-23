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

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionSpec;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;

import org.ikasan.connector.base.outbound.EISConnectionFactoryImpl;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This class implements the ConnectionFactory for the SFTP resource
 * adapter. Clients get an instance of this class by doing a JNDI
 * lookup.
 * 
 * @author Ikasan Development Team
 */
public class SFTPConnectionFactory extends EISConnectionFactoryImpl
{
    /** GUID for serialisation */
    private static final long serialVersionUID = -2362713339278565934L;

    /** The logger instance. */
    private static Logger logger = Logger.getLogger(SFTPConnectionFactory.class);

    /**
     * This Constructor is called when the SFTPManagedConnectionFactory
     * instantiates this object. It passes in a reference to itself and
     * to the connection manager. The connection manager will, in
     * practice, be a class passed into the resource adapter by the
     * Application Server.
     * 
     * It then calls passes this off to the another constructor passing in 
     * null for a SFTPConnectionRequestInfo (because at this stage in the 
     * Application Server startup, we haven't got one)
     * 
     * @param managedConnectionFactory The SFTP managed connection factory 
     * @param connectionManager The connection Manager 
     */
    public SFTPConnectionFactory(ManagedConnectionFactory managedConnectionFactory,
                                 ConnectionManager connectionManager)
    {
        this(managedConnectionFactory, connectionManager, null);
    }

    /**
     * This Constructor is intended to be called by a program outside of 
     * Application Server, however, it is also called by the constructor 
     * above (the work is done here for both constructors out of convenience) 
     *  
     * We simple save the connectionManager in an instance variable, 
     * and delegate getConnection() calls to it.
     * 
     * @param managedConnectionFactory
     * @param connectionManager - null if not supplied by caller
     * @param connectionRequestInfo - null if called by the Application Server
     */
    public SFTPConnectionFactory(ManagedConnectionFactory managedConnectionFactory,
                                 ConnectionManager connectionManager,
                                 ConnectionRequestInfo connectionRequestInfo)
    {
        logger.debug("Called SFTPConnectionFactory constructor"); //$NON-NLS-1$
        this.setManagedConnectionFactory(managedConnectionFactory); 
        
        // If connectionManager is not passed by the Application Server or the 
        // calling program, we need to create one
        ConnectionManager sftpConnectionManager = connectionManager;
        if (connectionManager == null) 
        {
            logger.warn("connectionManager param is null. " //$NON-NLS-1$
            + "This would suggest the connectionManager has not been provided " //$NON-NLS-1$
            + "by the Application Server. Inform development if this was not " //$NON-NLS-1$
            + "intentional. Creating new ConnectionManager outside of the " //$NON-NLS-1$
            + "Application Server."); //$NON-NLS-1$
            sftpConnectionManager = new SFTPConnectionManager(); 
        } 
        
        this.setConnectionManager(sftpConnectionManager);
        this.setConnectionRequestInfo(connectionRequestInfo);
    }

    /**
     * This is a method that application clients can call when they've
     * got a reference to the factory object by JNDI. See method below for 
     * details.
     * 
     * @return a Connection
     */
    @Override
    public Connection getConnection()
        throws ResourceException
    {
        logger.debug("Called getConnection()");  //$NON-NLS-1$
        return this.getConnection(null);
    }

    /**
     * This is a method that application clients can call when they've
     * got a reference to the factory object by JNDI. We get a new
     * EIS-Connection object by delegating the whole job to the
     * ConnectionManager. If we are running within an application server,
     * then the server will want to use its own connection manager, and
     * will have specified it when it created the EISManagedConnectionFactory.  
     * So this call actually delegates back to the application server. 
     * 
     * The server will in turn call one of the methods on EIS-ManagedConnection 
     * to get a virtual connection for the client. If we are not running in an 
     * application server, this call will be handled by the connection manager 
     * which is in this package. It simply creates a new physical connection and 
     * then a new virtual connection from it.
     * 
     * Get a connection given a Connection Spec (non physical details of the 
     * connection)
     *  
     * @param connectionSpec - null if called by getConnection()
     * @return A connection
     */
    @Override
    public Connection getConnection(ConnectionSpec connectionSpec)
        throws ResourceException
    {

        SFTPConnectionSpec spec = new SFTPConnectionSpec();
        ConnectionRequestInfo sftpConnectionRequestInfo = null;
        
        if (connectionSpec != null)
        {
            logger.debug("Called getConnection with connection Spec: \n[" //$NON-NLS-1$ 
                + connectionSpec.toString() + "]"); //$NON-NLS-1$

            // Check that the connection spec is OK, if not throw an error
            if(!(connectionSpec instanceof SFTPConnectionSpec))
            {
                throw new ResourceException("Invalid ConnectionSpec. Received [" //$NON-NLS-1$
                        + connectionSpec.getClass().getName() + "], expected [" //$NON-NLS-1$
                        + spec.getClass().getName() + "]"); //$NON-NLS-1$
            }
            
            // Create the CRI from the connection spec
            sftpConnectionRequestInfo = this.connectionSpecToCRI(connectionSpec);
            logger.debug("CRI: [" + sftpConnectionRequestInfo.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            logger.debug("No connection spec passed through."); //$NON-NLS-1$
        }
        
        // Allocate a connection, passing in the ConnectionRequestInfo
        logger.debug("About to allocate a connection."); //$NON-NLS-1$
        ConnectionManager cm = this.getConnectionManager();
        logger.debug("ConnectionManager of type [" + cm.getClass() + "].");  //$NON-NLS-1$//$NON-NLS-2$

        ManagedConnectionFactory mcf = this.getManagedConnectionFactory();
        Object object = cm.allocateConnection(mcf, sftpConnectionRequestInfo); 
        
        // If the connection is not a BaseFileTransferConnection then error 
        if(! (object instanceof BaseFileTransferConnection) )
        {
            throw new ResourceException("ConnectionManager connection " //$NON-NLS-1$
                    + "allocation returned an unknown object [" //$NON-NLS-1$
                    + object.getClass().getName() + "]"); //$NON-NLS-1$
        }
        
        return (BaseFileTransferConnection)object; 
    }
    
    /**
     * Converts the connection spec into a connection request info
     * 
     * @param spec The client connection details
     * @return The connection request info
     */
    private ConnectionRequestInfo connectionSpecToCRI(ConnectionSpec spec)
    {
        logger.debug("Converting Connection Spec to CRI"); //$NON-NLS-1$
        SFTPConnectionSpec sftpConnectionSpec = (SFTPConnectionSpec)spec;
        SFTPConnectionRequestInfo scri = new SFTPConnectionRequestInfo();
        if (sftpConnectionSpec != null)
        {
            scri.setCleanupJournalOnComplete(sftpConnectionSpec.getCleanupJournalOnComplete());
            scri.setClientID(sftpConnectionSpec.getClientID());
            scri.setRemoteHostname(sftpConnectionSpec.getRemoteHostname());
            scri.setKnownHostsFilename(sftpConnectionSpec.getKnownHostsFilename());
            scri.setMaxRetryAttempts(sftpConnectionSpec.getMaxRetryAttempts());
            scri.setRemotePort(sftpConnectionSpec.getRemotePort());
            scri.setPrivateKeyFilename(sftpConnectionSpec.getPrivateKeyFilename());
            scri.setUsername(sftpConnectionSpec.getUsername());
            scri.setRemoteHostname(sftpConnectionSpec.getRemoteHostname());
            scri.setRemotePort(sftpConnectionSpec.getRemotePort());
            scri.setPollTime(sftpConnectionSpec.getPollTime());
            scri.setPreferredAuthentications(sftpConnectionSpec.getPreferredAuthentications());
            scri.setConnectionTimeout(sftpConnectionSpec.getConnectionTimeout());
        }
        return scri;
    }
    
}

/*
 * $Id$
 * $URL$
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

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionSpec;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;

import org.apache.log4j.Logger;
import org.ikasan.connector.base.outbound.EISConnectionFactoryImpl;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;

/**
 * This class implements the ConnectionFactory for the FTP resource
 * adapter. Clients get an instance of this class by doing a JNDI
 * lookup.
 * 
 * @author Ikasan Development Team
 */
public class FTPConnectionFactory extends EISConnectionFactoryImpl
{
    /** GUID for serialisation */
    private static final long serialVersionUID = -2362713339278565934L;

    /** The logger instance. */
    private static Logger logger = Logger.getLogger(FTPConnectionFactory.class);

    /**
     * This Constructor is called when the FTPManagedConnectionFactory
     * instantiates this object. It passes in a reference to itself and
     * to the connection manager. The connection manager will, in
     * practice, be a class passed into the resource adapter by the
     * Application Server.
     * 
     * It then calls passes this off to the another constructor passing in 
     * null for a FTPConnectionRequestInfo (because at this stage in the 
     * Application Server startup, we haven't got one)
     * 
     * @param managedConnectionFactory The FTP managed connection factory 
     * @param connectionManager The connection Manager 
     */
    public FTPConnectionFactory(ManagedConnectionFactory managedConnectionFactory,
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
     * @param managedConnectionFactory - The managed connection factory for this connection factory 
     * @param connectionManager - null if not supplied by caller
     * @param connectionRequestInfo - null if called by the Application Server
     */
    public FTPConnectionFactory(ManagedConnectionFactory managedConnectionFactory,
                                 ConnectionManager connectionManager,
                                 ConnectionRequestInfo connectionRequestInfo)
    {
        logger.debug("Called FTPConnectionFactory constructor"); //$NON-NLS-1$
        this.setManagedConnectionFactory(managedConnectionFactory); 
        
        // If connectionManager is not passed by the Application Server or the 
        // calling program, we need to create one
        ConnectionManager ftpConnectionManager = connectionManager;
        if (connectionManager == null) 
        {
            logger.warn("connectionManager param is null. " //$NON-NLS-1$
            + "This would suggest the connectionManager has not been provided " //$NON-NLS-1$
            + "by the Application Server. Inform development if this was not " //$NON-NLS-1$
            + "intentional. Creating new ConnectionManager outside of the " //$NON-NLS-1$
            + "Application Server."); //$NON-NLS-1$
            ftpConnectionManager = new FTPConnectionManager(); 
        } 
        
        this.setConnectionManager(ftpConnectionManager);
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

        FTPConnectionSpec spec = new FTPConnectionSpec();        
        ConnectionRequestInfo ftpConnectionRequestInfo = null;
        
        if (connectionSpec != null)
        {
            logger.debug("Called getConnection with connection Spec: \n[" //$NON-NLS-1$ 
                + connectionSpec.toString() + "]"); //$NON-NLS-1$

            // Check that the connection spec is OK, if not throw an error
            if(!(connectionSpec instanceof FTPConnectionSpec))
            {
                throw new ResourceException("Invalid ConnectionSpec. Received [" //$NON-NLS-1$
                        + connectionSpec.getClass().getName() + "], expected [" //$NON-NLS-1$
                        + spec.getClass().getName() + "]"); //$NON-NLS-1$
            }
            spec = (FTPConnectionSpec)connectionSpec;
            
            // Create the CRI from the connection spec
            ftpConnectionRequestInfo = this.connectionSpecToCRI(connectionSpec);
            logger.debug("CRI: [" + ftpConnectionRequestInfo.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        // Allocate a connection, passing in the ConnectionRequestInfo
        logger.debug("About to allocate a connection."); //$NON-NLS-1$
        Object object = this.getConnectionManager()
                            .allocateConnection(this.getManagedConnectionFactory(), 
                                                ftpConnectionRequestInfo); 
        
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
        FTPConnectionSpec ftpConnectionSpec = (FTPConnectionSpec)spec;
        FTPConnectionRequestInfo fcri = new FTPConnectionRequestInfo();
        if (ftpConnectionSpec != null)
        {
            fcri.setActive(ftpConnectionSpec.getActive());
            fcri.setCleanupJournalOnComplete(ftpConnectionSpec.getCleanupJournalOnComplete());
            fcri.setClientID(ftpConnectionSpec.getClientID());
            fcri.setRemoteHostname(ftpConnectionSpec.getRemoteHostname());
            fcri.setMaxRetryAttempts(ftpConnectionSpec.getMaxRetryAttempts());
            fcri.setPassword(ftpConnectionSpec.getPassword());
            fcri.setRemotePort(ftpConnectionSpec.getRemotePort());
            fcri.setUsername(ftpConnectionSpec.getUsername());
            fcri.setPollTime(ftpConnectionSpec.getPollTime());
            fcri.setSystemKey(ftpConnectionSpec.getSystemKey());
            fcri.setConnectionTimeout(ftpConnectionSpec.getConnectionTimeout());
            fcri.setDataTimeout(ftpConnectionSpec.getDataTimeout());
            fcri.setSocketTimeout(ftpConnectionSpec.getSocketTimeout());
        }
        return fcri;
    }
    
}

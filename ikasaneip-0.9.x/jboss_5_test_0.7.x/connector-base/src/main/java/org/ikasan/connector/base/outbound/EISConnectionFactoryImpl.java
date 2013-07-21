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
package org.ikasan.connector.base.outbound;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.*;
import javax.naming.*;

import org.apache.log4j.Logger;

import org.ikasan.connector.ConnectorRuntimeException;

import java.io.*;

/**
 * This class implements the ConnectionFactory for the EIS resource adapter.
 * Each type of adapter derived from this class should implement getConnection()
 * for the adapter specifics. Clients get an instance of this class by doing a
 * JNDI lookup.
 * 
 * @author Ikasan Development Team
 */
public abstract class EISConnectionFactoryImpl implements EISConnectionFactory, Serializable, javax.resource.Referenceable
{
    /** SerialVersion */
    private static final long serialVersionUID = 1L;

    /** Logger */
    private Logger logger = Logger.getLogger(EISConnectionFactoryImpl.class);

    /** Reference */
    private Reference reference;

    /** Managed Connection Factory */
    private ManagedConnectionFactory managedConnectionFactory;

    /** Connection Manager */
    private ConnectionManager connectionManager;

    /** Connection Request Info */
    private ConnectionRequestInfo cri;

    /**
     * Actual connection must be created by the derived class
     */
    public abstract Connection getConnection() throws ResourceException;

    /**
     * Actual connection must be created by the derived class
     */
    public abstract Connection getConnection(ConnectionSpec spec) throws ResourceException;

    /**
     * Allocates an adapter specific Connection.
     * 
     * @param connRequestInfo connection request info
     * @return An adapter specific Connection
     * @throws ResourceException - Exception if there is an issue with the
     *             resource
     */
    protected final Connection allocateConnection(ConnectionRequestInfo connRequestInfo) throws ResourceException
    {
        logger.debug("allocateConnection(connRequestInfo)..."); //$NON-NLS-1$
        Connection connection = (Connection) this.connectionManager.allocateConnection(this.managedConnectionFactory, connRequestInfo);
        return connection;
    }

    /**
     * Getter for ConnectionManager
     * 
     * @return ConnectionManager
     */
    public ConnectionManager getConnectionManager()
    {
        logger.debug("getConnectionManager()..."); //$NON-NLS-1$
        return this.connectionManager;
    }

    /**
     * Setter for ConnectionManager
     * 
     * @param connectionManager - The connection manager to set
     */
    public void setConnectionManager(ConnectionManager connectionManager)
    {
        logger.debug("setConnectionManager(cm)..."); //$NON-NLS-1$
        this.connectionManager = connectionManager;
    }

    /**
     * Getter for ManagedConnectionFactory
     * 
     * @return the managedConnectionFactory
     */
    public ManagedConnectionFactory getManagedConnectionFactory()
    {
        logger.debug("getManagedConnectionFactory()..."); //$NON-NLS-1$
        return this.managedConnectionFactory;
    }

    /**
     * Setter for ManagedConnectionFactory
     * 
     * @param managedConnectionFactory - The managed connection factory to set
     */
    public void setManagedConnectionFactory(ManagedConnectionFactory managedConnectionFactory)
    {
        logger.debug("setManagedConnectionFactory(mcf)..."); //$NON-NLS-1$
        this.managedConnectionFactory = managedConnectionFactory;
    }

    /**
     * Getter for ConnectionRequestInfo
     * 
     * @return ConnectionRequestInfo
     */
    public ConnectionRequestInfo getConnectionRequestInfo()
    {
        logger.debug("getConnectionRequestInfo(cri)..."); //$NON-NLS-1$
        return this.cri;
    }

    /**
     * Setter for ConnectionRequestInfo
     * 
     * @param cri ConnectionRequestInfo
     */
    public void setConnectionRequestInfo(ConnectionRequestInfo cri)
    {
        logger.debug("setConnectionRequestInfo(cri)..."); //$NON-NLS-1$
        this.cri = cri;
    }

    /**
     * setReference and getReference have to be implemented so that the
     * application server can save references to this object in its JNDI store,
     * rather than serializing the object itself. The spec requires both
     * reference and serialization methods to be supported. Of course, the
     * serialization method is automatic; we just say 'implements serializable'.
     * Note that setReference is called by the application server, not by any
     * part of the resource adapter; we don't know (or care) what it passes as
     * an argument, so long as we can retrieve it again on demand.
     */
    public void setReference(Reference reference)
    {
        logger.debug("Called setReference"); //$NON-NLS-1$
        this.reference = reference;
    }

    public Reference getReference()
    {
        logger.debug("Called getReference"); //$NON-NLS-1$
        return this.reference;
    }

    public RecordFactory getRecordFactory()
    {
        throw new ConnectorRuntimeException("The RecordFactory is not implemented." //$NON-NLS-1$
                + "You must override to provide a real implementation."); //$NON-NLS-1$
    }

    public ResourceAdapterMetaData getMetaData()
    {
        throw new ConnectorRuntimeException("The ResourceAdapterMetaData is not implemented." //$NON-NLS-1$
                + "You must override to provide a real implementation."); //$NON-NLS-1$
    }
}

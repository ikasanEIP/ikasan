/*
 * $Id: EISConnectionImpl.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/outbound/EISConnectionImpl.java $
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

import javax.resource.*; 
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;
import javax.resource.spi.ManagedConnection;

import org.apache.log4j.Logger;

import org.ikasan.connector.base.ConnectionState;

/**
 * This class models a virtual connection to the EIS.
 * Clients (EJBs) get an instance of this class when they call 
 * getConnection(), it doesn't happen directly, 
 * but under the control of the application server's pool manager.
 * 
 * Calls to this class are essentially delegated to the managed connection.
 *
 * @author Ikasan Development Team
 */  
public abstract class EISConnectionImpl 
    implements EISConnection
{ 
    /** Logger */
    private static Logger logger = Logger.getLogger(EISConnectionImpl.class);

    /**
     * The ManagedConnection instance that controls this virtual
     * connection. This will get set to null when this instance is
     * closed.
     */  
    protected ManagedConnection managedConnection; 

    /**
     * Constructor
     * @param managedConnection Passed in managed connection
     */
    public EISConnectionImpl(final ManagedConnection managedConnection)
    {
        this.managedConnection = managedConnection;
    }
    
    /**
     * invalidate is called by the EISManagedConnection object that
     * owns this virtual connection, to indicate that it is defunct. It
     * only does this at the request of the application server. In this 
     * implementation we indicate defunct status by setting the 'manager'
     * instance variable to null. 
     */ 
    public void invalidate() 
    { 
        logger.debug("Called invalidate()");  //$NON-NLS-1$
        this.managedConnection = null; 
    } 

    /**
     * Closes the virtual connection to the EIS.
     * The derived class must implement this.
     */
    public abstract void close();
    
    /**
     * setManager is called by the ManagedConnection when the
     * application server wants to re-associate a virtual connection with
     * a new manager. 
     * 
     * @param managedConnection managed connection to associate with
     */ 
    public void setManager(final ManagedConnection managedConnection) 
    { 
        logger.debug("Called setManager()"); //$NON-NLS-1$
        this.managedConnection = managedConnection; 
    } 

    /**
     * Getter for auto-commit property
     * @return true if we're auto commiting, else false
     */
    public boolean getAutoCommit()
         
    {
        return ((EISManagedConnection)this.managedConnection).getAutoCommit();
    }
    
    /**
     * Setter for auto-commit property
     * @param flag auto commit flag to set
     */
    public void setAutoCommit(boolean flag)
    {
        ((EISManagedConnection)this.managedConnection).setAutoCommit(flag);
    }

    /**
     * derived class must provide a validate method to test the connection
     */
    public abstract void validate() throws ResourceException;

    public Interaction createInteraction()
    {
        return null;
    }

    public LocalTransaction getLocalTransaction() throws ResourceException
    {
        throw new NotSupportedException("CCI Local Transaction is not supported."); //$NON-NLS-1$
    }

    public ConnectionMetaData getMetaData()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSetInfo getResultSetInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Get the status of this JCA connection
     * @return ConnectionStatus
     */
    public ConnectionState getConnectionState()
    {
        if (this.managedConnection == null) 
            return ConnectionState.DISCONNECTED;
        
        return ((EISManagedConnection)this.managedConnection).getConnectionState();
    }
    
} 
 


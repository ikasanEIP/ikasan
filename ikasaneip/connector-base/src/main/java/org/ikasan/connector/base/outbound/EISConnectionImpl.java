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
 


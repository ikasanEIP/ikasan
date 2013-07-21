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

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;
import org.ikasan.connector.base.ConnectionState;

/**
 * This is an abstract class representing the ManagedConnection for the resource
 * adapter.
 * 
 * The ManagedConnection is a representation of a real, physical connection to
 * the server, so it has an object instance variable which remains allocated to
 * a server for the life of this object.
 * 
 * This class (or extensions thereof) is responsible for creating virtual
 * connections when the application server calls getConnection()
 * 
 * @author Ikasan Development Team
 */
public abstract class EISManagedConnection implements ManagedConnection
{
    /** Logger */
    private static Logger logger = Logger.getLogger(EISManagedConnection.class);

    /** Print Writer */
    private PrintWriter writer = null;

    /** Connector client ID */
    protected String clientID;

    /** Connector's connection state - starts as disconnected */
    protected ConnectionState connectionState = ConnectionState.DISCONNECTED;

    /**
     * When a connection is in an auto-commit mode, an operation on the
     * connection automatically commits after it has been executed. The
     * auto-commit mode must be off if multiple interactions have to be grouped
     * in a single transaction, either local or XA, and committed or rolled back
     * as a unit.
     */
    protected boolean autoCommit;

    /**
     * destroyed is set in the destroy() method, so that other methods can check
     * it before attempting any operations that require an active connection.
     */
    protected boolean destroyed;

    /**
     * connections is the set of Connection instances that this
     * ManagedConnection is looking after
     */
    protected Set<Connection> connections = new HashSet<Connection>();

    /**
     * connectionListeners is the list of connection listeners registered for
     * this instance.
     */
    protected Set<ConnectionEventListener> connectionListeners = new HashSet<ConnectionEventListener>();

    /**
     * Store a reference to the factory that created this ManagedConnection. We
     * will need this later to determine whether a ManagedConnection can be
     * reused to support a new client request.
     */
    protected ManagedConnectionFactory managedConnectionFactory;

    /**
     * Create a virtual connection (a specific EISConnection object) and add it
     * to the list of managed instances before returning it to the client.
     * 
     * @param subject - Security related information for an entity
     * @param cri - The connection request information to use
     * @return Object - The connection
     */
    public abstract Object getConnection(final Subject subject, final ConnectionRequestInfo cri);

    /**
     * Derived class must implement cleanup
     */
    public abstract void cleanup() throws ResourceException;

    /**
     * Derived class must implement destroy
     */
    public abstract void destroy() throws ResourceException;

    /**
     * Set the managed connection factory
     * 
     * @param managedConnectionFactory - The managed connection factory to set
     */
    public void setManagedConnectionFactory(ManagedConnectionFactory managedConnectionFactory)
    {
        this.managedConnectionFactory = managedConnectionFactory;
    }

    /**
     * Get the managed connection factory
     * 
     * @return managed connection factory
     */
    public ManagedConnectionFactory getManagedConnectionFactory()
    {
        return this.managedConnectionFactory;
    }

    /**
     * This method is called by the application server to register interest in
     * connection events.
     */
    public void addConnectionEventListener(final ConnectionEventListener listener)
    {
        logger.info("Called addConnectionEventListener()"); //$NON-NLS-1$
        synchronized (this.connectionListeners)
        {
            this.connectionListeners.add(listener);
        }
    }

    /**
     * This method is called by the application server when it has finished
     * monitoring connection events. In practice this method is never called.
     */
    public void removeConnectionEventListener(final ConnectionEventListener listener)
    {
        logger.info("Called removeConnectionEventListener()"); //$NON-NLS-1$
        synchronized (this.connectionListeners)
        {
            this.connectionListeners.remove(listener);
        }
    }

    /**
     * Indicates whether the physical connection has been destroyed. In this
     * implementation all we have to do is return the 'destroyed' flag.
     * 
     * @return true if this managed connection is destroyed
     */
    public boolean isDestroyed()
    {
        return this.destroyed;
    }

    /**
     * This is a convenience method called at the start of each method that
     * requires the physical connection to the server to be present. If it is
     * not, it throws an exception.
     * 
     * @throws javax.resource.spi.IllegalStateException - Exception if we've
     *             reached an illegal state
     */
    protected void throwIfDestroyed() throws javax.resource.spi.IllegalStateException
    {
        if (this.destroyed)
        {
            throw new javax.resource.spi.IllegalStateException("ManagedConnection is destroyed"); //$NON-NLS-1$
        }
    }

    /**
     * Add a Connection instance to the list of Connections that this object is
     * looking after.
     * 
     * @param connection Connection to add
     */
    public void addConnection(final EISConnection connection)
    {
        logger.debug("Called addConnection()"); //$NON-NLS-1$
        synchronized (this.connections)
        {
            this.connections.add(connection);
        }
    }

    /**
     * Remove a Connection instance from the list of Connections that this
     * object is looking after.
     * 
     * @param connection Connection to remove
     */
    public void removeConnection(final EISConnection connection)
    {
        logger.debug("Called removeConnection()"); //$NON-NLS-1$
        synchronized (this.connections)
        {
            this.connections.remove(connection);
        }
    }

    /**
     * When a virtual connection (an object of class EISConnection) is closed by
     * its client, the EISConnection calls this method on the
     * EISManagedConnection that owns it. This in turn passes the event on to
     * anything that has registered as a listener for connection events.
     * Typically this will be application server's connection pool manager.
     * 
     * @param connection - The connection to send the closed event to.
     */
    public void sendClosedEvent(EISConnection connection)
    {
        logger.debug("Called sendClosedEvent()"); //$NON-NLS-1$
        Iterator<ConnectionEventListener> it = this.connectionListeners.iterator();
        while (it.hasNext())
        {
            logger.debug("Informing the connection listener that the connection is closed."); //$NON-NLS-1$
            ConnectionEventListener listener = it.next();
            if (listener != null)
            {
                ConnectionEvent ce = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
                ce.setConnectionHandle(connection);
                listener.connectionClosed(ce);
            }
        }
        logger.debug("Finished notifying connection event listeners."); //$NON-NLS-1$
    }

    /**
     * Completely removes this connection and destroys it so it cannot be reused
     * in the future.
     * 
     * @param thrown - The throwable (error) that we're sending
     */
    protected void sendErrorEvent(Throwable thrown)
    {
        logger.debug("Called sendErrorEvent"); //$NON-NLS-1$
        if (this.isDestroyed())
        {
            logger.debug("Not sending ErrorEvent as this connection is already destroyed " //$NON-NLS-1$
                    + this);
            return;
        }
        Exception e = null;
        if (thrown instanceof Exception)
        {
            e = (Exception) thrown;
        }
        else
        {
            e = new ResourceAdapterInternalException("Unexpected error", thrown); //$NON-NLS-1$
        }
        Iterator<ConnectionEventListener> it = this.connectionListeners.iterator();
        while (it.hasNext())
        {
            ConnectionEventListener listener = it.next();
            ConnectionEvent ce = new ConnectionEvent(this, ConnectionEvent.CONNECTION_ERROR_OCCURRED, e);
            listener.connectionErrorOccurred(ce);
        }
    }

    /**
     * Standard getter for the logWriter
     * 
     * @see javax.resource.spi.ManagedConnectionFactory#getLogWriter()
     */
    public PrintWriter getLogWriter()
    {
        logger.debug("Getting logWriter..."); //$NON-NLS-1$
        return this.writer;
    }

    /**
     * Standard setter for the logWriter
     * 
     * @see javax.resource.spi.ManagedConnectionFactory#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter writer)
    {
        this.writer = writer;
        logger.debug("Setting logWriter..."); //$NON-NLS-1$
    }

    /**
     * Must be implemented by the derived class
     */
    public abstract void associateConnection(Object arg0) throws ResourceException;

    /**
     * Get local transaction Force an exception if this method is not overridden
     * by the derived class
     */
    public LocalTransaction getLocalTransaction() throws ResourceException
    {
        throw new NotSupportedException("Local Managed Connection must be " //$NON-NLS-1$
                + "implemented to support Local Transactions."); //$NON-NLS-1$
    }

    public abstract ManagedConnectionMetaData getMetaData() throws ResourceException;

    /**
     * Get XA resource Force an exception if this method is not overridden by
     * the derived class
     */
    public XAResource getXAResource() throws ResourceException
    {
        throw new NotSupportedException("XA Managed Connection must be " //$NON-NLS-1$
                + "implemented to support XA Transactions."); //$NON-NLS-1$
    }

    /**
     * Getter for auto-commit. When a connection is in an auto-commit mode, an
     * operation on the connection automatically commits after it has been
     * executed. The auto-commit mode must be off if multiple interactions have
     * to be grouped in a single transaction, either local or XA, and committed
     * or rolled back as a unit.
     * 
     * @return boolean
     */
    public boolean getAutoCommit()
    {
        return this.autoCommit;
    }

    /**
     * Setter for autocommit. When a connection is in an auto-commit mode, an
     * operation on the connection automatically commits after it has been
     * executed. The auto-commit mode must be off if multiple interactions have
     * to be grouped in a single transaction, either local or XA, and committed
     * or rolled back as a unit.
     * 
     * @param autoCommit - autocommit flag to set
     */
    public void setAutoCommit(boolean autoCommit)
    {
        this.autoCommit = autoCommit;
    }

    /**
     * Getter for clientID.
     * 
     * @return String
     */
    public String getClientID()
    {
        return this.clientID;
    }

    /**
     * Setter for clientID.
     * 
     * @param clientID - The client id to set
     */
    public void setClientID(String clientID)
    {
        this.clientID = clientID;
    }

    /**
     * Getter for the state of this connection.
     * 
     * @return ConnectionState
     */
    public ConnectionState getConnectionState()
    {
        return this.connectionState;
    }

    /**
     * Setter for the state of this connection. Apart from initialisation this
     * state should only be updated from the derived ManagedConnections.
     * 
     * @param connectionState - The connection state to set
     */
    protected final void setConnectionState(ConnectionState connectionState)
    {
        this.connectionState = connectionState;
    }
}

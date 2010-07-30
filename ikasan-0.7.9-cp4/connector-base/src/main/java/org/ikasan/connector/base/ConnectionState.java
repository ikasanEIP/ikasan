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
package org.ikasan.connector.base;

import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * Connection state defining standard JCA connector states. These states simply
 * reflect the status of the connector's connectivity with the EIS. These can be
 * subsequently interpreted by the calling client (EJB) and used to determine
 * whether this particular state should be reflective of normal or abnormal
 * operation.
 * 
 * @author Ikasan Development Team
 */
public class ConnectionState implements Serializable
{
    /** Serial id */
    private static final long serialVersionUID = 1L;

    /** Logger */
    private static Logger logger = Logger.getLogger(ConnectionState.class);

    /** internal id of the state */
    protected final Integer id;

    /** name depicting this state */
    protected final String state;

    /** wordy description for logging purposes */
    protected final String description;

    /** Component state cannot be identified */
    public static ConnectionState UNKNOWN = new ConnectionState(new Integer(0), "Unknown", "Cannot determine component status");

    /** Disconnected status */
    public static ConnectionState DISCONNECTED = new ConnectionState(new Integer(1), "Disconnected", "Physical connection is not established");

    /** Connected status */
    public static ConnectionState CONNECTED = new ConnectionState(new Integer(2), "Connected", "Physical connection is established, but without a session");

    /** Session established status */
    public static ConnectionState SESSION_OPEN = new ConnectionState(new Integer(3), "Session Open", "Physical connection and session established");

    /**
     * Session closed is no different from simply being connected, but included
     * for convenience
     */
    public static ConnectionState SESSION_CLOSED = new ConnectionState(new Integer(2), "Session Closed",
        "Physical connection is established, but without a session");

    /**
     * constructor
     * 
     * @param id - Initial id for this ConnectionState
     * @param state - Initial state for this ConnectionState
     * @param description - Initial description for this ConnectionState
     */
    protected ConnectionState(final Integer id, final String state, final String description)
    {
        this.id = id;
        this.state = state;
        this.description = description;
    }

    /**
     * Test equality of the Message Function objects based on the function
     * property.
     * 
     * @return id
     */
    public Integer getId()
    {
        logger.debug("Getting id [" + this.id + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.id;
    }

    /**
     * Getter for state
     * 
     * @return the state
     */
    public String getState()
    {
        logger.debug("Getting state [" + this.state + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.state;
    }

    /**
     * Getter for description
     * 
     * @return the description
     */
    public String getDescription()
    {
        logger.debug("Getting description [" + this.description + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.description;
    }

    /**
     * Utility method for determining if we have no physical connection.
     * 
     * @return true if disconnected
     */
    public boolean isDisconnected()
    {
        if (this.id == DISCONNECTED.getId())
        {
            return true;
        }
        return false;
    }

    /**
     * Utility method for determining if we have an open physical connection.
     * 
     * @return true if we have an open connection
     */
    public boolean isConnectionOpen()
    {
        if (this.id >= CONNECTED.getId())
        {
            return true;
        }
        return false;
    }

    /**
     * Utility method for determining if we have established a session.
     * 
     * @return true if we have an open session
     */
    public boolean isSessionOpen()
    {
        if (this.id >= SESSION_OPEN.getId())
        {
            return true;
        }
        return false;
    }

    /**
     * Utility method for determining if the session is closed.
     * 
     * @return true if the the session is closed
     */
    public boolean isSessionClosed()
    {
        if (this.id < SESSION_OPEN.getId())
        {
            return true;
        }
        return false;
    }

    /**
     * Utility method for determining if the physical connection is closed.
     * 
     * @return true if the connection is closed
     */
    public boolean isConnectionClosed()
    {
        if (this.id < CONNECTED.getId())
        {
            return true;
        }
        return false;
    }
}

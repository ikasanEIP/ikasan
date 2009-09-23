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

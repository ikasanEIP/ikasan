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
package org.ikasan.connector.base.exception;

import javax.resource.ResourceException;

/**
 * ConnectionInUseException.
 * 
 * Thrown when we fail to obtain a connection from the ManagedConnectionFactory
 * as they are all currently being used.
 * 
 * @author Ikasan Development Team
 */
public class ConnectionInUseException 
    extends ResourceException 
{

    /** 
     * String returned in ResourceException when no managed connections
     * are available i.e. all in use.
     * This sucks as a way of identifying this situation, only way I can
     * find to do it at present.
     */
    public static final String NO_MANAGED_CONNECTIONS_AVAILABLE = 
        "No ManagedConnections available within configured blocking timeout"; //$NON-NLS-1$

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new connector resource exception with <code>null</code> 
     * as its detail message.
     */
    public ConnectionInUseException() 
    {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public ConnectionInUseException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public ConnectionInUseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     * @since  1.4
     */
    public ConnectionInUseException(Throwable cause)
    {
        super(cause);
    }
    
    /**
     * Determine whether the exception is thrown due to all 
     * managed connections currently being in use.
     * @return boolean
     */
    public boolean isConnectionInUseException()
    {
        ResourceException e = null;
        if(this.getCause() instanceof ResourceException)
        {
            e = (ResourceException)this.getCause();
        }
        
        String eMsg = null;
        if (e != null)
        {
            eMsg = e.getMessage();
        }
        
        if(eMsg == null)
        {
            return false;
        }
        
        if(eMsg.startsWith(NO_MANAGED_CONNECTIONS_AVAILABLE))
        {
            return true;
        }
        
        return false;
    }

}

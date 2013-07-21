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

import org.ikasan.connector.base.ConnectionState;

/**
 * This interface defines the standard 
 * functions for a virtual connection to the EIS.
 *
 * @author Ikasan Development Team
 */  
public interface EISConnection
    extends Connection
{ 
    /**
     * Called by the client (EJB) to close a virtual connection to the EIS
     */
    public void close();

    /**
     * Called to invalidate an existing virtual connection to the EIS
     */
    public void invalidate();
    
    /**
     * Called by the client (EJB) to force a validation of the connection to 
     * the EIS without actually exchanging any real business data.
     * 
     * @throws ResourceException - The exception to throw if validate fails 
     */
    public void validate() throws ResourceException;

    /**
     * Called by the client (EJB) to retrieve the current state
     * of the connector.
     *  
     * The connector state is based on the standard ConnectionState constants.
     * 
     * @return the ConnectionState
     */
    public ConnectionState getConnectionState();
} 
 


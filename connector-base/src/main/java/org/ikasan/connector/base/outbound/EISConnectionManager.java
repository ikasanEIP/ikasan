/*
 * $Id: EISConnectionManager.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/outbound/EISConnectionManager.java $
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
import javax.resource.spi.*; 
import java.io.*; 

import org.apache.log4j.Logger;

/**
 * This class implements a default connection manager for any EIS
 * resource adapter outside of the management of the Application Server.
 * When the application server is used it will control 
 * the connection management itself. To that end it will
 * pass its own ConnectionManager implementation as an argument to
 * the createConnectionFactory method - this class would not be used.
 * 
 * Hence, this class is only provided for completeness of implementation.
 * 
 * @author Ikasan Development Team
 */ 
public abstract class EISConnectionManager 
    implements javax.resource.spi.ConnectionManager, Serializable 
{ 
    /** Serial GUID */
    private static final long serialVersionUID = 3076364861205203561L;
    
    /** Logger */
    private static Logger logger = Logger.getLogger(EISConnectionManager.class);

    /**
     * This is the compulsory method that generates a new (virtual) connection 
     * for clients.
     * 
     * This is a very basic implementation and does not currently support 
     * connection sharing.
     */  
    public Object allocateConnection (ManagedConnectionFactory mcf, ConnectionRequestInfo info) 
        throws ResourceException 
    { 
        logger.info("Called allocateConnection() on a connectionManager " //$NON-NLS-1$
                + "created outside the Application Server."); //$NON-NLS-1$
        ManagedConnection mc = mcf.createManagedConnection(null, info); 
        return mc.getConnection(null, info); 
    } 
} 

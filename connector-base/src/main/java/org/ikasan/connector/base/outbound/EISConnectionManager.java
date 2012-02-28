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

import java.io.Serializable;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;

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

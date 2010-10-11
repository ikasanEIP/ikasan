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

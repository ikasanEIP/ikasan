/* 
 * $Id$
 * $URL$
 *
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.client;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import org.apache.log4j.Logger;

/**
 * Similar in style to Spring's JMSTemplate, this code ensures that all code
 * mean to run against a JCA connection will be supplied with a Connection from
 * the ConnectionFactory, ensuring that that Connection is safely closed when
 * done, regardless of the way the routine completes
 * 
 * 
 * @author Ikasan Development Team
 * 
 */
public class ConnectionTemplate
{
    /**
     * Logger to use
     */
    private static Logger logger = Logger.getLogger(ConnectionTemplate.class);
    /**
     * ConnectionFactory from which to retrieve connections
     */
    private ConnectionFactory connectionFactory;
    /**
     * Connection specifics
     */
    private ConnectionSpec connectionSpec;

    /**
     * Constructor
     * 
     * @param connectionFactory The factory for the connections
     * @param connectionSpec The connection spec
     */
    public ConnectionTemplate(ConnectionFactory connectionFactory,
            ConnectionSpec connectionSpec)
    {
        super();
        this.connectionFactory = connectionFactory;
        this.connectionSpec = connectionSpec;
        if (connectionFactory == null)
        {
            throw new IllegalArgumentException(
                "ConnectionTemplate requires a non null ConnectionFactory");
        }
    }

    /**
     * Execute the action specified by the given action object with a
     * Connection.
     * 
     * @param action callback object that exposes the Connection
     * @return the result object from working with the Connection
     * @throws ResourceException if there is any problem
     */
    public Object execute(ConnectionCallback action) throws ResourceException
    {
        Connection connection = null;
        try
        {
            if (connectionSpec != null)
            {
                connection = connectionFactory.getConnection(connectionSpec);
            }
            else
            {
                connection = connectionFactory.getConnection();
            }
            return action.doInConnection(connection);
        }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * Closes the connection, suppressing any exceptions
     * 
     * @param connection - possibly null, not necessarily open
     */
    public static void closeConnection(Connection connection)
    {
        if (connection != null)
        {
            try
            {
                logger.debug("Attempting to close EIS Connection");
                connection.close();
            }
            catch (ResourceException ex)
            {
                logger.warn("Could not close EIS Connection", ex);
            }
            catch (Throwable ex)
            {
                // We don't trust the EIS provider: It might throw
                // RuntimeException or Error.
                logger.debug("Unexpected exception on closing EIS Connection",
                    ex);
            }
        }
    }
}

/* 
 * $Id: ConnectionTemplate.java 16743 2009-04-22 09:58:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/client-connection/src/main/java/org/ikasan/client/ConnectionTemplate.java $
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

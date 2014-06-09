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
package org.ikasan.setup;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Persistence creation for Wiretap resources.
 * Ikasan Development Team
 */
public class SystemEvent implements PersistenceCreator
{
    /** logger */
    private static Logger logger = Logger.getLogger(SystemEvent.class);

    /** systemEvent table */
    static final String CREATE_SYSTEM_EVENT_TABLE = "create.systemEvent.table";

    private Properties properties;

    private Connection connection;

    public SystemEvent(Connection connection, Properties properties)
    {
        this.connection = connection;
        if(connection == null)
        {
            throw new IllegalArgumentException("connection cannot be 'null'");
        }

        this.properties = properties;
        if(properties == null)
        {
            throw new IllegalArgumentException("properties cannot be 'null'");
        }
    }

    /**
     * SystemEvent required persistence
     * @throws java.sql.SQLException
     */
    public void execute()
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            statement.executeUpdate(properties.getProperty(CREATE_SYSTEM_EVENT_TABLE));
            logger.info("Created SystemEvent Table");
        }
        catch(SQLException e)
        {
            logger.error("SystemEvent persistence creation failed", e);
        }
        finally
        {
            try
            {
                if(statement != null) statement.close();
            }
            catch(SQLException e)
            {
                logger.error("Failed to close resources", e);
            }

        }
    }
}

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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simple persistence creation utility class for the initial creation
 * of underlying persistence tables.
 * Ikasan Development Team
 */
public class Persistence
{
    /** logger */
    private static Logger logger = Logger.getLogger(Persistence.class);

    // required parameters
    static final String DRIVER_CLASS = "driver";
    static final String URL = "url";
    static final String USER = "user";
    static final String PASS = "pass";

    /**
     * standalone entry
     * @param args
     */
    public static void main(String[] args)
    {
        if( System.getProperty(USER) == null || System.getProperty(PASS) == null ||
            System.getProperty(URL) == null || System.getProperty(DRIVER_CLASS) == null )
        {
            usage();
            return;
        }

        new Persistence().execute(
                System.getProperty(USER),
                System.getProperty(PASS),
                System.getProperty(URL),
                System.getProperty(DRIVER_CLASS));
    }

    /**
     * Print usage
     */
    protected static void usage()
    {
        logger.info("java -Duser=username -Dpass=password -Ddriver=com.sybase.jdbc4.jdbc.SybDataSource -Durl=jdbc:sybase:Tds:svc-dbadv_ase_cmi2d:PORT/DATABASE -jar jarFile");
    }

    /**
     * Execute the persistence creation
     * @param username
     * @param password
     * @param url
     * @param driverClass
     */
    public void execute(String username, String password, String url, String driverClass)
    {
        try
        {
            Connection connection = getConnection(driverClass, url, username, password);
            this.createWiretapPersistence(connection);
            this.createConfigurationPersistence(connection);
            this.createModulePersistence(connection);
            this.createConsolePersistence(connection);
            this.createSecurityPersistence(connection);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
     }

    /**
     * Wiretap required persistence
     * @param connection
     * @throws SQLException
     */
    protected void createWiretapPersistence(Connection connection) throws SQLException
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            statement.executeUpdate(SybaseConstants.CREATE_WIRETAP_TABLE);
            logger.info("Created IkasanWiretap Table");

            statement.executeUpdate(SybaseConstants.CREATE_FLOW_EVENT_TRIGGER_TABLE);
            logger.info("Created FlowEventTrigger Table");

            statement.executeUpdate(SybaseConstants.CREATE_FLOW_EVENT_TRIGGER_PARAMS_TABLE);
            logger.info("Created FlowEventTriggerParameters Table");
        }
        finally
        {
            if(statement != null) statement.close();
        }
    }

    /**
     * Configuration required persistence
     * @param connection
     * @throws SQLException
     */
    protected void createConfigurationPersistence(Connection connection) throws SQLException
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            statement.executeUpdate(SybaseConstants.CREATE_CONFIGURATION_TABLE);
            logger.info("Created Configuration Table");

            statement.executeUpdate(SybaseConstants.CREATE_CONFIGURATION_PARAMS_TABLE);
            logger.info("Created ConfigurationParameter Table");
        }
        finally
        {
            if(statement != null) statement.close();
        }
    }

    /**
     * Module required persistence
     * @param connection
     * @throws SQLException
     */
    protected void createModulePersistence(Connection connection) throws SQLException
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            statement.executeUpdate(SybaseConstants.CREATE_STARTUP_MODULE_TABLE);
            logger.info("Created Module StartupControl Table");
        }
        finally
        {
            if(statement != null) statement.close();
        }
    }

    /**
     * Console required persistence
     * @param connection
     * @throws SQLException
     */
    protected void createConsolePersistence(Connection connection) throws SQLException
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            statement.executeUpdate(SybaseConstants.CREATE_CONSOLE_MODULE_TABLE);
            logger.info("Created Console Module Table");
            statement.executeUpdate(SybaseConstants.CREATE_CONSOLE_POINT_TO_POINT_PROFILE_TABLE);
            logger.info("Created Console PointToPointProfile Table");
            statement.executeUpdate(SybaseConstants.CREATE_CONSOLE_POINT_TO_POINT_TABLE);
            logger.info("Created Console PointToPoint Table");
        }
        finally
        {
            if(statement != null) statement.close();
        }
    }

    /**
     * Security required persistence
     * @param connection
     * @throws SQLException
     */
    protected void createSecurityPersistence(Connection connection) throws SQLException
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            statement.executeUpdate(SybaseConstants.CREATE_SECURITY_USERS_TABLE);
            logger.info("Created Security User Table");
            statement.executeUpdate(SybaseConstants.CREATE_SECURITY_AUTHORITIES_TABLE);
            logger.info("Created Security Authority Table");
            statement.executeUpdate(SybaseConstants.CREATE_SECURITY_USERS_AUTHORITIES_TABLE);
            logger.info("Created Security UsersAuthorities Table");
        }
        finally
        {
            if(statement != null) statement.close();
        }
    }

    /**
     * Get a connection to the database
     * @param url
     * @param username
     * @param password
     * @return Connection
     */
    private Connection getConnection(String driver, String url, String username, String password)
    {
        try
        {
            Class.forName(driver);
            logger.info("Connecting to [" + url + "] as [" + username + "]");
            return DriverManager.getConnection(url, username, password);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

}

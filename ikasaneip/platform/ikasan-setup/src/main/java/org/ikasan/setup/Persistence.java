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

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
    static final String CONF = "conf";
    static final String USER = "user";
    static final String PASS = "pass";
    static final String HOST = "host";
    static final String PORT = "port";
    static final String DATABASE = "database";
    static final String DRIVER = "driver";
    static final String URL = "url";

    /** persistence creator instances */
    PersistenceCreator wiretapPersistenceCreator;
    PersistenceCreator configurationPersistenceCreator;
    PersistenceCreator modulePersistenceCreator;
    PersistenceCreator consolePersistenceCreator;
    PersistenceCreator securityPersistenceCreator;

    /** connection to persistence */
    Connection connection;

    /**
     * standalone entry
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            Persistence persistence = new Persistence(
                    System.getProperty(USER),
                    System.getProperty(PASS),
                    System.getProperty(HOST),
                    System.getProperty(PORT),
                    System.getProperty(DATABASE),
                    System.getProperty(CONF));

            persistence.execute();
        }
        catch(RuntimeException e)
        {
            logger.error("Failed to invoke", e);
            usage();
        }
    }

    /**
     * Constructor
     * @param user
     * @param password
     * @param host
     * @param port
     * @param database
     * @param conf
     */
    public Persistence(String user, String password, String host, String port, String database, String conf)
    {
        Properties dbConfiguration = loadConf(conf);

        this.connection = getConnection(
                dbConfiguration.getProperty(DRIVER),
                convertURL(dbConfiguration.getProperty(URL),host, port, database),
                user, password);

        this.wiretapPersistenceCreator = new WiretapPersistence(connection, dbConfiguration);
        this.configurationPersistenceCreator = new ConfigurationPersistence(connection, dbConfiguration);
        this.modulePersistenceCreator = new ModulePersistence(connection, dbConfiguration);
        this.consolePersistenceCreator = new ConsolePersistence(connection, dbConfiguration);
        this.securityPersistenceCreator = new SecurityPersistence(connection, dbConfiguration);
    }

    /**
     * replace host, port, and db in URL string
     * @param url
     * @param hostname
     * @param port
     * @param database
     * @return
     */
    protected String convertURL(String url, String hostname, String port, String database)
    {
        return url.replaceFirst("HOSTNAME", hostname).
                replaceFirst("PORT", port).
                replaceFirst("DATABASE", database);
    }

    /**
     * load configuration for persistence
     * @param conf
     * @return
     */
    protected Properties loadConf(String conf)
    {
        InputStream inputStream = null;
        if(conf != null)
        {
            inputStream = getInputStream(new File(conf));
            if(inputStream != null)
            {
                logger.info("loaded conf [" + conf + "] from file system.");
            }
            else
            {
                inputStream = this.getClass().getClassLoader().getResourceAsStream(conf);
                logger.info("loaded conf [" + conf + "] from classpath.");
            }
        }

        if(inputStream == null)
        {
            return null;
        }

        Properties properties = new Properties();
        try
        {
            properties.loadFromXML(inputStream);
        }
        catch (IOException e)
        {
            logger.error("Failed to load properties " + conf, e);
            return null;
        }

        return properties;
    }

    /**
     * load as file
     * @param file
     * @return
     */
    protected static InputStream getInputStream(File file)
    {
        try
        {
            return new FileInputStream(file);
        }
        catch(FileNotFoundException e)
        {
            logger.debug("Failed to load properties from file system" + file.getName(), e);
            return null;
        }
    }

    /**
     * Print usage
     */
    protected static void usage()
    {
        logger.info("");
        logger.info("Example Usage:");
        logger.info("java -D args where args,");
        logger.info("\t-Dconf=sybase15.xml - load configuration from classpath sybase15.xml resource");
        logger.info("\t-Dconf=confName - configuration properties resource");
        logger.info("\t-Duser=username - database principal");
        logger.info("\t-Dpass=password - database credential");
        logger.info("\t-Dhost=hostname - database hostname");
        logger.info("\t-Dport=port - database port");
        logger.info("\t-Ddatabase=dbName - database name");
        logger.info("");
        logger.info("java -Dconf=sybase15.xml -Duser=username -Dpass=password -Dhost=svc-dbadv_ase_cmi2d -Dport=50100 -Ddatabase=DBNAME -jar jarFile");
    }

    /**
     * Execute the persistence creation
     */
    public void execute()
    {
        this.wiretapPersistenceCreator.execute();
        this.configurationPersistenceCreator.execute();
        this.modulePersistenceCreator.execute();
        this.consolePersistenceCreator.execute();
        this.securityPersistenceCreator.execute();

        try
        {
            this.connection.close();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
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

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
package org.ikasan.component.endpoint.db.messageprovider;

import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.management.ManagedLifecycle;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Implementation of a MessageProvider based on returning a list of File references.
 *
 * @author Ikasan Development Team
 */
public class DbMessageProvider implements MessageProvider<ResultSet>,
        ManagedLifecycle, Configured<DbConsumerConfiguration>
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(DbMessageProvider.class);

    /** DB consumer configuration */
    private DbConsumerConfiguration dbConsumerConfiguration;

    /** db connection **/
    Connection connection;

    @Override
    public ResultSet invoke(JobExecutionContext context)
    {
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            return statement.executeQuery(this.dbConsumerConfiguration.getSqlStatement());
        }
        catch(SQLException e)
        {
            throw new ForceTransactionRollbackException("File processing interrupted by a stop request.");
        }
        finally
        {
            try
            {
                if(statement != null)
                {
                    statement.close();
                }
            }
            catch(SQLException e)
            {
                logger.warn("Error closing statement", e);
            }
        }
    }

    @Override
    public DbConsumerConfiguration getConfiguration()
    {
        return dbConsumerConfiguration;
    }

    @Override
    public void setConfiguration(DbConsumerConfiguration dbConsumerConfiguration)
    {
        this.dbConsumerConfiguration = dbConsumerConfiguration;
    }

    @Override
    public void start()
    {
        try
        {
            Class.forName(this.dbConsumerConfiguration.getDriver());
            connection = DriverManager.getConnection(
                    this.dbConsumerConfiguration.getUrl(),
                    this.dbConsumerConfiguration.getUsername(),
                    this.dbConsumerConfiguration.getPassword());

            logger.info("  - Started embedded managed component [DbMessageProvider]");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop()
    {
        try
        {
            if(connection != null)
            {
                connection.close();
                connection = null;
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        logger.info("  - Stopped embedded managed component [DbMessageProvider]");
    }
}

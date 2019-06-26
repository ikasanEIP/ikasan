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
package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.RequiresAopProxy;
import org.ikasan.component.endpoint.db.messageprovider.DbConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.Scheduler;

/**
 * Ikasan DB scheduled consumer default implementation.
 *
 * @author Ikasan Development Team
 */
public class DbConsumerBuilderImpl extends AbstractScheduledConsumerBuilderImpl<DbConsumerBuilder>
        implements DbConsumerBuilder, RequiresAopProxy
{
    /** sql driver class */
    String driver;

    /** url for the db connection */
    String url;

    /** connection username */
    String username;

    /** connection password */
    String password;

    /** sql to run */
    String sqlStatement;

    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobFactory
     * @param aopProxyProvider
     * @param messageProvider
     */
    public DbConsumerBuilderImpl(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                 AopProxyProvider aopProxyProvider, MessageProvider messageProvider)
    {
        super(scheduler, scheduledJobFactory, aopProxyProvider);
        this.messageProvider = messageProvider;
    }

    @Override
    public DbConsumerBuilder setConfiguration(DbConsumerConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    @Override
    public DbConsumerBuilder setDriver(String driver)
    {
        this.driver = driver;
        return this;
    }

    @Override
    public DbConsumerBuilder setUrl(String url)
    {
        this.url = url;
        return this;
    }

    @Override
    public DbConsumerBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }

    @Override
    public DbConsumerBuilder setPassword(String password)
    {
        this.password = password;
        return this;
    }

    @Override
    public DbConsumerBuilder setSqlStatement(String sqlStatement)
    {
        this.sqlStatement = sqlStatement;
        return this;
    }

    @Override
    protected DbConsumerConfiguration createConfiguration()
    {
        return DbConsumerBuilder.newConfiguration();
    }

    @Override
    public ScheduledConsumer build()
    {
        ScheduledConsumer scheduledConsumer = super.build();

        DbConsumerConfiguration configuration = (DbConsumerConfiguration)scheduledConsumer.getConfiguration();

        if(driver != null)
        {
            configuration.setDriver(this.driver);
        }

        if(url != null)
        {
            configuration.setUrl(this.url);
        }

        if(username != null)
        {
            configuration.setUsername(this.username);
        }

        if(password != null)
        {
            configuration.setPassword(this.password);
        }

        if(sqlStatement != null)
        {
            configuration.setSqlStatement(this.sqlStatement);
        }

        return scheduledConsumer;
    }

}


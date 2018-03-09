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
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ikasan provided scheduled consumer default implementation.
 * This implemnetation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class ScheduledConsumerBuilderImpl implements ScheduledConsumerBuilder, RequiresAopProxy
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(ScheduledConsumerBuilderImpl.class);

    /** default scheduled consumer instance */
    ScheduledConsumer scheduledConsumer;

    /** the scheduledJobFactory */
    ScheduledJobFactory scheduledJobFactory;

    /** AopProxyProvider provider */
    AopProxyProvider aopProxyProvider;

    /** scheduled job name */
    String scheduledJobName;

    /** scheduled job group name */
    String scheduledJobGroupName;

    /**
     * Constructor
     * @param scheduledConsumer
     */
    public ScheduledConsumerBuilderImpl(ScheduledConsumer scheduledConsumer, ScheduledJobFactory scheduledJobFactory,
                                        AopProxyProvider aopProxyProvider)
    {
        this.scheduledConsumer = scheduledConsumer;
        if(scheduledConsumer == null)
        {
            throw new IllegalArgumentException("scheduledConsumer cannot be 'null'");
        }

        this.scheduledJobFactory = scheduledJobFactory;
        this.aopProxyProvider = aopProxyProvider;
    }

    /**
     * Is this successful start of this component critical on flow start.
     * If it can recover post flow start up then its not crititcal.
     * @param criticalOnStartup
     * @return
     */
    public ScheduledConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.scheduledConsumer.setCriticalOnStartup(criticalOnStartup);
        return this;
    }

    /**
     * ConfigurationService identifier for this component configuration.
     * @param configuredResourceId
     * @return
     */
    public ScheduledConsumerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.scheduledConsumer.setConfiguredResourceId(configuredResourceId);
        return this;
    }

    /**
     * Actual runtime configuration
     * @param scheduledConsumerConfiguration
     * @return
     */
    public ScheduledConsumerBuilder setConfiguration(ScheduledConsumerConfiguration scheduledConsumerConfiguration)
    {
        this.scheduledConsumer.setConfiguration(scheduledConsumerConfiguration);
        return this;
    }

    /**
     * Underlying tech providing the message event
     * @param messageProvider
     * @return
     */
    public ScheduledConsumerBuilder setMessageProvider(MessageProvider messageProvider)
    {
        this.scheduledConsumer.setMessageProvider(messageProvider);
        return this;
    }

    /**
     * Implementation of the managed event identifier service - sets the life identifier based on the incoming event.
     * @param managedEventIdentifierService
     * @return
     */
    public ScheduledConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.scheduledConsumer.setManagedEventIdentifierService(managedEventIdentifierService);
        return this;
    }

    /**
     * Give the component a handle directly to the recovery manager
     * @param managedResourceRecoveryManager
     * @return
     */
    public ScheduledConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.scheduledConsumer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        return this;
    }

    /**
     * Override default event factory
     * @param eventFactory
     * @return
     */
    public ScheduledConsumerBuilder setEventFactory(EventFactory eventFactory) {
        this.scheduledConsumer.setEventFactory(eventFactory);
        return this;
    }

    /**
     * Scheduled consumer cron expression
     * @param cronExpression
     * @return
     */
    public ScheduledConsumerBuilder setCronExpression(String cronExpression)
    {
        getConfiguration().setCronExpression(cronExpression);
        return this;
    }

    /**
     * When true the scheduled consumer is immediately called back on completion of flow execution.
     * If false the scheduled consumers cron expression determines the callback.
     * @param eager
     * @return
     */
    public ScheduledConsumerBuilder setEager(boolean eager) {
        getConfiguration().setEager(eager);
        return this;
    }

    /**
     * Whether to ignore call back failures.
     * @param ignoreMisfire
     * @return
     */
    public ScheduledConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire) {
        getConfiguration().setIgnoreMisfire(ignoreMisfire);
        return this;
    }

    /**
     * Specifically set the timezone of the scheduled callback.
     * @param timezone
     * @return
     */
    public ScheduledConsumerBuilder setTimezone(String timezone) {
        getConfiguration().setTimezone(timezone);
        return this;
    }

    @Override
    public ScheduledConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName) {
        this.scheduledJobGroupName = scheduledJobGroupName;
        return this;
    }

    @Override
    public ScheduledConsumerBuilder setScheduledJobName(String scheduledJobName) {
        this.scheduledJobName = scheduledJobName;
        return this;
    }


    private ScheduledConsumerConfiguration getConfiguration()
    {
        ScheduledConsumerConfiguration scheduledConsumerConfiguration = this.scheduledConsumer.getConfiguration();
        if(scheduledConsumerConfiguration == null)
        {
            scheduledConsumerConfiguration = new ScheduledConsumerConfiguration();
            this.scheduledConsumer.setConfiguration(scheduledConsumerConfiguration);
        }

        return scheduledConsumerConfiguration;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public ScheduledConsumer build() {
        if (this.scheduledConsumer.getConfiguration() == null) {
            this.scheduledConsumer.setConfiguration(new ScheduledConsumerConfiguration());
        }

        validateBuilderConfiguration();

        if(this.aopProxyProvider == null)
        {
            scheduledConsumer.setJobDetail( scheduledJobFactory.createJobDetail(scheduledConsumer, ScheduledConsumer.class, this.scheduledJobName, this.scheduledJobGroupName) );
        }
        else
        {
            Job pointcutJob = this.aopProxyProvider.applyPointcut(this.scheduledJobName, scheduledConsumer);
            scheduledConsumer.setJobDetail( scheduledJobFactory.createJobDetail(pointcutJob, ScheduledConsumer.class, this.scheduledJobName, this.scheduledJobGroupName) );
        }

        return this.scheduledConsumer;
    }

    protected void validateBuilderConfiguration()
    {
        if(this.scheduledJobName == null)
        {
            this.scheduledJobName = this.getConfiguration().hashCode() + "-" + System.currentTimeMillis();
            logger.info("scheduledJobName not specified. Defaulted to '" + this.scheduledJobName + "'");
        }

        if(this.scheduledJobGroupName == null)
        {
            this.scheduledJobGroupName = this.getConfiguration().hashCode() + "-" + System.currentTimeMillis();
            logger.info("scheduledJobGroupName not specified. Defaulted to '" + this.scheduledJobGroupName + "'");
        }
    }

    @Override
    public void setAopProxyProvider(AopProxyProvider aopProxyProvider) {
        this.aopProxyProvider = aopProxyProvider;
    }
}


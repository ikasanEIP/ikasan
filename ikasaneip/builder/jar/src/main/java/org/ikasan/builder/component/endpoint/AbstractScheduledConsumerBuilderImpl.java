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
import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ikasan abstract implementation for a scheduled consumer builder.
 *
 * This is the base abstract class for all scheduled driven consumers.
 * This implementation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public abstract class AbstractScheduledConsumerBuilderImpl<BUILDER>
        implements AbstractScheduledConsumerBuilder<BUILDER>, RequiresAopProxy
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(AbstractScheduledConsumerBuilderImpl.class);

     /** scheduler instance */
    Scheduler scheduler;

    /** message provider instance */
    MessageProvider messageProvider;

    /** id for configuration */
    String configuredResourceId;

    /** configuration instance */
    ScheduledConsumerConfiguration configuration;

    /** is this consumer critical on startup */
    Boolean criticalOnStartup;

    /** managed identifer service */
    ManagedEventIdentifierService managedEventIdentifierService;

    /** recovery manager isntance */
    ManagedResourceRecoveryManager managedResourceRecoveryManager;

    /** event factory */
    EventFactory eventFactory;

    /** the scheduledJobFactory */
    ScheduledJobFactory scheduledJobFactory;

    /** AopProxyProvider provider */
    AopProxyProvider aopProxyProvider;

    /** scheduled job name */
    String scheduledJobName;

    /** scheduled job group name */
    String scheduledJobGroupName;

    /** allow cron expression override */
    String cronExpression;

    /** allow eager override */
    Boolean eager;

    /** allow ignoreMisfire override */
    Boolean ignoreMisfire;

    /** allow maxEagerCallbacks override */
    Integer maxEagerCallbacks;

    /** allow timezone override */
    String timezone;

    /**
     * Constructor
     * @param scheduler
     */
    public AbstractScheduledConsumerBuilderImpl(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                                AopProxyProvider aopProxyProvider)
    {
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }

        this.scheduledJobFactory = scheduledJobFactory;
        this.aopProxyProvider = aopProxyProvider;
    }

    /**
     * Is this successful start of this component critical on flow start.
     * If it can recover post flow start up then its not critical.
     * @param criticalOnStartup
     * @return
     */
    @Override
    public BUILDER setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.criticalOnStartup = Boolean.valueOf(criticalOnStartup);
        return (BUILDER)this;
    }

    /**
     * ConfigurationService identifier for this component configuration.
     * @param configuredResourceId
     * @return
     */
    @Override
    public BUILDER setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
        return (BUILDER)this;
    }

    /**
     * Underlying tech providing the message event
     * @param messageProvider
     * @return
     */
    @Override
    public BUILDER setMessageProvider(MessageProvider messageProvider)
    {
        this.messageProvider = messageProvider;
        return (BUILDER)this;
    }

    /**
     * Implementation of the managed event identifier service - sets the life identifier based on the incoming event.
     * @param managedEventIdentifierService
     * @return
     */
    @Override
    public BUILDER setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.managedEventIdentifierService = managedEventIdentifierService;
        return (BUILDER)this;
    }

    /**
     * Give the component a handle directly to the recovery manager
     * @param managedResourceRecoveryManager
     * @return
     */
    @Override
    public BUILDER setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
        return (BUILDER)this;
    }

    /**
     * Override default event factory
     * @param eventFactory
     * @return
     */
    @Override
    public BUILDER setEventFactory(EventFactory eventFactory)
    {
        this.eventFactory = eventFactory;
        return (BUILDER)this;
    }

    /**
     * Scheduled consumer cron expression
     * @param cronExpression
     * @return
     */
    @Override
    public BUILDER setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
        return (BUILDER)this;
    }

    /**
     * When true the scheduled consumer is immediately called back on completion of flow execution.
     * If false the scheduled consumers cron expression determines the callback.
     * @param eager
     * @return
     */
    @Override
    public BUILDER setEager(boolean eager)
    {
        this.eager = Boolean.valueOf(eager);
        return (BUILDER)this;
    }

    /**
     * Set a limit on the maxiumum number of callbacks when eager is set to true.
     * @param maxEagerCallbacks
     * @return
     */
    @Override
    public BUILDER setMaxEagerCallbacks(int maxEagerCallbacks)
    {
        this.maxEagerCallbacks = Integer.valueOf(maxEagerCallbacks);
        return (BUILDER)this;
    }

    /**
     * Whether to ignore call back failures.
     * @param ignoreMisfire
     * @return
     */
    @Override
    public BUILDER setIgnoreMisfire(boolean ignoreMisfire)
    {
        this.ignoreMisfire = Boolean.valueOf(ignoreMisfire);
        return (BUILDER)this;
    }

    /**
     * Specifically set the timezone of the scheduled callback.
     * @param timezone
     * @return
     */
    @Override
    public BUILDER setTimezone(String timezone)
    {
        this.timezone = timezone;
        return (BUILDER)this;
    }

    @Override
    public BUILDER setScheduledJobGroupName(String scheduledJobGroupName)
    {
        this.scheduledJobGroupName = scheduledJobGroupName;
        return (BUILDER)this;
    }

    @Override
    public BUILDER setScheduledJobName(String scheduledJobName)
    {
        this.scheduledJobName = scheduledJobName;
        return (BUILDER)this;
    }

    /**
     * Factory method to return a vanilla scheduled consumer to aid testing
     * @return
     */
    protected ScheduledConsumer getScheduledConsumer()
    {
        return new ScheduledConsumer(scheduler);
    }

    /**
     * Factory method to return a callback scheduled consumer to aid testing
     * @return
     */
    protected ScheduledConsumer getCallbackScheduledConsumer()
    {
        return new org.ikasan.component.endpoint.quartz.consumer.CallBackScheduledConsumer(scheduler);
    }

    /**
     * Create the scheduled consumer instance based on the type of message provider set.
     * If no message provider then default to vanilla message provider.
     *
     * @return ScheduledConsumer
     */
    protected ScheduledConsumer _build()
    {
        ScheduledConsumer scheduledConsumer;
        if(messageProvider == null)
        {
            scheduledConsumer = getScheduledConsumer();
        }
        else
        {
            if(messageProvider instanceof CallBackMessageProvider)
            {
                scheduledConsumer = getCallbackScheduledConsumer();
            }
            else
            {
                scheduledConsumer = getScheduledConsumer();
            }

            scheduledConsumer.setMessageProvider(messageProvider);
        }

        return scheduledConsumer;
    }

    /**
     * Individual scheduled consumers have to create their own configuration instances
     * which will extend ScheduledConsumerConfiguration.
     *
     * @return ScheduledConsumerConfiguration
     */
    protected abstract ScheduledConsumerConfiguration createConfiguration();

    @Override
    public ScheduledConsumer build()
    {
        ScheduledConsumer scheduledConsumer = _build();

        // set attributes on the consumer
        if(criticalOnStartup != null)
        {
            scheduledConsumer.setCriticalOnStartup(criticalOnStartup);
        }

        if(configuredResourceId != null)
        {
            scheduledConsumer.setConfiguredResourceId(configuredResourceId);
        }

        if(managedEventIdentifierService != null)
        {
            scheduledConsumer.setManagedEventIdentifierService(managedEventIdentifierService);
        }

        if(managedResourceRecoveryManager != null)
        {
            scheduledConsumer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        }

        if(eventFactory != null)
        {
            scheduledConsumer.setEventFactory(eventFactory);
        }

        if(this.configuration != null)
        {
            scheduledConsumer.setConfiguration(configuration);
        }

        if(scheduledConsumer.getConfiguration() == null)
        {
            scheduledConsumer.setConfiguration( createConfiguration() );
        }

        if(cronExpression != null)
        {
            scheduledConsumer.getConfiguration().setCronExpression(cronExpression);
        }

        if(maxEagerCallbacks != null)
        {
            scheduledConsumer.getConfiguration().setMaxEagerCallbacks(maxEagerCallbacks.intValue());
        }

        if(eager != null)
        {
            scheduledConsumer.getConfiguration().setEager(eager.booleanValue());
        }

        if(ignoreMisfire != null)
        {
            scheduledConsumer.getConfiguration().setIgnoreMisfire(ignoreMisfire.booleanValue());
        }

        if(timezone != null)
        {
            scheduledConsumer.getConfiguration().setTimezone(timezone);
        }

        if(this.scheduledJobName == null)
        {
            this.scheduledJobName = scheduledConsumer.getConfiguration().hashCode() + "-" + System.currentTimeMillis();
            logger.info("scheduledJobName not specified. Defaulted to '" + this.scheduledJobName + "'");
        }

        if(this.scheduledJobGroupName == null)
        {
            this.scheduledJobGroupName = scheduledConsumer.getConfiguration().hashCode() + "-" + System.currentTimeMillis();
            logger.info("scheduledJobGroupName not specified. Defaulted to '" + this.scheduledJobGroupName + "'");
        }

        if(this.aopProxyProvider == null)
        {
            scheduledConsumer.setJobDetail( scheduledJobFactory.createJobDetail(scheduledConsumer, getScheduledConsumerClass(), this.scheduledJobName, this.scheduledJobGroupName) );
        }
        else
        {
            Job pointcutJob = this.aopProxyProvider.applyPointcut(this.scheduledJobName, scheduledConsumer);
            scheduledConsumer.setJobDetail( scheduledJobFactory.createJobDetail(pointcutJob, getScheduledConsumerClass(), this.scheduledJobName, this.scheduledJobGroupName) );
        }

        return scheduledConsumer;
    }

    protected Class<? extends ScheduledConsumer> getScheduledConsumerClass(){
        return ScheduledConsumer.class;
    }

    @Override
    public void setAopProxyProvider(AopProxyProvider aopProxyProvider)
    {
        this.aopProxyProvider = aopProxyProvider;
    }
}


package org.ikasan.ootb.scheduler.agent.module.boot.builder;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.endpoint.ScheduledConsumerBuilderImpl;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.ScheduledConsumerEnhanced;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.Scheduler;

public class ScheduledConsumerEnhancedBuilderImpl extends ScheduledConsumerBuilderImpl
{
    private Scheduler localScheduler;
    /**
     * Constructor
     *
     * @param scheduler
     * @param scheduledJobFactory
     * @param aopProxyProvider
     */
    public ScheduledConsumerEnhancedBuilderImpl(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, AopProxyProvider aopProxyProvider) {
        super(scheduler, scheduledJobFactory, aopProxyProvider);
        localScheduler = scheduler;
    }

    @Override
    protected ScheduledConsumer getScheduledConsumer() {
        return new ScheduledConsumerEnhanced(localScheduler);
    }
}

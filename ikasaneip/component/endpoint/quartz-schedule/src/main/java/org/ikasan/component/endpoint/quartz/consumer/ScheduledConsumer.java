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
package org.ikasan.component.endpoint.quartz.consumer;

import org.ikasan.scheduler.ScheduledComponent;
import org.ikasan.spec.event.Resubmission;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.component.endpoint.quartz.HashedEventIdentifierServiceImpl;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.*;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.quartz.*;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * This test class supports the <code>Consumer</code> class.
 *
 * @author Ikasan Development Team
 */
@DisallowConcurrentExecution
@SuppressWarnings("unchecked")
public class ScheduledConsumer<T>
        implements ManagedResource, Consumer<EventListener, EventFactory>, ConfiguredResource<ScheduledConsumerConfiguration>, Job, ScheduledComponent<JobDetail>, ResubmissionService<T>
{
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(ScheduledConsumer.class);

    private static String EAGER_CALLBACK_COUNT = "eagerCallbackCount";

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    /**
     * consumer event factory
     */
    private EventFactory<FlowEvent<?, ?>> flowEventFactory;

    /**
     * default event identifier service - can be overridden via the setter
     */
    protected ManagedEventIdentifierService<?, T> managedEventIdentifierService = new HashedEventIdentifierServiceImpl();

    /**
     * consumer event listener
     */
    private EventListener eventListener;

    /**
     * configuredResourceId
     */
    private String configuredResourceId;

    /**
     * consumer configuration
     */
    private ScheduledConsumerConfiguration consumerConfiguration;

    /** is this a critical resource to cause startup failure */
    private boolean criticalOnStartup;

    /**
     * job detail wired by spring config.
     */
    private JobDetail jobDetail;

    /**
     * default messageProvider is set to QuartzMessageProvider - can be overridden via the setter
     */
    private MessageProvider<?> messageProvider = new QuartzMessageProvider();

    /**
     * Recovery manager for this Managed Resource and any extending implementations of it
     */
    protected ManagedResourceRecoveryManager managedResourceRecoveryManager;

    /** resubmission event factory */
    private ResubmissionEventFactory<Resubmission> resubmissionEventFactory;

    /**
     * Constructor
     *
     * @param scheduler the Quartz Scheduler
     */
    public ScheduledConsumer(Scheduler scheduler)
    {
        this.scheduler = scheduler;
        if (scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }
    }

    /**
     * Start the underlying tech
     */
    public void start()
    {
        try
        {
            JobKey jobkey = jobDetail.getKey();
            TriggerBuilder triggerBuilder = newTriggerFor(jobkey.getName(), jobkey.getGroup());
            Trigger trigger = getBusinessTrigger(triggerBuilder);
            Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
            logger.info("Scheduled consumer for flow ["
                    + jobkey.getName()
                    + "] module [" + jobkey.getGroup()
                    + "] starting at [" + scheduledDate + "]");
        }
        catch (SchedulerException | ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected TriggerBuilder newTriggerFor(String name, String group)
    {
        return newTrigger().withIdentity(name, group);
    }

    /**
     * Stop the scheduled job and triggers
     */
    public void stop()
    {
        try
        {
            JobKey jobKey = jobDetail.getKey();
            if (this.scheduler.checkExists(jobKey))
            {
                this.scheduler.deleteJob(jobKey);
            }
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Is the underlying tech actively running
     *
     * @return isRunning
     */
    public boolean isRunning()
    {
        try
        {
            if (this.scheduler.isShutdown() || this.scheduler.isInStandbyMode())
            {
                return false;
            }
            JobKey jobKey = jobDetail.getKey();
            return this.scheduler.checkExists(jobKey);
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback from the scheduler.
     *
     * @param context the JobExecutionContext
     */
    public void execute(JobExecutionContext context)
    {
        try
        {
            T t = (T) messageProvider.invoke(context);
            this.invoke(t);

            if(this.getConfiguration().isEager())
            {
                Trigger trigger = context.getTrigger();

                // potentially more data so use eager trigger
                if(t != null)
                {
                    invokeEagerSchedule(trigger);
                }
                // no more data and if callback is from an eager trigger then switch back to the business trigger
                else if(isEagerCallback(trigger))
                {
                    scheduleAsBusinessTrigger(trigger);
                }

                // else do not change the business trigger
            }
        }
        catch (ForceTransactionRollbackException thrownByRecoveryManager)
        {
            throw thrownByRecoveryManager;
        }
        catch (Throwable thr)
        {
            thr.printStackTrace();
            managedResourceRecoveryManager.recover(thr);
        }
    }

    protected boolean isEagerCallback(Trigger trigger)
    {
        return trigger.getJobDataMap().containsKey(EAGER_CALLBACK_COUNT);
    }

    /**
     * Logic to determine how to manage the eager trigger schedule.
     * @param trigger
     * @throws SchedulerException
     */
    protected void invokeEagerSchedule(Trigger trigger) throws SchedulerException
    {
        Integer eagerCallbacks = (Integer)trigger.getJobDataMap().get(EAGER_CALLBACK_COUNT);
        if(eagerCallbacks == null)
        {
            eagerCallbacks = new Integer(0);
        }

        // if data available and maxEagerCallbacks is not set or less than max
        if ((consumerConfiguration.getMaxEagerCallbacks() == 0 || eagerCallbacks < consumerConfiguration.getMaxEagerCallbacks()) )
        {
            // schedule the eager trigger
            scheduleAsEagerTrigger(trigger, ++eagerCallbacks);
        }
        else
        {
            scheduleAsBusinessTrigger(trigger);
        }
    }

    /**
     * Invoke the eventListener with the incoming mes
     * @param message the message
     */
    public void invoke(T message)
    {
        boolean isRecovering = managedResourceRecoveryManager.isRecovering();

        if (message != null)
        {
            FlowEvent<?, ?> flowEvent = createFlowEvent(message);
            this.eventListener.invoke(flowEvent);
        }
        else
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("'null' returned from MessageProvider. Flow not invoked");
            }
        }

        if(isRecovering)
        {
            // We need to restart the business schedule PRIOR to cancelling the recovery
            // otherwise the change in state on cancelling recovery reports the
            // consumer as stopped as the business schedule isn't active.
            // Starting it before the cancelAll should not cause any issues
            // as we only allow one quartz callback at a time and so will get
            // blocked until this recovery schedule has completed.

            // only start this consumer if its not currently running or purposefully paused.
            if(!this.isRunning() && !this.isPaused())
            {
                // clear any jobDetail based recovery jobs and triggers
                this.stop();

                // restore the business scheduled job and triggers
                this.start();
            }
        }
    }

    /**
     * Quick workaround to determine if this consumer has been stopped as part of a pause
     * rather than stopped as part of a complete flow stop.
     * @return true if the event listener is paused
     */
    protected boolean isPaused()
    {
        if(eventListener instanceof Flow)
        {
            return ((Flow)eventListener).isPaused();
        }
        return false;
    }

    /* (non-Javadoc)
	 * @see org.ikasan.spec.resubmission.ResubmissionService#submit(java.lang.Object)
	 */
	@Override
	public void onResubmission(T event)
	{
        boolean isRecovering = managedResourceRecoveryManager.isRecovering();

        if (event != null)
        {
            FlowEvent<?, ?> flowEvent = createFlowEvent(event);

            Resubmission resubmission = this.resubmissionEventFactory.newResubmissionEvent(flowEvent);
            
            this.eventListener.invoke(resubmission);
        }
        else
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("'null' returned from MessageProvider. Flow not invoked");
            }
        }

        if(isRecovering)
        {
            // cancelAll the recovery schedule if still active
            // could be the flow has already cancelled this, so check
            if(managedResourceRecoveryManager.isRecovering())
            {
                managedResourceRecoveryManager.cancel();
            }

            // only start this consumer if its not currently running or purposefully paused.
            if(!this.isRunning() && !this.isPaused())
            {
                // clear any jobDetail based recovery jobs and triggers
                this.stop();

                // restore the business scheduled job and triggers
                this.start();
            }
        }
	}

    @Override
    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory)
    {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    /**
     * Override this is you want control over the flow event created by this
     * consumer
     *
     * @param message the message
     * @return a FlowEvent created from the message
     */
    protected FlowEvent<?, ?> createFlowEvent(T message)
    {
        return this.flowEventFactory
                .newEvent(this.managedEventIdentifierService.getEventIdentifier(message), message);
    }

    /**
     *  Trigger Scheduler now.
     */
    public void scheduleAsEagerTrigger(Trigger oldTrigger, int eagerCallback) throws SchedulerException
    {
        try
        {
            TriggerBuilder oldTriggerBuilder = oldTrigger.getTriggerBuilder();
            Trigger newTrigger = oldTriggerBuilder.usingJobData(EAGER_CALLBACK_COUNT, eagerCallback).
                    startNow().
                    withSchedule(simpleSchedule().withMisfireHandlingInstructionNextWithRemainingCount()).
                    build();

            Date scheduledDate;
            if(this.scheduler.checkExists(oldTrigger.getKey()))
            {
                scheduledDate = scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
            }
            else
            {
                scheduledDate = scheduler.scheduleJob(newTrigger);
            }

            if(logger.isDebugEnabled())
            {
                logger.debug("Rescheduled consumer for flow ["
                        + newTrigger.getKey().getName()
                        + "] module [" + newTrigger.getKey().getGroup()
                        + "] on eager callback schedule [" + scheduledDate + "]");
            }
        }
        catch (SchedulerException e)
        {
            if(this.isRunning())
            {
                throw e;
            }
        }
    }

    /**
     *  Trigger Scheduler now.
     */
    public void scheduleAsBusinessTrigger(Trigger oldTrigger) throws SchedulerException
    {
        try
        {
            Trigger newTrigger = getBusinessTrigger(oldTrigger.getTriggerBuilder());
            newTrigger.getJobDataMap().clear();

            Date scheduledDate;
            if(this.scheduler.checkExists(oldTrigger.getKey()))
            {
                scheduledDate = scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
            }
            else
            {
                scheduledDate = scheduler.scheduleJob(newTrigger);
            }

            if(logger.isDebugEnabled())
            {
                logger.debug("Rescheduled consumer for flow ["
                        + newTrigger.getKey().getName()
                        + "] module [" + newTrigger.getKey().getGroup()
                        + "] on business callback schedule [" + scheduledDate + "]");
            }
        }
        catch (ParseException e)
        {
            throw new SchedulerException(e);
        }
        catch (SchedulerException e)
        {
            if(this.isRunning())
            {
                throw e;
            }
        }
    }

    public void setEventFactory(EventFactory flowEventFactory)
    {
        this.flowEventFactory = flowEventFactory;
    }

    public ManagedEventIdentifierService<?, T> getManagedEventIdentifierService()
    {
        return managedEventIdentifierService;
    }

    public void setManagedEventIdentifierService(ManagedEventIdentifierService<?, T> managedEventIdentifierService)
    {
        this.managedEventIdentifierService = managedEventIdentifierService;
    }

    public EventListener getEventListener()
    {
        return eventListener;
    }

    public void setEventListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    /**
     * Set the consumer event listener
     */
    public void setListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    public ScheduledConsumerConfiguration getConfiguration()
    {
        return consumerConfiguration;
    }

    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    public void setConfiguration(ScheduledConsumerConfiguration consumerConfiguration)
    {
        this.consumerConfiguration = consumerConfiguration;

        // pass configuration to messageProvider if this is configured
        if(messageProvider instanceof Configured)
        {
            ((Configured)messageProvider).setConfiguration(consumerConfiguration);
        }
    }

    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    public void setMessageProvider(MessageProvider<T> messageProvider)
    {
        this.messageProvider = messageProvider;

        // pass configuration to messageProvider if this is configured
        if(messageProvider instanceof Configured && consumerConfiguration != null)
        {
            ((Configured)messageProvider).setConfiguration(consumerConfiguration);
        }
    }

    public MessageProvider<?> getMessageProvider()
    {
        return this.messageProvider;
    }

    /**
     * Method factory for creating a cron trigger
     *
     * @return jobDetail
     * @throws java.text.ParseException
     */
    protected Trigger getBusinessTrigger(TriggerBuilder triggerBuilder) throws ParseException
    {
        CronScheduleBuilder cronScheduleBuilder = cronSchedule(this.consumerConfiguration.getCronExpression());
        if (this.consumerConfiguration.isIgnoreMisfire())
        {
            cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
        if (this.consumerConfiguration.getTimezone() != null && this.consumerConfiguration.getTimezone().length() > 0)
        {
            cronScheduleBuilder.inTimeZone(TimeZone.getTimeZone(this.consumerConfiguration.getTimezone()));
        }

        // start the business schedule 1 second in the future to ensure we
        // do not immediately callback on submission to quartz
        triggerBuilder.withSchedule(cronScheduleBuilder)
                .startAt(new Date(System.currentTimeMillis() + 1000));

        return triggerBuilder.build();
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.component.endpoint.Consumer#getEventFactory()
     */
    public EventFactory getEventFactory()
    {
        return this.flowEventFactory;
    }

    @Override
    public void startManagedResource()
    {
        if(messageProvider instanceof ManagedResource)
        {
            ((ManagedResource)messageProvider).startManagedResource();
        }
    }

    @Override
    public void stopManagedResource()
    {
        if(messageProvider instanceof ManagedResource)
        {
            ((ManagedResource)messageProvider).stopManagedResource();
        }
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
        if(messageProvider instanceof ManagedResource)
        {
            ((ManagedResource)messageProvider).setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        }
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return this.criticalOnStartup;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.criticalOnStartup = criticalOnStartup;
    }

    @Override
    public void setJobDetail(JobDetail jobDetail)
    {
        this.jobDetail = jobDetail;
    }

    @Override
    public JobDetail getJobDetail()
    {
        return this.jobDetail;
    }

}

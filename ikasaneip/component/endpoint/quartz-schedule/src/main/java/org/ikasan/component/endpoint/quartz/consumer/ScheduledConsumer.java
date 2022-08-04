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

import org.ikasan.component.endpoint.quartz.recovery.service.ScheduledJobRecoveryService;
import org.ikasan.component.endpoint.quartz.recovery.service.ScheduledJobRecoveryServiceFactory;
import org.ikasan.scheduler.ScheduledComponent;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.Resubmission;
import org.ikasan.spec.management.ManagedLifecycle;
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
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

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

    // anything beginning with Ikasan is an internal Ikasan property reference.
    public static String EAGER_CALLBACK_COUNT = "IkasanEagerCallbackCount";

    public static String PERSISTENT_RECOVERY = "IkasanPersistentRecovery";

    public static String CRON_EXPRESSION = "IkasanCronExpression";

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
    protected ScheduledConsumerConfiguration consumerConfiguration;

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

    protected ScheduledJobRecoveryService scheduledJobRecoveryService;

    /**
     * Constructor
     *
     * @param scheduler the Quartz Scheduler
     */
    public ScheduledConsumer(Scheduler scheduler)
    {
        this(scheduler, ScheduledJobRecoveryServiceFactory.getInstance());
    }

    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobRecoveryService
     */
    public ScheduledConsumer(Scheduler scheduler, ScheduledJobRecoveryService scheduledJobRecoveryService)
    {
        this.scheduler = scheduler;
        if (scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }

        this.scheduledJobRecoveryService = scheduledJobRecoveryService;
        if (scheduledJobRecoveryService == null)
        {
            throw new IllegalArgumentException("scheduledJobRecoveryService cannot be 'null'");
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
            String jobName = jobkey.getName();
            if(getConfiguration().getJobName() != null)
            {
                jobName = getConfiguration().getJobName();
            }

            String jobGroupName = jobkey.getGroup();
            if(getConfiguration().getJobGroupName() != null)
            {
                jobGroupName = getConfiguration().getJobGroupName();
            }

            // get all configured business expressions (expression and expressions) as a single list
            // and create uniquely named triggers for each
            List<String> cronExpressions = consumerConfiguration.getConsolidatedCronExpressions();
            Set<Trigger> triggers = new HashSet<>(cronExpressions.size());
            for(String cronExpression:cronExpressions)
            {
                String jobNameIteration = jobName + "_" + cronExpression.hashCode();
                TriggerBuilder triggerBuilder = newTriggerFor(jobNameIteration, jobGroupName);

                // check if last invocation was successful, if so schedule the business trigger otherwise create a persistent recovery trigger
                Trigger trigger = null;
                if (consumerConfiguration.isPersistentRecovery() && scheduledJobRecoveryService.isRecoveryRequired(jobNameIteration, jobGroupName, consumerConfiguration.getRecoveryTolerance()))
                {
                    // if unsuccessful, schedule a callback immediately if within tolerance of recovery
                    trigger = newTriggerFor(jobNameIteration, jobGroupName)
                        .startNow()
                        .withSchedule(simpleSchedule().withMisfireHandlingInstructionFireNow()).build();
                    trigger.getJobDataMap().put(PERSISTENT_RECOVERY, PERSISTENT_RECOVERY);
                }
                else
                {
                    // if successful then just add business trigger
                    trigger = getBusinessTrigger(triggerBuilder, cronExpression);
                }

                trigger.getJobDataMap().put(CRON_EXPRESSION, cronExpression);
                triggers.add(trigger);
            }

            if(getConfiguration().getPassthroughProperties() != null)
            {
                for(Trigger trigger: triggers)
                {
                    for(Map.Entry<String,String> entry:getConfiguration().getPassthroughProperties().entrySet())
                    {
                        trigger.getJobDataMap().put(entry.getKey(), entry.getValue());
                    }
                }
            }

            StringBuilder logStringBuilder = new StringBuilder();
            for(Trigger trigger: triggers)
            {
                logStringBuilder.append("Job [" + trigger.getKey() + " with firetime [" + trigger.getNextFireTime() + "] description [" + trigger.getDescription() + "]; ");
            }

            scheduleJobTriggers(jobDetail, triggers, true);
            logger.info("Started scheduled consumer for flow ["
                + jobkey.getName()
                + "] module [" + jobkey.getGroup()
                + "] " + logStringBuilder);
        }
        catch (SchedulerException | ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Added access to triggers for this jobDetail to aid in testing.
     * NOTEL: This is not part of the public contract and is explicit for Scheduler testing.
     * @return
     * @throws SchedulerException
     */
    public Set<Trigger> getTriggers() throws SchedulerException
    {
        JobKey jobKey = this.jobDetail.getKey();
        List triggers = this.scheduler.getTriggersOfJob(jobKey);
        return Set.copyOf(triggers);
    }

    /**
     * Factory method to aid testing.
     * @param jobDetail
     * @param triggers
     * @param replace
     * @throws SchedulerException
     */
    protected void scheduleJobTriggers(JobDetail jobDetail, Set<Trigger> triggers, boolean replace) throws SchedulerException
    {
        scheduler.scheduleJob(jobDetail, triggers, replace);
    }

    protected TriggerBuilder newTriggerFor(String name, String group)
    {
        return TriggerBuilder.newTrigger().withIdentity(name, group).withDescription(getConfiguration().getDescription());
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
            List<? extends Trigger> triggers = this.scheduler.getTriggersOfJob(jobKey);
            if(triggers.isEmpty())
            {
                return false;
            }

            return true;
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
            // are we in a runtime fail / recovery scenario invoked from the Recovery Manager
            boolean isRecovering = managedResourceRecoveryManager.isRecovering();

            // TODO - should this be saved after the event invocation?
            // only persist schedule for misfire if a business schedule ie not a recovery manager schedule
            if(!isRecovering && this.consumerConfiguration.isPersistentRecovery())
            {
                this.scheduledJobRecoveryService.save(context);
            }

            T t = (T) messageProvider.invoke(context);
            this.invoke(t);

            // we were recovering, but are now ok so restore eager or business trigger
            if(isRecovering)
            {
                if(this.getConfiguration().isEager() && t != null)
                {
                    invokeEagerSchedule(context.getTrigger());
                }
                else
                {
                    scheduleAsBusinessTrigger(context.getTrigger());
                }

                // cancel any remnants of the recovery
                this.managedResourceRecoveryManager.cancel();
            }
            else
            {
                if(this.getConfiguration().isEager())
                {
                    // potentially more data so use eager trigger
                    if(t != null)
                    {
                        invokeEagerSchedule(context.getTrigger());
                    }
                    // no more data and if callback is from an eager trigger then switch back to the business trigger
                    else if(isEagerCallback(context.getTrigger()))
                    {
                        scheduleAsBusinessTrigger(context.getTrigger());
                    }

                    // else do not change the business trigger
                }
                else
                {
                    // if persistent recovery callback then reschedule business cron
                    if(isPersistentRecoveryTrigger(context.getTrigger()))
                    {
                        scheduleAsBusinessTrigger(context.getTrigger());
                    }
                }
            }
        }
        catch (ForceTransactionRollbackForEventExclusionException thrownByRecoveryManager)
        {
            // reschedule immediately to allow the event to be excluded
            // assumes we will get the same event again
            try
            {
                scheduleAsEagerTrigger(context.getTrigger(), 0);
                throw thrownByRecoveryManager;
            }
            catch (SchedulerException e)
            {
                throw new RuntimeException(e);
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

    protected boolean isPersistentRecoveryTrigger(Trigger trigger)
    {
        return trigger.getJobDataMap().containsKey(PERSISTENT_RECOVERY);
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
                logger.debug("'null' value resubmitted. Flow not invoked");
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
            Date triggerStartTime = new Date(System.currentTimeMillis() + 1000);

            TriggerBuilder oldTriggerBuilder = oldTrigger.getTriggerBuilder();
            Trigger newTrigger = oldTriggerBuilder.usingJobData(EAGER_CALLBACK_COUNT, eagerCallback)
                .startAt(triggerStartTime)
                .withSchedule(simpleSchedule()
                    .withMisfireHandlingInstructionFireNow())
                .build();

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
    protected void scheduleAsBusinessTrigger(Trigger oldTrigger) throws SchedulerException
    {
        try
        {
            String cronExpression = (String)oldTrigger.getJobDataMap().get(CRON_EXPRESSION);
            Trigger newTrigger = getBusinessTrigger(oldTrigger.getTriggerBuilder(), cronExpression);
            newTrigger.getJobDataMap().clear();     // clear any passed state from the trigger
            newTrigger.getJobDataMap().put(CRON_EXPRESSION, cronExpression);  // think we need to keep cron expression value

            if(getConfiguration().getPassthroughProperties() != null)
            {
                for (Map.Entry<String, String> entry : getConfiguration().getPassthroughProperties().entrySet())
                {
                    newTrigger.getJobDataMap().put(entry.getKey(), entry.getValue());
                }
            }

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
    protected Trigger getBusinessTrigger(TriggerBuilder triggerBuilder, String cronExpression) throws ParseException
    {
        CronScheduleBuilder cronScheduleBuilder = cronSchedule(cronExpression);
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
        return triggerBuilder.withSchedule(cronScheduleBuilder)
                .startAt(new Date(System.currentTimeMillis() + 1000)).build();
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
        else if(messageProvider instanceof ManagedLifecycle)
        {
            ((ManagedLifecycle)messageProvider).start();
        }
    }

    @Override
    public void stopManagedResource()
    {
        if(messageProvider instanceof ManagedResource)
        {
            ((ManagedResource)messageProvider).stopManagedResource();
        }
        else if(messageProvider instanceof ManagedLifecycle)
        {
            ((ManagedLifecycle)messageProvider).stop();
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

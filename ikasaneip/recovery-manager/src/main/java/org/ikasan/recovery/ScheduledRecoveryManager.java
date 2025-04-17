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
package org.ikasan.recovery;

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.*;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.event.ForceTransactionRollbackForEventExclusionException;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.recovery.RecoveryManager;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Scheduled based stateful Recovery implementation.
 * 
 * @author Ikasan Development Team
 */
@DisallowConcurrentExecution
public class ScheduledRecoveryManager<ID> implements RecoveryManager<ExceptionResolver, FlowInvocationContext, ID>, Job,
        IsExclusionServiceAware, IsErrorReportingServiceAware, IsFlowAware
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(ScheduledRecoveryManager.class);

    /** recovery job name */
    public static final String RECOVERY_JOB_NAME = "recoveryJob_";

    /** recovery job trigger name */
    private static final String RECOVERY_JOB_TRIGGER_NAME = "recoveryJobTrigger_";

    /** flow to stop and start for recovery */
    private Flow flow;

    /** scheduler */
    private Scheduler scheduler;
    
    /** scheduled job factory for creating Quartz specific jobs */
    private ScheduledJobFactory scheduledJobFactory;
    
    /** flow name */
    private String flowName;
    
    /** module name */
    private String moduleName;

    /** exception resolver */
    private ExceptionResolver exceptionResolver;
    
    /** recovery attempts */
    volatile int recoveryAttempts;
    
    /** keep a handle on the previous component name comparison */
    private String previousComponentName;
    
    /** keep a handle on the previous action for comparison */
    private ExceptionAction previousExceptionAction;
    
    /** unrecoverable status */
    private boolean isUnrecoverable = false;

    /** optional managed resources to stop/start on recovery */
    private List<FlowElement<ManagedResource>> managedResources;

    /** Event Exclusion Service */
    private ExclusionService exclusionService;

    /** Error Reporting Service */
    private ErrorReportingService errorReportingService;

    private JobKey recoveryJobKey;
    private boolean isConsumerMultiThreaded = false;
    private boolean isEventBaseRecovery = false;
    private boolean isFlowStartBasedRecovery = false;

    /**
     * Constructor
     * @param scheduler the Scheduler
     * @param scheduledJobFactory a ScheduledJobFactory
     * @param flowName the name of the flow to which this manager is attached
     * @param moduleName the module name
     */
    public ScheduledRecoveryManager(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, String flowName, String moduleName)
    {
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be null");
        }

        this.scheduledJobFactory = scheduledJobFactory;
        if(scheduledJobFactory == null)
        {
            throw new IllegalArgumentException("scheduledJobFactory cannot be null");
        }

        this.flowName = flowName;
        if(flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be null");
        }

        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be null");
        }
    }

    /**
     * Set a specific exception resolver
     * @param exceptionResolver an ExceptionResolver
     */
    public void setResolver(ExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
    }

    /**
     * Get the specific exception resolver
     * @return exceptionResolver
     */
    public ExceptionResolver getResolver()
    {
        return this.exceptionResolver;
    }

    /**
     * Are we currently running an active recovery. 
     * @return boolean
     */
    public boolean isRecovering()
    {
        try
        {
            return this.scheduler.isStarted() && this.recoveryAttempts > 0;
        }
        catch(SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Common resolution of the action for the componentName and throwable instance
     *
     * @param action the given ExceptionAction
     * @param componentName the name of the component that threw the original exception
     * @param throwable  the exception that was thrown
     * @param id the identifier of the FlowEvent
     */
    protected void recover(ExceptionAction action, String componentName, Throwable throwable, ID id, boolean isEventBaseRecovery)
    {
        this.isEventBaseRecovery = isEventBaseRecovery;
        if(action instanceof StopAction)
        {
            if(isRecovering())
            {
                cancelAll();
            }

            this.flow.recoveryStop();

            this.isUnrecoverable = true;
            logger.info("Stopped flow [" + flowName +  "] module [" + moduleName + "]");
            
            throw new ForceTransactionRollbackException(action.toString(), throwable);
        }

        // simple delay retry action
        else if(action instanceof RetryAction)
        {
            RetryAction retryAction = (RetryAction)action;
            this.flow.recoveryStop();

            try
            {
                if(!isRecovering())
                {
                    startRecovery(retryAction);
                }
                else
                {
                    // TODO - not 100% identification of same issue, but sufficient
                    if(this.previousExceptionAction != null && this.previousExceptionAction.equals(retryAction)
                        && this.previousComponentName != null && this.previousComponentName.equals(componentName))
                    {
                        continueRecovery(retryAction);
                    }
                    else
                    {
                        cancelAll();
                        startRecovery(retryAction);
                    }
                }
                
                this.previousComponentName = componentName;
                this.previousExceptionAction = retryAction;
            }
            catch (SchedulerException e)
            {
                throw new RuntimeException(e);
            }
            
            throw new ForceTransactionRollbackException(action.toString(), throwable);
        }

        // cron expression based delay retry action
        else if(action instanceof ScheduledRetryAction)
        {
            ScheduledRetryAction scheduledRetryAction = (ScheduledRetryAction)action;

            this.flow.recoveryStop();

            try
            {
                if(!isRecovering())
                {
                    startRecovery(scheduledRetryAction);
                }
                else
                {
                    // TODO - not 100% identification of same issue, but sufficient
                    if(this.previousExceptionAction != null && this.previousExceptionAction.equals(scheduledRetryAction)
                    && this.previousComponentName != null && this.previousComponentName.equals(componentName))
                    {
                        continueRecovery(scheduledRetryAction);
                    }
                    else
                    {
                        cancelAll();
                        startRecovery(scheduledRetryAction);
                    }
                }

                this.previousComponentName = componentName;
                this.previousExceptionAction = scheduledRetryAction;
            }
            catch (SchedulerException e)
            {
                throw new RuntimeException(e);
            }

            throw new ForceTransactionRollbackException(action.toString(), throwable);
        }
        else
        {
            // undefine/unsupported action - stop consumer in error and throw exception to rollback
            if(isRecovering())
            {
                cancelAll();
            }

            this.flow.recoveryStop();

            this.isUnrecoverable = true;
            logger.info("Stopped flow [" + flowName +  "] module [" + moduleName + "] due to Unsupported action [" + action + "]");
            throw new ForceTransactionRollbackException(action.toString(), throwable);
        }
    }

    /**
     * Execute recovery based on the specified component name and exception. This is usually called when a flow fails to start
     * and no identifier is present
     * @param componentName the name of the component that threw the exception (usually the consumer)
     * @param throwable the exception
     */
    @Override
    public void recover(String componentName, Throwable throwable)
    {
        this.recover(componentName, throwable, false);
    }

    @Override
    public void recoverOnStart(String componentName, Throwable throwable)
    {
        // We set the flag that states that the recovery is as a result
        // of a exception that occurred attempting to start the flow.
        this.isFlowStartBasedRecovery = true;
        this.recover(componentName, throwable, false);
    }

    @Override
    public void recover(String componentName, Throwable throwable, boolean isEventBaseRecovery)
    {
        ExceptionAction action = resolveAction(componentName, throwable);
        if(action instanceof IgnoreAction)
        {
            return;
        }

        this.errorReportingService.notify(componentName, throwable, action.toString());
        this.recover(action, componentName, throwable, null, isEventBaseRecovery);
    }

    /**
     * Execute recovery based on the specified flowInvocationContext, exception, event and event identifier.
     * @param flowInvocationContext
     * @param throwable
     * @param event
     * @param identifier
     * @param <T>
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void recover(FlowInvocationContext flowInvocationContext, Throwable throwable, T event, ID identifier)
    {
        String lastComponentName = flowInvocationContext.getLastComponentName();
        ExceptionAction action = resolveAction(lastComponentName, throwable);
        flowInvocationContext.setFinalAction(getFinalAction(action));
        if(action instanceof IgnoreAction)
        {
            return;
        }

        String errorUri = this.errorReportingService.notify(lastComponentName, event, throwable, action.toString());
        flowInvocationContext.setErrorUri(errorUri);
        if(action instanceof ExcludeEventAction)
        {
            this.exclusionService.addBlacklisted(identifier, errorUri, flowInvocationContext);
            throw new ForceTransactionRollbackForEventExclusionException(action.toString(), throwable);
        }

        this.recover(action, lastComponentName, throwable, identifier, true);
    }

    protected FinalAction getFinalAction(ExceptionAction exceptionAction)
    {
        if (exceptionAction instanceof ExcludeEventAction)
        {
            return FinalAction.EXCLUDE;
        }
        else if (exceptionAction instanceof IgnoreAction)
        {
            return FinalAction.IGNORE;
        }
        else
        {
            return FinalAction.ROLLBACK;
        }
    }

    /**
     * Is the situation unrecoverable.
     */
    public boolean isUnrecoverable()
    {
        return this.isUnrecoverable;
    }
    
    /**
     * Start a new scheduled recovery job.
     * @param retryAction
     * @throws SchedulerException
     */
    private synchronized void startRecovery(RetryAction retryAction)
        throws SchedulerException
    {
        logger.info(String.format("Recovery manager starting to recover flow[%s]. The Quartz Scheduler is " +
            "currently managing the following recovery jobs[%s]", this.flowName, this.getCurrentScheduledRecoveryJobs()));

        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, ScheduledRecoveryManager.class, RECOVERY_JOB_NAME + this.flowName , this.moduleName);
        recoveryJobKey = recoveryJobDetail.getKey(); // store the jobkey for this recovery execution so it can be cancelled in-flight
        Trigger recoveryJobTrigger = newRecoveryTrigger(retryAction.getDelay(), RECOVERY_JOB_NAME + this.flowName);

        if(!scheduler.checkExists(this.recoveryJobKey)) {
            Date scheduled = this.scheduler.scheduleJob(recoveryJobDetail, recoveryJobTrigger);

            recoveryAttempts = 1;
            logger.info("Recovery [" + recoveryAttempts + "/"
                + ((retryAction.getMaxRetries() < 0) ? "unlimited" : retryAction.getMaxRetries())
                + "] flow [" + flowName + "] module [" + moduleName + "] started at ["
                + scheduled + "]");
        }
        else {
            logger.info(String.format("JobKey[%s] already has a recovery job scheduled for this retry action[%s]. This is likely due to the " +
                "recovery manager managing a multi threaded consumer!", this.recoveryJobKey, retryAction));
        }
    }

    /**
     * Start a new scheduled recovery job.
     * @param scheduledRetryAction
     * @throws SchedulerException
     */
    private synchronized void startRecovery(ScheduledRetryAction scheduledRetryAction) throws SchedulerException
    {
        logger.info(String.format("Recovery manager starting to recover flow[%s]. The Quartz Scheduler is " +
            "currently managing the following recovery jobs[%s]", this.flowName, this.getCurrentScheduledRecoveryJobs()));

        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, ScheduledRecoveryManager.class, RECOVERY_JOB_NAME + this.flowName , this.moduleName);
        recoveryJobKey = recoveryJobDetail.getKey(); // store the jobkey for this recovery execution so it can be cancelled in-flight
        Trigger recoveryJobTrigger = newRecoveryTrigger(scheduledRetryAction.getCronExpression(), RECOVERY_JOB_NAME + this.flowName);

        if(!scheduler.checkExists(this.recoveryJobKey)) {
            Date scheduled = this.scheduler.scheduleJob(recoveryJobDetail, recoveryJobTrigger);

            recoveryAttempts = 1;
            logger.info("Recovery [" + recoveryAttempts + "/"
                + ((scheduledRetryAction.getMaxRetries() < 0) ? "unlimited" : scheduledRetryAction.getMaxRetries())
                + "] flow [" + flowName + "] module [" + moduleName + "] started at ["
                + scheduled + "] with cronExpression[" + scheduledRetryAction.getCronExpression() + "]");
        }
        else {
            logger.info(String.format("JobKey[%s] already has a recovery job scheduled for this scheduled return action[%s]. This is likely due to the " +
                "recovery manager managing a multi threaded consumer!", this.recoveryJobKey, scheduledRetryAction));
        }
    }

    /**
     * Continue an in progress recovery based on the retry action.
     * @param retryAction
     */
    private synchronized void continueRecovery(RetryAction retryAction) throws SchedulerException
    {
        recoveryAttempts++;

        if(retryAction.getMaxRetries() != RetryAction.RETRY_INFINITE && recoveryAttempts > retryAction.getMaxRetries())
        {
            cancelAll();
            this.isUnrecoverable = true;

            // TODO - define a better exception!?!
            throw new RuntimeException("Exhausted maximum retries.");
        }

        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, ScheduledRecoveryManager.class, RECOVERY_JOB_NAME + this.flowName , this.moduleName);
        recoveryJobKey = recoveryJobDetail.getKey(); // store the jobkey for this recovery execution so it can be cancelled in-flight
        Trigger recoveryJobTrigger = newRecoveryTrigger(retryAction.getDelay(), RECOVERY_JOB_NAME + this.flowName);


        logger.info(String.format("Recovery manager continuing to recover flow[%s]. The Quartz Scheduler is " +
            "currently managing the following recovery jobs[%s]", this.flowName, this.getCurrentScheduledRecoveryJobs()));

        Optional<Trigger> optionalTrigger = (Optional<Trigger>) this.scheduler.getTriggersOfJob(recoveryJobKey).stream().findFirst();
        if(this.scheduler.checkExists(recoveryJobKey)
            && optionalTrigger.isPresent()
            &&  optionalTrigger.get().getNextFireTime() != null
            && optionalTrigger.get().getNextFireTime().after(new Date()))
        {
            logger.info("Recovery in progress flow ["
                + flowName + "] module [" + moduleName
                + "]. No additional recoveries will be scheduled!");
        }
        else if(!this.scheduler.checkExists(recoveryJobKey) ||
            // if we have a key in the scheduler but it has already fired, we have encountered a thread id clash
            (this.scheduler.checkExists(recoveryJobKey)
                && ((optionalTrigger.isPresent() && optionalTrigger.get().getNextFireTime() == null)
                ||
                (optionalTrigger.isPresent()
                && optionalTrigger.get().getNextFireTime() != null
                && optionalTrigger.get().getNextFireTime().before(new Date())))
            )
        )
        {
            if(this.scheduler.checkExists(recoveryJobKey)) {
                // add the timestamp suffix to the key in order to de-duplicate
                // if the scheduler contains a job in the past we need to create a unique key
                // key in order to schedule a new recovery job.
                String recoveryJobName = RECOVERY_JOB_NAME + this.flowName + System.currentTimeMillis();
                recoveryJobDetail = scheduledJobFactory.createJobDetail(this, ScheduledRecoveryManager.class, recoveryJobName, this.moduleName);
                recoveryJobTrigger = newRecoveryTrigger(retryAction.getDelay(), recoveryJobName);
                recoveryJobKey = recoveryJobDetail.getKey();
            }
            Date scheduled = this.scheduler.scheduleJob(recoveryJobDetail, recoveryJobTrigger);
            logger.info("Recovery [" + recoveryAttempts + "/"
                + ((retryAction.getMaxRetries() < 0) ? "unlimited" : retryAction.getMaxRetries())
                + "] flow [" + flowName + "] module [" + moduleName + "] rescheduled at ["
                + scheduled + "]");
        }

    }

    /**
     * Continue an in progress recovery based on the retry action.
     * @param scheduledRetryAction
     */
    private synchronized void continueRecovery(ScheduledRetryAction scheduledRetryAction) throws SchedulerException
    {
        recoveryAttempts++;

        if(scheduledRetryAction.getMaxRetries() != RetryAction.RETRY_INFINITE && recoveryAttempts > scheduledRetryAction.getMaxRetries())
        {
            this.cancelRecovery(null);
            this.isUnrecoverable = true;

            // TODO - define a better exception!?!
            throw new RuntimeException("Exhausted maximum retries.");
        }

        // nothing else to do as its on a cron expression schedule currently running

    }

    /**
     * Cancel an in progress recovery based on an optional identifier. If the id is null it cancels all jobs THIS recovery manager knows about
     */
    private void cancelRecovery(ID id)
    {
        this.recoveryAttempts = 0;
        this.isFlowStartBasedRecovery = false;
        try
        {
            cancelScheduledJob();
            this.previousComponentName = null;
            this.previousExceptionAction = null;
            logger.info("Recovery all cancelled for flow [" + flowName + "] module [" + moduleName + "]");
        }
        catch(SchedulerException e)
        {
            throw new RuntimeException(e);
        }

    }

    /**
     * Cancel an in progress recovery.
     */
    public void cancelAll()
    {
        this.cancelRecovery(null);
    }

    @Override
    public void cancel(ID identifier)
    {
        this.cancelRecovery(identifier);
    }

    public void initialise()
    {
        this.isUnrecoverable = false;
        this.recoveryAttempts = 0;
        this.previousComponentName = null;
        this.previousExceptionAction = null;
    }

    /**
     * Resolve the incoming component name and exception to an associated action.
     * If the resolver has not been set then return the default stop action.
     * @param componentName the component the threw the error
     * @param throwable the exception
     * @return a resolved ExceptionAction
     */
    private ExceptionAction resolveAction(String componentName, Throwable throwable)
    {
        ExceptionAction action;
        if(this.exceptionResolver == null)
        {
            action = StopAction.instance();
        }
        else
        {
            action = this.exceptionResolver.resolve(componentName, throwable);
        }

        logger.info("RecoveryManager resolving to [" + action.toString()
                + "] for componentName[" + componentName + "] exception [" + throwable.getMessage() + "]", throwable);

        return action;
    }

    /**
     * Factory method for creating a new recovery trigger.
     * @param delay
     * @return Trigger
     */
    protected Trigger newRecoveryTrigger(long delay, String triggerName)
    {
        return newTrigger()
        .withIdentity(triggerKey(triggerName, this.moduleName))
        .startAt(new Date(System.currentTimeMillis() + delay))
        .withSchedule(simpleSchedule().withMisfireHandlingInstructionNextWithRemainingCount())
        .build();
    }

    /**
     * Factory method for creating a new recovery trigger.
     * @param cronExpression
     * @return Trigger
     */
    protected Trigger newRecoveryTrigger(String cronExpression, String triggerName)
    {
        return newTrigger()
                .withIdentity(triggerKey(triggerName, this.moduleName))
                .withSchedule(cronSchedule(cronExpression))
                .build();
    }

    /**
     * Cancel the recovery job with the scheduler.
     * @throws SchedulerException on exception with the Scheduler
     */
    private void cancelScheduledJob() throws SchedulerException
    {
        if(this.isConsumerMultiThreaded) {
            // For multi-threaded consumers, we will remove all jobs for this flow when cancel all is called!
            logger.info(String.format("Cancelling all scheduler jobs for flow [%s]. Current jobs that will be cancelled [%s]",
                this.flowName, this.getCurrentScheduledRecoveryJobsForFlow()));

            for (JobKey jobKey : this.getCurrentScheduledRecoveryJobsForFlow()) {
                try {
                    this.deleteRecoveryJobFromScheduler(jobKey);
                } catch (SchedulerException e) {
                    logger.warn(String.format("An exception occurred removing recovery job [%s] from the scheduler with error [%s]!"
                        , jobKey, e.getMessage()));
                }
            }
        }
        else if (this.scheduler.checkExists(recoveryJobKey))
        {
            this.deleteRecoveryJobFromScheduler(this.recoveryJobKey);
        }
    }

    /**
     * Delete a recovery job from the scheduler if it exists.
     *
     * @param recoveryJobKey the key of the recovery job to be deleted
     * @throws SchedulerException if there is an issue with the scheduler
     */
    private void deleteRecoveryJobFromScheduler(JobKey recoveryJobKey) throws SchedulerException {
        if (this.scheduler.checkExists(recoveryJobKey))
        {
            boolean deletedRecoveryJob = this.scheduler.deleteJob(recoveryJobKey);
            if(deletedRecoveryJob)
            {
                logger.info("Successfully removed recovery job " + recoveryJobKey);
            }
            else
            {
                logger.info("Failed to remove recovery job " + recoveryJobKey);
            }
        }
    }

    /**
     * Callback from the scheduler.
     * @param context the jbo execution context
     */
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            this.flow.recoveryStart();
            if(!isEventBaseRecovery)
            {
                cancelAll();
            }
        }
        catch(Throwable throwable)
        {
            // this situation only occurs on failure of a retry of a
            // critical managed resource or a consumer
            // so we should be good using the previousComponentName which
            // will be equal to the consumer name
            this.recoverOnStart(this.previousComponentName, throwable);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.recovery.RecoveryManager#setManagedResources(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public <L> void setManagedResources(L managedResources)
    {
        this.managedResources = (List) managedResources;
    }

    @Override
    public void setExclusionService(ExclusionService exclusionService)
    {
        this.exclusionService = exclusionService;
    }

    @Override
    public void setErrorReportingService(ErrorReportingService errorReportingService)
    {
        this.errorReportingService = errorReportingService;
    }

    @Override
    public void setFlow(Flow flow) {
        this.flow = flow;
        this.isConsumerMultiThreaded = this.flow.isMultiThreadedCapable();
    }

    /**
     * Retrieves the currently scheduled recovery jobs for a specific flow.
     *
     * @return A Set of JobKeys representing the currently scheduled recovery jobs for the flow
     * @throws SchedulerException if there is an issue retrieving the scheduled recovery jobs
     */
    private Set<JobKey> getCurrentScheduledRecoveryJobsForFlow() throws SchedulerException {
        Set<JobKey> jobKeyList = scheduler.getJobKeys(GroupMatcher.anyGroup());

        return jobKeyList.stream()
            .filter(key -> key.getName().startsWith(RECOVERY_JOB_NAME + this.flowName))
            .collect(Collectors.toSet());
    }

    /**
     * Retrieve the names of currently scheduled recovery jobs.
     *
     * @return A string containing the names of the scheduled recovery jobs for the flow, separated by commas.
     * @throws SchedulerException if there is an issue retrieving the scheduled recovery jobs.
     */
    private String getCurrentScheduledRecoveryJobs() throws SchedulerException {
        Set<JobKey> jobKeyList = scheduler.getJobKeys(GroupMatcher.anyGroup());

        return jobKeyList.stream()
            .filter(key -> key.getName().startsWith(RECOVERY_JOB_NAME + this.flowName))
            .map(key -> key.getName())
            .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isFlowStartBasedRecovery() {
        return this.isFlowStartBasedRecovery;
    }
}

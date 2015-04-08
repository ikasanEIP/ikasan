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

import org.apache.log4j.Logger;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.*;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.recovery.RecoveryManager;
import org.quartz.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Scheduled based stateful Recovery implementation.
 * 
 * @author Ikasan Development Team
 */
@DisallowConcurrentExecution
public class ScheduledRecoveryManager implements RecoveryManager<ExceptionResolver>, Job
{
    /** logger */
    private static Logger logger = Logger.getLogger(ScheduledRecoveryManager.class);

    /** recovery job name */
    protected static final String RECOVERY_JOB_NAME = "recoveryJob_";

    /** consumer recovery job name */
    protected static final String CONSUMER_RECOVERY_JOB_NAME = "consumerRecoveryJob_";

    /** recovery job trigger name */
    protected static final String RECOVERY_JOB_TRIGGER_NAME = "recoveryJobTrigger_";

    /** recovery job trigger name */
    protected static final String IMMEDIATE_RECOVERY_JOB_TRIGGER_NAME = "immediateRecoveryJobTrigger_";

    /** consumer to stop and start for recovery */
    private Consumer<?,?> consumer;

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
    protected int recoveryAttempts;
    
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

    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobFactory
     * @param flowName
     * @param moduleName
     * @param consumer
     */
    public ScheduledRecoveryManager(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, String flowName, String moduleName, Consumer<?,?> consumer, ExclusionService exclusionService)
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

        this.consumer = consumer;
        if(consumer == null)
        {
            throw new IllegalArgumentException("consumer cannot be null");
        }

        this.exclusionService = exclusionService;
        if(exclusionService == null)
        {
            throw new IllegalArgumentException("exclusionService cannot be null");
        }
    }

    /**
     * Set a specific exception resolver
     * @param exceptionResolver
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
            if(this.scheduler.isStarted() && recoveryAttempts > 0)
            {
                return true;
            }
            
            return false;
        }
        catch(SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Common resolution of the action for the componentName and throwable instance
     *
     * @param action
     * @param componentName
     * @param throwable 
     */
    protected void recover(ExceptionAction action, String componentName, Throwable throwable)
    {
        if(action instanceof IgnoreAction)
        {
            return;
        }
        else if(action instanceof StopAction)
        {
            if(isRecovering())
            {
                this.cancel();
            }
            
            this.consumer.stop();
            stopManagedResources();

            this.isUnrecoverable = true;
            logger.info("Stopped flow [" + flowName +  "] module [" + moduleName + "]");
            
            throw new ForceTransactionRollbackException(action.toString(), throwable);
        }

        // simple delay retry action
        else if(action instanceof RetryAction)
        {
            RetryAction retryAction = (RetryAction)action;
            this.consumer.stop();
            stopManagedResources();
                
            try
            {
                if(!isRecovering())
                {
                    startRecovery(retryAction);
                }
                else
                {
                    // TODO - not 100% identification of same issue, but sufficient
                    if(this.previousExceptionAction.equals(retryAction) && this.previousComponentName.equals(componentName))
                    {
                        continueRecovery(retryAction);
                    }
                    else
                    {
                        cancel();
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
            this.consumer.stop();
            stopManagedResources();

            try
            {
                if(!isRecovering())
                {
                    startRecovery(scheduledRetryAction);
                }
                else
                {
                    // TODO - not 100% identification of same issue, but sufficient
                    if(this.previousExceptionAction.equals(scheduledRetryAction) && this.previousComponentName.equals(componentName))
                    {
                        continueRecovery(scheduledRetryAction);
                    }
                    else
                    {
                        cancel();
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
            throw new UnsupportedOperationException("Unsupported action [" + action + "]");
        }
    }

    /**
     * Execute recovery based on the specified component name and exception.
     * @param componentName
     * @param throwable
     */
    public void recover(String componentName, Throwable throwable)
    {
        ExceptionAction action = resolveAction(componentName, throwable);
        logger.info("RecoveryManager resolving to [" + action.toString() + "] for exception ", throwable);
        this.recover(action, componentName, throwable);
    }

    /**
     * Execute recovery based on the specified component name, exception,
     * and event.
     * @param componentName
     * @param throwable
     * @param event
     */
    public <T> void recover(String componentName, Throwable throwable, T event)
    {
        ExceptionAction action = resolveAction(componentName, throwable);
        logger.info("RecoveryManager resolving to [" + action.toString() + "] for exception ", throwable);
        if(action instanceof ExcludeEventAction)
        {
            this.exclusionService.addBlacklisted(event);
            throw new ForceTransactionRollbackException(action.toString(), throwable);
        }

        this.recover(action, componentName, throwable);
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
    private void startRecovery(RetryAction retryAction)
        throws SchedulerException
    {
        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, ScheduledRecoveryManager.class, RECOVERY_JOB_NAME + this.flowName, this.moduleName);
        Trigger recoveryJobTrigger = newRecoveryTrigger(retryAction.getDelay());
        Date scheduled = this.scheduler.scheduleJob(recoveryJobDetail, recoveryJobTrigger);

        recoveryAttempts = 1;
        logger.info("Recovery [" + recoveryAttempts + "/" 
            + ((retryAction.getMaxRetries() < 0) ? "unlimited" : retryAction.getMaxRetries()) 
            + "] flow [" + flowName + "] module [" + moduleName + "] started at ["
            + scheduled + "]");
    }

    /**
     * Start a new scheduled recovery job.
     * @param scheduledRetryAction
     * @throws SchedulerException
     */
    private void startRecovery(ScheduledRetryAction scheduledRetryAction)
            throws SchedulerException
    {
        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, ScheduledRecoveryManager.class, RECOVERY_JOB_NAME + this.flowName, this.moduleName);
        Trigger recoveryJobTrigger = newRecoveryTrigger(scheduledRetryAction.getCronExpression());
        Date scheduled = this.scheduler.scheduleJob(recoveryJobDetail, recoveryJobTrigger);

        recoveryAttempts = 1;
        logger.info("Recovery [" + recoveryAttempts + "/"
                + ((scheduledRetryAction.getMaxRetries() < 0) ? "unlimited" : scheduledRetryAction.getMaxRetries())
                + "] flow [" + flowName + "] module [" + moduleName + "] started at ["
                + scheduled + "] with cronExpression[" + scheduledRetryAction.getCronExpression() + "]");
    }

    /**
     * Continue an in progress recovery based on the retry action.
     * @param retryAction
     */
    private void continueRecovery(RetryAction retryAction) throws SchedulerException
    {
        recoveryAttempts++;

        if(retryAction.getMaxRetries() != RetryAction.RETRY_INFINITE && recoveryAttempts > retryAction.getMaxRetries())
        {
            this.cancel();
            this.isUnrecoverable = true;

            // TODO - define a better exception!?!
            throw new RuntimeException("Exhausted maximum retries.");
        }

        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, ScheduledRecoveryManager.class, RECOVERY_JOB_NAME + this.flowName, this.moduleName);
        Trigger recoveryJobTrigger = newRecoveryTrigger(retryAction.getDelay());
        
        // Only schedule a new recovery if we don't have one in-progress.
        // This can be the case on very high volume feeds where 
        // multiple recoveries are created by in-flight messages 
        // between stop/start of the flow
        if(this.scheduler.checkExists(recoveryJobDetail.getKey()))
        {
            logger.info("Recovery in progress flow [" 
                + flowName + "] module [" + moduleName 
                + "]. No additional recoveries will be scheduled!");
        }
        else
        {
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
    private void continueRecovery(ScheduledRetryAction scheduledRetryAction) throws SchedulerException
    {
        recoveryAttempts++;

        if(scheduledRetryAction.getMaxRetries() != RetryAction.RETRY_INFINITE && recoveryAttempts > scheduledRetryAction.getMaxRetries())
        {
            this.cancel();
            this.isUnrecoverable = true;

            // TODO - define a better exception!?!
            throw new RuntimeException("Exhausted maximum retries.");
        }

        // nothing else to do as its on a cron expression schedule currently running

    }

    /**
     * Cancel an in progress recovery.
     */
    public void cancel()
    {
        recoveryAttempts = 0;
        
        try
        {
            cancelScheduledJob();
            this.previousComponentName = null;
            this.previousExceptionAction = null;
            logger.info("Recovery cancelled for flow [" + flowName + "] module [" + moduleName + "]");

            // for scheduled based consumers we need start them on their normal flow of execution
            if(this.consumer instanceof Job)
            {
                this.consumer.start();
            }
        }
        catch(SchedulerException e)
        {
            throw new RuntimeException(e);
        }
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
     * @param componentName
     * @param throwable
     * @return
     */
    private ExceptionAction resolveAction(String componentName, Throwable throwable)
    {
        if(this.exceptionResolver == null)
        {
            return StopAction.instance();
        }

        return this.exceptionResolver.resolve(componentName, throwable);
    }
    
    /**
     * Factory method for creating a new recovery trigger.
     * @param delay
     * @return Trigger
     */
    protected Trigger newRecoveryTrigger(long delay)
    {
        return newTrigger()
        .withIdentity(triggerKey(RECOVERY_JOB_TRIGGER_NAME + this.flowName, this.moduleName))
        .startAt(new Date(System.currentTimeMillis() + delay))
        .build();
    }

    /**
     * Factory method for creating a new recovery trigger which starts immediately.
     * @return Trigger
     */
    protected Trigger newImmediateRecoveryTrigger()
    {
        return newTrigger()
                .withIdentity(triggerKey(IMMEDIATE_RECOVERY_JOB_TRIGGER_NAME + this.flowName, this.moduleName))
                .startNow()
                .build();
    }

    /**
     * Factory method for creating a new recovery trigger.
     * @param cronExpression
     * @return Trigger
     */
    protected Trigger newRecoveryTrigger(String cronExpression) throws SchedulerException
    {
        try
        {
            return newTrigger()
                    .withIdentity(triggerKey(RECOVERY_JOB_TRIGGER_NAME + this.flowName, this.moduleName))
                    .withSchedule(cronSchedule(cronExpression))
                    .build();
        }
        catch(ParseException e)
        {
            throw new SchedulerException(e);
        }
    }

    /**
     * Cancel the recovery job with the scheduler.
     * @throws SchedulerException
     */
    private void cancelScheduledJob() throws SchedulerException
    {
        JobKey jobKey = new JobKey(RECOVERY_JOB_NAME + this.flowName, this.moduleName);
        this.scheduler.deleteJob(jobKey);

        JobKey consumerJobKey = new JobKey(CONSUMER_RECOVERY_JOB_NAME + this.flowName, this.moduleName);
        if(this.scheduler.checkExists(consumerJobKey))
        {
            this.scheduler.deleteJob(consumerJobKey);
        }
    }

    /**
     * Callback from the scheduler.
     * @param context
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            startManagedResources();

            if(this.consumer instanceof Job)
            {
                Class consumerClass = this.consumer.getClass();
                JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail((Job)this.consumer, consumerClass, CONSUMER_RECOVERY_JOB_NAME + this.flowName, this.moduleName);
                this.scheduler.scheduleJob(recoveryJobDetail, newImmediateRecoveryTrigger());
            }
            else
            {
                this.consumer.start();
            }
        }
        catch(Throwable throwable)
        {
            // this situation only occurs on failure of a retry of a
            // critical managed resource or a consumer
            // so we should be good using the previousComponentName which
            // will be equal to the consumer name
            this.recover(this.previousComponentName, throwable);
        }
    }

    protected void stopManagedResources()
    {
        if(this.managedResources != null)
        {
            for(FlowElement<ManagedResource> flowElement:this.managedResources)
            {
                flowElement.getFlowComponent().stopManagedResource();
            }
        }
    }

    /**
     * Start the components marked as including Managed Resources.
     * These component are started from right to left in the flow.
     */
    protected void startManagedResources()
    {
    	if(this.managedResources != null)
    	{
            List<FlowElement<ManagedResource>> flowElements = this.managedResources;
            for(int index=flowElements.size()-1; index >= 0; index--)
            {
                FlowElement<ManagedResource> flowElement = flowElements.get(index);
                try
                {
                    flowElement.getFlowComponent().startManagedResource();
                    logger.info("Started managed component [" 
                        + flowElement.getComponentName() + "]");
                }
                catch(RuntimeException e)
                {
                    if(flowElement.getFlowComponent().isCriticalOnStartup())
                    {
                        // log issues as these may get resolved by the recovery manager 
                        logger.warn("Failed to start critical component [" 
                                + flowElement.getComponentName() + "] " + e.getMessage(), e);
                        throw e;
                    }
                    else
                    {
                        // just log any issues as these may get resolved by the recovery manager 
                        logger.warn("Failed to start managed component [" 
                                + flowElement.getComponentName() + "] " + e.getMessage(), e);
                    }
                }
            }
    	}
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.recovery.RecoveryManager#setManagedResources(java.lang.Object)
     */
    public <List> void setManagedResources(List managedResources)
    {
        this.managedResources = (java.util.List) managedResources;
    }
}

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

import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.ExceptionAction;
import org.ikasan.exceptionResolver.action.IgnoreAction;
import org.ikasan.exceptionResolver.action.RetryAction;
import org.ikasan.exceptionResolver.action.StopAction;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.recovery.RecoveryManager;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import static org.quartz.TriggerBuilder.*;
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
    
    /** recovery job trigger name */
    protected static final String RECOVERY_JOB_TRIGGER_NAME = "recoveryJobTrigger_";
    
    /** consumer to stop and start for recovery */
    private Consumer<?> consumer;

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

    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobFactory
     * @param flowName
     * @param moduleName
     * @param consumer
     */
    public ScheduledRecoveryManager(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, String flowName, String moduleName, Consumer<?> consumer)
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
     * Execute recovery based on the specified component name and exception.
     * @param componetName
     * @param throwable
     */
    public void recover(String componentName, Throwable throwable)
    {
        logger.info("RecoveryManager invoked", throwable);
        ExceptionAction action = resolveAction(componentName, throwable);
        if(action instanceof IgnoreAction)
        {
            logger.info("No action taken for exception ", throwable);
            return;
        }
        else if(action instanceof StopAction)
        {
            if(isRecovering())
            {
                this.cancel();
            }
            
            this.consumer.stop();
            this.isUnrecoverable = true;
            logger.info("Stopped flow [" + flowName +  "] module [" + moduleName + "]");
            
            throw new ForceTransactionRollbackException(action.toString());
        }
        else if(action instanceof RetryAction)
        {
            RetryAction retryAction = (RetryAction)action;
            
            this.consumer.stop();
                
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
            
            throw new ForceTransactionRollbackException(action.toString());
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported action [" + action + "]");
        }
    }

    /**
     * Execute recovery based on the specified component name, exception,
     * and flowEvent.
     * @param componetName
     * @param throwable
     * @param flowEvent
     */
    public <FlowEvent> void recover(String componentName, Throwable throwable, FlowEvent event)
    {
        this.recover(componentName, throwable);
    }

    /**
     * Is the situation unrecoverable.
     * @param boolean
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
        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, RECOVERY_JOB_NAME + this.flowName, this.moduleName);
        Trigger recoveryJobTrigger = newRecoveryTrigger(retryAction.getDelay());
        Date scheduled = this.scheduler.scheduleJob(recoveryJobDetail, recoveryJobTrigger);

        recoveryAttempts = 1;
        logger.info("Recovery [" + recoveryAttempts + "/" 
            + ((retryAction.getMaxRetries() < 0) ? "unlimited" : retryAction.getMaxRetries()) 
            + "] flow [" + flowName + "] module [" + moduleName + "] started at ["
            + scheduled + "]");
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

        JobDetail recoveryJobDetail = scheduledJobFactory.createJobDetail(this, RECOVERY_JOB_NAME + this.flowName, this.moduleName);
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
     * @param maxRetries
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
     * Cancel the recovery job with the scheduler.
     * @throws SchedulerException
     */
    private void cancelScheduledJob() throws SchedulerException
    {
        JobKey jobKey = new JobKey(RECOVERY_JOB_NAME + this.flowName, this.moduleName);
        this.scheduler.deleteJob(jobKey);
    }

    /**
     * Callback from the scheduler.
     * @param context
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            this.consumer.start();
        }
        catch(Throwable throwable)
        {
            // this situation only occurs on failure of a retry of a consumer
            // so we should be good using the previousComponentName which 
            // will be equal to the consumer name
            this.recover(this.previousComponentName, throwable);
        }
    }

}

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
import org.ikasan.exceptionResolver.action.RetryAction;
import org.ikasan.exceptionResolver.action.StopAction;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.recoveryManager.RecoveryManager;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

/**
 * Scheduled based stateful Recovery implementation.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManager implements RecoveryManager<ExceptionResolver>, StatefulJob
{
    /** logger */
    private static Logger logger = Logger.getLogger(ScheduledRecoveryManager.class);

    /** recovery job trigger name */
    private static final String RECOVERY_JOB_TRIGGER_NAME = "recoveryJobTrigger";
    
    /** consumer to stop and start for recovery */
    private Consumer<?> consumer;

    /** scheduler */
    private Scheduler scheduler;
    
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
     * @param flowName
     * @param moduleName
     * @param consumer
     */
    public ScheduledRecoveryManager(Scheduler scheduler, String flowName, String moduleName, Consumer<?> consumer)
    {
        this(scheduler, flowName, moduleName, consumer, null);
    }

    /**
     * Constructor
     * @param scheduler
     * @param flowName
     * @param moduleName
     * @param consumer
     * @param exceptionResolver
     */
    public ScheduledRecoveryManager(Scheduler scheduler, String flowName, String moduleName, Consumer<?> consumer, ExceptionResolver exceptionResolver)
    {
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be null");
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

        this.exceptionResolver = exceptionResolver;
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
        ExceptionAction action = resolveAction(componentName, throwable);
        if(action instanceof StopAction)
        {
            if(isRecovering())
            {
                this.cancel();
            }
            
            this.consumer.stop();
            this.isUnrecoverable = true;
            logger.info("Stopped flow [" + flowName +  "] module [" + moduleName + "]");
            throw new RuntimeException("stopAction invoked");
        }
        else if(action instanceof RetryAction)
        {
            RetryAction retryAction = (RetryAction)action;
            
            this.consumer.stop();
                
            try
            {
                if(this.previousExceptionAction == null)
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
            
            throw new RuntimeException("retryAction invoked");
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
        JobDetail recoveryJobDetail = newRecoveryJob();
        Trigger recoveryJobTrigger = newRecoveryTrigger(retryAction.getMaxRetries(), retryAction.getDelay());
        recoveryJobTrigger.setStartTime( new Date(System.currentTimeMillis() + retryAction.getDelay()) );
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
    private void continueRecovery(RetryAction retryAction)
    {
        recoveryAttempts++;

        if(retryAction.getMaxRetries() != RetryAction.RETRY_INFINITE && recoveryAttempts > retryAction.getMaxRetries())
        {
            this.cancel();
            this.isUnrecoverable = true;

            // TODO - define a better exception!?!
            throw new RuntimeException("Exhausted maximum retries.");
        }

        logger.info("Recovery [" + recoveryAttempts + "/" 
            + ((retryAction.getMaxRetries() < 0) ? "unlimited" : retryAction.getMaxRetries()) 
            + "] flow [" + flowName + "] module [" + moduleName + "]");
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
     * Factory method for creating a new recovery job
     * @return JobDetail
     */
    protected JobDetail newRecoveryJob()
    {
        return new JobDetail(ScheduledRecoveryManagerJobFactory.RECOVERY_JOB_NAME + this.flowName, 
            ScheduledRecoveryManagerJobFactory.RECOVERY_JOB_GROUP + this.moduleName, ScheduledRecoveryManager.class);
    }
    
    /**
     * Factory method for creating a new recovery trigger.
     * @param maxRetries
     * @param delay
     * @return Trigger
     */
    protected Trigger newRecoveryTrigger(int maxRetries, long delay)
    {
        return TriggerUtils.makeImmediateTrigger(RECOVERY_JOB_TRIGGER_NAME, maxRetries, delay);
    }
    
    /**
     * Cancel the recovery job with the scheduler.
     * @throws SchedulerException
     */
    private void cancelScheduledJob() throws SchedulerException
    {
        this.scheduler.deleteJob(ScheduledRecoveryManagerJobFactory.RECOVERY_JOB_NAME + this.flowName, 
            ScheduledRecoveryManagerJobFactory.RECOVERY_JOB_GROUP + this.moduleName);
    }

    /**
     * Callback from the scheduler.
     * @param context
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        this.consumer.start();
    }

}

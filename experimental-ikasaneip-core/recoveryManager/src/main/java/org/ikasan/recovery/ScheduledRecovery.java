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
import org.ikasan.spec.recoveryManager.Recovery;
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
public class ScheduledRecovery implements Recovery<FlowEvent<?>>
{
    /** logger */
    private static Logger logger = Logger.getLogger(ScheduledRecovery.class);

    /** recovery job name */
    private static final String RECOVERY_JOB_NAME = "recoveryJob";
    
    /** recovery job group */
    private static final String RECOVERY_JOB_GROUP = "recoveryManager";
    
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
    
    /** keep a handle on the previous action for comparison */
    private ExceptionAction previousExceptionAction;
    
    /** unrecoverable status */
    private boolean isUnrecoverable = false;

    /**
     * Constructor
     * @param consumer
     * @param consumerName
     */
    public ScheduledRecovery(Scheduler scheduler, String flowName, String moduleName, Consumer<?> consumer, ExceptionResolver exceptionResolver)
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
        if(exceptionResolver == null)
        {
            throw new IllegalArgumentException("exceptionResolver cannot be null");
        }
    }

    /**
     * Are we currently running an active recovery. 
     * @return boolean
     */
    public boolean isRecovering()
    {
        try
        {
            if(this.scheduler.isStarted() && recoveryAttempts > 1)
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

    public void recover(String componentName, Throwable throwable)
    {
        ExceptionAction action = this.exceptionResolver.resolve(componentName, throwable);
        if(action instanceof StopAction)
        {
            if(isRecovering())
            {
                this.cancel();
            }
            
            this.consumer.stop();
            this.isUnrecoverable = true;
            logger.info("Stopped flow [" + flowName +  "] module [" + moduleName + "]");
            throw new RuntimeException("Rollback all operations and stop");
        }
        else if(action instanceof RetryAction)
        {
            this.retry((RetryAction)action);
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported action [" + action + "]");
        }
    }
    
    public void recover(String componentName, Throwable throwable, FlowEvent<?> event)
    {
        this.recover(componentName, throwable);
    }

    public boolean isUnrecoverable()
    {
        return this.isUnrecoverable;
    }
    
    private void retry(RetryAction retryAction)
    {
        this.consumer.stop();
        recoveryAttempts++;
            
        try
        {
            if(this.previousExceptionAction == null)
            {
                startRecovery(retryAction);
            }
            else
            {
                if(this.previousExceptionAction.equals(retryAction))
                {
                    continueRecovery(retryAction);
                }
                else
                {
                    cancel();
                    startRecovery(retryAction);
                }
            }
            
            this.previousExceptionAction = retryAction;
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
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
        logger.info("Starting recovery at [" + scheduled + "] for  flow [" 
            + flowName + "] module [" + moduleName + "]");
    }

    private void continueRecovery(RetryAction retryAction)
    {
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

    public void cancel()
    {
        recoveryAttempts = 0;
        
        try
        {
            cancelScheduledJob();
        }
        catch(SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Factory method for creating a new recovery job
     * @return JobDetail
     */
    protected JobDetail newRecoveryJob()
    {
        return new JobDetail(RECOVERY_JOB_NAME + this.flowName, 
            RECOVERY_JOB_GROUP + this.moduleName, RecoveryJob.class);
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
        this.scheduler.deleteJob(RECOVERY_JOB_NAME + this.flowName, 
            RECOVERY_JOB_GROUP + this.moduleName);
    }

    /**
     * Implementation class of the recovery job to be invoked by the scheduler.
     * @author Ikasan Development Team
     *
     */
    private class RecoveryJob implements StatefulJob
    {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
        {
            consumer.start();
        }
    }

}

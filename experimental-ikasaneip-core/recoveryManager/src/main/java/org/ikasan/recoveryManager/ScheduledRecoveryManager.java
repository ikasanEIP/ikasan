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
package org.ikasan.recoveryManager;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.exceptionHandler.ExceptionHandler;
import org.ikasan.exceptionHandler.action.ExceptionAction;
import org.ikasan.exceptionHandler.action.ExcludeEventAction;
import org.ikasan.exceptionHandler.action.RetryAction;
import org.ikasan.exceptionHandler.action.StopAction;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.flow.FlowElement;
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
 * Scheduled based Recovery Manager implementation.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManager implements RecoveryManager<FlowEvent>
{
    /** logger */
    private static Logger logger = Logger.getLogger(ScheduledRecoveryManager.class);

    /** recovery job name */
    private static final String RECOVERY_JOB_NAME = "recoveryJob";
    
    /** recovery job group */
    private static final String RECOVERY_JOB_GROUP = "recoveryManager";
    
    /** recovery job trigger name */
    private static final String RECOVERY_JOB_TRIGGER_NAME = "recoveryJobTriggerName";
    
    /** Quartz Scheduler */
    private Scheduler scheduler;
    
    /** Exception Handler */
    private ExceptionHandler exceptionHandler;
    
    /** consumer to stop and start for recovery */
    private Consumer<?> consumer;

    /** consumer name */
    private String consumerName;

    /** recovery attempts */
    private int recoveryAttempts;
    
    /** keep a handle on the previous action for comparison */
    private RetryAction previousRetryAction;
    
    /** unrecoverable status */
    private boolean unrecoverable = false;
    
    /**
     * Constructor
     * @param flowElement
     * @param scheduler
     */
    public ScheduledRecoveryManager(Consumer consumer, String consumerName, ExceptionHandler exceptionHandler, Scheduler scheduler)
    {
        this.consumer = consumer;
        if(consumer == null)
        {
            throw new IllegalArgumentException("consumer cannot be 'null'");
        }
        
        this.consumerName = consumerName;
        if(consumerName == null)
        {
            throw new IllegalArgumentException("consumerName cannot be 'null'");
        }

        this.exceptionHandler = exceptionHandler;
        if(exceptionHandler == null)
        {
            throw new IllegalArgumentException("exceptionHandler cannot be 'null'");
        }
        
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }
    }
    
    public boolean isRecovering()
    {
        try
        {
            return this.scheduler.isStarted();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }


    public void recover(String componentName, Throwable throwable, FlowEvent event)
    {
        this.unrecoverable = false;
        
        ExceptionAction exceptionAction = this.exceptionHandler.handleThrowable(componentName, throwable);
        if(exceptionAction instanceof StopAction)
        {
            this.recover((StopAction)exceptionAction);
        }
        else if(exceptionAction instanceof RetryAction)
        {
            this.recover((RetryAction)exceptionAction);
        }
        else if(exceptionAction instanceof ExcludeEventAction)
        {
            this.recover((ExcludeEventAction)exceptionAction, event);
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported action [" + exceptionAction + "]");
        }
    }

    public void recover(String componentName, Throwable throwable)
    {
        this.recover(componentName, throwable, null);
    }

    private void recover(ExcludeEventAction stopAction, FlowEvent event)
    {
        // do whatever excluded event does
    }
    
    private void recover(StopAction stopAction)
    {
        if(this.isRecovering())
        {
            this.cancelRecovery();
        }
        
        this.consumer.stop();
        this.unrecoverable = true;
        logger.info("Stopped consumer [" + this.consumerName + "]");
        
        throw new RuntimeException("Rollback all operations");
    }
    
    private void recover(RetryAction retryAction)
    {
        this.consumer.stop();
        recoveryAttempts++;
        
        try
        {
            if(isRecovering())
            {
                if(this.previousRetryAction.equals(retryAction))
                {
                    continueScheduledJob(retryAction);
                }
                else
                {
                    cancelRecovery();
                    startScheduledJob(retryAction);
                }
            }
            else
            {
                this.scheduler.start();
                startScheduledJob(retryAction);
            }
            
            this.previousRetryAction = retryAction;
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void cancelRecovery()
    {
        try
        {
            cancelScheduledJob();
            this.scheduler.shutdown();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void continueScheduledJob(RetryAction retryAction)
    {
        if(retryAction.getMaxRetries() != RetryAction.RETRY_INFINITE && recoveryAttempts > retryAction.getMaxRetries())
        {
            this.cancelRecovery();
            // TODO - define a better exception!?!
            throw new RuntimeException("Exhausted maximum retries.");
        }

        logger.info("consumer [" + consumerName + "] recovery attempt [" 
            + recoveryAttempts + "/" 
            + ((retryAction.getMaxRetries() < 0) ? "unlimited" : retryAction.getMaxRetries()) + "]");
    }
    
    private void startScheduledJob(RetryAction retryAction) 
        throws SchedulerException
    {
        JobDetail recoveryJobDetail = new JobDetail(RECOVERY_JOB_NAME, RECOVERY_JOB_GROUP, RecoveryJob.class);
        Trigger recoveryJobTrigger = TriggerUtils.makeImmediateTrigger(RECOVERY_JOB_TRIGGER_NAME, retryAction.getMaxRetries(), retryAction.getDelay());
        recoveryJobTrigger.setStartTime(new Date(System.currentTimeMillis() + retryAction.getDelay()));
        Date scheduled = this.scheduler.scheduleJob(recoveryJobDetail, recoveryJobTrigger);
        logger.info("consumer [" + consumerName + "] recovery scheduled to start at [" + scheduled + "]");
    }
    
    private void cancelScheduledJob() throws SchedulerException
    {
        this.scheduler.deleteJob(RECOVERY_JOB_NAME, RECOVERY_JOB_GROUP);
    }
    
    private class RecoveryJob implements StatefulJob
    {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
        {
            consumer.start();
        }
    }

    public boolean isUnrecoverable()
    {
        return this.unrecoverable;
    }
}

/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.initiator.scheduled.quartz;

import java.util.Date;
import java.util.List;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.AbstractInvocationDrivenInitiator;
import org.ikasan.framework.initiator.InitiatorOperationException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * Quartz implementation of an Ikasan Schedule Driven Initiator.
 * 
 * @author Ikasan Development Team
 */
public class QuartzStatefulScheduledDrivenInitiator extends AbstractInvocationDrivenInitiator implements QuartzSchedulerInitiator
{
    private static final String INITIATOR_JOB_NAME = "initiatorJob";

    public static final String QUARTZ_SCHEDULE_DRIVEN_INITIATOR_TYPE = "QuartzScheduleDrivenInitiator";

    /** Logger */
    private static Logger logger = Logger.getLogger(QuartzStatefulScheduledDrivenInitiator.class);

    /** name for the retry trigger when it exists */
    private static final String RETRY_TRIGGER_NAME = "retry_trigger";


    /** Quartz scheduler */
    protected Scheduler scheduler;
    
    private List<Trigger> triggers;
    
    private JobDetail jobDetail;
    



    /**
     * Constructor.
     * 
     * @param initiatorName The name of the initiator
     * @param name of the Module
     * @param eventProvider The provider of the events to this initiator
     * @param flow The flow leading from this initiator
     * @param exceptionHandler The exception handler for this initiator
     */
    public QuartzStatefulScheduledDrivenInitiator(String initiatorName, String moduleName, EventProvider eventProvider, Flow flow,
            IkasanExceptionHandler exceptionHandler)
    {
        super(initiatorName, moduleName, flow, exceptionHandler, eventProvider);
    }

    /**
     * Setter for scheduler
     * 
     * @param scheduler The scheduler to set
     * @throws SchedulerException 
     */
    public void setScheduler(Scheduler scheduler) 
    {
        this.scheduler = scheduler;
        
    }




    @Override
    protected void startRetryCycle(Integer maxAttempts, long delay) throws InitiatorOperationException
    {
        //ScheduledDrivenQuartzContext sdContext = this.getScheduledDrivenContext(context);
        try
        {
            // pause the existing schedules
            scheduler.pauseJobGroup(getJobGroup());
            
            // create a retry schedule
            Trigger recoveryTrigger = TriggerUtils.makeImmediateTrigger(RETRY_TRIGGER_NAME, maxAttempts, delay);
            recoveryTrigger.setGroup(getTriggerGroup());
            
            recoveryTrigger.setJobName(INITIATOR_JOB_NAME);
            recoveryTrigger.setJobGroup(getJobGroup());          
            
            recoveryTrigger.setStartTime(new Date(System.currentTimeMillis() + delay));


            // add a new trigger to the job
            Date scheduled = this.scheduler.scheduleJob(recoveryTrigger);
            if (logger.isInfoEnabled())
            {
                logger.info("Starting retry cycle on Initiator [" + this.getName() + "] at [" + scheduled + "].");
                if (logger.isDebugEnabled())
                {
                    logger.debug("Added retry trigger [" + recoveryTrigger.getName() + "] group [" + recoveryTrigger.getGroup() + "].");
                }
            }
        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed to start retry cycle on Initiator [" + this.getName() + "] ["
                    + "].", e);
        }
    }





    private String getJobGroup()
    {
        return moduleName+"-"+name;
    }

    @Override
    protected void completeRetryCycle() throws InitiatorOperationException
    {
        try
        {
            // cancel the retry
            //this.cancelRetryTrigger(this.getScheduledDrivenContext(context).getTrigger());
            cancelRetryTrigger();
            // clear retry context
            //context.clearRetry();

            
            // resume normal schedules
            this.scheduler.resumeJobGroup(getJobGroup());
            if (logger.isInfoEnabled())
            {
                logger.info("Successfully completed retry cycle on Initiator [" + this.getName() + "].");
            }
        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed Initiator [" + this.getName() + "] ["
                    + "] on completeRetryCycle.", e);
        }
    }

    @Override
    protected void cancelRetryCycle() throws InitiatorOperationException
    {
        cancelRetryTrigger();
        if (logger.isInfoEnabled())
        {
            logger.info("Cancelled retry cycle on Initiator [" + this.getName() + "].");
        }
    }


    /**
     * Cancels the retry trigger for this job.
     * 
     * @param trigger trigger to cancel
     * @throws InitiatorOperationException Exception if we can't cancel the trigger
     */
    private void cancelRetryTrigger() throws InitiatorOperationException
    {
        retryCount = null;
        
        try
        {
            Trigger recoveryTrigger = getRecoveryTrigger();
            
                if (recoveryTrigger!=null){
                scheduler.unscheduleJob(recoveryTrigger.getName(), recoveryTrigger.getGroup());
                
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Initiator [" + this.getName() + "] trigger [" + recoveryTrigger.getName() + "] group ["
                            + recoveryTrigger.getGroup() + "] cancelled.");
                }
            }

        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed to cancel retry trigger for Initiator [" + this.getName()
                    + "] [" + "].", e);
        }
    }

    /**
     * Retrieves the RecoveryTrigger from the scheduler
     * 
     * @return recover Trigger or null if none exists
     */
    private Trigger getRecoveryTrigger() 
    {
        Trigger trigger = null;
        try
        {
            trigger = scheduler.getTrigger(RETRY_TRIGGER_NAME, getTriggerGroup());
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
        return trigger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.AbstractInitiator#stopInitiator()
     */
    @Override
    protected void stopInitiator() throws InitiatorOperationException
    {
        try
        {
            // pause business schedule jobs
            scheduler.pauseJobGroup(getJobGroup());

            logger.info("Initiator [" + this.getName() + "] stopped.");
        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed to stop Initiator [" + this.getName() + "] [" + "].", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.AbstractInitiator#startInitiator()
     */
    @Override
    protected void startInitiator() throws InitiatorOperationException
    {

        
        
        try
        {
            //if there are no triggers at all, then this must be first time it is started.
            //TODO - change the way stop/start works so that a stopped initiator is one
            //with no triggers at all, just like the initialised states
            if (scheduler.getTriggersOfJob(INITIATOR_JOB_NAME, getJobGroup()).length==0){
                jobDetail = new JobDetail(INITIATOR_JOB_NAME,getJobGroup(), QuartzStatefulJob.class);

                
                //quartz api requires that first trigger registration needs to use scheduler.scheduleJob(jobDetail,firstTrigger)
                //whilst subsequent trigger registrations for the same job use scheduler.scheduleJob(subsequentTrigger)
                //  - where all subsequentTriggers have the jobName and jobGroup set
                boolean firstTrigger = true;
                for(Trigger trigger: triggers){
                    trigger.setGroup(getTriggerGroup());
                    if (firstTrigger){
                        scheduler.scheduleJob(jobDetail, trigger);
                        firstTrigger = false;
                    }else{
                        trigger.setJobGroup(getJobGroup());
                        trigger.setJobName(INITIATOR_JOB_NAME);
                        scheduler.scheduleJob(trigger);
                    }
                }
            }

            
            
            // only restart business schedule jobs
            this.scheduler.resumeJobGroup(getJobGroup());
            logger.info("Initiator [" + this.getName() + "] started.");
        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed to start Initiator [" + this.getName() + "] [" + "].", e);
        }
    }


    
    private String getTriggerGroup()
    {
        return moduleName+"-"+name;
    }

    public String getType()
    {
        return QUARTZ_SCHEDULE_DRIVEN_INITIATOR_TYPE;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#isRecovering()
     */
    public boolean isRecovering()
    {
        return getRecoveryTrigger()!=null;
    }
    

    
    /**
     * Provides access to the underlying scheduler
     * 
     * @return scheduler instance
     */
    public Scheduler getScheduler(){
     return scheduler;   
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.initiator.Initiator#isRunning()
     */
    public boolean isRunning()
    {
        boolean running = false;
        
        //if the scheduler is shutdown we cannot be running
        try
        {
            running = !scheduler.isInStandbyMode()&&!scheduler.isShutdown();
            
            if (running){
                //if any of the triggers for this Initiator's job are either NORMAL or BLOCKED, we are running
                Trigger[] triggersOfJob = scheduler.getTriggersOfJob(INITIATOR_JOB_NAME, getJobGroup());
                boolean foundActiveTrigger = false;
                for (Trigger trigger : triggersOfJob){
                    int triggerState = scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
                    if (triggerState==Trigger.STATE_NORMAL || triggerState==Trigger.STATE_BLOCKED){
                        foundActiveTrigger = true;
                        break;
                    }
                }
                if (!foundActiveTrigger){
                    running = false;
                }
                
            }
            
            
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
        
        
        
        
        

        return running;
    }

    @Override
    protected Logger getLogger()
    {
        return logger;
    }
    
    public void setTriggers(List<Trigger> triggers)
    {
        this.triggers = triggers;
    }

}

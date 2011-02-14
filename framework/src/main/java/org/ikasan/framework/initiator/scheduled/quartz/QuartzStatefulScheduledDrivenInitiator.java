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
package org.ikasan.framework.initiator.scheduled.quartz;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.VisitingInvokerFlow;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.initiator.InitiatorOperationException;
import org.ikasan.framework.monitor.MonitorSubject;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

/**
 * Quartz implementation of an Ikasan Schedule Driven Initiator.
 * 
 * @author Ikasan Development Team
 */
public class QuartzStatefulScheduledDrivenInitiator extends AbstractInitiator implements QuartzDrivenInitiator, MonitorSubject
{
    private static final String INITIATOR_JOB_NAME = "initiatorJob";

    public static final String QUARTZ_SCHEDULE_DRIVEN_INITIATOR_TYPE = "QuartzScheduleDrivenInitiator";

    public static final String REINVOKE_IMMEDIATELY_FLAG = "invokeAgainImmediately";

    /** Logger */
    private static Logger logger = Logger.getLogger(QuartzStatefulScheduledDrivenInitiator.class);

    /** name for the retry trigger when it exists */
    private static final String RETRY_TRIGGER_NAME = "retry_trigger";

    protected EventProvider eventProvider = null;
    
    /** Quartz scheduler */
    protected Scheduler scheduler;
    
    private List<Trigger> triggers;
    
    private JobDetail jobDetail;

	private IkasanExceptionHandler exceptionHandler;
    
	/** Allow Reinvoke Immediately flag to be set, when 1 or more Events is encountered, without error */
	private boolean allowImmediateReinvocationOnEvent = false;

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
        super(moduleName, initiatorName, flow, exceptionHandler);
        
        this.eventProvider = eventProvider;
        this.exceptionHandler = exceptionHandler;
        notifyMonitorListeners();
        
        
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



//	private void invokeFlow(List<Event> events) {
//		// Within the event handling we need to accommodate for a
//		// single event outcome action; and for batched events outcome
//		// action.
//		// The batched events have multiple potential outcome actions,
//		// therefore, we need to use the highest precedent outcome action.
//		IkasanExceptionAction precedentAction = null;
//		for (Event event : events)
//		{
//		    IkasanExceptionAction action = this.getFlow().invoke(event);
//		    if (action != null)
//		    {
//		        if (precedentAction == null || action.getType().isHigherPrecedence(precedentAction.getType()))
//		        {
//		            precedentAction = action;
//		            if (precedentAction.getType().isRollback())
//		            {
//		                // if rollback then we may as well get out now
//		                break;
//		            }
//		        }
//		    }
//		}
//		this.handleAction(precedentAction);
//	}


    @Override
    protected void startRetryCycle(Integer maxAttempts, long delay) throws InitiatorOperationException
    {
        //ScheduledDrivenQuartzContext sdContext = this.getScheduledDrivenContext(context);
        try
        {
            // pause the existing schedules of this initiator job
        	scheduler.pauseJob(INITIATOR_JOB_NAME, this.getJobGroup());
            
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

            
            // resume normal schedules of this initiator job
            this.scheduler.resumeJob(INITIATOR_JOB_NAME, this.getJobGroup());
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
        retryCount = 0;
        
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
    
    /**
     * Standard invocation of an initiator.
     * @param mergedJobDataMap 
     */
    public boolean invoke()
    {
        if (stopping)
        {
            logger.warn("Attempt to invoke an initiator in a stopped state.");
            return false;
        }

        // TODO - this sucks. we are having to sync configuration on the flow
        // because this initiator uses the configuration as part of sourcing events
        flow.sync();
        
        // invoke flow all the time we have event activity
        // invoke flow
        List<Event> events = null;
        try
        {
            events = this.eventProvider.getEvents();
            // If we get no events then we simply resume (see Javadoc of resume method)
            if (events == null || events.size() == 0)
            {
                this.resume();
                return false;
            }
        }
        catch (Throwable eventSourcingThrowable)
        {
            IkasanExceptionAction action = exceptionHandler.handleThrowable(name, eventSourcingThrowable);

            //tell the error service
            logError(null, eventSourcingThrowable, name,action);
            handleAction(action,null);
        }
        invokeFlow(events);
        return allowImmediateReinvocationOnEvent;
    }
    
    /**
     * Accessor for allowImmediateReinvocationOnEvent
     * @return allowImmediateReinvocationOnEvent
     */
    public boolean isAllowImmediateReinvocationOnEvent() {
		return allowImmediateReinvocationOnEvent;
	}

	/**
	 * Setter for allowImmediateReinvocationOnEventS
	 * @param allowImmediateReinvocationOnEvent
	 */
	public void setAllowImmediateReinvocationOnEvent(
			boolean allowImmediateReinvocationOnEvent) {
		this.allowImmediateReinvocationOnEvent = allowImmediateReinvocationOnEvent;
	}
	

    /**
     * Returns the Quartz job name of this initiator
     * @return
     */
    public String getInitiatorJobName()
    {
        return INITIATOR_JOB_NAME;
    }
}

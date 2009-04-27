/* 
 * $Id: QuartzStatefulScheduledDrivenInitiator.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/scheduled/quartz/QuartzStatefulScheduledDrivenInitiator.java $
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
import org.ikasan.framework.initiator.AbstractContextDrivenInitiator;
import org.ikasan.framework.initiator.InitiatorContext;
import org.ikasan.framework.initiator.InitiatorOperationException;
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
public class QuartzStatefulScheduledDrivenInitiator extends AbstractContextDrivenInitiator
{
    /** Logger */
    private static Logger logger = Logger.getLogger(QuartzStatefulScheduledDrivenInitiator.class);

    /** literal suffix for retry trigger names */
    private static final String RETRY_CYCLE_SUFFIX = "_retryCycle";

    /** Ikasan Event provider instance for this initiator */
    protected EventProvider eventProvider;

    /** Quartz scheduler */
    protected Scheduler scheduler;

    /**
     * Constructor.
     * 
     * @param initiatorName The name of the initiator
     * @param eventProvider The provider of the events to this initiator
     * @param flow The flow leading from this initiator
     * @param exceptionHandler The exception handler for this initiator
     */
    public QuartzStatefulScheduledDrivenInitiator(String initiatorName, EventProvider eventProvider, Flow flow,
            IkasanExceptionHandler exceptionHandler)
    {
        super(initiatorName, flow, exceptionHandler);
        this.eventProvider = eventProvider;
    }

    /**
     * Setter for scheduler
     * 
     * @param scheduler The scheduler to set
     */
    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    /**
     * Internal method for getting payloads, creating the event and invoking the flow.
     */
    @Override
    protected void invokeFlow(InitiatorContext context)
    {
        // invoke flow
        try
        {
            List<Event> events = this.eventProvider.getEvents();
            if (events == null || events.size() == 0)
            {
                this.handleAction(null, context);
                return;
            }
            // Within the event handling we need to accommodate for a
            // single event outcome action; and for batched events outcome
            // action.
            // The batched events have multiple potential outcome actions,
            // therefore, we need to use the highest precedent outcome action.
            IkasanExceptionAction precedentAction = null;
            for (Event event : events)
            {
                IkasanExceptionAction action = this.getFlow().invoke(event);
                if (action != null)
                {
                    if (precedentAction == null || action.getType().isHigherPrecedence(precedentAction.getType()))
                    {
                        precedentAction = action;
                        if (precedentAction.getType().isRollback())
                        {
                            // if rollback then we may as well get out now
                            break;
                        }
                    }
                }
            }
            this.handleAction(precedentAction, context);
        }
        catch (ResourceException e)
        {
            this.handleAction(this.getExceptionHandler().invoke(this.getName(), e), context);
        }
    }

    @Override
    protected void startRetryCycle(InitiatorContext context, int limit, long delay) throws InitiatorOperationException
    {
        ScheduledDrivenQuartzContext sdContext = this.getScheduledDrivenContext(context);
        try
        {
            // pause the existing schedules
            JobDetail jobDetail = sdContext.getJobDetail();
            this.scheduler.pauseJobGroup(jobDetail.getGroup());
            // create a retry schedule
            Trigger trigger = TriggerUtils.makeImmediateTrigger(this.getName() + RETRY_CYCLE_SUFFIX, limit, delay);
            trigger.setStartTime(new Date(System.currentTimeMillis() + delay));
            trigger.setJobName(jobDetail.getName());
            trigger.setJobGroup(jobDetail.getGroup());
            trigger.setGroup(jobDetail.getGroup());
            // add a new trigger to the job
            Date scheduled = this.scheduler.scheduleJob(trigger);
            if (logger.isInfoEnabled())
            {
                logger.info("Starting retry cycle on Initiator [" + this.getName() + "] at [" + scheduled + "].");
                if (logger.isDebugEnabled())
                {
                    logger.debug("Added retry trigger [" + trigger.getName() + "] group [" + trigger.getGroup() + "].");
                }
            }
        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed to start retry cycle on Initiator [" + this.getName() + "] ["
                    + "].", e);
        }
    }

    @Override
    protected void completeRetryCycle(InitiatorContext context) throws InitiatorOperationException
    {
        try
        {
            // cancel the retry
            this.cancelRetryTrigger(this.getScheduledDrivenContext(context).getTrigger());
            // clear retry context
            context.clearRetry();
            // resume normal schedules
            this.scheduler.resumeJobGroup(this.getName());
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
    protected void cancelRetryCycle(InitiatorContext context) throws InitiatorOperationException
    {
        this.cancelRetryTrigger(this.getScheduledDrivenContext(context).getTrigger());
        if (logger.isInfoEnabled())
        {
            logger.info("Cancelled retry cycle on Initiator [" + this.getName() + "].");
        }
    }

    /**
     * Retrieve the scheduled driven specifics from the initiator context.
     * 
     * @param context The context for the initiator
     * @return ScheduledDrivenQuartzContext
     */
    private ScheduledDrivenQuartzContext getScheduledDrivenContext(InitiatorContext context)
    {
        if (context instanceof ScheduledDrivenQuartzContext) return (ScheduledDrivenQuartzContext) context;
        throw new RuntimeException("Unknown context class for this initiator.");
    }

    /**
     * Cancels the retry trigger for this job.
     * 
     * @param trigger trigger to cancel
     * @throws InitiatorOperationException Exception if we can't cancel the trigger
     */
    private void cancelRetryTrigger(Trigger trigger) throws InitiatorOperationException
    {
        try
        {
            this.scheduler.unscheduleJob(trigger.getName(), trigger.getGroup());
            if (logger.isDebugEnabled())
            {
                logger.debug("Initiator [" + this.getName() + "] trigger [" + trigger.getName() + "] group ["
                        + trigger.getGroup() + "] cancelled.");
            }
        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed to cancel retry trigger for Initiator [" + this.getName()
                    + "] [" + "].", e);
        }
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
            if (getState().isRecovering())
            {
                Trigger trigger = this.scheduler.getTrigger(this.getName() + RETRY_CYCLE_SUFFIX, this.getName());
                this.cancelRetryTrigger(trigger);
            }
            else
            {
                // pause business schedule jobs
                this.scheduler.pauseJobGroup(this.getName());
            }
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
            // only restart business schedule jobs
            this.scheduler.resumeJobGroup(this.getName());
            logger.info("Initiator [" + this.getName() + "] started.");
        }
        catch (SchedulerException e)
        {
            throw new InitiatorOperationException("Failed to start Initiator [" + this.getName() + "] [" + "].", e);
        }
    }
}

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
package org.ikasan.consumer.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * This test class supports the <code>Consumer</code> class.
 * 
 * @author Ikasan Development Team
 */
@DisallowConcurrentExecution
public class ScheduledConsumer 
    implements Consumer<EventListener, EventFactory>, ConfiguredResource<ScheduledConsumerConfiguration>, Job
{
    /** logger */
    private static Logger logger = Logger.getLogger(ScheduledConsumer.class);

    /** Scheduler */
    private Scheduler scheduler;
    
    /** consumer event factory */
    private EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** consumer event listener */
    private EventListener eventListener;

    /** configuredResourceId */
    private String configuredResourceId;
    
    /** consumer configuration */
    private ScheduledConsumerConfiguration consumerConfiguration;
    
    /** quartz job detail */
    private JobDetail jobDetail;

    /**
     * Constructor
     * @param scheduler
     * @param jobDetail
     * @param flowEventFactory
     */
    public ScheduledConsumer(Scheduler scheduler, EventFactory<FlowEvent<?,?>> flowEventFactory)
    {
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }

        this.flowEventFactory = flowEventFactory;
        if(flowEventFactory == null)
        {
            throw new IllegalArgumentException("flowEventFactory cannot be 'null'");
        }
    }
    
    public void setEventFactory(EventFactory flowEventFactory)
    {
    	this.flowEventFactory = flowEventFactory;
    }
    
    public void setJobDetail(JobDetail jobDetail)
    {
        this.jobDetail = jobDetail;
    }
    
    /**
     * Start the underlying tech
     */
    public void start()
    {
        try
        {
            // create trigger
            // TODO - allow configuration to support multiple triggers
            JobKey jobkey = jobDetail.getKey();

            Trigger trigger = getCronTrigger(jobkey, this.consumerConfiguration.getCronExpression());
            Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
            logger.info("Scheduled consumer for flow [" 
                + jobkey.getName()
                + "] module [" + jobkey.getGroup() 
                + "] starting at [" + scheduledDate + "]");
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop the scheduled job and triggers
     */
    public void stop()
    {
        try
        {
            this.scheduler.deleteJob(this.jobDetail.getKey());
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Is the underlying tech actively running
     * @return isRunning
     */
    public boolean isRunning()
    {
        try
        {
            JobDetail jobDetail = this.scheduler.getJobDetail(this.jobDetail.getKey());
            if(jobDetail == null)
            {
                return false;
            }
            
            return this.scheduler.isStarted();
        }
        catch(SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the consumer event listener
     * @param eventListener
     */
    public void setListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    /**
     * Callback from the scheduler.
     * @param context
     */
    public void execute(JobExecutionContext context)
    {
        JobKey jobkey = context.getJobDetail().getKey();
        String uniqueId = jobkey.getName() + jobkey.getGroup();
        FlowEvent<?,?> flowEvent =this.flowEventFactory.newEvent(uniqueId, context);
        this.eventListener.invoke(flowEvent);
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
    }

    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }
    
    /**
     * Method factory for creating a cron trigger
     * @return jobDetail
     * @throws ParseException 
     */
    protected Trigger getCronTrigger(JobKey jobkey, String cronExpression) throws ParseException
    {
        return newTrigger().withIdentity(jobkey.getName(), jobkey.getGroup()).withSchedule(cronSchedule(cronExpression)).build();
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.component.endpoint.Consumer#getEventFactory()
     */
    public EventFactory getEventFactory()
    {
        return this.flowEventFactory;
    }
    
}

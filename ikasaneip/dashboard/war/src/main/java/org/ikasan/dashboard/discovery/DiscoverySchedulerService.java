package org.ikasan.dashboard.discovery;

import org.apache.log4j.Logger;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.quartz.*;

import java.text.ParseException;
import java.util.*;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Ikasan Development Team on 24/08/2016.
 */
public class DiscoverySchedulerService
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(DiscoverySchedulerService.class);

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    private ScheduledJobFactory scheduledJobFactory;

    private DiscoveryJob discoveryJob;

    private JobDetail jobDetail;

    public DiscoverySchedulerService(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                     DiscoveryJob discoveryJob)
    {
        this.scheduler = scheduler;
        if(this.scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be null!");
        }
        this.scheduledJobFactory = scheduledJobFactory;
        if(this.scheduledJobFactory == null)
        {
            throw new IllegalArgumentException("scheduledJobFactory cannot be null!");
        }
        this.discoveryJob = discoveryJob;
        if(this.discoveryJob == null)
        {
            throw new IllegalArgumentException("discoveryJob cannot be null!");
        }

        this.jobDetail = this.scheduledJobFactory.createJobDetail
                (discoveryJob, DiscoveryJob.class, discoveryJob.getJobName(), "discovery");

    }


    public void removeJob()
    {
        try
        {
            if(this.scheduler.checkExists(this.jobDetail.getKey()));
            {
                this.scheduler.deleteJob(this.jobDetail.getKey());
            }
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addJob(IkasanAuthentication authentication)
    {
        try
        {
            // remove the job so we can reschedule it if the cron expression has changed.
            this.removeJob();

            if(!this.scheduler.checkExists(this.jobDetail.getKey()))
            {
                this.discoveryJob.setAuthentication(authentication);
                JobKey jobkey = jobDetail.getKey();
                Trigger trigger = getTrigger(jobkey);
                Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Scheduling discovery ["
                        + jobkey.getName()
                        + "-" + jobkey.getGroup()
                        + "] starting at [" + scheduledDate + "]");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method factory for creating a cron trigger
     *
     * @return jobDetail
     * @throws ParseException
     */
    protected Trigger getTrigger(JobKey jobkey) throws ParseException
    {
        SimpleTrigger trigger = (SimpleTrigger) newTrigger()
                .withIdentity(jobkey.getName(), jobkey.getGroup())
                .startAt(new Date(System.currentTimeMillis()))
                .forJob(jobkey.getName(), jobkey.getGroup()).build();

        return trigger;
    }

    /**
     * Start the underlying tech
     */
    public void startScheduler()
    {
        try
        {
            this.scheduler.start();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException("Could not start discovery scheduler!");
        }
    }

    public void shutdownScheduler()
    {
        try
        {
            this.scheduler.shutdown();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException("Could not shutdown discovery scheduler!");
        }
    }

    public boolean isRunnung()
    {
        return this.discoveryJob.getRunning();
    }
}

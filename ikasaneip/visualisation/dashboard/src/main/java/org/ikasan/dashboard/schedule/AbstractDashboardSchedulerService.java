package org.ikasan.dashboard.schedule;

import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public abstract class AbstractDashboardSchedulerService {
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger
        (AbstractDashboardSchedulerService.class);

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    protected ScheduledJobFactory scheduledJobFactory;

    protected List<JobDetail> jobDetails;

    protected Map<String, DashboardJob> dashboardJobsMap;

    protected Map<String, JobDetail> dashboardJobDetailsMap;


    public AbstractDashboardSchedulerService(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory)
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

        this.dashboardJobsMap = new HashMap<>();
        this.dashboardJobDetailsMap = new HashMap<>();
        this.jobDetails = new ArrayList<>();
    }

    @PostConstruct
    public abstract void registerJobs();

    protected void addJob(String jobName)
    {
        try
        {
            // remove the job so we can reschedule it if the cron expression has changed.
            this.removeJob(jobName);

            if(!this.scheduler.checkExists(this.dashboardJobDetailsMap.get(jobName).getKey()))
            {
                JobDetail jobDetail = this.dashboardJobDetailsMap.get(jobName);
                JobKey jobkey = jobDetail.getKey();
                Trigger trigger = getCronTrigger(jobkey, this.dashboardJobsMap.get(jobkey.toString()).getCronExpression());
                Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Scheduled   job ["
                    + jobkey.toString()
                    + "] starting at [" + scheduledDate + "] using cron expression ["
                    + this.dashboardJobsMap.get(jobkey.toString()).getCronExpression() + "]");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    protected void removeJob(String jobName)
    {
        try
        {
            if(this.scheduler.checkExists(this.dashboardJobDetailsMap.get(jobName).getKey()))
            {
                this.scheduler.deleteJob(this.dashboardJobDetailsMap.get(jobName).getKey());
            }
        }
        catch (SchedulerException e)
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
    protected Trigger getCronTrigger(JobKey jobkey, String cronExpression)
    {
        TriggerBuilder triggerBuilder = newTrigger().withIdentity(jobkey.getName(), jobkey.getGroup());

        CronScheduleBuilder cronScheduleBuilder = cronSchedule(cronExpression);

        triggerBuilder.withSchedule(cronScheduleBuilder);
        return triggerBuilder.build();
    }

}

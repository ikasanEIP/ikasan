package org.ikasan.dashboard.notification.service;

import org.ikasan.dashboard.notification.BusinessStreamNotificationJob;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class BusinessStreamNotificationSchedulerService  {
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger
        (BusinessStreamNotificationSchedulerService.class);

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    private ScheduledJobFactory scheduledJobFactory;

    private List<JobDetail> businessStreamJobDetails;

    private Map<String, BusinessStreamNotificationJob> businessStreamJobs;

    private Map<String, JobDetail> businessStreamJobDetailsMap;


    public BusinessStreamNotificationSchedulerService(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                                      List<BusinessStreamNotificationJob> businessStreamNotificationJobs)
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

        this.businessStreamJobs = new HashMap<>();
        this.businessStreamJobDetailsMap = new HashMap<>();
        this.businessStreamJobDetails = new ArrayList<>();

        for(BusinessStreamNotificationJob job: businessStreamNotificationJobs)
        {
            JobDetail jobDetail = this.scheduledJobFactory.createJobDetail
                (job, BusinessStreamNotificationJob.class, job.getJobName(), "notify");

            businessStreamJobDetails.add(jobDetail);
            businessStreamJobDetailsMap.put(job.getJobName(), jobDetail);
            this.businessStreamJobs.put(jobDetail.getKey().toString(), job);
        }
    }

    @PostConstruct
    public void registerJobs()
    {
        for(JobDetail jobDetail: this.businessStreamJobDetails)
        {
            this.addJob(jobDetail.getKey().getName());
        }
    }

    public void removeJob(String jobName)
    {
        try
        {
            if(this.scheduler.checkExists(this.businessStreamJobDetailsMap.get(jobName).getKey()))
            {
                this.scheduler.deleteJob(this.businessStreamJobDetailsMap.get(jobName).getKey());
            }
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addJob(String jobName)
    {
        try
        {
            // remove the job so we can reschedule it if the cron expression has changed.
            this.removeJob(jobName);

            if(!this.scheduler.checkExists(this.businessStreamJobDetailsMap.get(jobName).getKey()))
            {
                JobDetail jobDetail = this.businessStreamJobDetailsMap.get(jobName);
                JobKey jobkey = jobDetail.getKey();
                Trigger trigger = getCronTrigger(jobkey, this.businessStreamJobs.get(jobkey.toString()).getCronExpression());
                Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Scheduled notification job ["
                    + jobkey.toString()
                    + "] starting at [" + scheduledDate + "] using cron expression ["
                    + this.businessStreamJobs.get(jobkey.toString()).getCronExpression() + "]");
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
    protected Trigger getCronTrigger(JobKey jobkey, String cronExpression)
    {
        TriggerBuilder triggerBuilder = newTrigger().withIdentity(jobkey.getName(), jobkey.getGroup());

        CronScheduleBuilder cronScheduleBuilder = cronSchedule(cronExpression);

        triggerBuilder.withSchedule(cronScheduleBuilder);
        return triggerBuilder.build();
    }

}

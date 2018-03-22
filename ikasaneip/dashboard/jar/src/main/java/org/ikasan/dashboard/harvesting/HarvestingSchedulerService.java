package org.ikasan.dashboard.harvesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.*;

import java.text.ParseException;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Ikasan Development Team on 24/08/2016.
 */
public class HarvestingSchedulerService
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HarvestingSchedulerService.class);

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    private ScheduledJobFactory scheduledJobFactory;

    private List<JobDetail> houseKeepingJobDetails;

    private Map<String, SolrHarvestingJob> houseKeepingJobs;

    private Map<String, JobDetail> houseKeepingJobDetailsMap;

    public HarvestingSchedulerService(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                      List<SolrHarvestingJob> houseKeepingJobs)
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

        this.houseKeepingJobs = new HashMap<String, SolrHarvestingJob>();
        this.houseKeepingJobDetailsMap = new HashMap<String, JobDetail>();
        this.houseKeepingJobDetails = new ArrayList<JobDetail>();

        for(SolrHarvestingJob job: houseKeepingJobs)
        {
            JobDetail jobDetail = this.scheduledJobFactory.createJobDetail
                    (job, SolrHarvestingJob.class, job.getJobName(), "harvest");

            houseKeepingJobDetails.add(jobDetail);
            houseKeepingJobDetailsMap.put(job.getJobName(), jobDetail);
            this.houseKeepingJobs.put(jobDetail.getKey().toString(), job);
        }
    }

    public void registerJobs()
    {
        for(JobDetail jobDetail: this.houseKeepingJobDetails)
        {
            try
            {
                // create trigger
                JobKey jobkey = jobDetail.getKey();

                SolrHarvestingJob harvestingJob = this.houseKeepingJobs.get(jobkey.toString());
                harvestingJob.init();

                if(harvestingJob.isInitialised() && harvestingJob.isEnabled()
                        && !this.scheduler.checkExists(jobkey))
                {
                    Trigger trigger = getCronTrigger(jobkey, this.houseKeepingJobs.get(jobkey.toString()).getCronExpression());
                    Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                    logger.info("Scheduled harvesting job ["
                            + jobkey.toString()
                            + "] starting at [" + scheduledDate + "] using cron expression ["
                            + this.houseKeepingJobs.get(jobkey.toString()).getCronExpression() + "]");
                }

            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeJob(String jobName)
    {
        try
        {
            if(this.scheduler.checkExists(this.houseKeepingJobDetailsMap.get(jobName).getKey()))
            {
                this.scheduler.deleteJob(this.houseKeepingJobDetailsMap.get(jobName).getKey());
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

            if(!this.scheduler.checkExists(this.houseKeepingJobDetailsMap.get(jobName).getKey()))
            {
                JobDetail jobDetail = this.houseKeepingJobDetailsMap.get(jobName);
                JobKey jobkey = jobDetail.getKey();
                Trigger trigger = getCronTrigger(jobkey, this.houseKeepingJobs.get(jobkey.toString()).getCronExpression());
                Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Scheduled harvesting job ["
                        + jobkey.toString()
                        + "] starting at [" + scheduledDate + "] using cron expression ["
                        + this.houseKeepingJobs.get(jobkey.toString()).getCronExpression() + "]");
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
    protected Trigger getCronTrigger(JobKey jobkey, String cronExpression) throws ParseException
    {
        TriggerBuilder triggerBuilder = newTrigger().withIdentity(jobkey.getName(), jobkey.getGroup());

        CronScheduleBuilder cronScheduleBuilder = cronSchedule(cronExpression);

        triggerBuilder.withSchedule(cronScheduleBuilder);
        return triggerBuilder.build();
    }

    /**
     * Start the underlying tech
     */
    public void startScheduler()
    {
        try
        {
            this.registerJobs();
            this.scheduler.start();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException("Could not start house keeping scheduler!");
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
            throw new RuntimeException("Could not shutdown house keeping scheduler!");
        }
    }
}

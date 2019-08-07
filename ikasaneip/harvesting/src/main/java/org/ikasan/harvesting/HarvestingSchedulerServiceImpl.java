package org.ikasan.harvesting;

import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
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
public class HarvestingSchedulerServiceImpl implements HarvestingSchedulerService
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HarvestingSchedulerServiceImpl.class);

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    private ScheduledJobFactory scheduledJobFactory;

    private List<JobDetail> harvestingJobDetails;

    private Map<String, HarvestingJob> harvestingJobs;

    private Map<String, JobDetail> harvestingJobDetailsMap;

    public HarvestingSchedulerServiceImpl(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                      List<HarvestingJob> harvestingJobs)
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

        this.harvestingJobs = new HashMap<String, HarvestingJob>();
        this.harvestingJobDetailsMap = new HashMap<String, JobDetail>();
        this.harvestingJobDetails = new ArrayList<JobDetail>();

        for(HarvestingJob job: harvestingJobs)
        {
            JobDetail jobDetail = this.scheduledJobFactory.createJobDetail
                    (job, HarvestingJob.class, job.getJobName(), "harvest");

            harvestingJobDetails.add(jobDetail);
            harvestingJobDetailsMap.put(job.getJobName(), jobDetail);
            this.harvestingJobs.put(jobDetail.getKey().toString(), job);
        }
    }

    public void registerJobs()
    {
        for(JobDetail jobDetail: this.harvestingJobDetails)
        {
            try
            {
                // create trigger
                JobKey jobkey = jobDetail.getKey();

                HarvestingJob harvestingJob = this.harvestingJobs.get(jobkey.toString());
                harvestingJob.init();

                if(harvestingJob.isInitialised() && harvestingJob.isEnabled()
                        && !this.scheduler.checkExists(jobkey))
                {
                    Trigger trigger = getCronTrigger(jobkey, this.harvestingJobs.get(jobkey.toString()).getCronExpression());
                    Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                    logger.info("Scheduled harvesting job ["
                            + jobkey.toString()
                            + "] starting at [" + scheduledDate + "] using cron expression ["
                            + this.harvestingJobs.get(jobkey.toString()).getCronExpression() + "]");
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
            if(this.scheduler.checkExists(this.harvestingJobDetailsMap.get(jobName).getKey()))
            {
                this.scheduler.deleteJob(this.harvestingJobDetailsMap.get(jobName).getKey());
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

            if(!this.scheduler.checkExists(this.harvestingJobDetailsMap.get(jobName).getKey()))
            {
                JobDetail jobDetail = this.harvestingJobDetailsMap.get(jobName);
                JobKey jobkey = jobDetail.getKey();
                Trigger trigger = getCronTrigger(jobkey, this.harvestingJobs.get(jobkey.toString()).getCronExpression());
                Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Scheduled harvesting job ["
                        + jobkey.toString()
                        + "] starting at [" + scheduledDate + "] using cron expression ["
                        + this.harvestingJobs.get(jobkey.toString()).getCronExpression() + "]");
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

package org.ikasan.dashboard.notification.service;

import org.ikasan.dashboard.notification.BusinessStreamNotificationJob;
import org.ikasan.dashboard.schedule.AbstractDashboardSchedulerService;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

public class BusinessStreamNotificationSchedulerService extends AbstractDashboardSchedulerService {
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(BusinessStreamNotificationSchedulerService.class);

    /**
     * Scheduler
     */
    private List<BusinessStreamNotificationJob> businessStreamNotificationJobs;


    public BusinessStreamNotificationSchedulerService(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                                      List<BusinessStreamNotificationJob> businessStreamNotificationJobs)
    {
        super(scheduler, scheduledJobFactory);

        this.businessStreamNotificationJobs = businessStreamNotificationJobs;
        if(this.businessStreamNotificationJobs == null)
        {
            throw new IllegalArgumentException("businessStreamNotificationJobs cannot be null!");
        }
    }

    @PostConstruct
    public void registerJobs()
    {
        for(BusinessStreamNotificationJob job: businessStreamNotificationJobs)
        {
            JobDetail jobDetail = this.scheduledJobFactory.createJobDetail
                (job, BusinessStreamNotificationJob.class, job.getJobName(), "notify");

            super.jobDetails.add(jobDetail);
            super.dashboardJobDetailsMap.put(job.getJobName(), jobDetail);
            super.dashboardJobsMap.put(jobDetail.getKey().toString(), job);
        }

        for(JobDetail jobDetail: super.jobDetails)
        {
            logger.info(String.format("Registering business stream notification job[%s]", jobDetail.getKey().getName()));
            this.addJob(jobDetail.getKey().getName());
        }
    }

}

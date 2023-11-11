package org.ikasan.housekeeping;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.housekeeping.HousekeepingSchedulerService;
import org.ikasan.spec.monitor.JobMonitor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * House keeping related configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
@AutoConfiguration
public class ModuleHousekeepingAutoConfiguration
{

    @Bean
    public HousekeepingSchedulerService housekeepingSchedulerService(List<HousekeepingJob> housekeepingJobs)
    {
        return new HousekeepingSchedulerServiceImpl(SchedulerFactory.getInstance().getScheduler(),
            CachingScheduledJobFactory.getInstance(), housekeepingJobs);
    }

    @Bean
    public HousekeepingJob replyHousekeepingJob(HousekeepService replayManagementService, Environment environment,
                                                JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("replayHousekeepingJob", replayManagementService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    public HousekeepingJob wiretapHousekeepingJob(HousekeepService wiretapService, Environment environment,
                                                  JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("wiretapHousekeepingJob", wiretapService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    public HousekeepingJob errorReportingHousekeepingJob(HousekeepService errorReportingManagementService, Environment environment,
                                                         JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("errorReportingHousekeepingJob", errorReportingManagementService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    public HousekeepingJob systemEventServiceHousekeepingJob(HousekeepService systemEventService, Environment environment,
                                                             JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("systemEventServiceHousekeepingJob", systemEventService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    public HousekeepingJob duplicateFilterHousekeepingJob(HousekeepService managementFilterService, Environment environment,
                                                          JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("duplicateFilterHousekeepingJob", managementFilterService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    public HousekeepingJob messageHistoryHousekeepingJob(HousekeepService messageHistoryService, Environment environment,
                                                         JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("messageHistoryHousekeepingJob", messageHistoryService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }
}
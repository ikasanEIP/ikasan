package org.ikasan.housekeeping;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.housekeeping.HousekeepingSchedulerService;
import org.ikasan.spec.monitor.JobMonitor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * House keeping related configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
@Configuration
@DependsOn({"errorReportingManagementService", "replayManagementService", "wiretapService", "systemEventService", "managementFilterService", "messageHistoryService"})
public class ModuleHousekeepingAutoConfiguration
{

    @Bean(name = "housekeepingSchedulerService")
    public HousekeepingSchedulerService housekeepingSchedulerService(List<HousekeepingJob> housekeepingJobs)
    {
        return new HousekeepingSchedulerServiceImpl(SchedulerFactory.getInstance().getScheduler(),
            CachingScheduledJobFactory.getInstance(), housekeepingJobs);
    }

    @Bean
    @DependsOn("replayManagementService")
    public HousekeepingJob replyHousekeepingJob(@Qualifier("replayManagementService")HousekeepService replayManagementService, Environment environment,
                                                JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("replayHousekeepingJob", replayManagementService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    @DependsOn("wiretapService")
    public HousekeepingJob wiretapHousekeepingJob(@Qualifier("wiretapService")HousekeepService wiretapService, Environment environment,
                                                  JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("wiretapHousekeepingJob", wiretapService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    @DependsOn("errorReportingManagementService")
    public HousekeepingJob errorReportingHousekeepingJob(@Qualifier("errorReportingManagementService")HousekeepService errorReportingManagementService, Environment environment,
                                                         JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("errorReportingHousekeepingJob", errorReportingManagementService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    @DependsOn("systemEventService")
    public HousekeepingJob systemEventServiceHousekeepingJob(@Qualifier("systemEventService")HousekeepService systemEventService, Environment environment,
                                                             JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("systemEventServiceHousekeepingJob", systemEventService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    @DependsOn("managementFilterService")
    public HousekeepingJob duplicateFilterHousekeepingJob(@Qualifier("managementFilterService")HousekeepService managementFilterService, Environment environment,
                                                          JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("duplicateFilterHousekeepingJob", managementFilterService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }

    @Bean
    @DependsOn("messageHistoryService")
    public HousekeepingJob messageHistoryHousekeepingJob(@Qualifier("messageHistoryService")HousekeepService messageHistoryService, Environment environment,
                                                         JobMonitor jobMonitor)
    {
        HousekeepingJobImpl housekeepingJob = new HousekeepingJobImpl("messageHistoryHousekeepingJob", messageHistoryService, environment);
        jobMonitor.setJobName(housekeepingJob.getJobName());

        housekeepingJob.setMonitor(jobMonitor);
        return housekeepingJob;
    }
}
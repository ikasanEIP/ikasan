package org.ikasan.housekeeping;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.housekeeping.HousekeepingSchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * House keeping related configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
@Configuration
public class ModuleHousekeepingAutoConfiguration
{

    @Bean
    public HousekeepingSchedulerService housekeepingSchedulerService(List<HousekeepingJob> housekeepingJobs)
    {
        return new HousekeepingSchedulerServiceImpl(SchedulerFactory.getInstance().getScheduler(),
            CachingScheduledJobFactory.getInstance(), housekeepingJobs);
    }

    @Bean
    public HousekeepingJob replyHousekeepingJob(HousekeepService replayManagementService, Environment environment)
    {
        return new HousekeepingJobImpl("replayHousekeepingJob", replayManagementService, environment);
    }

    @Bean
    public HousekeepingJob wiretapHousekeepingJob(HousekeepService wiretapService, Environment environment)
    {
        return new HousekeepingJobImpl("wiretapHousekeepingJob", wiretapService, environment);
    }

    @Bean
    public HousekeepingJob errorReportingHousekeepingJob(HousekeepService errorReportingManagementService, Environment environment)
    {
        return new HousekeepingJobImpl("errorReportingHousekeepingJob", errorReportingManagementService, environment);
    }

    @Bean
    public HousekeepingJob systemEventServiceHousekeepingJob(HousekeepService systemEventService, Environment environment)
    {
        return new HousekeepingJobImpl("systemEventServiceHousekeepingJob", systemEventService, environment);
    }

    @Bean
    public HousekeepingJob duplicateFilterHousekeepingJob(HousekeepService managementFilterService, Environment environment)
    {
        return new HousekeepingJobImpl("duplicateFilterHousekeepingJob", managementFilterService, environment);
    }

    @Bean
    public HousekeepingJob messageHistoryHousekeepingJob(HousekeepService messageHistoryService, Environment environment)
    {
        return new HousekeepingJobImpl("messageHistoryHousekeepingJob", messageHistoryService, environment);
    }
}
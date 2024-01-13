package org.ikasan.harvesting;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.ikasan.spec.monitor.JobMonitor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Harvesting related configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
@DependsOn({"replayManagementService", "wiretapService", "errorReportingManagementService", "exclusionManagementService", "messageHistoryService", "systemEventService"})
public class HarvestingAutoConfiguration
{
    @Bean(name = "harvestingSchedulerService")
    public HarvestingSchedulerService harvestingSchedulerService(List<HarvestingJob> harvestingJobs)
    {
        return new HarvestingSchedulerServiceImpl(SchedulerFactory.getInstance().getScheduler(),
                CachingScheduledJobFactory.getInstance(), harvestingJobs);
    }

    @Bean
    @DependsOn("replayManagementService")
    public HarvestingJob replyHarvestingJob(@Qualifier("replayManagementService")HarvestService replayManagementService
        , Environment environment, @Qualifier("replyDashboardRestService") DashboardRestService replyDashboardRestService
        , JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("replayHarvestingJob", replayManagementService, environment, replyDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    @DependsOn("wiretapService")
    public HarvestingJob wiretapHarvestingJob(@Qualifier("wiretapService")HarvestService wiretapService, Environment environment,
        @Qualifier("wiretapDashboardRestService") DashboardRestService wiretapDashboardRestService, JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("wiretapHarvestingJob", wiretapService, environment, wiretapDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    @DependsOn("errorReportingManagementService")
    public HarvestingJob errorReportingHarvestingJob(@Qualifier("errorReportingManagementService")HarvestService errorReportingManagementService,
        Environment environment, @Qualifier("errorReportingDashboardRestService") DashboardRestService errorReportingDashboardRestService, JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("errorReportingHarvestingJob", errorReportingManagementService, environment,
            errorReportingDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    @DependsOn("exclusionManagementService")
    public HarvestingJob exclusionHarvestingJob(@Qualifier("exclusionManagementService")HarvestService exclusionManagementService, Environment environment
        , @Qualifier("exclusionDashboardRestService") DashboardRestService exclusionDashboardRestService
        , JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("exclusionHarvestingJob", exclusionManagementService, environment, exclusionDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }


    @Bean
    @DependsOn("messageHistoryService")
    public HarvestingJob messageHistoryJob(@Qualifier("messageHistoryService")HarvestService messageHistoryService, Environment environment
        , @Qualifier("metricsDashboardRestService") DashboardRestService metricsDashboardRestService, JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("messageHistoryHarvestingJob", messageHistoryService, environment,metricsDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    @DependsOn("systemEventService")
    public HarvestingJob systemEventJob(@Qualifier("systemEventService")HarvestService systemEventService, Environment environment
        , @Qualifier("systemEventsDashboardRestService") DashboardRestService systemEventsDashboardRestService, JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("systemEventHarvestingJob", systemEventService, environment, systemEventsDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }
}
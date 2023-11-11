package org.ikasan.harvesting;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.ikasan.spec.monitor.JobMonitor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Harvesting related configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
@AutoConfiguration
public class HarvestingAutoConfiguration
{
    @Bean
    public HarvestingSchedulerService harvestingSchedulerService(List<HarvestingJob> harvestingJobs)
    {
        return new HarvestingSchedulerServiceImpl(SchedulerFactory.getInstance().getScheduler(),
                CachingScheduledJobFactory.getInstance(), harvestingJobs);
    }

    @Bean
    public HarvestingJob replyHarvestingJob(HarvestService replayManagementService, Environment environment, DashboardRestService replyDashboardRestService,
                                            JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("replayHarvestingJob", replayManagementService, environment, replyDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    public HarvestingJob wiretapHarvestingJob(HarvestService wiretapService, Environment environment,
        DashboardRestService wiretapDashboardRestService, JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("wiretapHarvestingJob", wiretapService, environment, wiretapDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    public HarvestingJob errorReportingHarvestingJob(HarvestService errorReportingManagementService,
        Environment environment, DashboardRestService errorReportingDashboardRestService, JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("errorReportingHarvestingJob", errorReportingManagementService, environment,
            errorReportingDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    public HarvestingJob exclusionHarvestingJob(HarvestService exclusionManagementService, Environment environment,DashboardRestService exclusionDashboardRestService,
                                                JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("exclusionHarvestingJob", exclusionManagementService, environment, exclusionDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }


    @Bean
    public HarvestingJob messageHistoryJob(HarvestService messageHistoryService, Environment environment, DashboardRestService metricsDashboardRestService,
                                           JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("messageHistoryHarvestingJob", messageHistoryService, environment,metricsDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }

    @Bean
    public HarvestingJob systemEventJob(HarvestService systemEventService, Environment environment, DashboardRestService systemEventsDashboardRestService,
                                        JobMonitor jobMonitor)
    {
        HarvestingJobImpl harvestingJob = new HarvestingJobImpl("systemEventHarvestingJob", systemEventService, environment, systemEventsDashboardRestService);
        jobMonitor.setJobName(harvestingJob.getJobName());

        harvestingJob.setMonitor(jobMonitor);
        return harvestingJob;
    }
}
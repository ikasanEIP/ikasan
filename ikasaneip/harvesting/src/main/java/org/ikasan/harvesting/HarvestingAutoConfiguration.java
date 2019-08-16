package org.ikasan.harvesting;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Harvesting  related configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
public class HarvestingAutoConfiguration
{
    @Bean
    public HarvestingSchedulerService harvestingSchedulerService(List<HarvestingJob> harvestingJobs)
    {
        return
            new HarvestingSchedulerServiceImpl(SchedulerFactory.getInstance().getScheduler(),
                CachingScheduledJobFactory.getInstance(), harvestingJobs);
    }

    @Bean
    public HarvestingJob replyHarvestingJob(HarvestService replayManagementService, Environment environment,DashboardRestService replyDashboardRestService)
    {

        return new HarvestingJobImpl("replayHarvestingJob", replayManagementService, environment, replyDashboardRestService);
    }

    @Bean
    public HarvestingJob wiretapHarvestingJob(HarvestService wiretapService, Environment environment,
        DashboardRestService wiretapDashboardRestService )
    {

        return new HarvestingJobImpl("wiretapHarvestingJob", wiretapService, environment, wiretapDashboardRestService);
    }

    @Bean
    public HarvestingJob errorReportingHarvestingJob(HarvestService errorReportingManagementService,
        Environment environment, DashboardRestService errorReportingDashboardRestService)
    {

        return new HarvestingJobImpl("errorReportingHarvestingJob", errorReportingManagementService, environment,
            errorReportingDashboardRestService);
    }

    @Bean
    public HarvestingJob exclusionHarvestingJob(HarvestService exclusionManagementService, Environment environment,DashboardRestService exclusionDashboardRestService)
    {
        return new HarvestingJobImpl("exclusionHarvestingJob", exclusionManagementService, environment, exclusionDashboardRestService);
    }


    @Bean
    public HarvestingJob messageHistoryJob(HarvestService messageHistoryService, Environment environment, DashboardRestService metricsDashboardRestService)
    {

        return new HarvestingJobImpl("messageHistoryHarvestingJob", messageHistoryService, environment,metricsDashboardRestService);
    }


}
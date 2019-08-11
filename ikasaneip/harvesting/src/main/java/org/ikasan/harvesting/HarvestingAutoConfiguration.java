package org.ikasan.harvesting;

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.ikasan.topology.service.DashboardRestService;
import org.ikasan.topology.service.DashboardRestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Harvesting  related configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
public class HarvestingAutoConfiguration
{
    private String ERROR_PATH = "/rest/harvest/errors";

    private String EXCLUSION_PATH = "/rest/harvest/exclusions";

    private String METRICS_PATH = "/rest/harvest/metrics";

    private String REPLAY_PATH = "/rest/harvest/replay";

    private String WIRETAP_PATH = "/rest/harvest/wiretaps";

    @Bean
    public HarvestingSchedulerService harvestingSchedulerService(List<HarvestingJob> harvestingJobs)
    {
        return
            new HarvestingSchedulerServiceImpl(SchedulerFactory.getInstance().getScheduler(),
                CachingScheduledJobFactory.getInstance(), harvestingJobs);
    }

    @Bean
    public HarvestingJob replyHarvestingJob(HarvestService replayManagementService, Environment environment)
    {
        DashboardRestService dashboardRestService = new DashboardRestServiceImpl(environment, REPLAY_PATH);
        return new HarvestingJobImpl("replayHarvestingJob", replayManagementService, environment,dashboardRestService);
    }

    @Bean
    public HarvestingJob wiretapHarvestingJob(HarvestService wiretapService, Environment environment)
    {
        DashboardRestService dashboardRestService = new DashboardRestServiceImpl(environment, WIRETAP_PATH);

        return new HarvestingJobImpl("wiretapHarvestingJob", wiretapService, environment,dashboardRestService);
    }

    @Bean
    public HarvestingJob errorReportingHarvestingJob(HarvestService errorReportingManagementService, Environment environment)
    {
        DashboardRestService dashboardRestService = new DashboardRestServiceImpl(environment, ERROR_PATH);

        return new HarvestingJobImpl("errorReportingHarvestingJob", errorReportingManagementService, environment,dashboardRestService);
    }

    @Bean
    public HarvestingJob exclusionHarvestingJob(HarvestService exclusionManagementService, Environment environment)
    {
        DashboardRestService dashboardRestService = new DashboardRestServiceImpl(environment, EXCLUSION_PATH);

        return new HarvestingJobImpl("exclusionHarvestingJob", exclusionManagementService, environment,dashboardRestService);
    }


    @Bean
    public HarvestingJob messageHistorJob(HarvestService messageHistoryService, Environment environment)
    {
        DashboardRestService dashboardRestService = new DashboardRestServiceImpl(environment, METRICS_PATH);

        return new HarvestingJobImpl("messageHistoryHarvestingJob", messageHistoryService, environment,dashboardRestService);
    }


}
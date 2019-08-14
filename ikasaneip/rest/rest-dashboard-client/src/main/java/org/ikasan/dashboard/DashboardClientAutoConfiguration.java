package org.ikasan.dashboard;

import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 *  Dashboard Client configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
public class DashboardClientAutoConfiguration
{
    private String ERROR_PATH = "/rest/harvest/errors";

    private String EXCLUSION_PATH = "/rest/harvest/exclusions";

    private String METRICS_PATH = "/rest/harvest/metrics";

    private String REPLAY_PATH = "/rest/harvest/replay";

    private String WIRETAP_PATH = "/rest/harvest/wiretaps";

    private String METADATA_PATH = "/rest/module/metadata";

    @Bean
    public DashboardRestService replyDashboardRestService(Environment environment)
    {
        return new DashboardRestServiceImpl(environment, REPLAY_PATH);
    }

    @Bean
    public DashboardRestService wiretapDashboardRestService(Environment environment)
    {
        return new DashboardRestServiceImpl(environment, WIRETAP_PATH);

    }

    @Bean
    public DashboardRestService errorReportingDashboardRestService(Environment environment)
    {
        return new DashboardRestServiceImpl(environment, ERROR_PATH);
    }

    @Bean
    public DashboardRestService exclusionDashboardRestService(Environment environment)
    {
        return new DashboardRestServiceImpl(environment, EXCLUSION_PATH);
    }


    @Bean
    public DashboardRestService metricsDashboardRestService(Environment environment)
    {
        return new DashboardRestServiceImpl(environment, METRICS_PATH);
    }

    @Bean
    public DashboardRestService moduleMetadataDashboardRestService(Environment environment,
        ModuleMetaDataProvider jsonModuleMetaDataProvider)
    {
        return new DashboardRestServiceImpl(environment, METADATA_PATH, jsonModuleMetaDataProvider);

    }



}
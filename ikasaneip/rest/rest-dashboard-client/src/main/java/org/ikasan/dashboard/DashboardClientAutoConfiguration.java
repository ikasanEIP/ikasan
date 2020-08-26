package org.ikasan.dashboard;

import org.ikasan.dashboard.component.ModuleMetadataConverter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

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

    private String SYSTEM_EVENTS_PATH = "/rest/harvest/systemevents";

    private String METADATA_PATH = "/rest/module/metadata";

    private String CONFIGURATION_METADATA_PATH = "/rest/configuration/metadata";

    private String FLOW_STATES_CACHE_PATH = "/rest/flowStates/cache";

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
    public DashboardRestService systemEventsDashboardRestService(Environment environment)
    {
        return new DashboardRestServiceImpl(environment, SYSTEM_EVENTS_PATH);
    }

    @Bean
    public DashboardRestService moduleMetadataDashboardRestService(Environment environment,
        ModuleMetadataConverter moduleMetadataConverter)
    {
        return new DashboardRestServiceImpl(environment, METADATA_PATH, moduleMetadataConverter);

    }

    @Bean
    public ModuleMetadataConverter moduleMetadataConverter(ModuleMetaDataProvider<String> jsonModuleMetaDataProvider
        , ModuleService moduleService) {
        return new ModuleMetadataConverter(jsonModuleMetaDataProvider,moduleService);
    }

    @Bean
    public DashboardRestService configurationMetadataDashboardRestService(Environment environment,
        ConfigurationMetaDataExtractor<ConfigurationMetaData> configurationMetaDataProvider)
    {

        return new DashboardRestServiceImpl(environment, CONFIGURATION_METADATA_PATH,
            (Converter<Module, List>) module -> configurationMetaDataProvider.getComponentsConfiguration(module));

    }

    @Bean
    public DashboardRestService flowCacheStateRestService(Environment environment)
    {
        return new DashboardRestServiceImpl(environment, FLOW_STATES_CACHE_PATH);

    }
}
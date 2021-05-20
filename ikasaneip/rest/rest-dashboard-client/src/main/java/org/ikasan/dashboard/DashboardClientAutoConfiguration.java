package org.ikasan.dashboard;

import org.ikasan.dashboard.component.ModuleMetadataConverter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

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
    @ConfigurationProperties(prefix = "module.rest.connection")
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory()
    {
        return new HttpComponentsClientHttpRequestFactory();
    }

    @Bean
    public DashboardRestService replyDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, REPLAY_PATH);
    }

    @Bean
    public DashboardRestService wiretapDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, WIRETAP_PATH);

    }

    @Bean
    public DashboardRestService errorReportingDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, ERROR_PATH);
    }

    @Bean
    public DashboardRestService exclusionDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, EXCLUSION_PATH);
    }


    @Bean
    public DashboardRestService metricsDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, METRICS_PATH);
    }

    @Bean
    public DashboardRestService systemEventsDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, SYSTEM_EVENTS_PATH);
    }

    @Bean
    public DashboardRestService moduleMetadataDashboardRestService(Environment environment, HttpComponentsClientHttpRequestFactory customHttpRequestFactory,
        ModuleMetadataConverter moduleMetadataConverter)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, METADATA_PATH, moduleMetadataConverter);

    }

    @Bean
    public ModuleMetadataConverter moduleMetadataConverter(ModuleMetaDataProvider<String> jsonModuleMetaDataProvider
        , ModuleService moduleService) {
        return new ModuleMetadataConverter(jsonModuleMetaDataProvider,moduleService);
    }

    @Bean
    public DashboardRestService configurationMetadataDashboardRestService(Environment environment, HttpComponentsClientHttpRequestFactory customHttpRequestFactory,
        ConfigurationMetaDataExtractor<ConfigurationMetaData> configurationMetaDataProvider)
    {

        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, CONFIGURATION_METADATA_PATH,
            (Converter<Module, List>) module -> configurationMetaDataProvider.getComponentsConfiguration(module));

    }

    @Bean
    public DashboardRestService flowCacheStateRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, FLOW_STATES_CACHE_PATH);

    }
}
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
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 *  Dashboard Client configuration required by every module.
 * This autoconfig should be excluded from dashboard.
 */
public class DashboardClientAutoConfiguration
{
    public static final String ERROR_PATH = "/rest/harvest/errors";

    public static final String EXCLUSION_PATH = "/rest/harvest/exclusions";

    public static final String METRICS_PATH = "/rest/harvest/metrics";

    public static final String REPLAY_PATH = "/rest/harvest/replay";

    public static final String WIRETAP_PATH = "/rest/harvest/wiretaps";

    public static final String SYSTEM_EVENTS_PATH = "/rest/harvest/systemevents";

    public static final String METADATA_PATH = "/rest/module/metadata";

    public static final String CONFIGURATION_METADATA_PATH = "/rest/configuration/metadata";

    public static final String FLOW_STATES_CACHE_PATH = "/rest/flowStates/cache";

    @Bean
    @ConfigurationProperties(prefix = "module.rest.connection")
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory()
    {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory
            = new HttpComponentsClientHttpRequestFactory();

        // all of the properties can be overwritten using spring properties.
        httpComponentsClientHttpRequestFactory.setReadTimeout(5000);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(5000);
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(5000);

        return httpComponentsClientHttpRequestFactory;
    }

    @LoadBalanced
    @Bean
    public RestTemplate replyDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService replyDashboardRestService(RestTemplate replyDashboardRestServiceRestTemplate, Environment environment)
    {
        return new LoadBalancedDashboardRestServiceImpl(replyDashboardRestServiceRestTemplate, environment, REPLAY_PATH);
    }

    @LoadBalanced
    @Bean
    public RestTemplate wiretapDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService wiretapDashboardRestService(RestTemplate wiretapDashboardRestServiceRestTemplate, Environment environment)
    {
        return new LoadBalancedDashboardRestServiceImpl(wiretapDashboardRestServiceRestTemplate, environment
            , WIRETAP_PATH);

    }

    @LoadBalanced
    @Bean
    public RestTemplate errorReportingDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService errorReportingDashboardRestService(RestTemplate errorReportingDashboardRestServiceRestTemplate, Environment environment)
    {
        return new LoadBalancedDashboardRestServiceImpl(errorReportingDashboardRestServiceRestTemplate,environment
            , ERROR_PATH);
    }

    @LoadBalanced
    @Bean
    public RestTemplate exclusionDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService exclusionDashboardRestService(RestTemplate exclusionDashboardRestServiceRestTemplate, Environment environment)
    {
        return new LoadBalancedDashboardRestServiceImpl(exclusionDashboardRestServiceRestTemplate, environment, EXCLUSION_PATH);
    }

    @LoadBalanced
    @Bean
    public RestTemplate metricsDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService metricsDashboardRestService(RestTemplate metricsDashboardRestServiceRestTemplate, Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory)
    {
        return new LoadBalancedDashboardRestServiceImpl(metricsDashboardRestServiceRestTemplate, environment
            , METRICS_PATH);
    }

    @LoadBalanced
    @Bean
    public RestTemplate systemEventsDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService systemEventsDashboardRestService(RestTemplate systemEventsDashboardRestServiceRestTemplate, Environment environment)
    {
        return new LoadBalancedDashboardRestServiceImpl(systemEventsDashboardRestServiceRestTemplate, environment
            , SYSTEM_EVENTS_PATH);
    }

    @LoadBalanced
    @Bean
    public RestTemplate moduleMetadataDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService moduleMetadataDashboardRestService(RestTemplate moduleMetadataDashboardRestServiceRestTemplate
        , Environment environment, ModuleMetadataConverter moduleMetadataConverter)
    {
        return new LoadBalancedDashboardRestServiceImpl(moduleMetadataDashboardRestServiceRestTemplate, environment
            , METADATA_PATH, moduleMetadataConverter);

    }

    @Bean
    public ModuleMetadataConverter moduleMetadataConverter(ModuleMetaDataProvider<String> jsonModuleMetaDataProvider
        , ModuleService moduleService) {
        return new ModuleMetadataConverter(jsonModuleMetaDataProvider,moduleService);
    }

    @LoadBalanced
    @Bean
    public RestTemplate configurationMetadataDashboardRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService configurationMetadataDashboardRestService(RestTemplate configurationMetadataDashboardRestServiceRestTemplate
        , Environment environment, ConfigurationMetaDataExtractor<ConfigurationMetaData> configurationMetaDataProvider)
    {
        return new LoadBalancedDashboardRestServiceImpl(configurationMetadataDashboardRestServiceRestTemplate, environment
            , CONFIGURATION_METADATA_PATH
            , (Converter<Module, List>) module -> configurationMetaDataProvider.getComponentsConfiguration(module));
    }

    @Bean
    public RestTemplate flowCacheStateRestServiceRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new RestTemplate(customHttpRequestFactory);
    }

    @Bean
    public DashboardRestService flowCacheStateRestService(RestTemplate flowCacheStateRestServiceRestTemplate, Environment environment)
    {
        return new DashboardRestServiceImpl(flowCacheStateRestServiceRestTemplate, environment
            , FLOW_STATES_CACHE_PATH);

    }
}
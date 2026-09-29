package org.ikasan.ootb.data.sharing.module.boot;

import org.ikasan.dashboard.DashboardRestServiceImpl;
import org.ikasan.dashboard.DataSharingRestServiceImpl;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.ootb.data.sharing.module.util.DataSharingEnvironment;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import static org.ikasan.dashboard.DashboardClientAutoConfiguration.WIRETAP_PATH;
import static org.ikasan.dashboard.DataSharingRestServiceImpl.*;
import static org.ikasan.spec.dashboard.DashboardRestService.DASHBOARD_EXTRACT_ENABLED_PROPERTY;

@Configuration
public class ModuleConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "data-sharing")
    public DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration() {
        return new DataSharingConfiguredModuleConfiguration();
    }

    @Bean
    public DataSharingRestService wiretapDataSharingRestService
        (DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration) {

        DataSharingEnvironment environment = new DataSharingEnvironment();
        environment.setProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "true");
        environment.setProperty(DASHBOARD_BASE_URL_PROPERTY, dataSharingConfiguredModuleConfiguration.getUpstreamDashboard());
        environment.setProperty(DASHBOARD_USERNAME_PROPERTY, dataSharingConfiguredModuleConfiguration.getUpstreamDashboardUsername());
        environment.setProperty(DASHBOARD_PASSWORD_PROPERTY, dataSharingConfiguredModuleConfiguration.getUpstreamDashboardPassword());

        return new DataSharingRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory());
    }

    @Bean(name = "wiretapDashboardRestService")
    @Primary
    public DashboardRestService wiretapDashboardRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        return new DashboardRestServiceImpl(environment, customHttpRequestFactory, WIRETAP_PATH);
    }
}

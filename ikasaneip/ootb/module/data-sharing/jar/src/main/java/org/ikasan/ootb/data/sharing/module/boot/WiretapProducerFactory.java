package org.ikasan.ootb.data.sharing.module.boot;

import org.ikasan.dashboard.DashboardRestServiceImpl;
import org.ikasan.ootb.data.sharing.module.component.endpoint.producer.WiretapEventRestProducer;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.ootb.data.sharing.module.util.DataSharingEnvironment;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import static org.ikasan.dashboard.DashboardClientAutoConfiguration.WIRETAP_PATH;
import static org.ikasan.spec.dashboard.DashboardRestService.*;

@Service
public class WiretapProducerFactory {

    @Autowired
    DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration;

    /**
     * Creates and returns a {@link Producer} instance specifically configured for wiretap data sharing
     * with a downstream dashboard.
     *
     * @param targetDashboardName the name of the target dashboard for which the wiretap producer
     *                            will be created. This is used to fetch the corresponding dashboard URL,
     *                            username, and password from the module configuration.
     * @return a {@link Producer} instance configured to communicate with the specified target dashboard.
     */
    public Producer getWiretapProducer(String targetDashboardName) {
        DataSharingEnvironment environment = new DataSharingEnvironment();
        environment.setProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "true");
        environment.setProperty(DASHBOARD_BASE_URL_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboards().get(targetDashboardName));
        environment.setProperty(DASHBOARD_USERNAME_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardUsernames().get(targetDashboardName));
        environment.setProperty(DASHBOARD_PASSWORD_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardPasswords().get(targetDashboardName));

        DashboardRestService wiretapDashboardRestService
            = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), WIRETAP_PATH);

        return new WiretapEventRestProducer(wiretapDashboardRestService);
    }
}

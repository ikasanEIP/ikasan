package org.ikasan.dashboard;

import org.ikasan.spec.component.transformation.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@LoadBalancerClient(value = "module-extract", configuration = CustomLoadBalancerConfiguration.class)
public class LoadBalancedDashboardRestServiceImpl<T> extends DashboardRestServiceImpl
{
    Logger logger = LoggerFactory.getLogger(LoadBalancedDashboardRestServiceImpl.class);

    public LoadBalancedDashboardRestServiceImpl(RestTemplate restTemplate, Environment environment,
                                                String path, Converter converter)
    {
        this(restTemplate, environment, path);
        this.converter = converter;
    }

    public LoadBalancedDashboardRestServiceImpl(RestTemplate restTemplate, Environment environment, String path) {
        super(restTemplate, environment, path);
    }

    @Override
    protected void initialise(Environment environment, String path) {
        this.isEnabled = Boolean.valueOf(environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"));
        if (this.isEnabled)
        {
            if(path != null) {
                this.url = environment.getProperty(LoadBalancedDashboardRestServiceImpl.LOAD_BALANCED_DASHBOARD_BASE_URL_PROPERTY) + path;
            }
            else {
                this.url = environment.getProperty(LoadBalancedDashboardRestServiceImpl.LOAD_BALANCED_DASHBOARD_BASE_URL_PROPERTY);
            }
            this.authenticateUrl = environment.getProperty(LoadBalancedDashboardRestServiceImpl.LOAD_BALANCED_DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
            this.username = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_USERNAME_PROPERTY);
            this.password = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_PASSWORD_PROPERTY);
            this.moduleName = environment.getProperty(MODULE_NAME_PROPERTY);
            this.bubbleExceptionsUpToCaller = Boolean.valueOf(environment.getProperty(DASHBOARD_EXTRACT_EXCEPTIONS_PROPERTY, "false"));
        }
    }
}

package org.ikasan.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class CustomLoadBalancerConfiguration {
    Logger logger = LoggerFactory.getLogger(CustomLoadBalancerConfiguration.class);
    public static final String MODULE_EXTRACT = "module-extract";

    @Bean
    @ConfigurationProperties(prefix = "module.extract.services")
    public ServiceInstanceListSupplier serviceInstanceListSupplier(){
        logger.info("Loading ServiceInstanceListSupplier!");
        return new DashboardRestServiceInstanceListSupplier(MODULE_EXTRACT);
    }
}

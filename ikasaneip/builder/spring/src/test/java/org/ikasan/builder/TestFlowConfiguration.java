package org.ikasan.builder;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ImportResource(locations = {
    "/module-conf.xml",
    "/sample-flow-conf.xml",
    "/sample-component-conf.xml",
    "/recoveryManager-service-conf.xml",
    "/substitute-components.xml",
    "/ikasan-transaction-conf.xml",
    "/error-reporting-service-conf.xml",
    "/exclusion-service-conf.xml",
    "/serialiser-service-conf.xml",
    "/error-reporting-service-conf.xml",
    "/scheduler-service-conf.xml",
    "/configuration-service-conf.xml",
    "/systemevent-service-conf.xml",
    "/wiretap-service-conf.xml",
    "/replay-service-conf.xml",
    "/topology-conf.xml",
    "/exception-conf.xml",
    "/filter-service-conf.xml",
    "/h2-datasource-conf.xml"
})
@PropertySource("classpath:config.properties")
public class TestFlowConfiguration
{
}

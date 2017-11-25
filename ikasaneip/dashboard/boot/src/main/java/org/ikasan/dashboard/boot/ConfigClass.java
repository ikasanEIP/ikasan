package org.ikasan.dashboard.boot;

import org.springframework.boot.jta.narayana.NarayanaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( {


        "classpath:root-context.xml",
        "classpath:error-context.xml",
        "classpath:profile-context.xml",
        "classpath:replay-context.xml",
        "classpath:mapping-context.xml",
        "classpath:dashboard-context.xml",
        "classpath:hospital-context.xml",
        "classpath:persistence-setup-context.xml",

        "classpath:transaction-context.xml",
        "classpath:administration-context.xml",
        "classpath:policy-context.xml",
        "classpath:service-context.xml",
        "classpath:topology-context.xml",
        "classpath:monitor-context.xml",
        "classpath:notification-context.xml",
        "classpath:housekeeping-context.xml",

        "classpath:discovery-context.xml",
        "classpath:platform-service-conf.xml",
        "classpath:serialiser-service-conf.xml",
        "classpath:systemevent-service-conf.xml",
        "classpath:providers-conf.xml",
        "classpath:module-service-conf.xml",
        "classpath:scheduler-service-conf.xml",
        "classpath:solr-harvesting-context.xml",
        "classpath:search-context.xml",
        "classpath:datasource-conf.xml",
        "classpath:control-context.xml",
        /**

         /WEB-INF/discovery-context.xml,
         classpath:platform-service-conf.xml,
         classpath:serialiser-service-conf.xml,
         classpath:systemevent-service-conf.xml,
         classpath:providers-conf.xml,
         classpath:module-service-conf.xml,
         classpath:scheduler-service-conf.xml
         */
} )

@ComponentScan({
    "org.ikasan.web.*",
        "org.ikasan.rest.*",
        "org.ikasan.dashboard.ui.*",
        "org.ikasan.dashboard.boot.*"
})
public class ConfigClass {
    @Bean("narayanaProperties")
    public NarayanaProperties getNarayanaProperties(){
        NarayanaProperties narayanaProperties =  new NarayanaProperties();
        return narayanaProperties;
    }


}
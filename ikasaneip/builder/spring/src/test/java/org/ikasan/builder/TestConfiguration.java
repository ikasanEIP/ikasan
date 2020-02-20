package org.ikasan.builder;

import org.ikasan.module.IkasanModuleAutoConfiguration;
import org.ikasan.rest.module.IkasanRestAutoConfiguration;
import org.ikasan.transaction.IkasanTransactionConfiguration;
import org.ikasan.web.IkasanWebAutoConfiguration;
import org.ikasan.web.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {
    "/module-conf.xml",
    "/flow-conf.xml",
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

public class TestConfiguration
{
}

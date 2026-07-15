package org.ikasan.configurationService.service;

import org.ikasan.transaction.IkasanTransactionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {
    "classpath:configuration-service-conf.xml",
    "classpath:h2-datasource-conf.xml",
    "classpath:substitute-components.xml"
})
@Import({ IkasanTransactionConfiguration.class})
public class TestConfiguration
{
}

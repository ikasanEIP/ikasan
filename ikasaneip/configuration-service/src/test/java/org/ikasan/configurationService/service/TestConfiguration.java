package org.ikasan.configurationService.service;

import org.ikasan.transaction.IkasanTransactionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {
    "/configuration-service-conf.xml",
    "/hsqldb-datasource-conf.xml",
    "/substitute-components.xml"
})
@Import({ IkasanTransactionConfiguration.class})
public class TestConfiguration
{
}

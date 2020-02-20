package org.ikasan.topology.dao;

import org.ikasan.transaction.IkasanTransactionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {
    "/topology-conf.xml",
    "/topology-tx-conf.xml",
    "/h2db-config.xml"
})
@Import({ IkasanTransactionConfiguration.class})
public class TestConfiguration
{
}

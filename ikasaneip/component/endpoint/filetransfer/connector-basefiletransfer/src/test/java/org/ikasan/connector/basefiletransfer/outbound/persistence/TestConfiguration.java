package org.ikasan.connector.basefiletransfer.outbound.persistence;

import org.ikasan.transaction.IkasanTransactionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {
    "/substitute-beans.xml"
})
@Import({ IkasanTransactionConfiguration.class})
public class TestConfiguration
{
}

package org.ikasan.rest.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:configuration-service-conf.xml", "classpath:test-datasource-conf.xml" })
public class TestConfiguration
{
}

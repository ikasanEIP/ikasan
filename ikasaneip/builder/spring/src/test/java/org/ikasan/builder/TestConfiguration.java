package org.ikasan.builder;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {
    "/module-conf.xml",
    "/flow-conf.xml",
    "/sample-component-conf.xml",
    "/substitute-components.xml",
    "/h2-datasource-conf.xml"
})

public class TestConfiguration
{
}

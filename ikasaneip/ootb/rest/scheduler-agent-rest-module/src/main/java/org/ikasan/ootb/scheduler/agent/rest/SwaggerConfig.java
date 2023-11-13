package org.ikasan.ootb.scheduler.agent.rest;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                             .group("scheduler-agent")
                             .pathsToMatch("/rest/**")
                             .packagesToScan("org.ikasan.ootb.scheduler.agent.rest")
                             .build();
    }
}
package org.ikasan.rest.module;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {


    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                             .group("ikasan-public")
                             .pathsToMatch("/rest/**")
                             .packagesToScan("org.ikasan.rest.module")
                             .build();
    }
}
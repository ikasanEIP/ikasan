package org.ikasan.ootb.scheduler.agent.rest;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@AutoConfiguration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket schedulerAgentSwaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("org.ikasan.ootb.scheduler.agent.rest"))
            .paths(PathSelectors.any())
            .build()
            .groupName("scheduler-agent");
    }
}
package org.ikasan.builder;

import jakarta.annotation.Resource;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.spec.flow.Flow;
import org.springframework.context.annotation.Bean;
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
    @Resource
    BuilderFactory builderFactory;

    @Bean
    public Flow scheduledBuilderFlow()
    {

        FlowBuilder flowBuilder = builderFactory.getModuleBuilder("moduleName")
                                                .withDescription("Example module with pattern builder")
                                                .getFlowBuilder("scheduledBuilderFlow");
        return flowBuilder.withDescription("scheduled flow description")
                          .consumer("consumer", builderFactory.getComponentBuilder().scheduledConsumer()
                                                              .setCronExpression("0/5 * * * * ?")
                                                              .setConfiguredResourceId("configuredResourceId")
                                                                .setScheduledJobGroupName("scheduledJobGroupName")
                                                              .setScheduledJobName("scheduledJobName")
                                                              .build())
                          .producer("producer", builderFactory.getComponentBuilder()
                                                              .devNullProducer().build())
                          .build();
    }
}

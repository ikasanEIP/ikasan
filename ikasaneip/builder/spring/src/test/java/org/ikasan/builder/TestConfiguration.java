package org.ikasan.builder;

import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {
    "classpath:module-conf.xml",
    "classpath:flow-conf.xml",
    "classpath:sample-component-conf.xml",
    "classpath:substitute-components.xml",
    "classpath:h2-datasource-conf.xml"
})
public class TestConfiguration
{
    @Autowired
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

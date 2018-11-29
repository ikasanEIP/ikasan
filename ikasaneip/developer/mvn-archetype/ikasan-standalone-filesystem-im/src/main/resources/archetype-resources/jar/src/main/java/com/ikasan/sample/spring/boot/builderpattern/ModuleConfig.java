package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.flow.visitorPattern.invoker.FilterInvokerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    @Bean
    public Module getModule()
    {

        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("${artifactId}");

        Flow sourceFlow = moduleBuilder.getFlowBuilder("${sourceFlowName}")
            .withDescription("Sample file to JMS flow")
            .withExceptionResolver( componentFactory.getSourceFlowExceptionResolver() )
            .consumer("File Consumer", componentFactory.getFileConsumer())
            .filter("My Filter", componentFactory.getFilter(), new FilterInvokerConfiguration())
            .converter("File Converter", componentFactory.getSourceFileConverter())
            .producer("JMS Producer", componentFactory.getJmsProducer()).build();

        Flow targetFlow = moduleBuilder.getFlowBuilder("${targetFlowName}")
            .withDescription("Sample JMS to file flow")
            .consumer("JMS Consumer", componentFactory.getJmsConsumer())
            .producer("File Producer", componentFactory.getFileProducer()).build();

        Module module = moduleBuilder.withDescription("Sample file consumer / producer module.")
            .addFlow(sourceFlow)
            .addFlow(targetFlow)
            .build();

        return module;
    }
}

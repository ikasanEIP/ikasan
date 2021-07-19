package org.ikasan.ootb.scheduler.agent;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.ComponentFactory;
import org.ikasan.ootb.scheduler.agent.module.MyFlowFactory;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {

    @Value( "${module.name}" )
    String moduleName;

    @Resource
    BuilderFactory builderFactory;

    @Resource
    ComponentFactory componentFactory;

    @Bean
    @Primary
    public Module myTestModule()
    {
        ConfiguredModuleConfiguration configuration = new ConfiguredModuleConfiguration();
        configuration.getFlowDefinitions().put("Scheduler Flow 1", "MANUAL");

        // get the module builder
        return builderFactory.getModuleBuilder(moduleName)
            .withDescription("Scheduler Agent Integration Module.")
            .withType(ModuleType.SCHEDULER_AGENT)
            .withFlowFactory( new MyFlowFactory(builderFactory, componentFactory) )
            .setConfiguration(configuration)
            .build();
    }
}



package com.ikasan.sample.spring.boot.flow;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.RouteBuilder;
import org.ikasan.builder.Route;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;

import com.ikasan.sample.spring.boot.component.ComponentFactory;
@Configuration
public class JMSFLowConfig
{
    @Value("${module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    /**
    * Create flow bean for flow JMS FLow.

    * @return the flow bean.
    */
    @Bean(name = "jMSFLow")
    public Flow JMSFLow()
    {
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        return moduleBuilder.getFlowBuilder("JMS FLow")
        .consumer("JMS Consumer", componentFactory.getJMSConsumer())
        .broker("Exception Generating Broker", componentFactory.getExceptionGeneratingBroker())
        .translator("My Very Special Translator", componentFactory.getMyVerySpecialTranslator())
        .producer("JMS Producer", componentFactory.getJMSProducer())
        .build();
    }

}

package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.annotation.Resource;
import org.ikasan.builder.*;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( {
    "classpath:ikasan-transaction-pointcut-jms.xml",
    "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig
{
    @Resource BuilderFactory builderFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Value("${jms.connectionFactory.jndi.name}")
    private String jmsConnectionFactoryName;

    @Value("${jms.naming.factory.initial}")
    private String jmsNamingFactoryInitial;

    @Value("${jms.source.destination}")
    private String jmsSourceDestination;

    @Value("${jms.target.destination}")
    private String jmsTargetDestination;

    @Bean
    public Module getModule()
    {

        // get a module builder from the ikasanApplication
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("sample-builder-pattern").withDescription("Example module with pattern builder");

        // get an instance of flowBuilder from the moduleBuilder and create a flow
        Flow scheduledFlow = getScheduledFlow(moduleBuilder, builderFactory.getComponentBuilder());

        // get an instance of flowBuilder from the moduleBuilder and create a flow
        Flow jmsFlow = getJmsFlow(moduleBuilder, builderFactory.getComponentBuilder());

        // add flows to the module
        Module module = moduleBuilder.addFlow(scheduledFlow).addFlow(jmsFlow).build();

        return module;
    }

    public Flow getScheduledFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Scheduled Flow");
        return flowBuilder.withDescription("scheduled flow description")
            .consumer("consumer", componentBuilder.scheduledConsumer().setCronExpression("0/5 * * * * ?").setConfiguredResourceId("configuredResourceId")
                .setScheduledJobGroupName("scheduledJobGroupName").setScheduledJobName("scheduledJobName").build())
            .concurrentSplitter("splitterName", componentBuilder.listSplitter(),
                org.ikasan.builder.invoker.Configuration.concurrentSplitterInvoker().setConcurrentThreads(5))
            .producer("producer", new MyProducer()).build();
    }

    public Flow getJmsFlow(ModuleBuilder moduleBuilder,ComponentBuilder componentBuilder) {
        FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Jms Flow");

        return flowBuilder.withDescription("Jms flow description")
            .consumer("consumer", componentBuilder.jmsConsumer().setConfiguredResourceId("configuredResourceId")
                .setDestinationJndiName(jmsSourceDestination)
                .setConnectionFactoryName(jmsConnectionFactoryName)
                .setConnectionFactoryJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                .setConnectionFactoryJndiPropertyProviderUrl(brokerUrl)
                .setDestinationJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                .setDestinationJndiPropertyProviderUrl(brokerUrl)
                .setAutoContentConversion(true)
                .build()
            )
            .producer("producer", componentBuilder.jmsProducer()
                .setConfiguredResourceId("crid")
                .setDestinationJndiName(jmsTargetDestination)
                .setConnectionFactoryName(jmsConnectionFactoryName)
                .setConnectionFactoryJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                .setConnectionFactoryJndiPropertyProviderUrl(brokerUrl)
                .setDestinationJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                .setDestinationJndiPropertyProviderUrl(brokerUrl).build()
            )
            .build();

    }

    private class MyProducer implements Producer
    {

        @Override
        public void invoke(Object payload) throws EndpointException
        {

        }
    }
}
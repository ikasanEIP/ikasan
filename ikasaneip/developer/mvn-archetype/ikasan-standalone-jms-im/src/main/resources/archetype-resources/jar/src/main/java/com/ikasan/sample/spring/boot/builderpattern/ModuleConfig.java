package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;

@Configuration
@ImportResource( {

        "classpath:monitor-service-conf.xml",
        "classpath:monitor-conf.xml",
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig {


    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private ErrorReportingServiceFactory errorReportingServiceFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-jms");

        FlowBuilder fb = mb.getFlowBuilder("${sourceFlowName}");

        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        Consumer jmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
                .setConnectionFactory(consumerConnectionFactory)
                .setDestinationJndiName("source")
                .setAutoContentConversion(true)
                .setConfiguredResourceId("jmsConsumer")
                .build();


        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
                .setConnectionFactory(producerConnectionFactory)
                .setDestinationJndiName("target")
                .setConfiguredResourceId("jmsProducer")
                .build();

        Flow flow = fb
                .withDescription("Flow demonstrates usage of JMS Concumer and JMS Producer")
                .withErrorReportingServiceFactory(errorReportingServiceFactory)
                .consumer("JMS Consumer", jmsConsumer)
                .broker( "Exception Generating Broker", new ExceptionGenerationgBroker())
                .producer("JMS Producer", jmsProducer)
                .build();

        Module module = mb.withDescription("Sample Module").addFlow(flow).build();
        return module;
    }




}

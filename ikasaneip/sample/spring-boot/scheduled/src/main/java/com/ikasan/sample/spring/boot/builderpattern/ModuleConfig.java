package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.*;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.connector.basefiletransfer.BaseFileTransferAutoConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.Resource;
import jakarta.jms.ConnectionFactory;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:h2-datasource-conf.xml"

} )
@Import(BaseFileTransferAutoConfiguration.class)
public class ModuleConfig {

    @Resource
    private BuilderFactory builderFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Resource
    private JtaTransactionManager transactionManager;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-schdeuled-jms");

        Flow sftpToJmsFlow = getScheduledToJmsFlow(mb,builderFactory.getComponentBuilder());

        Module module = mb.withDescription("Scheduled to Jms Sample Module")
                .addFlow(sftpToJmsFlow).build();
        return module;

    }

    public Flow getScheduledToJmsFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("sftp.private.jms.queue")
            .setConfiguredResourceId("sftpJmsProducer")
            .build();

        SampleMessageProvider sampleMessageProvider = new SampleMessageProvider();
        sampleMessageProvider.setTransactionManager(this.transactionManager);

        FlowBuilder sftpToLogFlowBuilder = moduleBuilder.getFlowBuilder("Scheduled To Jms Flow");
        Flow sftpToJmsFlow = sftpToLogFlowBuilder
                .withDescription("Scheduled To Jms")
                .consumer("Scheduled Consumer", componentBuilder.scheduledConsumer()
                        .setMessageProvider(sampleMessageProvider)
                        .setManagedEventIdentifierService(new SampleIdentifierService())
                        .setConfiguration(new ScheduledConsumerConfiguration())
                        .setConfiguredResourceId("scheduled-consumer")
                        .build())
                .broker("Exception Generating Broker", new ExceptionGeneratingBroker())
                .producer("Scheduled Jms Producer", jmsProducer)
                .build();
        return sftpToJmsFlow;
    }
}

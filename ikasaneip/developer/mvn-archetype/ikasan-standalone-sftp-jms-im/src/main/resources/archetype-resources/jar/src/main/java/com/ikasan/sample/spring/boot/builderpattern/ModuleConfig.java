package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.*;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.converter.filetransfer.MapMessageToPayloadConverter;
import org.ikasan.component.converter.filetransfer.PayloadToMapConverter;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
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
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:filetransfer-service-conf.xml",
        "classpath:h2-datasource-conf.xml"

} )
public class ModuleConfig {

    @Resource
    private BuilderFactory builderFactory;

    @Value("${sftp.consumer.cronExpression}")
    private String sftpConsumerCronExpression;

    @Value("${sftp.consumer.clientID}")
    private String sftpConsumerClientID;

    @Value("${sftp.consumer.username}")
    private String sftpConsumerUsername;

    @Value("${sftp.consumer.password}")
    private String sftpConsumerPassword;

    @Value("${sftp.consumer.remoteHost}")
    private String sftpConsumerRemoteHost;

    @Value("${sftp.consumer.remotePort}")
    private Integer sftpConsumerRemotePort;

    @Value("${sftp.consumer.sourceDirectory}")
    private String sftpConsumerSourceDirectory;

    @Value("${sftp.consumer.filenamePattern}")
    private String sftpConsumerFilenamePattern;

    @Value("${sftp.consumer.knownHosts}")
    private String sftpConsumerKnownHosts;

    @Value("${sftp.producer.clientID}")
    private String sftpProducerClientID;

    @Value("${sftp.producer.username}")
    private String sftpProducerUsername;

    @Value("${sftp.producer.password}")
    private String sftpProducerPassword;

    @Value("${sftp.producer.remoteHost}")
    private String sftpProducerRemoteHost;

    @Value("${sftp.producer.remotePort}")
    private Integer sftpProducerRemotePort;

    @Value("${sftp.producer.outputDirectory}")
    private String sftpProducerOutputDirectory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("${artifactId}");

        Flow jmsToSftpFlow = getJmsToSftpFlow(mb,builderFactory.getComponentBuilder());
        Flow sftpToJmsFlow = getSftpConsumerFlow(mb,builderFactory.getComponentBuilder());

        Module module = mb.withDescription("Sftp Jms Sample Module")
                .addFlow(sftpToJmsFlow).addFlow(jmsToSftpFlow).build();
        return module;

    }

    public Flow getSftpConsumerFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("sftp.private.jms.queue")
            .setConfiguredResourceId("sftpJmsProducer")
            .build();

        FlowBuilder sftpToLogFlowBuilder = moduleBuilder.getFlowBuilder("${sourceFlowName}");
        Flow sftpToJmsFlow = sftpToLogFlowBuilder
                .withDescription("Sftp to Jms")
                .consumer("Sftp Consumer", componentBuilder.sftpConsumer()
                        .setCronExpression(sftpConsumerCronExpression)
                        .setClientID(sftpConsumerClientID)
                        .setUsername(sftpConsumerUsername)
                        .setPassword(sftpConsumerPassword)
                        .setRemoteHost(sftpConsumerRemoteHost)
                        .setRemotePort(sftpConsumerRemotePort)
                        .setSourceDirectory(sftpConsumerSourceDirectory)
                        .setFilenamePattern(sftpConsumerFilenamePattern)
                        .setKnownHostsFilename(sftpConsumerKnownHosts)
                        .setChronological(true)
                        .setCleanupJournalOnComplete(false)
                        .setAgeOfFiles(30)
                        .setMinAge(1l)
                        .setFilterDuplicates(true)
                        .setFilterOnLastModifiedDate(true)
                        .setRenameOnSuccess(false)
                        .setRenameOnSuccessExtension(".tmp")
                        .setDestructive(false)
                        .setChunking(false)
                        .setConfiguredResourceId("configuredResourceId")
                        .setScheduledJobGroupName("SftpToLogFlow")
                        .setScheduledJobName("SftpConsumer")
                        .build())
                .converter("Sftp Payload to Map Converter",new PayloadToMapConverter())
                .producer("Sftp Jms Producer", jmsProducer).build();
        return sftpToJmsFlow;
    }

    public Flow getJmsToSftpFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        Consumer sftpJmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
            .setConnectionFactory(consumerConnectionFactory)
            .setDestinationJndiName("sftp.private.jms.queue")
            //.setAutoContentConversion(true)
            .setConfiguredResourceId("sftpJmsConsumer")
            .build();

        Producer sftpProducer = componentBuilder.sftpProducer()
            .setClientID(sftpProducerClientID)
            .setUsername(sftpProducerUsername)
            .setPassword(sftpProducerPassword)
            .setRemoteHost(sftpProducerRemoteHost)
            .setRemotePort(sftpProducerRemotePort)
            .setOutputDirectory(sftpProducerOutputDirectory)
            .setConfiguredResourceId("sftpProducerConfiguration")
            .build();

        FlowBuilder jmsToSftpFlowBuilder = moduleBuilder.getFlowBuilder("${targetFlowName}");
        Flow timeGeneratorToSftpFlow = jmsToSftpFlowBuilder
                .withDescription("Receives Text Jms message and sends it to sftp as file")
                .consumer("Sftp Jms Consumer", sftpJmsConsumer)
                .converter("MapMessage to SFTP Payload Converter",new MapMessageToPayloadConverter())
                .producer("Sftp Producer", sftpProducer)
                .build();

        return timeGeneratorToSftpFlow;
    }


}

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

    @Value("${ftp.consumer.cronExpression}")
    private String ftpConsumerCronExpression;

    @Value("${ftp.consumer.clientID}")
    private String ftpConsumerClientID;

    @Value("${ftp.consumer.username}")
    private String ftpConsumerUsername;

    @Value("${ftp.consumer.password}")
    private String ftpConsumerPassword;

    @Value("${ftp.consumer.remoteHost}")
    private String ftpConsumerRemoteHost;

    @Value("${ftp.consumer.remotePort}")
    private Integer ftpConsumerRemotePort;

    @Value("${ftp.consumer.sourceDirectory}")
    private String ftpConsumerSourceDirectory;

    @Value("${ftp.consumer.filenamePattern}")
    private String ftpConsumerFilenamePattern;

    @Value("${ftp.producer.clientID}")
    private String ftpProducerClientID;

    @Value("${ftp.producer.username}")
    private String ftpProducerUsername;

    @Value("${ftp.producer.password}")
    private String ftpProducerPassword;

    @Value("${ftp.producer.remoteHost}")
    private String ftpProducerRemoteHost;

    @Value("${ftp.producer.remotePort}")
    private Integer ftpProducerRemotePort;

    @Value("${ftp.producer.outputDirectory}")
    private String ftpProducerOutputDirectory;


    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("${artifactId}");

        Flow jmsToFtpFlow = getJmsToFtpFlow(mb,builderFactory.getComponentBuilder());
        Flow ftpToJmsFlow = getFtpConsumerFlow(mb,builderFactory.getComponentBuilder());

        Module module = mb.withDescription("Ftp Jms Sample Module")
                .addFlow(ftpToJmsFlow).addFlow(jmsToFtpFlow).build();
        return module;

    }

    public Flow getFtpConsumerFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("ftp.private.jms.queue")
            .setConfiguredResourceId("ftpJmsProducer")
            .build();

        FlowBuilder ftpToLogFlowBuilder = moduleBuilder.getFlowBuilder("${sourceFlowName}");
        Flow ftpToJmsFlow = ftpToLogFlowBuilder
                .withDescription("Ftp to Jms")
                .consumer("Ftp Consumer", componentBuilder.ftpConsumer()
                    .setCronExpression(ftpConsumerCronExpression)
                    .setClientID(ftpConsumerClientID)
                    .setUsername(ftpConsumerUsername)
                    .setPassword(ftpConsumerPassword)
                    .setRemoteHost(ftpConsumerRemoteHost)
                    .setRemotePort(ftpConsumerRemotePort)
                    .setSourceDirectory(ftpConsumerSourceDirectory)
                    .setFilenamePattern(ftpConsumerFilenamePattern)
                    .setChronological(true)
                    .setAgeOfFiles(30)
                    .setMinAge(1l)
                    .setFilterDuplicates(true)
                    .setFilterOnLastModifiedDate(true)
                    .setRenameOnSuccess(false)
                    .setRenameOnSuccessExtension(".tmp")
                    .setDestructive(false)
                    .setChunking(false)
                    .setConfiguredResourceId("configuredResourceId")
                    .setScheduledJobGroupName("FtpToLogFlow")
                    .setScheduledJobName("FtpConsumer")
                    .build())
                .converter("Ftp Payload to Map Converter",new PayloadToMapConverter())
                .producer("Ftp Jms Producer", jmsProducer).build();
        return ftpToJmsFlow;
    }

    public Flow getJmsToFtpFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        Consumer ftpJmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
            .setConnectionFactory(consumerConnectionFactory)
            .setDestinationJndiName("ftp.private.jms.queue")
            //.setAutoContentConversion(true)
            .setConfiguredResourceId("ftpJmsConsumer")
            .build();

        Producer ftpProducer = componentBuilder.ftpProducer()
            .setClientID(ftpProducerClientID)
            .setUsername(ftpProducerUsername)
            .setPassword(ftpProducerPassword)
            .setRemoteHost(ftpProducerRemoteHost)
            .setRemotePort(ftpProducerRemotePort)
            .setOutputDirectory(ftpProducerOutputDirectory)
            .setOverwrite(true)
            .setConfiguredResourceId("ftpProducerConfiguration")
            .build();

        FlowBuilder jmsToFtpFlowBuilder = moduleBuilder.getFlowBuilder("${targetFlowName}");
        Flow jmsToftpFlow = jmsToFtpFlowBuilder
                .withDescription("Receives Text Jms message and sends it to FTP as file")
                .consumer("Ftp Jms Consumer", ftpJmsConsumer)
                .converter("MapMessage to FTP Payload Converter",new MapMessageToPayloadConverter())
                .producer("Ftp Producer", ftpProducer)
                .build();

        return jmsToftpFlow;
    }


}

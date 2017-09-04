package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.util.producer.DevNull;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ImportResource( {

        "classpath:monitor-service-conf.xml",
        "classpath:monitor-conf.xml",
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:logger-conf.xml",
        "classpath:exception-conf.xml",
        "classpath:ftp-components-conf.xml",
        "classpath:filetransfer-service-conf.xml"


} )
public class ModuleConfig {

    @Resource
    private ScheduledConsumer fileGeneratorScheduledConsumer;

    @Resource
    private Converter payloadToStringConverter;
    @Resource
    private Converter filePayloadGeneratorConverter;

    @Resource
    private Producer ftpProducer;

    @Resource
    private ApplicationContext context;

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

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-ftp-module");

        Flow ftpToLogFlow = getFtpToLogFlow(mb, builderFactory.getComponentBuilder());

        FlowBuilder timeGeneratorToFtpFlowBuilder = mb.getFlowBuilder("timeGeneratorToFtpFlow");
        Flow timeGeneratorToFtpFlow = timeGeneratorToFtpFlowBuilder
                .withDescription("Generates random string and send it to ftp as file")
                .consumer("Scheduled Consumer", fileGeneratorScheduledConsumer)
                .converter("Random String Generator",filePayloadGeneratorConverter)
                .producer("Ftp Producer", ftpProducer).build();

        Module module = mb.withDescription("Sample Spring Boot FTP Module")
                .addFlow(ftpToLogFlow).addFlow(timeGeneratorToFtpFlow).build();
        return module;
    }


    public Flow getFtpToLogFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        FlowBuilder ftpToLogFlowBuilder = moduleBuilder.getFlowBuilder("ftpToLogFlow");

        Flow ftpToLogFlow = ftpToLogFlowBuilder
                .withDescription("Ftp to Log")
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
                        .build())
                .converter("FTP payload to String Converter",payloadToStringConverter)
                .producer("Log", new DevNull()).build();

        return ftpToLogFlow;
    }




}

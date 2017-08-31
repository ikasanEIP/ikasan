package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.util.producer.DevNull;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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
    private ScheduledConsumer ftpConsumer;

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

    @Bean
    public Module getModule(){

        ModuleBuilder mb = IkasanApplicationFactory.getBuilderFactory(context).getModuleBuilder("sample-boot-ftp-module");
        FlowBuilder ftpToLogFlowBuilder = mb.getFlowBuilder("ftpToLogFlow");
        Flow ftpToLogFlow = ftpToLogFlowBuilder
                .withDescription("Ftp to Log")
                .consumer("Ftp Consumer", ftpConsumer)
                .converter("FTP payload to String Converter",payloadToStringConverter)
                .producer("Log", new DevNull()).build();

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





}

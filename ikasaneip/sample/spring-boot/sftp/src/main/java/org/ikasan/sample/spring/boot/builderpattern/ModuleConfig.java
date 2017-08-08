package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.util.producer.DevNull;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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
        "classpath:sftp-to-log-component-conf.xml",
        "classpath:filetransfer-service-conf.xml"


} )
public class ModuleConfig {

    @Resource
    private ScheduledConsumer sftpConsumer;

    @Resource
    private Converter payloadToStringConverter;

    @Resource
    private AutowireCapableBeanFactory beanFactory;

    @Bean
    public Module getModule(){

        FlowBuilder fb = BuilderFactory.flowBuilder("sftpToLogFlow", "sample-module");
        beanFactory.autowireBean(fb);
        Flow flow = fb
                .withDescription("Sftp to Log")
                .consumer("Sftp Consumer", sftpConsumer)
                .converter("SFTP payload to String Converter",payloadToStringConverter)
                .producer("Log", new DevNull()).build();

        Module module = BuilderFactory.moduleBuilder("sftp-sample-module").withDescription("SFTP Sample Module").addFlow(flow).build();
        return module;
    }





}

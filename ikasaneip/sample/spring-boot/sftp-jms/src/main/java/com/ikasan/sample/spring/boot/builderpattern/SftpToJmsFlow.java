package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.annotation.Resource;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.converter.filetransfer.PayloadToMapConverter;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("SftpToJmsFlowFactory")
public class SftpToJmsFlow
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${module.name}")
    private String moduleName;

    @Resource
    private Consumer sftpConsumer;

    @Resource
    private Producer jmsProducer;

    @Bean
    public Flow sftpToJmsFlow()
    {

        return builderFactory.getModuleBuilder(moduleName)
                             .getFlowBuilder("Sftp To Jms Flow").withDescription("Sftp to Jms")
                             .consumer("Sftp Consumer", sftpConsumer)
                             .converter("Sftp Payload to Map Converter", new PayloadToMapConverter())
                             .producer("Sftp Jms Producer", jmsProducer).build();
    }

}

package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.converter.filetransfer.PayloadToMapConverter;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration("SftpToJmsFlowFactory")
public class SftpToJmsFlow
{
    @Autowired
    private BuilderFactory builderFactory;

    @Value("${module.name}")
    private String moduleName;

    @Autowired
    private Consumer sftpConsumer;

    @Autowired
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

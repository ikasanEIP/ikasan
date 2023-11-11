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

@Configuration("SftpChunkingToJmsFlowFactory")
public class SftpChunkingToJmsFlow
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${module.name}")
    private String moduleName;

    @Resource
    private Consumer sftpChunkingConsumer;

    @Resource
    private Producer jmsChunkingProducer;

    @Bean
    public Flow sftpChunkingToJmsFlow()
    {

        return builderFactory.getModuleBuilder(moduleName)
                             .getFlowBuilder("Sftp Chunking To Jms Flow").withDescription("Sftp Chunking to Jms")
                             .consumer("Sftp Chunking Consumer", sftpChunkingConsumer)
                             .converter("Sftp Payload to Map Converter", new PayloadToMapConverter())
                             .producer("Sftp Chunking Jms Producer", jmsChunkingProducer).build();
    }

}

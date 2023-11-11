package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.annotation.Resource;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.converter.filetransfer.MapMessageToPayloadConverter;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("JmsToSftpChunkingFlowFactory")
public class JmsToSftpChunkingFlow
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${module.name}")
    private String moduleName;

    @Resource
    private Consumer jmsSftpChunkingConsumer;

    @Resource
    private Producer sftpChunkingProducer;

    @Bean
    public Flow jmsToSftpChunkingFlow()
    {

        return builderFactory.getModuleBuilder(moduleName)
                             .getFlowBuilder("Jms To Sftp Chunking Flow")
            .withDescription("Receives Map Jms message Representing Chunked SFTP content sends it to sftp as file")
            .consumer("Sftp Jms Consumer", jmsSftpChunkingConsumer)
            .converter("MapMessage to SFTP Payload Converter",new MapMessageToPayloadConverter())
            .producer("Sftp Producer", sftpChunkingProducer)
            .build();


    }

}

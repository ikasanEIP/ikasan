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

@Configuration("JmsToSftpFlowFactory")
public class JmsToSftpFlow
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${module.name}")
    private String moduleName;

    @Resource
    private Consumer jmsSftpConsumer;

    @Resource
    private Producer sftpProducer;

    @Bean
    public Flow jmsToSftpFlow()
    {

        return builderFactory.getModuleBuilder(moduleName)
                             .getFlowBuilder("Jms To Sftp Flow")
            .withDescription("Receives Text Jms message and sends it to sftp as file")
            .consumer("Sftp Jms Consumer", jmsSftpConsumer)
            .converter("MapMessage to SFTP Payload Converter",new MapMessageToPayloadConverter())
            .producer("Sftp Producer", sftpProducer)
            .build();


    }

}

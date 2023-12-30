package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import jakarta.jms.ConnectionFactory;

@Configuration
public class JmsToSftpChunkingFlowComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean(name = "jmsSftpChunkingConsumerConfiguration")
    @ConfigurationProperties(prefix = "jms.to.sftp.chunking.flow.jms.consumer")
    public SpringMessageConsumerConfiguration jmsSftpChunkingConsumerConfiguration()
    {
        return new SpringMessageConsumerConfiguration();
    }

    @Bean
    public Consumer jmsSftpChunkingConsumer(@Qualifier("jmsSftpChunkingConsumerConfiguration") SpringMessageConsumerConfiguration jmsSftpChunkingConsumerConfiguration)
    {

        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        return builderFactory.getComponentBuilder().jmsConsumer()
                             .setConnectionFactory(consumerConnectionFactory)
                             .setConfiguration(jmsSftpChunkingConsumerConfiguration)
                             .setConfiguredResourceId("sftpChunkingJmsConsumer")
                             .build();

    }

    @Bean(name = "sftpChunkingProducerConfiguration")
    @ConfigurationProperties(prefix = "jms.to.sftp.chunking.flow.sftp.producer")
    public SftpProducerConfiguration sftpChunkingProducerConfiguration()
    {
        return new SftpProducerConfiguration();
    }

    @Bean
    public Producer sftpChunkingProducer(@Qualifier("sftpChunkingProducerConfiguration") SftpProducerConfiguration sftpChunkingProducerConfiguration)
    {

        return builderFactory.getComponentBuilder().chunkSftpProducer()
                             .setConfiguration(sftpChunkingProducerConfiguration)
                             .setConfiguredResourceId("sftpChunkingProducerConfiguration")
                             .build();

    }

}

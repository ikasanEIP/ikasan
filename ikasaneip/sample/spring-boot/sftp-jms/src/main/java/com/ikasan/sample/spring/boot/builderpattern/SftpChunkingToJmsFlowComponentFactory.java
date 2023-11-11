package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.annotation.Resource;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.jms.ConnectionFactory;

@Configuration
public class SftpChunkingToJmsFlowComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    @ConfigurationProperties(prefix = "sftp.chunking.to.jms.flow.sftp.consumer")
    public SftpConsumerConfiguration sftpChunkingConsumerConfiguration()
    {
        return new SftpConsumerConfiguration();
    }

    @Bean
    public Consumer sftpChunkingConsumer(SftpConsumerConfiguration sftpChunkingConsumerConfiguration)
    {
        return builderFactory.getComponentBuilder()
                             .sftpConsumer()
                             .setConfiguration(sftpChunkingConsumerConfiguration)
                             .setConfiguredResourceId("SftpChunkingConsumer")
                             .setScheduledJobGroupName("SftpChunkingConsumer")
                             .setScheduledJobName("SftpChunkingConsumer").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "sftp.chunking.to.jms.flow.jms.producer")

    public SpringMessageProducerConfiguration jmsChunkingProducerConfiguration()
    {
        return new SpringMessageProducerConfiguration();
    }


    @Bean
    public Producer jmsChunkingProducer(SpringMessageProducerConfiguration jmsChunkingProducerConfiguration)
    {
        ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

        return builderFactory.getComponentBuilder().jmsProducer().setConnectionFactory(producerConnectionFactory)
                             .setConfiguration(jmsChunkingProducerConfiguration)
                             .setConfiguredResourceId("sftpChunkingJmsProducer").build();
    }

}

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
public class JmsToSftpFlowComponentFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean(name = "jmsSftpConsumerConfiguration")
    @ConfigurationProperties(prefix = "jms.to.sftp.flow.jms.consumer")
    public SpringMessageConsumerConfiguration jmsSftpConsumerConfiguration()
    {
        return new SpringMessageConsumerConfiguration();
    }

    @Bean
    public Consumer jmsSftpConsumer(@Qualifier("jmsSftpConsumerConfiguration") SpringMessageConsumerConfiguration jmsSftpConsumerConfiguration)
    {

        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        return builderFactory.getComponentBuilder().jmsConsumer()
                             .setConnectionFactory(consumerConnectionFactory)
                             .setConfiguration(jmsSftpConsumerConfiguration)
                             .setConfiguredResourceId("sftpJmsConsumer")
                             .build();

    }

    @Bean(name = "sftpProducerConfiguration")
    @ConfigurationProperties(prefix = "jms.to.sftp.flow.sftp.producer")
    public SftpProducerConfiguration sftpProducerConfiguration()
    {
        return new SftpProducerConfiguration();
    }

    @Bean
    public Producer sftpProducer(@Qualifier("sftpProducerConfiguration") SftpProducerConfiguration sftpProducerConfiguration)
    {

        return builderFactory.getComponentBuilder().sftpProducer()
                             .setConfiguration(sftpProducerConfiguration)
                             .setConfiguredResourceId("sftpProducerConfiguration").build();

    }

}

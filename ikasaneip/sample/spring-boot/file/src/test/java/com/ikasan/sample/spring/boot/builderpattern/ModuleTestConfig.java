package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;

@Configuration
public class ModuleTestConfig
{
    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Resource JmsListenerEndpointRegistry registry;

    @Bean
    @DependsOn("broker")
    JmsTemplate jmsTemplate()
    {
        JmsTemplate jmsTemplate = new JmsTemplate(new ActiveMQConnectionFactory(brokerUrl));
        return jmsTemplate;
    }

    @Bean
    @DependsOn("broker")
    MessageListenerVerifier messageListenerVerifierTarget()
    {

        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "jms.topic.test", registry);
        return messageListenerVerifier;
    }

    @Bean EmbeddedActiveMQBroker broker()
    {

        EmbeddedActiveMQBroker broker =  new EmbeddedActiveMQBroker();
        broker.start();
        return broker;
    }
}

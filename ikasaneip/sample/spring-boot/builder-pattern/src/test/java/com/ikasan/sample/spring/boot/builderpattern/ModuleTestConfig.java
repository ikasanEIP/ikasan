package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class ModuleTestConfig
{
    @Resource
    JmsListenerEndpointRegistry registry;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean JmsTemplate jmsTemplate()
    {
        JmsTemplate jmsTemplate = new JmsTemplate(new ActiveMQConnectionFactory(brokerUrl));
        return jmsTemplate;
    }

    @Bean
    MessageListenerVerifier messageListenerVerifierTarget()
    {

        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "target", registry);
        return messageListenerVerifier;
    }
}

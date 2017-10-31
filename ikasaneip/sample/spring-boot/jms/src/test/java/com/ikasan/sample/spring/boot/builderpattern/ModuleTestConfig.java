package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
public class ModuleTestConfig
{
    @Value("${jms.provider.url}")
    private String brokerUrl;


    @Bean JmsListenerContainerFactory<?> myJmsContainerFactory()
    {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
        return factory;
    }

    @Bean JmsTemplate jmsTemplate()
    {
        JmsTemplate jmsTemplate = new JmsTemplate(new ActiveMQConnectionFactory(brokerUrl));
        return jmsTemplate;
    }
}

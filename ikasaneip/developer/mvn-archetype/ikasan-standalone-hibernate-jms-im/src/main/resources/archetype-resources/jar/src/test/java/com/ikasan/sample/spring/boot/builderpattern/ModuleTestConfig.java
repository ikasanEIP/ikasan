package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class ModuleTestConfig
{
    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    JmsTemplate jmsTemplate()
    {
        JmsTemplate jmsTemplate = new JmsTemplate(new ActiveMQConnectionFactory(brokerUrl));
        return jmsTemplate;
    }

    /**
     * Registry the tests use for MessageListenerVerifier endpoints - it is not
     * provided by auto-configuration in the test context.
     */
    @Bean
    public JmsListenerEndpointRegistry jmsListenerEndpointRegistry()
    {
        return new JmsListenerEndpointRegistry();
    }
}

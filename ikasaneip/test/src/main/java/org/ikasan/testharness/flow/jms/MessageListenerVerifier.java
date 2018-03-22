package org.ikasan.testharness.flow.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for capturing of test messages on JMS endpoints
 */
public class MessageListenerVerifier implements MessageListener
{
    TestJmsListenerEndpoint endpoint;

    public MessageListenerVerifier(final String brokerUrl, final String destinationName, final JmsListenerEndpointRegistry registry)
    {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
        endpoint = new TestJmsListenerEndpoint(destinationName, this);
        registry.registerListenerContainer(endpoint, factory);
    }

    public void start()
    {
        this.endpoint.startMessageListener();
    }

    protected List<Object> captureResults = new ArrayList<>();

    public List<Object> getCaptureResults()
    {
        return captureResults;
    }

    @Override
    public void onMessage(Message message)
    {
        captureResults.add(message);
    }

    class TestJmsListenerEndpoint implements JmsListenerEndpoint
    {
        private String destinationName;

        private MessageListener messageListener;

        MessageListenerContainer listenerContainer;

        TestJmsListenerEndpoint(String destinationName, MessageListener messageListener)
        {
            this.destinationName = destinationName;
            this.messageListener = messageListener;
        }

        @Override
        public String getId()
        {
            return destinationName;
        }

        @Override
        public void setupListenerContainer(MessageListenerContainer listenerContainer)
        {
            listenerContainer.setupMessageListener(messageListener);
            ((SimpleMessageListenerContainer) listenerContainer).setDestinationName(destinationName);
            ((SimpleMessageListenerContainer) listenerContainer).setAutoStartup(false);
            this.listenerContainer = listenerContainer;
        }

        void startMessageListener()
        {
            this.listenerContainer.start();
        }
    }
}

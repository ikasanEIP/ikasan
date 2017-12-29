package org.ikasan.testharness.flow.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

public class MessageListenerVerifier implements MessageListener
{
    private String destinationName;

    TestJmsListenerEndpoint endpoint;

    public MessageListenerVerifier(final String brokerUrl, final String destinationName,
        final JmsListenerEndpointRegistry registry)
    {
        this.destinationName = destinationName;
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
        endpoint = new TestJmsListenerEndpoint(destinationName, this);
        registry.registerListenerContainer(endpoint, factory);
    }

    public void start()
    {
        this.endpoint.startMessageListener();
    }

    List<Object> captureResults = new ArrayList<>();

    public List<Object> getCaptureResults()
    {
        return captureResults;
    }

    @Override public void onMessage(Message message)
    {
        if (message instanceof TextMessage)
        {
            try
            {
                String msg = ((TextMessage) message).getText();
                System.out.println("Message has been consumed from [" + destinationName + "]: " + msg);
                captureResults.add(msg);
            }
            catch (JMSException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        if (message instanceof MapMessage)
        {
                System.out.println("Message has been consumed from [" + destinationName + "]: " + message.toString());
                captureResults.add(message);

        }
        else
        {
            throw new IllegalArgumentException("Message Error");
        }
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

        @Override public String getId()
        {
            return destinationName;
        }

        @Override public void setupListenerContainer(MessageListenerContainer listenerContainer)
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

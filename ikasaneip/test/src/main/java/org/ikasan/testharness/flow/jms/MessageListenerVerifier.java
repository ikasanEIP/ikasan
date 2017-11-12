package org.ikasan.testharness.flow.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

public class MessageListenerVerifier implements MessageListener
{
    private String destinationName;

    public MessageListenerVerifier(final String brokerUrl, final String destinationName,
                                   final JmsListenerEndpointRegistry registry)
    {
        this.destinationName = destinationName;
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
        JmsListenerEndpoint endpoint = new TestJmsListenerEndpoint(destinationName, this);
        registry.registerListenerContainer(endpoint, factory);
    }

    List<String> captureResults = new ArrayList<>();

    public List<String> getCaptureResults()
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
        else
        {
            throw new IllegalArgumentException("Message Error");
        }
    }

    class TestJmsListenerEndpoint implements JmsListenerEndpoint
    {
        private String destinationName;

        private MessageListener messageListener;

        public TestJmsListenerEndpoint(String destinationName, MessageListener messageListener)
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
        }
    }
}

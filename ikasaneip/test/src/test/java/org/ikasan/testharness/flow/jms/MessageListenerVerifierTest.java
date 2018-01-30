package org.ikasan.testharness.flow.jms;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import javax.jms.*;

/**
 * Test cases for MessageListenerVerifier
 */
public class MessageListenerVerifierTest
{
    private MessageListenerVerifier verifier = new MessageListenerVerifier("embedded-broker", "dest", new JmsListenerEndpointRegistry());

    @Test
    public void test_text_message() throws JMSException
    {
        TextMessage message = new ActiveMQTextMessage();
        message.setText("test");
        verifier.onMessage(message);
        Assert.assertEquals(1, verifier.getCaptureResults().size());
        Assert.assertEquals(message, verifier.getCaptureResults().get(0));
    }

    @Test
    public void test_bytes_message() throws JMSException
    {
        BytesMessage message = new ActiveMQBytesMessage();
        message.writeBytes("test".getBytes());
        verifier.onMessage(message);
        Assert.assertEquals(1, verifier.getCaptureResults().size());
        Assert.assertEquals(message, verifier.getCaptureResults().get(0));
    }

    @Test
    public void test_map_message() throws JMSException
    {
        MapMessage message = new ActiveMQMapMessage();
        message.setStringProperty("test", "foo");
        verifier.onMessage(message);
        Assert.assertEquals(1, verifier.getCaptureResults().size());
        Assert.assertEquals(message, verifier.getCaptureResults().get(0));
    }

    @Test
    public void test_object_message() throws JMSException
    {
        ObjectMessage message = new ActiveMQObjectMessage();
        message.setObject("test");
        verifier.onMessage(message);
        Assert.assertEquals(1, verifier.getCaptureResults().size());
        Assert.assertEquals(message, verifier.getCaptureResults().get(0));
    }
}

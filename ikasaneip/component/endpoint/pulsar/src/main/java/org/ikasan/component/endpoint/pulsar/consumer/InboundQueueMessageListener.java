package org.ikasan.component.endpoint.pulsar.consumer;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.event.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundQueueMessageListener implements org.apache.pulsar.client.api.MessageListener<byte[]> {
    private static Logger logger = LoggerFactory.getLogger(InboundQueueMessageListener.class);

    private MessageListener messageListener;
    private EndpointListener endpointListener;
    private Message<byte[]> currentMessage;

    /**
     * Sets the MessageListener instance to handle incoming messages.
     *
     * @param messageListener the MessageListener implementation to process messages.
     */
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Sets the EndpointListener instance to handle endpoint events such as
     * message receipt and exception notifications.
     *
     * @param endpointListener the EndpointListener implementation that processes
     *                         endpoint events and exceptions.
     */
    public void setEndpointListener(EndpointListener endpointListener) {
        this.endpointListener = endpointListener;
    }

    @Override
    public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
        try {
            this.currentMessage = msg;
            byte[] payload = msg.getValue();
            this.messageListener.onMessage(payload);
        } catch (Exception e) {
            logger.error("Error processing Pulsar message", e);
            this.endpointListener.onException(e);
        }
    }

    /**
     * Retrieves the current message being processed by the instance.
     *
     * @return the current Pulsar message as a Message<byte[]> object, or null if no message is currently set.
     */
    public Message<byte[]> getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Resets the current message being processed by this listener instance.
     * This method clears the reference to the current message, setting it to null.
     * It is typically used to mark the end of processing for the current message.
     */
    public void reset() {
        this.currentMessage = null;
    }
}

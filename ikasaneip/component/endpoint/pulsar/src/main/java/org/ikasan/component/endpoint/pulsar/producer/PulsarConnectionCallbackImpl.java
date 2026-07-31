package org.ikasan.component.endpoint.pulsar.producer;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.ikasan.spec.flow.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of PulsarConnectionCallback that sends messages to Pulsar.
 *
 * @author Ikasan Development Team
 */
public class PulsarConnectionCallbackImpl<IDENTIFIER, PAYLOAD> implements PulsarConnectionCallback {

    private static Logger logger = LoggerFactory.getLogger(PulsarConnectionCallbackImpl.class);

    private FlowEvent<IDENTIFIER, PAYLOAD> flowEvent;
    private Producer<byte[]> producer;

    /**
     * Constructor
     *
     * @param flowEvent the flow event containing the payload to be published to Pulsar
     * @param producer the Pulsar producer used to send messages
     */
    public PulsarConnectionCallbackImpl(FlowEvent<IDENTIFIER, PAYLOAD> flowEvent, Producer<byte[]> producer) {
        this.flowEvent = flowEvent;
        if (this.flowEvent == null) {
            throw new IllegalArgumentException("flowEvent cannot be null!");
        }
        this.producer = producer;
        if (this.producer == null) {
            throw new IllegalArgumentException("producer cannot be null!");
        }
    }

    @Override
    public void execute() throws PulsarClientException {
        PAYLOAD payload = flowEvent.getPayload();

        if (payload == null) {
            logger.warn("Flow event payload is null, skipping message send");
            return;
        }

        // Convert payload to byte array
        byte[] messageBytes;
        if (payload instanceof byte[]) {
            messageBytes = (byte[]) payload;
        } else if (payload instanceof String) {
            messageBytes = ((String) payload).getBytes();
        } else {
            // For other types, use toString() and convert to bytes
            messageBytes = payload.toString().getBytes();
        }

        logger.debug("Sending message to Pulsar, size: {} bytes", messageBytes.length);

        // Send message synchronously
        producer.send(messageBytes);

        logger.debug("Message sent successfully to Pulsar");
    }
}

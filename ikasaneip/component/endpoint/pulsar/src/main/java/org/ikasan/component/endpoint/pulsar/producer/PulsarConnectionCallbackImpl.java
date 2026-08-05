package org.ikasan.component.endpoint.pulsar.producer;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.ikasan.spec.flow.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of PulsarConnectionCallback that sends messages to Pulsar.
 * Supports both BYTES schema (with byte[] conversion) and typed schemas (direct payload send).
 *
 * @author Ikasan Development Team
 */
public class PulsarConnectionCallbackImpl<IDENTIFIER, PAYLOAD> implements PulsarConnectionCallback {

    private static Logger logger = LoggerFactory.getLogger(PulsarConnectionCallbackImpl.class);

    private FlowEvent<IDENTIFIER, PAYLOAD> flowEvent;
    private Producer<?> producer;
    private boolean isBytesSchema;

    /**
     * Constructor for BYTES schema (legacy compatibility)
     *
     * @param flowEvent the flow event containing the payload to be published to Pulsar
     * @param producer the Pulsar producer used to send messages
     */
    public PulsarConnectionCallbackImpl(FlowEvent<IDENTIFIER, PAYLOAD> flowEvent, Producer<byte[]> producer) {
        this(flowEvent, (Producer<?>) producer, true);
    }

    /**
     * Constructor with schema type awareness
     *
     * @param flowEvent the flow event containing the payload to be published to Pulsar
     * @param producer the Pulsar producer used to send messages
     * @param isBytesSchema true if using BYTES schema, false for typed schemas
     */
    public PulsarConnectionCallbackImpl(FlowEvent<IDENTIFIER, PAYLOAD> flowEvent, Producer<?> producer, boolean isBytesSchema) {
        this.flowEvent = flowEvent;
        if (this.flowEvent == null) {
            throw new IllegalArgumentException("flowEvent cannot be null!");
        }
        this.producer = producer;
        if (this.producer == null) {
            throw new IllegalArgumentException("producer cannot be null!");
        }
        this.isBytesSchema = isBytesSchema;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws PulsarClientException {
        PAYLOAD payload = flowEvent.getPayload();

        if (payload == null) {
            logger.warn("Flow event payload is null, skipping message send");
            return;
        }

        if (isBytesSchema) {
            // BYTES schema - convert payload to byte array
            byte[] messageBytes = convertToBytes(payload);
            logger.debug("Sending message to Pulsar with BYTES schema, size: {} bytes", messageBytes.length);
            ((Producer<byte[]>) producer).send(messageBytes);
        } else {
            // Typed schema - send payload directly, let schema handle serialization
            logger.debug("Sending message to Pulsar with typed schema, payload type: {}", payload.getClass().getName());
            ((Producer<Object>) producer).send(payload);
        }

        logger.debug("Message sent successfully to Pulsar");
    }

    /**
     * Convert payload to byte array for BYTES schema.
     *
     * @param payload the payload to convert
     * @return byte array representation of the payload
     */
    private byte[] convertToBytes(PAYLOAD payload) {
        if (payload instanceof byte[]) {
            return (byte[]) payload;
        } else if (payload instanceof String) {
            return ((String) payload).getBytes();
        } else {
            // For other types, use toString() and convert to bytes
            return payload.toString().getBytes();
        }
    }
}

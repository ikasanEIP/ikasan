package org.ikasan.component.endpoint.bigqueue.serialiser;

import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;
import tools.jackson.databind.json.JsonMapper;

/**
 * Serializer implementation to serialise BigQueueMessages
 *
 * This convert messages to json bytes and back from json bytes.
 *
 * @author Ikasan Development Team
 */

public class BigQueueMessageJsonSerialiser<T> implements Serialiser<BigQueueMessage<T>, byte[]>  {
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    @Override
    public byte[] serialise(BigQueueMessage<T> source) {
        return JSON_MAPPER.writeValueAsBytes(source);
    }

    /**
     * This deserialise will always return the message payload as a json string.
     * It is up to the calling classes to determine the type of the message at run time.
     */
    @Override
    public BigQueueMessage<T> deserialise(byte[] source) {
        BigQueueMessage bigQueueMessage = JSON_MAPPER.readValue(source, BigQueueMessageImpl.class);
        bigQueueMessage.setMessage(new String(JSON_MAPPER.writeValueAsBytes(bigQueueMessage.getMessage())));
        return bigQueueMessage;

    }
}

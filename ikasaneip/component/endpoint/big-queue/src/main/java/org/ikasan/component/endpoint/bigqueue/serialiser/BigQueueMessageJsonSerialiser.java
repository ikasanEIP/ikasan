package org.ikasan.component.endpoint.bigqueue.serialiser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;

import java.io.IOException;

/**
 * Serializer implementation to serialise BigQueueMessages
 *
 * This convert messages to json bytes and back from json bytes.
 *
 * @author Ikasan Development Team
 */

public class BigQueueMessageJsonSerialiser<T> implements Serialiser<BigQueueMessage<T>, byte[]>  {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialise(BigQueueMessage<T> source) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(source);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This deserialise will always return the message payload as a json string.
     * It is up to the calling classes to determine the type of the message at run time.
     */
    @Override
    public BigQueueMessage<T> deserialise(byte[] source) {
        try {
            BigQueueMessage bigQueueMessage = OBJECT_MAPPER.readValue(source, BigQueueMessageImpl.class);
            bigQueueMessage.setMessage(new String(OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage.getMessage())));
            return bigQueueMessage;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

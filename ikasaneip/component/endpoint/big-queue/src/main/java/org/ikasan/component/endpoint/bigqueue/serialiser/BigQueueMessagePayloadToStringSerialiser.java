package org.ikasan.component.endpoint.bigqueue.serialiser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigQueueMessagePayloadToStringSerialiser implements Serialiser<String, byte[]> {
    private static final Logger LOG = LoggerFactory.getLogger(BigQueueMessagePayloadToStringSerialiser.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialise(String source) {
        return source.getBytes();
    }

    @Override
    public String deserialise(byte[] source) {
        String messageAsString;

        try {
            BigQueueMessage bigQueueMessage = OBJECT_MAPPER.readValue(new String(source), BigQueueMessageImpl.class);
            messageAsString = OBJECT_MAPPER.writeValueAsString(bigQueueMessage.getMessage());
        } catch (JsonProcessingException e) {
            LOG.warn(String.format("Could not deserialise big queue message [%s] error [%s]", new String(source), e.getMessage()));
            throw new RuntimeException(e);
        }

        return messageAsString;
    }
}

package org.ikasan.component.endpoint.bigqueue.serialiser;

import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class BigQueueMessagePayloadToStringSerialiser implements Serialiser<String, byte[]> {
    private static final Logger LOG = LoggerFactory.getLogger(BigQueueMessagePayloadToStringSerialiser.class);
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    @Override
    public byte[] serialise(String source) {
        return source.getBytes();
    }

    @Override
    public String deserialise(byte[] source) {
        String messageAsString;

        try {
            BigQueueMessage bigQueueMessage = JSON_MAPPER.readValue(new String(source), BigQueueMessageImpl.class);
            messageAsString = JSON_MAPPER.writeValueAsString(bigQueueMessage.getMessage());
        } catch (JacksonException e) {
            LOG.warn("Could not deserialise big queue message [%s] error [%s]".formatted(new String(source), e.getMessage()));
            throw e;
        }

        return messageAsString;
    }
}

package org.ikasan.ootb.data.sharing.module.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.dashboard.dto.replay.ReplayEventImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Serializer implementation to serialise replay events to BigQueueMessage
 *
 * @author Ikasan Development Team
 */
public class ReplayEventToBigQueueMessageSerialiser implements Serialiser<List<ReplayEvent>, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReplayEventToBigQueueMessageSerialiser.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public synchronized byte[] serialise(List<ReplayEvent> replayEvents) {
        try {
            BigQueueMessage message = new BigQueueMessageBuilder()
                .withMessage(replayEvents)
                .build();

            byte[] bytes = objectMapper.writeValueAsBytes(message);
            LOGGER.debug("Serialised - " + new String(bytes));
            return bytes;
        } catch (Exception e) {
            LOGGER.warn(String.format("Got exception serialising replay events[%s]", replayEvents), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public synchronized List<ReplayEvent> deserialise(byte[] source) {
        try {
            LOGGER.debug("De-serialising - " + new String(source));
            BigQueueMessage bigQueueMessage = objectMapper.readValue(source, BigQueueMessageImpl.class);
            LOGGER.debug("ReplayEvent big queue message! " + bigQueueMessage.getMessage());
            return objectMapper.convertValue(bigQueueMessage.getMessage(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ReplayEventImpl.class));
        } catch (Exception e) {
            LOGGER.warn(String.format("Exception de-serialising[%s] ", new String(source)), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

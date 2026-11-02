package org.ikasan.ootb.data.sharing.module.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.dashboard.dto.exclusion.ExclusionEventImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Serializer implementation to serialise exclusion events to BigQueueMessage
 *
 * @author Ikasan Development Team
 */
public class ExclusionEventToBigQueueMessageSerialiser implements Serialiser<List<ExclusionEvent<String>>, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExclusionEventToBigQueueMessageSerialiser.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public synchronized byte[] serialise(List<ExclusionEvent<String>> exclusionEvents) {
        try {
            BigQueueMessage message = new BigQueueMessageBuilder()
                .withMessage(exclusionEvents)
                .build();

            byte[] bytes = objectMapper.writeValueAsBytes(message);
            LOGGER.debug("Serialised - " + new String(bytes));
            return bytes;
        } catch (Exception e) {
            LOGGER.warn(String.format("Got exception serialising exclusion events[%s]", exclusionEvents), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public synchronized List<ExclusionEvent<String>> deserialise(byte[] source) {
        try {
            LOGGER.debug("De-serialising - " + new String(source));
            BigQueueMessage bigQueueMessage = objectMapper.readValue(source, BigQueueMessageImpl.class);
            LOGGER.debug("ExclusionEvent big queue message! " + bigQueueMessage.getMessage());
            return objectMapper.convertValue(bigQueueMessage.getMessage(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ExclusionEventImpl.class));
        } catch (Exception e) {
            LOGGER.warn(String.format("Exception de-serialising[%s] ", new String(source)), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

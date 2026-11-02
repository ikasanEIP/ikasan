package org.ikasan.ootb.data.sharing.module.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.dashboard.dto.error.ErrorOccurrenceImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Serializer implementation to serialise ContextualisedScheduledProcessEvents to BigQueueMessage
 *
 * @author Ikasan Development Team
 */
public class ErrorOccurrenceToBigQueueMessageSerialiser implements Serialiser<List<ErrorOccurrence<byte[]>>, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorOccurrenceToBigQueueMessageSerialiser.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public synchronized byte[] serialise(List<ErrorOccurrence<byte[]>> errorOccurrences) {
        try {
            BigQueueMessage message = new BigQueueMessageBuilder()
                .withMessage(errorOccurrences)
                .build();

            byte[] bytes = objectMapper.writeValueAsBytes(message);
            LOGGER.debug("Serialised - " + new String(bytes));
            return bytes;
        } catch (Exception e) {
            LOGGER.warn(String.format("Got exception serialising file watcher job errorOccurrences[%s]", errorOccurrences), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public synchronized List<ErrorOccurrence<byte[]>> deserialise(byte[] source) {
        try {
            LOGGER.debug("De-serialising - " + new String(source));
            BigQueueMessage bigQueueMessage = objectMapper.readValue(source, BigQueueMessageImpl.class);
            LOGGER.debug("ErrorOccurrence big queue message! " + bigQueueMessage.getMessage());
            return objectMapper.convertValue(bigQueueMessage.getMessage(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ErrorOccurrenceImpl.class));
        } catch (Exception e) {
            LOGGER.warn(String.format("Exception de-serialising[%s] ", new String(source)), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

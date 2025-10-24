package org.ikasan.ootb.scheduler.agent.module.component.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Serializer implementation to serialise ContextualisedScheduledProcessEvents to BigQueueMessage
 *
 * @author Ikasan Development Team
 */
public class FileWatcherJobEventToBigQueueMessageSerialiser implements Serialiser<FileWatcherJobEvent, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherJobEventToBigQueueMessageSerialiser.class);

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public synchronized byte[] serialise(FileWatcherJobEvent event) {
        try {
            Map<String, String> properties = new HashMap<>();
            if (event.getContextName() != null && !event.getContextName().isEmpty()) {
                properties.put("contextName", event.getContextName());
            }
            if (event.getCorrelationIdentifier() != null && !event.getCorrelationIdentifier().isEmpty()) {
                properties.put("contextInstanceId", event.getCorrelationIdentifier());
            }

            BigQueueMessage message = new BigQueueMessageBuilder()
                .withMessage(event)
                .withMessageProperties(properties)
                .build();

            byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(message);
            LOGGER.debug("Serialised - " + new String(bytes));
            return bytes;
        } catch (Exception e) {
            LOGGER.warn(String.format("Got exception serialising file watcher job event[%s]", event), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public synchronized FileWatcherJobEvent deserialise(byte[] source) {
        try {
            LOGGER.debug("De-serialising - " + new String(source));
            BigQueueMessage bigQueueMessage = OBJECT_MAPPER.readValue(source, BigQueueMessageImpl.class);
            LOGGER.debug("File watcher big queue message! " + bigQueueMessage.getMessage());
            byte [] bytes = OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage.getMessage());
            return OBJECT_MAPPER.readValue(new String(bytes), FileWatcherJobEvent.class);
        } catch (Exception e) {
            LOGGER.warn(String.format("Exception de-serialising[%s] ", new String(source)), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

package org.ikasan.ootb.data.sharing.module.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.dashboard.dto.wiretap.WiretapEventImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Serializer implementation to serialise ContextualisedScheduledProcessEvents to BigQueueMessage
 *
 * @author Ikasan Development Team
 */
public class WiretapEventsToBigQueueMessageSerialiser implements Serialiser<List<WiretapEvent>, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiretapEventsToBigQueueMessageSerialiser.class);

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public synchronized byte[] serialise(List<WiretapEvent> event) {
        try {
            BigQueueMessage message = new BigQueueMessageBuilder()
                .withMessage(event)
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
    public synchronized List<WiretapEvent> deserialise(byte[] source) {
        try {
            LOGGER.debug("De-serialising - " + new String(source));
            BigQueueMessage bigQueueMessage = OBJECT_MAPPER.readValue(source, BigQueueMessageImpl.class);
            LOGGER.debug("File watcher big queue message! " + bigQueueMessage.getMessage());
            byte [] bytes = OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage.getMessage());
            return OBJECT_MAPPER.convertValue(bigQueueMessage.getMessage(),
                OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, WiretapEventImpl.class));
        } catch (Exception e) {
            LOGGER.warn(String.format("Exception de-serialising[%s] ", new String(source)), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

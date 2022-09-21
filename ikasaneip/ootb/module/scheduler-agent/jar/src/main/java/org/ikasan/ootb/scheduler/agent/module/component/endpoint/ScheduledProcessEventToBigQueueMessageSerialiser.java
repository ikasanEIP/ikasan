package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
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
public class ScheduledProcessEventToBigQueueMessageSerialiser implements Serialiser<ScheduledProcessEvent, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledProcessEventToBigQueueMessageSerialiser.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialise(ScheduledProcessEvent source) {
        try {
            ContextualisedScheduledProcessEvent event = (ContextualisedScheduledProcessEvent) source;
            Map<String, String> properties = new HashMap<>();
            if (event.getContextName() != null) {
                properties.put("contextName", event.getContextName());
            }
            if (event.getContextInstanceId() != null) {
                properties.put("contextInstanceId", event.getContextInstanceId());
            }

            BigQueueMessage message = new BigQueueMessageBuilder()
                .withMessage(event)
                .withMessageProperties(properties)
                .build();

            byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(message);
            return bytes;
        } catch (Exception e) {
            LOGGER.warn("Got exception serialising " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ScheduledProcessEvent deserialise(byte[] source) {
        try {
            BigQueueMessage bigQueueMessage = OBJECT_MAPPER.readValue(source, BigQueueMessageImpl.class);
            byte [] bytes = OBJECT_MAPPER.writeValueAsBytes(bigQueueMessage.getMessage());
            return OBJECT_MAPPER.readValue(new String(bytes), ContextualisedScheduledProcessEventImpl.class);
        } catch (Exception e) {
            LOGGER.warn("Got exception deserialising " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}

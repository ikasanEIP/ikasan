package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.serialiser.Serialiser;

import java.io.IOException;

/**
 * Serializer implementation to serialise ScheduledProcessEvents
 *
 * @author Ikasan Development Team
 */
public class SchedulerProcessorEventSerialiser implements Serialiser<ScheduledProcessEvent, byte[]> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialise(ScheduledProcessEvent source) {
        try {
            return this.objectMapper.writeValueAsBytes(source);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ScheduledProcessEvent deserialise(byte[] source) {
        try {
            return objectMapper.readValue(source, ScheduledProcessEvent.class);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

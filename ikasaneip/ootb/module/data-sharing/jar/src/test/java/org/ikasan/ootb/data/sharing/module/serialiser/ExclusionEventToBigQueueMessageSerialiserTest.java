package org.ikasan.ootb.data.sharing.module.serialiser;

import org.ikasan.dashboard.dto.exclusion.ExclusionEventImpl;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

public class ExclusionEventToBigQueueMessageSerialiserTest {
    private final ExclusionEventToBigQueueMessageSerialiser serialiser =
        new ExclusionEventToBigQueueMessageSerialiser();

    @Test
    public void preservesExclusionFieldsAndPayload() {
        ExclusionEventImpl event = new ExclusionEventImpl();
        event.setId("exclusion-1");
        event.setModuleName("module-1");
        event.setFlowName("flow-1");
        event.setIdentifier("event-1");
        event.setErrorUri("error-1");
        event.setTimestamp(123456789L);
        event.setHarvested(true);
        event.setEvent("Excluded payload: £ € \n\"quoted\"".getBytes(StandardCharsets.UTF_8));

        List<ExclusionEvent<String>> restored = serialiser.deserialise(serialiser.serialise(List.of(event)));

        assertEquals(1, restored.size());
        ExclusionEvent<String> actual = restored.get(0);
        assertEquals(event.getId(), actual.getId());
        assertEquals(event.getModuleName(), actual.getModuleName());
        assertEquals(event.getFlowName(), actual.getFlowName());
        assertEquals(event.getIdentifier(), actual.getIdentifier());
        assertEquals(event.getErrorUri(), actual.getErrorUri());
        assertEquals(event.getTimestamp(), actual.getTimestamp());
        assertEquals(event.isHarvested(), actual.isHarvested());
        assertArrayEquals(event.getEvent(), actual.getEvent());
    }

    @Test
    public void preservesEmptyCheckpointBatch() {
        assertTrue(serialiser.deserialise(serialiser.serialise(List.of())).isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void rejectsInvalidQueueMessage() {
        serialiser.deserialise("invalid json".getBytes(StandardCharsets.UTF_8));
    }
}

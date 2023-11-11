package org.ikasan.component.endpoint.bigqueue.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigQueueMessagePayloadToStringSerialiserTest {

    @Test
    void should_serialise_and_deserialise() throws Exception {
        String messageId = UUID.randomUUID().toString();
        long createdTime = System.currentTimeMillis();
        TestEvent testEvent = createTestEvent();

        BigQueueMessage bigQueueMessage
            = new BigQueueMessageBuilder<>()
            .withMessageId(messageId)
            .withCreatedTime(createdTime)
            .withMessage(testEvent)
            .withMessageProperties(Map.of("property1", "value1", "property2", "value2"))
            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(bigQueueMessage);

        BigQueueMessagePayloadToStringSerialiser serialiser = new BigQueueMessagePayloadToStringSerialiser();
        byte[] serialised = serialiser.serialise(message);
        assertNotNull(serialised);
        assertEquals(new String(serialised), message);

        String deserialisedMessage = serialiser.deserialise(serialised);
        assertNotNull(deserialisedMessage);
        String expected = objectMapper.writeValueAsString(testEvent);
        assertEquals(expected, deserialisedMessage);
    }

    public TestEvent createTestEvent() {
        TestEvent testEvent = new TestEvent();
        testEvent.setSomeValue1("value1");
        testEvent.setSomeValue2("value2");
        List<TestParam> testParams = List.of(new TestParam("paramName1", 11), new TestParam("paramName1", 12));
        testEvent.setParams(testParams);
        return testEvent;
    }
}


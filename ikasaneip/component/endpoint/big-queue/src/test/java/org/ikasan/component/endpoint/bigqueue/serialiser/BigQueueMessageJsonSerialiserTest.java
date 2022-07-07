package org.ikasan.component.endpoint.bigqueue.serialiser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BigQueueMessageJsonSerialiserTest {

    @Test
    public void should_serialise_and_deserialise() throws JsonProcessingException {
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
        BigQueueMessageJsonSerialiser<TestEvent> serialiser = new BigQueueMessageJsonSerialiser<>();
        byte[] serialised = serialiser.serialise(bigQueueMessage);

        BigQueueMessage<TestEvent> deserialisedMessage = serialiser.deserialise(serialised);
        assertEquals(messageId, deserialisedMessage.getMessageId());
        assertEquals(createdTime, deserialisedMessage.getCreatedTime());

        Map<String, String> messageProperties = deserialisedMessage.getMessageProperties();
        assertEquals(2, messageProperties.size());
        assertEquals("value1", messageProperties.get("property1"));
        assertEquals("value2", messageProperties.get("property2"));

        String message = (String) (Object) deserialisedMessage.getMessage();
        assertNotNull(message);
        ObjectMapper mapper = new ObjectMapper();
        TestEvent actual = mapper.readValue(message, TestEvent.class);
        assertNotNull(actual);

        assertEquals(testEvent, actual);
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
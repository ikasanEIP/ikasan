package org.ikasan.component.endpoint.bigqueue.producer;

import org.ikasan.bigqueue.BigQueueImpl;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class BigQueueProducerTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_big_queue_constructor() {
        new BigQueueProducer(null);
    }

    @Test
    public void test_producer_invoke_success() throws IOException {
        String messageId = UUID.randomUUID().toString();
        long createdTime = System.currentTimeMillis();
        BigQueueMessage bigQueueMessage
            = new BigQueueMessageBuilder<>()
            .withMessageId(messageId)
            .withCreatedTime(createdTime)
            .withMessage("test message")
            .withMessageProperties(Map.of("property1", "value1", "property2", "value2"))
            .build();

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueProducer producer = new BigQueueProducer(bigQueue);

        producer.invoke(bigQueueMessage);

        byte[] dequeue = bigQueue.dequeue();

        BigQueueMessageJsonSerialiser serialiser = new BigQueueMessageJsonSerialiser();
        BigQueueMessage deserialisedBigQueueMessage = serialiser.deserialise(dequeue);

        assertEquals(messageId, deserialisedBigQueueMessage.getMessageId());
        assertEquals(createdTime, deserialisedBigQueueMessage.getCreatedTime());
        assertEquals("\"test message\"", deserialisedBigQueueMessage.getMessage());
    }
}

package org.ikasan.component.endpoint.bigqueue.producer;

import org.ikasan.bigqueue.BigQueueImpl;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.flow.FlowEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.xa.XAException;
import jakarta.transaction.xa.Xid;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BigQueueProducerTest {

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private Transaction transaction;

    @Mock
    private Xid xid;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_exception_null_big_queue_constructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BigQueueProducerLRCO(null, null);
        });
    }

    @Test
    void test_exception_null_transaction_manager_constructor() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            new BigQueueProducerLRCO(new BigQueueImpl("./target", "test"), null);
        });
    }

    @Test
    void test_producer_invoke_success() throws IOException, SystemException, XAException {
        when(transactionManager.getTransaction()).thenReturn(transaction);
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

        BigQueueProducerLRCO producer = new BigQueueProducerLRCO(bigQueue, this.transactionManager);

        FlowEvent flowEvent = new FlowEvent() {
            @Override
            public Object getIdentifier() {
                return null;
            }

            @Override
            public Object getRelatedIdentifier() {
                return null;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }

            @Override
            public Object getPayload() {
                return bigQueueMessage;
            }

            @Override
            public void setPayload(Object o) {

            }

            @Override
            public void replace(FlowEvent flowEvent) {

            }
        };

        producer.invoke(flowEvent);

        BigQueueConnection connection = (BigQueueConnection)ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        byte[] dequeue = bigQueue.dequeue();

        BigQueueMessageJsonSerialiser serialiser = new BigQueueMessageJsonSerialiser();
        BigQueueMessage deserialisedBigQueueMessage = serialiser.deserialise(dequeue);

        assertEquals(messageId, deserialisedBigQueueMessage.getMessageId());
        assertEquals(createdTime, deserialisedBigQueueMessage.getCreatedTime());
        assertEquals("\"test message\"", deserialisedBigQueueMessage.getMessage());
    }
}

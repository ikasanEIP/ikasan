package org.ikasan.builder.component.endpoint;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.producer.BigQueueProducerLRCO;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.transaction.TransactionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BigQueueProducerBuilderTest {

    @Mock
    TransactionManager transactionManager;

    @Mock
    IBigQueue outboundQueue;

    @Mock
    Serialiser<BigQueueMessage, byte[]> serialiser;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_build_success() {
        BigQueueProducerLRCOBuilder builder = new BigQueueProducerLRCOBuilderImpl(this.transactionManager);

        BigQueueProducerLRCO producer = (BigQueueProducerLRCO) builder.setOutboundQueue(this.outboundQueue)
            .setSerialiser(serialiser)
            .build();

        assertEquals(this.outboundQueue, ReflectionTestUtils.getField(producer, "outboundQueue"));
        assertEquals(this.serialiser, ReflectionTestUtils.getField(producer, "serialiser"));
        }


    @Test
    void test_exception_null_transaction_manager() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BigQueueProducerLRCOBuilderImpl(null);
        });
    }
}

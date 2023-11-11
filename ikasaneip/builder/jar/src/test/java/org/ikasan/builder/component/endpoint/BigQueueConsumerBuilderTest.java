package org.ikasan.builder.component.endpoint;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.bigqueue.consumer.BigQueueConsumer;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.serialiser.Serialiser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.transaction.TransactionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BigQueueConsumerBuilderTest {

    @Mock
    TransactionManager transactionManager;

    @Mock
    AopProxyProvider aopProxyProvider;

    @Mock
    IBigQueue inboundQueue;

    @Mock
    EventFactory eventFactory;

    @Mock
    ManagedRelatedEventIdentifierService relatedEventIdentifierService;

    @Mock
    EventListener eventListener;

    @Mock
    ResubmissionEventFactory resubmissionEventFactory;

    @Mock
    Serialiser<BigQueueMessage, byte[]> serialiser;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_build_success() {
        BigQueueConsumerBuilder builder = new BigQueueConsumerBuilderImpl(this.aopProxyProvider, this.transactionManager);

        BigQueueConsumer consumer = (BigQueueConsumer) builder.setInboundQueue(this.inboundQueue)
            .setPutErrorsToBackOfQueue(true)
            .setEventFactory(this.eventFactory)
            .setManagedEventIdentifierService(this.relatedEventIdentifierService)
            .setConfigurationId("configurationId")
            .setListener(this.eventListener)
            .setResubmissionEventFactory(this.resubmissionEventFactory)
            .setSerialiser(serialiser)
            .build();

        assertEquals(this.inboundQueue, ReflectionTestUtils.getField(consumer, "inboundQueue"));
        assertEquals(this.eventFactory, ReflectionTestUtils.getField(consumer, "flowEventFactory"));
        assertEquals(this.relatedEventIdentifierService, ReflectionTestUtils.getField(consumer, "managedRelatedEventIdentifierService"));
        assertEquals("configurationId", consumer.getConfiguredResourceId());
        assertEquals(this.eventListener, ReflectionTestUtils.getField(consumer, "eventListener"));
        assertEquals(this.resubmissionEventFactory, ReflectionTestUtils.getField(consumer, "resubmissionEventFactory"));
        assertEquals(this.serialiser, ReflectionTestUtils.getField(consumer, "serialiser"));
    }

    @Test
    void test_exception_null_aop_proxy() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BigQueueConsumerBuilderImpl(null, this.transactionManager);
        });
    }

    @Test
    void test_exception_null_transaction_manager() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BigQueueConsumerBuilderImpl(this.aopProxyProvider, null);
        });
    }
}

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.transaction.TransactionManager;

public class BigQueueConsumerBuilderTest {

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

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_build_success() {
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

        Assert.assertEquals(this.inboundQueue, ReflectionTestUtils.getField(consumer, "inboundQueue"));
        Assert.assertEquals(this.eventFactory, ReflectionTestUtils.getField(consumer, "flowEventFactory"));
        Assert.assertEquals(this.relatedEventIdentifierService, ReflectionTestUtils.getField(consumer, "managedRelatedEventIdentifierService"));
        Assert.assertEquals("configurationId", consumer.getConfiguredResourceId());
        Assert.assertEquals(this.eventListener, ReflectionTestUtils.getField(consumer, "eventListener"));
        Assert.assertEquals(this.resubmissionEventFactory, ReflectionTestUtils.getField(consumer, "resubmissionEventFactory"));
        Assert.assertEquals(this.serialiser, ReflectionTestUtils.getField(consumer, "serialiser"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_aop_proxy() {
        new BigQueueConsumerBuilderImpl(null, this.transactionManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_transaction_manager() {
        new BigQueueConsumerBuilderImpl(this.aopProxyProvider, null);
    }
}

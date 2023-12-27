package org.ikasan.builder.component.endpoint;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.producer.BigQueueProducerLRCO;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.serialiser.Serialiser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.transaction.TransactionManager;

public class BigQueueProducerBuilderTest {

    @Mock
    TransactionManager transactionManager;

    @Mock
    IBigQueue outboundQueue;

    @Mock
    Serialiser<BigQueueMessage, byte[]> serialiser;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_build_success() {
        BigQueueProducerLRCOBuilder builder = new BigQueueProducerLRCOBuilderImpl(this.transactionManager);

        BigQueueProducerLRCO producer = (BigQueueProducerLRCO) builder.setOutboundQueue(this.outboundQueue)
            .setSerialiser(serialiser)
            .build();

        Assert.assertEquals(this.outboundQueue, ReflectionTestUtils.getField(producer, "outboundQueue"));
        Assert.assertEquals(this.serialiser, ReflectionTestUtils.getField(producer, "serialiser"));
        }


    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_transaction_manager() {
        new BigQueueProducerLRCOBuilderImpl(null);
    }
}

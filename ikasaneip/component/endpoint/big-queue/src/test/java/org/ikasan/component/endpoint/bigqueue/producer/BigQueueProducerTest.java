package org.ikasan.component.endpoint.bigqueue.producer;

import com.leansoft.bigqueue.BigQueueImpl;
import org.ikasan.component.endpoint.bigqueue.serialiser.SimpleStringSerialiser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class BigQueueProducerTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_big_queue_constructor() {
        new BigQueueProducer(null, new SimpleStringSerialiser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_seerialiser_queue_constructor() throws IOException {
        new BigQueueProducer(new BigQueueImpl("./target", "test"), null);
    }

    @Test
    public void test_producer_invoke_success() throws IOException {
        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueProducer producer = new BigQueueProducer(bigQueue, new SimpleStringSerialiser());

        producer.invoke("test message");

        Assert.assertEquals("test message", new String(bigQueue.dequeue()));
    }
}

package org.ikasan.component.endpoint.bigqueue.consumer;

import com.leansoft.bigqueue.BigQueueImpl;
import org.ikasan.component.endpoint.bigqueue.serialiser.SimpleStringSerialiser;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.Resubmission;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BigQueueConsumerTest {

    @Mock
    private EventFactory<FlowEvent<?,?>> flowEventFactory;
    @Mock
    private ResubmissionEventFactory<Resubmission<?>> resubmissionEventFactory;
    @Mock
    private EventListener eventListener;
    @Mock
    private FlowEvent flowEvent;
    @Mock
    private Resubmission resubmission;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_message_consumed_successfully() throws IOException, InterruptedException {
        Mockito.doNothing().when(eventListener).invoke(any(FlowEvent.class));
        when(flowEventFactory.newEvent(anyString(), anyString(), anyString())).thenReturn(flowEvent);

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueConsumer consumer = new BigQueueConsumer(bigQueue, new SimpleStringSerialiser(), false);
        consumer.setEventFactory(this.flowEventFactory);
        consumer.setResubmissionEventFactory(this.resubmissionEventFactory);
        consumer.setListener(this.eventListener);

        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        bigQueue.enqueue("test message".getBytes());

        Thread.sleep(1000);

        consumer.stop();
        Assert.assertFalse(consumer.isRunning());
        Assert.assertEquals(0, bigQueue.size());
        bigQueue.close();

        verify(eventListener, times(1)).invoke(any(FlowEvent.class));
    }

    @Test
    public void test_exception_invoke() throws IOException, InterruptedException {
        doThrow(new RuntimeException("test exception")).when(eventListener).invoke(any(FlowEvent.class));
        when(flowEventFactory.newEvent(anyString(), anyString(), anyString())).thenReturn(flowEvent);

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueConsumer consumer = new BigQueueConsumer(bigQueue, new SimpleStringSerialiser(), false);
        consumer.setEventFactory(this.flowEventFactory);
        consumer.setResubmissionEventFactory(this.resubmissionEventFactory);
        consumer.setListener(this.eventListener);

        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        bigQueue.enqueue("test message".getBytes());

        Thread.sleep(1000);

        consumer.stop();
        Assert.assertFalse(consumer.isRunning());

        Assert.assertEquals(1, bigQueue.size());
        bigQueue.close();

        verify(eventListener, times(1)).invoke(any(FlowEvent.class));
    }

    @Test
    public void test_exception_invoke_messagee_moved_to_back_of_queue() throws IOException, InterruptedException {
        doThrow(new RuntimeException("test exception")).when(eventListener).invoke(any(FlowEvent.class));
        when(flowEventFactory.newEvent(anyString(), anyString(), anyString())).thenReturn(flowEvent);

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueConsumer consumer = new BigQueueConsumer(bigQueue, new SimpleStringSerialiser(), true);
        consumer.setEventFactory(this.flowEventFactory);
        consumer.setResubmissionEventFactory(this.resubmissionEventFactory);
        consumer.setListener(this.eventListener);

        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        bigQueue.enqueue("test message 1".getBytes());
        bigQueue.enqueue("test message 2".getBytes());

        Thread.sleep(1000);

        consumer.stop();
        Assert.assertFalse(consumer.isRunning());

        Assert.assertEquals(2, bigQueue.size());

        Assert.assertEquals("test message 2", new String(bigQueue.dequeue()));
        Assert.assertEquals("test message 1", new String(bigQueue.dequeue()));

        bigQueue.close();

        verify(eventListener, times(1)).invoke(any(FlowEvent.class));
    }

    @Test
    public void test_exception_invoke_null_event_listener() throws IOException, InterruptedException {
        doThrow(new RuntimeException("test exception")).when(eventListener).invoke(any(FlowEvent.class));
        when(flowEventFactory.newEvent(anyString(), anyString(), anyString())).thenReturn(flowEvent);

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueConsumer consumer = new BigQueueConsumer(bigQueue, new SimpleStringSerialiser(), false);
        consumer.setEventFactory(this.flowEventFactory);
        consumer.setResubmissionEventFactory(this.resubmissionEventFactory);

        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        bigQueue.enqueue("test message".getBytes());

        Thread.sleep(1000);

        consumer.stop();
        Assert.assertFalse(consumer.isRunning());

        Assert.assertEquals(1, bigQueue.size());
        bigQueue.close();

        verify(eventListener, times(0)).invoke(any(FlowEvent.class));
        verify(eventListener, times(0)).invoke(any(RuntimeException.class));
    }

    @Test
    public void test_message_resubmitted_successfully() throws IOException, InterruptedException {
        Mockito.doNothing().when(eventListener).invoke(any(Resubmission.class));
        when(resubmissionEventFactory.newResubmissionEvent(anyString())).thenReturn(resubmission);

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueConsumer consumer = new BigQueueConsumer(bigQueue, new SimpleStringSerialiser(), false);
        consumer.setEventFactory(this.flowEventFactory);
        consumer.setResubmissionEventFactory(this.resubmissionEventFactory);
        consumer.setListener(this.eventListener);

        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        consumer.onResubmission("test message");


        Thread.sleep(1000);

        consumer.stop();
        Assert.assertFalse(consumer.isRunning());

        Assert.assertEquals(0, bigQueue.size());
        bigQueue.close();

        verify(eventListener, times(1)).invoke(any(Resubmission.class));
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_resubmitted_invoke_exception() throws IOException, InterruptedException {
        doThrow(new RuntimeException("test exception")).when(eventListener).invoke(any(Resubmission.class));
        when(resubmissionEventFactory.newResubmissionEvent(anyString())).thenReturn(resubmission);

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueConsumer consumer = new BigQueueConsumer(bigQueue, new SimpleStringSerialiser(), false);
        consumer.setEventFactory(this.flowEventFactory);
        consumer.setResubmissionEventFactory(this.resubmissionEventFactory);
        consumer.setListener(this.eventListener);

        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        consumer.onResubmission("test message");
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_resubmitted_null_event_listener() throws IOException, InterruptedException {
        doThrow(new RuntimeException("test exception")).when(eventListener).invoke(any(Resubmission.class));
        when(resubmissionEventFactory.newResubmissionEvent(anyString())).thenReturn(resubmission);

        BigQueueImpl bigQueue = new BigQueueImpl("./target", "test");
        bigQueue.removeAll();

        BigQueueConsumer consumer = new BigQueueConsumer(bigQueue, new SimpleStringSerialiser(), false);
        consumer.setEventFactory(this.flowEventFactory);
        consumer.setResubmissionEventFactory(this.resubmissionEventFactory);

        consumer.start();
        Assert.assertTrue(consumer.isRunning());

        consumer.onResubmission("test message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_big_queue_constructor() {
        new BigQueueConsumer(null, new SimpleStringSerialiser(), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_seerialiser_queue_constructor() throws IOException {
        new BigQueueConsumer(new BigQueueImpl("./target", "test"), null, false);
    }
}
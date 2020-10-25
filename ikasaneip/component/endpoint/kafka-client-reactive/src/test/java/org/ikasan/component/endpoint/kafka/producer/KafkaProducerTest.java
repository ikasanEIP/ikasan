package org.ikasan.component.endpoint.kafka.producer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.testharness.flow.kafka.MessageListenerVerifier;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1,
    topics = {
        "test-topic" }  )
public class KafkaProducerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    // mocked Message Consumer
    EventListener eventListener = mockery.mock(EventListener.class);

    EventFactory flowEventFactory = mockery.mock(EventFactory.class);

    private KafkaProducerConfiguration producerConfiguration;

    @Before
    public void setup() {
        this.producerConfiguration = new KafkaProducerConfiguration();
        this.producerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));
        this.producerConfiguration.setAcks("all");
        this.producerConfiguration.setKeySerializer("org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfiguration.setValueSerializer("org.apache.kafka.common.serialization.StringSerializer");
//        this.producerConfiguration.setTransactionalId("TransactionalSend");
    }

    @Test
    @DirtiesContext
    public void test_publish_message_success() throws InterruptedException {
        
        mockery.checking(new Expectations()
        {
//            {
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//            }
        });

        this.producerConfiguration.setClientId("testClient");
        this.producerConfiguration.setTopicName("test-topic");

        KafkaProducer<String> kafkaProducer = new KafkaProducer<>();
        kafkaProducer.setConfiguration(this.producerConfiguration);

        kafkaProducer.startManagedResource();

        kafkaProducer.invoke("test message");

        MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(this.embeddedKafka.getBrokersAsString(),
            "test-topic", StringDeserializer.class, StringDeserializer.class, "testClient");
        messageListenerVerifier.start();

        Thread.sleep(1000);

        kafkaProducer.stopManagedResource();
        mockery.assertIsSatisfied();

        Assert.assertEquals(1, messageListenerVerifier.getCaptureResults().size());

        messageListenerVerifier.stop();
    }

//    @Test
//    @DirtiesContext
//    public void test_consume_message_success_stop_start_with_offset() throws InterruptedException {
//        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
//
//        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
//        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer(), new StringSerializer()).createProducer();
//
//        mockery.checking(new Expectations()
//        {
//            {
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//            }
//        });
//
//        this.producerConfiguration.setGroupId("testGroup");
//        this.producerConfiguration.setTopicName("test-topic");
//        this.producerConfiguration.setOffset(0L);
//
//        KafkaConsumer<Integer, String> kafkaConsumer = new KafkaConsumer<>();
//        kafkaConsumer.setConfiguration(this.producerConfiguration);
//        kafkaConsumer.setListener(this.eventListener);
//        kafkaConsumer.setEventFactory(this.flowEventFactory);
//        kafkaConsumer.setMessageProcessor(kafkaConsumer);
//
//
//        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
//        producer.send(new ProducerRecord<>("test-topic", 2, "my-test-value2"));
//        producer.flush();
//
//        BrokerTopicMetrics metrics = this.embeddedKafka.getKafkaServer(0).brokerTopicStats().allTopicsStats();
//
//        Long count = metrics.messagesInRate().count();
//
//        kafkaConsumer.start();
//
//        Thread.sleep(1000);
//
//        kafkaConsumer.stop();
//
//        producer.send(new ProducerRecord<>("test-topic", 3, "my-test-value3"));
//        producer.send(new ProducerRecord<>("test-topic", 4, "my-test-value4"));
//        producer.flush();
//
//        kafkaConsumer.start();
//
//        Thread.sleep(1000);
//
//        kafkaConsumer.stop();
//        mockery.assertIsSatisfied();
//    }
//
//    @Test
//    @DirtiesContext
//    public void test_consume_message_success_reset_offset() throws InterruptedException {
//        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
//
//        mockery.checking(new Expectations()
//        {
//            {
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//            }
//        });
//
//        this.producerConfiguration.setGroupId("testGroup");
//        this.producerConfiguration.setTopicName("test-topic");
//        this.producerConfiguration.setOffset(0L);
//
//        KafkaConsumer<Integer, String> kafkaConsumer = new KafkaConsumer<>();
//        kafkaConsumer.setConfiguration(this.producerConfiguration);
//        kafkaConsumer.setListener(this.eventListener);
//        kafkaConsumer.setEventFactory(this.flowEventFactory);
//        kafkaConsumer.setMessageProcessor(kafkaConsumer);
//
//        kafkaConsumer.start();
//
//        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
//        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer(), new StringSerializer()).createProducer();
//        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
//        producer.send(new ProducerRecord<>("test-topic", 2, "my-test-value2"));
//        producer.flush();
//
//        Thread.sleep(1000);
//
//        kafkaConsumer.stop();
//
//        this.producerConfiguration.setOffset(0L);
//
//        kafkaConsumer.start();
//
//        producer.send(new ProducerRecord<>("test-topic", 3, "my-test-value3"));
//        producer.send(new ProducerRecord<>("test-topic", 4, "my-test-value4"));
//        producer.flush();
//
//        Thread.sleep(1000);
//
//        kafkaConsumer.stop();
//        mockery.assertIsSatisfied();
//    }
//
//    @Test
//    @DirtiesContext
//    public void test_consume_message_invoke_exception() throws InterruptedException {
//        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
//        final Exception exception = new RuntimeException("test exception");
//
//        mockery.checking(new Expectations()
//        {
//            {
//                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(mockFlowEvent));
//                exactly(1).of(eventListener).invoke(with(any(Object.class)));
//                will(throwException(exception));
////                exactly(1).of(eventListener).invoke(exception);
//            }
//        });
//
//        this.producerConfiguration.setGroupId("testGroup");
//        this.producerConfiguration.setMaxPollRecords(1);
//        this.producerConfiguration.setTopicName("test-topic");
//
//        KafkaConsumer<Integer, String> kafkaConsumer = new KafkaConsumer<>();
//        kafkaConsumer.setConfiguration(this.producerConfiguration);
//        kafkaConsumer.setListener(this.eventListener);
//        kafkaConsumer.setEventFactory(this.flowEventFactory);
//        kafkaConsumer.setMessageProcessor(kafkaConsumer);
//
//        kafkaConsumer.start();
//
//        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
//        Producer<Integer, String> producer = new DefaultKafkaProducerFactory<>(configs, new IntegerSerializer(), new StringSerializer()).createProducer();
//        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
//        producer.flush();
//
//        Thread.sleep(1000);
//
//        kafkaConsumer.stop();
//        mockery.assertIsSatisfied();
//    }

}

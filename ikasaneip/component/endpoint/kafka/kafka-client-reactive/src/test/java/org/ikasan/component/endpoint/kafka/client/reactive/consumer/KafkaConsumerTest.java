package org.ikasan.component.endpoint.kafka.client.reactive.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ikasan.exceptionResolver.action.ExcludeEventAction;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.flow.FlowEvent;
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
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(partitions = 3,
    topics = {
        "test-topic" }  )
public class KafkaConsumerTest {

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

    final ConfigurationService mockConfigurationService = mockery.mock(ConfigurationService.class);


    private KafkaConsumerConfiguration consumerConfiguration;

    @Before
    public void setup() {
        this.consumerConfiguration = new KafkaConsumerConfiguration();
        this.consumerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));
        this.consumerConfiguration.setAutoCommitIntervalMillis(10);
        this.consumerConfiguration.setSessionTimeoutMillis(60000);
        this.consumerConfiguration.setKeyDeserializer("org.apache.kafka.common.serialization.IntegerDeserializer");
        this.consumerConfiguration.setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfiguration.setEnableAutoCommit(false);
        this.consumerConfiguration.getPartitions().addAll(List.of("0", "1", "2"));
    }

    @Test
    @DirtiesContext
    public void test_consume_message_success() throws InterruptedException {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(flowEventFactory).newEvent(with(any(String.class)), with(aNull(String.class)), with(any(String.class)));
                will(returnValue(mockFlowEvent));
                exactly(2).of(eventListener).invoke(with(any(Object.class)));
                exactly(2).of(mockConfigurationService).update(with(any(KafkaConsumer.class)));
            }
        });

        this.consumerConfiguration.setGroupId("testGroup");
        this.consumerConfiguration.setTopicName("test-topic");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(mockConfigurationService);
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setListener(this.eventListener);
        kafkaConsumer.setEventFactory(this.flowEventFactory);
        kafkaConsumer.setManagedIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl());
        kafkaConsumer.setMessageListener(kafkaConsumer);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new KafkaProducer<>(configs, new IntegerSerializer(), new StringSerializer());
        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
        producer.send(new ProducerRecord<>("test-topic", 2, "my-test-value2"));
        producer.flush();

        kafkaConsumer.start();
        Assert.assertTrue(kafkaConsumer.isRunning());

        await()
            .pollDelay(Duration.ofSeconds(1))
            .timeout(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                assertTrue(true);
            });

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());

        await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> mockery.assertIsSatisfied());
    }

    @Test
    @DirtiesContext
    public void test_consume_message_success_many_message_spread_across_partitions() throws InterruptedException {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        mockery.checking(new Expectations()
        {
            {
                exactly(900).of(flowEventFactory).newEvent(with(any(String.class)), with(aNull(String.class))
                    , with(any(String.class)));
                will(returnValue(mockFlowEvent));
                exactly(900).of(eventListener).invoke(with(any(Object.class)));

                exactly(900).of(mockConfigurationService).update(with(any(KafkaConsumer.class)));
            }
        });

        this.consumerConfiguration.setGroupId("testGroup");
        this.consumerConfiguration.setTopicName("test-topic");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(mockConfigurationService);
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setListener(this.eventListener);
        kafkaConsumer.setEventFactory(this.flowEventFactory);
        kafkaConsumer.setManagedIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl());
        kafkaConsumer.setMessageListener(kafkaConsumer);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new KafkaProducer<>(configs, new IntegerSerializer(), new StringSerializer());

        // 300 messages to partition 1
        IntStream.range(0, 300)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 0, "my-test-value" +i)));
        // 300 messages to partition 2
        IntStream.range(0, 300)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value" +i)));
        // 300 messages to partition 3
        IntStream.range(0, 300)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 2, "my-test-value" +i)));
        producer.flush();

        kafkaConsumer.start();
        Assert.assertTrue(kafkaConsumer.isRunning());

        await()
            .pollDelay(Duration.ofSeconds(1))
            .timeout(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                assertTrue(true);
            });

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());

        await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> mockery.assertIsSatisfied());

        Assert.assertEquals("300", this.consumerConfiguration.getPartitionOffsets().get("0"));
        Assert.assertEquals("300", this.consumerConfiguration.getPartitionOffsets().get("1"));
        Assert.assertEquals("300", this.consumerConfiguration.getPartitionOffsets().get("2"));
    }

    @Test
    @DirtiesContext
    public void test_consume_message_success_many_message_spread_across_partitions_but_only_subscribing_to_one_partition() throws InterruptedException {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        mockery.checking(new Expectations()
        {
            {
                exactly(300).of(flowEventFactory).newEvent(with(any(String.class)), with(aNull(String.class))
                    , with(any(String.class)));
                will(returnValue(mockFlowEvent));
                exactly(300).of(eventListener).invoke(with(any(Object.class)));

                exactly(300).of(mockConfigurationService).update(with(any(KafkaConsumer.class)));
            }
        });

        this.consumerConfiguration.setGroupId("testGroup");
        this.consumerConfiguration.setTopicName("test-topic");
        this.consumerConfiguration.setPartitions(new CopyOnWriteArrayList<>(List.of("1")));

        KafkaConsumer kafkaConsumer = new KafkaConsumer(mockConfigurationService);
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setListener(this.eventListener);
        kafkaConsumer.setEventFactory(this.flowEventFactory);
        kafkaConsumer.setManagedIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl());
        kafkaConsumer.setMessageListener(kafkaConsumer);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new KafkaProducer<>(configs, new IntegerSerializer(), new StringSerializer());

        // 300 messages to partition 1
        IntStream.range(0, 300)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 0, "my-test-value" +i)));
        // 300 messages to partition 2
        IntStream.range(0, 300)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value" +i)));
        // 300 messages to partition 3
        IntStream.range(0, 300)
            .forEach(i -> producer.send(new ProducerRecord<>("test-topic", 2, "my-test-value" +i)));
        producer.flush();

        kafkaConsumer.start();
        Assert.assertTrue(kafkaConsumer.isRunning());

        await()
            .pollDelay(Duration.ofSeconds(1))
            .timeout(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                assertTrue(true);
            });

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());

        await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> mockery.assertIsSatisfied());

        Assert.assertEquals(null, this.consumerConfiguration.getPartitionOffsets().get("0"));
        Assert.assertEquals("300", this.consumerConfiguration.getPartitionOffsets().get("1"));
        Assert.assertEquals(null, this.consumerConfiguration.getPartitionOffsets().get("2"));
    }

    @Test
    @DirtiesContext
    public void test_consume_message_success_stop_start_with_offset() throws InterruptedException {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new KafkaProducer<>(configs, new IntegerSerializer(), new StringSerializer());

        mockery.checking(new Expectations()
        {
            {
                exactly(4).of(flowEventFactory).newEvent(with(any(String.class)), with(aNull(String.class)), with(any(String.class)));
                will(returnValue(mockFlowEvent));
                exactly(4).of(eventListener).invoke(with(any(Object.class)));
                exactly(4).of(mockConfigurationService).update(with(any(KafkaConsumer.class)));
            }
        });

        this.consumerConfiguration.setGroupId("testGroup");
        this.consumerConfiguration.setTopicName("test-topic");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(mockConfigurationService);
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setListener(this.eventListener);
        kafkaConsumer.setEventFactory(this.flowEventFactory);
        kafkaConsumer.setManagedIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl());
        kafkaConsumer.setMessageListener(kafkaConsumer);


        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value2"));
        producer.flush();

        kafkaConsumer.start();
        Assert.assertTrue(kafkaConsumer.isRunning());

        await()
            .pollDelay(Duration.ofSeconds(1))
            .timeout(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                assertTrue(true);
            });

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());

        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value3"));
        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value4"));
        producer.flush();

        kafkaConsumer.start();
        Assert.assertTrue(kafkaConsumer.isRunning());

        await()
            .pollDelay(Duration.ofSeconds(1))
            .timeout(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                assertTrue(true);
            });

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());

        await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> mockery.assertIsSatisfied());
    }

    @Test
    @DirtiesContext
    public void test_consume_message_success_reset_offset() throws InterruptedException {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);

        mockery.checking(new Expectations()
        {
            {
                exactly(6).of(flowEventFactory).newEvent(with(any(String.class)), with(aNull(String.class)), with(any(String.class)));
                will(returnValue(mockFlowEvent));
                exactly(6).of(eventListener).invoke(with(any(Object.class)));
                exactly(6).of(mockConfigurationService).update(with(any(KafkaConsumer.class)));
            }
        });

        this.consumerConfiguration.setGroupId("testGroup");
        this.consumerConfiguration.setTopicName("test-topic");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(mockConfigurationService);
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setListener(this.eventListener);
        kafkaConsumer.setEventFactory(this.flowEventFactory);
        kafkaConsumer.setManagedIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl());
        kafkaConsumer.setMessageListener(kafkaConsumer);

        kafkaConsumer.start();
        Assert.assertTrue(kafkaConsumer.isRunning());

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new KafkaProducer<>(configs, new IntegerSerializer(), new StringSerializer());
        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
        producer.send(new ProducerRecord<>("test-topic", 2, "my-test-value2"));
        producer.flush();

        await()
            .pollDelay(Duration.ofSeconds(1))
            .timeout(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                assertTrue(true);
            });

        kafkaConsumer.stop();

        this.consumerConfiguration.getPartitionOffsets().keySet()
            .forEach(k -> this.consumerConfiguration.getPartitionOffsets().put(k, "0"));

        kafkaConsumer.start();

        producer.send(new ProducerRecord<>("test-topic", 3, "my-test-value3"));
        producer.send(new ProducerRecord<>("test-topic", 3, "my-test-value4"));
        producer.flush();

        await()
            .pollDelay(Duration.ofSeconds(1))
            .timeout(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                assertTrue(true);
            });

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());

        await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> mockery.assertIsSatisfied());
    }

    @Test
    @DirtiesContext
    public void test_consume_message_invoke_exception() throws InterruptedException {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final Exception exception = new RuntimeException("test exception");

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowEventFactory).newEvent(with(any(String.class)), with(aNull(String.class)), with(any(String.class)));
                will(returnValue(mockFlowEvent));
                exactly(1).of(eventListener).invoke(with(any(Object.class)));
                will(throwException(exception));
                exactly(1).of(eventListener).invoke(exception);
            }
        });

        this.consumerConfiguration.setGroupId("testGroup");
        this.consumerConfiguration.setMaxPollRecords(1);
        this.consumerConfiguration.setTopicName("test-topic");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(mockConfigurationService );
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setListener(this.eventListener);
        kafkaConsumer.setEventFactory(this.flowEventFactory);
        kafkaConsumer.setManagedIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl());
        kafkaConsumer.setMessageListener(kafkaConsumer);

        kafkaConsumer.start();

        Assert.assertTrue(kafkaConsumer.isRunning());

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new KafkaProducer<>(configs, new IntegerSerializer(), new StringSerializer());
        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
        producer.flush();

        await().pollDelay(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> mockery.assertIsSatisfied());

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());
    }

    @Test
    @DirtiesContext
    public void test_consume_message_invoke_force_transaction_rollback_exception() throws InterruptedException {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final ForceTransactionRollbackException exception = new ForceTransactionRollbackException(ExcludeEventAction.EXCLUDE_EVENT);

        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(flowEventFactory).newEvent(with(any(String.class)), with(aNull(String.class)), with(any(String.class)));
                will(returnValue(mockFlowEvent));
                exactly(1).of(eventListener).invoke(with(any(Object.class)));
                will(throwException(exception));
                exactly(1).of(eventListener).invoke(with(any(Object.class)));
                exactly(1).of(mockConfigurationService).update(with(any(KafkaConsumer.class)));
            }
        });

        this.consumerConfiguration.setGroupId("testGroup");
        this.consumerConfiguration.setMaxPollRecords(1);
        this.consumerConfiguration.setTopicName("test-topic");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(mockConfigurationService );
        kafkaConsumer.setConfiguration(this.consumerConfiguration);
        kafkaConsumer.setListener(this.eventListener);
        kafkaConsumer.setEventFactory(this.flowEventFactory);
        kafkaConsumer.setManagedIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl());
        kafkaConsumer.setMessageListener(kafkaConsumer);

        kafkaConsumer.start();

        Assert.assertTrue(kafkaConsumer.isRunning());

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        Producer<Integer, String> producer = new KafkaProducer<>(configs, new IntegerSerializer(), new StringSerializer());
        producer.send(new ProducerRecord<>("test-topic", 1, "my-test-value1"));
        producer.flush();

        await().pollDelay(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> mockery.assertIsSatisfied());

        kafkaConsumer.stop();
        Assert.assertFalse(kafkaConsumer.isRunning());
    }
}

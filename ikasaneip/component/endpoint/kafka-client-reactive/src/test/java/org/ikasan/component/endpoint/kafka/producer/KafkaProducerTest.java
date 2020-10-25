package org.ikasan.component.endpoint.kafka.producer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.testharness.flow.kafka.MessageListenerVerifier;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1,
    topics = {
        "test-topic" },
    brokerProperties = {
        "transaction.state.log.replication.factor=1",
        "transaction.state.log.min.isr=1",
        "auto.create.topics.enable=false"} )
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

    private KafkaProducerConfiguration producerConfiguration;

    @Before
    public void setup() {
        this.producerConfiguration = new KafkaProducerConfiguration();
        this.producerConfiguration.setBootstrapServers(List.of(this.embeddedKafka.getBrokersAsString()));
        this.producerConfiguration.setAcks("all");
        this.producerConfiguration.setKeySerializer("org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfiguration.setValueSerializer("org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfiguration.setTransactionTimeoutMillis(1000);
        this.producerConfiguration.setRetries(0);
        this.producerConfiguration.setRequestTimeoutMillis(500);
        this.producerConfiguration.setLingerMillis(50L);
        this.producerConfiguration.setDeliveryTimeoutMillis(1000);
        this.producerConfiguration.setMaxBlockMillis(1000L);
        this.producerConfiguration.setTransactionalId("test-transaction");

        Map<String, String> props = new HashMap<>();
        props.put("transaction.state.log.replication.factor", "1");
        embeddedKafka.brokerProperties(props);
    }

    @Test
    @DirtiesContext
    public void test_publish_message_success() throws InterruptedException {
        this.producerConfiguration.setClientId("testClient");
        this.producerConfiguration.setTopicName("test-topic");

        KafkaProducer<String> kafkaProducer = new KafkaProducer<>(new StringKeyProvider());
        kafkaProducer.setConfiguration(this.producerConfiguration);

        kafkaProducer.startManagedResource();

        IntStream.range(0, 100).forEach(i -> kafkaProducer.invoke("test message"));

        MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(this.embeddedKafka.getBrokersAsString(),
            "test-topic", StringDeserializer.class, StringDeserializer.class, "testClient");
        messageListenerVerifier.start();

        Thread.sleep(1000);

        kafkaProducer.stopManagedResource();
        mockery.assertIsSatisfied();

        Assert.assertEquals(100, messageListenerVerifier.getCaptureResults().size());

        messageListenerVerifier.stop();
    }

    @Test (expected = EndpointException.class)
    @DirtiesContext
    public void test_publish_message_exception_broker_not_available() throws InterruptedException {
        this.producerConfiguration.setClientId("testClient");
        this.producerConfiguration.setTopicName("test-topic");

        KafkaProducer<String> kafkaProducer = new KafkaProducer<>(new StringKeyProvider());
        kafkaProducer.setConfiguration(this.producerConfiguration);

        kafkaProducer.startManagedResource();


        this.embeddedKafka.destroy();

        IntStream.range(0, 1).forEach(i ->kafkaProducer.invoke("test message"));
    }

    @Test (expected = EndpointException.class)
    @DirtiesContext
    public void test_publish_message_exception_bad_topic_name() throws InterruptedException {
        this.producerConfiguration.setClientId("testClient");
        this.producerConfiguration.setTopicName("bad-topic");

        KafkaProducer<String> kafkaProducer = new KafkaProducer<>(new StringKeyProvider());
        kafkaProducer.setConfiguration(this.producerConfiguration);

        kafkaProducer.startManagedResource();

        IntStream.range(0, 1).forEach(i ->kafkaProducer.invoke("test message"));
    }

    private class StringKeyProvider implements KafkaKeyProvider<String> {
        @Override
        public String getKey() {
            return "key";
        }
    }

}

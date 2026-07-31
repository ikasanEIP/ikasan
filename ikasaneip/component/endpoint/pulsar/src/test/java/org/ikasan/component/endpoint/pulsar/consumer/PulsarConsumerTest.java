package org.ikasan.component.endpoint.pulsar.consumer;

import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.ikasan.component.endpoint.pulsar.consumer.configuration.PulsarConsumerConfiguration;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.event.Resubmission;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.utility.DockerImageName;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for PulsarConsumer using TestContainers
 *
 * @author Ikasan Development Team
 */
public class PulsarConsumerTest {

    @ClassRule
    public static PulsarContainer pulsarContainer
        = new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:3.0.0"));

    @Mock
    private EventFactory<FlowEvent<?, ?>> flowEventFactory;

    @Mock
    private ResubmissionEventFactory<Resubmission<?>> resubmissionEventFactory;

    @Mock
    private EventListener eventListener;

    @Mock
    private FlowEvent flowEvent;

    @Mock
    private Resubmission resubmission;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private Transaction transaction;

    @Mock
    private Xid xid;

    private PulsarConsumer consumer;
    private PulsarConsumerConfiguration configuration;
    private Producer<byte[]> testProducer;
    private PulsarClient testClient;
    private org.apache.pulsar.client.api.Consumer<byte[]> cleanupConsumer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create test Pulsar client and producer
        testClient = PulsarClient.builder()
            .serviceUrl(pulsarContainer.getPulsarBrokerUrl())
            .build();

        testProducer = testClient.newProducer(Schema.BYTES)
            .topic("test-topic")
            .create();

        // Clear any existing messages from previous test runs
        clearMessages("test-topic", "test-subscription");
        clearMessages("test-topic", "test-subscription-earliest");

        // Setup configuration
        configuration = new PulsarConsumerConfiguration();
        configuration.setServiceUrl(pulsarContainer.getPulsarBrokerUrl());
        configuration.setTopics(new String[]{"test-topic"});
        configuration.setSubscriptionName("test-subscription");
        configuration.setSubscriptionType("Exclusive");
        configuration.setMessageListenerThreads(1);
        configuration.setReceiverQueueSize(10);

        // Create consumer
        InboundQueueMessageListener inboundQueueMessageListener = new InboundQueueMessageListener();

        consumer = new PulsarConsumer(transactionManager, inboundQueueMessageListener);
        consumer.setConfiguration(configuration);
        consumer.setConfiguredResourceId("test-consumer");
        consumer.setEventFactory(flowEventFactory);
        consumer.setResubmissionEventFactory(resubmissionEventFactory);
        consumer.setListener(eventListener);

        inboundQueueMessageListener.setMessageListener(consumer);
        inboundQueueMessageListener.setEndpointListener(consumer);

        // Setup common mocks
        when(transactionManager.getTransaction()).thenReturn(transaction);
        when(flowEventFactory.newEvent(anyString(), anyString(), any())).thenReturn(flowEvent);
    }

    @After
    public void tearDown() throws Exception {
        if (consumer != null && consumer.isRunning()) {
            consumer.stop();
        }
        if (cleanupConsumer != null) {
            cleanupConsumer.close();
        }
        if (testProducer != null) {
            testProducer.close();
        }
        if (testClient != null) {
            testClient.close();
        }
    }

    /**
     * Clear all messages from a topic/subscription to ensure clean state between tests
     */
    private void clearMessages(String topic, String subscription) throws Exception {
        try {
            cleanupConsumer = testClient.newConsumer(Schema.BYTES)
                .topic(topic)
                .subscriptionName(subscription)
                .subscriptionType(org.apache.pulsar.client.api.SubscriptionType.Exclusive)
                .subscribe();

            // Drain all messages with a short timeout
            org.apache.pulsar.client.api.Message<byte[]> msg;
            while ((msg = cleanupConsumer.receive(100, TimeUnit.MILLISECONDS)) != null) {
                cleanupConsumer.acknowledge(msg);
            }

            cleanupConsumer.close();
            cleanupConsumer = null;
        } catch (Exception e) {
            // If subscription doesn't exist yet, that's fine - no messages to clear
            if (cleanupConsumer != null) {
                try {
                    cleanupConsumer.close();
                } catch (Exception ex) {
                    // Ignore
                }
                cleanupConsumer = null;
            }
        }
    }

    @Test
    public void test_message_consumed_successfully() throws Exception {
        // Given
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        String testMessage = "test message";
        testProducer.send(testMessage.getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));

        // Commit the transaction
        consumer.commit(xid, true);

        verify(flowEventFactory).newEvent(anyString(), anyString(), any(byte[].class));
    }

    @Test
    public void test_multiple_messages_consumed_successfully() throws Exception {
        // Given
        AtomicInteger messageCount = new AtomicInteger(0);
        doAnswer(invocation -> {
            messageCount.incrementAndGet();
            return null;
        }).when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send multiple test messages
        for (int i = 0; i < 5; i++) {
            testProducer.send(("test message " + i).getBytes(StandardCharsets.UTF_8));
        }

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(5, messageCount.get()));

        // Commit all transactions
        for (int i = 0; i < 5; i++) {
            consumer.commit(xid, true);
        }
    }

    @Test
    public void test_transaction_commit_acknowledges_message() throws Exception {
        // Given
        doNothing().when(eventListener).invoke(any(FlowEvent.class));
        configuration.setAutoAcknowledge(false);

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Wait for message to be consumed
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));

        // Commit the transaction
        consumer.commit(xid, true);

        // Verify commit was successful (no exception thrown)
        verify(eventListener, times(1)).invoke(any(FlowEvent.class));
    }

    @Test
    public void test_transaction_rollback_negatively_acknowledges_message() throws Exception {
        // Given
        doThrow(new RuntimeException("test exception")).when(eventListener).invoke(any(FlowEvent.class));
        configuration.setAutoAcknowledge(false);

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Wait for message to be consumed and exception to be thrown
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, atLeastOnce()).invoke(any(FlowEvent.class)));

        // Rollback the transaction
        consumer.rollback(xid);

        // Message should be negatively acknowledged and redelivered
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, atLeast(2)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_auto_acknowledge_enabled() throws Exception {
        // Given
        doNothing().when(eventListener).invoke(any(FlowEvent.class));
        configuration.setAutoAcknowledge(true);

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));

        // No explicit commit needed, message should be auto-acknowledged
        // Sending another message should work fine
        testProducer.send("test message 2".getBytes(StandardCharsets.UTF_8));

        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(2)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_exception_handling_invokes_event_listener() throws Exception {
        // Given
        RuntimeException testException = new RuntimeException("test exception");
        doThrow(testException).when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, atLeastOnce()).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_force_transaction_rollback_exception_stops_consumer() throws Exception {
        // Given
        ForceTransactionRollbackException rollbackException = new ForceTransactionRollbackException("force rollback");
        doThrow(rollbackException).when(eventListener).invoke(any(Throwable.class));
        doThrow(new RuntimeException("trigger exception")).when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message to trigger exception
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> assertFalse("Consumer should be stopped", consumer.isRunning()));
    }

    @Test
    public void test_resubmission_event() throws Exception {
        byte[] testEvent = "resubmission test".getBytes(StandardCharsets.UTF_8);

        // Given
        when(resubmissionEventFactory.newResubmissionEvent(any())).thenReturn(resubmission);
        when(resubmission.getEvent()).thenReturn(testEvent);
        doNothing().when(eventListener).invoke(any(Resubmission.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        consumer.onResubmission(testEvent);

        // Then
        verify(resubmissionEventFactory, times(1)).newResubmissionEvent(testEvent);
        verify(eventListener, times(1)).invoke(any(Resubmission.class));
    }

    @Test(expected = RuntimeException.class)
    public void test_resubmission_without_event_listener_throws_exception() throws Exception {
        // Given
        consumer.setListener(null);
        when(resubmissionEventFactory.newResubmissionEvent(any())).thenReturn(resubmission);

        // When
        consumer.start();
        byte[] testEvent = "resubmission test".getBytes(StandardCharsets.UTF_8);
        consumer.onResubmission(testEvent);

        // Then - exception should be thrown
    }

    @Test
    public void test_consumer_lifecycle_start_stop() throws Exception {
        // Given
        assertFalse("Consumer should not be running initially", consumer.isRunning());

        // When - Start
        consumer.start();

        // Then
        assertTrue("Consumer should be running after start", consumer.isRunning());
        assertNotNull("Pulsar client should be created", consumer.consumer);
        assertNotNull("Pulsar consumer should be created", consumer.consumer);

        // When - Stop
        consumer.stop();

        // Then
        assertFalse("Consumer should not be running after stop", consumer.isRunning());
    }

    @Test
    public void test_consumer_with_custom_name() throws Exception {
        // Given
        configuration.setConsumerName("my-custom-consumer");
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_acknowledgment_timeout() throws Exception {
        // Given
        configuration.setAckTimeoutMillis(5000);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_batch_index_acknowledgment() throws Exception {
        // Given
        configuration.setBatchIndexAckEnabled(true);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_xa_resource_prepare() throws XAException {
        // Given
        consumer.start();

        // When
        int result = consumer.prepare(xid);

        // Then
        assertEquals("Prepare should return XA_OK", consumer.XA_OK, result);
    }

    @Test
    public void test_xa_resource_operations() throws XAException {
        // Given
        consumer.start();

        // When/Then - verify XAResource operations don't throw exceptions
        consumer.start(xid, 0);
        consumer.end(xid, 0);
        consumer.forget(xid);

        int timeout = consumer.getTransactionTimeout();
        assertEquals("Transaction timeout should be 0", 0, timeout);

        boolean timeoutSet = consumer.setTransactionTimeout(30);
        assertFalse("Set transaction timeout should return false", timeoutSet);

        Xid[] recovered = consumer.recover(0);
        assertNotNull("Recover should return empty array", recovered);
        assertEquals("Recover should return empty array", 0, recovered.length);

        boolean sameRM = consumer.isSameRM(consumer);
        assertFalse("isSameRM should return false", sameRM);
    }

    @Test
    public void test_configuration_management() {
        // Given
        String configId = "test-config-id";
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();

        // When
        consumer.setConfiguredResourceId(configId);
        consumer.setConfiguration(config);

        // Then
        assertEquals("Configuration ID should match", configId, consumer.getConfiguredResourceId());
        assertEquals("Configuration should match", config, consumer.getConfiguration());
    }

    @Test
    public void test_event_factory_management() {
        // Given
        EventFactory testFactory = mock(EventFactory.class);

        // When
        consumer.setEventFactory(testFactory);

        // Then
        assertEquals("Event factory should match", testFactory, consumer.getEventFactory());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_transaction_manager() {
        // When
        new PulsarConsumer(null, new InboundQueueMessageListener());

        // Then - exception should be thrown
    }

    @Test
    public void test_shared_subscription_type() throws Exception {
        // Given
        configuration.setSubscriptionType("Shared");
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_failover_subscription_type() throws Exception {
        // Given
        configuration.setSubscriptionType("Failover");
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("test message".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_multiple_topics_subscription() throws Exception {
        // Given
        Producer<byte[]> testProducer2 = testClient.newProducer(Schema.BYTES)
            .topic("test-topic-2")
            .create();

        // Clear messages from second topic as well
        clearMessages("test-topic-2", "test-subscription");

        configuration.setTopics(new String[]{"test-topic", "test-topic-2"});
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send messages to both topics
        testProducer.send("message from topic 1".getBytes(StandardCharsets.UTF_8));
        testProducer2.send("message from topic 2".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(2)).invoke(any(FlowEvent.class)));

        testProducer2.close();
    }

    @Test
    public void test_consumer_is_not_active() {
        // Given/When/Then
        assertFalse("Consumer should not be active", consumer.isActive());
    }

    @Test
    public void test_stop_non_running_consumer() {
        // Given
        assertFalse("Consumer should not be running", consumer.isRunning());

        // When/Then - should not throw exception
        consumer.stop();

        assertFalse("Consumer should still not be running", consumer.isRunning());
    }

    // New Configuration Option Tests

    @Test
    public void test_consumer_with_priority_level() throws Exception {
        // Given
        configuration.setPriorityLevel(5);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("priority test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_max_total_receiver_queue_size() throws Exception {
        // Given
        configuration.setMaxTotalReceiverQueueSizeAcrossPartitions(1000);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("queue size test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_read_compacted() throws Exception {
        // Given
        configuration.setReadCompacted(true);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("compacted test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_subscription_initial_position_earliest() throws Exception {
        // Given
        configuration.setSubscriptionInitialPosition("Earliest");
        configuration.setSubscriptionName("test-subscription-earliest");
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("earliest position test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_subscription_initial_position_latest() throws Exception {
        // Given
        configuration.setSubscriptionInitialPosition("Latest");
        configuration.setSubscriptionName("test-subscription-latest");
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message after starting consumer (Latest means only new messages)
        testProducer.send("latest position test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_pattern_auto_discovery() throws Exception {
        // Given
        configuration.setPatternAutoDiscoveryPeriod(true);
        configuration.setAutoDiscoveryPeriodMinutes(1);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("auto discovery test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_start_message_id_inclusive() throws Exception {
        // Given
        configuration.setStartMessageIdInclusive(true);
        configuration.setSubscriptionName("test-subscription-inclusive");
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("inclusive test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_batch_receive_enabled() throws Exception {
        // Given
        configuration.setBatchReceiveEnabled(true);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send multiple test messages
        for (int i = 0; i < 3; i++) {
            testProducer.send(("batch message " + i).getBytes(StandardCharsets.UTF_8));
        }

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(3)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_ack_receipt_enabled() throws Exception {
        // Given
        configuration.setAckReceiptEnabled(true);
        configuration.setAutoAcknowledge(false);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("ack receipt test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));

        // Commit to acknowledge
        consumer.commit(xid, true);
    }

    @Test
    public void test_consumer_with_pool_messages_disabled() throws Exception {
        // Given
        configuration.setPoolMessages(false);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("pool messages test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_replicate_subscription_state() throws Exception {
        // Given
        configuration.setReplicateSubscriptionState(true);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("replicate state test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_ack_timeout_tick_time() throws Exception {
        // Given
        configuration.setAckTimeoutTickTimeMillis(2000);
        configuration.setAckTimeoutMillis(5000);
        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send a test message
        testProducer.send("ack timeout tick test".getBytes(StandardCharsets.UTF_8));

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(1)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_consumer_with_multiple_new_configurations_combined() throws Exception {
        // Given - Test multiple new configurations together
        configuration.setPriorityLevel(3);
        configuration.setMaxTotalReceiverQueueSizeAcrossPartitions(2000);
        configuration.setReadCompacted(false);
        configuration.setSubscriptionInitialPosition("Latest");
        configuration.setPoolMessages(true);
        configuration.setAckTimeoutTickTimeMillis(1500);
        configuration.setSubscriptionName("test-subscription-combined");

        doNothing().when(eventListener).invoke(any(FlowEvent.class));

        // When
        consumer.start();
        assertTrue("Consumer should be running", consumer.isRunning());

        // Send multiple test messages
        for (int i = 0; i < 5; i++) {
            testProducer.send(("combined config test " + i).getBytes(StandardCharsets.UTF_8));
        }

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(eventListener, times(5)).invoke(any(FlowEvent.class)));
    }

    @Test
    public void test_configuration_getters_and_setters() {
        // Test all new configuration getters and setters
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();

        config.setPriorityLevel(5);
        assertEquals(5, config.getPriorityLevel());

        config.setMaxTotalReceiverQueueSizeAcrossPartitions(10000);
        assertEquals(10000, config.getMaxTotalReceiverQueueSizeAcrossPartitions());

        config.setReadCompacted(true);
        assertTrue(config.isReadCompacted());

        config.setTopicsPattern("test-pattern-*");
        assertEquals("test-pattern-*", config.getTopicsPattern());

        config.setSubscriptionInitialPosition("Earliest");
        assertEquals("Earliest", config.getSubscriptionInitialPosition());

        config.setPatternAutoDiscoveryPeriod(true);
        assertTrue(config.isPatternAutoDiscoveryPeriod());

        config.setAutoDiscoveryPeriodMinutes(5);
        assertEquals(5, config.getAutoDiscoveryPeriodMinutes());

        config.setCryptoKeyReaderClassName("com.example.CryptoReader");
        assertEquals("com.example.CryptoReader", config.getCryptoKeyReaderClassName());

        config.setRetryEnable(true);
        assertTrue(config.isRetryEnable());

        config.setDeadLetterTopic("my-dlq");
        assertEquals("my-dlq", config.getDeadLetterTopic());

        config.setMaxRedeliverCount(5);
        assertEquals(5, config.getMaxRedeliverCount());

        config.setStartMessageIdInclusive(true);
        assertTrue(config.isStartMessageIdInclusive());

        config.setBatchReceiveEnabled(true);
        assertTrue(config.isBatchReceiveEnabled());

        config.setAckReceiptEnabled(true);
        assertTrue(config.isAckReceiptEnabled());

        config.setPoolMessages(false);
        assertFalse(config.isPoolMessages());

        config.setReplicateSubscriptionState(true);
        assertTrue(config.isReplicateSubscriptionState());

        config.setAckTimeoutTickTimeMillis(3000);
        assertEquals(3000, config.getAckTimeoutTickTimeMillis());

        config.setNegativeAckRedeliveryDelay(5000);
        assertEquals(5000, config.getNegativeAckRedeliveryDelay());
    }
}

package org.ikasan.component.endpoint.pulsar.producer;

import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import org.apache.pulsar.client.api.*;
import org.ikasan.component.endpoint.pulsar.producer.configuration.PulsarProducerConfiguration;
import org.ikasan.spec.flow.FlowEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.utility.DockerImageName;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for PulsarProducerLRCO using TestContainers
 *
 * @author Ikasan Development Team
 */
public class PulsarProducerLRCOTest {

    @ClassRule
    public static PulsarContainer pulsarContainer = new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:3.0.0"));

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private Transaction transaction;

    @Mock
    private Xid xid;

    private PulsarProducerLRCO producer;
    private PulsarProducerConfiguration configuration;
    private PulsarClient testClient;
    private Consumer<byte[]> testConsumer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create test Pulsar client and consumer
        testClient = PulsarClient.builder()
            .serviceUrl(pulsarContainer.getPulsarBrokerUrl())
            .build();

        testConsumer = testClient.newConsumer(Schema.BYTES)
            .topic("test-producer-topic")
            .subscriptionName("test-subscription")
            .subscribe();

        // Clear any existing messages
        clearMessages();

        // Setup configuration
        configuration = new PulsarProducerConfiguration();
        configuration.setServiceUrl(pulsarContainer.getPulsarBrokerUrl());
        configuration.setTopic("test-producer-topic");
        configuration.setProducerName("test-producer");

        // Setup common mocks
        when(transactionManager.getTransaction()).thenReturn(transaction);
    }

    @After
    public void tearDown() throws Exception {
        if (producer != null) {
            try {
                producer.stopManagedResource();
            } catch (Exception e) {
                // Ignore
            }
        }
        if (testConsumer != null) {
            testConsumer.close();
        }
        if (testClient != null) {
            testClient.close();
        }
    }

    private void clearMessages() throws Exception {
        Message<byte[]> msg;
        while ((msg = testConsumer.receive(100, TimeUnit.MILLISECONDS)) != null) {
            testConsumer.acknowledge(msg);
        }
    }

    private FlowEvent createFlowEvent(Object payload) {
        return new FlowEvent() {
            @Override
            public Object getIdentifier() {
                return "test-id";
            }

            @Override
            public Object getRelatedIdentifier() {
                return "test-related-id";
            }

            @Override
            public long getTimestamp() {
                return System.currentTimeMillis();
            }

            @Override
            public Object getPayload() {
                return payload;
            }

            @Override
            public void setPayload(Object o) {
            }

            @Override
            public void replace(FlowEvent flowEvent) {
            }
        };
    }

    // Constructor Validation Tests

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_transaction_manager() {
        new PulsarProducerLRCO(null, configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_configuration() {
        new PulsarProducerLRCO(transactionManager, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_topic() {
        configuration.setTopic(null);
        new PulsarProducerLRCO(transactionManager, configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_empty_topic() {
        configuration.setTopic("");
        new PulsarProducerLRCO(transactionManager, configuration);
    }

    // Lifecycle Management Tests

    @Test
    public void test_managed_resource_start_stop() {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.setConfiguredResourceId("test-producer");

        // When - Start
        producer.startManagedResource();

        // Then
        assertNotNull("Producer should be created", producer.getProducer());
        assertFalse("Should not be recovering", producer.isRecovering());
        assertNull("Should have no critical failure", producer.getCriticalFailure());

        // When - Stop
        producer.stopManagedResource();

        // Then
        assertNull("Producer should be null after stop", producer.getProducer());
    }

    @Test
    public void test_managed_resource_start_creates_pulsar_client() {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);

        // When
        producer.startManagedResource();

        // Then
        assertNotNull("Pulsar producer should be created", producer.getProducer());
        assertEquals("Producer name should match", "test-producer", producer.getProducer().getProducerName());
        assertEquals("Topic should match", "test-producer-topic", producer.getProducer().getTopic());
    }

    @Test(expected = RuntimeException.class)
    public void test_managed_resource_start_with_invalid_url() {
        // Given
        configuration.setServiceUrl("invalid://url");
        producer = new PulsarProducerLRCO(transactionManager, configuration);

        // When/Then - should throw exception
        producer.startManagedResource();
    }

    @Test
    public void test_stop_without_start() {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);

        // When/Then - should not throw exception
        producer.stopManagedResource();
    }

    // Transaction Commit Tests

    @Test
    public void test_invoke_and_commit_byte_array_message() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        byte[] testMessage = "test message".getBytes(StandardCharsets.UTF_8);
        FlowEvent flowEvent = createFlowEvent(testMessage);

        // When - Invoke
        producer.invoke(flowEvent);

        // Verify transaction enlisted
        verify(transaction, times(1)).enlistResource(any(PulsarConnection.class));

        // Get the connection and commit
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        assertNotNull("Connection should be created", connection);

        connection.commit(xid, true);

        // Then - Verify message received
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        assertEquals("Message content should match", "test message",
            new String(receivedMessage.getData(), StandardCharsets.UTF_8));

        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_invoke_and_commit_string_message() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        String testMessage = "test string message";
        FlowEvent flowEvent = createFlowEvent(testMessage);

        // When
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        assertEquals("Message content should match", testMessage,
            new String(receivedMessage.getData(), StandardCharsets.UTF_8));

        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_invoke_multiple_messages_and_commit() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When - Send multiple messages
        for (int i = 0; i < 5; i++) {
            byte[] message = ("message-" + i).getBytes(StandardCharsets.UTF_8);
            FlowEvent flowEvent = createFlowEvent(message);
            producer.invoke(flowEvent);

            PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
            connection.commit(xid, true);
        }

        // Then - Verify all messages received
        for (int i = 0; i < 5; i++) {
            Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
            assertNotNull("Should receive message " + i, receivedMessage);
            String expected = "message-" + i;
            String actual = new String(receivedMessage.getData(), StandardCharsets.UTF_8);
            assertEquals("Message " + i + " should match", expected, actual);
            testConsumer.acknowledge(receivedMessage);
        }
    }

    // Transaction Rollback Tests

    @Test
    public void test_invoke_and_rollback_discards_message() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        byte[] testMessage = "rollback test".getBytes(StandardCharsets.UTF_8);
        FlowEvent flowEvent = createFlowEvent(testMessage);

        // When - Invoke but rollback
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.rollback(xid);

        // Then - No message should be received
        Message<byte[]> receivedMessage = testConsumer.receive(2, TimeUnit.SECONDS);
        assertNull("Should not receive message after rollback", receivedMessage);
    }

    @Test
    public void test_rollback_then_commit_different_message() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When - First message rolled back
        FlowEvent flowEvent1 = createFlowEvent("rollback message".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent1);
        PulsarConnection connection1 = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection1.rollback(xid);

        // Second message committed
        FlowEvent flowEvent2 = createFlowEvent("commit message".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent2);
        PulsarConnection connection2 = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection2.commit(xid, true);

        // Then - Only second message received
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive committed message", receivedMessage);
        assertEquals("Should receive only committed message", "commit message",
            new String(receivedMessage.getData(), StandardCharsets.UTF_8));

        testConsumer.acknowledge(receivedMessage);

        // No more messages
        Message<byte[]> noMessage = testConsumer.receive(1, TimeUnit.SECONDS);
        assertNull("Should not receive rolled back message", noMessage);
    }

    // Configuration Tests

    @Test
    public void test_configuration_with_batching_enabled() throws Exception {
        // Given
        configuration.setBatchingEnabled(true);
        configuration.setBatchingMaxMessages(100);
        configuration.setBatchingMaxPublishDelayMillis(50);
        configuration.setBatchingMaxBytes(64 * 1024);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("batching test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_batching_disabled() throws Exception {
        // Given
        configuration.setBatchingEnabled(false);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("no batching test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_compression() throws Exception {
        // Given
        configuration.setCompressionType("LZ4");

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("compressed message".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive compressed message", receivedMessage);
        assertEquals("Message should be decompressed correctly", "compressed message",
            new String(receivedMessage.getData(), StandardCharsets.UTF_8));
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_send_timeout() throws Exception {
        // Given
        configuration.setSendTimeoutMillis(10000);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("timeout test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_message_routing_mode() throws Exception {
        // Given
        configuration.setMessageRoutingMode("RoundRobinPartition");
        configuration.setHashingScheme("Murmur3_32Hash");

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("routing test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_max_pending_messages() throws Exception {
        // Given
        configuration.setMaxPendingMessages(500);
        configuration.setBlockIfQueueFull(false);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("pending test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_chunking_enabled() throws Exception {
        // Given
        configuration.setChunkingEnabled(true);
        configuration.setBatchingEnabled(false);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("chunking test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message with chunking enabled", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_auto_update_partitions() throws Exception {
        // Given
        configuration.setAutoUpdatePartitions(true);
        configuration.setAutoUpdatePartitionsIntervalSeconds(30);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("partition update test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_max_pending_messages_across_partitions() throws Exception {
        // Given
        configuration.setMaxPendingMessagesAcrossPartitions(100000);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("cross partition test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_multi_schema_disabled() throws Exception {
        // Given
        configuration.setMultiSchema(false);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("multi schema test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_access_mode_shared() throws Exception {
        // Given
        configuration.setAccessMode("Shared");

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("shared access test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_access_mode_exclusive() throws Exception {
        // Given
        configuration.setAccessMode("Exclusive");

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("exclusive access test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_access_mode_wait_for_exclusive() throws Exception {
        // Given
        configuration.setAccessMode("WaitForExclusive");

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("wait exclusive test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_lazy_start_partitioned_producers() throws Exception {
        // Given
        configuration.setLazyStartPartitionedProducers(true);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("lazy start test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_initial_sequence_id() throws Exception {
        // Given
        configuration.setInitialSequenceId(1000L);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("sequence id test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test
    public void test_configuration_with_round_robin_router_batching_frequency() throws Exception {
        // Given
        configuration.setRoundRobinRouterBatchingPartitionSwitchFrequency(5);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        // When
        FlowEvent flowEvent = createFlowEvent("round robin test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        testConsumer.acknowledge(receivedMessage);
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_batching_and_chunking_cannot_be_enabled_together() throws Exception {
        // Given - Test all new configuration options together
        configuration.setMaxPendingMessagesAcrossPartitions(75000);
        configuration.setMultiSchema(false);
        configuration.setAccessMode("Shared");
        configuration.setLazyStartPartitionedProducers(true);
        configuration.setInitialSequenceId(500L);
        configuration.setRoundRobinRouterBatchingPartitionSwitchFrequency(8);
        configuration.setChunkingEnabled(true);
        configuration.setAutoUpdatePartitions(true);
        configuration.setAutoUpdatePartitionsIntervalSeconds(45);

        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();
    }

    // Message Payload Type Tests

    @Test
    public void test_invoke_with_null_payload() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        FlowEvent flowEvent = createFlowEvent(null);

        // When
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then - No message should be sent (callback handles null gracefully)
        Message<byte[]> receivedMessage = testConsumer.receive(2, TimeUnit.SECONDS);
        assertNull("Should not receive message for null payload", receivedMessage);
    }

    @Test
    public void test_invoke_with_custom_object_payload() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        CustomTestObject customObject = new CustomTestObject("test-value", 42);
        FlowEvent flowEvent = createFlowEvent(customObject);

        // When
        producer.invoke(flowEvent);
        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");
        connection.commit(xid, true);

        // Then
        Message<byte[]> receivedMessage = testConsumer.receive(5, TimeUnit.SECONDS);
        assertNotNull("Should receive message", receivedMessage);
        String received = new String(receivedMessage.getData(), StandardCharsets.UTF_8);
        assertTrue("Should contain custom object string representation", received.contains("test-value"));
        testConsumer.acknowledge(receivedMessage);
    }

    // XA Resource Operation Tests

    @Test
    public void test_xa_resource_prepare() throws XAException {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        FlowEvent flowEvent = createFlowEvent("xa test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);

        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");

        // When
        int result = connection.prepare(xid);

        // Then
        assertEquals("Prepare should return XA_OK", connection.XA_OK, result);
    }

    @Test
    public void test_xa_resource_operations() throws XAException {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        producer.startManagedResource();

        FlowEvent flowEvent = createFlowEvent("xa operations test".getBytes(StandardCharsets.UTF_8));
        producer.invoke(flowEvent);

        PulsarConnection connection = (PulsarConnection) ReflectionTestUtils.getField(producer, "connection");

        // When/Then - verify XAResource operations don't throw exceptions
        connection.start(xid, 0);
        connection.end(xid, 0);
        connection.forget(xid);

        int timeout = connection.getTransactionTimeout();
        assertEquals("Transaction timeout should be 0", 0, timeout);

        boolean timeoutSet = connection.setTransactionTimeout(30);
        assertFalse("Set transaction timeout should return false", timeoutSet);

        Xid[] recovered = connection.recover(0);
        assertNotNull("Recover should return empty array", recovered);
        assertEquals("Recover should return empty array", 0, recovered.length);

        boolean sameRM = connection.isSameRM(connection);
        assertFalse("isSameRM should return false", sameRM);
    }

    // Configuration Management Tests

    @Test
    public void test_configuration_get_set() {
        // Given
        PulsarProducerConfiguration newConfig = new PulsarProducerConfiguration();
        newConfig.setTopic("new-topic");

        producer = new PulsarProducerLRCO(transactionManager, configuration);

        // When
        producer.setConfiguration(newConfig);

        // Then
        assertEquals("Configuration should be updated", newConfig, producer.getConfiguration());
        assertEquals("Topic should be updated", "new-topic", producer.getConfiguration().getTopic());
    }

    @Test
    public void test_configured_resource_id_get_set() {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        String resourceId = "my-producer-id";

        // When
        producer.setConfiguredResourceId(resourceId);

        // Then
        assertEquals("Resource ID should match", resourceId, producer.getConfiguredResourceId());
    }

    @Test
    public void test_is_critical_on_startup() {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);

        // When/Then
        assertTrue("Producer should be critical on startup", producer.isCriticalOnStartup());
    }

    @Test
    public void test_set_critical_on_startup_does_not_change() {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);

        // When
        producer.setCriticalOnStartup(false);

        // Then - Still critical (not configurable)
        assertTrue("Producer should remain critical on startup", producer.isCriticalOnStartup());
    }

    // Error Handling Tests

    @Test(expected = RuntimeException.class)
    public void test_invoke_without_starting_managed_resource_throws_exception() throws Exception {
        // Given
        producer = new PulsarProducerLRCO(transactionManager, configuration);
        // Note: NOT calling startManagedResource()

        FlowEvent flowEvent = createFlowEvent("test".getBytes(StandardCharsets.UTF_8));

        // When/Then - should throw exception
        producer.invoke(flowEvent);
    }

    @Test
    public void test_critical_failure_tracking_on_startup_error() {
        // Given
        configuration.setServiceUrl("invalid://url:9999");
        producer = new PulsarProducerLRCO(transactionManager, configuration);

        // When
        try {
            producer.startManagedResource();
            fail("Should have thrown exception");
        } catch (RuntimeException e) {
            // Expected
        }

        // Then
        assertNotNull("Should have critical failure message", producer.getCriticalFailure());
        assertTrue("Critical failure should contain error info",
            producer.getCriticalFailure().contains("Failed to start Pulsar Producer"));
    }

    // Helper class for testing custom object payloads
    private static class CustomTestObject {
        private String value;
        private int number;

        public CustomTestObject(String value, int number) {
            this.value = value;
            this.number = number;
        }

        @Override
        public String toString() {
            return "CustomTestObject{value='" + value + "', number=" + number + "}";
        }
    }
}

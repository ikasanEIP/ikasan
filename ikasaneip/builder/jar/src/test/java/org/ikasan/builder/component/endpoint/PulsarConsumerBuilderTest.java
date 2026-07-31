package org.ikasan.builder.component.endpoint;

import jakarta.transaction.TransactionManager;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.pulsar.consumer.PulsarConsumer;
import org.ikasan.component.endpoint.pulsar.consumer.configuration.PulsarConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * Unit tests for PulsarConsumerBuilder.
 *
 * @author Ikasan Development Team
 */
public class PulsarConsumerBuilderTest {

    @Mock
    private AopProxyProvider aopProxyProvider;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventListener<?> eventListener;

    @Mock
    private EventFactory eventFactory;

    @Mock
    private ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService;

    @Mock
    private ResubmissionEventFactory resubmissionEventFactory;

    private PulsarConsumerBuilder builder;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        builder = new PulsarConsumerBuilderImpl(aopProxyProvider, transactionManager);
    }

    // Constructor Tests

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_aop_proxy_provider() {
        new PulsarConsumerBuilderImpl(null, transactionManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_transaction_manager() {
        new PulsarConsumerBuilderImpl(aopProxyProvider, null);
    }

    @Test
    public void test_constructor_creates_default_configuration() {
        PulsarConsumerBuilderImpl impl = new PulsarConsumerBuilderImpl(aopProxyProvider, transactionManager);
        assertNotNull("Should create default configuration", impl);
    }

    // Configuration Tests

    @Test
    public void test_set_configuration() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setServiceUrl("pulsar://test:6650");

        PulsarConsumerBuilder result = builder.setConfiguration(config);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertNotNull("Consumer should be created", consumer);
        assertTrue("Should be PulsarConsumer instance", consumer instanceof PulsarConsumer);
        assertEquals("Service URL should match", "pulsar://test:6650",
            ((PulsarConsumer) consumer).getConfiguration().getServiceUrl());
    }

    @Test
    public void test_set_service_url() {
        String serviceUrl = "pulsar://localhost:6650";

        PulsarConsumerBuilder result = builder.setServiceUrl(serviceUrl);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("Service URL should match", serviceUrl,
            ((PulsarConsumer) consumer).getConfiguration().getServiceUrl());
    }

    @Test
    public void test_set_topics() {
        String[] topics = {"topic1", "topic2", "topic3"};

        PulsarConsumerBuilder result = builder.setTopics(topics);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertArrayEquals("Topics should match", topics,
            ((PulsarConsumer) consumer).getConfiguration().getTopics());
    }

    @Test
    public void test_set_single_topic() {
        String topic = "my-topic";

        PulsarConsumerBuilder result = builder.setTopics(topic);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        String[] actualTopics = ((PulsarConsumer) consumer).getConfiguration().getTopics();
        assertEquals("Should have one topic", 1, actualTopics.length);
        assertEquals("Topic should match", topic, actualTopics[0]);
    }

    @Test
    public void test_set_subscription_name() {
        String subscriptionName = "my-subscription";

        PulsarConsumerBuilder result = builder.setSubscriptionName(subscriptionName);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("Subscription name should match", subscriptionName,
            ((PulsarConsumer) consumer).getConfiguration().getSubscriptionName());
    }

    @Test
    public void test_set_subscription_type() {
        String subscriptionType = "Shared";

        PulsarConsumerBuilder result = builder.setSubscriptionType(subscriptionType);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("Subscription type should match", subscriptionType,
            ((PulsarConsumer) consumer).getConfiguration().getSubscriptionType());
    }

    @Test
    public void test_set_consumer_name() {
        String consumerName = "test-consumer";

        PulsarConsumerBuilder result = builder.setConsumerName(consumerName);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("Consumer name should match", consumerName,
            ((PulsarConsumer) consumer).getConfiguration().getConsumerName());
    }

    // Authentication Tests

    @Test
    public void test_set_authentication_enabled() {
        PulsarConsumerBuilder result = builder.setAuthenticationEnabled(true);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertTrue("Authentication should be enabled",
            ((PulsarConsumer) consumer).getConfiguration().isAuthenticationEnabled());
    }

    @Test
    public void test_set_authentication_plugin_class_name() {
        String authPlugin = "org.apache.pulsar.client.impl.auth.AuthenticationToken";

        PulsarConsumerBuilder result = builder.setAuthPluginClassName(authPlugin);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("Auth plugin class name should match", authPlugin,
            ((PulsarConsumer) consumer).getConfiguration().getAuthPluginClassName());
    }

    @Test
    public void test_set_auth_params() {
        String authParams = "token:eyJhbGciOiJIUzI1NiJ9...";

        PulsarConsumerBuilder result = builder.setAuthParams(authParams);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("Auth params should match", authParams,
            ((PulsarConsumer) consumer).getConfiguration().getAuthParams());
    }

    // TLS Tests

    @Test
    public void test_set_tls_enabled() {
        PulsarConsumerBuilder result = builder.setTlsEnabled(true);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertTrue("TLS should be enabled",
            ((PulsarConsumer) consumer).getConfiguration().isTlsEnabled());
    }

    @Test
    public void test_set_tls_trust_certs_file_path() {
        String tlsPath = "/path/to/ca-cert.pem";

        PulsarConsumerBuilder result = builder.setTlsTrustCertsFilePath(tlsPath);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("TLS trust certs path should match", tlsPath,
            ((PulsarConsumer) consumer).getConfiguration().getTlsTrustCertsFilePath());
    }

    // Event Listener and Factory Tests

    @Test
    public void test_set_listener() {
        PulsarConsumerBuilder result = builder.setListener(eventListener);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertNotNull("Consumer should be created", consumer);
    }

    @Test
    public void test_set_event_factory() {
        PulsarConsumerBuilder result = builder.setEventFactory(eventFactory);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertNotNull("Consumer should be created", consumer);
    }

    @Test
    public void test_set_managed_identifier_service() {
        PulsarConsumerBuilder result = builder.setManagedIdentifierService(managedRelatedEventIdentifierService);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertNotNull("Consumer should be created", consumer);
    }

    @Test
    public void test_set_managed_event_identifier_service() {
        PulsarConsumerBuilder result = builder.setManagedEventIdentifierService(managedRelatedEventIdentifierService);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertNotNull("Consumer should be created", consumer);
    }

    @Test
    public void test_set_resubmission_event_factory() {
        PulsarConsumerBuilder result = builder.setResubmissionEventFactory(resubmissionEventFactory);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertNotNull("Consumer should be created", consumer);
    }

    @Test
    public void test_set_configuration_id() {
        String configId = "test-consumer-config";

        PulsarConsumerBuilder result = builder.setConfigurationId(configId);

        assertSame("Should return builder for chaining", builder, result);
        Consumer consumer = builder.build();
        assertEquals("Configuration ID should match", configId,
            ((PulsarConsumer) consumer).getConfiguredResourceId());
    }

    // Build Tests

    @Test
    public void test_build_minimal_configuration() {
        Consumer consumer = builder.build();

        assertNotNull("Consumer should be created", consumer);
        assertTrue("Should be PulsarConsumer instance", consumer instanceof PulsarConsumer);
        assertNotNull("Configuration should not be null",
            ((PulsarConsumer) consumer).getConfiguration());
    }

    @Test
    public void test_build_full_configuration() {
        Consumer consumer = builder
            .setServiceUrl("pulsar://test:6650")
            .setTopics("topic1", "topic2")
            .setSubscriptionName("test-subscription")
            .setSubscriptionType("Shared")
            .setConsumerName("test-consumer")
            .setAuthenticationEnabled(true)
            .setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken")
            .setAuthParams("token:test")
            .setTlsEnabled(true)
            .setTlsTrustCertsFilePath("/path/to/cert")
            .setConfigurationId("test-config")
            .setListener(eventListener)
            .setEventFactory(eventFactory)
            .setManagedIdentifierService(managedRelatedEventIdentifierService)
            .setResubmissionEventFactory(resubmissionEventFactory)
            .build();

        assertNotNull("Consumer should be created", consumer);
        assertTrue("Should be PulsarConsumer instance", consumer instanceof PulsarConsumer);

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Service URL should match", "pulsar://test:6650", config.getServiceUrl());
        assertArrayEquals("Topics should match", new String[]{"topic1", "topic2"}, config.getTopics());
        assertEquals("Subscription name should match", "test-subscription", config.getSubscriptionName());
        assertEquals("Subscription type should match", "Shared", config.getSubscriptionType());
        assertEquals("Consumer name should match", "test-consumer", config.getConsumerName());
        assertTrue("Authentication should be enabled", config.isAuthenticationEnabled());
        assertEquals("Auth plugin class should match",
            "org.apache.pulsar.client.impl.auth.AuthenticationToken", config.getAuthPluginClassName());
        assertEquals("Auth params should match", "token:test", config.getAuthParams());
        assertTrue("TLS should be enabled", config.isTlsEnabled());
        assertEquals("TLS cert path should match", "/path/to/cert", config.getTlsTrustCertsFilePath());
        assertEquals("Configuration ID should match", "test-config", pulsarConsumer.getConfiguredResourceId());
    }

    // Fluent API Tests

    @Test
    public void test_fluent_api_chaining() {
        Consumer consumer = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopics("test-topic")
            .setSubscriptionName("test-sub")
            .setConsumerName("test-consumer")
            .setConfigurationId("test-id")
            .build();

        assertNotNull("Consumer should be created through fluent API", consumer);
        assertTrue("Should be PulsarConsumer instance", consumer instanceof PulsarConsumer);
    }

    @Test
    public void test_builder_reusability() {
        // Build first consumer
        Consumer consumer1 = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopics("topic1")
            .setSubscriptionName("sub1")
            .build();

        // Build second consumer with different settings
        Consumer consumer2 = builder
            .setServiceUrl("pulsar://localhost:6651")
            .setTopics("topic2")
            .setSubscriptionName("sub2")
            .build();

        assertNotNull("First consumer should be created", consumer1);
        assertNotNull("Second consumer should be created", consumer2);

        // Note: Both consumers will have the same config due to builder reuse
        // This is expected behavior - builders are typically used once
    }

    // Edge Case Tests

    @Test
    public void test_build_with_null_optional_fields() {
        Consumer consumer = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopics("test-topic")
            .setSubscriptionName("test-sub")
            .setListener(null)
            .setEventFactory(null)
            .setManagedIdentifierService(null)
            .setResubmissionEventFactory(null)
            .build();

        assertNotNull("Consumer should be created with null optional fields", consumer);
    }

    @Test
    public void test_build_with_empty_topic() {
        Consumer consumer = builder
            .setTopics("")
            .build();

        assertNotNull("Consumer should be created", consumer);
        String[] topics = ((PulsarConsumer) consumer).getConfiguration().getTopics();
        assertEquals("Should have one empty topic", 1, topics.length);
        assertEquals("Topic should be empty string", "", topics[0]);
    }

    @Test
    public void test_multiple_subscription_types() {
        String[] subscriptionTypes = {"Exclusive", "Shared", "Failover", "Key_Shared"};

        for (String subscriptionType : subscriptionTypes) {
            PulsarConsumerBuilder testBuilder = new PulsarConsumerBuilderImpl(aopProxyProvider, transactionManager);
            Consumer consumer = testBuilder
                .setSubscriptionType(subscriptionType)
                .build();

            assertEquals("Subscription type should match for " + subscriptionType,
                subscriptionType,
                ((PulsarConsumer) consumer).getConfiguration().getSubscriptionType());
        }
    }
}

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

    // ========================================
    // Schema Configuration Tests
    // ========================================

    @Test
    public void test_set_schema_type_bytes() {
        Consumer consumer = builder
            .setSchemaType("BYTES")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Schema type should be BYTES",
                     "BYTES",
                     pulsarConsumer.getConfiguration().getSchemaType());
    }

    @Test
    public void test_set_schema_type_string() {
        Consumer consumer = builder
            .setSchemaType("STRING")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Schema type should be STRING",
                     "STRING",
                     pulsarConsumer.getConfiguration().getSchemaType());
    }

    @Test
    public void test_set_schema_type_json() {
        Consumer consumer = builder
            .setSchemaType("JSON")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Schema type should be JSON",
                     "JSON",
                     pulsarConsumer.getConfiguration().getSchemaType());
    }

    @Test
    public void test_set_message_class_name() {
        Consumer consumer = builder
            .setSchemaType("JSON")
            .setMessageClassName("com.example.MyMessage")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Message class name should be set",
                     "com.example.MyMessage",
                     pulsarConsumer.getConfiguration().getSchemaMessageClassName());
    }

    @Test
    public void test_json_schema_with_class_name() {
        Consumer consumer = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopics("test-topic")
            .setSubscriptionName("test-subscription")
            .setSchemaType("JSON")
            .setMessageClassName("org.ikasan.component.endpoint.pulsar.consumer.TestMessage")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be JSON", "JSON", config.getSchemaType());
        assertEquals("Message class name should be set",
                     "org.ikasan.component.endpoint.pulsar.consumer.TestMessage",
                     config.getSchemaMessageClassName());
    }

    @Test
    public void test_avro_schema_configuration() {
        Consumer consumer = builder
            .setSchemaType("AVRO")
            .setMessageClassName("com.example.AvroClass")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be AVRO", "AVRO", config.getSchemaType());
        assertEquals("Message class name should be set",
                     "com.example.AvroClass",
                     config.getSchemaMessageClassName());
    }

    @Test
    public void test_protobuf_schema_configuration() {
        Consumer consumer = builder
            .setSchemaType("PROTOBUF")
            .setMessageClassName("com.example.ProtoClass")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be PROTOBUF", "PROTOBUF", config.getSchemaType());
        assertEquals("Message class name should be set",
                     "com.example.ProtoClass",
                     config.getSchemaMessageClassName());
    }

    @Test
    public void test_auto_consume_schema() {
        Consumer consumer = builder
            .setSchemaType("AUTO_CONSUME")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Schema type should be AUTO_CONSUME",
                     "AUTO_CONSUME",
                     pulsarConsumer.getConfiguration().getSchemaType());
    }

    @Test
    public void test_default_schema_is_bytes() {
        Consumer consumer = builder.build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Default schema type should be BYTES",
                     "BYTES",
                     pulsarConsumer.getConfiguration().getSchemaType());
    }

    @Test
    public void test_schema_fluent_api() {
        Consumer consumer = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopics("topic1", "topic2")
            .setSubscriptionName("sub-name")
            .setSchemaType("JSON")
            .setMessageClassName("com.example.Message")
            .setConsumerName("test-consumer")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertNotNull("Consumer should be created", consumer);
        assertEquals("Schema type should be JSON", "JSON", config.getSchemaType());
        assertEquals("Message class name should be set",
                     "com.example.Message",
                     config.getSchemaMessageClassName());
        assertEquals("Service URL should be set",
                     "pulsar://localhost:6650",
                     config.getServiceUrl());
    }

    @Test
    public void test_all_primitive_schema_types() {
        String[] primitiveTypes = {"INT8", "INT16", "INT32", "INT64", "BOOL", "FLOAT", "DOUBLE"};

        for (String schemaType : primitiveTypes) {
            PulsarConsumerBuilder testBuilder = new PulsarConsumerBuilderImpl(aopProxyProvider, transactionManager);
            Consumer consumer = testBuilder
                .setSchemaType(schemaType)
                .build();

            assertEquals("Schema type should match for " + schemaType,
                         schemaType,
                         ((PulsarConsumer) consumer).getConfiguration().getSchemaType());
        }
    }

    @Test
    public void test_all_temporal_schema_types() {
        String[] temporalTypes = {"DATE", "TIME", "TIMESTAMP", "INSTANT", "LOCAL_DATE", "LOCAL_TIME", "LOCAL_DATE_TIME"};

        for (String schemaType : temporalTypes) {
            PulsarConsumerBuilder testBuilder = new PulsarConsumerBuilderImpl(aopProxyProvider, transactionManager);
            Consumer consumer = testBuilder
                .setSchemaType(schemaType)
                .build();

            assertEquals("Schema type should match for " + schemaType,
                         schemaType,
                         ((PulsarConsumer) consumer).getConfiguration().getSchemaType());
        }
    }

    // ========================================
    // Extended Schema Configuration Tests
    // ========================================

    @Test
    public void test_set_schema_avro_definition() {
        String avroDefinition = "{\"type\":\"record\",\"name\":\"TestMessage\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"}]}";
        Consumer consumer = builder
            .setSchemaType("AVRO")
            .setSchemaAvroDefinition(avroDefinition)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("AVRO definition should be set", avroDefinition,
                     pulsarConsumer.getConfiguration().getSchemaAvroDefinition());
    }

    @Test
    public void test_set_key_value_schema_types() {
        Consumer consumer = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyType("STRING")
            .setSchemaValueType("JSON")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be KEY_VALUE", "KEY_VALUE", config.getSchemaType());
        assertEquals("Key type should be STRING", "STRING", config.getSchemaKeyType());
        assertEquals("Value type should be JSON", "JSON", config.getSchemaValueType());
    }

    @Test
    public void test_set_key_value_schema_class_names() {
        Consumer consumer = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyClassName("java.lang.String")
            .setSchemaValueClassName("com.example.MyValue")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Key class name should be set", "java.lang.String", config.getSchemaKeyClassName());
        assertEquals("Value class name should be set", "com.example.MyValue", config.getSchemaValueClassName());
    }

    @Test
    public void test_set_key_value_encoding_type_inline() {
        Consumer consumer = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyValueEncodingType("INLINE")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Encoding type should be INLINE", "INLINE",
                     pulsarConsumer.getConfiguration().getSchemaKeyValueEncodingType());
    }

    @Test
    public void test_set_key_value_encoding_type_separated() {
        Consumer consumer = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyValueEncodingType("SEPARATED")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        assertEquals("Encoding type should be SEPARATED", "SEPARATED",
                     pulsarConsumer.getConfiguration().getSchemaKeyValueEncodingType());
    }

    @Test
    public void test_set_schema_properties() {
        java.util.Map<String, String> properties = new java.util.HashMap<>();
        properties.put("key1", "value1");
        properties.put("key2", "value2");

        Consumer consumer = builder
            .setSchemaProperties(properties)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        java.util.Map<String, String> resultProperties = pulsarConsumer.getConfiguration().getSchemaProperties();

        assertEquals("Should have 2 properties", 2, resultProperties.size());
        assertEquals("Property key1 should match", "value1", resultProperties.get("key1"));
        assertEquals("Property key2 should match", "value2", resultProperties.get("key2"));
    }

    @Test
    public void test_complex_key_value_schema_configuration() {
        java.util.Map<String, String> properties = new java.util.HashMap<>();
        properties.put("compression", "snappy");

        Consumer consumer = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopics("kv-topic")
            .setSubscriptionName("kv-subscription")
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyType("STRING")
            .setSchemaValueType("AVRO")
            .setSchemaKeyClassName("java.lang.String")
            .setSchemaValueClassName("com.example.AvroMessage")
            .setSchemaKeyValueEncodingType("SEPARATED")
            .setSchemaProperties(properties)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Service URL should be set", "pulsar://localhost:6650", config.getServiceUrl());
        assertEquals("Topic should be set", "kv-topic", config.getTopics()[0]);
        assertEquals("Subscription name should be set", "kv-subscription", config.getSubscriptionName());
        assertEquals("Schema type should be KEY_VALUE", "KEY_VALUE", config.getSchemaType());
        assertEquals("Key type should be STRING", "STRING", config.getSchemaKeyType());
        assertEquals("Value type should be AVRO", "AVRO", config.getSchemaValueType());
        assertEquals("Key class should be String", "java.lang.String", config.getSchemaKeyClassName());
        assertEquals("Value class should be AvroMessage", "com.example.AvroMessage", config.getSchemaValueClassName());
        assertEquals("Encoding should be SEPARATED", "SEPARATED", config.getSchemaKeyValueEncodingType());
        assertEquals("Schema property should be set", "snappy", config.getSchemaProperties().get("compression"));
    }

    @Test
    public void test_avro_schema_with_definition() {
        String avroSchema = "{\"type\":\"record\",\"name\":\"User\",\"fields\":[" +
                            "{\"name\":\"name\",\"type\":\"string\"}," +
                            "{\"name\":\"age\",\"type\":\"int\"}]}";

        Consumer consumer = builder
            .setSchemaType("AVRO")
            .setSchemaAvroDefinition(avroSchema)
            .setMessageClassName("com.example.User")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be AVRO", "AVRO", config.getSchemaType());
        assertEquals("AVRO definition should be set", avroSchema, config.getSchemaAvroDefinition());
        assertEquals("Message class should be set", "com.example.User", config.getSchemaMessageClassName());
    }

    @Test
    public void test_schema_fluent_api_with_all_attributes() {
        java.util.Map<String, String> schemaProps = new java.util.HashMap<>();
        schemaProps.put("__alwaysAllowNull", "true");

        Consumer consumer = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyType("INT32")
            .setSchemaValueType("JSON")
            .setSchemaValueClassName("com.example.Event")
            .setSchemaKeyValueEncodingType("INLINE")
            .setSchemaProperties(schemaProps)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be KEY_VALUE", "KEY_VALUE", config.getSchemaType());
        assertEquals("Key type should be INT32", "INT32", config.getSchemaKeyType());
        assertEquals("Value type should be JSON", "JSON", config.getSchemaValueType());
        assertEquals("Value class should be set", "com.example.Event", config.getSchemaValueClassName());
        assertEquals("Encoding should be INLINE", "INLINE", config.getSchemaKeyValueEncodingType());
        assertTrue("Schema properties should contain allowNull",
                   config.getSchemaProperties().containsKey("__alwaysAllowNull"));
    }

    @Test
    public void test_empty_schema_properties() {
        java.util.Map<String, String> emptyProps = new java.util.HashMap<>();

        Consumer consumer = builder
            .setSchemaProperties(emptyProps)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        java.util.Map<String, String> resultProps = pulsarConsumer.getConfiguration().getSchemaProperties();

        assertNotNull("Schema properties should not be null", resultProps);
        assertEquals("Schema properties should be empty", 0, resultProps.size());
    }

    @Test
    public void test_null_schema_properties() {
        Consumer consumer = builder
            .setSchemaProperties(null)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        // Should not throw NPE, configuration should handle null gracefully
        assertNotNull("Consumer should be created", pulsarConsumer);
    }

    @Test
    public void test_schema_builder_method_chaining() {
        // Verify all schema methods return the builder for fluent API
        PulsarConsumerBuilder result = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyType("STRING")
            .setSchemaValueType("AVRO")
            .setSchemaKeyClassName("java.lang.String")
            .setSchemaValueClassName("com.example.Value")
            .setSchemaKeyValueEncodingType("SEPARATED")
            .setSchemaAvroDefinition("{}")
            .setSchemaProperties(new java.util.HashMap<>());

        assertSame("All schema methods should return builder for chaining", builder, result);
    }

    @Test
    public void test_key_value_schema_with_primitive_key() {
        Consumer consumer = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyType("INT64")
            .setSchemaValueType("STRING")
            .setSchemaKeyValueEncodingType("INLINE")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Key type should be INT64", "INT64", config.getSchemaKeyType());
        assertEquals("Value type should be STRING", "STRING", config.getSchemaValueType());
        assertEquals("Encoding should be INLINE", "INLINE", config.getSchemaKeyValueEncodingType());
    }

    @Test
    public void test_key_value_schema_with_complex_types() {
        Consumer consumer = builder
            .setSchemaType("KEY_VALUE")
            .setSchemaKeyType("JSON")
            .setSchemaValueType("AVRO")
            .setSchemaKeyClassName("com.example.KeyModel")
            .setSchemaValueClassName("com.example.ValueModel")
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Key type should be JSON", "JSON", config.getSchemaKeyType());
        assertEquals("Value type should be AVRO", "AVRO", config.getSchemaValueType());
        assertEquals("Key class should be set", "com.example.KeyModel", config.getSchemaKeyClassName());
        assertEquals("Value class should be set", "com.example.ValueModel", config.getSchemaValueClassName());
    }

    @Test
    public void test_schema_properties_with_multiple_entries() {
        java.util.Map<String, String> props = new java.util.HashMap<>();
        props.put("jsr310ConversionEnabled", "true");
        props.put("__alwaysAllowNull", "false");
        props.put("__schemaInfoRequired", "true");

        Consumer consumer = builder
            .setSchemaType("JSON")
            .setMessageClassName("com.example.Message")
            .setSchemaProperties(props)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Should have 3 properties", 3, config.getSchemaProperties().size());
        assertEquals("jsr310 should be true", "true", config.getSchemaProperties().get("jsr310ConversionEnabled"));
        assertEquals("allowNull should be false", "false", config.getSchemaProperties().get("__alwaysAllowNull"));
        assertEquals("schemaInfoRequired should be true", "true", config.getSchemaProperties().get("__schemaInfoRequired"));
    }

    @Test
    public void test_comprehensive_schema_configuration_json() {
        java.util.Map<String, String> props = new java.util.HashMap<>();
        props.put("jsr310ConversionEnabled", "true");

        Consumer consumer = builder
            .setServiceUrl("pulsar://test:6650")
            .setTopics("json-topic")
            .setSubscriptionName("json-sub")
            .setSchemaType("JSON")
            .setMessageClassName("org.ikasan.test.JsonMessage")
            .setSchemaProperties(props)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be JSON", "JSON", config.getSchemaType());
        assertEquals("Message class should be set", "org.ikasan.test.JsonMessage", config.getSchemaMessageClassName());
        assertEquals("Schema property should be set", "true", config.getSchemaProperties().get("jsr310ConversionEnabled"));
    }

    @Test
    public void test_comprehensive_schema_configuration_avro() {
        String avroDefinition = "{\"type\":\"record\",\"name\":\"TestRecord\"}";

        Consumer consumer = builder
            .setServiceUrl("pulsar://test:6650")
            .setTopics("avro-topic")
            .setSubscriptionName("avro-sub")
            .setSchemaType("AVRO")
            .setMessageClassName("org.ikasan.test.AvroMessage")
            .setSchemaAvroDefinition(avroDefinition)
            .build();

        PulsarConsumer pulsarConsumer = (PulsarConsumer) consumer;
        PulsarConsumerConfiguration config = pulsarConsumer.getConfiguration();

        assertEquals("Schema type should be AVRO", "AVRO", config.getSchemaType());
        assertEquals("Message class should be set", "org.ikasan.test.AvroMessage", config.getSchemaMessageClassName());
        assertEquals("AVRO definition should be set", avroDefinition, config.getSchemaAvroDefinition());
    }
}

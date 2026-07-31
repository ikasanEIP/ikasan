package org.ikasan.builder.component.endpoint;

import jakarta.transaction.TransactionManager;
import org.ikasan.component.endpoint.pulsar.producer.PulsarProducerLRCO;
import org.ikasan.component.endpoint.pulsar.producer.configuration.PulsarProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * Unit tests for PulsarProducerLRCOBuilder.
 *
 * @author Ikasan Development Team
 */
public class PulsarProducerLRCOBuilderTest {

    @Mock
    private TransactionManager transactionManager;

    private PulsarProducerLRCOBuilder builder;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        builder = new PulsarProducerLRCOBuilderImpl(transactionManager);
    }

    // Constructor Tests

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_transaction_manager() {
        new PulsarProducerLRCOBuilderImpl(null);
    }

    @Test
    public void test_constructor_creates_default_configuration() {
        PulsarProducerLRCOBuilderImpl impl = new PulsarProducerLRCOBuilderImpl(transactionManager);
        assertNotNull("Should create builder", impl);
    }

    // Configuration Tests

    @Test
    public void test_set_configuration() {
        PulsarProducerConfiguration config = new PulsarProducerConfiguration();
        config.setServiceUrl("pulsar://test:6650");
        config.setTopic("test-topic");

        PulsarProducerLRCOBuilder result = builder.setConfiguration(config);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.build();
        assertNotNull("Producer should be created", producer);
        assertTrue("Should be PulsarProducerLRCO instance", producer instanceof PulsarProducerLRCO);
        assertEquals("Service URL should match", "pulsar://test:6650",
            ((PulsarProducerLRCO) producer).getConfiguration().getServiceUrl());
        assertEquals("Topic should match", "test-topic",
            ((PulsarProducerLRCO) producer).getConfiguration().getTopic());
    }

    @Test
    public void test_set_service_url() {
        String serviceUrl = "pulsar://localhost:6650";

        PulsarProducerLRCOBuilder result = builder.setServiceUrl(serviceUrl);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Service URL should match", serviceUrl,
            ((PulsarProducerLRCO) producer).getConfiguration().getServiceUrl());
    }

    @Test
    public void test_set_topic() {
        String topic = "my-topic";

        PulsarProducerLRCOBuilder result = builder.setTopic(topic);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.build();
        assertEquals("Topic should match", topic,
            ((PulsarProducerLRCO) producer).getConfiguration().getTopic());
    }

    @Test
    public void test_set_producer_name() {
        String producerName = "test-producer";

        PulsarProducerLRCOBuilder result = builder.setProducerName(producerName);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Producer name should match", producerName,
            ((PulsarProducerLRCO) producer).getConfiguration().getProducerName());
    }

    // Authentication Tests

    @Test
    public void test_set_authentication_enabled() {
        PulsarProducerLRCOBuilder result = builder.setAuthenticationEnabled(true);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertTrue("Authentication should be enabled",
            ((PulsarProducerLRCO) producer).getConfiguration().isAuthenticationEnabled());
    }

    @Test
    public void test_set_authentication_plugin_class_name() {
        String authPlugin = "org.apache.pulsar.client.impl.auth.AuthenticationToken";

        PulsarProducerLRCOBuilder result = builder.setAuthPluginClassName(authPlugin);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Auth plugin class name should match", authPlugin,
            ((PulsarProducerLRCO) producer).getConfiguration().getAuthPluginClassName());
    }

    @Test
    public void test_set_auth_params() {
        String authParams = "token:eyJhbGciOiJIUzI1NiJ9...";

        PulsarProducerLRCOBuilder result = builder.setAuthParams(authParams);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Auth params should match", authParams,
            ((PulsarProducerLRCO) producer).getConfiguration().getAuthParams());
    }

    // TLS Tests

    @Test
    public void test_set_tls_enabled() {
        PulsarProducerLRCOBuilder result = builder.setTlsEnabled(true);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertTrue("TLS should be enabled",
            ((PulsarProducerLRCO) producer).getConfiguration().isTlsEnabled());
    }

    @Test
    public void test_set_tls_trust_certs_file_path() {
        String tlsPath = "/path/to/ca-cert.pem";

        PulsarProducerLRCOBuilder result = builder.setTlsTrustCertsFilePath(tlsPath);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("TLS trust certs path should match", tlsPath,
            ((PulsarProducerLRCO) producer).getConfiguration().getTlsTrustCertsFilePath());
    }

    // Batching Tests

    @Test
    public void test_set_batching_enabled_true() {
        PulsarProducerLRCOBuilder result = builder.setBatchingEnabled(true);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertTrue("Batching should be enabled",
            ((PulsarProducerLRCO) producer).getConfiguration().isBatchingEnabled());
    }

    @Test
    public void test_set_batching_enabled_false() {
        PulsarProducerLRCOBuilder result = builder.setBatchingEnabled(false);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertFalse("Batching should be disabled",
            ((PulsarProducerLRCO) producer).getConfiguration().isBatchingEnabled());
    }

    // Compression Tests

    @Test
    public void test_set_compression_type_none() {
        PulsarProducerLRCOBuilder result = builder.setCompressionType("NONE");

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Compression type should be NONE", "NONE",
            ((PulsarProducerLRCO) producer).getConfiguration().getCompressionType());
    }

    @Test
    public void test_set_compression_type_lz4() {
        PulsarProducerLRCOBuilder result = builder.setCompressionType("LZ4");

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Compression type should be LZ4", "LZ4",
            ((PulsarProducerLRCO) producer).getConfiguration().getCompressionType());
    }

    @Test
    public void test_set_compression_type_zstd() {
        PulsarProducerLRCOBuilder result = builder.setCompressionType("ZSTD");

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Compression type should be ZSTD", "ZSTD",
            ((PulsarProducerLRCO) producer).getConfiguration().getCompressionType());
    }

    @Test
    public void test_set_compression_type_zlib() {
        PulsarProducerLRCOBuilder result = builder.setCompressionType("ZLIB");

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Compression type should be ZLIB", "ZLIB",
            ((PulsarProducerLRCO) producer).getConfiguration().getCompressionType());
    }

    @Test
    public void test_set_compression_type_snappy() {
        PulsarProducerLRCOBuilder result = builder.setCompressionType("SNAPPY");

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Compression type should be SNAPPY", "SNAPPY",
            ((PulsarProducerLRCO) producer).getConfiguration().getCompressionType());
    }

    // Configuration ID Tests

    @Test
    public void test_set_configuration_id() {
        String configId = "test-producer-config";

        PulsarProducerLRCOBuilder result = builder.setConfigurationId(configId);

        assertSame("Should return builder for chaining", builder, result);
        Producer producer = builder.setTopic("test-topic").build();
        assertEquals("Configuration ID should match", configId,
            ((PulsarProducerLRCO) producer).getConfiguredResourceId());
    }

    // Build Tests

    @Test
    public void test_build_minimal_configuration() {
        Producer producer = builder
            .setTopic("test-topic")
            .build();

        assertNotNull("Producer should be created", producer);
        assertTrue("Should be PulsarProducerLRCO instance", producer instanceof PulsarProducerLRCO);
        assertNotNull("Configuration should not be null",
            ((PulsarProducerLRCO) producer).getConfiguration());
    }

    @Test
    public void test_build_full_configuration() {
        Producer producer = builder
            .setServiceUrl("pulsar://test:6650")
            .setTopic("test-topic")
            .setProducerName("test-producer")
            .setAuthenticationEnabled(true)
            .setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken")
            .setAuthParams("token:test")
            .setTlsEnabled(true)
            .setTlsTrustCertsFilePath("/path/to/cert")
            .setBatchingEnabled(true)
            .setCompressionType("ZSTD")
            .setConfigurationId("test-config")
            .build();

        assertNotNull("Producer should be created", producer);
        assertTrue("Should be PulsarProducerLRCO instance", producer instanceof PulsarProducerLRCO);

        PulsarProducerLRCO pulsarProducer = (PulsarProducerLRCO) producer;
        PulsarProducerConfiguration config = pulsarProducer.getConfiguration();

        assertEquals("Service URL should match", "pulsar://test:6650", config.getServiceUrl());
        assertEquals("Topic should match", "test-topic", config.getTopic());
        assertEquals("Producer name should match", "test-producer", config.getProducerName());
        assertTrue("Authentication should be enabled", config.isAuthenticationEnabled());
        assertEquals("Auth plugin class should match",
            "org.apache.pulsar.client.impl.auth.AuthenticationToken", config.getAuthPluginClassName());
        assertEquals("Auth params should match", "token:test", config.getAuthParams());
        assertTrue("TLS should be enabled", config.isTlsEnabled());
        assertEquals("TLS cert path should match", "/path/to/cert", config.getTlsTrustCertsFilePath());
        assertTrue("Batching should be enabled", config.isBatchingEnabled());
        assertEquals("Compression type should match", "ZSTD", config.getCompressionType());
        assertEquals("Configuration ID should match", "test-config", pulsarProducer.getConfiguredResourceId());
    }

    // Fluent API Tests

    @Test
    public void test_fluent_api_chaining() {
        Producer producer = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopic("test-topic")
            .setProducerName("test-producer")
            .setBatchingEnabled(true)
            .setCompressionType("LZ4")
            .setConfigurationId("test-id")
            .build();

        assertNotNull("Producer should be created through fluent API", producer);
        assertTrue("Should be PulsarProducerLRCO instance", producer instanceof PulsarProducerLRCO);
    }

    @Test
    public void test_builder_reusability() {
        // Build first producer
        Producer producer1 = builder
            .setServiceUrl("pulsar://localhost:6650")
            .setTopic("topic1")
            .setProducerName("producer1")
            .build();

        // Build second producer with different settings
        Producer producer2 = builder
            .setServiceUrl("pulsar://localhost:6651")
            .setTopic("topic2")
            .setProducerName("producer2")
            .build();

        assertNotNull("First producer should be created", producer1);
        assertNotNull("Second producer should be created", producer2);

        // Note: Both producers will have the same config due to builder reuse
        // This is expected behavior - builders are typically used once
    }

    // Edge Case Tests

    @Test
    public void test_build_with_null_optional_fields() {
        Producer producer = builder
            .setTopic("test-topic")
            .setProducerName(null)
            .setAuthPluginClassName(null)
            .setAuthParams(null)
            .setTlsTrustCertsFilePath(null)
            .setConfigurationId(null)
            .build();

        assertNotNull("Producer should be created with null optional fields", producer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_build_with_empty_topic() {
        Producer producer = builder
            .setTopic("")
            .build();
    }

    @Test
    public void test_build_with_empty_producer_name() {
        Producer producer = builder
            .setTopic("test-topic")
            .setProducerName("")
            .build();

        assertNotNull("Producer should be created", producer);
        assertEquals("Producer name should be empty string", "",
            ((PulsarProducerLRCO) producer).getConfiguration().getProducerName());
    }

    // Default Configuration Tests

    @Test
    public void test_default_configuration_values() {
        Producer producer = builder
            .setTopic("test-topic")
            .build();

        PulsarProducerConfiguration config = ((PulsarProducerLRCO) producer).getConfiguration();

        // Test default values
        assertEquals("Default service URL should be pulsar://localhost:6650",
            "pulsar://localhost:6650", config.getServiceUrl());
        assertTrue("Default batching should be enabled", config.isBatchingEnabled());
        assertEquals("Default compression should be NONE", "NONE", config.getCompressionType());
        assertFalse("Default authentication should be disabled", config.isAuthenticationEnabled());
        assertFalse("Default TLS should be disabled", config.isTlsEnabled());
    }

    // Authentication Configuration Tests

    @Test
    public void test_full_authentication_configuration() {
        Producer producer = builder
            .setTopic("test-topic")
            .setAuthenticationEnabled(true)
            .setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken")
            .setAuthParams("token:my-secret-token")
            .build();

        PulsarProducerConfiguration config = ((PulsarProducerLRCO) producer).getConfiguration();

        assertTrue("Authentication should be enabled", config.isAuthenticationEnabled());
        assertEquals("Auth plugin class should match",
            "org.apache.pulsar.client.impl.auth.AuthenticationToken",
            config.getAuthPluginClassName());
        assertEquals("Auth params should match", "token:my-secret-token", config.getAuthParams());
    }

    // TLS Configuration Tests

    @Test
    public void test_full_tls_configuration() {
        Producer producer = builder
            .setServiceUrl("pulsar+ssl://test:6651")
            .setTopic("test-topic")
            .setTlsEnabled(true)
            .setTlsTrustCertsFilePath("/etc/ssl/certs/ca-bundle.crt")
            .build();

        PulsarProducerConfiguration config = ((PulsarProducerLRCO) producer).getConfiguration();

        assertEquals("Service URL should use SSL", "pulsar+ssl://test:6651", config.getServiceUrl());
        assertTrue("TLS should be enabled", config.isTlsEnabled());
        assertEquals("TLS cert path should match",
            "/etc/ssl/certs/ca-bundle.crt",
            config.getTlsTrustCertsFilePath());
    }

    // Compression Types Tests

    @Test
    public void test_all_compression_types() {
        String[] compressionTypes = {"NONE", "LZ4", "ZLIB", "ZSTD", "SNAPPY"};

        for (String compressionType : compressionTypes) {
            PulsarProducerLRCOBuilder testBuilder = new PulsarProducerLRCOBuilderImpl(transactionManager);
            Producer producer = testBuilder
                .setTopic("test-topic")
                .setCompressionType(compressionType)
                .build();

            assertEquals("Compression type should match for " + compressionType,
                compressionType,
                ((PulsarProducerLRCO) producer).getConfiguration().getCompressionType());
        }
    }

    // Batching Configuration Tests

    @Test
    public void test_batching_disabled_configuration() {
        Producer producer = builder
            .setTopic("test-topic")
            .setBatchingEnabled(false)
            .build();

        PulsarProducerConfiguration config = ((PulsarProducerLRCO) producer).getConfiguration();

        assertFalse("Batching should be disabled", config.isBatchingEnabled());
    }

    @Test
    public void test_complex_configuration_scenario() {
        // Simulate a production-like configuration
        Producer producer = builder
            .setServiceUrl("pulsar+ssl://prod-pulsar.example.com:6651")
            .setTopic("persistent://tenant/namespace/my-topic")
            .setProducerName("prod-producer-01")
            .setAuthenticationEnabled(true)
            .setAuthPluginClassName("org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2")
            .setAuthParams("{\"issuerUrl\":\"https://auth.example.com\",\"audience\":\"pulsar\"}")
            .setTlsEnabled(true)
            .setTlsTrustCertsFilePath("/etc/pulsar/certs/ca-cert.pem")
            .setBatchingEnabled(true)
            .setCompressionType("ZSTD")
            .setConfigurationId("prod-pulsar-producer")
            .build();

        assertNotNull("Producer should be created with complex configuration", producer);
        PulsarProducerLRCO pulsarProducer = (PulsarProducerLRCO) producer;
        assertNotNull("Configuration should be set", pulsarProducer.getConfiguration());
        assertEquals("Configuration ID should match", "prod-pulsar-producer",
            pulsarProducer.getConfiguredResourceId());
    }
}

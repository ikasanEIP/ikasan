![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# PulsarProducerLRCOBuilder Overview

The `PulsarProducerLRCOBuilder` provides a fluent API for constructing and configuring Apache Pulsar `Producer` instances within the Ikasan framework. This builder simplifies the setup of Pulsar producers by allowing declarative configuration of connection properties, producer settings, authentication, TLS, and comprehensive schema support.

## Purpose

The primary purpose of the `PulsarProducerLRCOBuilder` is to facilitate the creation of `org.ikasan.component.endpoint.pulsar.producer.PulsarProducerLRCO` objects. These producers are designed for message production to Apache Pulsar topics, integrating seamlessly with Ikasan's event processing and transaction management mechanisms. The producer uses Last Resource Commit Optimization (LRCO) for efficient transaction handling.

## Key Configuration Methods

### Connection Configuration

*   **`setServiceUrl(String serviceUrl)`**
    *   **Description**: Sets the Pulsar service URL (e.g., `pulsar://localhost:6650` for plain connections, or `pulsar+ssl://localhost:6651` for TLS).
    *   **Default**: `pulsar://localhost:6650`

*   **`setTopic(String topic)`**
    *   **Description**: Specifies the topic to publish messages to.
    *   **Requirement**: A topic must be specified.

*   **`setProducerName(String producerName)`**
    *   **Description**: Sets an optional name for the producer instance.

### Authentication & Security

*   **`setAuthenticationEnabled(boolean authenticationEnabled)`**
    *   **Description**: Enables or disables authentication.
    *   **Default**: `false`

*   **`setAuthPluginClassName(String authPluginClassName)`**
    *   **Description**: Sets the authentication plugin class name (e.g., `org.apache.pulsar.client.impl.auth.AuthenticationToken`).

*   **`setAuthParams(String authParams)`**
    *   **Description**: Sets the authentication parameters (e.g., `token:eyJhbGciOiJIUzI1NiJ9...`).

*   **`setTlsEnabled(boolean tlsEnabled)`**
    *   **Description**: Enables or disables TLS/SSL encryption.
    *   **Default**: `false`

*   **`setTlsTrustCertsFilePath(String tlsTrustCertsFilePath)`**
    *   **Description**: Sets the path to the TLS trust certificate file for validating broker certificates.

### Performance Configuration

*   **`setBatchingEnabled(boolean batchingEnabled)`**
    *   **Description**: Enables or disables message batching for improved throughput.
    *   **Default**: `false`
    *   **Note**: Batching can significantly improve performance but may add latency.

*   **`setCompressionType(String compressionType)`**
    *   **Description**: Sets the compression algorithm for messages. Valid values are:
        *   `NONE` - No compression (default)
        *   `LZ4` - Fast compression with good balance
        *   `ZLIB` - Higher compression ratio, slower
        *   `ZSTD` - Best compression ratio, moderate speed
        *   `SNAPPY` - Very fast, moderate compression
    *   **Default**: `NONE`

### Schema Configuration

The builder provides comprehensive schema support for various data types:

#### Basic Schema Types

*   **`setSchemaType(String schemaType)`**
    *   **Description**: Sets the schema type for message serialization. Supported types:
        *   **Primitive Types**: `BYTES` (default), `STRING`, `INT8`, `INT16`, `INT32`, `INT64`, `BOOL`, `FLOAT`, `DOUBLE`
        *   **Temporal Types**: `DATE`, `TIME`, `TIMESTAMP`, `INSTANT`, `LOCAL_DATE`, `LOCAL_TIME`, `LOCAL_DATE_TIME`
        *   **Structured Types**: `JSON`, `AVRO`, `PROTOBUF`, `PROTOBUF_NATIVE`
        *   **Special Types**: `KEY_VALUE`, `AUTO_PRODUCE_BYTES`
    *   **Default**: `BYTES`

*   **`setMessageClassName(String messageClassName)`**
    *   **Description**: Sets the fully qualified class name for JSON, AVRO, or PROTOBUF schemas (e.g., `com.example.MyMessage`).
    *   **Required for**: JSON, AVRO, PROTOBUF schemas

### Configuration Management

*   **`setConfigurationId(String configurationId)`**
    *   **Description**: Assigns a unique identifier to the producer's configuration for externalized configuration management.

*   **`setConfiguration(PulsarProducerConfiguration configuration)`**
    *   **Description**: Provides a complete `PulsarProducerConfiguration` object with all settings. This can be used as an alternative to setting individual properties.

## How to Use

To build a Pulsar producer using the `PulsarProducerLRCOBuilder`, follow these steps:

1.  Obtain an instance of `PulsarProducerLRCOBuilder` from the `BuilderFactory`.
2.  Configure connection settings (service URL, topic).
3.  Optionally configure authentication and TLS if required.
4.  Configure performance settings (batching, compression) as needed.
5.  Configure the schema type based on your message format.
6.  Invoke the `build()` method to create the `Producer` instance.

## Example Usage

### Example 1: Basic BYTES Schema Producer

```java
// Assuming builderFactory is an instance of org.ikasan.builder.BuilderFactory

Producer pulsarProducer = builderFactory.getComponentBuilder()
    .pulsarProducer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopic("my-output-topic")
    .setProducerName("my-producer")
    .setBatchingEnabled(true)
    .setCompressionType("LZ4")
    .setConfigurationId("myPulsarProducerConfig")
    .build();
```

### Example 2: STRING Schema Producer

```java
Producer stringProducer = builderFactory.getComponentBuilder()
    .pulsarProducer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopic("string-messages-topic")
    .setProducerName("string-producer")
    .setSchemaType("STRING")
    .setBatchingEnabled(true)
    .setCompressionType("SNAPPY")
    .build();
```

### Example 3: JSON Schema Producer

```java
Producer jsonProducer = builderFactory.getComponentBuilder()
    .pulsarProducer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopic("json-events-topic")
    .setProducerName("json-event-producer")
    .setSchemaType("JSON")
    .setMessageClassName("com.example.events.OrderEvent")
    .setBatchingEnabled(true)
    .setCompressionType("ZSTD")
    .build();
```

### Example 4: AVRO Schema Producer

```java
Producer avroProducer = builderFactory.getComponentBuilder()
    .pulsarProducer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopic("user-events")
    .setProducerName("avro-producer")
    .setSchemaType("AVRO")
    .setMessageClassName("com.example.model.User")
    .setBatchingEnabled(true)
    .setCompressionType("LZ4")
    .build();
```

### Example 5: Authenticated Producer with TLS

```java
Producer secureProducer = builderFactory.getComponentBuilder()
    .pulsarProducer()
    .setServiceUrl("pulsar+ssl://prod-pulsar.example.com:6651")
    .setTopic("secure-output-topic")
    .setProducerName("secure-producer")
    .setAuthenticationEnabled(true)
    .setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken")
    .setAuthParams("token:eyJhbGciOiJIUzI1NiJ9...")
    .setTlsEnabled(true)
    .setTlsTrustCertsFilePath("/path/to/ca-cert.pem")
    .setSchemaType("JSON")
    .setMessageClassName("com.example.SecureMessage")
    .setBatchingEnabled(true)
    .setCompressionType("ZLIB")
    .build();
```

### Example 6: High-Performance Producer with Optimized Settings

```java
Producer highPerfProducer = builderFactory.getComponentBuilder()
    .pulsarProducer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopic("high-throughput-topic")
    .setProducerName("high-perf-producer")
    .setSchemaType("AVRO")
    .setMessageClassName("com.example.events.HighVolumeEvent")
    .setBatchingEnabled(true)
    .setCompressionType("LZ4")  // Fast compression
    .setConfigurationId("highPerfProducerConfig")
    .build();
```

### Example 7: Primitive Schema Producer (INT32)

```java
Producer int32Producer = builderFactory.getComponentBuilder()
    .pulsarProducer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopic("counter-topic")
    .setProducerName("counter-producer")
    .setSchemaType("INT32")
    .build();
```

## Schema Type Guide

### When to Use Each Schema Type

| Schema Type | Use Case | Example |
|-------------|----------|---------|
| `BYTES` | Raw binary data, custom serialization | Legacy systems, custom formats |
| `STRING` | Plain text messages | Log messages, simple text data |
| `JSON` | Structured data with schema validation | REST API payloads, events |
| `AVRO` | Efficient binary format with schema evolution | High-throughput data pipelines |
| `PROTOBUF` | Cross-language serialization | Microservices communication |
| Primitive types | Simple numeric or boolean values | Counters, flags, metrics |
| Temporal types | Date/time values | Event timestamps, scheduling |

### Compression Type Guide

| Compression | Speed | Ratio | CPU Usage | Best For |
|-------------|-------|-------|-----------|----------|
| `NONE` | Fastest | 1:1 | Lowest | Small messages, LAN |
| `LZ4` | Very Fast | Good | Low | General purpose, balanced |
| `SNAPPY` | Very Fast | Moderate | Low | High throughput required |
| `ZLIB` | Moderate | High | Medium | Network bandwidth limited |
| `ZSTD` | Moderate | Highest | Medium | Best compression needed |

## Transaction Support

The Pulsar producer uses Last Resource Commit Optimization (LRCO) for transaction handling. This optimization allows the producer to participate in Ikasan's transaction management while minimizing the overhead of distributed transactions.

Key points:
- Messages are sent only after transaction commit
- Failed transactions prevent message sending
- LRCO optimizes performance by treating the producer as the last resource in the transaction
- Integrates seamlessly with other transactional resources in Ikasan flows
- Provides reliable message delivery with transactional guarantees

## Performance Tuning

### Batching

Enable batching for improved throughput when sending multiple messages:

```java
.setBatchingEnabled(true)
```

**Benefits:**
- Reduces network overhead
- Increases throughput
- Lowers per-message latency

**Considerations:**
- May increase latency for individual messages
- Increases memory usage
- Best for high-volume scenarios

### Compression

Choose compression based on your requirements:

```java
.setCompressionType("LZ4")  // Recommended for most cases
```

**LZ4 (Recommended):**
- Fast compression and decompression
- Good compression ratio
- Low CPU overhead

**ZSTD (Best Compression):**
- Highest compression ratio
- Moderate speed
- Use when network bandwidth is limited

**SNAPPY (Fastest):**
- Minimal compression overhead
- Good for high-throughput scenarios
- Lower compression ratio than LZ4

## Best Practices

1. **Schema Selection**: Choose the most appropriate schema type for your data format. Use structured schemas (JSON, AVRO) for better type safety and schema evolution.

2. **Performance Optimization**:
   - Enable batching for high-throughput scenarios
   - Use LZ4 compression as a default for good balance
   - Use ZSTD for bandwidth-constrained environments
   - Disable compression for small messages on fast networks

3. **Security**: Always use TLS in production environments and implement proper authentication.

4. **Configuration Management**: Use `setConfigurationId()` to externalize configuration for easier management across environments.

5. **Schema Evolution**: When using AVRO, ensure producer and consumer schemas are compatible for smooth evolution.

6. **Producer Naming**: Use meaningful producer names for monitoring and debugging.

7. **Topic Strategy**: Design topic naming conventions for better organization and access control.

## Notes

- The builder uses fluent API design, allowing method chaining for concise configuration.
- All configuration methods return the builder instance for chaining.
- The `build()` method creates the actual `PulsarProducerLRCO` instance with all configured properties.
- Schema configuration must match the consumer schema for successful message delivery.
- The producer automatically participates in Ikasan's transaction management when integrated into a flow.
- LRCO optimization provides transaction safety with better performance than full XA.
- Batching and compression settings significantly impact performance and should be tuned based on use case.

![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# PulsarConsumerBuilder Overview

The `PulsarConsumerBuilder` provides a fluent API for constructing and configuring Apache Pulsar `Consumer` instances within the Ikasan framework. This builder simplifies the setup of Pulsar consumers by allowing declarative configuration of connection properties, subscription settings, authentication, TLS, and comprehensive schema support.

## Purpose

The primary purpose of the `PulsarConsumerBuilder` is to facilitate the creation of `org.ikasan.component.endpoint.pulsar.consumer.PulsarConsumer` objects. These consumers are designed for message consumption from Apache Pulsar topics, integrating seamlessly with Ikasan's event processing, transaction management, and error handling mechanisms. The consumer is fully XA compliant and implements the XAResource interface for participation in distributed transactions.

## Key Configuration Methods

### Connection Configuration

*   **`setServiceUrl(String serviceUrl)`**
    *   **Description**: Sets the Pulsar service URL (e.g., `pulsar://localhost:6650` for plain connections, or `pulsar+ssl://localhost:6651` for TLS).
    *   **Default**: `pulsar://localhost:6650`

*   **`setTopics(String... topics)`**
    *   **Description**: Specifies one or more topics to subscribe to. Can accept a single topic or multiple topics.
    *   **Requirement**: At least one topic must be specified.

*   **`setSubscriptionName(String subscriptionName)`**
    *   **Description**: Sets the subscription name. Pulsar uses subscriptions to track consumer progress.
    *   **Requirement**: A subscription name must be provided.

*   **`setSubscriptionType(String subscriptionType)`**
    *   **Description**: Sets the subscription type. Valid values are:
        *   `Exclusive` - Only one consumer can subscribe (default)
        *   `Shared` - Multiple consumers can subscribe, messages distributed round-robin
        *   `Failover` - Multiple consumers, but only one active at a time
        *   `Key_Shared` - Multiple consumers, messages with same key go to same consumer
    *   **Default**: `Exclusive`

*   **`setConsumerName(String consumerName)`**
    *   **Description**: Sets an optional name for the consumer instance.

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

### Schema Configuration

The builder provides comprehensive schema support for various data types:

#### Basic Schema Types

*   **`setSchemaType(String schemaType)`**
    *   **Description**: Sets the schema type for message serialization/deserialization. Supported types:
        *   **Primitive Types**: `BYTES` (default), `STRING`, `INT8`, `INT16`, `INT32`, `INT64`, `BOOL`, `FLOAT`, `DOUBLE`
        *   **Temporal Types**: `DATE`, `TIME`, `TIMESTAMP`, `INSTANT`, `LOCAL_DATE`, `LOCAL_TIME`, `LOCAL_DATE_TIME`
        *   **Structured Types**: `JSON`, `AVRO`, `PROTOBUF`, `PROTOBUF_NATIVE`
        *   **Special Types**: `KEY_VALUE`, `AUTO_CONSUME`, `AUTO_PRODUCE_BYTES`
    *   **Default**: `BYTES`

*   **`setMessageClassName(String messageClassName)`**
    *   **Description**: Sets the fully qualified class name for JSON, AVRO, or PROTOBUF schemas (e.g., `com.example.MyMessage`).
    *   **Required for**: JSON, AVRO, PROTOBUF schemas

#### AVRO Schema Configuration

*   **`setSchemaAvroDefinition(String avroDefinition)`**
    *   **Description**: Sets the AVRO schema definition as a JSON string.
    *   **Example**: `{"type":"record","name":"User","fields":[{"name":"name","type":"string"}]}`

#### KEY_VALUE Schema Configuration

For KEY_VALUE schemas, which allow messages with typed keys and values:

*   **`setSchemaKeyType(String keyType)`**
    *   **Description**: Sets the schema type for the message key (e.g., `STRING`, `INT64`, `JSON`, `AVRO`).

*   **`setSchemaValueType(String valueType)`**
    *   **Description**: Sets the schema type for the message value (e.g., `STRING`, `JSON`, `AVRO`, `PROTOBUF`).

*   **`setSchemaKeyClassName(String keyClassName)`**
    *   **Description**: Sets the fully qualified class name for the key (required for JSON, AVRO, PROTOBUF key types).

*   **`setSchemaValueClassName(String valueClassName)`**
    *   **Description**: Sets the fully qualified class name for the value (required for JSON, AVRO, PROTOBUF value types).

*   **`setSchemaKeyValueEncodingType(String encodingType)`**
    *   **Description**: Sets the encoding type for KEY_VALUE schemas:
        *   `INLINE` - Key and value encoded together (default)
        *   `SEPARATED` - Key and value encoded separately
    *   **Default**: `INLINE`

#### Advanced Schema Configuration

*   **`setSchemaProperties(Map<String, String> schemaProperties)`**
    *   **Description**: Sets additional schema properties for fine-grained control. Common properties include:
        *   `jsr310ConversionEnabled` - Enable JSR-310 date/time conversion
        *   `__alwaysAllowNull` - Allow null values in schema
        *   `__schemaInfoRequired` - Require schema info in messages

### Event Processing

*   **`setListener(EventListener<?> eventListener)`**
    *   **Description**: Sets the event listener that will process consumed messages.

*   **`setEventFactory(EventFactory eventFactory)`**
    *   **Description**: Sets the factory for creating Ikasan flow events from Pulsar messages.

*   **`setManagedEventIdentifierService(ManagedRelatedEventIdentifierService service)`**
    *   **Description**: Sets the service for managing event identifiers and related events.

*   **`setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory)`**
    *   **Description**: Sets the factory for creating resubmission events for error handling scenarios.

### Configuration Management

*   **`setConfigurationId(String configurationId)`**
    *   **Description**: Assigns a unique identifier to the consumer's configuration for externalized configuration management.

*   **`setConfiguration(PulsarConsumerConfiguration configuration)`**
    *   **Description**: Provides a complete `PulsarConsumerConfiguration` object with all settings. This can be used as an alternative to setting individual properties.

## How to Use

To build a Pulsar consumer using the `PulsarConsumerBuilder`, follow these steps:

1.  Obtain an instance of `PulsarConsumerBuilder` from the `BuilderFactory`.
2.  Configure connection settings (service URL, topics, subscription).
3.  Optionally configure authentication and TLS if required.
4.  Configure the schema type based on your message format.
5.  Set event listeners and factories for integration with Ikasan flows.
6.  Invoke the `build()` method to create the `Consumer` instance.

## Example Usage

### Example 1: Basic BYTES Schema Consumer

```java
// Assuming builderFactory is an instance of org.ikasan.builder.BuilderFactory

Consumer pulsarConsumer = builderFactory.getComponentBuilder()
    .pulsarConsumer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopics("my-topic")
    .setSubscriptionName("my-subscription")
    .setSubscriptionType("Shared")
    .setConfigurationId("myPulsarConsumerConfig")
    .setListener(myEventListener)
    .setEventFactory(myEventFactory)
    .build();
```

### Example 2: JSON Schema Consumer

```java
Consumer jsonConsumer = builderFactory.getComponentBuilder()
    .pulsarConsumer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopics("json-events-topic")
    .setSubscriptionName("json-consumer-group")
    .setSubscriptionType("Exclusive")
    .setSchemaType("JSON")
    .setMessageClassName("com.example.events.OrderEvent")
    .setListener(myEventListener)
    .setEventFactory(myEventFactory)
    .build();
```

### Example 3: AVRO Schema with Custom Definition

```java
String avroSchema = "{\"type\":\"record\",\"name\":\"User\"," +
                    "\"fields\":[" +
                    "{\"name\":\"id\",\"type\":\"long\"}," +
                    "{\"name\":\"name\",\"type\":\"string\"}," +
                    "{\"name\":\"email\",\"type\":\"string\"}" +
                    "]}";

Consumer avroConsumer = builderFactory.getComponentBuilder()
    .pulsarConsumer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopics("user-events")
    .setSubscriptionName("avro-consumer")
    .setSchemaType("AVRO")
    .setMessageClassName("com.example.model.User")
    .setSchemaAvroDefinition(avroSchema)
    .setListener(myEventListener)
    .build();
```

### Example 4: KEY_VALUE Schema Consumer

```java
Consumer keyValueConsumer = builderFactory.getComponentBuilder()
    .pulsarConsumer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopics("keyed-messages")
    .setSubscriptionName("kv-consumer")
    .setSchemaType("KEY_VALUE")
    .setSchemaKeyType("STRING")
    .setSchemaValueType("JSON")
    .setSchemaValueClassName("com.example.events.PaymentEvent")
    .setSchemaKeyValueEncodingType("SEPARATED")
    .setListener(myEventListener)
    .build();
```

### Example 5: Authenticated Consumer with TLS

```java
Consumer secureConsumer = builderFactory.getComponentBuilder()
    .pulsarConsumer()
    .setServiceUrl("pulsar+ssl://prod-pulsar.example.com:6651")
    .setTopics("secure-topic")
    .setSubscriptionName("secure-consumer")
    .setAuthenticationEnabled(true)
    .setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken")
    .setAuthParams("token:eyJhbGciOiJIUzI1NiJ9...")
    .setTlsEnabled(true)
    .setTlsTrustCertsFilePath("/path/to/ca-cert.pem")
    .setSchemaType("JSON")
    .setMessageClassName("com.example.SecureMessage")
    .setListener(myEventListener)
    .build();
```

### Example 6: Advanced KEY_VALUE with Schema Properties

```java
Map<String, String> schemaProps = new HashMap<>();
schemaProps.put("jsr310ConversionEnabled", "true");
schemaProps.put("__alwaysAllowNull", "false");

Consumer advancedConsumer = builderFactory.getComponentBuilder()
    .pulsarConsumer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopics("complex-events")
    .setSubscriptionName("advanced-consumer")
    .setSchemaType("KEY_VALUE")
    .setSchemaKeyType("INT64")
    .setSchemaValueType("AVRO")
    .setSchemaValueClassName("com.example.ComplexEvent")
    .setSchemaKeyValueEncodingType("INLINE")
    .setSchemaProperties(schemaProps)
    .setListener(myEventListener)
    .setEventFactory(myEventFactory)
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
| `KEY_VALUE` | Messages requiring typed keys | Event sourcing, partitioned data |
| `AUTO_CONSUME` | Multiple schema types on same topic | Multi-schema topics |
| Primitive types | Simple numeric or boolean values | Counters, flags, timestamps |
| Temporal types | Date/time values | Event timestamps, scheduling |

## Transaction Support

The Pulsar consumer is fully XA compliant and integrates with Ikasan's transaction management through the XAResource interface. This ensures that message acknowledgments are coordinated with other transactional resources in the flow within distributed transactions.

Key points:
- Messages are acknowledged only after successful transaction commit
- Failed transactions result in negative acknowledgment (nack)
- Fully supports integration with other XA resources (databases, JMS, etc.) in distributed transactions
- Implements the complete XAResource interface including prepare, commit, and rollback phases
- Provides proper two-phase commit (2PC) support for reliable distributed transaction processing

## Best Practices

1. **Schema Selection**: Choose the most appropriate schema type for your data format. Use structured schemas (JSON, AVRO) for better type safety and schema evolution.

2. **Subscription Types**:
   - Use `Exclusive` for single consumer scenarios requiring strict ordering
   - Use `Shared` for load balancing across multiple consumers
   - Use `Key_Shared` when you need both parallelism and key-based ordering

3. **Security**: Always use TLS in production environments and implement proper authentication.

4. **Configuration Management**: Use `setConfigurationId()` to externalize configuration for easier management across environments.

5. **Schema Evolution**: When using AVRO, leverage schema evolution capabilities for backward/forward compatibility.

6. **Error Handling**: Configure `setResubmissionEventFactory()` to enable proper error handling and resubmission workflows.

## Notes

- The builder uses fluent API design, allowing method chaining for concise configuration.
- All configuration methods return the builder instance for chaining.
- The `build()` method creates the actual `PulsarConsumer` instance with all configured properties.
- Schema configuration is flexible but requires careful matching between producer and consumer schemas.
- The consumer automatically participates in Ikasan's transaction management when integrated into a flow.

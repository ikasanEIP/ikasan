![Ikasan](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Sample - Apache Pulsar Integration

This sample demonstrates how to use the Ikasan Pulsar endpoints for consuming and producing messages with Apache Pulsar.

## Overview

This sample provides a simple flow that:
1. Consumes messages from a Pulsar topic
2. Produces messages to another Pulsar topic

The sample uses TestContainers to run a Pulsar broker during tests, making it easy to run without external dependencies.

## Architecture

```
[Pulsar Inbound Topic] --> [Pulsar Consumer] --> [Pulsar Producer] --> [Pulsar Outbound Topic]
```

The sample demonstrates multiple flows with different schema types:
- **BYTES Schema Flow** (default): Raw byte array messages
- **STRING Schema Flow**: Text messages with UTF-8 encoding
- **INT32 Schema Flow**: Integer primitive messages
- **JSON Schema Flow**: Complex objects serialized as JSON
- **AVRO Schema Flow**: Complex objects with binary AVRO serialization

## Supported Schema Types

The Ikasan Pulsar endpoints support the following schema types:

### Primitive Schemas
- **BYTES**: Raw byte arrays (default)
- **STRING**: UTF-8 encoded strings
- **INT8**: 8-bit signed integers (byte)
- **INT16**: 16-bit signed integers (short)
- **INT32**: 32-bit signed integers (int)
- **INT64**: 64-bit signed integers (long)
- **BOOL**: Boolean values
- **FLOAT**: 32-bit floating point
- **DOUBLE**: 64-bit floating point

### Complex Schemas
- **JSON**: JSON-encoded objects (requires message class name)
- **AVRO**: Apache Avro binary format (requires message class name)
- **PROTOBUF**: Protocol Buffers (requires message class name)
- **PROTOBUF_NATIVE**: Native Protocol Buffers format

### Temporal Schemas
- **DATE**: java.util.Date
- **TIME**: java.sql.Time
- **TIMESTAMP**: java.sql.Timestamp
- **INSTANT**: java.time.Instant
- **LOCAL_DATE**: java.time.LocalDate
- **LOCAL_TIME**: java.time.LocalTime
- **LOCAL_DATE_TIME**: java.time.LocalDateTime

### Advanced Schemas
- **KEY_VALUE**: Composite key-value pairs
- **AUTO_CONSUME**: Automatic schema detection for consumers
- **AUTO_PRODUCE_BYTES**: Automatic byte serialization for producers

## Components

### Pulsar Consumer
- Consumes messages from `test-inbound-topic`
- Uses Shared subscription type for load balancing
- Configured with XA transaction support

### Pulsar Producer  
- Produces messages to `test-outbound-topic`
- Configured with batching enabled for performance
- Uses LZ4 compression
- Last Resource Commit Optimization (LRCO) for transactions

## Configuration

### Consumer Configuration
```java
builderFactory.getComponentBuilder().pulsarConsumer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopics(inboundTopic)
    .setSubscriptionName(subscriptionName)
    .setSubscriptionType("Shared")
    .setConfigurationId("pulsarConsumer")
    .build();
```

### Producer Configuration
```java
builderFactory.getComponentBuilder().pulsarProducer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopic(outboundTopic)
    .setProducerName("sample-producer")
    .setBatchingEnabled(true)
    .setCompressionType("LZ4")
    .setConfigurationId("pulsarProducer")
    .build();
```

## Running the Sample

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Docker (for TestContainers)

### Build
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

The tests will:
1. Start a Pulsar broker using TestContainers
2. Start the Ikasan flow
3. Send test messages to the inbound topic
4. Verify messages are received on the outbound topic
5. Clean up resources

## Test Cases

### Basic Flow Tests

#### test_Pulsar_Sample_Flow
Basic test that sends a single message through the flow and verifies it arrives at the destination.
- Uses default BYTES schema
- Tests basic consumer → broker → producer flow

#### test_Pulsar_Flow_Multiple_Messages
Tests the flow with multiple messages to verify throughput and message ordering.
- Sends 10 messages
- Verifies all messages are received

#### test_Pulsar_Flow_With_Compressed_Messages
Tests that message compression works correctly by sending a large message.
- Creates a large message (repeated 100 times)
- Verifies LZ4 compression works

### Schema Variant Tests

The sample demonstrates comprehensive Pulsar schema support with dedicated flows for different schema types:

#### test_string_schema_flow
Tests STRING schema serialization through the complete Ikasan flow.
- Flow: String Consumer → Brokers → String Producer
- Schema: STRING
- Message: Simple string messages
- Validates string content preservation

#### test_int32_schema_flow
Tests INT32 primitive schema for integer messages.
- Flow: INT32 Consumer → Brokers → INT32 Producer
- Schema: INT32
- Message: Integer values
- Validates numeric type handling

#### test_json_schema_flow
Tests JSON schema with complex object serialization.
- Flow: JSON Consumer → Brokers → JSON Producer
- Schema: JSON
- Message: TestMessage POJO (id, content, timestamp)
- Validates complete object serialization/deserialization

#### test_avro_schema_flow
Tests AVRO schema with binary serialization.
- Flow: AVRO Consumer → Brokers → AVRO Producer
- Schema: AVRO
- Message: TestMessage POJO
- Validates AVRO binary format handling

### Error Handling & Recovery Tests

#### test_exclusion
Tests exclusion handling when errors occur in the flow.
- Forces an exclusion exception in the broker
- Verifies error is stored in error reporting service
- Verifies exclusion is stored in exclusion management service
- Validates message is NOT delivered to outbound topic

#### test_exclusion_followed_by_resubmission
Tests the complete exclusion and resubmission cycle.
- Creates an exclusion
- Resubmits the excluded event via hospital service
- Verifies message is successfully processed after resubmission

#### test_exclusion_followed_by_ignore
Tests ignoring excluded events.
- Creates an exclusion
- Ignores the excluded event via hospital service
- Verifies exclusion is removed but message is not reprocessed

#### test_flow_in_recovery
Tests flow recovery behavior on recoverable errors.
- Forces a recoverable exception
- Verifies flow enters "recovering" state
- Validates error reporting
- Confirms message is not delivered during recovery

#### test_flow_in_scheduled_recovery
Tests scheduled recovery with cron-based retry.
- Forces a scheduled recovery exception
- Verifies flow enters "recovering" state
- Tests cron-based retry (0/10 * * * * ?)
- Validates automatic recovery when error is resolved

#### test_flow_stopped_in_error
Tests flow behavior when critical errors occur.
- Forces a stop-level exception
- Verifies flow enters "stoppedInError" state
- Validates error is recorded
- Confirms message is not delivered

#### test_transaction_timeout_stopped_in_error
Tests transaction timeout handling.
- Introduces 10-second delay exceeding transaction timeout
- Verifies timeout causes error
- Tests transaction rollback behavior

## Pulsar Features Demonstrated

- **Shared Subscriptions**: Multiple consumers can share message processing
- **Message Batching**: Producer batches messages for better performance
- **Compression**: LZ4 compression reduces network bandwidth
- **XA Transactions**: Full ACID transaction support for consumer
- **LRCO**: Last Resource Commit Optimization for producer
- **Schema Support**: Comprehensive schema types including BYTES, STRING, INT32, JSON, AVRO
- **Schema Evolution**: Support for schema versioning and compatibility
- **Type Safety**: Strongly-typed message serialization/deserialization
- **Error Handling**: Exclusion, resubmission, and recovery patterns
- **Transaction Timeout**: Configurable transaction timeout with rollback

## Configuration Properties

Key configuration properties in `application.properties`:

```properties
# Pulsar broker URL
pulsar.service.url=pulsar://localhost:6650

# Topic names
pulsar.inbound.topic=test-inbound-topic
pulsar.outbound.topic=test-outbound-topic

# Subscription name for consumer
pulsar.subscription.name=test-subscription

# Transaction timeout
ikasan.default.transaction.timeout.seconds=30
```

## Extending the Sample

### Adding Schema Support

#### STRING Schema
```java
// Consumer
builderFactory.getComponentBuilder().pulsarConsumer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopics("my-topic")
    .setSubscriptionName("my-subscription")
    .setSchemaType("STRING")
    .build();

// Producer
builderFactory.getComponentBuilder().pulsarProducer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopic("my-topic")
    .setSchemaType("STRING")
    .build();
```

#### JSON Schema
```java
// Consumer
builderFactory.getComponentBuilder().pulsarConsumer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopics("my-topic")
    .setSubscriptionName("my-subscription")
    .setSchemaType("JSON")
    .setMessageClassName("com.example.MyMessage")
    .build();

// Producer
builderFactory.getComponentBuilder().pulsarProducer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopic("my-topic")
    .setSchemaType("JSON")
    .setMessageClassName("com.example.MyMessage")
    .build();
```

#### AVRO Schema
```java
// Consumer
builderFactory.getComponentBuilder().pulsarConsumer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopics("my-topic")
    .setSubscriptionName("my-subscription")
    .setSchemaType("AVRO")
    .setMessageClassName("com.example.MyMessage")
    .build();

// Producer
builderFactory.getComponentBuilder().pulsarProducer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopic("my-topic")
    .setSchemaType("AVRO")
    .setMessageClassName("com.example.MyMessage")
    .build();
```

#### Primitive Schemas (INT32, INT64, BOOL, FLOAT, DOUBLE)
```java
builderFactory.getComponentBuilder().pulsarConsumer()
    .setServiceUrl(pulsarServiceUrl)
    .setTopics("my-topic")
    .setSubscriptionName("my-subscription")
    .setSchemaType("INT32")  // or INT64, BOOL, FLOAT, DOUBLE
    .build();
```

### Adding Authentication
```java
.setAuthenticationEnabled(true)
.setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken")
.setAuthParams("token:your-token-here")
```

### Adding TLS
```java
.setTlsEnabled(true)
.setTlsTrustCertsFilePath("/path/to/ca-cert.pem")
```

### Changing Subscription Type
```java
.setSubscriptionType("Exclusive")  // or "Failover", "Key_Shared"
```

### Adjusting Compression
```java
.setCompressionType("ZSTD")  // or "LZ4", "ZLIB", "SNAPPY", "NONE"
```

## Troubleshooting

### Docker Issues
If TestContainers fails to start Pulsar:
- Ensure Docker is running
- Check Docker has sufficient resources allocated
- Check firewall settings

### Connection Issues
If you see connection refused errors:
- Verify Pulsar broker is running
- Check the service URL configuration
- Verify network connectivity

### Performance Issues
For better performance:
- Increase batch size on producer
- Use multiple consumer threads
- Enable compression
- Tune receiver queue size

## References

- [Ikasan Documentation](https://github.com/ikasanEIP/ikasan)
- [Apache Pulsar Documentation](https://pulsar.apache.org/docs/)
- [Pulsar Java Client](https://pulsar.apache.org/docs/client-libraries-java/)
- [TestContainers Pulsar Module](https://www.testcontainers.org/modules/pulsar/)

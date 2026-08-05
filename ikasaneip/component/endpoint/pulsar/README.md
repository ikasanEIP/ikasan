[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Pulsar Component

This module provides Apache Pulsar integration for Ikasan, enabling message consumption and production with full XA transaction support.

## Overview

The Pulsar component provides:
- **PulsarConsumer** - Consumes messages from Pulsar topics with XA transaction support
- **PulsarProducerLRCO** - Produces messages to Pulsar topics with Last Resource Commit Optimization (LRCO)

Both components are fully integrated with Ikasan's transaction management and recovery framework.

## Maven Dependency

```xml
<dependency>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-pulsar-endpoint</artifactId>
    <version>${ikasan.version}</version>
</dependency>
```

## PulsarConsumer

The `PulsarConsumer` is a managed consumer that receives messages from one or more Pulsar topics and participates in XA transactions.

### Basic Configuration

```java
@Bean
public Consumer pulsarConsumer(TransactionManager transactionManager) {
    PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
    config.setServiceUrl("pulsar://localhost:6650");
    config.setTopics(new String[]{"my-topic"});
    config.setSubscriptionName("my-subscription");
    config.setSubscriptionType("Exclusive");

    PulsarConsumer consumer = new PulsarConsumer(transactionManager);
    consumer.setConfiguration(config);
    consumer.setConfiguredResourceId("myPulsarConsumer");

    return consumer;
}
```

### Configuration Options

#### Connection Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `serviceUrl` | String | `pulsar://localhost:6650` | Pulsar broker service URL |
| `authenticationEnabled` | boolean | `false` | Enable authentication |
| `authPluginClassName` | String | `null` | Authentication plugin class name (e.g., `org.apache.pulsar.client.impl.auth.AuthenticationToken`) |
| `authParams` | String | `null` | Authentication parameters as string |
| `tlsEnabled` | boolean | `false` | Enable TLS/SSL |
| `tlsTrustCertsFilePath` | String | `null` | Path to TLS trust certificates file |

#### Subscription Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `topics` | String[] | `null` | Array of topic names to subscribe to |
| `subscriptionName` | String | `null` | Subscription name (required) |
| `subscriptionType` | String | `Exclusive` | Subscription type: `Exclusive`, `Shared`, `Failover`, `Key_Shared` |
| `subscriptionInitialPosition` | String | `Latest` | Initial position: `Latest`, `Earliest` |
| `subscriptionProperties` | String | `null` | Subscription properties as JSON string |
| `replicateSubscriptionState` | boolean | `false` | Replicate subscription state across clusters |

#### Consumer Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `consumerName` | String | `null` | Consumer name |
| `consumerEventListener` | String | `null` | Consumer event listener class name |
| `messageListenerThreads` | int | `1` | Number of threads for message listener executor |
| `priorityLevel` | int | `0` | Priority level for shared subscription consumers (higher value = higher priority) |

#### Queue & Flow Control Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `receiverQueueSize` | int | `1000` | Size of the consumer receive queue |
| `maxTotalReceiverQueueSizeAcrossPartitions` | int | `50000` | Max total receiver queue size across all partitions |
| `autoScaleReceiverQueueSizeEnabled` | boolean | `false` | Automatically scale receiver queue size based on consumer throughput |

#### Acknowledgment Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `autoAcknowledge` | boolean | `false` | Automatically acknowledge messages (disable for XA transaction control) |
| `ackTimeoutMillis` | long | `0` | Acknowledgment timeout in milliseconds (0 = disabled) |
| `ackTimeoutTickTimeMillis` | long | `1000` | Tick time for acknowledgment timeout redelivery |
| `acknowledgementGroupTimeMillis` | long | `100` | Time to group acknowledgments together |
| `ackReceiptEnabled` | boolean | `false` | Enable acknowledgment receipt (adds network overhead) |
| `negativeAckRedeliveryDelay` | long | `1000` | Delay before redelivering negatively acknowledged messages (ms) |

#### Batch Processing Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `batchIndexAckEnabled` | boolean | `false` | Enable batch index acknowledgment |
| `batchReceiveEnabled` | boolean | `false` | Enable batch receive |

#### Chunked Messages Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `autoAckOldestChunkedMessageOnQueueFull` | boolean | `false` | Auto-acknowledge oldest chunked message when queue is full |
| `maxPendingChunkedMessage` | int | `10` | Maximum number of pending chunked messages |
| `expireTimeOfIncompleteChunkedMessageMillis` | long | `60000` | Expiration time for incomplete chunked messages (ms) |

#### Topic Discovery Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `topicsPattern` | String | `null` | Regex pattern for topic discovery |
| `patternAutoDiscoveryPeriod` | boolean | `false` | Enable automatic topic pattern discovery |
| `autoDiscoveryPeriodMinutes` | int | `1` | Auto discovery period in minutes |

#### Dead Letter Policy Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `retryEnable` | boolean | `false` | Enable retry mechanism |
| `deadLetterTopic` | String | `null` | Dead letter topic name |
| `maxRedeliverCount` | int | `0` | Maximum redelivery count before moving to dead letter topic |

#### Advanced Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `readCompacted` | boolean | `false` | Read only compacted messages |
| `startMessageIdInclusive` | boolean | `false` | Include start message ID in consumption |
| `poolMessages` | boolean | `true` | Pool message objects for better performance |
| `properties` | String | `null` | Consumer properties as JSON string |
| `cryptoKeyReaderClassName` | String | `null` | Crypto key reader class name for message decryption |

#### Schema Configuration

Pulsar supports multiple schema types for message serialization and deserialization. The consumer can be configured to use different schemas based on your message format.

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `schemaType` | String | `BYTES` | Schema type: `BYTES`, `STRING`, `JSON`, `AVRO`, `PROTOBUF`, `PROTOBUF_NATIVE`, `KEY_VALUE`, `AUTO_CONSUME`, `AUTO_PRODUCE_BYTES`, or primitive types (`INT8`, `INT16`, `INT32`, `INT64`, `BOOL`, `FLOAT`, `DOUBLE`) and temporal types (`DATE`, `TIME`, `TIMESTAMP`, `INSTANT`, `LOCAL_DATE`, `LOCAL_TIME`, `LOCAL_DATE_TIME`) |
| `messageClassName` | String | `null` | Fully qualified class name for JSON, AVRO, or PROTOBUF schemas |
| `avroSchemaDefinition` | String | `null` | AVRO schema definition (JSON string) |
| `keySchemaType` | String | `null` | Schema type for keys in KEY_VALUE schema |
| `valueSchemaType` | String | `null` | Schema type for values in KEY_VALUE schema |
| `keyClassName` | String | `null` | Class name for keys in KEY_VALUE schema |
| `valueClassName` | String | `null` | Class name for values in KEY_VALUE schema |
| `keyValueEncodingType` | String | `INLINE` | KEY_VALUE encoding: `INLINE` or `SEPARATED` |
| `schemaVersion` | byte[] | `null` | Schema version for schema registry |
| `schemaProperties` | String | `null` | Additional schema metadata as JSON string |

##### Schema Examples

**BYTES Schema (Default)**
```java
// No configuration needed - BYTES is the default
PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
config.setServiceUrl("pulsar://localhost:6650");
config.setTopics(new String[]{"my-topic"});
config.setSubscriptionName("my-subscription");
```

**STRING Schema**
```java
PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
config.setServiceUrl("pulsar://localhost:6650");
config.setTopics(new String[]{"my-topic"});
config.setSubscriptionName("my-subscription");

// Configure schema
PulsarSchemaConfiguration schemaConfig = new PulsarSchemaConfiguration();
schemaConfig.setSchemaType("STRING");
config.setSchemaConfiguration(schemaConfig);
```

**JSON Schema**
```java
PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
config.setServiceUrl("pulsar://localhost:6650");
config.setTopics(new String[]{"my-topic"});
config.setSubscriptionName("my-subscription");

// Configure JSON schema
PulsarSchemaConfiguration schemaConfig = new PulsarSchemaConfiguration();
schemaConfig.setSchemaType("JSON");
schemaConfig.setMessageClassName("com.example.MyMessage");
config.setSchemaConfiguration(schemaConfig);
```

**AVRO Schema**
```java
PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
config.setServiceUrl("pulsar://localhost:6650");
config.setTopics(new String[]{"my-topic"});
config.setSubscriptionName("my-subscription");

// Configure AVRO schema
PulsarSchemaConfiguration schemaConfig = new PulsarSchemaConfiguration();
schemaConfig.setSchemaType("AVRO");
schemaConfig.setMessageClassName("com.example.AvroMessage");
config.setSchemaConfiguration(schemaConfig);
```

**KEY_VALUE Schema**
```java
PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
config.setServiceUrl("pulsar://localhost:6650");
config.setTopics(new String[]{"my-topic"});
config.setSubscriptionName("my-subscription");

// Configure KEY_VALUE schema
PulsarSchemaConfiguration schemaConfig = new PulsarSchemaConfiguration();
schemaConfig.setSchemaType("KEY_VALUE");
schemaConfig.setKeySchemaType("STRING");
schemaConfig.setValueSchemaType("JSON");
schemaConfig.setValueClassName("com.example.ValueMessage");
schemaConfig.setKeyValueEncodingType("INLINE");
config.setSchemaConfiguration(schemaConfig);
```

**Using Builder Pattern for Schema Configuration**
```java
Consumer consumer = builderFactory.getComponentBuilder().pulsarConsumer()
    .setServiceUrl("pulsar://localhost:6650")
    .setTopics("my-topic")
    .setSubscriptionName("my-subscription")
    .setSchemaType("JSON")
    .setMessageClassName("com.example.MyMessage")
    .setConfigurationId("myConsumer")
    .build();
```

### Example: Full Configuration

```java
PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();

// Connection
config.setServiceUrl("pulsar+ssl://pulsar.example.com:6651");
config.setTlsEnabled(true);
config.setTlsTrustCertsFilePath("/path/to/ca-cert.pem");
config.setAuthenticationEnabled(true);
config.setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken");
config.setAuthParams("token:eyJhbGciOiJIUzI1NiJ9...");

// Subscription
config.setTopics(new String[]{"persistent://tenant/namespace/topic1", "topic2"});
config.setSubscriptionName("my-service-subscription");
config.setSubscriptionType("Shared");
config.setSubscriptionInitialPosition("Earliest");

// Consumer
config.setConsumerName("consumer-01");
config.setMessageListenerThreads(5);
config.setPriorityLevel(1);

// Queue & Flow Control
config.setReceiverQueueSize(2000);
config.setMaxTotalReceiverQueueSizeAcrossPartitions(100000);
config.setAutoScaleReceiverQueueSizeEnabled(true);

// Acknowledgment
config.setAckTimeoutMillis(30000);
config.setNegativeAckRedeliveryDelay(5000);
config.setAcknowledgementGroupTimeMillis(200);

// Dead Letter Policy
config.setRetryEnable(true);
config.setDeadLetterTopic("my-topic-dlq");
config.setMaxRedeliverCount(10);

// Advanced
config.setReadCompacted(true);
config.setBatchIndexAckEnabled(true);
```

## PulsarProducerLRCO

The `PulsarProducerLRCO` is a managed producer that sends messages to a Pulsar topic with Last Resource Commit Optimization for XA transactions.

### Basic Configuration

```java
@Bean
public Producer pulsarProducer(TransactionManager transactionManager) {
    PulsarProducerConfiguration config = new PulsarProducerConfiguration();
    config.setServiceUrl("pulsar://localhost:6650");
    config.setTopic("my-topic");
    config.setProducerName("my-producer");

    PulsarProducerLRCO producer = new PulsarProducerLRCO(transactionManager, config);
    producer.setConfiguredResourceId("myPulsarProducer");

    return producer;
}
```

### Configuration Options

#### Connection Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `serviceUrl` | String | `pulsar://localhost:6650` | Pulsar broker service URL |
| `authenticationEnabled` | boolean | `false` | Enable authentication |
| `authPluginClassName` | String | `null` | Authentication plugin class name |
| `authParams` | String | `null` | Authentication parameters as string |
| `tlsEnabled` | boolean | `false` | Enable TLS/SSL |
| `tlsTrustCertsFilePath` | String | `null` | Path to TLS trust certificates file |

#### Producer Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `topic` | String | `null` | Topic name to publish to (required) |
| `producerName` | String | `null` | Producer name |
| `initialSequenceId` | Long | `null` | Initial sequence ID for messages |
| `accessMode` | String | `Shared` | Producer access mode: `Shared`, `Exclusive`, `WaitForExclusive` |
| `multiSchema` | boolean | `true` | Enable multi-schema support |

#### Batching Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `batchingEnabled` | boolean | `true` | Enable message batching |
| `batchingMaxMessages` | int | `1000` | Maximum number of messages in a batch |
| `batchingMaxPublishDelayMillis` | long | `10` | Maximum time to wait before sending a batch (ms) |
| `batchingMaxBytes` | int | `131072` (128KB) | Maximum batch size in bytes |
| `batchingPartitionSwitchFrequencyByPublishDelay` | int | `10` | Frequency of partition switches in batch mode |

#### Compression & Encoding Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `compressionType` | String | `NONE` | Compression type: `NONE`, `LZ4`, `ZLIB`, `ZSTD`, `SNAPPY` |
| `chunkingEnabled` | boolean | `false` | Enable chunking for large messages |

#### Queue & Flow Control Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `sendTimeoutMillis` | int | `30000` | Send timeout in milliseconds |
| `blockIfQueueFull` | boolean | `false` | Block if message queue is full (vs throwing exception) |
| `maxPendingMessages` | int | `1000` | Maximum pending messages for a single producer |
| `maxPendingMessagesAcrossPartitions` | int | `50000` | Maximum pending messages across all partitions |

#### Routing & Partitioning Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `messageRoutingMode` | String | `RoundRobinPartition` | Message routing mode: `SinglePartition`, `RoundRobinPartition`, `CustomPartition` |
| `hashingScheme` | String | `JavaStringHash` | Hashing scheme for message keys: `JavaStringHash`, `Murmur3_32Hash` |
| `roundRobinRouterBatchingPartitionSwitchFrequency` | int | `10` | Frequency to switch partitions in round-robin mode |
| `autoUpdatePartitions` | boolean | `true` | Automatically update partitions when topic is scaled |
| `autoUpdatePartitionsIntervalSeconds` | int | `60` | Interval to check for partition updates (seconds) |
| `lazyStartPartitionedProducers` | boolean | `false` | Lazily start partition producers (only when needed) |

#### Advanced Settings

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `producerProperties` | Map<String, Object> | `{}` | Additional producer properties |

### Example: Full Configuration

```java
PulsarProducerConfiguration config = new PulsarProducerConfiguration();

// Connection
config.setServiceUrl("pulsar+ssl://pulsar.example.com:6651");
config.setTlsEnabled(true);
config.setTlsTrustCertsFilePath("/path/to/ca-cert.pem");
config.setAuthenticationEnabled(true);
config.setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken");
config.setAuthParams("token:eyJhbGciOiJIUzI1NiJ9...");

// Producer
config.setTopic("persistent://tenant/namespace/my-topic");
config.setProducerName("producer-01");
config.setInitialSequenceId(1000L);
config.setAccessMode("Exclusive");
config.setMultiSchema(false);

// Batching
config.setBatchingEnabled(true);
config.setBatchingMaxMessages(500);
config.setBatchingMaxPublishDelayMillis(50);
config.setBatchingMaxBytes(256 * 1024); // 256KB

// Compression
config.setCompressionType("ZSTD");
config.setChunkingEnabled(true);

// Queue & Flow Control
config.setSendTimeoutMillis(60000);
config.setBlockIfQueueFull(true);
config.setMaxPendingMessages(2000);
config.setMaxPendingMessagesAcrossPartitions(100000);

// Routing & Partitioning
config.setMessageRoutingMode("RoundRobinPartition");
config.setHashingScheme("Murmur3_32Hash");
config.setRoundRobinRouterBatchingPartitionSwitchFrequency(5);
config.setAutoUpdatePartitions(true);
config.setAutoUpdatePartitionsIntervalSeconds(30);
config.setLazyStartPartitionedProducers(true);
```

## XA Transaction Support

Both the consumer and producer participate in XA transactions managed by Ikasan's transaction manager.

### Consumer XA Behavior

- Messages are enlisted in the transaction when received
- On **commit**: Message is acknowledged to Pulsar
- On **rollback**: Message is negatively acknowledged (will be redelivered)

### Producer XA Behavior (LRCO)

- Uses Last Resource Commit Optimization pattern
- Message is staged when `invoke()` is called
- On **commit**: Message is sent to Pulsar
- On **rollback**: Message is discarded

### Example Flow Configuration

```java
@Bean
public Flow pulsarToJmsFlow(ModuleBuilder moduleBuilder,
                            Consumer pulsarConsumer,
                            Producer jmsProducer) {
    FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Pulsar-to-JMS");

    return flowBuilder
        .withDescription("Consumes from Pulsar and produces to JMS")
        .consumer("Pulsar Consumer", pulsarConsumer)
        .producer("JMS Producer", jmsProducer)
        .build();
}
```

## Authentication Examples

### Token Authentication

```java
config.setAuthenticationEnabled(true);
config.setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationToken");
config.setAuthParams("token:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9...");
```

### TLS Authentication

```java
config.setAuthenticationEnabled(true);
config.setAuthPluginClassName("org.apache.pulsar.client.impl.auth.AuthenticationTls");
config.setAuthParams("{\"tlsCertFile\":\"/path/to/client-cert.pem\",\"tlsKeyFile\":\"/path/to/client-key.pem\"}");
```

### OAuth 2.0 Authentication

```java
config.setAuthenticationEnabled(true);
config.setAuthPluginClassName("org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2");
config.setAuthParams("{\"issuerUrl\":\"https://issuer.example.com\",\"audience\":\"pulsar\",\"privateKey\":\"file:///path/to/key.json\"}");
```

## TLS/SSL Configuration

```java
// Consumer or Producer
config.setTlsEnabled(true);
config.setTlsTrustCertsFilePath("/path/to/ca-cert.pem");
config.setServiceUrl("pulsar+ssl://pulsar.example.com:6651");
```

## Best Practices

### Consumer Best Practices

1. **Use Shared subscriptions** for load balancing across multiple consumers
2. **Enable dead letter policy** for handling failed messages
3. **Set appropriate receiver queue size** based on message rate and size
4. **Use negative ack redelivery delay** to prevent immediate redelivery on failures
5. **Enable batch index acknowledgment** when consuming batched messages
6. **Use acknowledgment timeout** to detect stuck consumers

### Producer Best Practices

1. **Enable batching** for high-throughput scenarios
2. **Use compression** (ZSTD or LZ4) to reduce network bandwidth
3. **Set appropriate send timeout** based on network latency
4. **Use block if queue full** to implement backpressure
5. **Enable chunking** for large messages (> 5MB)
6. **Use exclusive access mode** when ordering guarantees are required

### Performance Tuning

1. **Increase receiver queue size** for high-throughput consumers
2. **Use multiple message listener threads** for parallel processing
3. **Enable auto-scale receiver queue** for variable load
4. **Tune batching parameters** for optimal throughput vs latency
5. **Use round-robin routing** for balanced partition distribution
6. **Enable lazy start** for topics with many partitions

## Troubleshooting

### Consumer Not Receiving Messages

1. Check subscription type matches your use case
2. Verify subscription position (Latest vs Earliest)
3. Check topic name and namespace
4. Verify authentication and authorization
5. Check receiver queue size isn't too small

### Producer Send Failures

1. Check send timeout configuration
2. Verify topic exists and is accessible
3. Check max pending messages limits
4. Verify compression type is supported
5. Check authentication and TLS settings

### Transaction Rollbacks

1. Check acknowledgment timeout settings
2. Verify negative ack redelivery delay
3. Review dead letter policy configuration
4. Check max redelivery count

## References

- [Apache Pulsar Documentation](https://pulsar.apache.org/docs/)
- [Pulsar Java Client](https://pulsar.apache.org/docs/client-libraries-java/)
- [Ikasan Documentation](https://github.com/ikasanEIP/ikasan)

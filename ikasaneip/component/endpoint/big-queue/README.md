[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Big Queue Component

This module provides persistent, disk-based queue integration for Ikasan, enabling reliable asynchronous message processing with full XA transaction support using [BigQueue](https://github.com/bulldog2011/bigqueue).

## Overview

The Big Queue component provides:
- **BigQueueConsumer** - Consumes messages from a persistent disk-based queue with XA transaction support
- **BigQueueProducerLRCO** - Produces messages to a persistent disk-based queue with Last Resource Commit Optimization (LRCO)

Both components are fully integrated with Ikasan's transaction management and recovery framework, providing durability and reliability without requiring external message brokers.

## Use Cases

Big Queue is ideal for:
- **Decoupling flows** - Reliable asynchronous communication between flows within the same JVM
- **Buffering and flow control** - Managing spikes in message traffic
- **Local message persistence** - Durable message storage without external infrastructure
- **Reliable retry mechanisms** - Processing messages with guaranteed delivery
- **Development and testing** - Simulating message queues without external dependencies

## Maven Dependency

```xml
<dependency>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-component-endpoint-big-queue</artifactId>
    <version>${ikasan.version}</version>
</dependency>
```

## BigQueueConsumer

The `BigQueueConsumer` reads messages from a persistent disk-based queue and participates in XA transactions.

### Basic Usage with Builder

```java
@Configuration
public class FlowConfiguration {

    @Autowired
    private BuilderFactory builderFactory;

    @Bean
    public IBigQueue inboundQueue() throws Exception {
        return new BigQueueImpl("./data/inbound-queue", "inbound-queue");
    }

    @Bean
    public Consumer bigQueueConsumer(IBigQueue inboundQueue) {
        return builderFactory.getComponentBuilder()
            .bigQueueConsumer()
            .setInboundQueue(inboundQueue)
            .setConfigurationId("myBigQueueConsumer")
            .setPutErrorsToBackOfQueue(true)
            .build();
    }

    @Bean
    public Flow myFlow(Module module, Consumer bigQueueConsumer) {
        return module.getFlowBuilder("My Flow")
            .withDescription("Consumes from Big Queue")
            .consumer("Big Queue Consumer", bigQueueConsumer)
            .broker("My Broker", myBroker())
            .producer("My Producer", myProducer())
            .build();
    }
}
```

### Programmatic Configuration

```java
@Bean
public Consumer bigQueueConsumer(TransactionManager transactionManager,
                                  IBigQueue inboundQueue) {

    // Create the message runner
    InboundQueueMessageRunner messageRunner = new InboundQueueMessageRunner(
        inboundQueue,
        new BigQueueMessageJsonSerialiser()
    );

    // Create the consumer
    BigQueueConsumer consumer = new BigQueueConsumer(
        inboundQueue,
        messageRunner,
        transactionManager
    );

    // Configure
    BigQueueConsumerConfiguration config = new BigQueueConsumerConfiguration();
    config.setPutErrorsToBackOfQueue(true);
    consumer.setConfiguration(config);
    consumer.setConfiguredResourceId("myConsumer");

    return consumer;
}
```

### Configuration Options

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `putErrorsToBackOfQueue` | boolean | `false` | On transaction rollback, put failed messages to the back of the queue for retry. If `false`, messages are left at the front of the queue. |

### Custom Serialiser

By default, the consumer uses `BigQueueMessageJsonSerialiser` which expects messages in `BigQueueMessage` format. You can provide a custom serialiser:

```java
@Bean
public Consumer bigQueueConsumer(BuilderFactory builderFactory, IBigQueue inboundQueue) {
    Serialiser<String, byte[]> customSerialiser = new SimpleStringSerialiser();

    return builderFactory.getComponentBuilder()
        .bigQueueConsumer()
        .setInboundQueue(inboundQueue)
        .setSerialiser(customSerialiser)
        .build();
}
```

### Transaction Behavior

The consumer participates as an XA resource in Ikasan transactions:

- **On commit**: Message is dequeued from the queue and garbage collected
- **On rollback**:
  - If `putErrorsToBackOfQueue` is `true`: Message is moved to the back of the queue
  - If `putErrorsToBackOfQueue` is `false`: Message remains at the front for immediate retry

## BigQueueProducerLRCO

The `BigQueueProducerLRCO` writes messages to a persistent disk-based queue using Last Resource Commit Optimization.

### Basic Usage with Builder

```java
@Bean
public IBigQueue outboundQueue() throws Exception {
    return new BigQueueImpl("./data/outbound-queue", "outbound-queue");
}

@Bean
public Producer bigQueueProducer(BuilderFactory builderFactory, IBigQueue outboundQueue) {
    return builderFactory.getComponentBuilder()
        .bigQueueProducerLRCO()
        .setOutboundQueue(outboundQueue)
        .build();
}

@Bean
public Flow myFlow(Module module, Producer bigQueueProducer) {
    return module.getFlowBuilder("My Flow")
        .withDescription("Produces to Big Queue")
        .consumer("My Consumer", myConsumer())
        .broker("My Broker", myBroker())
        .producer("Big Queue Producer", bigQueueProducer)
        .build();
}
```

### Programmatic Configuration

```java
@Bean
public Producer bigQueueProducer(TransactionManager transactionManager,
                                  IBigQueue outboundQueue) {
    BigQueueProducerLRCO producer = new BigQueueProducerLRCO(
        outboundQueue,
        transactionManager
    );

    return producer;
}
```

### Custom Serialiser

By default, the producer uses `BigQueueMessageJsonSerialiser`. You can provide a custom serialiser:

```java
@Bean
public Producer bigQueueProducer(BuilderFactory builderFactory, IBigQueue outboundQueue) {
    Serialiser<String, byte[]> customSerialiser = new SimpleStringSerialiser();

    return builderFactory.getComponentBuilder()
        .bigQueueProducerLRCO()
        .setOutboundQueue(outboundQueue)
        .setSerialiser(customSerialiser)
        .build();
}
```

### Transaction Behavior

The producer uses Last Resource Commit Optimization (LRCO):

- **On invoke**: Message is staged for production
- **On commit**: Message is enqueued to the disk-based queue
- **On rollback**: Message is discarded, not written to queue

## BigQueue Message Format

### BigQueueMessage Interface

The default message format is `BigQueueMessage`:

```java
public interface BigQueueMessage<T> {
    void setMessageId(String messageId);
    String getMessageId();

    void setCreatedTime(long createdTime);
    long getCreatedTime();

    void setMessage(T message);
    T getMessage();

    void setMessageProperties(Map<String, String> messageProperties);
    Map<String, String> getMessageProperties();
}
```

### Creating Messages

```java
BigQueueMessage<String> message = new BigQueueMessageImpl<>();
message.setMessageId(UUID.randomUUID().toString());
message.setCreatedTime(System.currentTimeMillis());
message.setMessage("Hello, World!");

Map<String, String> properties = new HashMap<>();
properties.put("sourceSystem", "CRM");
properties.put("messageType", "ORDER");
message.setMessageProperties(properties);
```

### Using BigQueueMessageBuilder

```java
BigQueueMessage<String> message = BigQueueMessageBuilder.newInstance()
    .withMessageId(UUID.randomUUID().toString())
    .withCreatedTime(System.currentTimeMillis())
    .withMessage("Hello, World!")
    .withMessageProperty("sourceSystem", "CRM")
    .withMessageProperty("messageType", "ORDER")
    .build();
```

## Serializers

The component provides several built-in serializers:

### BigQueueMessageJsonSerialiser (Default)

Serializes `BigQueueMessage` objects to/from JSON:

```java
Serialiser<BigQueueMessage, byte[]> serialiser = new BigQueueMessageJsonSerialiser();
```

This is the default serializer and requires messages to implement the `BigQueueMessage` interface.

### BigQueueMessagePayloadToStringSerialiser

Extracts the payload from `BigQueueMessage` and serializes it as a string:

```java
Serialiser<String, byte[]> serialiser = new BigQueueMessagePayloadToStringSerialiser();
```

### SimpleStringSerialiser

Simple string serialization using UTF-8 encoding:

```java
Serialiser<String, byte[]> serialiser = new SimpleStringSerialiser();
```

### Custom Serialiser

Implement the `Serialiser` interface for custom serialization:

```java
public class CustomObjectSerialiser implements Serialiser<MyObject, byte[]> {

    @Override
    public byte[] serialise(MyObject object) {
        // Convert object to byte array
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public MyObject deserialise(byte[] bytes) {
        // Convert byte array to object
        return objectMapper.readValue(bytes, MyObject.class);
    }
}
```

## Complete Example: Flow Decoupling

This example shows how to use Big Queue to decouple two flows:

```java
@Configuration
public class DecoupledFlowsConfiguration {

    @Autowired
    private BuilderFactory builderFactory;

    // Shared queue between flows
    @Bean
    public IBigQueue sharedQueue() throws Exception {
        return new BigQueueImpl("./data/shared-queue", "shared-queue");
    }

    // Producer Flow - writes to queue
    @Bean
    public Flow producerFlow(Module module, IBigQueue sharedQueue) {
        Producer queueProducer = builderFactory.getComponentBuilder()
            .bigQueueProducerLRCO()
            .setOutboundQueue(sharedQueue)
            .build();

        return module.getFlowBuilder("Producer Flow")
            .withDescription("Receives data and writes to queue")
            .consumer("HTTP Consumer", httpConsumer())
            .converter("To BigQueue Message", toBigQueueMessageConverter())
            .producer("Queue Producer", queueProducer)
            .build();
    }

    // Consumer Flow - reads from queue
    @Bean
    public Flow consumerFlow(Module module, IBigQueue sharedQueue) {
        Consumer queueConsumer = builderFactory.getComponentBuilder()
            .bigQueueConsumer()
            .setInboundQueue(sharedQueue)
            .setPutErrorsToBackOfQueue(true)
            .build();

        return module.getFlowBuilder("Consumer Flow")
            .withDescription("Reads from queue and processes")
            .consumer("Queue Consumer", queueConsumer)
            .converter("Extract Payload", extractPayloadConverter())
            .broker("Process Message", processingBroker())
            .producer("Database Producer", databaseProducer())
            .build();
    }

    @Bean
    public Converter toBigQueueMessageConverter() {
        return new Converter() {
            @Override
            public Object convert(Object payload) {
                return BigQueueMessageBuilder.newInstance()
                    .withMessageId(UUID.randomUUID().toString())
                    .withCreatedTime(System.currentTimeMillis())
                    .withMessage(payload.toString())
                    .build();
            }
        };
    }

    @Bean
    public Converter extractPayloadConverter() {
        return new Converter() {
            @Override
            public Object convert(Object payload) {
                if (payload instanceof BigQueueMessage) {
                    return ((BigQueueMessage) payload).getMessage();
                }
                return payload;
            }
        };
    }
}
```

## IBigQueue Configuration

The underlying `IBigQueue` implementation can be configured:

```java
@Bean
public IBigQueue myQueue() throws Exception {
    String queueDir = "./data/my-queue";      // Directory for queue files
    String queueName = "my-queue";             // Queue name

    // Optional: Configure page size (default 128MB)
    int pageSize = 128 * 1024 * 1024;

    return new BigQueueImpl(queueDir, queueName);
}
```

### Queue Storage

- Queue data is stored on disk in the specified directory
- Each queue uses memory-mapped files for efficient I/O
- Queues are persistent across application restarts
- Multiple queues can exist independently

### Queue Maintenance

```java
// Get queue size
long size = bigQueue.size();

// Check if empty
boolean empty = bigQueue.isEmpty();

// Manual garbage collection (happens automatically on commit)
bigQueue.gc();

// Remove all messages
bigQueue.removeAll();

// Close queue
bigQueue.close();
```

## Best Practices

### Consumer Best Practices

1. **Enable error requeuing** for automatic retry of failed messages
2. **Configure managed event identifier service** for proper event tracking
3. **Use appropriate serializers** based on your message format
4. **Monitor queue size** to detect processing bottlenecks
5. **Configure proper error handling** in downstream components

### Producer Best Practices

1. **Wrap payloads in BigQueueMessage** for rich metadata
2. **Set meaningful message IDs** for tracking and debugging
3. **Use message properties** for routing and filtering metadata
4. **Consider serialization overhead** when choosing serializers
5. **Ensure queue directory has sufficient disk space**

### Performance Tuning

1. **Queue location** - Use fast SSD storage for queue files
2. **Page size** - Larger pages reduce file I/O but increase memory usage
3. **Garbage collection** - Happens automatically on commit, reclaims disk space
4. **Serialization** - Choose efficient serializers for your data format
5. **Consumer threads** - Single-threaded by design for ordered processing

### Error Handling

```java
// Configure error handling with requeuing
BigQueueConsumerConfiguration config = new BigQueueConsumerConfiguration();
config.setPutErrorsToBackOfQueue(true);

// Add error handler in flow
Flow flow = flowBuilder
    .consumer("Queue Consumer", queueConsumer)
    .broker("Error Handler", new ErrorHandlingBroker())
    .producer("Queue Producer", queueProducer)
    .build();
```

### Monitoring

Monitor these metrics for queue health:

```java
// Queue depth
long queueSize = bigQueue.size();

// Is processing keeping up?
boolean isEmpty = bigQueue.isEmpty();

// Disk usage (check queue directory)
Path queuePath = Paths.get("./data/my-queue");
long diskUsage = Files.walk(queuePath)
    .mapToLong(p -> p.toFile().length())
    .sum();
```

## Troubleshooting

### Messages Not Being Consumed

1. Check that consumer is started: `consumer.isRunning()`
2. Verify queue has messages: `bigQueue.size() > 0`
3. Check queue directory permissions
4. Review consumer configuration
5. Check for exceptions in logs

### Messages Not Being Produced

1. Verify transaction is committing successfully
2. Check serializer is compatible with consumer
3. Verify queue directory is writable
4. Check disk space availability
5. Review producer transaction behavior

### Queue Growing Unbounded

1. Check consumer processing rate vs producer rate
2. Verify consumer is not repeatedly failing
3. Check for transaction rollbacks
4. Review `putErrorsToBackOfQueue` configuration
5. Add monitoring and alerting on queue depth

### File System Issues

1. **Insufficient disk space** - Monitor and allocate adequate storage
2. **Permission errors** - Ensure write access to queue directory
3. **File descriptor limits** - Increase OS limits if needed
4. **Corrupted queue files** - May need to rebuild queue from backup

## Advantages Over External Message Brokers

1. **No external dependencies** - Self-contained, no broker installation required
2. **Zero network latency** - In-process communication
3. **Transactional integration** - Native XA transaction support
4. **Simple deployment** - No broker configuration or management
5. **Development friendly** - Easy to set up and test locally
6. **Cost effective** - No broker licensing or infrastructure costs

## Limitations

1. **Single JVM** - Cannot be used across multiple processes
2. **No pub-sub** - Point-to-point only (single consumer)
3. **Ordering** - Single-threaded consumption for ordering guarantees
4. **Disk-bound** - Performance limited by disk I/O
5. **No query capabilities** - Sequential access only

## Migration from BigQueue to Message Brokers

If your requirements grow beyond local queuing, you can migrate to external message brokers:

```java
// Replace BigQueue consumer with JMS consumer
Consumer jmsConsumer = builderFactory.getComponentBuilder()
    .jmsConsumer()
    .setConnectionFactory(connectionFactory)
    .setDestinationJndiName("queue/MyQueue")
    .build();

// Replace BigQueue producer with JMS producer
Producer jmsProducer = builderFactory.getComponentBuilder()
    .jmsProducer()
    .setConnectionFactory(connectionFactory)
    .setDestinationJndiName("queue/MyQueue")
    .build();
```

## References

- [BigQueue GitHub Repository](https://github.com/bulldog2011/bigqueue)
- [Ikasan Documentation](https://github.com/ikasanEIP/ikasan)
- [XA Transactions in Ikasan](https://github.com/ikasanEIP/ikasan/wiki/XA-Transactions)

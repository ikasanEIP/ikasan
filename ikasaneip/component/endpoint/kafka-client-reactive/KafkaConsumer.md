[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# KafkaConsumer

The `KafkaConsumer` is a reactive Kafka client consumer implementation within the Ikasan Enterprise Integration Platform. It is designed to consume messages from a Kafka topic in a non-blocking, back-pressure-ready manner, leveraging Project Reactor and its `reactor-kafka` library.

## Core Concepts

The consumer is built on the following key principles:

- **Reactive Streams:** It uses `Flux` from Project Reactor to handle streams of Kafka messages, providing an asynchronous and efficient way to process data.
- **Configuration Driven:** As a `ConfiguredResource`, its behavior is managed by the `KafkaConsumerConfiguration` class, which exposes a comprehensive set of Kafka consumer properties.
- **Lifecycle Management:** It implements standard Ikasan lifecycle interfaces (`start`, `stop`, `isRunning`) for consistent control within an Ikasan flow.
- **Event-driven:** It integrates with the Ikasan flow architecture by creating and passing `FlowEvent` instances to a registered `EventListener`.

## Key Interfaces Implemented

The `KafkaConsumer` implements several important Ikasan specification interfaces, defining its roles and capabilities:

- **`Consumer<EventListener<?>, EventFactory>`:** The fundamental interface for a message consumer, defining methods for setting listeners and event factories.
- **`ManagedIdentifierService<ManagedRelatedEventIdentifierService>`:** Allows for the provision of a service to generate unique and related event identifiers from the incoming Kafka message payload. This is crucial for tracking events as they are processed through a flow.
- **`ConfiguredResource<KafkaConsumerConfiguration>`:** Marks the component as having a dedicated configuration object (`KafkaConsumerConfiguration`) and a configurable resource ID.
- **`ResubmissionService<Object>`:** Provides a mechanism to handle resubmitted events, re-introducing them into the flow.
- **`EndpointListener<ReceiverRecord, Throwable>`:** Defines callbacks for handling messages (`onMessage`) and exceptions (`onException`) from the underlying reactive Kafka receiver.

## Configuration (`KafkaConsumerConfiguration`)

The `KafkaConsumer` is configured via the `KafkaConsumerConfiguration` object. This class provides a wrapper around the standard Apache Kafka consumer properties.
See [KafkaConsumerConfiguration](./KafkaConsumerConfiguration.md) for more details

### Essential Configuration Properties:

- **`topicName`**: The Kafka topic to subscribe to.
- **`bootstrapServers`**: A list of host/port pairs for the initial connection to the Kafka cluster.
- **`groupId`**: The consumer group ID.
- **`keyDeserializer` / `valueDeserializer`**: The fully qualified class names for the key and value deserializers.
- **`partitions`**: A list of specific partition numbers to consume from.
- **`partitionOffsets`**: A map where keys are partition numbers and values are the starting offsets for consumption. The consumer manages and updates these offsets as it processes messages.

The configuration class also allows setting a wide range of other standard Kafka properties, including those for SSL, SASL, timeouts, fetch sizes, and more.

## Lifecycle and Operation

### Starting the Consumer

1.  The `start()` method is called by the Ikasan framework.
2.  It performs pre-flight checks to ensure that a `ManagedRelatedEventIdentifierService` and a `MessageListener` have been set.
3.  It calls the private `subscribe()` method to establish the connection to Kafka.

### The `subscribe()` Method

This is the core of the reactive setup:

1.  **`ReceiverOptions` Creation:** It creates an instance of `reactor.kafka.receiver.ReceiverOptions` using the properties from `KafkaConsumerConfiguration`.
2.  **Partition Assignment:** It explicitly assigns the consumer to the partitions specified in the configuration.
3.  **Offset Seeking:** For each assigned partition, it sets the initial read position using the offsets stored in `partitionOffsets`. This allows the consumer to resume from where it last left off.
4.  **Flux Creation:** It creates a `Flux<ReceiverRecord<Object, Object>>` using `KafkaReceiver.create(options).receive()`. This `Flux` represents the stream of incoming messages.
5.  **Subscription:** It subscribes to the `kafkaFlux`. The subscription has two main parts:
    - A `consumer` lambda (`record -> this.messageListener.onMessage(record)`) that is executed for each received message.
    - An `errorConsumer` lambda (`e -> ...`) that logs any errors and passes them to the flow's exception handler.
6.  The subscription is stored in a `Disposable` object, which is used to manage the lifecycle of the stream.

### Message Processing (`onMessage`)

The `KafkaConsumer` itself acts as the default `MessageListener`. When a `ReceiverRecord` arrives from the Kafka topic:

1.  The `onMessage(ReceiverRecord record)` method is invoked.
2.  It calls `invokeFlowEvent(record.value())`, which wraps the message payload (`record.value()`) in a `FlowEvent` using the configured `EventFactory` and `ManagedRelatedEventIdentifierService`.
3.  The newly created `FlowEvent` is passed to the registered `EventListener` (typically the next component in the flow).
4.  The consumer updates its internal `partitionOffsets` map with the next offset (`offset.offset() + 1`).
5.  It persists this configuration change by calling `configurationService.update(this)`.
6.  Finally, it calls `offset.acknowledge()` to commit the offset with Kafka, marking the message as successfully processed.

### Stopping the Consumer

- The `stop()` method simply calls `dispose()` on the `Disposable` subscription, which terminates the reactive stream and closes the connection to Kafka.

## Error Handling and Resubmission

- **General Exceptions:** Any exception occurring in the reactive stream is caught and passed to the `onException(Throwable throwable)` method, which in turn invokes the `EventListener` with the exception, allowing for standard Ikasan error handling.
- **`ForceTransactionRollbackException`:** This exception is handled specially. If the message is `ExcludeEventAction.EXCLUDE_EVENT`, it indicates the event should be skipped. The consumer will re-process the record to allow blacklisting, acknowledge the offset, and then restart its reactive subscription to continue processing.
- **Resubmission:** When a message is resubmitted via the `ResubmissionService`, the `onResubmission(Object resubmissionEvent)` method is called. It wraps the event in a special `Resubmission` event type and passes it to the listener.

## How to Use

1.  **Instantiate:** Create an instance of `KafkaConsumer`, providing a `ConfigurationService`.
2.  **Configure:** Obtain a `KafkaConsumerConfiguration` instance and set the required properties (topic, bootstrap servers, etc.). Set this configuration on the consumer instance.
3.  **Set Dependencies:**
    - Provide an implementation of `ManagedRelatedEventIdentifierService` to extract a unique ID from your message payload.
    - Set an `EventListener` (the next component in the flow).
    - Set an `EventFactory` and a `ResubmissionEventFactory`.
4.  **Integrate:** Place the configured consumer at the start of an Ikasan flow.

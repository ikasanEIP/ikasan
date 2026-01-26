![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# KafkaReactiveConsumerBuilder Overview

The `KafkaReactiveConsumerBuilder` provides a fluent API for constructing and configuring `KafkaConsumer` instances designed for reactive stream processing within the Ikasan framework. This builder simplifies the setup of Kafka consumers by allowing declarative configuration of essential properties and services.

## Purpose

The primary purpose of the `KafkaReactiveConsumerBuilder` is to facilitate the creation of `org.ikasan.component.endpoint.kafka.client.reactive.consumer.KafkaConsumer` objects. These consumers are specifically tailored for reactive message consumption from Kafka topics, integrating seamlessly with Ikasan's event processing and error handling mechanisms.

## Key Configuration Methods

The builder offers several methods to configure the Kafka consumer:

*   **`setManagedEventIdentifierService(ManagedRelatedEventIdentifierService managedEventIdentifierService)`**
    *   **Description**: This is a **mandatory** configuration. It sets a service responsible for extracting a unique and reliably reproducible identifier from the value of a Kafka `ReceiverRecord`. This identifier is crucial for event tracking, correlation, and management within Ikasan.
    *   **Requirement**: A custom implementation of `ManagedRelatedEventIdentifierService` must be provided, as there is no default for reactive Kafka consumers.

*   **`setListener(MessageListener<ReceiverRecord> messageListener)`**
    *   **Description**: Specifies the `MessageListener` that will be invoked to process each `ReceiverRecord` received from Kafka.
    *   **Default Behavior**: If no custom listener is set, the `KafkaConsumer` instance itself will act as the message listener.

*   **`setEventFactory(EventFactory eventFactory)`**
    *   **Description**: Sets the `EventFactory` responsible for creating Ikasan `Event` objects from Kafka `ReceiverRecord`s.

*   **`setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory)`**
    *   **Description**: Configures the `ResubmissionEventFactory` used for creating events specifically for resubmission scenarios.

*   **`setConfigurationId(String configurationId)`**
    *   **Description**: Assigns a unique identifier to the consumer's configuration. This is often used for externalized configuration management.

*   **`setConfiguration(KafkaConsumerConfiguration configuration)`**
    *   **Description**: Provides a `KafkaConsumerConfiguration` object, which encapsulates all the low-level Kafka consumer properties. This includes settings such as:
        *   `topic` (the Kafka topic to consume from)
        *   `groupId` (the consumer group ID)
        *   `brokers` (list of Kafka broker addresses)
        *   `offsetReset` (policy for resetting offsets, e.g., "earliest", "latest")
        *   `pollTimeout` (the maximum time to wait for records)
        *   And many other standard Kafka consumer properties.

## How to Use

To build a `KafkaConsumer` using the `KafkaReactiveConsumerBuilder`, you typically follow these steps:

1.  Obtain an instance of `KafkaReactiveConsumerBuilder` (e.g., from a `BuilderFactory` or through Spring autowiring).
2.  Call the `setManagedEventIdentifierService()` method with your custom implementation. This step is critical.
3.  Optionally, configure other properties using the `setListener()`, `setEventFactory()`, `setResubmissionEventFactory()`, `setConfigurationId()`, and `setConfiguration()` methods.
4.  Finally, invoke the `build()` method to create the `Consumer` instance.

### Example Usage

```java
// Assuming builderFactory is an instance of org.ikasan.builder.BuilderFactory
// and configurationService is an instance of org.ikasan.spec.configuration.ConfigurationService

// 1. Define your KafkaConsumerConfiguration
KafkaConsumerConfiguration kafkaConfig = new KafkaConsumerConfiguration();
kafkaConfig.setTopic("my-reactive-topic");
kafkaConfig.setGroupId("my-reactive-group");
kafkaConfig.setBrokers("localhost:9092");
kafkaConfig.setPartitionOffsets(List.of("0", "1", "2"));
// ... set other Kafka properties as needed

// 2. Implement your ManagedRelatedEventIdentifierService
ManagedRelatedEventIdentifierService myEventIdentifierService = new MyCustomKafkaEventIdentifierServiceImpl();

// 3. Build the Kafka Reactive Consumer
Consumer kafkaReactiveConsumer = builderFactory.getComponentBuilder()
    .kafkaReactiveConsumer()
    .setConfigurationId("myKafkaReactiveConsumerConfig")
    .setConfiguration(kafkaConfig)
    .setManagedEventIdentifierService(myEventIdentifierService)
    .setListener(myMessageListener) // Optional: if you have a custom message listener
    .build();

// This consumer can then be integrated into an Ikasan Flow using the FlowBuilder.
```

**Note**: The `MyCustomKafkaEventIdentifierService` would be an implementation that knows how to extract a unique identifier from the `ReceiverRecord`'s value, which is essential for Ikasan's event management.

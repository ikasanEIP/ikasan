![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Builder Module Overview

The Ikasan Builder module provides a fluent Java API for declaratively constructing integration modules and flows. It simplifies the process of defining complex integration logic by allowing developers to chain together components in a readable and maintainable way.

This document provides a high-level overview of the main concepts and components of the builder module.

## Core Concepts

The builder module is centered around a hierarchy of builders, each responsible for a specific part of the integration application:

1.  **IkasanApplication**: The top-level container for the entire integration application. It manages modules and provides access to the necessary factories.
2.  **Module**: A deployable unit that contains one or more related flows.
3.  **Flow**: A message-processing pipeline that defines the path of a message through a series of components.
4.  **Route**: A sequence of flow components that can be part of a flow, often used for conditional routing.
5.  **Component**: Individual processing units within a flow, such as consumers, producers, converters, translators, filters, and routers.

## The Builder Hierarchy

The fluent API is designed to guide the developer through the process of building an application from the top down.

### 1. `IkasanApplication`

This is the entry point. For Spring Boot applications, `DefaultSpringBootIkasanApplication` is the typical implementation. It manages the Spring `ApplicationContext` and provides access to the `BuilderFactory`.

### 2. `BuilderFactory`

The `BuilderFactory` is the central factory for obtaining different types of builders. It is typically autowired or retrieved from the `IkasanApplication`.

Key methods:
- `getModuleBuilder(String moduleName)`: Gets a builder for a `Module`.
- `getFlowBuilder(String moduleName, String flowName)`: A shortcut to get a `FlowBuilder`.
- `getComponentBuilder()`: Gets a builder for creating individual Ikasan components.
- `getRouteBuilder()`: Gets a builder for creating reusable `Route` segments.
- `getMonitorBuilder()`: Gets a builder for creating `Monitor` instances.
- `getExceptionResolverBuilder()`: Gets a builder for creating `ExceptionResolver` instances.

### 3. `ModuleBuilder`

The `ModuleBuilder` is used to construct a `Module`. You can set its name, description, version, and add flows to it.

**Example:**
```java
Module myModule = builderFactory.getModuleBuilder("My Module")
    .withDescription("A module that does something.")
    .withVersion("1.0.0")
    .addFlow(myFlow)
    .build();
```

### 4. `FlowBuilder`

The `FlowBuilder` is the heart of the builder module. It provides a fluent API to define the structure of an integration flow. A flow is defined as a sequence of components, starting with a consumer and ending with one or more producers.

A flow definition always starts with a `.consumer(...)` call and ends with a `.build()` call on the final component in the chain.

**Example:**
```java
Flow myFlow = moduleBuilder.getFlowBuilder("My Flow")
    .consumer("myConsumer", myConsumerComponent)
    .converter("myConverter", myConverterComponent)
    .producer("myProducer", myProducerComponent)
    .build();
```

The `FlowBuilder` also handles the configuration of cross-cutting concerns for the flow, such as:
- Recovery Management (`withRecoveryManager`)
- Exception Resolution (`withExceptionResolver`)
- Monitoring (`withMonitor`)
- Error Reporting (`withErrorReportingService`)
- Resubmission (`withResubmissionService`)

### 5. `RouteBuilder`

The `RouteBuilder` is used to define a `Route`, which is a a sequence of components that can be used as a branch in a routing decision. This is particularly useful with `singleRecipientRouter` and `multiRecipientRouter`.

**Example:**
```java
RouteBuilder routeBuilder = builderFactory.getRouteBuilder();
Route routeA = routeBuilder.translator("translatorA", translatorAComponent)
                           .producer("producerA", producerAComponent);
```

## Defining a Flow: A Closer Look

A flow is a chain of components. The `FlowBuilder` provides methods for adding different types of components.

### Components

- **`consumer(name, component)`**: The starting point of a flow.
- **`converter(name, component)`**: Transforms the message payload from one format to another.
- **`translator(name, component)`**: Enriches or modifies the message payload.
- **`splitter(name, component)`**: Splits a single message into multiple messages.
- **`filter(name, component)`**: Decides whether a message should continue in the flow.
- **`broker(name, component)`**: A content-based router.
- **`producer(name, component)`**: The endpoint of a flow or a flow branch.

### Routing

The builder provides powerful routing capabilities.

- **`singleRecipientRouter(name, component)`**: Routes a message to a single destination based on its content. It uses `when(condition, route)` and `otherwise(route)` to define the paths.

- **`multiRecipientRouter(name, component)`**: Routes a copy of the message to multiple destinations. It uses `when(condition, route)` to define the paths.

**Example with a Single Recipient Router:**
```java
Route routeA = builderFactory.getRouteBuilder().producer("producerA", producerA);
Route routeB = builderFactory.getRouteBuilder().producer("producerB", producerB);

Flow srrFlow = moduleBuilder.getFlowBuilder("SRR Flow")
    .consumer("myConsumer", myConsumer)
    .singleRecipientRouter("myRouter", myRouterComponent)
        .when("isTypeA", routeA)
        .when("isTypeB", routeB)
        .otherwise(builderFactory.getRouteBuilder().producer("defaultProducer", defaultProducer))
    .build();
```

## Auto-Configuration

The `IkasanBaseAutoConfiguration` class is a Spring Boot auto-configuration that sets up all the necessary beans for the builder module to work, including the `BuilderFactory`, `AopProxyProvider`, and a default `ExceptionResolver`. It also imports many other Ikasan configurations, making the setup of an Ikasan application straightforward.

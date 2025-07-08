![Problem Domain](../quickstart-images/Ikasan-title-transparent.png)

### Theme 1: The Ikasan Module JSON Data Model (The Foundation)

The foundation for our developer tooling and AI-driven features is a standardized JSON representation of an Ikasan module. Crucially, this JSON data model is **directly derived from and maps to existing Ikasan core classes**, ensuring consistency and leveraging the established domain model.

*   **Goal: Leverage Existing Ikasan Metadata Classes**
    *   **Action:** The JSON data model will directly reflect the structure of the following core Ikasan classes:
        *   `org.ikasan.spec.metadata.ModuleMetaData` (implemented by `org.ikasan.topology.metadata.model.ModuleMetaDataImpl`)
        *   `org.ikasan.spec.metadata.FlowMetaData` (implemented by `org.ikasan.topology.metadata.model.FlowMetaDataImpl`)
        *   `org.ikasan.spec.metadata.FlowElementMetaData` (implemented by `org.ikasan.topology.metadata.model.FlowElementMetaDataImpl`)
        *   `org.ikasan.spec.metadata.Transition` (implemented by `org.ikasan.topology.metadata.model.TransitionImpl`)
        *   `org.ikasan.spec.metadata.ConfigurationMetaData` (implemented by `org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl`)
        *   `org.ikasan.spec.metadata.ConfigurationParameterMetaData` (implemented by `org.ikasan.configurationService.metadata.ConfigurationParameterMetaDataImpl`)
    *   **Action:** The JSON structure, as detailed below, will serve as the canonical representation for module definitions.
    *   **Action:** Ensure that any tooling (IDE, AI generator) directly works with this established data model for both input and output, minimizing transformation overhead and maintaining fidelity with the Ikasan core.

#### `moduleMetaData`

This object defines the structure of the module, its flows, and its components.

```json
{
  "moduleMetaData": {
    "name": "module-name",
    "description": "A description of the module.",
    "version": "1.0.0",
    "type": "SCHEDULER_AGENT",
    "url": "http://localhost:8080",
    "flows": [
      // Array of FlowMetaData objects
    ]
  },
  "configurationMetaData": [
    // Array of ConfigurationMetaData objects
  ]
}
```

#### `FlowMetaData`

This object defines a single flow within the module.

```json
{
  "name": "flow-name",
  "consumer": { /* FlowElementMetaData object */ },
  "flowElements": [
    // Array of FlowElementMetaData objects
  ],
  "transitions": [
    // Array of Transition objects
  ],
  "configurationId": "flow-configuration-id"
}
```

#### `FlowElementMetaData`

This object represents a single component within a flow.

```json
{
  "componentName": "component-name",
  "componentType": "consumer", // e.g., consumer, producer, splitter, router
  "implementingClass": "org.ikasan.component.jms.JmsConsumer",
  "isConfigurable": true,
  "configurationId": "component-configuration-id",
  "invokerConfigurationId": "invoker-configuration-id",
  "decorators": []
}
```

#### `Transition`

This object defines a directed link between two components.

```json
{
  "from": "component-name-1",
  "to": "component-name-2",
  "name": "transition-name" // e.g., "when 'true'"
}
```

#### `ConfigurationMetaData`

This object defines the configuration for a single component.

```json
{
  "configurationId": "component-configuration-id",
  "description": "Configuration for the JMS consumer.",
  "implementingClass": "org.ikasan.component.jms.JmsConsumerConfiguration",
  "parameters": [
    // Array of ConfigurationParameterMetaData objects
  ]
}
```

#### `ConfigurationParameterMetaData`

This object defines a single configuration parameter.

```json
{
  "name": "destinationName",
  "value": "in.queue",
  "description": "The name of the JMS queue to consume from.",
  "implementingClass": "java.lang.String"
}
```

### Full Working Example: `hello-ikasan-jms`

This example demonstrates a complete JSON representation for a module named `hello-ikasan-jms`. The module contains a single flow, "Hello World Flow," that consumes a message from a JMS queue, logs it, and then publishes it to a JMS topic.

```json
{
    "moduleMetaData": {
        "url": "http://localhost:7080/jms-demo",
        "host": "localhost",
        "port": 7080,
        "context": "/jms-demo",
        "protocol": "http",
        "name": "jms-demo",
        "description": "Sample Module",
        "version": "1.0.0-SNAPSHOT",
        "ikasanVersion": "4.1.1-SNAPSHOT",
        "flows": [
            {
                "name": "JMS FLow",
                "consumer": {
                    "componentName": "JMS Consumer",
                    "componentType": "org.ikasan.spec.component.endpoint.Consumer",
                    "implementingClass": "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
                    "configurationId": "jmsConsumer",
                    "invokerConfigurationId": "jms-demo_JMS FLow_JMS Consumer_1165847135_I",
                    "configurable": true
                },
                "transitions": [
                    {
                        "from": "Exception Generating Broker",
                        "to": "JMS Producer",
                        "name": "default"
                    },
                    {
                        "from": "JMS Consumer",
                        "to": "Exception Generating Broker",
                        "name": "default"
                    }
                ],
                "flowElements": [
                    {
                        "componentName": "JMS Producer",
                        "componentType": "org.ikasan.spec.component.endpoint.Producer",
                        "implementingClass": "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
                        "configurationId": "jmsProducer",
                        "invokerConfigurationId": "jms-demo_JMS FLow_JMS Producer_1165847135_I",
                        "configurable": true
                    },
                    {
                        "componentName": "Exception Generating Broker",
                        "componentType": "org.ikasan.spec.component.endpoint.Broker",
                        "implementingClass": "com.ikasan.sample.spring.boot.builderpattern.ExceptionGenerationgBroker",
                        "invokerConfigurationId": "jms-demo_JMS FLow_Exception Generating Broker_1165847135_I",
                        "configurable": false
                    },
                    {
                        "componentName": "JMS Consumer",
                        "componentType": "org.ikasan.spec.component.endpoint.Consumer",
                        "implementingClass": "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
                        "configurationId": "jmsConsumer",
                        "invokerConfigurationId": "jms-demo_JMS FLow_JMS Consumer_1165847135_I",
                        "configurable": true
                    }
                ],
                "configurationId": "jms-demo-JMS FLow",
                "flowStartupType": "MANUAL"
            }
        ],
        "type": "INTEGRATION_MODULE"
    },
    "configurationMetaData": [
        {
            "configurationId": "jmsConsumer",
            "implementingClass": "org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration",
            "parameters": [
                {
                    "name": "autoContentConversion",
                    "value": true,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "autoSplitBatch",
                    "value": true,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "batchMode",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "batchSize",
                    "value": 0,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "cacheLevel",
                    "value": 1,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "concurrentConsumers",
                    "value": 1,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "connectionFactoryJndiProperties",
                    "value": {},
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
                },
                {
                    "name": "connectionFactoryName",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "connectionFactoryPassword",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl"
                },
                {
                    "name": "connectionFactoryUsername",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "destinationJndiName",
                    "value": "source",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "destinationJndiProperties",
                    "value": {},
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
                },
                {
                    "name": "durable",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "durableSubscriptionName",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "maxConcurrentConsumers",
                    "value": 1,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "pubSubDomain",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "sessionAcknowledgeMode",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "sessionTransacted",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                }
            ]
        },
        {
            "configurationId": "jmsProducer",
            "implementingClass": "org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration",
            "parameters": [
                {
                    "name": "connectionFactoryJndiProperties",
                    "value": {},
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
                },
                {
                    "name": "connectionFactoryName",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "connectionFactoryPassword",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "connectionFactoryUsername",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "deliveryMode",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "deliveryPersistent",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "destinationJndiName",
                    "value": "target",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "destinationJndiProperties",
                    "value": {},
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
                },
                {
                    "name": "explicitQosEnabled",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "messageIdEnabled",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "messageTimestampEnabled",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "priority",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "pubSubDomain",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "pubSubNoLocal",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "receiveTimeout",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
                },
                {
                    "name": "sessionAcknowledgeMode",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
                },
                {
                    "name": "sessionAcknowledgeModeName",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
                },
                {
                    "name": "sessionTransacted",
                    "value": false,
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
                },
                {
                    "name": "timeToLive",
                    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
                }
            ]
        }
    ],
    "dependencyManagement": {
        "dependencies": [
            {
                "group": "org.ikasan",
                "artefact": "ikasan-eip-standalone",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spring-resource",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-uber-spec",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-component",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-event",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-flow",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-replay",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-module",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-recovery-manager",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-exclusion",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-monitor",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-deployment",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-configuration",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-management",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-wiretap",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-search",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-serialiser",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-error-reporting",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-resubmission",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-hospital",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-housekeeping",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-history",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-scheduled",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-big-queue",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-system-event",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-search",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-management",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-housekeeping",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-system-event",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-module",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "com.google.guava",
                "artefact": "guava",
                "version": "33.0.0-jre"
            },
            {
                "group": "com.google.guava",
                "artefact": "failureaccess",
                "version": "1.0.2"
            },
            {
                "group": "com.google.guava",
                "artefact": "listenablefuture",
                "version": "9999.0-empty-to-avoid-conflict-with-guava"
            },
            {
                "group": "com.google.errorprone",
                "artefact": "error_prone_annotations",
                "version": "2.23.0"
            },
            {
                "group": "com.google.j2objc",
                "artefact": "j2objc-annotations",
                "version": "2.8"
            },
            {
                "group": "jakarta.xml.bind",
                "artefact": "jakarta.xml.bind-api",
                "version": "4.0.1"
            },
            {
                "group": "jakarta.activation",
                "artefact": "jakarta.activation-api",
                "version": "2.1.2"
            },
            {
                "group": "org.jmock",
                "artefact": "jmock-imposters",
                "version": "2.12.0"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-module",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-dashboard-client",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-monitor",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-scheduler",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.quartz-scheduler",
                "artefact": "quartz",
                "version": "2.3.2"
            },
            {
                "group": "com.zaxxer",
                "artefact": "HikariCP-java7",
                "version": "2.4.13"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-recovery-manager",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-exclusion-service",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-exclusion",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-configuration",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-serialiser",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-persistence",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "javax.annotation",
                "artefact": "javax.annotation-api",
                "version": "1.3.1"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-serialiser",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "com.esotericsoftware",
                "artefact": "kryo",
                "version": "4.0.2"
            },
            {
                "group": "com.esotericsoftware",
                "artefact": "reflectasm",
                "version": "1.11.3"
            },
            {
                "group": "com.esotericsoftware",
                "artefact": "minlog",
                "version": "1.3.0"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-topology",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-metadata",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-version",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-transaction-arjuna",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.jboss",
                "artefact": "jboss-transaction-spi",
                "version": "8.0.0.Final"
            },
            {
                "group": "org.jboss.narayana.arjunacore",
                "artefact": "txoj",
                "version": "7.0.0.Final"
            },
            {
                "group": "org.jboss.windup.decompiler",
                "artefact": "decompiler-fernflower",
                "version": "6.3.9.Final"
            },
            {
                "group": "org.jboss.windup.decompiler.fernflower",
                "artefact": "windup-fernflower",
                "version": "1.0.0.20171018"
            },
            {
                "group": "org.jboss.windup.decompiler",
                "artefact": "decompiler-api",
                "version": "6.3.9.Final"
            },
            {
                "group": "org.jboss.windup.utils",
                "artefact": "windup-utils",
                "version": "6.3.9.Final"
            },
            {
                "group": "javax.xml.bind",
                "artefact": "jaxb-api",
                "version": "2.3.1"
            },
            {
                "group": "javax.activation",
                "artefact": "javax.activation-api",
                "version": "1.2.0"
            },
            {
                "group": "org.kamranzafar",
                "artefact": "jtar",
                "version": "2.3"
            },
            {
                "group": "commons-codec",
                "artefact": "commons-codec",
                "version": "1.13"
            },
            {
                "group": "com.fasterxml.jackson.core",
                "artefact": "jackson-core",
                "version": "2.16.0"
            },
            {
                "group": "commons-io",
                "artefact": "commons-io",
                "version": "2.14.0"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-error-reporting-service",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-error-reporting",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-hospital",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-flow-visitorPattern",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-resubmission",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-replay",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "io.github.kostaskougios",
                "artefact": "cloning",
                "version": "1.10.3"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-filter",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-component",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-component-multiRecipient-router",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-component-splitter",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-configuration-service",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.jasypt",
                "artefact": "jasypt",
                "version": "1.9.3"
            },
            {
                "group": "org.jasypt",
                "artefact": "jasypt-hibernate5",
                "version": "1.9.3"
            },
            {
                "group": "org.apache.commons",
                "artefact": "commons-lang3",
                "version": "3.17.0"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-wiretap",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-wiretap",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-history",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-security-db",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.springframework.data",
                "artefact": "spring-data-jpa",
                "version": "3.4.1"
            },
            {
                "group": "org.springframework.data",
                "artefact": "spring-data-commons",
                "version": "3.4.1"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-security-rest",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-replay",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.apache.httpcomponents.client5",
                "artefact": "httpclient5",
                "version": "5.4.3"
            },
            {
                "group": "org.apache.httpcomponents.core5",
                "artefact": "httpcore5",
                "version": "5.3.4"
            },
            {
                "group": "org.apache.httpcomponents.core5",
                "artefact": "httpcore5-h2",
                "version": "5.3.4"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-rest-module",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.springdoc",
                "artefact": "springdoc-openapi-starter-webmvc-ui",
                "version": "2.2.0"
            },
            {
                "group": "org.springdoc",
                "artefact": "springdoc-openapi-starter-webmvc-api",
                "version": "2.2.0"
            },
            {
                "group": "org.springdoc",
                "artefact": "springdoc-openapi-starter-common",
                "version": "2.2.0"
            },
            {
                "group": "io.swagger.core.v3",
                "artefact": "swagger-core",
                "version": "2.2.15"
            },
            {
                "group": "io.swagger.core.v3",
                "artefact": "swagger-annotations",
                "version": "2.2.15"
            },
            {
                "group": "io.swagger.core.v3",
                "artefact": "swagger-models",
                "version": "2.2.15"
            },
            {
                "group": "com.fasterxml.jackson.dataformat",
                "artefact": "jackson-dataformat-yaml",
                "version": "2.15.1"
            },
            {
                "group": "org.webjars",
                "artefact": "swagger-ui",
                "version": "5.2.0"
            },
            {
                "group": "org.apache.commons",
                "artefact": "commons-text",
                "version": "1.13.0"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-hospital",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-builder",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-housekeeping-module",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-housekeeping",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-harvesting",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-rest-dashboard-client",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-metrics",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-h2-backup",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-setup",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "com.opencsv",
                "artefact": "opencsv",
                "version": "5.8"
            },
            {
                "group": "org.apache.commons",
                "artefact": "commons-collections4",
                "version": "4.4"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-builder-spring",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-monitor",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "joda-time",
                "artefact": "joda-time",
                "version": "2.10.5"
            },
            {
                "group": "javax.mail",
                "artefact": "mail",
                "version": "1.4.3"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-persistence",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-quartz-endpoint",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-utility-endpoint",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-webconsole-jar",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-webconsole-boot-war",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "jakarta.servlet.jsp.jstl",
                "artefact": "jakarta.servlet.jsp.jstl-api",
                "version": "3.0.0"
            },
            {
                "group": "jakarta.el",
                "artefact": "jakarta.el-api",
                "version": "6.0.0-M1"
            },
            {
                "group": "org.glassfish.web",
                "artefact": "jakarta.servlet.jsp.jstl",
                "version": "3.0.0"
            },
            {
                "group": "jakarta.servlet",
                "artefact": "jakarta.servlet-api",
                "version": "6.1.0-M1"
            },
            {
                "group": "org.yaml",
                "artefact": "snakeyaml",
                "version": "2.2"
            },
            {
                "group": "com.fasterxml.jackson.datatype",
                "artefact": "jackson-datatype-jsr310",
                "version": "2.18.3"
            },
            {
                "group": "org.hdrhistogram",
                "artefact": "HdrHistogram",
                "version": "2.2.2"
            },
            {
                "group": "org.latencyutils",
                "artefact": "LatencyUtils",
                "version": "2.0.3"
            },
            {
                "group": "org.ikasan",
                "artefact": "commons-dbcp2",
                "version": "1.0.0"
            },
            {
                "group": "org.apache.commons",
                "artefact": "commons-pool2",
                "version": "2.12.0"
            },
            {
                "group": "jakarta.enterprise",
                "artefact": "jakarta.enterprise.cdi-api",
                "version": "4.1.0-M1"
            },
            {
                "group": "jakarta.enterprise",
                "artefact": "jakarta.enterprise.lang-model",
                "version": "4.1.0-M1"
            },
            {
                "group": "jakarta.interceptor",
                "artefact": "jakarta.interceptor-api",
                "version": "2.2.0-M1"
            },
            {
                "group": "jakarta.persistence",
                "artefact": "jakarta.persistence-api",
                "version": "3.1.0"
            },
            {
                "group": "org.jboss.logging",
                "artefact": "jboss-logging",
                "version": "3.3.2.Final"
            },
            {
                "group": "io.smallrye",
                "artefact": "jandex",
                "version": "3.1.2"
            },
            {
                "group": "com.fasterxml",
                "artefact": "classmate",
                "version": "1.5.1"
            },
            {
                "group": "org.glassfish.jaxb",
                "artefact": "jaxb-runtime",
                "version": "4.0.4"
            },
            {
                "group": "org.glassfish.jaxb",
                "artefact": "jaxb-core",
                "version": "4.0.4"
            },
            {
                "group": "org.eclipse.angus",
                "artefact": "angus-activation",
                "version": "2.0.1"
            },
            {
                "group": "org.glassfish.jaxb",
                "artefact": "txw2",
                "version": "4.0.4"
            },
            {
                "group": "com.sun.istack",
                "artefact": "istack-commons-runtime",
                "version": "4.1.2"
            },
            {
                "group": "jakarta.inject",
                "artefact": "jakarta.inject-api",
                "version": "2.0.1"
            },
            {
                "group": "org.antlr",
                "artefact": "antlr4-runtime",
                "version": "4.13.0"
            },
            {
                "group": "org.hibernate.validator",
                "artefact": "hibernate-validator",
                "version": "8.0.1.Final"
            },
            {
                "group": "jakarta.validation",
                "artefact": "jakarta.validation-api",
                "version": "3.1.0-M1"
            },
            {
                "group": "net.bytebuddy",
                "artefact": "byte-buddy",
                "version": "1.12.19"
            },
            {
                "group": "org.javassist",
                "artefact": "javassist",
                "version": "3.18.1-GA"
            },
            {
                "group": "org.apache.logging.log4j",
                "artefact": "log4j-to-slf4j",
                "version": "2.24.3"
            },
            {
                "group": "org.apache.logging.log4j",
                "artefact": "log4j-api",
                "version": "2.24.3"
            },
            {
                "group": "org.slf4j",
                "artefact": "jul-to-slf4j",
                "version": "2.0.12"
            },
            {
                "group": "com.github.ulisesbocchio",
                "artefact": "jasypt-spring-boot-starter",
                "version": "3.0.3"
            },
            {
                "group": "com.github.ulisesbocchio",
                "artefact": "jasypt-spring-boot",
                "version": "3.0.3"
            },
            {
                "group": "org.slf4j",
                "artefact": "slf4j-api",
                "version": "2.0.12"
            },
            {
                "group": "ch.qos.logback",
                "artefact": "logback-core",
                "version": "1.5.16"
            },
            {
                "group": "ch.qos.logback",
                "artefact": "logback-classic",
                "version": "1.5.16"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-jms-spring-arjuna",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "me.snowdrop",
                "artefact": "narayana-spring-boot-starter",
                "version": "3.0.0.redhat-00042"
            },
            {
                "group": "me.snowdrop",
                "artefact": "narayana-spring-boot-core",
                "version": "3.0.0.redhat-00042"
            },
            {
                "group": "org.jboss.narayana.jts",
                "artefact": "narayana-jts-integration",
                "version": "6.0.1.Final-redhat-00002"
            },
            {
                "group": "org.messaginghub",
                "artefact": "pooled-jms",
                "version": "3.1.0"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-jms-spring",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-jms-client",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "jakarta.jms",
                "artefact": "jakarta.jms-api",
                "version": "3.1.0"
            },
            {
                "group": "org.jboss.narayana.jta",
                "artefact": "jta",
                "version": "7.0.0.Final"
            },
            {
                "group": "org.jboss.narayana",
                "artefact": "common",
                "version": "7.0.0.Final"
            },
            {
                "group": "org.jboss.narayana.arjunacore",
                "artefact": "arjuna",
                "version": "7.0.0.Final"
            },
            {
                "group": "org.jboss.narayana.jta",
                "artefact": "jms",
                "version": "7.0.0.Final"
            },
            {
                "group": "jakarta.transaction",
                "artefact": "jakarta.transaction-api",
                "version": "2.0.0"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-h2-standalone-persistence",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-standalone-persistence",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-flow",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.ikasan",
                "artefact": "ikasan-spec-event",
                "version": "4.1.1-SNAPSHOT"
            },
            {
                "group": "org.jmock",
                "artefact": "jmock",
                "version": "2.12.0"
            },
            {
                "group": "org.jmock",
                "artefact": "jmock-testjar",
                "version": "2.12.0"
            },
            {
                "group": "com.google.code.findbugs",
                "artefact": "annotations",
                "version": "3.0.1"
            },
            {
                "group": "com.google.code.findbugs",
                "artefact": "jsr305",
                "version": "3.0.1"
            },
            {
                "group": "org.apache.activemq",
                "artefact": "activemq-client",
                "version": "6.1.6"
            },
            {
                "group": "org.fusesource.hawtbuf",
                "artefact": "hawtbuf",
                "version": "1.11"
            },
            {
                "group": "org.apache.activemq",
                "artefact": "activemq-broker",
                "version": "6.1.6"
            },
            {
                "group": "org.apache.activemq",
                "artefact": "activemq-openwire-legacy",
                "version": "6.1.6"
            },
            {
                "group": "jakarta.annotation",
                "artefact": "jakarta.annotation-api",
                "version": "2.1.1"
            },
            {
                "group": "com.fasterxml.jackson.core",
                "artefact": "jackson-databind",
                "version": "2.16.0"
            },
            {
                "group": "com.fasterxml.jackson.core",
                "artefact": "jackson-annotations",
                "version": "2.16.0"
            }
        ]
    },
    "parameterizedTypes": [
        {
            "componentName": "JMS Consumer",
            "implementingClassName": "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
            "typeParameters": [
                {
                    "name": "LISTENER",
                    "type": "org.ikasan.spec.event.EventListener<?>"
                },
                {
                    "name": "EVENT_FACTORY",
                    "type": "org.ikasan.spec.event.EventFactory"
                }
            ]
        }
    ]
}
```
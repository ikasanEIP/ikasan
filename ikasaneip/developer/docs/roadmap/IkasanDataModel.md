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
        "name": "my-first-ikasan-module",
        "description": "This is my first attempt to build Ikasan using an agent",
        "version": "1.0.0-SNAPSHOT",
        "type": "INTEGRATION_MODULE",
        "url": "http://localhost:8080",
        "flows": [
            {
                "name": "JMS to Database Flow",
                "consumer": {
                    "componentName": "JMS Consumer",
                    "componentType": "consumer",
                    "implementingClass": "org.ikasan.component.jms.JmsConsumer",
                    "isConfigurable": true,
                    "configurationId": "jms-consumer-config"
                },
                "flowElements": [
                    {
                        "componentName": "Database Producer",
                        "componentType": "producer",
                        "implementingClass": "org.ikasan.agent.sample.DbProducer",
                        "isConfigurable": true,
                        "configurationId": "db-producer-config"
                    }
                ],
                "transitions": [
                    {
                        "from": "JMS Consumer",
                        "to": "Database Producer",
                        "name": "default"
                    }
                ]
            },
            {
                "name": "DB to JMS Flow",
                "consumer": {
                    "componentName": "Database Consumer",
                    "componentType": "consumer",
                    "implementingClass": "org.ikasan.agent.demo.DBConsumer",
                    "isConfigurable": true,
                    "configurationId": "db-consumer-config"
                },
                "flowElements": [
                    {
                        "componentName": "JMS Producer",
                        "componentType": "producer",
                        "implementingClass": "org.ikasan.component.jms.JmsProducer",
                        "isConfigurable": true,
                        "configurationId": "jms-producer-config-db-flow"
                    }
                ],
                "transitions": [
                    {
                        "from": "Database Consumer",
                        "to": "JMS Producer",
                        "name": "default"
                    }
                ]
            }
        ]
    },
    "configurationMetaData": [
        {
            "configurationId": "jms-consumer-config",
            "description": "Configuration for the JMS Consumer",
            "implementingClass": "org.ikasan.component.jms.JmsConsumerConfiguration",
            "parameters": [
                {
                    "name": "connectionFactoryName",
                    "value": "${jms.consumer.connectionFactoryName}",
                    "description": "The name of the JMS ConnectionFactory bean to use.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "destinationName",
                    "value": "${inbound.queue}",
                    "description": "The name of the JMS queue to consume from.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "autoContentConversion",
                    "value": "${jms.consumer.autoContentConversion}",
                    "description": "Whether to automatically convert message content.",
                    "implementingClass": "java.lang.Boolean"
                },
                {
                    "name": "acknowledgeMode",
                    "value": "${jms.consumer.acknowledgeMode}",
                    "description": "The JMS acknowledgement mode (e.g., AUTO_ACKNOWLEDGE, CLIENT_ACKNOWLEDGE).",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "sessionTransacted",
                    "value": "${jms.consumer.sessionTransacted}",
                    "description": "Whether the JMS session is transacted.",
                    "implementingClass": "java.lang.Boolean"
                },
                {
                    "name": "pubSubDomain",
                    "value": "${jms.consumer.pubSubDomain}",
                    "description": "Whether the destination is a topic (true) or a queue (false).",
                    "implementingClass": "java.lang.Boolean"
                },
                {
                    "name": "durableSubscriptionName",
                    "value": "${jms.consumer.durableSubscriptionName}",
                    "description": "The name of the durable subscription (for topics).",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "messageSelector",
                    "value": "${jms.consumer.messageSelector}",
                    "description": "The JMS message selector.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "concurrentConsumers",
                    "value": "${jms.consumer.concurrentConsumers}",
                    "description": "The number of concurrent consumers.",
                    "implementingClass": "java.lang.Integer"
                },
                {
                    "name": "maxConcurrentConsumers",
                    "value": "${jms.consumer.maxConcurrentConsumers}",
                    "description": "The maximum number of concurrent consumers.",
                    "implementingClass": "java.lang.Integer"
                },
                {
                    "name": "receiveTimeout",
                    "value": "${jms.consumer.receiveTimeout}",
                    "description": "The timeout for receiving messages in milliseconds.",
                    "implementingClass": "java.lang.Long"
                },
                {
                    "name": "idleTaskExecutionLimit",
                    "value": "${jms.consumer.idleTaskExecutionLimit}",
                    "description": "The maximum number of idle executions of the receive task.",
                    "implementingClass": "java.lang.Integer"
                },
                {
                    "name": "idleConsumerLimit",
                    "value": "${jms.consumer.idleConsumerLimit}",
                    "description": "The maximum number of idle consumers.",
                    "implementingClass": "java.lang.Integer"
                }
            ]
        },
        {
            "configurationId": "db-producer-config",
            "description": "Configuration for the Database Producer",
            "implementingClass": "org.ikasan.agent.sample.DbProducerConfiguration",
            "parameters": [
                {
                    "name": "dataSourceName",
                    "value": "${db.datasource.name}",
                    "description": "The name of the DataSource bean to use for database operations.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "sqlStatement",
                    "value": "${db.producer.sql}",
                    "description": "The SQL statement to execute for each message.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "batchSize",
                    "value": "${db.producer.batchSize}",
                    "description": "The number of records to process in a single batch.",
                    "implementingClass": "java.lang.Integer"
                }
            ]
        },
        {
            "configurationId": "db-consumer-config",
            "description": "Configuration for the Database Consumer",
            "implementingClass": "org.ikasan.agent.demo.DBConsumerConfiguration",
            "parameters": [
                {
                    "name": "dataSourceName",
                    "value": "${db.consumer.datasource.name}",
                    "description": "The name of the DataSource bean to use for database polling.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "sqlQuery",
                    "value": "${db.consumer.sql.query}",
                    "description": "The SQL query to execute to retrieve data.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "pollingFrequency",
                    "value": "${db.consumer.polling.frequency}",
                    "description": "The frequency (in milliseconds) at which the database is polled.",
                    "implementingClass": "java.lang.Long"
                },
                {
                    "name": "maxRows",
                    "value": "${db.consumer.max.rows}",
                    "description": "The maximum number of rows to retrieve per poll.",
                    "implementingClass": "java.lang.Integer"
                }
            ]
        },
        {
            "configurationId": "jms-producer-config-db-flow",
            "description": "Configuration for the JMS Producer in DB to JMS Flow",
            "implementingClass": "org.ikasan.component.jms.JmsProducerConfiguration",
            "parameters": [
                {
                    "name": "connectionFactoryName",
                    "value": "${jms.producer.connectionFactoryName}",
                    "description": "The name of the JMS ConnectionFactory bean to use.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "destinationName",
                    "value": "${jms.producer.destinationName}",
                    "description": "The name of the JMS queue or topic to publish to.",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "timeToLive",
                    "value": "${jms.producer.timeToLive}",
                    "description": "The time to live for messages in milliseconds.",
                    "implementingClass": "java.lang.Long"
                },
                {
                    "name": "deliveryMode",
                    "value": "${jms.producer.deliveryMode}",
                    "description": "The JMS delivery mode (PERSISTENT or NON_PERSISTENT).",
                    "implementingClass": "java.lang.String"
                },
                {
                    "name": "priority",
                    "value": "${jms.producer.priority}",
                    "description": "The JMS message priority (0-9).",
                    "implementingClass": "java.lang.Integer"
                },
                {
                    "name": "explicitQosEnabled",
                    "value": "${jms.producer.explicitQosEnabled}",
                    "description": "Whether explicit Quality of Service (QoS) is enabled.",
                    "implementingClass": "java.lang.Boolean"
                },
                {
                    "name": "pubSubDomain",
                    "value": "${jms.producer.pubSubDomain}",
                    "description": "Whether the destination is a topic (true) or a queue (false).",
                    "implementingClass": "java.lang.Boolean"
                },
                {
                    "name": "sessionTransacted",
                    "value": "${jms.producer.sessionTransacted}",
                    "description": "Whether the JMS session is transacted.",
                    "implementingClass": "java.lang.Boolean"
                },
                {
                    "name": "sessionAcknowledgeMode",
                    "value": "${jms.producer.sessionAcknowledgeMode}",
                    "description": "The JMS acknowledgement mode for the session.",
                    "implementingClass": "java.lang.String"
                }
            ]
        }
    ]
}
```
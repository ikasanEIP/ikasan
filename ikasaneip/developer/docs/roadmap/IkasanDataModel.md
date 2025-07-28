![Problem Domain](../quickstart-images/Ikasan-title-transparent.png)

# Theme 1: The Ikasan Module JSON Data Model (The Foundation)

The foundation for our developer tooling and AI-driven features is a standardized JSON representation of an Ikasan module. Crucially, this JSON data model is **directly derived from and maps to existing Ikasan core classes**, ensuring consistency and leveraging the established domain model.

*   **Goal: Leverage Existing Ikasan Metadata Classes**
    *   **Action:** The JSON data model will directly reflect the structure of Ikasan classes.
    *   **Action:** The JSON structure, as detailed below, will serve as the canonical representation for module definitions.
    *   **Action:** Ensure that any tooling (IDE, AI generator) directly works with this established data model for both input and output, minimizing transformation overhead and maintaining fidelity with the Ikasan core.

## ModuleManifestMetaDataImpl JSON Data Model

This document provides a detailed description of the JSON data model for `ModuleManifestMetaDataImpl.java`, which represents the metadata of an Ikasan module. This metadata is essential for understanding the module's structure, configurations, and dependencies.

### Data Model Class Diagram

````mermaid
classDiagram
    class ModuleManifestMetaData {
        +ModuleMetaData moduleMetaData
        +List~ConfigurationMetaData~ configurationMetaData
        +DependencyManagementMetaData dependencyManagement
        +List~ParameterizedType~ parameterizedTypes
        +List~ConstructorMetaData~ constructorMetaData
        +List~BeanDefinitionMetaData~ beanDefinitionMetaData
    }
    class ModuleMetaData {
        +String name
        +String version
        +String description
        +String type
        +String url
        +String ikasanVersion
        +List~FlowMetaData~ flows
        +String configuredResourceId
        +String host
        +Integer port
        +String context
        +String protocol
    }
    class FlowMetaData {
        +String name
        +FlowElementMetaData consumer
        +List~Transition~ transitions
        +List~FlowElementMetaData~ flowElements
        +String configurationId
        +String flowStartupType
        +String flowStartupComment
    }
    class FlowElementMetaData {
        +String componentName
        +String description
        +String componentType
        +String implementingClass
        +boolean configurable
        +String configurationId
        +String invokerConfigurationId
        +List~DecoratorMetaData~ decorators
    }
    class DecoratorMetaData {
        +String name
        +String type
        +boolean configurable
        +String configurationId
    }
    class Transition {
        +String from
        +String to
        +String name
    }
    class ConfigurationMetaData {
        +String configurationId
        +List~ConfigurationParameterMetaData~ parameters
        +String description
        +String implementingClass
    }
    class ConfigurationParameterMetaData {
        +Long id
        +String name
        +Object value
        +String description
        +String implementingClass
    }
    class DependencyManagementMetaData {
        +List~RepositoryMetaData~ repositories
        +List~DependencyMetaData~ dependencies
    }
    class RepositoryMetaData {
        +String id
        +String url
    }
    class DependencyMetaData {
        +String group
        +String artefact
        +String version
    }
    class ParameterizedType {
        +String implementingClassName
        +List~TypeParameter~ typeParameters
    }
    class ConstructorMetaData {
        +String componentName
        +String className
        +List~TypeParameter~ constructorArguments
    }
    class TypeParameter {
        +String name
        +String type
    }
    class BeanDefinitionMetaData {
        +String beanName
        +String type
        +String beanClass
        +String beanResource
    }

    ModuleManifestMetaData "1" *-- "1" ModuleMetaData
    ModuleManifestMetaData "1" *-- "0..*" ConfigurationMetaData
    ModuleManifestMetaData "1" *-- "1" DependencyManagementMetaData
    ModuleManifestMetaData "1" *-- "0..*" ParameterizedType
    ModuleManifestMetaData "1" *-- "0..*" ConstructorMetaData
    ModuleManifestMetaData "1" *-- "0..*" BeanDefinitionMetaData

    ModuleMetaData "1" *-- "0..*" FlowMetaData

    FlowMetaData "1" *-- "1" FlowElementMetaData : consumer
    FlowMetaData "1" *-- "0..*" Transition
    FlowMetaData "1" *-- "0..*" FlowElementMetaData

    FlowElementMetaData "1" *-- "0..*" DecoratorMetaData

    ConfigurationMetaData "1" *-- "0..*" ConfigurationParameterMetaData

    DependencyManagementMetaData "1" *-- "0..*" RepositoryMetaData
    DependencyManagementMetaData "1" *-- "0..*" DependencyMetaData

    ParameterizedType "1" *-- "0..*" TypeParameter
    ConstructorMetaData "1" *-- "0..*" TypeParameter
````

### JSON Structure

The JSON object consists of the following top-level properties:

-   `moduleMetaData`: General information about the module.
-   `configurationMetaData`: A list of metadata for the module's configurations.
-   `dependencyManagement`: Information about the module's dependencies.
-   `parameterizedTypes`: A list of parameterized types used in the module.
-   `constructorMetaData`: A list of metadata for the module's constructors.
-   `beanDefinitionMetaData`: A list of metadata for the bean definitions within the module.

### Detailed Field Descriptions

#### ModuleMetaData

This object contains fundamental information about the module.

| Field                | Type       | Description                                                                 |
| -------------------- | ---------- | --------------------------------------------------------------------------- |
| `name`               | `String`   | The name of the module.                                                     |
| `version`            | `String`   | The version of the module.                                                  |
| `description`        | `String`   | A brief description of the module's purpose.                                |
| `type`               | `String`   | The type of the module (e.g., `Integration Module`).                        |
| `url`                | `String`   | The URL of the module.                                                      |
| `ikasanVersion`      | `String`   | The version of the Ikasan platform the module is built for.                 |
| `flows`              | `Array`    | A list of flow metadata objects within the module.                          |
| `configuredResourceId` | `String`   | The ID for the module's configuration.                                      |
| `host`               | `String`   | The host where the module is running.                                       |
| `port`               | `Integer`  | The port the module is running on.                                          |
| `context`            | `String`   | The context path for the module.                                            |
| `protocol`           | `String`   | The protocol used by the module (e.g., `http`).                             |

##### FlowMetaData

| Field                | Type      | Description                                                                 |
| -------------------- | --------- | --------------------------------------------------------------------------- |
| `name`               | `String`  | The name of the flow.                                                       |
| `consumer`           | `Object`  | The flow's consumer metadata.                                               |
| `transitions`        | `Array`   | A list of transitions between flow elements.                                |
| `flowElements`       | `Array`   | A list of all elements within the flow.                                     |
| `configurationId`    | `String`  | The ID for the flow's configuration.                                        |
| `flowStartupType`    | `String`  | The startup type for the flow.                                              |
| `flowStartupComment` | `String`  | A comment about the flow's startup.                                         |

##### Consumer

| Field                 | Type      | Description                                                                 |
| --------------------- | --------- | --------------------------------------------------------------------------- |
| `componentName`       | `String`  | The name of the consumer component.                                         |
| `description`         | `String`  | A description of the consumer.                                              |
| `componentType`       | `String`  | The type of the consumer component.                                         |
| `implementingClass`   | `String`  | The fully qualified class name of the consumer's implementation.            |
| `configurable`        | `Boolean` | Whether the consumer is configurable.                                       |
| `configurationId`     | `String`  | The ID for the consumer's configuration.                                    |
| `invokerConfigurationId` | `String`  | The ID for the consumer's invoker configuration.                            |
| `decorators`          | `Array`   | A list of decorators applied to the consumer.                               |

##### Decorators

| Field             | Type      | Description                                                                 |
| ----------------- | --------- | --------------------------------------------------------------------------- |
| `name`            | `String`  | The name of the decorator.                                                  |
| `type`            | `String`  | The type of the decorator.                                                  |
| `configurable`    | `Boolean` | Whether the decorator is configurable.                                      |
| `configurationId` | `String`  | The ID for the decorator's configuration.                                   |

##### Transitions

| Field | Type     | Description                               |
| ----- | -------- | ----------------------------------------- |
| `from`  | `String` | The name of the source flow element.      |
| `to`    | `String` | The name of the target flow element.      |
| `name`  | `String` | The name of the transition.               |

##### FlowElementMetaData

| Field                 | Type      | Description                                                                 |
| --------------------- | --------- | --------------------------------------------------------------------------- |
| `componentName`       | `String`  | The name of the flow element.                                               |
| `description`         | `String`  | A description of the flow element.                                          |
| `componentType`       | `String`  | The type of the flow element.                                               |
| `implementingClass`   | `String`  | The fully qualified class name of the flow element's implementation.        |
| `configurable`        | `Boolean` | Whether the flow element is configurable.                                   |
| `configurationId`     | `String`  | The ID for the flow element's configuration.                                |
| `invokerConfigurationId` | `String`  | The ID for the flow element's invoker configuration.                        |
| `decorators`          | `Array`   | A list of decorators applied to the flow element.                           |

##### ConfigurationMetaData

An array of objects, each describing a specific configuration within the module.

| Field               | Type      | Description                                                                 |
| ------------------- | --------- | --------------------------------------------------------------------------- |
| `configurationId`   | `String`  | A unique identifier for the configuration.                                  |
| `parameters`        | `Object`  | A map of configuration parameters.                                          |
| `description`       | `String`  | A description of the configuration's purpose.                               |
| `implementingClass` | `String`  | The fully qualified class name of the configuration's implementation.       |

##### ConfigurationParameters

| Field             | Type      | Description                                                                 |
| ----------------- | --------- | --------------------------------------------------------------------------- |
| `id`              | `Long`    | The ID of the configuration parameter.                                      |
| `name`            | `String`  | The name of the configuration parameter.                                    |
| `value`           | `Any`     | The value of the configuration parameter.                                   |
| `description`     | `String`  | A description of the configuration parameter.                               |
| `implementingClass` | `String`  | The fully qualified class name of the configuration parameter's implementation. |

##### DependencyManagement

This object details the module's dependencies.

| Field          | Type    | Description                                      |
| -------------- | ------- | ------------------------------------------------ |
| `repositories` | `Array` | A list of Maven repositories used by the module. |
| `dependencies` | `Array` | A list of the module's dependencies.            |

##### Repositories

| Field | Type     | Description                               |
| ----- | -------- | ----------------------------------------- |
| `id`    | `String` | The ID of the repository.                 |
| `url`   | `String` | The URL of the repository.                |

#### Dependencies

| Field      | Type     | Description                               |
| ---------- | -------- | ----------------------------------------- |
| `group`    | `String` | The group ID of the dependency.           |
| `artefact` | `String` | The artifact ID of the dependency.        |
| `version`  | `String` | The version of the dependency.            |

### ParameterizedTypes

An array of objects, each representing a parameterized type used in the module.

| Field                 | Type     | Description                                                              |
| --------------------- | -------- | ------------------------------------------------------------------------ |
| `implementingClassName` | `String` | The fully qualified class name of the parameterized type.                |
| `typeParameters`      | `Array`  | A list of the type parameters.                                           |

#### TypeParameter

| Field | Type     | Description                               |
| ----- | -------- | ----------------------------------------- |
| `name`  | `String` | The name of the type parameter.           |
| `type`  | `String` | The type of the type parameter.           |

##### ConstructorMetaData

An array of objects, each describing a constructor within the module.

| Field                | Type     | Description                                                              |
| -------------------- | -------- | ------------------------------------------------------------------------ |
| `componentName`      | `String` | The name of the component.                                               |
| `className`          | `String` | The fully qualified class name of the component.                         |
| `constructorArguments` | `Array`  | A list of the constructor's arguments.                                   |

##### ConstructorArguments 

| Field | Type     | Description                               |
| ----- | -------- | ----------------------------------------- |
| `name`  | `String` | The name of the constructor argument.     |
| `type`  | `String` | The type of the constructor argument.     |

##### BeanDefinitionMetaData

An array of objects, each providing metadata for a bean definition within the module.

| Field          | Type     | Description                                                              |
| -------------- | -------- | ------------------------------------------------------------------------ |
| `beanName`     | `String` | The name of the bean.                                                    |
| `type`         | `String` | The scope of the bean (e.g., `singleton`).                               |
| `beanClass`    | `String` | The fully qualified class name of the bean.                              |
| `beanResource` | `String` | The resource where the bean is defined (e.g., a classpath XML file).     |

## Example

```json
{
    "moduleMetaData" : {
        "url" : "http://localhost:0/jms-demo",
        "host" : "localhost",
        "port" : 0,
        "context" : "/jms-demo",
        "protocol" : "http",
        "name" : "jms-demo",
        "description" : "Sample Module",
        "version" : "1.0.0-SNAPSHOT",
        "ikasanVersion" : "4.1.1-SNAPSHOT",
        "flows" : [ {
            "name" : "JMS FLow",
            "consumer" : {
                "componentName" : "JMS Consumer",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
                "configurationId" : "jmsConsumer",
                "invokerConfigurationId" : "jms-demo_JMS FLow_JMS Consumer_1165847135_I",
                "decorators" : null,
                "configurable" : true
            },
            "transitions" : [ {
                "from" : "My Very Special Translator",
                "to" : "JMS Producer",
                "name" : "default"
            }, {
                "from" : "JMS Consumer",
                "to" : "Exception Generating Broker",
                "name" : "default"
            } , {
                "to" : "My Very Special Translator",
                "from" : "Exception Generating Broker",
                "name" : "default"
            } ],
            "flowElements" : [ {
                "componentName" : "JMS Producer",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Producer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
                "configurationId" : "jmsProducer",
                "invokerConfigurationId" : "jms-demo_JMS FLow_JMS Producer_1165847135_I",
                "decorators" : null,
                "configurable" : true
            }, {
                "componentName" : "Exception Generating Broker",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Broker",
                "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.ExceptionGenerationgBroker",
                "configurationId" : null,
                "invokerConfigurationId" : "jms-demo_JMS FLow_Exception Generating Broker_1165847135_I",
                "decorators" : null,
                "configurable" : false
            }, {
                "componentName" : "My Very Special Translator",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.transformation.Translator",
                "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.translator.MyCustomTranslator",
                "configurationId" : "myTranslator",
                "invokerConfigurationId" : "jms-demo_JMS FLow_Exception Generating Broker_1165847135_I",
                "decorators" : null,
                "configurable" : false
            }, {
                "componentName" : "JMS Consumer",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
                "configurationId" : "jmsConsumer",
                "invokerConfigurationId" : "jms-demo_JMS FLow_JMS Consumer_1165847135_I",
                "decorators" : null,
                "configurable" : true
            } ],
            "configurationId" : "jms-demo-JMS FLow",
            "flowStartupType" : "MANUAL",
            "flowStartupComment" : null
        }, {
            "name" : "Recipient List FLow",
            "consumer" : {
                "componentName" : "My Recipient Flow JMS Consumer",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
                "configurationId" : "myRecipientFlowConsumer",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow JMS Consumer_1165847135_I",
                "decorators" : null,
                "configurable" : true
            },
            "transitions" : [ {
                "from" : "My Recipient Flow Filter",
                "to" : "My Recipient Flow JMS Producer 1",
                "name" : "default"
            }, {
                "from" : "My Recipient Flow Router",
                "to" : "My Recipient Flow Filter",
                "name" : "1"
            }, {
                "from" : "MySingleRecipientRouter",
                "to" : "My Recipient Flow JMS Producer 4",
                "name" : "true"
            }, {
                "from" : "MySingleRecipientRouter",
                "to" : "My Recipient Flow JMS Producer 5",
                "name" : "false"
            }, {
                "from" : "My Recipient Flow Router",
                "to" : "MySingleRecipientRouter",
                "name" : "2"
            }, {
                "from" : "My Recipient Flow Router",
                "to" : "My Recipient Flow JMS Producer 3",
                "name" : "3"
            }, {
                "from" : "My Recipient Flow Converter",
                "to" : "My Recipient Flow Router",
                "name" : "default"
            }, {
                "from" : "My Recipient Flow JMS Consumer",
                "to" : "My Recipient Flow Converter",
                "name" : "default"
            } ],
            "flowElements" : [ {
                "componentName" : "My Recipient Flow JMS Producer 1",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Producer",
                "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.producer.CustomJMSProducer",
                "configurationId" : "myRecipientFlowProducer1",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow JMS Producer 1_1165847135_I",
                "decorators" : null,
                "configurable" : true
            }, {
                "componentName" : "My Recipient Flow Filter",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.filter.Filter",
                "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.filter.MyFilter",
                "configurationId" : null,
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow Filter_1096089527_I",
                "decorators" : null,
                "configurable" : false
            }, {
                "componentName" : "My Recipient Flow JMS Producer 4",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Producer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
                "configurationId" : "myRecipientFlowProducer4",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow JMS Producer 4_1165847135_I",
                "decorators" : null,
                "configurable" : true
            }, {
                "componentName" : "My Recipient Flow JMS Producer 5",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Producer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
                "configurationId" : "myRecipientFlowProducer5",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow JMS Producer 5_1165847135_I",
                "decorators" : null,
                "configurable" : true
            }, {
                "componentName" : "MySingleRecipientRouter",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.routing.SingleRecipientRouter",
                "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.router.MySingleRecipientRouter",
                "configurationId" : "mySingleRecipientRouter_configuration_id",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_MySingleRecipientRouter_1165847135_I",
                "decorators" : null,
                "configurable" : true
            }, {
                "componentName" : "My Recipient Flow JMS Producer 3",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Producer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
                "configurationId" : "myRecipientFlowProducer3",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow JMS Producer 3_1165847135_I",
                "decorators" : null,
                "configurable" : true
            }, {
                "componentName" : "My Recipient Flow Router",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.routing.MultiRecipientRouter",
                "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.router.MyMultiRecipientRouter",
                "configurationId" : null,
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow Router_-255997752_I",
                "decorators" : null,
                "configurable" : false
            }, {
                "componentName" : "My Recipient Flow Converter",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.transformation.Converter",
                "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.converter.MyRecipientFlowConverter",
                "configurationId" : "MyRecipientFlowConverter_configuration_id",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow Converter_1165847135_I",
                "decorators" : null,
                "configurable" : true
            }, {
                "componentName" : "My Recipient Flow JMS Consumer",
                "description" : null,
                "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
                "implementingClass" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
                "configurationId" : "myRecipientFlowConsumer",
                "invokerConfigurationId" : "jms-demo_Recipient List FLow_My Recipient Flow JMS Consumer_1165847135_I",
                "decorators" : null,
                "configurable" : true
            } ],
            "configurationId" : "jms-demo-Recipient List FLow",
            "flowStartupType" : "MANUAL",
            "flowStartupComment" : null
        } ],
        "configuredResourceId" : null,
        "type" : "INTEGRATION_MODULE"
    },
    "configurationMetaData" : [ {
        "configurationId" : "jmsConsumer",
        "description" : null,
        "implementingClass" : "org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "autoContentConversion",
            "value" : true,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "autoSplitBatch",
            "value" : true,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "batchMode",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "batchSize",
            "value" : 0,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "cacheLevel",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "concurrentConsumers",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "source",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "durable",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "durableSubscriptionName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "maxConcurrentConsumers",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        } ]
    },{
        "configurationId" : "myTranslator",
        "description" : null,
        "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.translator.configuration.MyCustomTranslatorConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "autoContentConversion",
            "value" : true,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "autoSplitBatch",
            "value" : true,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "batchMode",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "batchSize",
            "value" : 0,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "cacheLevel",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "concurrentConsumers",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "source",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "durable",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "durableSubscriptionName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "maxConcurrentConsumers",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        } ]
    }, {
        "configurationId" : "jmsProducer",
        "description" : null,
        "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "deliveryMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "deliveryPersistent",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "target",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "explicitQosEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageIdEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageTimestampEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "priority",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "pubSubNoLocal",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "receiveTimeout",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeModeName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "timeToLive",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        } ]
    }, {
        "configurationId" : "myRecipientFlowConsumer",
        "description" : null,
        "implementingClass" : "org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "autoContentConversion",
            "value" : true,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "autoSplitBatch",
            "value" : true,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "batchMode",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "batchSize",
            "value" : 0,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "cacheLevel",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "concurrentConsumers",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "myRecipientFlowConsumerSource",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "durable",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "durableSubscriptionName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "maxConcurrentConsumers",
            "value" : 1,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        } ]
    }, {
        "configurationId" : "MyRecipientFlowConverter_configuration_id",
        "description" : null,
        "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.converter.configuration.MyRecipientFlowConverterConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "intValue",
            "value" : 0,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "longValue",
            "value" : 0,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        }, {
            "id" : null,
            "name" : "stringValue",
            "value" : "my string value",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "values",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterListImpl"
        } ]
    }, {
        "configurationId" : "mySingleRecipientRouter_configuration_id",
        "description" : null,
        "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.router.configuration.MySingleRecipientRouterConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "intValue",
            "value" : 0,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "longValue",
            "value" : 0,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        }, {
            "id" : null,
            "name" : "stringValue",
            "value" : "my string value",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "values",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterListImpl"
        } ]
    }, {
        "configurationId" : "myRecipientFlowProducer3",
        "description" : null,
        "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "deliveryMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "deliveryPersistent",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "myRecipientFlowProducer1Target",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "explicitQosEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageIdEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageTimestampEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "priority",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "pubSubNoLocal",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "receiveTimeout",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeModeName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "timeToLive",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        } ]
    }, {
        "configurationId" : "myRecipientFlowProducer1",
        "description" : null,
        "implementingClass" : "com.ikasan.sample.spring.boot.builderpattern.components.producer.configuration.CustomJMSProducerConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "deliveryMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "deliveryPersistent",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "myRecipientFlowProducer1Target",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "explicitQosEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageIdEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageTimestampEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "priority",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "pubSubNoLocal",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "receiveTimeout",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeModeName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "timeToLive",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        } ]
    }, {
        "configurationId" : "myRecipientFlowProducer4",
        "description" : null,
        "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "deliveryMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "deliveryPersistent",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "myRecipientFlowProducer1Target",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "explicitQosEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageIdEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageTimestampEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "priority",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "pubSubNoLocal",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "receiveTimeout",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeModeName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "timeToLive",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        } ]
    }, {
        "configurationId" : "myRecipientFlowProducer5",
        "description" : null,
        "implementingClass" : "org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration",
        "parameters" : [ {
            "id" : null,
            "name" : "connectionFactoryJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryPassword",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "connectionFactoryUsername",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "deliveryMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "deliveryPersistent",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiName",
            "value" : "myRecipientFlowProducer1Target",
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "destinationJndiProperties",
            "value" : { },
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
        }, {
            "id" : null,
            "name" : "explicitQosEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageIdEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "messageTimestampEnabled",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "priority",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "pubSubDomain",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "pubSubNoLocal",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "receiveTimeout",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeMode",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
        }, {
            "id" : null,
            "name" : "sessionAcknowledgeModeName",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
        }, {
            "id" : null,
            "name" : "sessionTransacted",
            "value" : false,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl"
        }, {
            "id" : null,
            "name" : "timeToLive",
            "value" : null,
            "description" : null,
            "implementingClass" : "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
        } ]
    } ],
    "dependencyManagement": {
        "repositories": [
            {
                "id": "central",
                "url": "https://repo.maven.apache.org/maven2"
            }
        ],
        "dependencies": [
            {
                "groupId": "org.apache.activemq",
                "artifactId": "activemq-client",
                "version": "5.16.3"
            }
        ]
    },
    "parameterizedTypes" : [ {
        "implementingClassName" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
        "typeParameters" : [ {
            "name" : "LISTENER",
            "type" : "org.ikasan.spec.event.EventListener<?>"
        }, {
            "name" : "EVENT_FACTORY",
            "type" : "org.ikasan.spec.event.EventFactory"
        } ]
    }, {
        "implementingClassName" : "com.ikasan.sample.spring.boot.builderpattern.components.filter.MyFilter",
        "typeParameters" : [ {
            "name" : "T",
            "type" : "java.lang.String"
        } ]
    }, {
        "implementingClassName" : "com.ikasan.sample.spring.boot.builderpattern.components.router.MySingleRecipientRouter",
        "typeParameters" : [ {
            "name" : "T",
            "type" : "java.lang.String"
        } ]
    }, {
        "implementingClassName" : "com.ikasan.sample.spring.boot.builderpattern.components.router.MyMultiRecipientRouter",
        "typeParameters" : [ {
            "name" : "T",
            "type" : "java.lang.String"
        } ]
    }, {
        "implementingClassName" : "com.ikasan.sample.spring.boot.builderpattern.components.converter.MyRecipientFlowConverter",
        "typeParameters" : [ {
            "name" : "SOURCE",
            "type" : "java.lang.String"
        }, {
            "name" : "TARGET",
            "type" : "java.lang.String"
        } ]
    }, {
        "implementingClassName" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
        "typeParameters" : [ {
            "name" : "LISTENER",
            "type" : "org.ikasan.spec.event.EventListener<?>"
        }, {
            "name" : "EVENT_FACTORY",
            "type" : "org.ikasan.spec.event.EventFactory"
        } ]
    } ],
    "constructorMetaData" : [ {
        "componentName" : "JMS Producer",
        "className" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
        "constructorArguments" : [ {
            "name" : "arg0",
            "type" : "org.springframework.jms.core.IkasanJmsTemplate"
        } ]
    }, {
        "componentName" : "Exception Generating Broker",
        "className" : "com.ikasan.sample.spring.boot.builderpattern.ExceptionGenerationgBroker",
        "constructorArguments" : [ {
            "name" : "arg0",
            "type" : "java.lang.String"
        } ]
    }, {
        "componentName" : "JMS Consumer",
        "className" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
        "constructorArguments" : [ ]
    }, {
        "componentName" : "My Recipient Flow JMS Producer 1",
        "className" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
        "constructorArguments" : [ {
            "name" : "arg0",
            "type" : "org.springframework.jms.core.IkasanJmsTemplate"
        } ]
    }, {
        "componentName" : "My Recipient Flow Filter",
        "className" : "com.ikasan.sample.spring.boot.builderpattern.components.filter.MyFilter",
        "constructorArguments" : [ ]
    }, {
        "componentName" : "My Recipient Flow JMS Producer 4",
        "className" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
        "constructorArguments" : [ {
            "name" : "arg0",
            "type" : "org.springframework.jms.core.IkasanJmsTemplate"
        } ]
    }, {
        "componentName" : "My Recipient Flow JMS Producer 5",
        "className" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
        "constructorArguments" : [ {
            "name" : "arg0",
            "type" : "org.springframework.jms.core.IkasanJmsTemplate"
        } ]
    }, {
        "componentName" : "MySingleRecipientRouter",
        "className" : "com.ikasan.sample.spring.boot.builderpattern.components.router.MySingleRecipientRouter",
        "constructorArguments" : [ ]
    }, {
        "componentName" : "My Recipient Flow JMS Producer 3",
        "className" : "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer",
        "constructorArguments" : [ {
            "name" : "arg0",
            "type" : "org.springframework.jms.core.IkasanJmsTemplate"
        } ]
    }, {
        "componentName" : "My Recipient Flow Router",
        "className" : "com.ikasan.sample.spring.boot.builderpattern.components.router.MyMultiRecipientRouter",
        "constructorArguments" : [ ]
    }, {
        "componentName" : "My Recipient Flow Converter",
        "className" : "com.ikasan.sample.spring.boot.builderpattern.components.converter.MyRecipientFlowConverter",
        "constructorArguments" : [ ]
    }, {
        "componentName" : "My Recipient Flow JMS Consumer",
        "className" : "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer",
        "constructorArguments" : [ ]
    } ],
    "beanDefinitionMetaData" : [ {
        "beanName" : "jmsTemplate",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.springframework.jms.core.JmsTemplate",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.ModuleTestConfig"
    }, {
        "beanName" : "thirdBroker",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "com.ikasan.sample.spring.boot.builderpattern.ExceptionGenerationgBroker",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/test-classes/test-beans.xml"
    }, {
        "beanName" : "anotherTestString",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "java.lang.String",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/test-classes/test-beans.xml"
    }, {
        "beanName" : "getModule",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.module.Module",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.ModuleConfig"
    }, {
        "beanName" : "exceptionGeneratingBroker",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "com.ikasan.sample.spring.boot.builderpattern.ExceptionGenerationgBroker",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.ModuleConfig"
    }, {
        "beanName" : "dependency",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "java.lang.String",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.ModuleConfig"
    }, {
        "beanName" : "recipientFlow",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.flow.Flow",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myMultiRecipientRouter",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.routing.MultiRecipientRouter",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myFilter",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.filter.Filter",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myRecipientFlowConsumer",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.endpoint.Consumer",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myRecipientFlowProducer1",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.endpoint.Producer",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myRecipientFlowProducer2",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.endpoint.Producer",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myRecipientFlowProducer3",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.endpoint.Producer",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myRecipientFlowProducer4",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.endpoint.Producer",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "myRecipientFlowProducer5",
        "type" : "CONFIGURATION_CLASS_BEAN_DEFINITION",
        "beanClass" : "org.ikasan.spec.component.endpoint.Producer",
        "beanResource" : "com.ikasan.sample.spring.boot.builderpattern.flow.RecipientRouterFlowConfiguration"
    }, {
        "beanName" : "secondBroker",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "com.ikasan.sample.spring.boot.builderpattern.ExceptionGenerationgBroker",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/classes/beans.xml"
    }, {
        "beanName" : "testString",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "java.lang.String",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/classes/beans.xml"
    }, {
        "beanName" : "myRecipientFlowConverter",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "com.ikasan.sample.spring.boot.builderpattern.components.converter.MyRecipientFlowConverter",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/classes/beans.xml"
    }, {
        "beanName" : "myRecipientFlowConverterConfiguration",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "com.ikasan.sample.spring.boot.builderpattern.components.converter.configuration.MyRecipientFlowConverterConfiguration",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/classes/beans.xml"
    }, {
        "beanName" : "mySingleRecipientRouter",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "com.ikasan.sample.spring.boot.builderpattern.components.router.MySingleRecipientRouter",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/classes/beans.xml"
    }, {
        "beanName" : "mySingleRecipientRouterConfiguration",
        "type" : "XML_BEAN_DEFINITION",
        "beanClass" : "com.ikasan.sample.spring.boot.builderpattern.components.router.configuration.MySingleRecipientRouterConfiguration",
        "beanResource" : "/Users/mick/workspace/archetype/jms-demo/jar/target/classes/beans.xml"
    } ]
}
```

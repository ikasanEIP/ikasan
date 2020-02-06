![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Topology
The Ikasan Topology module provides various views onto the Ikasan estate.


The aim of the Ikasan Metadata element of the topology module is to provide the ability for an Ikasan runtime module to be queried in order for the
module to describe itself. The module will have the ability to describe the following:

- The module will describe details of itself including its name and description, along with details of all associated flows.
- Each flow will describe all the components within it along with the routes and transitions between all the components.

A contract has been defined in order to support both the serialisation and de-serialisation of a module 
called [ModuleMetaDataProvider](../spec/metadata/src/main/java/org/ikasan/spec/metadata/ModuleMetaDataProvider.java).

````java
public interface ModuleMetaDataProvider<T>
{
    /**
     * Method to convert a module into a description of it.
     *
     * @param module module we are describing.
     * @return the description of the flow.
     */
    public T describeModule(Module<Flow> module);

    /**
     * Method to deserialise a meta data representation of a flow.
     *
     * @param module the module meta data
     * @return the deserialised module meta data
     */
    public ModuleMetaData deserialiseModule(T module);
}
````

There is currently one implementation of the ModuleMetaDataProvider which supports JSON [JsonModuleMetaDataProvider](./src/main/java/org/ikasan/topology/metadata/JsonModuleMetaDataProvider.java).

The JSON below is an example of the meta data for a module:

````json
{
  "name" : "module name",
  "description" : "module description",
  "version" : "module version",
  "flows" : [ {
    "name" : "Simple Flow 1",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    },
    "transitions" : [ {
      "from" : "Test Converter",
      "to" : "Test Producer",
      "name" : "default"
    }, {
      "from" : "Test Broker",
      "to" : "Test Converter",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Filter",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer",
      "description" : "Test Producer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  }, {
    "name" : "Simple Flow 2",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    },
    "transitions" : [ {
      "from" : "Test Converter",
      "to" : "Test Producer",
      "name" : "default"
    }, {
      "from" : "Test Broker",
      "to" : "Test Converter",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Filter",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer",
      "description" : "Test Producer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  }, {
    "name" : "Multi Flow 1",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.ConfiguredConsumer",
      "configurationId" : "CONFIGURATION_ID",
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : true
    },
    "transitions" : [ {
      "from" : "Test Broker",
      "to" : "Test Producer 2",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Multi Recipient Router",
      "to" : "Test Filter",
      "name" : "route 2"
    }, {
      "from" : "Test Converter",
      "to" : "Test Producer",
      "name" : "default"
    }, {
      "from" : "Test Multi Recipient Router",
      "to" : "Test Converter",
      "name" : "route 1"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Multi Recipient Router",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer 2",
      "description" : "Test Producer 2 Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Producer",
      "description" : "Test Producer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Multi Recipient Router",
      "description" : "Test Multi Recipient Router Description",
      "componentType" : "org.ikasan.spec.component.routing.MultiRecipientRouter",
      "implementingClass" : "org.ikasan.metadata.components.TestMultiRecipientRouter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.ConfiguredConsumer",
      "configurationId" : "CONFIGURATION_ID",
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : true
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  }, {
    "name" : "Multi Flow 2",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.ConfiguredConsumer",
      "configurationId" : "CONFIGURATION_ID",
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : true
    },
    "transitions" : [ {
      "from" : "Test Broker",
      "to" : "Test Producer 2",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Multi Recipient Router",
      "to" : "Test Filter",
      "name" : "route 2"
    }, {
      "from" : "Test Converter",
      "to" : "Test Producer",
      "name" : "default"
    }, {
      "from" : "Test Multi Recipient Router",
      "to" : "Test Converter",
      "name" : "route 1"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Multi Recipient Router",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer 2",
      "description" : "Test Producer 2 Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Producer",
      "description" : "Test Producer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Multi Recipient Router",
      "description" : "Test Multi Recipient Router Description",
      "componentType" : "org.ikasan.spec.component.routing.MultiRecipientRouter",
      "implementingClass" : "org.ikasan.metadata.components.TestMultiRecipientRouter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.ConfiguredConsumer",
      "configurationId" : "CONFIGURATION_ID",
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : true
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  }, {
    "name" : "Single Flow 1",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    },
    "transitions" : [ {
      "from" : "Test Converter",
      "to" : "Test Producer 2",
      "name" : "default"
    }, {
      "from" : "Test Broker",
      "to" : "Test Converter",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Single Recipient Router",
      "to" : "Test Filter",
      "name" : "route 2"
    }, {
      "from" : "Test Converter",
      "to" : "Test Producer 2",
      "name" : "default"
    }, {
      "from" : "Test Single Recipient Router",
      "to" : "Test Converter",
      "name" : "route 1"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Single Recipient Router",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer 2",
      "description" : "Test Producer 2 Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Producer 2",
      "description" : "Test Producer 2 Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Single Recipient Router",
      "description" : "Test Single Recipient Router Description",
      "componentType" : "org.ikasan.spec.component.routing.SingleRecipientRouter",
      "implementingClass" : "org.ikasan.metadata.components.TestSingleRecipientRouter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  }, {
    "name" : "Single Flow 2",
    "consumer" : {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    },
    "transitions" : [ {
      "from" : "Test Converter",
      "to" : "Test Producer 2",
      "name" : "default"
    }, {
      "from" : "Test Broker",
      "to" : "Test Converter",
      "name" : "default"
    }, {
      "from" : "Test Splitter",
      "to" : "Test Broker",
      "name" : "default"
    }, {
      "from" : "Test Filter",
      "to" : "Test Splitter",
      "name" : "default"
    }, {
      "from" : "Test Single Recipient Router",
      "to" : "Test Filter",
      "name" : "route 2"
    }, {
      "from" : "Test Converter",
      "to" : "Test Producer 2",
      "name" : "default"
    }, {
      "from" : "Test Single Recipient Router",
      "to" : "Test Converter",
      "name" : "route 1"
    }, {
      "from" : "Test Consumer",
      "to" : "Test Single Recipient Router",
      "name" : "default"
    } ],
    "flowElements" : [ {
      "componentName" : "Test Producer 2",
      "description" : "Test Producer 2 Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Broker",
      "description" : "Test Broker Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Broker",
      "implementingClass" : "org.ikasan.metadata.components.TestBroker",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Splitter",
      "description" : "Test Splitter Description",
      "componentType" : "org.ikasan.spec.component.splitting.Splitter",
      "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Filter",
      "description" : "Test Filter Description",
      "componentType" : "org.ikasan.spec.component.filter.Filter",
      "implementingClass" : "org.ikasan.metadata.components.TestFilter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Producer 2",
      "description" : "Test Producer 2 Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Producer",
      "implementingClass" : "org.ikasan.metadata.components.TestProducer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Converter",
      "description" : "Test Converter Description",
      "componentType" : "org.ikasan.spec.component.transformation.Converter",
      "implementingClass" : "org.ikasan.metadata.components.TestConverter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Single Recipient Router",
      "description" : "Test Single Recipient Router Description",
      "componentType" : "org.ikasan.spec.component.routing.SingleRecipientRouter",
      "implementingClass" : "org.ikasan.metadata.components.TestSingleRecipientRouter",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    }, {
      "componentName" : "Test Consumer",
      "description" : "Test Consumer Description",
      "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
      "implementingClass" : "org.ikasan.metadata.components.TestConsumer",
      "configurationId" : null,
      "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
      "configurable" : false
    } ],
    "configurationId" : "FLOW_CONFIGURATION_ID"
  } ]
}
````

A contract has been defined in order to support both the serialisation and de-serialisation of a flow 
called [FlowMetaDataProvider](../spec/metadata/src/main/java/org/ikasan/spec/metadata/FlowMetaDataProvider.java).

````java
public interface FlowMetaDataProvider<T>
{
    /**
     * Method to convert a flow into a description of it.
     *
     * @param flow the flow we are describing.
     * @return the description of the flow.
     */
    public T describeFlow(Flow flow);

    /**
     * Method to deserialise a meta data representation of a flow.
     *
     * @param flow the flow meta data
     * @return the deserialised flow meta data
     */
    public FlowMetaData deserialiseFlow(T flow);
}
````
There is currently one implementation of the FlowMetaDataProvider which supports JSON [JsonFlowMetaDataProvider](./src/main/java/org/ikasan/topology/metadata/JsonFlowMetaDataProvider.java).

The JSON below is an example of the meta data for a flow:

```json
{
  "name" : "Flow Name",
  "consumer" : {
    "componentName" : "Test Consumer",
    "description" : "Test Consumer Description",
    "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
    "implementingClass" : "org.ikasan.metadata.components.ConfiguredConsumer",
    "configurationId" : "CONFIGURATION_ID",
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : true,
    "decorators" : [ {
          "type" : "Wiretap",
          "name" : "BEFORE Test Consumer",
          "configurable" : false
        }, {
          "configurationId" : "wiretap_Id",
          "type" : "Wiretap",
          "name" : "BEFORE Test Consumer",
          "configurable" : true
        } ]
  },
  "transitions" : [ {
    "from" : "Test Broker",
    "to" : "Test Producer 2",
    "name" : "default"
  }, {
    "from" : "Test Splitter",
    "to" : "Test Broker",
    "name" : "default"
  }, {
    "from" : "Test Filter",
    "to" : "Test Splitter",
    "name" : "default"
  }, {
    "from" : "Test Multi Recipient Router",
    "to" : "Test Filter",
    "name" : "route 2"
  }, {
    "from" : "Test Converter",
    "to" : "Test Producer",
    "name" : "default"
  }, {
    "from" : "Test Multi Recipient Router",
    "to" : "Test Converter",
    "name" : "route 1"
  }, {
    "from" : "Test Consumer",
    "to" : "Test Multi Recipient Router",
    "name" : "default"
  } ],
  "flowElements" : [ {
    "componentName" : "Test Producer 2",
    "description" : "Test Producer 2 Description",
    "componentType" : "org.ikasan.spec.component.endpoint.Producer",
    "implementingClass" : "org.ikasan.metadata.components.TestProducer",
    "configurationId" : null,
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : false,
    "decorators" : [ {
              "configurationId" : "WiretapId",
              "type" : "Wiretap",
              "name" : "AFTER Test Producer 2",
              "configurable" : true
            }
     ]
  }, {
    "componentName" : "Test Broker",
    "description" : "Test Broker Description",
    "componentType" : "org.ikasan.spec.component.endpoint.Broker",
    "implementingClass" : "org.ikasan.metadata.components.TestBroker",
    "configurationId" : null,
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : false
  }, {
    "componentName" : "Test Splitter",
    "description" : "Test Splitter Description",
    "componentType" : "org.ikasan.spec.component.splitting.Splitter",
    "implementingClass" : "org.ikasan.metadata.components.TestSplitter",
    "configurationId" : null,
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : false
  }, {
    "componentName" : "Test Filter",
    "description" : "Test Filter Description",
    "componentType" : "org.ikasan.spec.component.filter.Filter",
    "implementingClass" : "org.ikasan.metadata.components.TestFilter",
    "configurationId" : null,
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : false
  }, {
    "componentName" : "Test Producer",
    "description" : "Test Producer Description",
    "componentType" : "org.ikasan.spec.component.endpoint.Producer",
    "implementingClass" : "org.ikasan.metadata.components.TestProducer",
    "configurationId" : null,
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : false
  }, {
    "componentName" : "Test Converter",
    "description" : "Test Converter Description",
    "componentType" : "org.ikasan.spec.component.transformation.Converter",
    "implementingClass" : "org.ikasan.metadata.components.TestConverter",
    "configurationId" : null,
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : false
  }, {
    "componentName" : "Test Multi Recipient Router",
    "description" : "Test Multi Recipient Router Description",
    "componentType" : "org.ikasan.spec.component.routing.MultiRecipientRouter",
    "implementingClass" : "org.ikasan.metadata.components.TestMultiRecipientRouter",
    "configurationId" : null,
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : false
  }, {
    "componentName" : "Test Consumer",
    "description" : "Test Consumer Description",
    "componentType" : "org.ikasan.spec.component.endpoint.Consumer",
    "implementingClass" : "org.ikasan.metadata.components.ConfiguredConsumer",
    "configurationId" : "CONFIGURATION_ID",
    "invokerConfigurationId" : "FLOW_INVOKER_CONFIGURATION_ID",
    "configurable" : true
  } ],
  "configurationId" : "FLOW_CONFIGURATION_ID"
}
```



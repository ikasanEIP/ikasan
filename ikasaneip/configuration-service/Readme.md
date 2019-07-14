![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Configuration Service
The Ikasan Configuration Service is responsible for converting POJO injected on Components, Invokers or Flows (known as ConfiguredResources) into Configuration objects persisted in a Ikasan DB.


The aim of the Ikasan Metadata element of the configuration service is to provide the ability for Configured Resources of Ikasan runtime to be describe in chosen metadata format.
The metadata package provide helper implementations to:
- serialise from Configuration Resources to meta data format
- deserialise from metadata format to Configuration Resources
- extract Configuration Resources from Ikasan runtime and convert them to meta data format 
  - Components
  - Invokers
  - Flows 

A contract has been defined in order to support both the serialisation and de-serialisation of a configured resources 
called [ConfigurationMetaDataProvider](../spec/metadata/src/main/java/org/ikasan/spec/metadata/ConfigurationMetaDataProvider.java).

````java
public interface ConfigurationMetaDataProvider<T>
{
    /**
     * Method to deserialise a meta data representation of a single configuration metadata.
     *
     * @param metaData the configuration meta data
     * @return the deserialised configuration meta data
     */
    ConfigurationMetaData deserialiseMetadataConfiguration(T metaData);

    /**
     * Method to deserialise a meta data representation of a list of configuration metadata.
     *
     * @param metaData the configuration list meta data
     * @return the deserialised list of configuration meta data
     */
    List<ConfigurationMetaData> deserialiseMetadataConfigurations(T metaData);

    /**
     * Method to convert a list of configured resources into a description of it.
     *
     * @param configuredResources list of configured resource we are describing.
     * @return the description of the list of configured resources.
     */
    T describeConfiguredResources(List<ConfiguredResource> configuredResources);

    /**
     * Method to convert a configured resource into a description of it.
     *
     * @param configuredResource configured Resource we are describing.
     * @return the description of the configured resource.
     */
    T describeConfiguredResource(ConfiguredResource configuredResource);
}
````

There is currently one implementation of the ConfigurationMetaDataProvider which supports JSON [JsonConfigurationMetaDataProvider](./src/main/java/org/ikasan/configurationService/metadata/JsonConfigurationMetaDataProvider.java).

The JSON below is an example of the meta data for a configuration resource:

````json
{
  "configurationId" : "configuredResourceId",
  "description" : null,
  "implementingClass" : "org.ikasan.configurationService.model.DefaultConfiguration",
  "parameters" : [ {
    "id" : null,
    "name" : "name",
    "value" : "value",
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : 10,
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : 10,
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : [ "one", "two", "three" ],
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterListImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : {
      "one" : "1",
      "two" : "2",
      "three" : "3"
    },
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
  } ]
}
````
 
A contract has been defined in order to support extraction of a configured resources from Ikasan runtime and to convert them meta data format,
called [ConfigurationMetaDataExtractor](../spec/metadata/src/main/java/org/ikasan/spec/metadata/ConfigurationMetaDataExtractor.java).


````java

public interface ConfigurationMetaDataProvider<T>
{
    /**
     * Method to deserialise a meta data representation of a single configuration metadata.
     *
     * @param metaData the configuration meta data
     * @return the deserialised configuration meta data
     */
    ConfigurationMetaData deserialiseMetadataConfiguration(T metaData);

    /**
     * Method to deserialise a meta data representation of a list of configuration metadata.
     *
     * @param metaData the configuration list meta data
     * @return the deserialised list of configuration meta data
     */
    List<ConfigurationMetaData> deserialiseMetadataConfigurations(T metaData);

    /**
     * Method to convert a list of configured resources into a description of it.
     *
     * @param configuredResources list of configured resource we are describing.
     * @return the description of the list of configured resources.
     */
    T describeConfiguredResources(List<ConfiguredResource> configuredResources);

    /**
     * Method to convert a configured resource into a description of it.
     *
     * @param configuredResource configured Resource we are describing.
     * @return the description of the configured resource.
     */
    T describeConfiguredResource(ConfiguredResource configuredResource);
}

````
There is currently one implementation of the ConfigurationMetaDataExtractor which supports JSON [JsonConfigurationMetaDataExtractor](./src/main/java/org/ikasan/configurationService/metadata/JsonConfigurationMetaDataExtractor.java).

The JSON below is an example of the meta data for a module:

```json
[ {
  "configurationId" : "consumerConfiguredResourceId",
  "description" : "desc",
  "implementingClass" : "org.ikasan.configurationService.model.DefaultConfiguration",
  "parameters" : [ {
    "id" : null,
    "name" : "name",
    "value" : "value",
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : 10,
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : 10,
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : [ "one", "two", "three" ],
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterListImpl"
  }, {
    "id" : null,
    "name" : "name",
    "value" : {
      "one" : "1",
      "two" : "2",
      "three" : "3"
    },
    "description" : "desc",
    "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
  } ]
},
  {
    "configurationId" : "producerConfiguredResourceId",
    "description" : "desc",
    "implementingClass" : "org.ikasan.configurationService.model.DefaultConfiguration",
    "parameters" : [ {
      "id" : null,
      "name" : "name",
      "value" : "value",
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterStringImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : 10,
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : 10,
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterLongImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : [ "one", "two", "three" ],
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterListImpl"
    }, {
      "id" : null,
      "name" : "name",
      "value" : {
        "one" : "1",
        "two" : "2",
        "three" : "3"
      },
      "description" : "desc",
      "implementingClass": "org.ikasan.configurationService.model.ConfigurationParameterMapImpl"
    } ]
  }]
```



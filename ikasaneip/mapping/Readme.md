[../](../../Readme.md)
![Problem Domain](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Mapping Service
<img src="../developer/docs/quickstart-images/mapping.gif" width="200px" align="left">
The mapping service provides a unified and centralised approach for mapping values between source and target contexts. The service was desiged in order to 
remove the need for each individial system or interface to provide their own mappings, and replace that with an enterprise wide and standardised approach to
context based mapping. The types of mappings that are supported are flexible as follows: One to One, One to Many, Many to Many and Many to One.<br/>
Mappings are uniquely defined by type Client, Type, Source Context and Target Context and are managed through the Ikasan Dashboard. Mappings
can be imported and exported into an XML format as seen in the example below. 
<br/>
<br/>

**An example of a mapping configuration file.**

 ```xml
<?xml version="1.0" encoding="UTF-8"?>
<mappingConfiguration
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="200">
    <exportDateTime>30 April 2019 11:01:56 BST</exportDateTime>
    <client>IkasanESB</client>
    <type>ProductType</type>
    <sourceContext>SourceSystem</sourceContext>
    <targetContext>TargetSystem</targetContext>
    <description>ProductType - SourceSystem to TargetSystem</description>
    <isManyToMany>false</isManyToMany>
    <isFixedParameterListSize>false</isFixedParameterListSize>
    <numberOfSourceParams>1</numberOfSourceParams>
    <numberOfTargetParams>0</numberOfTargetParams>
    <mappingConfigurationValues>
        <mappingConfigurationValue>
            <sourceConfigurationValues>
                <sourceConfigurationValue>Widget1</sourceConfigurationValue>
            </sourceConfigurationValues>
            <targetConfigurationValue>W1</targetConfigurationValue>
        </mappingConfigurationValue>
        <mappingConfigurationValue>
            <sourceConfigurationValues>
                <sourceConfigurationValue>Widget2</sourceConfigurationValue>
            </sourceConfigurationValues>
            <targetConfigurationValue>W2</targetConfigurationValue>
        </mappingConfigurationValue>
        <mappingConfigurationValue>
            <sourceConfigurationValues>
                <sourceConfigurationValue>Widget3</sourceConfigurationValue>
            </sourceConfigurationValues>
            <targetConfigurationValue>W3</targetConfigurationValue>
        </mappingConfigurationValue>
        <mappingConfigurationValue>
            <sourceConfigurationValues>
                <sourceConfigurationValue>Widget4</sourceConfigurationValue>
            </sourceConfigurationValues>
            <targetConfigurationValue>W4</targetConfigurationValue>
        </mappingConfigurationValue>
        <mappingConfigurationValue>
            <sourceConfigurationValues>
                <sourceConfigurationValue>Widget5</sourceConfigurationValue>
            </sourceConfigurationValues>
            <targetConfigurationValue>W5</targetConfigurationValue>
        </mappingConfigurationValue>
    </mappingConfigurationValues>
</mappingConfiguration>
```

Ikasan provides a service interface [MappingService](ikasaneip/spec/service/mapping/src/main/java/org/ikasan/spec/mapping/MappingService.java) in order to access
to a mapping from within an Ikasan module. To include the mapping service libraries in an Imasan module include the following dependency in the pom.

```xml
<dependency>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-mapping</artifactId>
    <version>${version.ikasan}</version>
</dependency>
```

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
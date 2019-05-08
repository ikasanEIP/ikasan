[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## JSON XML Converter
[JsonXmlConverter.java](src/main/java/org/ikasan/component/converter/xml/JsonXmlConverter.java)

### Purpose

<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. A coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html)
[JsonXmlConverter](src/main/java/org/ikasan/component/converter/xml/JsonXmlConverter.java) is an implementation of the [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java)
. 

The purpose of this converter is to take a JSON payload and convert it to an XML payload. It does this by delegating to a [Marshaller](../../marshaller/xml-marshaller/src/main/java/org/ikasan/marshaller/Marshaller.java).
There are various Marshaller implementations available in Ikasan. See [Marshallers](../../marshaller/Readme.md) for more details of the various implementations.
<br/>
##### Configuration Options
Not applicable as this is not a configurable resource.

##### Sample Usage
The sample below defines a simple module with one flow that translates a JSON payload received on
JMS channel, into XML before publishing the XML payload to another JMS channel. 
```java
public class ModuleConfig {

  @Value("${jms.provider.url}")
  private String brokerUrl;

  @Value("${jms.connectionFactory.jndi.name}")
  private String jmsConnectionFactoryName;

  @Value("${jms.naming.factory.initial}")
  private String jmsNamingFactoryInitial;

  @Value("${jms.source.destination}")
  private String jmsSourceDestination;

  @Value("${jms.target.destination}")
  private String jmsTargetDestination;

  @Bean
  public Module getModule()
  {

      // get a module builder from the ikasanApplication
      ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("sample-builder-pattern").withDescription("Example module with pattern builder");

      Flow jmsFlow = getJmsFlow(moduleBuilder, builderFactory.getComponentBuilder());

      // add flows to the module
      Module module = moduleBuilder.addFlow(jmsFlow).build();

      return module;
  }

  public Flow getJmsFlow(ModuleBuilder moduleBuilder,ComponentBuilder componentBuilder) {
          FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Jms Flow");
  
          return flowBuilder.withDescription("Jms flow description")
              .consumer("consumer", componentBuilder.jmsConsumer().setConfiguredResourceId("configuredResourceId")
                  .setDestinationJndiName(jmsSourceDestination)
                  .setConnectionFactoryName(jmsConnectionFactoryName)
                  .setConnectionFactoryJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                  .setConnectionFactoryJndiPropertyProviderUrl(brokerUrl)
                  .setDestinationJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                  .setDestinationJndiPropertyProviderUrl(brokerUrl)
                  .setAutoContentConversion(true)
                  .build()
              )
              .converter("JSON to XML", getConverter())
              .producer("producer", componentBuilder.jmsProducer()
                  .setConfiguredResourceId("crid")
                  .setDestinationJndiName(jmsTargetDestination)
                  .setConnectionFactoryName(jmsConnectionFactoryName)
                  .setConnectionFactoryJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                  .setConnectionFactoryJndiPropertyProviderUrl(brokerUrl)
                  .setDestinationJndiPropertyFactoryInitial(jmsNamingFactoryInitial)
                  .setDestinationJndiPropertyProviderUrl(brokerUrl).build()
              )
              .build();
  
      }
  
      public Converter getConverter()
      {
          return new JsonXmlConverter(new XmlStringJsonMarshaller(new XMLSerializer()));
      }
}
````

### Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |

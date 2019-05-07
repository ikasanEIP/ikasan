[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## Map Message to Object Converter

### Purpose

<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. Coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html)
In order to create your own converter you need to implement [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java)
<br/>
##### Configuration Options
| Option | Type | Purpose |
| --- | --- | --- |
| attributeName | String | The attribute from the MapMessage to be converted to an Object |

##### Sample Usage
The sample below defines a simple module with one flow that translates an attribute on a map message to an Object. 
````java
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
  
  @Value("${map.message.attribute.name}")
  private String mapMessageAttributeName;

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
          MapMessageToObjectConverterConfiguration converterConfiguration = new MapMessageToObjectConverterConfiguration();
          converterConfiguration.setAttributeName(mapMessageAttributeName);
          MapMessageToObjectConverter converter =  new MapMessageToObjectConverter();
          converter.setConfiguration(converterConfiguration);
          converter.setConfiguredResourceId("mapMessageTpObjectConverterConfigId");
          
          return converter;
      }
}
````

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |

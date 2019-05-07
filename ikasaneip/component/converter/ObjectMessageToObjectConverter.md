[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## Object Message to Object Converter
[ObjectMessageToObjectConverter.java](src/main/java/org/ikasan/component/converter/jms/ObjectMessageToObjectConverter.java)

### Purpose

<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. Coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html).  

The [Object Message to Objcet Converter](./src/main/java/org/ikasan/component/converter/jms/ObjectMessageToObjectConverter.java)
is an implementation of the  [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java). 
It provides a mechanism that translates a JMS [ObjectMessge](https://javaee.github.io/javaee-spec/javadocs/javax/jms/ObjectMessage.html)
into an [Object](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html), by retrieving the content Object
from the ObjectMessge and returning this Object. 
<br/>


##### Configuration Options
Not applicable as this is not a [Configured Resource](../../spec/service/configuration/src/main/java/org/ikasan/spec/configuration/ConfiguredResource.java).

##### Sample Usage
````java
public class ModuleConfig
{
    @Resource BuilderFactory builderFactory;

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

        // get an instance of flowBuilder from the moduleBuilder and create a flow
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
            .converter("Object Message to Object", this.getConverter())
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

    private Converter getConverter()
    {
        return new ObjectMessageToObjectConverter();
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

[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## XML String to Object Converter
[XmlStringToObjectConverter.java](src/main/java/org/ikasan/component/converter/xml/XmlStringToObjectConverter.java)

### Purpose
<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. Coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html).  

The [XML String to Object Converter](./src/main/java/org/ikasan/component/converter/xml/XmlStringToObjectConverter.java)
is an implementation of the  [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java). 
It provides a mechanism that translates [String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html) representation of an XML Document and converts it to a
[JAXB Object](https://docs.oracle.com/javase/tutorial/jaxb/intro/index.html) which is a materialised Java POJO
that has been initialised with the contents of the XML. 
<br/>


##### Configuration Options
| Option | Type | Default |Purpose |
| --- | --- | --- | --- |
| classesToBeBound | Class<?>[] |  | |
| contextPath | String |  | |
| contextPaths | String[] |  | |
| schema | String |  | |
| autoConvertElementToValue | Boolean | | 
| unmarshallerProperties | Map<String, Object> |  |  |
| marshallerProperties | Map<String, Object> |  |  |
| validationEventHandler | [ValidationEventHandler](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/ValidationEventHandler.html) |  | Should we use namespace prefixes? |

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
            .converter("XML String to JAXB Object", this.getConverter())
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
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();

        Class[] classes = new Class[1];
        classes[0] = MyJaxb.class;

        configuration.setClassesToBeBound(classes);

        XmlStringToObjectConverter converter =  new XmlStringToObjectConverter();
        converter.setConfiguredResourceId("id");
        converter.setConfiguration(configuration);

        return converter;
    }
}
````

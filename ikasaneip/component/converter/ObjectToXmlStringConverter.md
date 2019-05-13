[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## Object to XML String Converter
[ObjectMessageToObjectConverter.java](src/main/java/org/ikasan/component/converter/xml/ObjectToXMLStringConverter.java)

### Purpose

<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. Coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html).  

The [Object Message to XML String Converter](./src/main/java/org/ikasan/component/converter/xml/ObjectToXMLStringConverter.java)
is an implementation of the  [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java). 
It provides a mechanism that translates a [JAXB Object](https://docs.oracle.com/javase/tutorial/jaxb/intro/index.html)
into an XML representation of that Object as a String. 
<br/>


##### Configuration Options
| Option | Type | Default |Purpose |
| --- | --- | --- | --- |
| fastFailOnConfigurationLoad | Boolean |  | Determines whether we let the component fail if the initial setting and loading of configuration fails. |
| schema | String |  | Actual schema against which validation will occur |
| schemaLocation | String |  | Schema location as put in the root attribute of the generated XML |
| noNamespaceSchema | String |  | Is this a no namespace schema? |
| validate | Boolean |  | Whether to validate the generated XML against the schema |
| useNamespacePrefix | Boolean |  | Should we use namespace prefixes? |
| rootName | String |  | Optionally set root name ([QName](https://docs.oracle.com/javase/8/docs/api/javax/xml/namespace/QName.html)) |
| namespaceURI | String |  | Optionally set namespace URI (for [QName](https://docs.oracle.com/javase/8/docs/api/javax/xml/namespace/QName.html)) |
| namespacePrefix | String |  | Optionally set namespace prefix (for [QName](https://docs.oracle.com/javase/8/docs/api/javax/xml/namespace/QName.html)) |
| rootClassName | String |  | Optionally set root class name (for [QName](https://docs.oracle.com/javase/8/docs/api/javax/xml/namespace/QName.html)) |
| routeOnValidationException | Boolean |  | Whether to route an XML validation failure to the next component (true) or throw an exception and rollback (false) |

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
            .converter("Object Message to Object", this.getObjectConverter())
            .converter("JAXB Object to XML String", getObjectToXMLStringConverter())
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

    private Converter getObjectConverter()
    {
        return new ObjectMessageToObjectConverter();
    }
    
    private Converter getObjectToXMLStringConverter()
    {
        XmlConfiguration configuration = new XmlConfiguration();
        configuration.setFastFailOnConfigurationLoad(false);
        configuration.setValidate(false);
        configuration.setRouteOnValidationException(false);

        ObjectToXMLStringConverter converter = new ObjectToXMLStringConverter(MyJaxB.class);
        converter.setConfiguredResourceId("id");
        converter.setConfiguration(configuration);

        return converter
    }
}
````

##### Sample Usage (Builder)
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
            .converter("Object Message to Object", this.getObjectConverter())
            // Using the componennt builder here to create the converter.
            .converter("JAXB Object to XML String", componentBuilder.objectToXmlStringConverter()
                .setObjectClass(MyJaxB.class)
                .setFastFailOnConfigurationLoad(false)
                .setValidate(false)
                .setRouteOnValidationException(false)
                .build())
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

    private Converter getObjectConverter()
    {
        return new ObjectMessageToObjectConverter();
    }
}
````

[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## Map Message to Payload Converter
[MapMessageToPayloadConverter.java](src/main/java/org/ikasan/component/converter/filetransfer/MapMessageToPayloadConverter.java)

### Purpose

<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. Coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html).  

The [Map Message to Payload Converter](./src/main/java/org/ikasan/component/converter/filetransfer/MapMessageToPayloadConverter.java)
is an implementation of the  [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java). 
It provides a mechanism that translates a JMS [MapMessge](https://javaee.github.io/javaee-spec/javadocs/javax/jms/MapMessage.html)
into an [Payload](../endpoint/filetransfer/common/src/main/java/org/ikasan/filetransfer/Payload.java), by retrieving the content Object
from the MapMessage using the content attribute name provided on the configuration and converting it to a Payload. 
<br/>


##### Configuration Options
| Option | Type | Default |Purpose |
| --- | --- | --- | --- |
| contentAttributeName | String | content |The content attribute from the MapMessage to be converted to an Payload |
| idAttributeName | String | fileName |The id attribute from the MapMessage to be used as the id on the Payload |
| fileNameAttributeName | String | fileName |The file name to be used on the Payload |


##### Sample Usage
````java
public class ModuleConfig {

    @Resource
    private BuilderFactory builderFactory;

    @Value("${ftp.producer.clientID}")
    private String ftpProducerClientID;

    @Value("${ftp.producer.username}")
    private String ftpProducerUsername;

    @Value("${ftp.producer.password}")
    private String ftpProducerPassword;

    @Value("${ftp.producer.remoteHost}")
    private String ftpProducerRemoteHost;

    @Value("${ftp.producer.remotePort}")
    private Integer ftpProducerRemotePort;

    @Value("${ftp.producer.outputDirectory}")
    private String ftpProducerOutputDirectory;


    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-ftp-jms");

        Flow jmsToFtpFlow = getJmsToFtpFlow(mb,builderFactory.getComponentBuilder());

        Module module = mb.withDescription("Ftp Jms Sample Module")
                .addFlow(jmsToFtpFlow).build();
        return module;

    }


    public Flow getJmsToFtpFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
        Consumer ftpJmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
            .setConnectionFactory(consumerConnectionFactory)
            .setDestinationJndiName("ftp.private.jms.queue")
            //.setAutoContentConversion(true)
            .setConfiguredResourceId("ftpJmsConsumer")
            .build();

        Producer ftpProducer = componentBuilder.ftpProducer()
            .setClientID(ftpProducerClientID)
            .setUsername(ftpProducerUsername)
            .setPassword(ftpProducerPassword)
            .setRemoteHost(ftpProducerRemoteHost)
            .setRemotePort(ftpProducerRemotePort)
            .setOutputDirectory(ftpProducerOutputDirectory)
            .setOverwrite(true)
            .setConfiguredResourceId("ftpProducerConfiguration")
            .build();

        FlowBuilder jmsToFtpFlowBuilder = moduleBuilder.getFlowBuilder("Jms To Ftp Flow");
        Flow jmsToftpFlow = jmsToFtpFlowBuilder
                .withDescription("Receives Text Jms message and sends it to FTP as file")
                .consumer("Ftp Jms Consumer", ftpJmsConsumer)
                .converter("MapMessage to FTP Payload Converter", this.getConverter())
                .producer("Ftp Producer", ftpProducer)
                .build();

        return jmsToftpFlow;
    }

    public Converter getConverter()
    {
        MapMessageToPayloadConverterConfiguration configuration = new MapMessageToPayloadConverterConfiguration();
        configuration.setContentAttributeName("myCustomContent");
        configuration.setIdAttributeName("myCustomId");
        configuration.setFileNameAttributeName("MyFile.csv");
        
        MapMessageToPayloadConverter mapMessageToPayloadConverter = new MapMessageToPayloadConverter();
        mapMessageToPayloadConverter.setConfiguredResourceId("configurationId");
        mapMessageToPayloadConverter.setConfiguration(configuration);
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

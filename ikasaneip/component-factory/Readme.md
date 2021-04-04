[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Component Factory Guide

## Introduction

Using the ikasan component factory allows developers to create components in a consistent and simplified manner. 
This removes the need to instantiate components "by hand" in per flow (or per module) component factories. 
Code duplication and testing overhead is significantly reduced. 

__Note all example code below is available in the [sample component-factory project](../sample/spring-boot/component-factory/README.md)__

## Usage

Include the ikasan-component-factory-spring library in your pom.xml

```xml
    <dependency>
        <groupId>org.ikasan</groupId>
        <artifactId>ikasan-component-factory-spring</artifactId>
        <version>3.2.0-SNAPSHOT</version>
    </parent>
```

In your Flow Class use the [@IkasanComponent](spring/src/main/java/org/ikasan/component/factory/spring/annotation/IkasanComponent.java)
to create your components :-

```java
    @IkasanComponent(prefix="jms.esb.broker.shared", factoryPrefix = "increase.book.prices.jms.consumer")
    private JmsContainerConsumer increaseBookPricesJmsConsumer;

    @IkasanComponent(prefix="increase.book.prices.xslt.converter")
    private XsltConverter<String,String>increaseBookPricesXsltConverter;

    @IkasanComponent(prefix="increase.book.prices.xml.validator")
    private XMLValidator<String, String> increaseBookPricesXmlValidator;

    @IkasanComponent(prefix="increase.book.prices.string.to.payload.converter")
    private StringToPayloadConverter stringToPayloadConverter;

    @IkasanComponent(prefix="increase.book.prices.sftp.producer")
    private SftpProducer increaseBookPricesSftpProducer;
```

The annotation attributes allow your component to be configured from your spring ```application.properties```. 
Your flow can then be defined in the normal way :-

```java
@Bean
public Flow increaseBookPricesJmsToSftpFlow(){
ModuleBuilder mb = builderFactory.getModuleBuilder(moduleName);
return mb.getFlowBuilder("Increase Book Prices Jms to Sftp Flow")
.withDescription("Increases received book prices by 10% then uploads to a sftp directory")
.withExceptionResolver(exceptionResolver)
.consumer("Increase Book Prices Jms Consumer", increaseBookPricesJmsConsumer)
.converter("Increase Book Prices Xslt Converter", increaseBookPricesXsltConverter)
.converter("Increase Book Prices Xml Validator", increaseBookPricesXmlValidator)
.converter("Increase Book Prices Xml to Sftp Payload Converter", stringToPayloadConverter)
.producer("Increase Book Prices Sftp Producer", increaseBookPricesSftpProducer)
.build();
}
```

## The @IkasanComponent Annotation

This is configured via 3 attributes none of which are mandatory

* ```suffix``` this is used to create the ikasan configured resource id name when the components configuration is saved. 
It is concatenated to the ```"${module.name}-"```. By default this will be the name of the annotated field. You should 
  rarely need to specify this.
* ```prefix``` - this is the prefix of the component configuration properties in the spring properties file. You should
  only specify this if your component has configuration.
* ```factoryPrefix``` - this is the prefix of the factory configuration properties in the spring properties file. You should
only specify this if your component factory has configuration.

Hopefully the use of prefix to wire in the components properties is obvious and follows the same pattern as the 
spring ```@ComponentProperties``` annotation.  The ```factoryPrefix``` needs further explanation. As well as the component,
the component factory itself can have a configuration that is used for each component creation. One useful
side effect of this is you can separate shared component properties from those that are specific to each instantiation. 
The ```JmsContainerConsumer``` and ```JmsTemplateProducer``` factories are good examples of this :-

```java
@IkasanComponent(prefix="jms.esb.broker.shared", factoryPrefix = "increased.book.prices.internal.jms.consumer")
private JmsContainerConsumer increasedBookPricesInternalJmsConsumer;

@IkasanComponent(prefix="jms.esb.broker.shared", factoryPrefix = "increased.book.prices.external.jms.producer")
private JmsTemplateProducer<String> increasedBookPricesExternalJmsProducer;

```

Then using the properties below allows the sharing of the common infrastructure properties across multiple components 
without having to duplicate these or use placeholders 

```properties
#=======================================================================================================================
# Shared Properties
#=======================================================================================================================

jms.esb.broker.shared.provider.url=vm://embedded-broker?create=true&broker.persistent=false&broker.useJmx=false&broker.useShutdownHook=false&broker.deleteAllMessagesOnStartup=true
jms.esb.broker.shared.connectionFactoryUsername
jms.esb.broker.shared.connectionFactoryPassword=
jms.esb.broker.shared.connectionFactoryName=XAConnectionFactory
jms.esb.broker.shared.java.naming.factory.initial=org.apache.activemq.jndi.ActiveMQInitialContextFactory

#=======================================================================================================================
# IncreasedBookPricesInternalJmsToExternalJmsFlow
#=======================================================================================================================

#
# increasedBookPricesInternalJmsConsumer
#
increased.book.prices.internal.jms.consumer.destination=dynamicQueues/book.prices.internal.outbound
increased.book.prices.internal.jms.consumer.autoContentConversion=true

#
# increasedBookPricesExternalJmsProducer
#
increased.book.prices.external.jms.producer.destination=dynamicQueues/book.prices.external.outbound
```
## Custom Components

To develop a custom component in addition to defining your component and configuration (if required) classes you will need
to create a component factory class that implements the [ComponentFactory](../spec/component-factory/src/main/java/org/ikasan/spec/component/factory/ComponentFactory.java)
 interface. A simple custom component factory is shown below :-

```java
@Component
public class PayloadToStringConverterComponentFactory implements ComponentFactory<PayloadToStringConverter> {

    @Override
    public PayloadToStringConverter create(String nameSuffix, String configPrefix, String factoryConfigPrefix) {
        return new PayloadToStringConverter();
    }
}
```
In the case of a configured component the BaseComponentFactory provides useful methods to configure the component :-
```java
@Component
public class StringToPayloadConverterComponentFactory extends BaseComponentFactory<StringToPayloadConverter> {

    @Override
    public StringToPayloadConverter create(String nameSuffix, String configPrefix, String factoryConfigPrefix) {
        StringToPayloadConverter stringToPayloadConverter = new StringToPayloadConverter();
        stringToPayloadConverter.setConfiguration(configuration(configPrefix,
            StringToPayloadConverterConfiguration.class));
        stringToPayloadConverter.setConfiguredResourceId(configuredResourceId(nameSuffix));
        return stringToPayloadConverter;
    }
```
You can then create your custom component using the annotation in the normal way :- 
```java
    @IkasanComponent(prefix="increase.book.prices.string.to.payload.converter")
    private StringToPayloadConverter stringToPayloadConverter;
```

## Logging

All creation of components is  displayed in the logs with ```INFO``` level messages

```text
[INFO] org.ikasan.component.factory.spring.IkasanComponentFactory - Created component of type [JmsContainerConsumer] with property prefix [jms.esb.broker.shared], factoryConfigPrefix[increase.book.prices.jms.consumer].
[INFO] org.ikasan.component.factory.spring.IkasanComponentAnnotationProcessor - Creating @IkasanComponent with suffix [increaseBookPricesXsltConverter], prefix [increase.book.prices.xslt.converter], factoryPrefix [increase.book.prices.xslt.converter] of type [XsltConverter]. 
[INFO] org.ikasan.component.factory.spring.IkasanComponentFactory - Created component of type [XsltConverter] with property prefix [increase.book.prices.xslt.converter], factoryConfigPrefix[increase.book.prices.xslt.converter].
```


## Implementation Details

There are 3 central classes used :-

### [The ComponentFactory Interface](../spec/component-factory/src/main/java/org/ikasan/spec/component/factory/ComponentFactory.java)

This must be implemented by all component factories :-

```java
/**
 * Used to instantiate components in a consistent manner
 *
 * T - The type of the component being created
 */
public interface ComponentFactory<T>
{
    /**
     * Creates a component
     *
     * @param nameSuffix The name of the component to be added to the module name
     * @param configPrefix If the component has configuration this is the prefix used to lookup the associated
     *                    configuration
     * @param factoryConfigPrefix If the component has factory configuration this is the prefix used to look up
     *                            the associated configuration
     * @return the created component of type T
     */
    T create (String nameSuffix, String configPrefix, String factoryConfigPrefix);
```
Given the suffix, configPrefix and factoryConfigPrefix any implementation is able to create the component of interest. 


### [The IkasanComponentFactory](spring/src/main/java/org/ikasan/component/factory/spring/IkasanComponentFactory.java)

This is a spring boot component at the heart of using component factories. Given any class it will find the associated
component factory and instantiate the component via its create method :-

```
    public <T> T create(String suffix, String prefix, String factoryConfigPrefix, Class<T> clazz)
    {
        ComponentFactory<T> componentFactory = getComponentFactory(clazz);
        T component = componentFactory.create(suffix, prefix, factoryConfigPrefix);
        logger.info("Created component of type [{}] with property prefix [{}], factoryConfigPrefix[{}].",
            clazz.getSimpleName(), prefix, factoryConfigPrefix);
        return component;
    }
```
If no or multiple component factories are found an ```IkasanComponentFactoryException``` will be thrown.

### [The IkasanComponentAnnotationProcessor](spring/src/main/java/org/ikasan/component/factory/spring/IkasanComponentAnnotationProcessor.java)

This is a spring ```BeanPostProcessor``` that will scan any created beans for ```IkasanComponent``` annotations and then create
the annotated components using the ```IkasanComponentFactory``` bean. 

## Spring Auto-Configuration

This spring project defines a [spring.factories](spring/src/main/resources/META-INF/spring.factories) file which  
automatically creates the ```IkasanComponentFactory```, all the Component Factory beans and the ```IkasanComponentAnnoationProcessor```.

## Dependency Management

This project is home to multiple implementations of ikasan component factories for different components. 
To prevent an explosion of transitive dependencies when this project's library is used by a module all component libraries 
are specified with their ```optional``` tag set to ```true```. It is then up to the module to specify the
component dependencies it requires. Optional dependencies in the [pom.xml](spring/pom.xml) are shown below :- 

```xml
        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-sftp-endpoint</artifactId>
            <scope>compile</scope>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-component-validator</artifactId>
            <scope>compile</scope>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-component-converter</artifactId>
            <scope>compile</scope>
            <optional>true</optional>
        </dependency>
```

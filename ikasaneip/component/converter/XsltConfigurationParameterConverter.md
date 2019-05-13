[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## XML Configuration Parameter Converter
[XsltConfigurationParameterConverter.java](src/main/java/org/ikasan/component/converter/xml/XsltConfigurationParameterConverter.java)

### Purpose

<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. A coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html)
[XML Configuration Parameter Converter](src/main/java/org/ikasan/component/converter/xml/XsltConfigurationParameterConverter.java) is an implementation of the [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java)
. 

The purpose of the XsltConverter is to convert configuration parameters into xslt parameters.
 
By convention fields in the configuration that start with xsltParam will be injected into
the transformer and made available to the xslt.

e.g. a field with name xsltParamMyList, would have the xsltParam stripped and passed in as
a parameter named myList.

Primitive fields will be injected as is. Map and List parameters will be converted into an xml fragment
as follows, before being injected into a parameter named as above:

Map:
````xml
<map>
    <entry key="key 1" value="1"/>
    <entry key="key 2" value="2"/>
</map>
````


List:
````xml
<list>
    <value>1</value>
    <value>2</value>
</list>
````
<br/>

##### Configuration Options
Not applicable as this is not a configurable resource.

##### Sample Usage
```java
````

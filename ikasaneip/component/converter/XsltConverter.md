[<< Component Quick Start](../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## XSLT Converter
[XsltConverter.java](src/main/java/org/ikasan/component/converter/xml/XsltConverter.java)

### Purpose
<img src="../../developer/docs/quickstart-images/message-translator.png" width="200px" align="left">The main responsibility of a converter is to convert from one POJO type to another. Coverter acts as an adapter between components requiring different input types.
Read more about EIP [Translators](http://www.enterpriseintegrationpatterns.com/patterns/messaging/MessageTranslator.html).  

The [XSLT Converter](src/main/java/org/ikasan/component/converter/xml/XsltConverter.java)
is an implementation of the  [Converter Interface](../spec/component/src/main/java/org/ikasan/spec/component/transformation/Converter.java). 
It provides a mechanism that applies an [XSLT](https://docs.oracle.com/javase/tutorial/jaxp/xslt/transformingXML.html) against an XML payload,
subsequently transforming it to another XML format, or other formats that are supported by XSLT.

##### XsltConverter Setter Options
| Setter | Type | Purpose |
| --- | --- | --- | 
| URIResolver | [URIResolver](https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/URIResolver.html) | Use [ClasspathUriResolver](./src/main/java/)   |
| contextPath | stylesheetLocation | The location of the stylesheet. |

##### Configuration Options
| Option | Type | Purpose |
| --- | --- | --- | 
| useTranslets | Boolean |  |
| contextPath | stylesheetLocation | The location of the stylesheet. |

##### Sample Usage
````java

````

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |

[![Build Status](https://travis-ci.org/ikasanEIP/ikasan.svg?branch=master)](https://travis-ci.org/ikasanEIP/ikasan)

![Problem Domain](ikasaneip/developer/docs/quickstart-images/Ikasan-title-transparent.png)

Open Source Enterprise Integration Platform

The Ikasan Enterprise Integration Platform (EIP) addresses the problem 
domain most commonly known as Enterprise Application Integration (EAI). 

Enterprise application integration can be, and already has been, 
approached a number of different ways by a number of projects/vendors, 
both Open Source and closed commercial frameworks. 

It is the intention of the Ikasan Enterprise Integration Platform 
to address this domain as commoditised configurable solutions rather 
than another development framework.


# Problem Domain

* System integration can be exponentially complex
* It is commonly agreed that “spaghetti integration” is bad
* Bleed out of APIs, data syntax and business semantics
* Ripple effect of change is massive
![Problem Domain](ikasaneip/developer/docs/quickstart-images/problem-domain.png) 


# Solution

* Use Standard Enterprise Application Integration (EAI) approach
* Provision of an Event/Service backbone
* Single point of integration for EIS business flow
* Ripple effect of change is localised (assuming best practice)
* Ikasan adopts the standard EAI approach and helps users solve integration problems by building applications constructed of modules/flows/components
![Problem Domain](ikasaneip/developer/docs/quickstart-images/solution.png) 
#  Integration Module

* An Integration Module is a high level logical construct
* Provides a logical grouping of business operations as a single integration point
* Provide either a source, target, or bi-directional business flow
![Integration Modules](ikasaneip/developer/docs/quickstart-images/integration-module.png) 

#  Flows

 * Integration modules comprise of one or more related flows
 * Flows are cohesive operations on a business artifact as a synchronous operation
 * Multiple flows can be chained to isolate concerns
 * Standard event container allows any data type to be transported
 <img src="ikasaneip/developer/docs/quickstart-images/flows.png" width="90%">

#  Components

 * Flows comprise of flow components which have implementation injected as POJOs
 * Components are individual operations acting on events within a flow
 * There are different types of components see the full list below
 * Consumer Component is a POJO with injected tech API for application integration to source events
 * Core services automatically bound to each flow
 * Service APIs support management of the flow, runtime status, resubmission, and replay
 ![Detail view](ikasaneip/developer/docs/quickstart-images/ikasan-anatomy-detail.png)
 
 #  Services
 
 ## Hospital Service
<img src="ikasaneip/developer/docs/quickstart-images/hospital.gif" width="200px" align="left"> 
The Ikasan Hospital Service provides Ikasan users with the ability to view and understand errors that have occurred on the Ikasan service bus. Depending upon the categorisation of the error, the user is
able to remediate the error by resubmitting messages that have been excluded. Error within Ikasan are broadly categorised into to two types of errors. Firstly, there are technical errors. Technical
errors are considered to be transient, and as such when one occurs, Ikasan will log the error to the error reporting component of the Hospital Service and then will rollback and attempt
process the message again. Ikasan can be configured to retry a fixed number of times or indefinitely. If configured to retry for a fixed
number of times, Ikasan, upon exhausting the number of retries, will stop the processing flow, flag it into an error state, and notify the monitoring service of the error that has occurred.<br/>
The second broad categorisation of errors within Ikasan, are those that are considered business errors. Business errors typically occur when an Ikasan module is unable to process a message that it has received, perhaps dues to missing
static data it is trying to retrieve from the mapping service, or due to an XML validation issue. Generally business errors are deemed to be repairable. With this in mind Ikasan excludes messages associated
with business exceptions. These excluded messages can be viewed via the Ikasan Dashboard along with the error that caused the exclusion. Ikasan users are then able to resubmit the excluded messages once the underlying
business exception has been remediated. Alternatively users can choose to ignore excluded message. All details of the user actions are recorded in order to provide an audit trail of actions taken and can be linked back
to problem management systems.
<br/>
<br/>

Follow the link to discover more about the [Ikasan Hospital Service](ikasaneip/hospital/Readme.md).

**An example of a JMSException configured to retry every 10 seconds, indefinitely.**
```xml
<bean class="org.ikasan.exceptionResolver.matcher.MatcherBasedExceptionGroup">
    <constructor-arg>
        <bean class="org.hamcrest.core.IsInstanceOf">
            <constructor-arg value="javax.jms.JMSException"/>
        </bean>
    </constructor-arg>
    <constructor-arg>
        <bean class="org.ikasan.exceptionResolver.action.RetryAction">
            <property name="delay" value="10000"/>
        </bean>
    </constructor-arg>
</bean>
```

**An example of a FixSessionException configured to retry every 60 seconds, for a maximum of 60 times before stopping in error.**
```xml
<bean class="org.ikasan.exceptionResolver.matcher.MatcherBasedExceptionGroup">
    <constructor-arg>
        <bean class="org.ikasan.exceptionResolver.matcher.ThrowableCauseMatcher">
            <constructor-arg>
                <bean class="org.hamcrest.core.IsInstanceOf">
                    <constructor-arg value="com.mizuho.api.fix.session.exception.FixSessionException"/>
                </bean>
            </constructor-arg>
            <constructor-arg value="false"/>
        </bean>
    </constructor-arg>
    <constructor-arg>
        <bean class="org.ikasan.exceptionResolver.action.RetryAction">
            <constructor-arg name="maxRetries" value="60"/>
            <constructor-arg name="delay" value="60000"/>
        </bean>
    </constructor-arg>
</bean>
```

**An example of a TransformationException configured to exclude the underlying message.**
```xml
<bean class="org.ikasan.exceptionResolver.matcher.MatcherBasedExceptionGroup">
     <constructor-arg>
         <bean class="org.hamcrest.core.IsInstanceOf">
             <constructor-arg value="org.ikasan.spec.component.transformation.TransformationException"/>
         </bean>
     </constructor-arg>
     <constructor-arg>
         <bean class="org.ikasan.exceptionResolver.action.ExcludeEventAction"/>
     </constructor-arg>
 </bean>
```
<br/>

## Replay Service
<img src="ikasaneip/developer/docs/quickstart-images/replay.gif" width="200px" align="left"> 
The Replay Service provides a mechanism for recording and replaying data events. Flows can be configured to record events as they are received by the consumer of the flow, prior to any
mutations within the flow. Replay events are serialised and persisted to the underlying datastore as well as the text index if one is configured. Once data events have been recorded, they
can then be replayed back into the flow from which they were recorded, in either the same environment that they were recorded, or into another environment in which the same module/flow is 
deployed. This service provide 2 valuable features. Firstly in the unlikely event that data has not arrived at its intended destination, the data event can be replayed into the same flow
, in the same environment, within which it was recorded, thus providing a fall back approach to the guaranteed delivery of data. The second feature allows for data recoded from one environment
to be played into another environment. This is particularly useful when troubleshooting problems or providing quality assurance against new developments that require real data for the purpose 
of testing.</br>

<br/>
 
 ## Wiretap Service
<img src="ikasaneip/developer/docs/quickstart-images/wiretap.gif" width="200px" align="left"> 
The Wiretap Service allows for data to be collected and inspected, as it flows through the Ikasan service bus. This Wiretap Service is an invaluable tool allowing for end to
end tracking of data events, in real time, as data flows and mutates. Wiretap events are captured with a time stamp with millisecond precision along with the location of where the wiretap was triggered.
They are also captured with a life identifier that remains immutable for the data event for its entire journey throughout the bus, even if the undelying data mutates. The correalting elements of the wiretap
build a full chronolonogical picture of the flow of data which can then be queried via the Ikasan Dashboard.</br> 
Wiretap jobs are configured on a component at runtime and record all data events that are received by the component. The Wiretap events can
be written to the underlying persistent data store or alternatively written to the log file. Wiretap events are also written to a text index in order to facilitate a fast and efficient context based search facility.
This service provides a high level of visibility on all data events, and coupled with the transactional, guaranteed data delivery features of Ikasan, provides support users assurance that data has been delivered
to all of the intended endpoints.</br>
The Wiretap Service works out of the box and requires no coding. See the Dashboard documentation for details on how to set up wiretaps on a component.

<br/>

## Configuration Service
<img src="ikasaneip/developer/docs/quickstart-images/configuration.gif" width="200px" align="left">
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.

<br/>

## Mapping Service
<img src="ikasaneip/developer/docs/quickstart-images/mapping.gif" width="200px" align="left">
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

<br/>

## Monitoring Service
<img src="ikasaneip/developer/docs/quickstart-images/monitoring.gif" width="200px" align="left"> 
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. 
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. 
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.

<br/>

## Management Service
<img src="ikasaneip/developer/docs/quickstart-images/management.gif" width="200px" align="left"> 
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. 
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. 
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.
Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image. Trying to get sime text to flow around the image.

<br/>

# Documentation
## Getting Started
* [Developer Pre-Requisiites](ikasaneip/developer/docs/DeveloperPreRequisites.md) 
* [Standalone Developer Guide](ikasaneip/developer/docs/StandaloneDeveloperGuide.md) 
* [Application Server Developer Guide](ikasaneip/developer/docs/Readme.md)
* [Component Quick Start](ikasaneip/component/Readme.md)
* [Dashboard Guide](ikasaneip/dashboard/Readme.md)


| Samples overview |
|-------------|
|  [spring-boot-builder-pattern](ikasaneip/sample/spring-boot/builder-pattern/README.md) |
|  [spring-boot-file](ikasaneip/sample/spring-boot/file/README.md) |
|  [spring-boot-jms](ikasaneip/sample/spring-boot/jms/README.md) |
|  [spring-boot-ftp-jms](ikasaneip/sample/spring-boot/ftp-jms/README.md) |
|  [spring-boot-sftp-jms](ikasaneip/sample/spring-boot/sftp-jms/README.md) |
---------------------


Contributor Best Practices
--------------------------
1. Ensure logging output from performing ```mvn clean install``` is kept to an absolute minimum, 
   as we have a 4MB limit on log output on our ci builds. 
2. Ensure the max log level is INFO for all code
3. Likewise ```hibernate.show_sql``` should always be ```false``` 
2. If adding a new component do add a README.md page to explain its configuration and use
3. For each new component ensure this is demonstrated in a sample module / flow. 
   
Using Eclipse
-------------
1. Install the latest version of eclipse
2. Launch eclipse and install the m2e plugin, make sure it uses your repo configs 
   (get it from: http://www.eclipse.org/m2e/download/ or install "Maven Integration for Eclipse" from the Eclipse Marketplace)
3. In eclipse preferences Java->Code Style, import the cleanup, templates, and
   formatter configs in [ikasaneip/ikasan-developer/eclipse](https://github.com/ikasanEIP/ikasan/tree/master/ikasaneip/developer/eclipse) in the ikasanEIP repository.
4. In eclipse preferences Java->Code Style->Code Templates enable the "Automatically add comments"
   checkbox to ensure the standard copyright notice gets added at the top of classes. 
5. Also in code template under Comments -> Types ensure you add your name to the @author tag   
6. In eclipse preferences Java->Editor->Save Actions enable "Additional Actions"
7. Use import on the root pom.xml which will pull in all modules
8. Wait (m2e takes awhile on initial import)



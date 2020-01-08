![Problem Domain](./quickstart-images/Ikasan-title-transparent.png)
# Ikasan Standalone Developer Guide

# Introduction

## Overview

The Ikasan Enterprise Integration Platform (IkasanEIP) provides a robust and scalable ESB platform based on open technologies and standards. 
IkasanEIP can be adopted as little or as much as required to meet your integration needs.

## About

This guide demonstrates the IkasanESB features through a hands-on Ikasan Application build from scratch to a fully working solution.

_NOTE: This is an example to demonstrate the development and core features of Ikasan - it is not intended to produce a production ready solution._

This is part of the documentation suite for the Ikasan Enterprise Integration Platform.

## Audience

This guide is targeted at developers wishing to get started and undertake their first development projects with the Ikasan Enterprise Integration Platform.

A familiarity with Java and Maven is assumed.

## How to Use This Guide

This guide provides a quick and concise series of steps for getting started with the IkasanEIP platform. These steps should be followed sequentially. Points to note and other hints/tips are provided as additional information or best practices, however, full details on these aspects are beyond the scope of this document.

On completion of this document the reader should have installed and configured all required software for development and have created and run an Ikasan Application.

# Pre-Requisites

[Developer Pre-Requisiites](./DeveloperPreRequisites.md)
 
# Problem Definition
To begin we can define a simple problem to give some context to the demo. 
Typically the problem domain addressed by IkasanESB requires the sourcing of business events from one application followed by 
various conversions, orchestrations, and delivery to other downstream applications.

# Design
We will create an Ikasan Integration Module which consists of a single flow containing two component operations.

    IntegrationModule
    - Flow
        - Consumer -> Producer
        
For simplicity we will utilise some Ikasan off-the-shelf consumers to mock application event generation as a source; and off-the-shelf producers to mock event publication as a target.

# Implementation
We will be using IntelliJ for the rest of this demonstration.

## Create a Project
In IntelliJ select `File/New/Project...`

Select maven (blank archetype). Ensure you select JDK 1.8 as the SDK for this new project.
![Login](quickstart-images/IntelliJ-new-project-screen1.png) 

Specify your project Maven coordinates such as GroupId, ArtefactId, and Version.

For instance, 
- GroupId --> `com.ikasan.example`
- ArtefactId --> `MyIntegrationModule`
- Version --> `1.0.0-SNAPSHOT`

![Login](quickstart-images/IntelliJ-new-project-screen2.png) 

Select the directory/workspace to create this project.
![Login](quickstart-images/IntelliJ-new-project-screen3.png) 

Thats it! 

Your project will be created and look something like this.
![Login](quickstart-images/IntelliJ-new-project-screen4.png) 

We now have a blank Java project based on a Maven project structure.
Next we need to update the Maven pom.xml to set the application packaging to create a Jar.
```xml
<?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
          <modelVersion>4.0.0</modelVersion>
      
          <groupId>com.ikasan.example</groupId>
          <artifactId>MyIntegrationModule</artifactId>
          <version>1.0.0-SNAPSHOT</version>
          <packaging>jar</packaging>        <!-- Add packaging type to create a jar -->
      
      </project>
```

Now add the first Ikasan Application dependency to the Maven pom.xml.
 
Edit the pom.xml and add the following properties and dependencies section.
This is best practice layout. It keeps all your application versions in one place and defines the dependencies.
In this case we are adding the ikasan-eip-standalone main library; an h2 standalone persistence library; and two Ikasan test libraries.
```xml
<?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
   
       <groupId>com.ikasan.example</groupId>
       <artifactId>MyIntegrationModule</artifactId>
       <version>1.0.0-SNAPSHOT</version>
       <packaging>jar</packaging>
   
    <!-- Add project properties -->
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <version.ikasan>2.2.0</version.ikasan>
        <version.org.springboot>2.0.9.RELEASE</version.org.springboot>
    </properties>

    <!-- Add project dependencies -->
    <dependencies>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-eip-standalone</artifactId>
            <version>${version.ikasan}</version>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-h2-standalone-persistence</artifactId>
            <version>${version.ikasan}</version>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-test-endpoint</artifactId>
            <version>${version.ikasan}</version>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-test</artifactId>
            <scope>test</scope>
            <version>${version.ikasan}</version>
        </dependency>

    </dependencies>
    
</project>
```

Next we need to add some standard Maven project build features; your SCM (optional); and 
an Ikasan bill of materials (bom) as project dependency management.

```xml
<?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
   
       <groupId>com.ikasan.example</groupId>
       <artifactId>MyIntegrationModule</artifactId>
       <version>1.0.0-SNAPSHOT</version>
       <packaging>jar</packaging>
   
        ...

    <!-- Add project build plugins -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${version.org.springboot}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>2.8.1</version>
                <executions>
                    <execution>
                        <id>attach-javadocs</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>2.1.2</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-enforcer-plugin</artifactId>
                <version>1.4.1</version>
                <executions>
                    <execution>
                        <id>enforce-versions</id>
                        <goals>
                            <goal>enforce</goal>
                        </goals>
                        <configuration>
                            <rules><dependencyConvergence /></rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <!-- Add SCM URLS -->
    <scm>
        <developerConnection><!-- scm:svn: developer connection URL --></developerConnection>
        <url><!-- SCM connection URL --></url>
        <connection><!-- scm:svn: connection URL --></connection>
    </scm>

    <!-- Add project dependency management -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.ikasan</groupId>
                <artifactId>ikasan-eip-standalone-bom</artifactId>
                <version>${version.ikasan}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>
```
Now create a class with a fully qualified name of ```com.ikasan.example.Application``` - this is the entry point for Springboot.

Copy and paste the entirety of the code below replacing the content of that class.
```java
package com.ikasan.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}
```
Create a class for your Ikasan Module with a fully qualified name of ```com.ikasan.example.MyModule```.

Copy and paste the entirety of the code below replacing the content of that class.
```java
package com.ikasan.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml"
} )
public class MyModule
{
}   
```

Provide some configuration properties for the module by creating a resources/application.properties
```properties
# Logging levels across packages (optional)
logging.level.com.arjuna=INFO
logging.level.org.springframework=INFO

# Blue console servlet settings (optional)
server.error.whitelabel.enabled=false

# Web Bindings
server.port=8080
server.address=localhost
server.servlet.context-path=/example-im
server.tomcat.additional-tld-skip-patterns=xercesImpl.jar,xml-apis.jar,serializer.jar

# Spring config
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
spring.liquibase.change-log=classpath:db-changelog.xml
spring.liquibase.enabled=true

# health probs and remote management (optional)
management.endpoints.web.expose=*
management.server.servlet.context-path=/manage
management.endpoint.shutdown.enabled=true

# Ikasan persistence store
datasource.username=sa
datasource.password=sa
datasource.driver-class-name=org.h2.Driver
datasource.xadriver-class-name=org.h2.jdbcx.JdbcDataSource
datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
datasource.dialect=org.hibernate.dialect.H2Dialect
datasource.show-sql=false
datasource.hbm2ddl.auto=none
datasource.validationQuery=select 1
```

Build and run the application.
From IntelliJ just right click on Application and select run ```Application.main()```

Alternatively to run from the command line ensure you are in the project root directory i.e. MyIntegrationModule then run a Maven clean install
```
mvn clean install
```

If successful run the application
```
java -jar target/MyIntegrationModule-1.0.0-SNAPSHOT.jar
```

Try accessing the application through a browser at ```http://localhost:8090/example-im``` (user:admin password:admin)
![Login](quickstart-images/new-project-embeddedConsole-screen5.png) 

Logging in will give you the default home page.
![Login](quickstart-images/new-project-embeddedConsole-screen6.png) 

Selecting 'Modules' will show the Integration Module for this example, but as we haven't defined one you just see a fairly empty screen.
![Login](quickstart-images/new-project-embeddedConsole-screen7.png) 
 
Lets create a module.

Go back to IntelliJ open the MyModule class. So far this class just loads the persistence configuration via the ImportResource, but we now want to use this class to define our Ikasan Integration Module. Replace the entirety of this class with the code below.  
```java
package com.ikasan.example;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml"
} )
public class MyModule
{
    @Resource BuilderFactory builderFactory;

    @Bean
    public Module myModule()
    {
        // Create a module builder from the builder factory
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("My Integration Module")
                .withDescription("My first integration module.");

        // Create a component builder from the builder factory
        ComponentBuilder componentBuilder = builderFactory.getComponentBuilder();

        // create a flow from the module builder and add required orchestration components
        Flow eventGeneratingFlow = moduleBuilder.getFlowBuilder("Event Generating Flow")
                .consumer("My Source Consumer", componentBuilder.eventGeneratingConsumer())
                .producer("My Target Producer", componentBuilder.logProducer())
                .build();

        // Add the created flow to the module builder and create the module
        Module module = moduleBuilder
                .addFlow(eventGeneratingFlow)
                .build();
        return module;
    }
}
```

Build and run the application again.

From IntelliJ just right click on Application and select run ```Application.main()```

Alternatively to run from the command line ensure you are in the project root directory i.e. MyIntegrationModule then run a Maven clean install
```
mvn clean install
```

Refresh your browser at ```http://localhost:8090/example-im```, login and select 'Modules' - you now see your new module.
![Login](quickstart-images/new-project-embeddedConsole-screen9.png)

Selecting the module will show you the flows within this module 
which can be started, stopped, paused/resumed. The component 'Type' may have a different $Proxy value, dont worry about this for now.
![Login](quickstart-images/new-project-embeddedConsole-screen10.png)

Start the flow - you will see the state change from 'stopped' to 'running'. 
![Login](quickstart-images/new-project-embeddedConsole-screen11.png)

View the application logs (either in IntelliJ or the command line, depending on how the application was started).
```less
2019-05-24 20:31:39.300  INFO 98339 --- [0.1-8080-exec-2] o.i.module.service.ModuleServiceImpl     : startFlow : My Integration Module.Event Generating Flow requested by [admin]
2019-05-24 20:31:39.304  INFO 98339 --- [0.1-8080-exec-2] o.h.h.i.QueryTranslatorFactoryInitiator  : HHH000397: Using ASTQueryTranslatorFactory
2019-05-24 20:31:39.306  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration Module_Event Generating Flow_My Source Consumer_1165847135_I]. Default programmatic dao will be used.
2019-05-24 20:31:39.306  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration Module_Event Generating Flow_My Target Producer_1165847135_I]. Default programmatic dao will be used.
2019-05-24 20:31:39.307  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration Module_Event Generating Flow_My Source Consumer_1165847135_I]. Default programmatic dao will be used.
2019-05-24 20:31:39.307  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration Module_Event Generating Flow_My Target Producer_1165847135_I]. Default programmatic dao will be used.
2019-05-24 20:31:39.308  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration Module_Event Generating Flow_My Target Producer_729287509_C]. Default programmatic dao will be used.
2019-05-24 20:31:39.308  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration ModuleEvent Generating FlowMy Source Consumer_element]. Default programmatic dao will be used.
2019-05-24 20:31:39.309  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration ModuleEvent Generating FlowMy Target Producer_element]. Default programmatic dao will be used.
2019-05-24 20:31:39.309  WARN 98339 --- [0.1-8080-exec-2] s.ConfiguredResourceConfigurationService : No persisted dao for configuredResource [My Integration Module-Event Generating Flow]. Default programmatic dao will be used.
2019-05-24 20:31:39.310  INFO 98339 --- [0.1-8080-exec-2] o.i.f.v.VisitingInvokerFlow              : Started Flow[Event Generating Flow] in Module[My Integration Module]
2019-05-24 20:31:39.312  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131690, relatedIdentifier=null, timestamp=1558726299311, payload=Test Message 1]
2019-05-24 20:31:40.317  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131691, relatedIdentifier=null, timestamp=1558726300317, payload=Test Message 2]
2019-05-24 20:31:41.318  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131692, relatedIdentifier=null, timestamp=1558726301318, payload=Test Message 3]
2019-05-24 20:31:42.323  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131693, relatedIdentifier=null, timestamp=1558726302323, payload=Test Message 4]
2019-05-24 20:31:43.328  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131694, relatedIdentifier=null, timestamp=1558726303328, payload=Test Message 5]
2019-05-24 20:31:44.334  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131695, relatedIdentifier=null, timestamp=1558726304334, payload=Test Message 6]
2019-05-24 20:31:45.338  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131696, relatedIdentifier=null, timestamp=1558726305338, payload=Test Message 7]
2019-05-24 20:31:46.343  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131697, relatedIdentifier=null, timestamp=1558726306343, payload=Test Message 8]
2019-05-24 20:31:47.344  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131698, relatedIdentifier=null, timestamp=1558726307344, payload=Test Message 9]
2019-05-24 20:31:48.345  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131699, relatedIdentifier=null, timestamp=1558726308345, payload=Test Message 10]
2019-05-24 20:31:49.347  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131700, relatedIdentifier=null, timestamp=1558726309347, payload=Test Message 11]
2019-05-24 20:31:50.352  INFO 98339 --- [pool-2-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=1200131701, relatedIdentifier=null, timestamp=1558726310352, payload=Test Message 12]
```
Congratulations - your first working Ikasan Integration Module, but what is it doing...

Lets go back to the code, specifically the ```MyModule``` class to understand what we just implemented and ran.

```java
@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml"
} )
public class MyModule
{
    @Resource BuilderFactory builderFactory;
    ...
}
```
Firstly, we can see we have added an extra line to the ImportResource 
for ```classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml```.
 This tells Ikasan that we want our flow to be transactional and 
 to guarantee that all operations successfully complete or rollback 
 to a consistent state in the event of a failure. 
 This configuration changes based on the Consumer protocol 
 being implemented. For this example we are using an 
 EventGeneratingConsumer to which this config will be applied. 
 Transactions are complex and will be covered in detail later.

All Ikasan artefacts can be created from a single instance of a ```builderFactory``` from Spring context. The builderFactory is the base factory class from which all Ikasan constructs such as modules, flows, and components are created.

Next we create a ```moduleBuilder``` from the ```builderFactory```. When we create the ```moduleBuilder``` we provide the name we are going to assign to the module.
We can also set other properties on the module through this ```moduleBuilder``` such as description.

```java
@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml"
} )
public class MyModule
{
    @Resource BuilderFactory builderFactory;

    @Bean
    public Module myModule()
    {
        // Create a module builder from the builder factory
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("My Integration Module")
                .withDescription("My first integration module.");
       ...
       }
} 
```
Next, get a ```componentBuilder``` instance from the ```builderFactory``` - we will be using this in the ```flowBuilder```.
```java
@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml"
} )
public class MyModule
{
    @Resource BuilderFactory builderFactory;

    @Bean
    public Module myModule()
    {
        // Create a module builder from the builder factory
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("My Integration Module")
                .withDescription("My first integration module.");

        // Create a component builder from the builder factory
        ComponentBuilder componentBuilder = builderFactory.getComponentBuilder();
        ...
    }
}
```

Now on to the interesting parts.

We use the ```moduleBuilder``` to get a ```flowBuilder``` and specify the name of the flow.
The components within the flow are then added as ```consumer``` and ```producer```, both from the ```componentBuilder```.
Each component is given a name and a functional class that does the work. The component classes below are off-the-shelf 
Ikasan components, however, your own components can be easily written and added as shown later.
```java
@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml"
} )
public class MyModule
{
    @Resource BuilderFactory builderFactory;

    @Bean
    public Module myModule()
    {
        // Create a module builder from the builder factory
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("My Integration Module")
                .withDescription("My first integration module.");

        // Create a component builder from the builder factory
        ComponentBuilder componentBuilder = builderFactory.getComponentBuilder();

        // create a flow from the module builder and add required orchestration components
        Flow eventGeneratingFlow = moduleBuilder.getFlowBuilder("Event Generating Flow")
                .consumer("My Source Consumer", componentBuilder.eventGeneratingConsumer())
                .producer("My Target Producer", componentBuilder.logProducer())
                .build();

        // Add the created flow to the module builder and create the module
        Module module = moduleBuilder
                .addFlow(eventGeneratingFlow)
                .build();
        return module;
    }
}
```

So ```componentBuilder.eventGeneratingConsumer()``` returns an off-the-shelf event generating consumer component which will provide the functionality of that consumer named "My Source Consumer";
```componentBuilder.logProducer()``` returns an off-the-shelf logProducer producer component which will provide the functionality of the producer named "My Target Producer".

The consumer of this flow will continuously generate events as fast as it can - this is the default behaviour if this component isn't further configured by overriding the MessageProvider. 
The event from the consumer to the producer is a simple string message with an incrementing sequential number. The event is subsequently logged by the producer.

Each off-the-shelf component can optionally have a ```build()``` method called against it which tells the builder pattern to create this instance, as does the flow. Typically, this is optional against components when created inline like this as the Ikasan builder will call this if not called explicitly.
 
```java
@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml"
} )
public class MyModule
{
    @Resource BuilderFactory builderFactory;

    @Bean
    public Module myModule()
    {
        // Create a module builder from the builder factory
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("My Integration Module")
                .withDescription("My first integration module.");

        // Create a component builder from the builder factory
        ComponentBuilder componentBuilder = builderFactory.getComponentBuilder();

        // create a flow from the module builder and add required orchestration components
        Flow eventGeneratingFlow = moduleBuilder.getFlowBuilder("Event Generating Flow")
                .consumer("My Source Consumer", componentBuilder.eventGeneratingConsumer())
                .producer("My Target Producer", componentBuilder.logProducer())
                .build();

        // Add the created flow to the module builder and create the module
        Module module = moduleBuilder
                .addFlow(eventGeneratingFlow)
                .build();
        return module;
    }
}
```
Finally we have the flow we can add it to the moduleBuilder and ```build()``` the module.
 
That is how every Ikasan Application Integration Module is created regardless of how simple or complex the operations.

## Adding your own Components
Lets add a custom Converter between the Consumer and Producer components.

To start with we will create a new Converter component implementation.

Create a new class called ```com.ikasan.example.converter.MyConverter```.
To implement the class as an Ikasan Converter component we simply need to use the Converter contract through ```implement Converter```
The contract for the Converter allows for the provision of generic types defining the incoming type and the return type on the method.
In the example below the Converter is expecting an incoming String which it will convert and return as an Integer.

```java
package com.ikasan.example.converter;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;

public class MyConverter implements Converter<String,Integer>
{
    public Integer convert(String payload) throws TransformationException
    {
        String[] strings = payload.split(" ");
        int intPart = Integer.valueOf( strings[2] );
        return Integer.valueOf(intPart);
    }
}
```

Now we need to add this new Converter operation to our flow.

Simply update the flowBuilder lines to insert this new component called "My Converter".
```java
        // create a flow from the module builder and add required orchestration components
        Flow eventGeneratingFlow = moduleBuilder.getFlowBuilder("Event Generating Flow")
                .consumer("My Source Consumer", componentBuilder.eventGeneratingConsumer())
                .converter("My Converter", new MyConverter())
                .producer("My Target Producer", componentBuilder.logProducer())
                .build();
```
Remember, you are adding a new functional class that will need to be imported at the top of your MyModule class as 
```
import com.ikasan.example.converter.MyConverter;
```
Build it, run it, and start the flow from the Browser and see what gets output to the logs by the LogProducer.

You will notice that the `payload=` has changed from the 'Test Message 1' etc, to simply just the integer part of the message from the consumer.

## Exception Handling
Every Ikasan flow has an automatically created and assigned 'Recovery Manager'. The role of the Recovery Manager is to manage the flow if things fail at runtime i.e. an Exception occurs in the flow or any of its components.

Lets see what happens if we get our Converter to throw an exception on a specific message i.e message 5.
```java
    public Integer convert(String payload) throws TransformationException
    {
        String[] strings = payload.split(" ");
        int intPart = Integer.valueOf( strings[2] );
        if(intPart == 5)
        {
            throw new TransformationException("error - bad number received [" + 5 + "]");
        }
        return Integer.valueOf(intPart);
    }
```
Build it, run it, and start the flow from the Browser.

If you look at the logs you will see that when it gets to message 5 an exception occurs 
and the Recovery Manager kicked in to decide what to do. 
```java
RecoveryManager resolving to [Stop] for componentName[My Converter] exception [error - bad number received [5]]

org.ikasan.spec.component.transformation.TransformationException: error - bad number received [5]
	at com.ikasan.example.converter.MyConverter.convert(MyConverter.java:14) ~[classes/:na]
	at com.ikasan.example.converter.MyConverter.convert(MyConverter.java:6) ~[classes/:na]
	at org.ikasan.flow.visitorPattern.invoker.ConverterFlowElementInvoker.invoke(ConverterFlowElementInvoker.java:121) ~[ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.flow.visitorPattern.VisitingInvokerFlow.invoke(VisitingInvokerFlow.java:860) [ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.flow.visitorPattern.VisitingInvokerFlow.invoke(VisitingInvokerFlow.java:778) [ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.flow.visitorPattern.VisitingInvokerFlow.invoke(VisitingInvokerFlow.java:76) [ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.component.endpoint.util.consumer.EventGeneratingConsumer.onMessage(EventGeneratingConsumer.java:190) [ikasan-utility-endpoint-2.1.0.jar:na]
	at org.ikasan.component.endpoint.util.consumer.EventGeneratingConsumer.onMessage(EventGeneratingConsumer.java:61) [ikasan-utility-endpoint-2.1.0.jar:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_111]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_111]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_111]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_111]
	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:333) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:190) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:99) [spring-tx-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:282) [spring-tx-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:96) [spring-tx-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:213) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at com.sun.proxy.$Proxy87.onMessage(Unknown Source) [na:na]
	at org.ikasan.component.endpoint.util.consumer.SimpleMessageGenerator.execute(SimpleMessageGenerator.java:95) [ikasan-utility-endpoint-2.1.0.jar:na]
	at org.ikasan.component.endpoint.util.consumer.SimpleMessageGenerator.run(SimpleMessageGenerator.java:83) [ikasan-utility-endpoint-2.1.0.jar:na]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) [na:1.8.0_111]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) [na:1.8.0_111]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [na:1.8.0_111]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [na:1.8.0_111]
	at java.lang.Thread.run(Thread.java:745) [na:1.8.0_111]
```
In this case because we havent told the Recovery Manager to take specific action on this type of exception
it resolves to stopping the flow in error. This stoppedInError state can also be seen on the Console when refreshed.
![Login](quickstart-images/new-project-embeddedConsole-screen13.png)

Lets tell the Recovery Manager to take a different action on this exception.
As this is a setting on the flow we need to update the code on the flowBuilder to add
a different exceptionResolver. In this case when we see a TransformationException we are now going to exclude the event
that caused the exception.
```java
        // create a flow from the module builder and add required orchestration components
        Flow eventGeneratingFlow = moduleBuilder.getFlowBuilder("Event Generating Flow")
                .withExceptionResolver(builderFactory
                        .getExceptionResolverBuilder()
                        .addExceptionToAction(TransformationException.class, OnException.excludeEvent()).build())
                .consumer("My Source Consumer", componentBuilder.eventGeneratingConsumer())
                .converter("My Converter", new MyConverter())
                .producer("My Target Producer", componentBuilder.logProducer())
                .build();
```
Build it, run it, and start the flow from the Browser.

Now we see the same error occuring, but the Recovery Manager excludes the event and allows the flow to keep running.
If you look in the logs you will see something like this,
```
2018-04-14 11:57:51.681  INFO 4811 --- [0.1-8090-exec-2] o.i.f.v.VisitingInvokerFlow              : Started Flow[EventGeneratingFlow] in Module[My Integration Module]
2018-04-14 11:57:51.687  INFO 4811 --- [pool-4-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=Message 1, relatedIdentifier=null, timestamp=1523703471683, payload=1]
2018-04-14 11:57:52.692  INFO 4811 --- [pool-4-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=Message 2, relatedIdentifier=null, timestamp=1523703472692, payload=2]
2018-04-14 11:57:53.696  INFO 4811 --- [pool-4-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=Message 3, relatedIdentifier=null, timestamp=1523703473696, payload=3]
2018-04-14 11:57:54.701  INFO 4811 --- [pool-4-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=Message 4, relatedIdentifier=null, timestamp=1523703474701, payload=4]
2018-04-14 11:57:55.711  INFO 4811 --- [pool-4-thread-1] o.i.recovery.ScheduledRecoveryManager    : RecoveryManager resolving to [ExcludeEvent] for componentName[My Converter] exception [error - bad number received [5]]

org.ikasan.spec.component.transformation.TransformationException: error - bad number received [5]
	at com.ikasan.example.converter.MyConverter.convert(MyConverter.java:14) ~[classes/:na]
	at com.ikasan.example.converter.MyConverter.convert(MyConverter.java:6) ~[classes/:na]
	at org.ikasan.flow.visitorPattern.invoker.ConverterFlowElementInvoker.invoke(ConverterFlowElementInvoker.java:121) ~[ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.flow.visitorPattern.VisitingInvokerFlow.invoke(VisitingInvokerFlow.java:860) [ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.flow.visitorPattern.VisitingInvokerFlow.invoke(VisitingInvokerFlow.java:778) [ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.flow.visitorPattern.VisitingInvokerFlow.invoke(VisitingInvokerFlow.java:76) [ikasan-flow-visitorPattern-2.1.0.jar:na]
	at org.ikasan.component.endpoint.util.consumer.EventGeneratingConsumer.onMessage(EventGeneratingConsumer.java:190) [ikasan-utility-endpoint-2.1.0.jar:na]
	at org.ikasan.component.endpoint.util.consumer.EventGeneratingConsumer.onMessage(EventGeneratingConsumer.java:61) [ikasan-utility-endpoint-2.1.0.jar:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_111]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_111]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_111]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_111]
	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:333) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:190) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:99) [spring-tx-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:282) [spring-tx-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:96) [spring-tx-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:213) [spring-aop-4.3.10.RELEASE.jar:4.3.10.RELEASE]
	at com.sun.proxy.$Proxy87.onMessage(Unknown Source) [na:na]
	at org.ikasan.component.endpoint.util.consumer.SimpleMessageGenerator.execute(SimpleMessageGenerator.java:95) [ikasan-utility-endpoint-2.1.0.jar:na]
	at org.ikasan.component.endpoint.util.consumer.SimpleMessageGenerator.run(SimpleMessageGenerator.java:83) [ikasan-utility-endpoint-2.1.0.jar:na]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) [na:1.8.0_111]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) [na:1.8.0_111]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [na:1.8.0_111]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [na:1.8.0_111]
	at java.lang.Thread.run(Thread.java:745) [na:1.8.0_111]

Hibernate: select erroroccur_.Uri, erroroccur_.ModuleName as ModuleNa2_2_, erroroccur_.FlowName as FlowName3_2_, erroroccur_.FlowElementName as FlowElem4_2_, erroroccur_.ErrorDetail as ErrorDet5_2_, erroroccur_.ErrorMessage as ErrorMes6_2_, erroroccur_.ExceptionClass as Exceptio7_2_, erroroccur_.EventLifeIdentifier as EventLif8_2_, erroroccur_.EventRelatedIdentifier as EventRel9_2_, erroroccur_.Action as Action10_2_, erroroccur_.Event as Event11_2_, erroroccur_.EventAsString as EventAs12_2_, erroroccur_.Timestamp as Timesta13_2_, erroroccur_.Expiry as Expiry14_2_, erroroccur_.UserAction as UserAct15_2_, erroroccur_.ActionedBy as Actione16_2_, erroroccur_.UserActionTimestamp as UserAct17_2_, erroroccur_.Harvested as Harvest18_2_ from ErrorOccurrence erroroccur_ where erroroccur_.Uri=?
Hibernate: insert into ErrorOccurrence (ModuleName, FlowName, FlowElementName, ErrorDetail, ErrorMessage, ExceptionClass, EventLifeIdentifier, EventRelatedIdentifier, Action, Event, EventAsString, Timestamp, Expiry, UserAction, ActionedBy, UserActionTimestamp, Harvested, Uri) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate: insert into ExclusionEvent (Id, ModuleName, FlowName, Identifier, Event, Timestamp, ErrorUri, Harvested) values (null, ?, ?, ?, ?, ?, ?, ?)
2018-04-14 11:57:56.769  INFO 4811 --- [pool-4-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=Message 6, relatedIdentifier=null, timestamp=1523703476769, payload=6]
2018-04-14 11:57:57.774  INFO 4811 --- [pool-4-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=Message 7, relatedIdentifier=null, timestamp=1523703477773, payload=7]
2018-04-14 11:57:58.778  INFO 4811 --- [pool-4-thread-1] o.i.c.e.util.producer.LogProducer        : GenericFlowEvent [identifier=Message 8, relatedIdentifier=null, timestamp=1523703478778, payload=8]
```

If you are running the Ikasan Dashboard, you will be able to navigate to the Topology view and select the Error tab, to see the error; or the Exclusion tab to see the excluded event.

## Configuring Components
Currently we have hardcoded a value of 5 to indicate the bad message in the Converter. What if we want this to be user defined or even dynamic based on runtime?

### Configured Resources
We can tell a component that it is a ConfiguredResource. Telling it this allows access to configure attributes on the component through the Console.
Firstly, lets define a simple JavaBean to denote the configuration properties we wish to expose.
Create a new class called ```com.ikasan.example.converter.MyConverterConfiguration``` within which we will
define an attribute as a configurable value called 'badNumber'.

```java
package com.ikasan.example.converter;

public class MyConverterConfiguration
{
    int badNumber = 5;

    public int getBadNumber() {
        return badNumber;
    }

    public void setBadNumber(int badNumber) {
        this.badNumber = badNumber;
    }
}
```
Next, update the Converter component to implement ```ConfiguredResource```.
The ConfiguredResource interface allows you to specify the exact type of the configuration, 
in this case ```MyConverterConfiguration```.
```java
package com.ikasan.example.converter;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;

public class MyConverter implements Converter<String,Integer>, ConfiguredResource<MyConverterConfiguration>
{
    public Integer convert(String payload) throws TransformationException
    {
        String[] strings = payload.split(" ");
        int intPart = Integer.valueOf( strings[2] );
        if(intPart == 5)
        {
            throw new TransformationException("error - bad number received [" + 5 + "]");
        }
        return Integer.valueOf(intPart);
    }
}
```
Implementing this new contract will require some additional methods to be created to support the configuration instance and 
a configuredResourceId.

The ConfiguredResourceId is an identifier which ties this instance of the configuration with this component. Infact, if you use the same ConfiguredResourceId across multiple components
it will load the same instance of the configuration. This can sometimes be very useful where multiple components need to share a configuration.
However, care should be taken as sometimes shared configuration is not what you want and in that case you simply need to provide a different ConfiguredResourceId value.
It is best practice to set the ConfiguredResourceId to something representative of that module, flow, and component.

The example class below is updated for these method implementations.
```java
package com.ikasan.example.converter;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;

public class MyConverter implements Converter<String,Integer>, ConfiguredResource<MyConverterConfiguration>
{
    String configuredResourceId;
    MyConverterConfiguration configuration = new MyConverterConfiguration();
    
    public Integer convert(String payload) throws TransformationException
    {
        String[] strings = payload.split(" ");
        int intPart = Integer.valueOf( strings[2] );
        if(intPart == 5)
        {
            throw new TransformationException("error - bad number received [" + 5 + "]");
        }
        return Integer.valueOf(intPart);
    }

    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    public MyConverterConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MyConverterConfiguration configuration) {
        this.configuration = configuration;
    }
}
```
Finally, change the converter method to use the configuration attribute rather than the hard-coded 5.
```java
    public Integer convert(String payload) throws TransformationException
    {
        String[] strings = payload.split(" ");
        int intPart = Integer.valueOf( strings[2] );
        if(intPart == configuration.getBadNumber())
        {
            throw new TransformationException("error - bad number received [" + configuration.getBadNumber() + "]");
        }
        return Integer.valueOf(intPart);
    }
```
Ikasan also has the concept of Configured - this is typically used where your component, marked as a ConfiguredResource, has other classes that can also be configured, but arent defined as specific components.

In this case the subsequent classes would simply be marked as Configured.

So now we have updated our module lets build it, run it, and open the Console from a Browser.

#### Configuration Validation
The configuration class can implement the ```IsValidationAware``` interface which will require the class to implement a ```validate()``` method.
This method allows the configuration class to have specific attributes checked to ensure validity. The ```validate()``` method is called by Ikasan just prior to the configuration being passed to the component, any failure will result in an ```InvalidConfigurationException``` being thrown from validate().
Typically this should only be thrown from ```validate()``` on critical issues with the configuration instance. Validation will be called on any configuration implementing this contract.

### Dynamic Configured Resources
Configured resources can specifically be set to persist their configuration on every event invocation through the flow.
This can be useful where the configuration itself is updated based on the inflight event.
To do this the component must first be set as a ConfiguredResource (interface implementation on the component class) at build time. 
Once set dynamic configuration can be turned on at runtime through the configuration of the component invoker.

*NOTE: If dynamicConfiguration is false, any updates to a component configuration will only work whilst the flow is running - it will not be persisted. Restart of a flow will reset that configuration back to its original state.*

Setting the component invoker with dynamicConfiguration true or false turns dynamic configuration on and off respectively. 
Turning this on will ensure the configuration is loaded at the start of the inflight event and updated on completion of processing the inflight event.
Changing the content of the configuration is left to the remit of the developer. This can be achieved by updating the configuration in 
the component using that configuration; or, for off-the-shelf components, as a subsequent translator component.

#### FluentAPI Builder Pattern Usage
Dynamic Configuration can also be set programmatically via the FluentAPI builder pattern.
```java
         Flow eventGeneratingFlow = moduleBuilder.getFlowBuilder("Event Generating Flow")
                 .withExceptionResolver(builderFactory
                         .getExceptionResolverBuilder()
                         .addExceptionToAction(TransformationException.class, OnException.excludeEvent()).build())
                 .consumer("My Source Consumer", componentBuilder.eventGeneratingConsumer())
                 .converter("My Converter", new MyConverter(), org.ikasan.builder.invoker.Configuration.converterInvoker().withDynamicConfiguration(true))
                 .producer("My Target Producer", componentBuilder.logProducer())
                 .build();
```
Here is an example of changing a configuration property based on the incoming event within a ConfiguredResource component.

Lets first add a new attribute to hold timestamp in the configuration.
```java
package com.ikasan.example.converter;

public class MyConverterConfiguration
{
    long timestamp;
    
    int badNumber = 5;

    public int getBadNumber() {
        return badNumber;
    }

    public void setBadNumber(int badNumber) {
        this.badNumber = badNumber;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
```

Then update the converter to set the timestamp to the invocation time.
```java
package com.ikasan.example.converter;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;

public class MyConverter implements Converter<String,Integer>, ConfiguredResource<MyConverterConfiguration>
{
    String configuredResourceId;
    MyConverterConfiguration configuration = new MyConverterConfiguration();

    public Integer convert(String payload) throws TransformationException
    {
        // update a timestamp in the configuration based on the time the event hits this component
        configuration.setTimestamp( System.currentTimeMillis() );

        String[] strings = payload.split(" ");
        int intPart = Integer.valueOf( strings[2] );
        if(intPart == configuration.getBadNumber())
        {
            throw new TransformationException("error - bad number received [" + configuration.getBadNumber() + "]");
        }
        return Integer.valueOf(intPart);
    }

    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    public MyConverterConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MyConverterConfiguration configuration) {
        this.configuration = configuration;
    }
}
```

In this example a timestamp configuration property is set to current timestamp on the occurrence of each inflight event. By turning on dynamicConfiguration we can ensure this configuration change is persisted with the success of the event publication.
Should any exceptions occur within the flow then the persistence does not occur and the original configuration values restored on the next inflight event.

#### Configuration of Invoker via the Management Console
Changing DynamicConfiguration at runtime.
![Login](quickstart-images/dynamicConfiguration-invokerConfiguration.png)

![Login](quickstart-images/dynamicConfiguration-settingProperty.png)

#### Configuration of Invoker via the Ikasan Dashboard
TODO

#### Compatibility Note ####
Prior to release ikasaneip-2.1.0 you needed to implement a marker interface on the class to tell Ikasan that it was a dynamic configured resource. 
From ikasan-2.1.0 onwards, the DynamicConfigured interface has been removed and components can be marked as dynamically configured through runtime configuration on the component invoker.

When upgrading from a version prior to 2.1.0, you will need to remove any DynamicConfiguration interface implementations on your component classes and ensure you turn on dynamicConfiguration either through the FluentAPI builder pattern or the runtime console for that component.

## Managed Resource Component
TODO

```java
package com.ikasan.example.converter;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyConverter implements Converter<String,Integer>,
        ConfiguredResource<MyConverterConfiguration>, ManagedResource
{
    private final static Logger logger = LoggerFactory.getLogger(MyConverter.class);

    MyConverterConfiguration configuration  = new MyConverterConfiguration();
    String configuredResourceId;
    boolean critical;

    public Integer convert(String payload) throws TransformationException
    {
        // update a timestamp in the configuration based on the time the event hits this component
        configuration.setTimestamp( System.currentTimeMillis() );

        String[] strings = payload.split(" ");
        int intPart = Integer.valueOf( strings[2] );
        if(intPart == configuration.getBadNumber())
        {
            throw new TransformationException("error - bad number received [" + configuration.getBadNumber() + "]");
        }
        return Integer.valueOf(intPart);
    }

    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    public MyConverterConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MyConverterConfiguration myConverterConfiguration)
    {
        this.configuration =  myConverterConfiguration;
    }

    public void startManagedResource() 
    {
        configuration.setTimestamp(0L);
        logger.info("Call to start any managed resources");
    }

    public void stopManagedResource() {
        logger.info("Call to stop any managed resources");
    }

    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

    }

    public boolean isCriticalOnStartup() {
        return critical;
    }

    public void setCriticalOnStartup(boolean critical) {
        this.critical = critical;
    }
}
```
## Testing
TOOO
### Unit Testing
#### Methods
#### Components
#### Flows
Flow testing is a great way of verifying that the whole Integration Module bootstraps correctly. It also allows you to exercise each and every route through the flow in a controlled and verifiable manner. 

Before we create the flow test we need to change the module to provide certainty around the events invoking the flow. The default EndpointEventProvider for the Event Generating consumer creates as many events as possible as quickly as possible - we need to override this with something more measureable to test.

So firstly, lets create a consistent EndpointEventProvider implementation by adding the following to the ```com.ikasan.example.MyModule``` class.
```java
public class MyModule
{
    ...
    
    class MyMessageProvider implements EndpointEventProvider<String>
    {
        int count = 0;

        @Override
        public String getEvent()
        {
            return (++count <= 1 ?  "Test Message " + count : null);
        }

        @Override
        public void rollback()
        {

        }
    }
}
```
The above MyMessageProvider will create events until count exceeds 1 - so in this case just one event in total.

We also need to override the EndpointEventProvider on the consumer in the same class. The sample below shows the full java class with the new EndpointEventProvider implementation and override on the consumer.
```java
package com.ikasan.example;

import com.ikasan.example.converter.MyConverter;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.endpoint.consumer.api.spec.EndpointEventProvider;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.quartz.JobExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration("ModuleFactory")
@ImportResource( {
        "classpath:h2-datasource-conf.xml",
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml"
} )
public class MyModule
{
    @Resource BuilderFactory builderFactory;

    @Bean
    public Module myModule()
    {
        // Create a module builder from the builder factory
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("My Integration Module")
                .withDescription("My first integration module.");

        // Create a component builder from the builder factory
        ComponentBuilder componentBuilder = builderFactory.getComponentBuilder();

        // create a flow from the module builder and add required orchestration components
        Flow eventGeneratingFlow = moduleBuilder.getFlowBuilder("Event Generating Flow")
                .consumer("My Source Consumer", componentBuilder.eventGeneratingConsumer()
                .setEndpointEventProvider( new MyMessageProvider() ))
                .converter("My Converter", new MyConverter())
                .producer("My Target Producer", componentBuilder.logProducer())
                .build();

        // Add the created flow to the module builder and create the module
        Module module = moduleBuilder
                .addFlow(eventGeneratingFlow)
                .build();
        return module;
    }

    class MyMessageProvider implements EndpointEventProvider<String>
    {
        int count = 0;

        @Override
        public String getEvent()
        {
            return (++count <= 1 ?  "Test Message " + count : null);
        }

        @Override
        public void rollback()
        {

        }
    }
}
```
Now we have consistent event generation we can use the following test class.
```java
package com.ikasan.example;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest
{
    @Resource
    private Module moduleUnderTest;

    @Rule
    public IkasanFlowTestRule ikasanFlowTestRule = new IkasanFlowTestRule();

    @Before
    public void setup()
    {
        Flow flowUnderTest = (Flow) moduleUnderTest.getFlow("Event Generating Flow");
        ikasanFlowTestRule.withFlow(flowUnderTest);
    }

    @Test
    public void test()
    {
        ikasanFlowTestRule
                .consumer("My Source Consumer")
                .converter("My Converter")
                .producer("My Target Producer");

        ikasanFlowTestRule.startFlow();

        while (ikasanFlowTestRule.getFlowState().equals(Flow.RUNNING))
        {
            ikasanFlowTestRule.sleep(500);
        }

        Assert.assertTrue(ikasanFlowTestRule.getFlowState().equals(Flow.STOPPED));
    }
}
```

#### Exception Handling
#### Monitoring
#### Modules
#### Business Streams

# Ikasan Maven Archetypes
The basic constituents of an Ikasan Integration Module (IM) are the same. 
Due to this we have created some out of the box IM archetypes based on common technical integration problems.

- [DB (SQL) to JMS / JMS to DB (SQL) Integration Module Archetype](../mvn-archetype/ikasan-standalone-db-jms-im/Readme.md)
- [Local File System to JMS / JMS to Local File System Integration Module Archetype](../mvn-archetype/ikasan-standalone-filesystem-im/Readme.md)
- [FTP to JMS / JMS to FTP Integration Module Archetype](../mvn-archetype/ikasan-standalone-ftp-jms-im/Readme.md)
- [Hibernate DB to JMS / JMS to Hibernate DB Integration Module Archetype](../mvn-archetype/ikasan-standalone-hibernate-jms-im/Readme.md)
- [JMS to JMS Integration Module Archetype](../mvn-archetype/ikasan-standalone-jms-im/Readme.md)
- [SFTP to JMS / JMS to SFTP Integration Module Archetype](../mvn-archetype/ikasan-standalone-sftp-jms-im/Readme.md)
- [Vanilla Integration Module Archetype](../mvn-archetype/ikasan-standalone-vanilla-im/Readme.md)

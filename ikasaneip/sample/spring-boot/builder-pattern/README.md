# sample-builder-pattern

Sample builder-pattern project provides self contained example of Ikasan integration module. 
The sample is build as fat-jar containing all dependencies and bootstraps as a spring-boot web application with embedded tomcat web-container. 
As majority of core ikasan services depend on persistent store this sample starts up with embedded in memory H2 database.

sample-builder-pattern provides example of integration module using builder pattern. The module contains two flows:
* Scheduled Flow Name (This flow demonstrates usage of scheduled consumer)
  * consumer 
  * producer
* Jms Flow Name (This flow demonstrates usage of jms Consumer and producer created through builder pattern)
  * consumer
  * producer

## How to construct a flow using builder pattern
Check out the source code at [MyApplication](src/main/java/com/ikasan/sample/spring/boot/builderpattern/MyApplication.java) 
```java
 
 public Flow getJmsFlow(ModuleBuilder moduleBuilder,ComponentBuilder componentBuilder) {
   FlowBuilder flowBuilder = moduleBuilder.getFlowBuilder("Jms Flow Name");
 
   return flowBuilder.withDescription("Jms flow description")
      .consumer("consumer", componentBuilder.jmsConsumer().setConfiguredResourceId("configuredResourceId")
         .setDestinationJndiName("dynamicQueues/source")
         .setConnectionFactoryName("ConnectionFactory")
         .setConnectionFactoryJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
         .setConnectionFactoryJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)")
         .setDestinationJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
         .setDestinationJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)")
         .setAutoContentConversion(true)
         .build()
      )
      .producer("producer", componentBuilder.jmsProducer()
         .setConfiguredResourceId("crid")
         .setDestinationJndiName("dynamicQueues/target")
         .setConnectionFactoryName("ConnectionFactory")
         .setConnectionFactoryJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
         .setConnectionFactoryJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)")
         .setDestinationJndiPropertyFactoryInitial("org.apache.activemq.jndi.ActiveMQInitialContextFactory")
         .setDestinationJndiPropertyProviderUrl("failover:(vm://embedded-broker?create=false)").build()
      )
      .build();
 
     }
```

## How to build from source

```
mvn clean install
```


## How to startup

If you managed to obtain the jar by building it or by downloading it from mvn repo:
* https://oss.sonatype.org/content/repositories/snapshots/org/ikasan/sample-builder-pattern/2.0.0-SNAPSHOT/ 

You can start up the sample 

```java -jar sample-builder-pattern-2.0.0-SNAPSHOT.jar```

If all went well you will see following 
```
2017-10-22 20:42:55.896  INFO 2837 --- [main] o.i.m.s.ModuleInitialisationServiceImpl  : Module host [localhost:8080] running with PID [2837]
2017-10-22 20:42:55.907  INFO 2837 --- [main] o.i.m.s.ModuleInitialisationServiceImpl  : Server instance  [Server [id=null, name=localhost, description=http://localhost:8080/sample-builder-pattern, url=http://localhost, port=8080, createdDateTime=Sun Oct 22 20:42:55 BST 2017, updatedDateTime=Sun Oct 22 20:42:55 BST 2017]], creating...
(...)

2017-10-22 20:11:10.628  INFO 2734 --- [main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2017-10-22 20:11:10.640  INFO 2734 --- [main] o.s.c.support.DefaultLifecycleProcessor  : Starting beans in phase 0
2017-10-22 20:11:10.788  INFO 2734 --- [main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2017-10-22 20:11:10.798  INFO 2734 --- [main] o.i.s.s.boot.builderpattern.Application  : Started Application in 11.208 seconds (JVM running for 11.712)
Context ready
```

You can now access the basic web interface http://localhost:8080/sample-builder-pattern/ 


## How to navigate the web console


* Open Login Page ![Login](../../../developer/docs/sample-images/sample-login.png) 

* Login using admin/admin as username and password ![Home](../../../developer/docs/sample-images/home-page.png) 

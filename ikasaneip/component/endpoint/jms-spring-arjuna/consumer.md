[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# SpringTemplate JMS Consumer

<img src="../../../developer/docs/quickstart-images/event-driven-consumer.png" width="200px" align="left">The JMS consumer is Event Driven Consumer, used to connect to any Vendor specific JMS Broker(ActiveMQ, HornetQ, IBM MQ etc). However one need to include the related vendor specific libraries in the IM.
Read more about EIP [Event Driven Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/EventDrivenConsumer.html)
<br/>
<br/>
<br/>
<br/>

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| destinationJNDIProperties | Map<String,String> | Optional JNDI parameters map. Typical map would include following keys :<ul><li>java.naming.provider.url</li><li> java.naming.factory.initial</li><li>java.naming.factory.url.pkgs </li><li>java.naming.security.credentials</li><li>java.naming.security.principle</li></ul> |
| destinationJNDIName | String | Destination(Topic/Queue) name, which could refer as well to JNDI name |
| connectionFactoryJNDIProperties | Map<String,String> | Optional JNDI parameters map. Typical map would include following keys : <ul><li>java.naming.provider.url</li><li> java.naming.factory.initial</li><li>java.naming.factory.url.pkgs </li><li>java.naming.security.credentials</li><li>java.naming.security.principle</li></ul> |
| connectionFactoryName | String | ConnectionFactoryName is required if not already passed on the constructor |
| connectionFactoryUsername | String | Authentication principal  |
| connectionFactoryPassword | String | Id set on the JMS connection for durable subscribers |
| pubSubDomain | boolean | set to true to indicate that destination is a topic, otherwise destina|
| durableSubscriptionName | String | Id set on the JMS session for durable subscribers  |
| durable | boolean | Create a durable subscription (true) on the destination which will ensure messages aren&#39;t missed when the flow is stopped or paused.If not durable (false) messages on the destination will be missed when the flow is stopped or paused. This is only applicable to topics. |
| sessionTransacted | boolean | Sets whether the session should be part of a transaction. |
| autoContentConversion | boolean | Extract the content of the JMS message and present this to the next component (true); or leave as a JMS message payload (false). This option can save extracting the JMS message body in subsequent components if it is only the JMS content that is of interest. For instance, you are not interested in the JMS headers. |
| batchMode | boolean  | Use message batching. |
| batchSize |  integer  | Batching consumer maximum messages per batch limit. |
| autoSplitBatch | boolean  | When batchMode is true this option determines whether to automatically split the batch of messages into individual messages to fire downstream (true); or to simply pass them as a list of messages downstream (false). |
| maxConcurrentConsumers |  integer | Maximum number of concurrent consumers within this message listener. WARN: Using concurrent concurrency on the consumer requires downstream components within this flow to be thread safe.  |
| concurrentConsumers |  integer | Initial number of concurrent consumers within this message listener. WARN: Using concurrent concurrency on the consumer requires downstream components within this flow to be thread safe. |
| cacheLevel | integer  | Caching level of the underlying message listener container. <ul><li>CACHE\_NONE = 0</li><li>CACHE\_CONNECTION = 1</li><li>CACHE\_SESSION = 2</li><li>CACHE\_CONSUMER = 3</li><li>CACHE\_AUTO = 4</li></ui>  |
| sessionAcknowledgeMode | integer | |

##### Sample Usage - Spring XML

```xml
<!-- jmsSampleConsumer is a bean definition of component -->
<bean id="jmsSampleConsumer" class="org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer">
    <property name="messageProvider" ref="jmsToSftpConsumerListener"/>
    <property name="configuration">
        <bean class="org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration">
            <property name="destinationJndiName" value="java:jboss/exported/jms/topic/test.source"/>
            <property name="connectionFactoryName" value="java:/JmsXA"/>
            <property name="durableSubscriptionName" value="jmsSampleConsumer"/>
            <property name="pubSubDomain" value="true"/>
            <property name="durable" value="true"/>
            <property name="autoContentConversion" value="true"/>
            <property name="sessionTransacted" value="true"/>
        </bean>
    </property>
    <property name="configuredResourceId" value="jmsSampleConsumer"/>
</bean>

<bean id="jmsToSftpConsumerListener" class="org.springframework.jms.listener.ArjunaIkasanMessageListenerContainer">
    <property name="messageListener" ref="jmsSampleConsumer" />
    <property name="exceptionListener" ref="jmsSampleConsumer" />
    <property name="errorHandler" ref="jmsSampleConsumer" />
    <property name="transactionManager" ref="transactionManager" />
    <property name="localTransactionManager" ref="arjunaTransactionManager" />
</bean>

<!-- jmsSampleConsumerFlowElement is a bean definition of flow elements which uses jmsSampleConsumer as a component -->
<bean id="jmsSampleConsumerFlowElement" class="org.ikasan.builder.FlowElementFactory">
    <property name="name" value="JMS Consumer"/>
    <property name="component"  ref="jmsSampleConsumer"/>
    <property name="transition" ref="converterFlowElement"/>
</bean>

```

##### Sample Usage - builder pattern

```java
public class ModuleConfig {


  @Resource
  private BuilderFactory builderFactory;

  public Consumer getJmsConsumer(){


    Consumer jmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
            .setConnectionFactoryName("java:/JmsXA")
            .setDestinationJndiName("java:jboss/exported/jms/topic/test.source")
            .setDurableSubscriptionName("jmsSampleConsumer")
            .setPubSubDomain(true)
            .setDurable(true)
            .setAutoContentConversion(true)
            .setSessionTransacted(true)
            .setConfiguredResourceId("jmsConsumer")
            .build();
    return jmsConsumer;
  }
}

```

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |

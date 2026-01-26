![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# sample-spring-boot-kafka-reactive

Sample spring-boot-kafka-reactive project provides a self-contained example of an Ikasan integration module. 
The sample is built as a fat-jar containing all dependencies and bootstraps as a spring-boot web application with an embedded tomcat web-container. 
As the majority of core Ikasan services depend on a persistent store, this sample starts up with an embedded in-memory H2 database.

sample-spring-boot-kafka-reactive provides an example of an integration module using a reactive Kafka consumer and a JMS producer. In order to keep the sample self-contained, an embedded ActiveMQ broker is used for the JMS producer. The module contains a single flow with the following components:
* Reactive Kafka Consumer (subscribing to 'test-topic' topic)
* Exception Generating Broker 
* JMS Producer (sends messages to 'target' queue)


## How to construct 'Kafka Sample Flow' using the builder pattern
Check out the source code at [ModuleConfig](src/main/java/com/ikasan/sample/spring/boot/builderpattern/ModuleConfig.java)
```java
@Bean
public Module getModule(){

    ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-jms");

    FlowBuilder fb = mb.getFlowBuilder("Kafka Sample Flow");

    ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

    Producer jmsProducer = builderFactory.getComponentBuilder().jmsProducer()
            .setConnectionFactory(producerConnectionFactory)
            .setDestinationJndiName("target")
            .setConfiguredResourceId("jmsProducer")
            .build();

    InvokerConfiguration flowComponentInvokerConfiguration = new InvokerConfiguration();
    flowComponentInvokerConfiguration.setDynamicConfiguration(true);

    Flow flow = fb
            .withDescription("Flow demonstrates usage of Kafka Consumer and JMS Producer")
            .withExceptionResolver(builderFactory.getExceptionResolverBuilder()
                    .addExceptionToAction(SampleGeneratedException.class, OnException.excludeEvent())
                    .addExceptionToAction(EndpointException.class, OnException.retryIndefinitely(1000)).build())
            .withErrorReportingServiceFactory(errorReportingServiceFactory)
            .consumer("Kafka Consumer", getConsumer(), flowComponentInvokerConfiguration)
            .broker( "Exception Generating Broker", new ExceptionGeneratingBroker())
            .producer("JMS Producer", jmsProducer)
            .build();

    Module module = mb.withDescription("Sample Module")
        .addFlow(flow)
        .build();
    return module;
}

private Consumer getConsumer() {
    return this.builderFactory.getComponentBuilder().kafkaReactiveConsumer()
        .setConfigurationId("kafka-consumer")
        .setManagedEventIdentifierService(new KafkaStringRecordEventIdentifierServiceImpl())
        .setConfiguration(this.getKafkaConsumerConfiguration())
        .build();
}

private KafkaConsumerConfiguration getKafkaConsumerConfiguration() {
    KafkaConsumerConfiguration consumerConfiguration = new KafkaConsumerConfiguration();
    consumerConfiguration.setGroupId("testGroup");
    consumerConfiguration.setTopicName("test-topic");
    consumerConfiguration.setPartitions(new CopyOnWriteArrayList<>(List.of("0", "1", "2")));
    consumerConfiguration.setKeyDeserializer("org.apache.kafka.common.serialization.IntegerDeserializer");
    consumerConfiguration.setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");

    return consumerConfiguration;
}


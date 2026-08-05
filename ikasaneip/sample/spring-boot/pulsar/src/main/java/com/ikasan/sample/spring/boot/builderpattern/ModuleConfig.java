package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-ikasanMessageListener.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig
{
    @Autowired
    private BuilderFactory builderFactory;

    @Value("${pulsar.service.url}")
    private String pulsarServiceUrl;

    @Value("${pulsar.inbound.topic}")
    private String inboundTopic;

    @Value("${pulsar.outbound.topic}")
    private String outboundTopic;

    @Value("${pulsar.subscription.name}")
    private String subscriptionName;

    public Consumer pulsarConsumer()  {
        return builderFactory.getComponentBuilder().pulsarConsumer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopics(inboundTopic)
            .setSubscriptionName(subscriptionName)
            .setSubscriptionType("Shared")
            .setConfigurationId("pulsarConsumer")
            .build();
    }

    public Producer pulsarProducer() {
        return builderFactory.getComponentBuilder().pulsarProducer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopic(outboundTopic)
            .setProducerName("sample-producer")
            .setBatchingEnabled(true)
            .setCompressionType("LZ4")
            .setConfigurationId("pulsarProducer")
            .build();
    }

    // Schema-specific consumer/producer builders
    public Consumer pulsarStringConsumer()  {
        return builderFactory.getComponentBuilder().pulsarConsumer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopics("test-string-inbound-topic")
            .setSubscriptionName("string-schema-subscription")
            .setSubscriptionType("Shared")
            .setSchemaType("STRING")
            .setConfigurationId("stringConsumer")
            .build();
    }

    public Producer pulsarStringProducer() {
        return builderFactory.getComponentBuilder().pulsarProducer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopic("test-string-outbound-topic")
            .setProducerName("string-producer")
            .setSchemaType("STRING")
            .setConfigurationId("stringProducer")
            .build();
    }

    public Consumer pulsarInt32Consumer()  {
        return builderFactory.getComponentBuilder().pulsarConsumer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopics("test-int32-inbound-topic")
            .setSubscriptionName("int32-schema-subscription")
            .setSubscriptionType("Shared")
            .setSchemaType("INT32")
            .setConfigurationId("int32Consumer")
            .build();
    }

    public Producer pulsarInt32Producer() {
        return builderFactory.getComponentBuilder().pulsarProducer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopic("test-int32-outbound-topic")
            .setProducerName("int32-producer")
            .setSchemaType("INT32")
            .setConfigurationId("int32Producer")
            .build();
    }

    public Consumer pulsarJsonConsumer()  {
        return builderFactory.getComponentBuilder().pulsarConsumer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopics("test-json-inbound-topic")
            .setSubscriptionName("json-schema-subscription")
            .setSubscriptionType("Shared")
            .setSchemaType("JSON")
            .setMessageClassName("com.ikasan.sample.spring.boot.builderpattern.TestMessage")
            .setConfigurationId("jsonConsumer")
            .build();
    }

    public Producer pulsarJsonProducer() {
        return builderFactory.getComponentBuilder().pulsarProducer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopic("test-json-outbound-topic")
            .setProducerName("json-producer")
            .setSchemaType("JSON")
            .setMessageClassName("com.ikasan.sample.spring.boot.builderpattern.TestMessage")
            .setConfigurationId("jsonProducer")
            .build();
    }

    public Consumer pulsarAvroConsumer()  {
        return builderFactory.getComponentBuilder().pulsarConsumer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopics("test-avro-inbound-topic")
            .setSubscriptionName("avro-schema-subscription")
            .setSubscriptionType("Shared")
            .setSchemaType("AVRO")
            .setMessageClassName("com.ikasan.sample.spring.boot.builderpattern.TestMessage")
            .setConfigurationId("avroConsumer")
            .build();
    }

    public Producer pulsarAvroProducer() {
        return builderFactory.getComponentBuilder().pulsarProducer()
            .setServiceUrl(pulsarServiceUrl)
            .setTopic("test-avro-outbound-topic")
            .setProducerName("avro-producer")
            .setSchemaType("AVRO")
            .setMessageClassName("com.ikasan.sample.spring.boot.builderpattern.TestMessage")
            .setConfigurationId("avroProducer")
            .build();
    }

    @Bean
    public Module getModule() {
        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-pulsar");

        // Original BYTES schema flow
        FlowBuilder fb = mb.getFlowBuilder("Pulsar Sample Flow");
        Flow flow = fb
                .withDescription("Flow demonstrates usage of Pulsar Consumer and Pulsar Producer")
                .consumer("Pulsar Consumer", this.pulsarConsumer())
                .broker( "Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker( "Delay Generating Broker", new DelayGenerationBroker())
                .producer("Pulsar Producer", this.pulsarProducer())
                .build();

        // STRING schema flow
        FlowBuilder stringFb = mb.getFlowBuilder("String Schema Flow");
        Flow stringFlow = stringFb
                .withDescription("Flow with STRING schema")
                .consumer("String Consumer", this.pulsarStringConsumer())
                .broker("Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker("Delay Generating Broker", new DelayGenerationBroker())
                .producer("String Producer", this.pulsarStringProducer())
                .build();

        // INT32 schema flow
        FlowBuilder int32Fb = mb.getFlowBuilder("INT32 Schema Flow");
        Flow int32Flow = int32Fb
                .withDescription("Flow with INT32 schema")
                .consumer("INT32 Consumer", this.pulsarInt32Consumer())
                .broker("Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker("Delay Generating Broker", new DelayGenerationBroker())
                .producer("INT32 Producer", this.pulsarInt32Producer())
                .build();

        // JSON schema flow
        FlowBuilder jsonFb = mb.getFlowBuilder("JSON Schema Flow");
        Flow jsonFlow = jsonFb
                .withDescription("Flow with JSON schema")
                .consumer("JSON Consumer", this.pulsarJsonConsumer())
                .broker("Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker("Delay Generating Broker", new DelayGenerationBroker())
                .producer("JSON Producer", this.pulsarJsonProducer())
                .build();

        // AVRO schema flow
        FlowBuilder avroFb = mb.getFlowBuilder("AVRO Schema Flow");
        Flow avroFlow = avroFb
                .withDescription("Flow with AVRO schema")
                .consumer("AVRO Consumer", this.pulsarAvroConsumer())
                .broker("Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker("Delay Generating Broker", new DelayGenerationBroker())
                .producer("AVRO Producer", this.pulsarAvroProducer())
                .build();

        Module module = mb.withDescription("Sample Pulsar Module")
            .addFlow(flow)
            .addFlow(stringFlow)
            .addFlow(int32Flow)
            .addFlow(jsonFlow)
            .addFlow(avroFlow)
            .build();
        return module;
    }
}

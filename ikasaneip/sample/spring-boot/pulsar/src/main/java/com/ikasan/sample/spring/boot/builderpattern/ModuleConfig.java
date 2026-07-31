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

    @Bean
    public Module getModule() {
        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-pulsar");

        FlowBuilder fb = mb.getFlowBuilder("Pulsar Sample Flow");

        Flow flow = fb
                .withDescription("Flow demonstrates usage of Pulsar Consumer and Pulsar Producer")
                .consumer("Pulsar Consumer", this.pulsarConsumer())
                .broker( "Exception Generating Broker", new ExceptionGeneratingBroker())
                .broker( "Delay Generating Broker", new DelayGenerationBroker())
                .producer("Pulsar Producer", this.pulsarProducer())
                .build();

        Module module = mb.withDescription("Sample Pulsar Module")
            .addFlow(flow)
            .build();
        return module;
    }
}

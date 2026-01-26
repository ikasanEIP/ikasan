package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.kafka.client.reactive.consumer.KafkaConsumer;
import org.ikasan.component.endpoint.kafka.client.reactive.consumer.KafkaConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import reactor.kafka.receiver.ReceiverRecord;

public class KafkaReactiveConsumerBuilderImpl implements KafkaReactiveConsumerBuilder {

    protected AopProxyProvider aopProxyProvider;
    protected ConfigurationService configurationService;
    protected ManagedRelatedEventIdentifierService managedEventIdentifierService;
    protected EventFactory eventFactory;
    protected ResubmissionEventFactory resubmissionEventFactory;
    protected KafkaConsumerConfiguration kafkaConsumerConfiguration;
    protected MessageListener<ReceiverRecord> messageListener;
    protected String configurationId;

    /**
     * Constructor for KafkaReactiveConsumerBuilderImpl.
     *
     * @param aopProxyProvider the AOP Proxy Provider used for applying pointcuts
     * @param configurationService the Configuration Service for managing configuration
     */
    public KafkaReactiveConsumerBuilderImpl(AopProxyProvider aopProxyProvider, ConfigurationService configurationService) {
        this.aopProxyProvider = aopProxyProvider;
        if(this.aopProxyProvider == null) {
            throw new IllegalArgumentException("this.aopProxyProvider cannot be null!");
        }
        this.configurationService = configurationService;
        if(this.configurationService == null) {
            throw new IllegalArgumentException("this.configurationService cannot be null!");
        }
    }

    @Override
    public KafkaReactiveConsumerBuilder setManagedEventIdentifierService(ManagedRelatedEventIdentifierService managedEventIdentifierService) {
        this.managedEventIdentifierService = managedEventIdentifierService;
        return this;
    }

    @Override
    public KafkaReactiveConsumerBuilder setListener(MessageListener<ReceiverRecord> messageListener) {
        this.messageListener = messageListener;
        return this;
    }

    @Override
    public KafkaReactiveConsumerBuilder setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    @Override
    public KafkaReactiveConsumerBuilder setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
        return this;
    }

    @Override
    public KafkaReactiveConsumerBuilder setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
        return this;
    }

    @Override
    public KafkaReactiveConsumerBuilder setConfiguration(KafkaConsumerConfiguration configuration) {
        this.kafkaConsumerConfiguration = configuration;
        return this;
    }

    @Override
    public Consumer build() {
        KafkaConsumer kafkaConsumer = new KafkaConsumer(this.configurationService);
        kafkaConsumer.setConfiguration(this.kafkaConsumerConfiguration);
        kafkaConsumer.setConfiguredResourceId(this.configurationId);

        if(messageListener != null) {
            kafkaConsumer.setMessageListener(this.aopProxyProvider.applyPointcut("Kafka Reactive Consumer", messageListener));
        }
        else {
            kafkaConsumer.setMessageListener(this.aopProxyProvider.applyPointcut("Kafka Reactive Consumer", kafkaConsumer));
        }

        if(this.eventFactory != null) {
            kafkaConsumer.setEventFactory(this.eventFactory);
        }

        if(this.resubmissionEventFactory != null) {
            kafkaConsumer.setResubmissionEventFactory(this.resubmissionEventFactory);
        }

        if(this.managedEventIdentifierService == null) {
            throw new RuntimeException("The managedRelatedEventIdentifierService is null and there is no default" +
                " implementation provided for the reactive KafkaConsumer. All reactive Kafka Consumers require" +
                " a custom implementation of the ManagedRelatedEventIdentifierService that derives the identifier" +
                " from the value associated with the Kafka ConsumerRecord, in a reliably reproducible and unique manner" +
                " from the associated business data.");
        }

        kafkaConsumer.setManagedIdentifierService(this.managedEventIdentifierService);

        return kafkaConsumer;
    }
}

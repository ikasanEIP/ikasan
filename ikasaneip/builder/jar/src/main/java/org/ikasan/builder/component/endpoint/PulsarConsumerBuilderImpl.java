package org.ikasan.builder.component.endpoint;

import jakarta.transaction.TransactionManager;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.pulsar.consumer.InboundQueueMessageListener;
import org.ikasan.component.endpoint.pulsar.consumer.PulsarConsumer;
import org.ikasan.component.endpoint.pulsar.consumer.configuration.PulsarConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;

/**
 * Implementation of PulsarConsumerBuilder.
 *
 * @author Ikasan Development Team
 */
public class PulsarConsumerBuilderImpl implements PulsarConsumerBuilder {

    protected AopProxyProvider aopProxyProvider;
    protected TransactionManager transactionManager;
    protected PulsarConsumerConfiguration configuration;
    protected ManagedRelatedEventIdentifierService managedEventIdentifierService;
    protected EventListener<?> eventListener;
    protected EventFactory eventFactory;
    protected ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService;
    protected ResubmissionEventFactory resubmissionEventFactory;
    protected String configurationId;

    /**
     * Constructor
     *
     * @param aopProxyProvider
     * @param transactionManager
     */
    public PulsarConsumerBuilderImpl(AopProxyProvider aopProxyProvider, TransactionManager transactionManager) {
        this.aopProxyProvider = aopProxyProvider;
        if (this.aopProxyProvider == null) {
            throw new IllegalArgumentException("aopProxyProvider cannot be null!");
        }
        this.transactionManager = transactionManager;
        if (this.transactionManager == null) {
            throw new IllegalArgumentException("transactionManager cannot be null!");
        }
        this.configuration = new PulsarConsumerConfiguration();
    }

    @Override
    public PulsarConsumerBuilder setConfiguration(PulsarConsumerConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public PulsarConsumerBuilder setServiceUrl(String serviceUrl) {
        this.configuration.setServiceUrl(serviceUrl);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setTopics(String... topics) {
        this.configuration.setTopics(topics);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setSubscriptionName(String subscriptionName) {
        this.configuration.setSubscriptionName(subscriptionName);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setSubscriptionType(String subscriptionType) {
        this.configuration.setSubscriptionType(subscriptionType);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setConsumerName(String consumerName) {
        this.configuration.setConsumerName(consumerName);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setAuthenticationEnabled(boolean authenticationEnabled) {
        this.configuration.setAuthenticationEnabled(authenticationEnabled);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setAuthPluginClassName(String authPluginClassName) {
        this.configuration.setAuthPluginClassName(authPluginClassName);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setAuthParams(String authParams) {
        this.configuration.setAuthParams(authParams);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setTlsEnabled(boolean tlsEnabled) {
        this.configuration.setTlsEnabled(tlsEnabled);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setTlsTrustCertsFilePath(String tlsTrustCertsFilePath) {
        this.configuration.setTlsTrustCertsFilePath(tlsTrustCertsFilePath);
        return this;
    }

    @Override
    public PulsarConsumerBuilder setManagedEventIdentifierService(ManagedRelatedEventIdentifierService managedEventIdentifierService) {
        this.managedEventIdentifierService = managedEventIdentifierService;
        return this;
    }

    @Override
    public PulsarConsumerBuilder setListener(EventListener<?> eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    @Override
    public PulsarConsumerBuilder setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    @Override
    public PulsarConsumerBuilder setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService) {
        this.managedRelatedEventIdentifierService = managedRelatedEventIdentifierService;
        return this;
    }

    @Override
    public PulsarConsumerBuilder setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
        return this;
    }

    @Override
    public PulsarConsumerBuilder setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
        return this;
    }

    @Override
    public Consumer build() {
        InboundQueueMessageListener inboundQueueMessageListener = new InboundQueueMessageListener();

        PulsarConsumer consumer = new PulsarConsumer(transactionManager, inboundQueueMessageListener);
        consumer.setConfiguration(configuration);

        MessageListener messageListener = this.aopProxyProvider.applyPointcut("bigQueueConsumer", consumer);
        inboundQueueMessageListener.setMessageListener(messageListener);
        inboundQueueMessageListener.setEndpointListener(consumer);

        if (configurationId != null) {
            consumer.setConfiguredResourceId(configurationId);
        }

        if (managedRelatedEventIdentifierService != null) {
            consumer.setManagedIdentifierService(managedRelatedEventIdentifierService);
        }

        if (eventListener != null) {
            consumer.setListener(eventListener);
        }

        if (eventFactory != null) {
            consumer.setEventFactory(eventFactory);
        }

        if (resubmissionEventFactory != null) {
            consumer.setResubmissionEventFactory(resubmissionEventFactory);
        }

        return consumer;
    }
}

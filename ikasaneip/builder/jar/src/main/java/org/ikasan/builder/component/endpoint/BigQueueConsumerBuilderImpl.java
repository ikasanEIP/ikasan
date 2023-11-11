package org.ikasan.builder.component.endpoint;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.bigqueue.consumer.BigQueueConsumer;
import org.ikasan.component.endpoint.bigqueue.consumer.InboundQueueMessageRunner;
import org.ikasan.component.endpoint.bigqueue.consumer.configuration.BigQueueConsumerConfiguration;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.serialiser.Serialiser;

import jakarta.transaction.TransactionManager;

public class BigQueueConsumerBuilderImpl implements BigQueueConsumerBuilder {
    private AopProxyProvider aopProxyProvider;

    private TransactionManager transactionManager;
    private IBigQueue inboundQueue;
    private boolean putErrorsToBackOfQueue;
    private Serialiser serialiser = new BigQueueMessageJsonSerialiser();
    private ManagedRelatedEventIdentifierService managedEventIdentifierService;
    private EventListener<?> eventListener;
    private EventFactory eventFactory;

    private ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService;
    private ResubmissionEventFactory resubmissionEventFactory;
    private String configurationId;

    public BigQueueConsumerBuilderImpl(AopProxyProvider aopProxyProvider, TransactionManager transactionManager) {
        this.aopProxyProvider = aopProxyProvider;
        if(this.aopProxyProvider == null) {
            throw new IllegalArgumentException("aopProxyProvider cannot be null!");
        }
        this.transactionManager = transactionManager;
        if(this.transactionManager == null) {
            throw new IllegalArgumentException("transaction manager cannot be null!");
        }
    }

    @Override
    public BigQueueConsumerBuilder setInboundQueue(IBigQueue inboundQueue) {
        this.inboundQueue = inboundQueue;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setPutErrorsToBackOfQueue(boolean putErrorsToBackOfQueue) {
        this.putErrorsToBackOfQueue = putErrorsToBackOfQueue;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setSerialiser(Serialiser serialiser) {
        this.serialiser = serialiser;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setManagedEventIdentifierService(ManagedRelatedEventIdentifierService managedEventIdentifierService) {
        this.managedEventIdentifierService = managedEventIdentifierService;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setListener(EventListener<?> eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService) {
        this.managedEventIdentifierService = managedRelatedEventIdentifierService;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
        return this;
    }

    @Override
    public BigQueueConsumerBuilder setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
        return this;
    }

    @Override
    public BigQueueConsumer build() {
        InboundQueueMessageRunner inboundQueueMessageRunner = new InboundQueueMessageRunner(inboundQueue, serialiser);
        BigQueueConsumer consumer = new BigQueueConsumer(inboundQueue
            , inboundQueueMessageRunner, this.transactionManager);
        BigQueueConsumerConfiguration configuration = new BigQueueConsumerConfiguration();
        configuration.setPutErrorsToBackOfQueue(this.putErrorsToBackOfQueue);
        consumer.setConfiguration(configuration);
        consumer.setConfiguredResourceId(this.configurationId);

        consumer.setSerialiser(serialiser);
        consumer.setManagedIdentifierService(this.managedEventIdentifierService);

        MessageListener messageListener = this.aopProxyProvider.applyPointcut("bigQueueConsumer", consumer);
        inboundQueueMessageRunner.setMessageListener(messageListener);

        if(messageListener instanceof EndpointListener listener)
        {
            inboundQueueMessageRunner.setEndpointListener( listener );
        }

        if(this.eventListener != null) {
            consumer.setListener(eventListener);
        }

        if(this.eventFactory != null) {
            consumer.setEventFactory(this.eventFactory);
        }

        if(this.managedEventIdentifierService != null) {
            consumer.setManagedIdentifierService(this.managedEventIdentifierService);
        }

        if(this.resubmissionEventFactory != null) {
            consumer.setResubmissionEventFactory(this.resubmissionEventFactory);
        }

        return consumer;
    }
}

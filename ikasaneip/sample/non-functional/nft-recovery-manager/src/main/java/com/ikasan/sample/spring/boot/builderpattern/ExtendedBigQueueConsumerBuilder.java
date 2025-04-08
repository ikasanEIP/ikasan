package com.ikasan.sample.spring.boot.builderpattern;

import javax.transaction.TransactionManager;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.endpoint.BigQueueConsumerBuilderImpl;
import org.ikasan.component.endpoint.bigqueue.consumer.BigQueueConsumer;
import org.ikasan.component.endpoint.bigqueue.consumer.InboundQueueMessageRunner;
import org.ikasan.component.endpoint.bigqueue.consumer.configuration.BigQueueConsumerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.event.MessageListener;

public class ExtendedBigQueueConsumerBuilder extends BigQueueConsumerBuilderImpl {
    /**
     * Construct an ExtendedBigQueueConsumerBuilder with the provided AopProxyProvider and TransactionManager.
     *
     * @param aopProxyProvider the AOP Proxy Provider for applying pointcuts
     * @param transactionManager the Transaction Manager for managing transactions
     */
    public ExtendedBigQueueConsumerBuilder(AopProxyProvider aopProxyProvider, TransactionManager transactionManager) {
        super(aopProxyProvider, transactionManager);
    }

    @Override
    public BigQueueConsumer build() {
        InboundQueueMessageRunner inboundQueueMessageRunner = new InboundQueueMessageRunner(inboundQueue, serialiser);
        ExtendedBigQueueConsumer consumer = new ExtendedBigQueueConsumer(inboundQueue
            , inboundQueueMessageRunner, this.transactionManager);
        BigQueueConsumerConfiguration configuration = new BigQueueConsumerConfiguration();
        configuration.setPutErrorsToBackOfQueue(this.putErrorsToBackOfQueue);
        consumer.setConfiguration(configuration);
        consumer.setConfiguredResourceId(this.configurationId);

        consumer.setSerialiser(serialiser);
        consumer.setManagedIdentifierService(this.managedEventIdentifierService);

        MessageListener messageListener = this.aopProxyProvider.applyPointcut("extendedBigQueueConsumer", consumer);
        inboundQueueMessageRunner.setMessageListener(messageListener);

        if(messageListener instanceof EndpointListener)
        {
            inboundQueueMessageRunner.setEndpointListener((EndpointListener) messageListener);
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

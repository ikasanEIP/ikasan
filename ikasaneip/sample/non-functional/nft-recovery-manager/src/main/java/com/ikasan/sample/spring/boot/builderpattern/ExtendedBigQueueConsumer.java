package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.transaction.TransactionManager;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.consumer.BigQueueConsumer;
import org.ikasan.component.endpoint.bigqueue.consumer.InboundQueueMessageRunner;
import org.ikasan.component.endpoint.bigqueue.consumer.NullPayloadCallback;
import org.ikasan.component.endpoint.bigqueue.consumer.configuration.BigQueueConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionService;

import javax.transaction.xa.XAResource;

public class ExtendedBigQueueConsumer<T> extends BigQueueConsumer<T> implements Consumer<EventListener<?>, EventFactory>,
    ManagedIdentifierService<ManagedRelatedEventIdentifierService>, EndpointListener<T, Throwable>, MessageListener<T>,
    ConfiguredResource<BigQueueConsumerConfiguration>,
    ResubmissionService<T>, XAResource, NullPayloadCallback {

    private boolean throwExceptionOnStart = false;

    /**
     * Constructs a new ExtendedBigQueueConsumer with the provided inbound queue, message runner, and transaction manager.
     *
     * @param inboundQueue the IBigQueue to consume messages from
     * @param inboundQueueMessageRunner the InboundQueueMessageRunner to process messages received from the inbound queue
     * @param transactionManager the TransactionManager to handle transactions for the consumer
     */
    public ExtendedBigQueueConsumer(IBigQueue inboundQueue, InboundQueueMessageRunner inboundQueueMessageRunner
        , TransactionManager transactionManager) {
        super(inboundQueue, inboundQueueMessageRunner, transactionManager);
    }

    @Override
    public void start() {
        if(ExceptionToggle.isThrowStartRetryException()) throw new EndpointException("Error!");
        super.start();
    }
}

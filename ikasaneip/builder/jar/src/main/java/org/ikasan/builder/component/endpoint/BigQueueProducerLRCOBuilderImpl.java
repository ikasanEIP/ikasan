package org.ikasan.builder.component.endpoint;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.producer.BigQueueProducerLRCO;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.serialiser.Serialiser;

import javax.transaction.TransactionManager;

public class BigQueueProducerLRCOBuilderImpl implements BigQueueProducerLRCOBuilder {

    private TransactionManager transactionManager;
    private IBigQueue outboundQueue;
    private Serialiser serialiser = new BigQueueMessageJsonSerialiser();;

    public BigQueueProducerLRCOBuilderImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        if(this.transactionManager == null) {
            throw new IllegalArgumentException("transaction manager cannot be null!");
        }
    }

    @Override
    public BigQueueProducerLRCOBuilder setOutboundQueue(IBigQueue outboundQueue) {
        this.outboundQueue = outboundQueue;
        return this;
    }

    @Override
    public BigQueueProducerLRCOBuilder setSerialiser(Serialiser serialiser) {
         this.serialiser = serialiser;
         return this;
    }

    @Override
    public Producer build() {
        BigQueueProducerLRCO producer = new BigQueueProducerLRCO<>(this.outboundQueue, this.transactionManager);
        producer.setSerialiser(serialiser);

        return producer;
    }
}

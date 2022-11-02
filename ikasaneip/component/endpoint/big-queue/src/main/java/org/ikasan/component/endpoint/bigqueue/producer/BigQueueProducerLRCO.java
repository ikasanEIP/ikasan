package org.ikasan.component.endpoint.bigqueue.producer;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.TransactionManager;

/**
 * Implementation of a BigQueue producer.
 *
 * @author Ikasan Development Team
 */
public class BigQueueProducerLRCO<T> implements Producer<FlowEvent> {

    private static Logger logger = LoggerFactory.getLogger(BigQueueProducerLRCO.class);

    private TransactionManager transactionManager;
    private IBigQueue outboundQueue;
    private Serialiser<T,byte[]> serialiser;
    private BigQueueConnection connection = null;

    /**
     * Constructor
     *
     * @param outboundQueue
     */
    public BigQueueProducerLRCO(IBigQueue outboundQueue, TransactionManager transactionManager) {
        this.outboundQueue = outboundQueue;
        if(this.outboundQueue == null) {
            throw new IllegalArgumentException("inboundQueue cannot be null!");
        }
        this.transactionManager = transactionManager;
        if(this.transactionManager == null) {
            throw new IllegalArgumentException("transactionManager cannot be null!");
        }
        this.serialiser = new BigQueueMessageJsonSerialiser();
    }

    /**
     * Override the default implementation of the serialiser.
     * The default serialiser of for big queue messages.
     *
     * @param serialiser
     */
    public void setSerialiser(Serialiser<T, byte[]> serialiser) {
        this.serialiser = serialiser;
    }

    @Override
    public void invoke(FlowEvent payload) throws EndpointException {
        try {
            BigQueueConnectionCallback bigQueueConnectionCallback = new BigQueueConnectionCallbackImpl<>(payload, this.outboundQueue,
                this.serialiser);
            connection = new BigQueueConnection(bigQueueConnectionCallback);
            this.transactionManager.getTransaction().enlistResource(connection);
        }
        catch (Exception e) {
            throw new EndpointException(e);
        }
    }
}

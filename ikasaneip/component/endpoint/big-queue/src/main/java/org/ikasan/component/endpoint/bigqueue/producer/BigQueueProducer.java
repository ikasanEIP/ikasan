package org.ikasan.component.endpoint.bigqueue.producer;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.serialiser.Serialiser;

import java.io.IOException;

/**
 * Implementation of a BigQueue producer.
 *
 * @author Ikasan Development Team
 */
public class BigQueueProducer<T> implements Producer<T> {

    protected IBigQueue inboundQueue;
    protected Serialiser<T,byte[]> serialiser;

    /**
     * Constructor
     *
     * @param inboundQueue
     */
    public BigQueueProducer(IBigQueue inboundQueue) {
        this.inboundQueue = inboundQueue;
        if(this.inboundQueue == null) {
            throw new IllegalArgumentException("inboundQueue cannot be null!");
        }

        this.serialiser = new BigQueueMessageJsonSerialiser();
    }

    /**
     * Override the default implementation of the serialiser.
     * The default serialiser of for big queue messages.
     * @param serialiser
     */
    public void setSerialiser(Serialiser<T, byte[]> serialiser) {
        this.serialiser = serialiser;
    }

    @Override
    public void invoke(T payload) throws EndpointException {
        try {
            this.inboundQueue.enqueue(this.serialiser.serialise(payload));
        }
        catch (IOException e) {
            throw new EndpointException(e);
        }
    }
}

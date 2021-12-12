package org.ikasan.component.endpoint.bigqueue.producer;

import com.leansoft.bigqueue.IBigQueue;
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
     * @param serialiser
     */
    public BigQueueProducer(IBigQueue inboundQueue, Serialiser<T,byte[]> serialiser) {
        this.inboundQueue = inboundQueue;
        if(this.inboundQueue == null) {
            throw new IllegalArgumentException("inboundQueue cannot be null!");
        }
        this.serialiser = serialiser;
        if(this.serialiser == null) {
            throw new IllegalArgumentException("serialiser cannot bee null!");
        }
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

package org.ikasan.builder.component.endpoint;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.component.Builder;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.serialiser.Serialiser;

/**
 * Contract for a BigQueue consumer builder.
 *
 * @author Ikasan Development Team.
 */
public interface BigQueueProducerLRCOBuilder extends Builder<Producer>
{
    /**
     * Set the outbound queue that the producer will publish messages to.
     *
     * @param outboundQueue
     */
    BigQueueProducerLRCOBuilder setOutboundQueue(IBigQueue outboundQueue);

    /**
     * Set the serialiser used to serialise/deserialise the big queue messages.
     *
     * @param serialiser
     */
    BigQueueProducerLRCOBuilder setSerialiser(Serialiser serialiser);

}

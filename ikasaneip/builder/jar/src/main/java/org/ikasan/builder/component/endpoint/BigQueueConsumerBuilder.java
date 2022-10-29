package org.ikasan.builder.component.endpoint;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.component.Builder;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.serialiser.Serialiser;

/**
 * Contract for a BigQueue consumer builder.
 *
 * @author Ikasan Development Team.
 */
public interface BigQueueConsumerBuilder extends Builder<Consumer>
{
    /**
     * Set the inbound queue that the consumer will consume messages from.
     *
     * @param inboundQueue
     */
    BigQueueConsumerBuilder setInboundQueue(IBigQueue inboundQueue);

    /**
     * Flag to indicate if errors are put onto the back of the queue.
     *
     * @param putErrorsToBackOfQueue
     */
    BigQueueConsumerBuilder setPutErrorsToBackOfQueue(boolean putErrorsToBackOfQueue);

    /**
     * Set the serialiser used to serialise/deserialise the big queue messages.
     *
     * @param serialiser
     */
    BigQueueConsumerBuilder setSerialiser(Serialiser serialiser);

    /**
     * Set the managed event identifier service.
     *
     * @param managedEventIdentifierService
     * @return
     */
    BigQueueConsumerBuilder setManagedEventIdentifierService(ManagedRelatedEventIdentifierService managedEventIdentifierService);

    /**
     * Set the event listener on the consumer.
     *
     * @param eventListener
     */
    BigQueueConsumerBuilder setListener(EventListener<?> eventListener);

    /**
     * Set the event factor on the consumer.
     *
     * @param eventFactory
     */
    BigQueueConsumerBuilder setEventFactory(EventFactory eventFactory);

    /**
     * Set the managed identifier service on the consumer.
     *
     * @param managedRelatedEventIdentifierService
     */
    BigQueueConsumerBuilder setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService);

    /**
     * Set the resubmission event factory on the consumer.
     *
     * @param resubmissionEventFactory
     */
    BigQueueConsumerBuilder setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory);

    /**
     * Set the configuration id on the consumer.
     *
     * @param configurationId
     */
    BigQueueConsumerBuilder setConfigurationId(String configurationId);
}

package org.ikasan.builder.component.endpoint;

import com.leansoft.bigqueue.IBigQueue;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.Builder;
import org.ikasan.component.endpoint.bigqueue.consumer.BigQueueConsumer;
import org.ikasan.component.endpoint.consumer.api.spec.EndpointEventProvider;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.*;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.serialiser.Serialiser;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.util.concurrent.Executors;

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

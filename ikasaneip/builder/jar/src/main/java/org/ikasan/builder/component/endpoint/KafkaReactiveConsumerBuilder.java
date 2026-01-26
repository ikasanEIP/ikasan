package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.component.Builder;
import org.ikasan.component.endpoint.kafka.client.reactive.consumer.KafkaConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import reactor.kafka.receiver.ReceiverRecord;

/**
 * Contract for a BigQueue consumer builder.
 *
 * @author Ikasan Development Team.
 */
public interface KafkaReactiveConsumerBuilder extends Builder<Consumer>
{
    /**
     * Set the managed event identifier service.
     *
     * @param managedEventIdentifierService
     * @return
     */
    KafkaReactiveConsumerBuilder setManagedEventIdentifierService(ManagedRelatedEventIdentifierService managedEventIdentifierService);


    /**
     * Set the message listener for the Kafka reactive consumer.
     *
     * @param messageListener the message listener to be set
     * @return this KafkaReactiveConsumerBuilder instance
     */
    KafkaReactiveConsumerBuilder setListener(MessageListener<ReceiverRecord> messageListener);

    /**
     * Set the event factor on the consumer.
     *
     * @param eventFactory
     */
    KafkaReactiveConsumerBuilder setEventFactory(EventFactory eventFactory);

     /**
     * Set the resubmission event factory on the consumer.
     *
     * @param resubmissionEventFactory
     */
    KafkaReactiveConsumerBuilder setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory);

    /**
     * Set the configuration id on the consumer.
     *
     * @param configurationId
     */
    KafkaReactiveConsumerBuilder setConfigurationId(String configurationId);

    /**
     * Set the configuration for the Kafka reactive consumer.
     *
     * @param configuration the Kafka consumer configuration to be set
     * @return a KafkaReactiveConsumerBuilder instance with the specified configuration set
     */
    KafkaReactiveConsumerBuilder setConfiguration(KafkaConsumerConfiguration configuration);
}

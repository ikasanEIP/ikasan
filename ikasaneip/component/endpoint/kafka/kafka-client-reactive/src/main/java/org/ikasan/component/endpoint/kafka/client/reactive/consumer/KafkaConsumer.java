/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.endpoint.kafka.client.reactive.consumer;

import org.apache.kafka.common.TopicPartition;
import org.ikasan.exceptionResolver.action.ExcludeEventAction;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.*;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a Kafka client consumer.
 *
 * @author Ikasan Development Team
 */
public class KafkaConsumer
    implements Consumer<EventListener<?>, EventFactory>,
        ManagedIdentifierService<ManagedRelatedEventIdentifierService>, MessageListener<ReceiverRecord>,
        ConfiguredResource<KafkaConsumerConfiguration>, ResubmissionService<Object>,
        EndpointListener<ReceiverRecord, Throwable>
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    /**
     * Represents the identifier of a configuration, typically used for
     * uniquely identifying a specific configuration setting or setup.
     */
    private String configurationId;

    /**
     * Class representing the configuration for the Kafka Consumer module.
     */
    private KafkaConsumerConfiguration kafkaConsumerConfiguration;

    /**
     * Represents a disposable resource that needs to be cleaned up after its use.
     */
    private Disposable disposable;

    /** consumer event factory */
    protected EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** resubmission event factory */
    protected ResubmissionEventFactory<Resubmission<?>> resubmissionEventFactory;

    /** consumer event listener */
    protected EventListener eventListener;

    /**
     * Protected variable storing an instance of ManagedRelatedEventIdentifierService.
     * This service provides the contract for adding a related business event identifier
     * on the creation of a business event. It allows for tying a main business event
     * to an immutable business identifier for the duration of the business event's life.
     *
     * This service is commonly utilized when the business event mutates during a flow,
     * such as in a Splitter, and tracking of related business events is necessary.
     */
    protected ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService = null;

    /**
     * Variable to hold a MessageProcessor instance specialized to process ReceiverRecords.
     */
    protected  MessageListener<ReceiverRecord> messageListener;

    /**
     * Represents a private variable configurationService of type ConfigurationService.
     * ConfigurationService defines the operational contract of any configuration service in Ikasan.
     * This variable is used to configure and update resources.
     *
     * @see ConfigurationService
     */
    private ConfigurationService configurationService;

    /**
     * Constructor for creating a KafkaConsumer instance using the provided ConfigurationService.
     *
     * @param configurationService the ConfigurationService to use for configuration
     */
    public KafkaConsumer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        if(this.configurationService == null) {
            throw new IllegalArgumentException("configurationService cannot be null!");
        }
    }

    @Override
    public void onException(Throwable throwable) {
        this.eventListener.invoke(throwable);
    }

    @Override
    public boolean isActive() {
        return this.isRunning();
    }

    @Override
    public void setListener(EventListener<?> eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void setEventFactory(EventFactory eventFactory) {
        this.flowEventFactory = eventFactory;
    }

    @Override
    public EventFactory getEventFactory() {
        return this.flowEventFactory;
    }

    @Override
    public void start() {
        if(this.managedRelatedEventIdentifierService == null) {
            throw new RuntimeException("The managedRelatedEventIdentifierService is null and there is no default" +
                " implementation provided for the reactive KafkaConsumer. All reactive Kafka Consumers require" +
                " a custom implementation of the ManagedRelatedEventIdentifierService that derives the identifier" +
                " from the value associated with the Kafka ConsumerRecord, in a reliably reproducible and unique manner" +
                " from the associated business data.");
        }
        if(this.messageListener == null) {
            throw new RuntimeException("The messageListener is null. Please set a messageListener on this consumer!");
        }
        this.subscribe();
    }

    @Override
    public boolean isRunning() {
        return this.disposable != null && !this.disposable.isDisposed();
    }

    @Override
    public void stop() {
        if(this.disposable != null && !this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }

    /**
     * Invokes a flow event with the provided value.
     *
     * @param value the value to create and process a flow event with
     * @throws RuntimeException if flowEventFactory is null
     */
    private void invokeFlowEvent(Object value) {
        logger.debug("Received message " + value);

        if(this.flowEventFactory == null) {
            throw new RuntimeException("flowEventFactory cannot be null!");
        }

        FlowEvent<?, ?> flowEvent = flowEventFactory.newEvent(this.managedRelatedEventIdentifierService.getEventIdentifier(value)
            , this.managedRelatedEventIdentifierService.getRelatedEventIdentifier(value), value);
        this.eventListener.invoke(flowEvent);
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String configurationId) {
        this.configurationId = configurationId;
    }

    @Override
    public KafkaConsumerConfiguration getConfiguration() {
        return this.kafkaConsumerConfiguration;
    }

    @Override
    public void setConfiguration(KafkaConsumerConfiguration configuration) {
        this.kafkaConsumerConfiguration = configuration;
    }

    @Override
    public void setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService) {
        this.managedRelatedEventIdentifierService = managedRelatedEventIdentifierService;
    }

    @Override
    public void onResubmission(Object resubmissionEvent) {
        logger.debug("Resubmission message " + resubmissionEvent);
        if(this.resubmissionEventFactory == null) {
                throw new RuntimeException("resubmissionEventFactory cannot be null!");
        }

        FlowEvent<?,?> flowEvent;

        if(this.managedRelatedEventIdentifierService != null) {
            flowEvent = flowEventFactory.newEvent(managedRelatedEventIdentifierService.getEventIdentifier(resubmissionEvent)
                , managedRelatedEventIdentifierService.getRelatedEventIdentifier(resubmissionEvent), resubmissionEvent);
        }
        else {
            flowEvent = flowEventFactory.newEvent(String.valueOf(resubmissionEvent.hashCode())
                , String.valueOf(resubmissionEvent.hashCode()), resubmissionEvent);
        }

        Resubmission resubmission = this.resubmissionEventFactory.newResubmissionEvent(flowEvent);
        this.eventListener.invoke(resubmission);
    }

    @Override
    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    /**
     * Subscribe to a Kafka topic and process received messages.
     * Initializes a KafkaReceiver using the configured Kafka consumer properties,
     * assigns partitions to seek the specified offset, subscribes to the configured topic,
     * and subscribes to incoming Kafka records to process with the provided message processor.
     * Any errors that occur during message processing are logged as errors.
     * ClassNotFoundException is caught and rethrown as a RuntimeException.
     */
    private void subscribe() {
        try {
            ReceiverOptions<Object, Object> receiverOptions = ReceiverOptions.create(this.kafkaConsumerConfiguration.getConsumerProps());
            List<TopicPartition> topicPartitions = new ArrayList<>();
            for(String partition: this.kafkaConsumerConfiguration.getPartitions()) {
                if(!this.kafkaConsumerConfiguration.getPartitionOffsets()
                    .containsKey(String.valueOf(partition))) {
                    this.kafkaConsumerConfiguration.getPartitionOffsets()
                        .put(String.valueOf(partition), "0");
                }

                TopicPartition topicPartition = new TopicPartition(this.kafkaConsumerConfiguration.getTopicName()
                    , Integer.valueOf(partition));
                topicPartitions.add(topicPartition);
            }
            ReceiverOptions<Object, Object> options = receiverOptions
                .assignment(topicPartitions)
                .addAssignListener(partitions -> partitions.forEach(p -> {
                    if(!this.kafkaConsumerConfiguration.getPartitionOffsets()
                        .containsKey(String.valueOf(p.topicPartition().partition()))) {
                        this.kafkaConsumerConfiguration.getPartitionOffsets()
                            .put(String.valueOf(p.topicPartition().partition()), "0");
                    }

                    String offsetString = this.kafkaConsumerConfiguration.getPartitionOffsets()
                        .get(String.valueOf(p.topicPartition().partition()));
                    p.seek(Long.valueOf(offsetString));
                }));

            Flux<ReceiverRecord<Object, Object>> kafkaFlux = KafkaReceiver
                .create(options)
                .receive();

            this.disposable = kafkaFlux.subscribe(record -> {
                    this.messageListener.onMessage(record);
                }, e -> {
                logger.error("An error has occurred processing Kafka message!", e);
                this.onException(e);
            });
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the message listener to be invoked when a message is received.
     *
     * @param messageListener the MessageListener to be set
     */
    public void setMessageListener(MessageListener<ReceiverRecord> messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void onMessage(ReceiverRecord record) {
        ReceiverOffset offset = record.receiverOffset();
        try {
            this.invokeFlowEvent(record.value());
            this.kafkaConsumerConfiguration.getPartitionOffsets()
                .put(String.valueOf(record.partition()), String.valueOf(offset.offset() + 1));
            this.configurationService.update(this);
            offset.acknowledge();
        }
        catch (ForceTransactionRollbackException e) {
            if(e.getMessage().equals(ExcludeEventAction.EXCLUDE_EVENT)) {
                // We process the same record again so that the blacklisted record can be parked.
                this.invokeFlowEvent(record.value());
                this.kafkaConsumerConfiguration.getPartitionOffsets()
                    .put(String.valueOf(record.partition()), String.valueOf(offset.offset() + 1));
                this.configurationService.update(this);
                offset.acknowledge();

                // Dispose of of the reactive subscription and subscribe again.
                this.disposable.dispose();
                this.subscribe();
            }
            else {
                throw e;
            }
        }
    }
}

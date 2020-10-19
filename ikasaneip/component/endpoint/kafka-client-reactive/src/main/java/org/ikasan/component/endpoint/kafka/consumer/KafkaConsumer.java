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
package org.ikasan.component.endpoint.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.ikasan.exceptionResolver.action.ExcludeEventAction;
import org.ikasan.exceptionResolver.action.RetryAction;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.component.endpoint.MultiThreadedCapable;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.*;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Implementation of a Kafka client consumer.
 *
 * @author Ikasan Development Team
 */
public class KafkaConsumer<KEY, VALUE>
    implements Consumer<EventListener<?>,EventFactory>,
        ManagedIdentifierService<ManagedRelatedEventIdentifierService>, EndpointListener<ConsumerRecord<KEY, VALUE>,Throwable>,
        ConfiguredResource<KafkaConsumerConfiguration>, ResubmissionService<ConsumerRecord<KEY, VALUE>>, //Converter<ConsumerRecord<KEY, VALUE>,Object>,
        MultiThreadedCapable, IsExclusionServiceAware, MessageProcessor<ConsumerRecord<KEY, VALUE>>
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private String configurationId;

    private KafkaConsumerConfiguration kafkaConsumerConfiguration;


    private ConsumerFactory<KEY, VALUE> consumerFactory;

    private Disposable disposable;

    /** consumer event factory */
    protected EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** resubmission event factory */
    protected ResubmissionEventFactory<Resubmission<?>> resubmissionEventFactory;

    /** consumer event listener */
    protected EventListener eventListener;

    protected  MessageProcessor<ConsumerRecord<KEY, VALUE>> messageProcessor;

    /**
     * Invoke the eventListener with the given flowEvent.
     * @param flowEvent
     */
    protected void invoke(FlowEvent flowEvent)
    {
        if(this.eventListener == null)
        {
            throw new RuntimeException("No active eventListeners registered for flowEvent!");
        }

        this.eventListener.invoke(flowEvent);
    }

    /**
     * Invoke the eventListener with the given resubmission.
     * @param resubmission
     */
    protected void invoke(Resubmission resubmission)
    {
        if(this.eventListener == null)
        {
            throw new RuntimeException("No active eventListeners registered for resubmission event!");
        }

        this.eventListener.invoke(resubmission);
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

    @Override
    public void onMessage(ConsumerRecord<KEY, VALUE> consumerRecord) {
        logger.info("Received message " + consumerRecord.value());

//        try {
        FlowEvent<?, ?> flowEvent = flowEventFactory.newEvent(consumerRecord.value(), "", consumerRecord.value());
        invoke(flowEvent);
//        }
//        catch (Exception e) {
//            if(this.eventListener != null) {
//                this.eventListener.invoke(e);
//            }
//
//        }
    }

    @Override
    public void onException(Throwable throwable) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

//    @Override
//    public Object convert(ConsumerRecord<KEY, VALUE> payload) throws TransformationException {
//        return payload.value();
//    }

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

    }

    @Override
    public void onResubmission(ConsumerRecord consumerRecord) {
        logger.info("Resubmission message " + consumerRecord.value());

        Resubmission flowEvent = resubmissionEventFactory.newResubmissionEvent(consumerRecord.value());
        invoke(flowEvent);
    }

    @Override
    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory) {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    @Override
    public void setExclusionService(ExclusionService exclusionService) {

    }

    private void subscribe() {
        try {
            ReceiverOptions<KEY, VALUE> receiverOptions = ReceiverOptions.create(this.kafkaConsumerConfiguration.getConsumerProps());
            ReceiverOptions<KEY, VALUE> options = receiverOptions
                .addAssignListener(partitions -> partitions.forEach(p -> p.seek(this.kafkaConsumerConfiguration.getOffset())))
                .subscription(Collections.singleton(this.kafkaConsumerConfiguration.getTopicName()));

            Flux<ReceiverRecord<KEY, VALUE>> kafkaFlux = KafkaReceiver
                .create(options)
                .receive();

            this.disposable = kafkaFlux.subscribe(record -> {
                    ReceiverOffset offset = record.receiverOffset();
                    this.messageProcessor.process(record);
                    this.kafkaConsumerConfiguration.setOffset(offset.offset() + 1);
                    offset.acknowledge();
                }, e -> {
//                    if(e.getMessage().startsWith("Retry")) {
//                        this.kafkaConsumerConfiguration.setOffset(this.kafkaConsumerConfiguration.getOffset() - 1);
//                    }
                });
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMessageProcessor(MessageProcessor<ConsumerRecord<KEY, VALUE>> messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void process(ConsumerRecord<KEY, VALUE> record) {
        try {
            this.onMessage(record);
        }
        catch (ForceTransactionRollbackException e) {
            if(e.getMessage().equals(ExcludeEventAction.EXCLUDE_EVENT)) {
                // We process the same record again so that the blacklisted record can be parked.
                this.onMessage(record);

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

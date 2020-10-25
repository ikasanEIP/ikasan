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
package org.ikasan.component.endpoint.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.reactivestreams.Publisher;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedIdentifierService;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.TransactionManager;

import java.util.Date;

/**
 * Implementation of a producer based on the JMS specification.
 *
 * @author Ikasan Development Team
 */
public class KafkaProducer<VALUE>
        implements Producer<VALUE>, ManagedIdentifierService<ManagedEventIdentifierService>,
        ManagedResource, ConfiguredResource<KafkaProducerConfiguration>
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private KafkaSender<String, VALUE> sender;

    private KafkaProducerConfiguration configuration;
    private String configurationId;

    @Override
    public void invoke(VALUE payload) throws EndpointException {
        sender.send(Flux.just(payload)
            .map(t -> SenderRecord.create(new ProducerRecord<>(this.configuration.getTopicName(), "key", t), t)))
            .doOnError(e -> {
                logger.error("Send failed", e);
                throw new EndpointException(e);
            })
            .subscribe(r -> {
                RecordMetadata metadata = r.recordMetadata();
                System.out.printf("Message sent successfully: \n" +
                    r.correlationMetadata() + "\n" +
                    metadata.topic() + "\n" +
                    metadata.partition() + "\n" +
                    metadata.offset() + "\n" +
                    new Date(metadata.timestamp()));
            });
    }

    @Override
    public String getConfiguredResourceId() {
        return configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }

    @Override
    public KafkaProducerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(KafkaProducerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setManagedIdentifierService(ManagedEventIdentifierService managedEventIdentifierService) {

    }

    @Override
    public void startManagedResource() {
        try {
            SenderOptions<String, VALUE> senderOptions = SenderOptions.create(this.configuration.getProducerProps());
            sender = KafkaSender.create(senderOptions);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopManagedResource() {
        sender.close();
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

    }

    @Override
    public boolean isCriticalOnStartup() {
        return false;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {

    }
}

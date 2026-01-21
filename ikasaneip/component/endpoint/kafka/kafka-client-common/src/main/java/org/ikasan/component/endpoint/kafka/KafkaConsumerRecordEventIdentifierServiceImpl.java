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
package org.ikasan.component.endpoint.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.ikasan.spec.event.ManagedEventIdentifierException;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

/**
 * Implementation of the ManagedEventIdentifierService specifically for Kafka.
 * 
 * @author Ikasan Development Team
 *
 */
public class KafkaConsumerRecordEventIdentifierServiceImpl implements ManagedRelatedEventIdentifierService<String, ConsumerRecord>
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumerRecordEventIdentifierServiceImpl.class);

    @Override
    public void setRelatedEventIdentifier(String relatedIdentifier, ConsumerRecord consumerRecord) throws ManagedEventIdentifierException {
        // nothing to do when related identifier is null
        if(relatedIdentifier == null) return;

        consumerRecord.headers().add(RELATED_EVENT_LIFE_ID, relatedIdentifier.getBytes());
    }

    @Override
    public String getRelatedEventIdentifier(ConsumerRecord consumerRecord) throws ManagedEventIdentifierException {
        Optional<Header> relatedIdentifier = Arrays.stream(consumerRecord.headers().toArray())
            .filter(header -> header.key().equals(RELATED_EVENT_LIFE_ID))
            .findFirst();

        if(relatedIdentifier.isPresent()) {
            return new String(relatedIdentifier.get().value());
        }
        else {
            // return null if not related identifier
            return null;
        }
    }

    @Override
    public void setEventIdentifier(String eventLifeIdentifier, ConsumerRecord consumerRecord) throws ManagedEventIdentifierException {
        consumerRecord.headers().add(EVENT_LIFE_ID, eventLifeIdentifier.getBytes());
    }

    @Override
    public String getEventIdentifier(ConsumerRecord consumerRecord) throws ManagedEventIdentifierException {
        Optional<Header> relatedIdentifier = Arrays.stream(consumerRecord.headers().toArray())
            .filter(header -> header.key().equals(EVENT_LIFE_ID))
            .findFirst();

        if(relatedIdentifier.isPresent()) {
            return new String(relatedIdentifier.get().value());
        }
        else {
            return consumerRecord.topic() + "_" + consumerRecord.partition() + "_" + consumerRecord.offset();
        }
    }
}

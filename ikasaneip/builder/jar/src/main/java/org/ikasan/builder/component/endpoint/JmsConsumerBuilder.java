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
package org.ikasan.builder.component.endpoint;


import org.ikasan.builder.component.Builder;
import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;

import javax.jms.ConnectionFactory;
import java.util.Map;

/**
 * Contract for a default Spring based jmsConsumerBuilder.
 *
 * @author Ikasan Development Team.
 */
public interface JmsConsumerBuilder extends Builder<Consumer> {

    JmsConsumerBuilder setConfiguredResourceId(String configuredResourceId);

    JmsConsumerBuilder setConfiguration(SpringMessageConsumerConfiguration springMessageConsumerConfiguration);

    JmsConsumerBuilder setMessageProvider(MessageProvider messageProvider);

    JmsConsumerBuilder setManagedIdentifierService(ManagedRelatedEventIdentifierService managedRelatedEventIdentifierService);

    JmsConsumerBuilder setEventFactory(EventFactory eventFactory);

    JmsConsumerBuilder setDestinationJndiName(String destinationJndiName);

    JmsConsumerBuilder setDestinationJndiProperties(Map<String, String> destinationJndiProperties);

    JmsConsumerBuilder setDurable(Boolean durable);

    JmsConsumerBuilder setDurableSubscriptionName(String durableSubscriptionName);

    JmsConsumerBuilder setConnectionFactoryJndiProperties(Map<String, String> connectionFactoryJndiProperties);

    JmsConsumerBuilder setConnectionFactory(ConnectionFactory connectionFactory);

    JmsConsumerBuilder setConnectionFactoryJndiPropertyProviderUrl(String providerUrl);

    JmsConsumerBuilder setConnectionFactoryJndiPropertyFactoryInitial(String initialFactory);

    JmsConsumerBuilder setConnectionFactoryJndiPropertyUrlPkgPrefixes(String urlPkgPrefixes);

    JmsConsumerBuilder setConnectionFactoryJndiPropertySecurityCredentials(String securityCredentials);

    JmsConsumerBuilder setConnectionFactoryJndiPropertySecurityPrincipal(String securityPrincipal);

    JmsConsumerBuilder setDestinationJndiPropertyProviderUrl(String providerUrl);

    JmsConsumerBuilder setDestinationJndiPropertyFactoryInitial(String initialFactory);

    JmsConsumerBuilder setDestinationJndiPropertyUrlPkgPrefixes(String urlPkgPrefixes);

    JmsConsumerBuilder setDestinationJndiPropertySecurityCredentials(String securityCredentials);

    JmsConsumerBuilder setDestinationJndiPropertySecurityPrincipal(String securityPrincipal);

    JmsConsumerBuilder setConnectionFactoryUsername(String username);

    JmsConsumerBuilder setConnectionFactoryPassword(String password);

    JmsConsumerBuilder setConnectionFactoryName(String connectionFactoryName);

    JmsConsumerBuilder setAutoContentConversion(boolean autoContentConversion);

    JmsConsumerBuilder setAutoSplitBatch(boolean autoSplitBatch);

    JmsConsumerBuilder setBatchMode(boolean batchMode);

    JmsConsumerBuilder setBatchSize(int batchSize);

    JmsConsumerBuilder setCacheLevel(int cacheLevel);

    JmsConsumerBuilder setConcurrentConsumers(int concurrentConsumers);

    JmsConsumerBuilder setMaxConcurrentConsumers(int maxConcurrentConsumers);

    JmsConsumerBuilder setPubSubDomain(Boolean pubSubDomain);

    JmsConsumerBuilder setSessionTransacted(Boolean sessionTransacted);

    JmsConsumerBuilder setSessionAcknowledgeMode(Integer sessionAcknowledgeMode);
}


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
import org.ikasan.component.endpoint.jms.producer.PostProcessor;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import java.util.Map;

/**
 * Contract for a default Spring based jmsProducerBuilder.
 *
 * @author Ikasan Development Team.
 */
public interface JmsProducerBuilder extends Builder<Producer> {

    JmsProducerBuilder setConfiguredResourceId(String configuredResourceId);

    JmsProducerBuilder setConfiguration(SpringMessageProducerConfiguration springMessageProducerConfiguration);

    JmsProducerBuilder setDestinationJndiName(String destinationJndiName);

    JmsProducerBuilder setDestinationJndiPropertyUrlPkgPrefixes(String initialFactory);

    JmsProducerBuilder setDestinationJndiPropertySecurityCredentials(String initialFactory);

    JmsProducerBuilder setDestinationJndiPropertySecurityPrincipal(String securityPrincipal);

    JmsProducerBuilder setDestinationJndiProperties(Map<String, String> destinationJndiProperties);

    JmsProducerBuilder setConnectionFactory(ConnectionFactory connectionFactory);

    JmsProducerBuilder setConnectionFactoryJndiProperties(Map<String, String> connectionFactoryJndiProperties);

    JmsProducerBuilder setConnectionFactoryJndiPropertyProviderUrl(String providerUrl);

    JmsProducerBuilder setConnectionFactoryJndiPropertyFactoryInitial(String initialFactory);

    JmsProducerBuilder setConnectionFactoryJndiPropertyUrlPkgPrefixes(String urlPackage);

    JmsProducerBuilder setConnectionFactoryJndiPropertySecurityCredentials(String securityCredentials);

    JmsProducerBuilder setConnectionFactoryJndiPropertySecurityPrincipal(String securityPrincipal);

    JmsProducerBuilder setDestinationJndiPropertyProviderUrl(String providerUrl);

    JmsProducerBuilder setDestinationJndiPropertyFactoryInitial(String initialFactory);

    JmsProducerBuilder setConnectionFactoryUsername(String username);

    JmsProducerBuilder setConnectionFactoryPassword(String password);

    JmsProducerBuilder setConnectionFactoryName(String connectionFactoryName);

    JmsProducerBuilder setPubSubDomain(Boolean pubSubDomain);

    JmsProducerBuilder setDeliveryPersistent(Boolean deliveryPersistent);

    JmsProducerBuilder setDeliveryMode(Integer deliveryMode);

    JmsProducerBuilder setSessionTransacted(Boolean sessionTransacted);

    JmsProducerBuilder setExplicitQosEnabled(Boolean explicitQosEnabled);

    JmsProducerBuilder setMessageIdEnabled(Boolean messageIdEnabled);

    JmsProducerBuilder setMessageTimestampEnabled(Boolean messageTimestampEnabled);

    JmsProducerBuilder setPriority(Integer priority);

    JmsProducerBuilder setPubSubNoLocal(Boolean pubSubNoLocal);

    JmsProducerBuilder setReceiveTimeout(Long receiveTimeout);

    JmsProducerBuilder setSessionAcknowledgeMode(Integer sessionAcknowledgeMode);

    JmsProducerBuilder setSessionAcknowledgeModeName(String sessionAcknowledgeModeName);

    JmsProducerBuilder setTimeToLive(Long timeToLive);

    JmsProducerBuilder setPostProcessor(PostProcessor<?,?> postProcessor);

    JmsProducerBuilder setMessageConverter(MessageConverter messageConverter);
}


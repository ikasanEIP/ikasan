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

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.consumer.EventGeneratingConsumer;
import org.ikasan.component.endpoint.consumer.api.endpoint.TechEndpointRunnableThread;
import org.ikasan.component.endpoint.consumer.api.spec.EndpointEventProvider;
import org.ikasan.component.endpoint.consumer.api.spec.Endpoint;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.ExceptionListener;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.event.MessageListener;

import java.util.concurrent.Executors;

/**
 * Ikasan provided event generating consumer builder implementation.
 *
 * @author Ikasan Development Team
 */
public class EventGeneratingConsumerBuilderImpl implements EventGeneratingConsumerBuilder
{
    // aop point cut proxy
    private AopProxyProvider aopProxyProvider;

    private EndpointEventProvider endpointEventProvider = EndpointEventProvider.defaultInstance();

    // API tech endpoint
    private Endpoint techEndpoint = new TechEndpointRunnableThread();

    // managed event identifier override
    private ManagedEventIdentifierService managedEventIdentifierService;

    /**
     * Constructor
     * @param aopProxyProvider
     */
    public EventGeneratingConsumerBuilderImpl(AopProxyProvider aopProxyProvider)
    {
        this.aopProxyProvider = aopProxyProvider;
        if(aopProxyProvider == null)
        {
            throw new IllegalArgumentException("aopProxyProvider cannot be 'null'");
        }
    }

    /**
     * Build component.
     * @return
     */
    public Consumer build()
    {
        EventGeneratingConsumer eventConsumer = new EventGeneratingConsumer(Executors.newSingleThreadExecutor(), techEndpoint);
        MessageListener messageListener = this.aopProxyProvider.applyPointcut("eventConsumer", eventConsumer);
        techEndpoint.setMessageListener(messageListener);

        if(messageListener instanceof ExceptionListener listener)
        {
            techEndpoint.setExceptionListener( listener );
        }

        techEndpoint.setEventProvider(this.endpointEventProvider);
        if(this.managedEventIdentifierService != null)
        {
            eventConsumer.setManagedIdentifierService(managedEventIdentifierService);
        }

        return eventConsumer;
    }

    @Override
    public EventGeneratingConsumerBuilder setEndpointEventProvider(EndpointEventProvider endpointEventProvider)
    {
        this.endpointEventProvider = endpointEventProvider;
        return this;
    }

    @Override
    public EventGeneratingConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.managedEventIdentifierService = managedEventIdentifierService;
        return this;
    }
}


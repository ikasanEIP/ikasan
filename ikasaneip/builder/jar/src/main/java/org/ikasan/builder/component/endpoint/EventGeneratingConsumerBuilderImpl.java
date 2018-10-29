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
import org.ikasan.component.endpoint.consumer.*;
import org.ikasan.component.endpoint.consumer.event.TechEndpointExecutableEventFactoryImpl;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.ExceptionListener;
import org.ikasan.spec.event.MessageListener;

import java.util.ArrayList;
import java.util.List;
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

    private List<TechEndpointEventProvider> techEndpointEventProviders = new ArrayList<TechEndpointEventProvider>();

    // API tech endpoint
    private TechEndpoint techEndpoint;

    String configuredResourceId;
    EventGeneratingConsumerConfiguration eventGeneratingConsumerConfiguration;

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
        // default tech endpoint if not provided
        if(techEndpoint == null)
        {
            this.techEndpoint = new TechEndpointRunnableThread();
        }

        EventGeneratingConsumer eventGeneratingConsumer = new EventGeneratingConsumer(Executors.newSingleThreadExecutor(), techEndpoint);
        MessageListener messageListener = this.aopProxyProvider.applyPointcut("eventGeneratingConsumer", eventGeneratingConsumer);
        techEndpoint.setMessageListener(messageListener);

        if(this.techEndpointEventProviders.isEmpty())
        {
            this.techEndpointEventProviders.add( getDefaultTechEndpointProvider() );
        }

        TechEndpointEventFactory eventFactory;
        if(messageListener instanceof ExceptionListener)
        {
            eventFactory = new TechEndpointExecutableEventFactoryImpl(messageListener, (ExceptionListener)messageListener);
            techEndpoint.setExceptionListener( (ExceptionListener)messageListener );
        }
        else
        {
            eventFactory = new TechEndpointExecutableEventFactoryImpl(messageListener, null);
        }

        //
        List<TechEndpointEventProvider> executableProviders =
                new ArrayList<TechEndpointEventProvider>( this.techEndpointEventProviders.size() );
        for(TechEndpointEventProvider techEndpointEventProvider:this.techEndpointEventProviders)
        {
            executableProviders.add( new DefaultTechEndpointEventProviderImpl(eventFactory, techEndpointEventProvider) );
        }

        techEndpoint.setTechEndpointEventProvider( new TechEndpointMultipleEventProvidersImpl(executableProviders) );

        ((ConfiguredResource)eventGeneratingConsumer).setConfiguration(eventGeneratingConsumerConfiguration);
        ((ConfiguredResource)eventGeneratingConsumer).setConfiguredResourceId(configuredResourceId);

        return eventGeneratingConsumer;
    }

    @Override
    public EventGeneratingConsumerBuilder setTechEndpoint(TechEndpoint techEndpoint) {
        this.techEndpoint = techEndpoint;
        return this;
    }

    @Override
    public EventGeneratingConsumerBuilder withTechEventProvider(TechEndpointEventProvider techEndpointEventProvider)
    {
        this.techEndpointEventProviders.add(techEndpointEventProvider);
        return this;
    }

    @Override
    public EventGeneratingConsumerBuilder repeatProvider(int repeatTimes)
    {
        if(this.techEndpointEventProviders.isEmpty())
        {
            this.techEndpointEventProviders.add( getDefaultTechEndpointProvider() );
        }

        TechEndpointEventProvider techEndpointEventProvider = this.techEndpointEventProviders.remove( this.techEndpointEventProviders.size()-1 );

        for(int count=0; count<repeatTimes; count++)
        {
            this.techEndpointEventProviders.add(techEndpointEventProvider);
        }

        return this;
    }

    protected TechEndpointEventProvider getDefaultTechEndpointProvider()
    {
        return TechEndpointEventProvider.with()
                .messageEvent("Test Message")
                .repeatForever()
                .interval(1000)
                .build();
    }
}


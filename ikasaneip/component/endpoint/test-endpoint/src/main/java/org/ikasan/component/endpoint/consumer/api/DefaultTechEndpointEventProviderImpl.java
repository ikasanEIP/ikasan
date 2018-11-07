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
package org.ikasan.component.endpoint.consumer.api;

import org.ikasan.component.endpoint.consumer.api.event.APIEvent;
import org.ikasan.component.endpoint.consumer.api.event.APIMessageEvent;
import org.ikasan.component.endpoint.consumer.api.event.APIRepeatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a TechEndpointEventProvider based on APIEvent construct.
 * 
 * @author Ikasan Development Team
 */
public class DefaultTechEndpointEventProviderImpl<T extends APIEvent> implements TechEndpointProviderBuilder, TechEndpointEventProvider<T>
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(DefaultTechEndpointEventProviderImpl.class);

    /** Event factory to create tech events for this provider */
    TechEndpointEventFactory<T> techEndpointEventFactory;

    /** list of tech endpoint events created */
    List<T> events = new ArrayList();

    /** index on the above list */
    int index;

    /** inflight event */
    T apiEvent;

    /** did we rollback */
    boolean rollback;

    /**
     * Constructor
     * @param techEndpointEventFactory
     */
    public DefaultTechEndpointEventProviderImpl(TechEndpointEventFactory<T> techEndpointEventFactory)
    {
        this.techEndpointEventFactory = techEndpointEventFactory;
        if(techEndpointEventFactory == null)
        {
            throw new IllegalArgumentException("APIEventFactory cannot be 'null'");
        }
    }

    /**
     * Constructor
     * @param techEndpointEventFactory
     * @param techEndpointEventProvider
     */
    public DefaultTechEndpointEventProviderImpl(TechEndpointEventFactory<T> techEndpointEventFactory, TechEndpointEventProvider<APIEvent> techEndpointEventProvider)
    {
        this.techEndpointEventFactory = techEndpointEventFactory;
        if(techEndpointEventFactory == null)
        {
            throw new IllegalArgumentException("APIEventFactory cannot be 'null'");
        }

        if(techEndpointEventProvider == null)
        {
            throw new IllegalArgumentException("techEndpointEventProvider cannot be 'null'");
        }

        // create new provider instance based on events in the incoming provider
        for(APIEvent apiEvent:techEndpointEventProvider.getEvents())
        {
            T apiExecutableEvent = repackEvent(techEndpointEventFactory, apiEvent);
            if(apiExecutableEvent != null)
            {
                this.events.add(apiExecutableEvent);
            }
        }
    }

    private T repackEvent(TechEndpointEventFactory<T> techEndpointEventFactory, APIEvent apiEvent)
    {
        if(apiEvent == null)
        {
            return null;
        }

        if(apiEvent.isMessageEvent())
        {
            return techEndpointEventFactory.getMessageEvent( ((APIMessageEvent)apiEvent).getLifeIdentifer(), apiEvent.getPayload() );
        }
        else if(apiEvent.isExceptionEvent())
        {
            return techEndpointEventFactory.getExceptionEvent( apiEvent.getPayload() );
        }
        else if(apiEvent.isIntervalEvent())
        {
            return (T)techEndpointEventFactory.getIntervalEvent( apiEvent.getPayload() );
        }
        else if(apiEvent.isRepeatEvent())
        {
            T repackedEvent = repackEvent(techEndpointEventFactory, ((APIRepeatEvent)apiEvent).getEvent());
            return (T)techEndpointEventFactory.getRepeatEvent(repackedEvent, ((APIRepeatEvent)apiEvent).getRepeat());
        }

        logger.warn("Ignoring unsupported apiEvent type " + apiEvent.getClass().getName());
        return null;
    }

    /**
     * Clone this event provider.
     * @return
     */
    public TechEndpointEventProvider clone()
    {
        return new DefaultTechEndpointEventProviderImpl(techEndpointEventFactory, this);
    }

    public <M> TechEndpointProviderBuilder messageEvent(M message)
    {
        this.events.add( techEndpointEventFactory.getMessageEvent(message) );
        return this;
    }

    public <I,M> TechEndpointProviderBuilder messageEvent(I lifeIdentifier, M message)
    {
        this.events.add( techEndpointEventFactory.getMessageEvent(lifeIdentifier, message) );
        return this;
    }

    public <T> TechEndpointProviderBuilder exceptionEvent(T exception)
    {
        this.events.add( techEndpointEventFactory.getExceptionEvent(exception) );
        return this;
    }

    public TechEndpointProviderBuilder interval(long interval)
    {
        this.events.add( (T)techEndpointEventFactory.getIntervalEvent(interval) );
        return this;
    }

    public TechEndpointProviderBuilder repeat(int repeatTimes)
    {
        T apiEvent = this.events.remove(this.events.size() -1);
        this.events.add( (T)techEndpointEventFactory.getRepeatEvent(apiEvent, repeatTimes) );
        return this;
    }

    @Override
    public TechEndpointProviderBuilder repeatForever()
    {
        return this.repeat(APIRepeatEvent.INFINITE);
    }

    @Override
    public T consumeEvent()
    {
        try
        {
            if(rollback)
            {
                rollback = false;
                return (T)apiEvent;
            }

            while( (apiEvent = this.events.get(index++)).isRepeatEvent())
            {
                apiEvent = apiEvent.getPayload();
                if(apiEvent != null)
                {
                    index--;
                    return (T)apiEvent;
                }
            }

            return (T)apiEvent;
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    @Override
    public List<T> getEvents()
    {
        return this.events;
    }

    @Override
    public void rollback()
    {
        if(apiEvent.isMessageEvent())
        {
            rollback = true;
        }
    }

    @Override
    public TechEndpointEventProvider<T> build()
    {
        return this;
    }
}

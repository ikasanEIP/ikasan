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
package org.ikasan.builder.sample;

import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventListener;

/**
 * Sample component for test only.
 * @author Ikasan Development Team.
 */
public class SampleConsumer implements
        Consumer<EventListener<Integer>, FlowEventFactory> // Ikasan Contract
{
    /** Ikasan Event Listener registered with this consumer to be called back with the instantiated flowEvent */
    EventListener eventListener;

    /** Ikasan event factory for instantiating the Ikasan flow event based on the tech endpoint payload */
    FlowEventFactory flowEventFactory;

    volatile boolean isRunning = false;

    /**
     * Set the Ikasan event listener. This is the consumer's handle to pass the event back up to the ikasan flow.
     * @param eventListener
     */
    @Override
    public void setListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    /**
     * Set the event factory. Factory used to instantiate ikasan flow events based on incoming payload from the tech endpoint
     * @param flowEventFactory
     */
    @Override
    public void setEventFactory(FlowEventFactory flowEventFactory)
    {
        this.flowEventFactory = flowEventFactory;
    }

    @Override
    public FlowEventFactory getEventFactory()
    {
        return this.flowEventFactory;
    }

    /**
     * Tell the consumer to start the tech endpoint publication
     */
    @Override
    public void start()
    {
        isRunning = true;
    }

    /**
     * Is the tech endpoint currently active
     * @return
     */
    @Override
    public boolean isRunning()
    {
        return isRunning;
    }

    /**
     * Tell the consumer to stop the tech endpoint publication
     */
    @Override
    public void stop()
    {
        isRunning = false;
    }

    /**
     * Callback from the tech endpoint with the payload
     * @param message
     */
    public void onMessage(String message)
    {
        this.eventListener.invoke( this.flowEventFactory.newEvent(message, message) );
    }

}

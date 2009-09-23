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
package org.ikasan.framework.component.sequencing;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.service.EventAggregator;

/**
 * Sequencer implementation which aggregates incoming events into a single event.
 * The associated aggregation may return one event, or 'null' if
 * the aggregation criteria has not been met.
 * 
 * @author Ikasan Development Team
 */
public class EventAggregatingSequencer implements Sequencer
{
    /** Implementation of an aggregator */
    protected EventAggregator aggregator;

    /**
     * Constructor
     * 
     * @param aggregator The event aggregator to use
     */
    public EventAggregatingSequencer(EventAggregator aggregator)
    {
        this.aggregator = aggregator;
        if (this.aggregator == null)
        {
            throw new IllegalArgumentException("EventAggregator cannot be 'null'");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.sequencing.Sequencer#onEvent(org.ikasan.framework.component.Event)
     */
    public List<Event> onEvent(Event event) throws SequencerException
    {
        try
        {
            Event aggregatedEvent = aggregator.aggregate(event);
            if(aggregatedEvent != null)
            {
                List<Event> events = new ArrayList<Event>();
                events.add(aggregatedEvent);
                return events;
            }
            
            return null;
        }
        catch (ResourceException e)
        {
            throw new SequencerException(e);
        }
    }
}
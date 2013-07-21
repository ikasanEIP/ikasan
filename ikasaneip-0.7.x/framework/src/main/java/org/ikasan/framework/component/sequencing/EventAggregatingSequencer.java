/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany
 * or see the FSF site: http://www.fsfeurope.org/.
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
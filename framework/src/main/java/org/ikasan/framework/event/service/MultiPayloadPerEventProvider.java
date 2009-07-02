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
package org.ikasan.framework.event.service;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.payload.service.PayloadProvider;

/**
 * Implementation of <code>EventProvider</code>.<br>
 * 
 * This implementation returns a single Event containing all sourced payloads.<br>
 * The purpose of this is to keep 'related' payloads within the logical unit of a single Event.<br>
 * If no payloads are present then the Event is not created and 'null' is returned.
 * 
 * @author Ikasan Development Team
 */
public class MultiPayloadPerEventProvider implements EventProvider
{
    /** Payload provider */
    private PayloadProvider payloadProvider;

    /** Module name */
    private String moduleName;

    /** Component name that created the event */
    private String componentName;

    /**
     * Constructor
     * 
     * @param payloadProvider The payload provider
     * @param moduleName The name of the module
     * @param componentName The name of the component
     */
    public MultiPayloadPerEventProvider(final PayloadProvider payloadProvider, final String moduleName,
            final String componentName)
    {
        this.payloadProvider = payloadProvider;
        if (this.payloadProvider == null)
        {
            throw new IllegalArgumentException("'payloadProvider' cannot be 'null'.");
        }
        this.moduleName = moduleName;
        this.componentName = componentName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.event.service.EventProvider#getEvents()
     */
    public List<Event> getEvents() throws ResourceException
    {
        List<Payload> payloads = this.payloadProvider.getNextRelatedPayloads();
        if (payloads == null || payloads.isEmpty())
        {
            return null;
        }
 
        Event event = new Event(this.moduleName, this.componentName, hashPayloadIds(payloads), payloads);
        List<Event> events = new ArrayList<Event>();
        events.add(event);
        return events;
    }

	private String hashPayloadIds(List<Payload> payloads) {
		StringBuffer aggregatedId = new StringBuffer();
		for (Payload payload : payloads){
			aggregatedId.append(payload.getId());
		}
		return ""+aggregatedId.toString().hashCode();
	}
}

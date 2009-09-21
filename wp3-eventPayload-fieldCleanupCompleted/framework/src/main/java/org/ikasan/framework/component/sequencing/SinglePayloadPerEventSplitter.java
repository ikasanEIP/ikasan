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

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

/**
 * Sequencer implementation which splits an incoming event's payloads
 * into multiple outgoing events each containing a single payload.
 * 
 * @author Ikasan Development Team
 */
public class SinglePayloadPerEventSplitter implements Sequencer
{
    /* (non-Javadoc)
     * @see org.ikasan.framework.component.sequencing.Sequencer#onEvent(org.ikasan.framework.component.Event)
     */
    public List<Event> onEvent(Event event, String moduleName, String componentName) throws SequencerException
    {
        // we must always return a list of events
        List<Event> events = new ArrayList<Event>();

        
		// get the payloads in this event
		List<Payload> payloads = event.getPayloads();
		if (payloads.size() == 1) {
			// only one payload so add the incoming event to the outgoing list
			// and return
			events.add(event);
			return events;
		}

		for (int i = 0; i < payloads.size(); i++) {
			Payload payload = payloads.get(i);
			events.add(event.spawnChild(moduleName, componentName, i, payload));

		}
		return events;
    }
}
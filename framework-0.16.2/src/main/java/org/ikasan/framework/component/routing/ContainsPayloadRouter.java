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
package org.ikasan.framework.component.routing;

import java.util.List;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

/**
 * Simple <code>Router</code> that switches between a single "true" result or a single "false" result depending on
 * whether the incoming <code>Event</code> contains 1 or more <code>Payload</code>s
 * 
 * @deprecated No longer used as Initiators no longer deliver empty events.
 * 
 * @author Ikasan Development Team
 */
@Deprecated
public class ContainsPayloadRouter extends SingleResultRouter
{
    /**
     * Simply assesses whether or not the Event contains a Payload
     * 
     * @param event The even to assess
     * @return "true" or "false" depending on the event contents
     */
    @Override
    protected String evaluate(Event event)
    {
        boolean resultBoolean = false;
        List<Payload> payloads = event.getPayloads();
        if (payloads != null)
        {
            resultBoolean = (payloads.size() > 0);
        }
        return new Boolean(resultBoolean).toString();
    }
}

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

import javax.resource.ResourceException;
import org.ikasan.framework.component.Event;

/**
 * Interface for components capable of aggregating <code>Event</code>s.
 * 
 * @author Ikasan Development Team
 */
public interface EventAggregator
{
    /**
     * Aggregate the <code>Event</code>
     * 
     * @param event to be aggregated
     * @return Event - aggregated event or 'null' if aggregation criteria is not met
     * @throws ResourceException 
     */
    public Event aggregate(Event event) 
        throws ResourceException;
}

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
package org.ikasan.framework.event.wiretap.service;

import java.util.Date;
import java.util.Set;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;

/**
 * This Service allows <code>Event</code>s to be dumped out at runtime for later retrieval and inspection
 * 
 * @author Ikasan Development Team
 */
public interface WiretapService
{
    /**
     * Allows previously stored Events to be searched for.
     * 
     * By default the search has no restrictions. Specifying any of the arguments as anything other than null will cause
     * the search to be restricted by an exact match on that field
     * 
     * @param moduleNames - Set of names of modules to include in search - must contain at least one moduleName
     * @param componentName - The name of the component
     * @param eventId - The Event Id
     * @param payloadId - The Payload Id
     * @param fromDate - Include only events after fromDate
     * @param untilDate - Include only events before untilDate
     * @param payloadContent - The Payload content
     * @param pageSize - how many results to return in the result
     * @param pageNo - page index into the greater result set
     * 
     * 
     * @throws IllegalArgumentException - if moduleNames is null or empty
     * @return List of <code>WiretapEventHeader</code> representing the result of the search
     *
     */
    public PagedWiretapSearchResult findWiretapEvents(Set<String> moduleNames, String componentName,
            String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent, int pageSize, int pageNo);

    /**
     * Retrieve a specific <code>WiretapEvent</code> by Id
     * 
     * @param wiretapEventId - The id of the wiretap event to retrieve
     * @return <code>WiretapEvent</code>
     */
    public WiretapEvent getWiretapEvent(Long wiretapEventId);

    /**
     * dumps a snapshot of an <code>Event</code> at runtime in the form of one or more <code>WiretapEvent</code>s - one
     * for every <code>Payload</code> contained
     * 
     * @param event - Event to snapshot
     * @param componentName - name of the component
     * @param moduleName - name of the <code>Module</code>
     * @param flowName - name of the <code>Flow</code>
     * @param timeToLive - no of minutes from now until <code>WiretapEvents</code> should expire
     */
    public void tapEvent(Event event, String componentName, String moduleName, String flowName, Long timeToLive);

    /**
     * Causes all <code>WiretapEvent</code>s that are past their expiry to be deleted
     */
    public void housekeep();
}

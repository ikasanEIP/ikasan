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
package org.ikasan.framework.event.wiretap.dao;

import java.util.Date;
import java.util.Set;

import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * Interface for all wiretap event data access.
 * 
 * @author Ikasan Development Team
 */
public interface WiretapDao
{

    /**
     * Save a wiretapEvent entry.
     * 
     * @param wiretapEvent - The wiretap event to save
     */
    public void save(WiretapEvent wiretapEvent);

    /**
     * Find the paging for the wiretap search results
     * 
     * @deprecated - Use findWiretapEvents instead
     * 
     * @param moduleNames - The list of module names
     * @param componentName - The component name
     * @param eventId - The event id
     * @param payloadId - The payload id
     * @param fromDate - The from date
     * @param untilDate - The to date
     * @param payloadContent - The payload content
     * @param maxResults - The maximum amount of results
     * @param firstResult - The first result
     * @return A paged wiretap search result
     */
    public PagedWiretapSearchResult findPaging(final Set<String> moduleNames, final String componentName, final String eventId, final String payloadId,
            Date fromDate, Date untilDate, String payloadContent, final int maxResults, final int firstResult);

    /**
     * Perform a paged search for <code>WiretapEvent</code>s
     * 
     * @param pageNo - The page number to retrieve
     * @param pageSize - The size of the page
     * @param orderBy - order by field
     * @param orderAscending - ascending flag
     * @param moduleNames - The list of module names
     * @param componentName - The component name
     * @param eventId - The event id
     * @param payloadId - The payload id
     * @param fromDate - The from date
     * @param untilDate - The to date
     * @param payloadContent - The payload content
     * 
     * @return PagedSearchResult
     */
    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, final Set<String> moduleNames,
            final String componentName, final String eventId, final String payloadId, Date fromDate, Date untilDate, String payloadContent);

    /**
     * Find wiretap entry by identifier
     * 
     * @param id - The id to search on
     * @return WiretapEvent
     */
    public WiretapEvent findById(Long id);

    /**
     * Deletes all WiretapEvents that have surpassed their expiryDate
     */
    public void deleteAllExpired();
}

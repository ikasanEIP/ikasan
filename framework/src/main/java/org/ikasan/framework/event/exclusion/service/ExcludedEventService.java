/*
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
package org.ikasan.framework.event.exclusion.service;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * @author The Ikasan Development Team
 *
 */
public interface ExcludedEventService {

	
	/**
	 * Exclude and Event from a specified flow
	 * 
	 * @param event
	 * @param moduleName
	 * @param flowName
	 */
	public void excludeEvent(Event event, String moduleName, String flowName);

	/**
	 * Returns a paged listing of ExcludedEvent
	 * 
	 * @param pageNo - 0 or greater, index into the list of all possible results
	 * @param pageSize - 0 or greater, no of excludedEvents to return on a page
	 * 
	 * @return PagedSearchResult<ExcludedEvent>
	 */
	public PagedSearchResult<ExcludedEvent> getExcludedEvents(int pageNo, int pageSize);

	/**
	 * Retrieve an ExcludedEvent specified by its Id
	 * 
	 * @param excludedEventId
	 * @return ExcludedEvent
	 */
	public ExcludedEvent getExcludedEvent(long excludedEventId);

}

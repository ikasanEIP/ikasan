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
package org.ikasan.framework.systemevent.dao;

import java.util.Date;

import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.systemevent.model.SystemEvent;

/**
 * Data access interface for persistence of <code>SystemEventDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public interface SystemEventDao {

	/**
	 * Persists a new system event
	 * 
	 * @param systemEvent
	 */
	public void save(SystemEvent systemEvent);


	/**
	 * Performs a paged search for <code>SystemEvent</code>s restricting by criteria fields as supplied
	 * 
	 * @param pageNo - page control field - page no of results to return
	 * @param pageSize - page control field - size of page
	 * @param orderBy - page control - field to order by
	 * @param orderAscending - page control field - true/false results in ascending order with respect to orderBy field
	 * @param subject - criteria field - filter for exact match on subject
	 * @param action - criteria field - filter for exact match on action
	 * @param timestampFrom - criteria field - filter for events with timestamp greater than this value
	 * @param timestampTo - criteria field - filter for events with timestamp less than this value
	 * @param actor - criteria field - filter for exact match on actor
	 * 
	 * @return PagedSearchResult<SystemEvent> - page friendly search result subset
	 */
	public PagedSearchResult<SystemEvent> find(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,String subject, String action,
			Date timestampFrom, Date timestampTo, String actor);

	/**
	 * Deletes all expired system events
	 */
	public void deleteExpired();
}

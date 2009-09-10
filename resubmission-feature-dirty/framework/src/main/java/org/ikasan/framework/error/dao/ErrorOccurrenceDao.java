/*
 * $Id
 * $URL
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
package org.ikasan.framework.error.dao;

import java.util.List;

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * Data Access interface for the persistence of <code>ErrorOccurrence</code> instances
 * @author Ikasan Development Team
 *
 */
public interface ErrorOccurrenceDao {
	
	/**
	 * Persist an ErrorOccurrence
	 * 
	 * @param errorOccurrence
	 */
	public void save(ErrorOccurrence errorOccurrence);
	
	/**
	 * Retrieves an ErrorOccurrence by ids
	 * 
	 * @param id
	 * @return specified ErrorOccurrence
	 */
	public ErrorOccurrence getErrorOccurrence(Long id);

	
	/**
	 * Perform a paged search for <code>ErrorOccurrence</code>s
	 * 
	 * @param pageNo 
	 * @param pageSize
	 * @param orderBy
	 * @param orderAscending
	 * @param moduleName
	 * @param flowName
	 * 
	 * @return PagedSearchResult
	 */
	public PagedSearchResult<ErrorOccurrence> findErrorOccurrences(int pageNo, int pageSize, String orderBy, boolean orderAscending,String moduleName, String flowName);
	
    /**
     * Deletes all ErrorOccurrences that have surpassed their expiry
     */
    public void deleteAllExpired();

	/**
	 * Returns all ErrorOccurrences with the specified eventId
	 * 
	 * @param eventId
	 * @return List of ErrorOccurrence 
	 */
	public List<ErrorOccurrence> getErrorOccurrences(String eventId);
}

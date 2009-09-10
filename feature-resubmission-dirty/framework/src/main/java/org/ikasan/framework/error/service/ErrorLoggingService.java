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

package org.ikasan.framework.error.service;

import java.util.List;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * This class represents a platform level service for the heavyweight logging of Errors
 * 
 * @author Ikasan Development Team
 *
 */
public interface ErrorLoggingService {

	/**
	 * Logs an Error where there is an inflight Event involved in a Flow
	 * 
	 * @param throwable
	 * @param moduleName
	 * @param flowName
	 * @param flowElementName
	 * @param currentEvent
	 */
	public void logError(Throwable throwable, String moduleName, String flowName, String flowElementName, Event currentEvent);

	/**
	 * Returns a paged listing of errors
	 * 
	 * @param pageNo - 0 or greater, index into the list of all possible results
	 * @param pageSize - 0 or greater, no of errors to return on a page
	 * @param flowName 
	 * @param moduleName 
	 * 
	 * @return PagedSearchResult<ErrorOccurrence>
	 */
	public PagedSearchResult<ErrorOccurrence> getErrors(int pageNo, int pageSize, String orderBy, boolean orderAscending,String moduleName, String flowName);

	/**
	 * Logs an Error caused before there was an Event
	 * 
	 * @param throwable
	 * @param moduleName
	 * @param initiatorName
	 */
	public void logError(Throwable throwable, String moduleName, String initiatorName);
	
	/**
	 * Retrieve an ErrorOccurrence specified by its Id
	 * 
	 * @param errorOccurrenceId
	 * @return ErrorOccurrence
	 */
	public ErrorOccurrence getErrorOccurrence(long errorOccurrenceId);
	
    /**
     * Causes all <code>ErrorOccurrence</code>s that are deemed to be too old to be deleted
     */
	public void housekeep();

	/**
	 * Returns all ErrorOccurrences for the event specified by its id
	 * 
	 * @param eventId
	 * @return List of ErrorOccurrences for the specified event
	 */
	public List<ErrorOccurrence> getErrorOccurrences(String eventId);
	
	
	/**
	 * Registers an <code>ErrorOccurrenceListener</code> as a listener for new <code>ErrorOccurrence</code>
	 * 
	 * @param errorOccurrenceListener
	 */
	public void addErrorOccurrenceListener(ErrorOccurrenceListener errorOccurrenceListener);
	
	/**
     * Deregisters an <code>ErrorOccurrenceListener</code> as a listener for new <code>ErrorOccurrence</code>
	 * 
	 * @param errorOccurrenceListener
	 */
	public void removeErrorOccurrenceListener(ErrorOccurrenceListener errorOccurrenceListener);
}

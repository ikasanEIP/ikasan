/* 
 * $Id: 
 * $URL: 
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */

package org.ikasan.framework.error.service;

import java.util.List;

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.spec.flow.event.FlowEvent;

/**
 * This class represents a platform level service for the heavyweight logging of Errors
 * 
 * @author Ikasan Development Team
 *
 */
public interface ErrorLoggingService {

	/**
	 * Logs an Error where there is an inflight FlowEvent involved in a Flow
	 * 
	 * @param throwable
	 * @param moduleName
	 * @param flowName
	 * @param flowElementName
	 * @param currentFlowEvent
	 * @param actionTaken
	 */
	public void logError(Throwable throwable, String moduleName, String flowName, String flowElementName, FlowEvent currentFlowEvent, String actionTaken);

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
	 * Logs an Error caused before there was an FlowEvent
	 * 
	 * @param throwable
	 * @param moduleName
	 * @param initiatorName
	 * @param actionTaken
	 */
	public void logError(Throwable throwable, String moduleName, String initiatorName, String actionTaken);
	
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

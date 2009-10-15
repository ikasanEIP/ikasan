/* 
 * $Id$
 * $URL$ 
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
	 * @param orderBy - field to order by
	 * @param orderAscending - in ascending order?
	 * @param flowName - restrict by flowName if supplied
	 * @param moduleName - restrict by moduleName if supplied
	 * 
	 * @return PagedSearchResult<ExcludedEvent>
	 */
	public PagedSearchResult<ExcludedEvent> getExcludedEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, String moduleName, String flowName);

	
	/**
	 * Retrieve an ExcludedEvent specified by its event Id
	 * 
	 * @param eventId
	 * @return ExcludedEvent
	 */	
	public ExcludedEvent getExcludedEvent(String eventId);
	
	/**
	 * Synchronously attempts to resubmit an ExcludedEvent specified by id
	 * 
	 * 
	 * @throws IllegalArgumentException if ExcludedEvent cannot be found, or if referenced
	 * 	module or flow are not available
	 * 
	 * @param eventId
	 * @param resubmitter
	 */
	public void resubmit(String eventId, String resubmitter);



}

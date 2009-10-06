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
package org.ikasan.framework.event.exclusion.dao;

import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * @author The Ikasan Development Team
 *
 */
public interface ExcludedEventDao{

	/**
	 * Saves an <code>ExcludedEvent</code> to persistent storage
	 * 
	 * @param excludedEvent
	 */
	public void save(ExcludedEvent excludedEvent);
	
	/**
	 * Loads an <code>ExcludedEvent</code> to persistent storage
	 * 
	 * @param excludedEventId
	 */
	public ExcludedEvent load(Long excludedEventId);

	/**
	 * Perform a paged search for <code>ExcludedEvent</code>s
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param orderAscending 
	 * @param flowName 
	 * @param moduleName 
	 * 
	 * @return PagedSearchResult
	 */
	public PagedSearchResult<ExcludedEvent> findExcludedEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, String moduleName, String flowName);

	/**
	 * Retrieves an ExcludedEvent by id
	 * 
	 * @param id
	 * @return ExcludedEvent
	 */
	public ExcludedEvent getExcludedEvent(long excludedEventId);
	
	/**
	 * Retrieves an ExcludedEvent by event Id
	 * 
	 * @param eventId
	 * @return ExcludedEvent
	 */
	public ExcludedEvent getExcludedEvent(String eventId);

	
	/**
	 * Deletes excluded event
	 * 
	 * @param excludedEvent
	 */
	public void delete(ExcludedEvent excludedEvent);

}

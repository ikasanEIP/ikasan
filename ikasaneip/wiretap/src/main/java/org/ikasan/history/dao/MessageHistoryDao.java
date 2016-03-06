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
package org.ikasan.history.dao;

import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.search.PagedSearchResult;

import java.util.Date;
import java.util.Set;

/**
 * DAO contract for accessing Message History data
 *
 * @author Ikasan Development Team
 */
public interface MessageHistoryDao
{
    /**
     * Save a MessageHistoryEvent
     * @param messageHistoryEvent the event
     */
    void save(MessageHistoryEvent messageHistoryEvent);


    /**
     * Search for MessageHistoryEvents
     * @param pageNo the current page number
     * @param pageSize the page size
     * @param orderBy columns to order by
     * @param orderAscending order ascending?
     * @param moduleNames a set of module names to search for
     * @param flowName a flow name to search for
     * @param componentName a component name to search for
     * @param lifeId a lifeId to search for
     * @param relatedLifeId a relatedLifeId to search for
     * @param fromDate the from datetime
     * @param toDate the to datetime
     * @return a paged result set of MessageHistoryEvent
     */
    PagedSearchResult<MessageHistoryEvent> findMessageHistoryEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                                    Set<String> moduleNames, String flowName, String componentName,
                                                                    String lifeId, String relatedLifeId, Date fromDate, Date toDate);

    /**
     * Retrieve a MessageHistoryEvent (or set of events from multiple Flows) using the lifeId or relatedLifeId
     *
     * This is a paged result since the response could contain many modules/flows/components
     * @param pageNo the current page number
     * @param pageSize the page size
     * @param orderBy columns to order by
     * @param orderAscending order ascending?
     * @param lifeId the lifeId to retrieve
     * @param relatedLifeId an optional relatedLifeId to retrieve events that had the main lifeId mutated
     * @return a paged result set of MessageHistoryEvent
     */
    PagedSearchResult<MessageHistoryEvent> getMessageHistoryEvent(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                                  String lifeId, String relatedLifeId);

    /**
     * Delete all expired MessageHistoryEvents
     */
    void deleteAllExpired();

    /**
     * Method to state that there are housekeepable records available.
     * @return true if so, false otherwise
     */
    boolean housekeepablesExist();


}

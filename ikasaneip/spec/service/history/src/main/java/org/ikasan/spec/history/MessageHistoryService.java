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
package org.ikasan.spec.history;


import java.util.Date;
import java.util.Set;

/**
 * This Service allows <code>MessageHistoryEvent</code>s to be saved out at runtime for later
 * retrieval and inspection
 *
 * Paged search is also catered for
 *
 * @author Ikasan Development Team
 */
public interface MessageHistoryService<EVENT, RESULT>
{

    /**
     * Save an event
     * @param event the event
     * @param moduleName the module name
     * @param flowName the flow name
     */
    void save(EVENT event, String moduleName, String flowName);

    /**
     * Search for saved MessageHistoryEvents
     * @param pageNo the current page number
     * @param pageSize the page size
     * @param orderBy columns to order by
     * @param orderAscending order ascending?
     * @param moduleNames a set of module names to search for
     * @param flowName a flow name to search for
     * @param componentName a component name to search for
     * @param eventId an eventId to search for
     * @param relatedEventId a relatedEventId to search for
     * @param fromDate the from datetime
     * @param toDate the to datetime
     * @return a result set
     */
    RESULT findMessageHistoryEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                    Set<String> moduleNames, String flowName, String componentName,
                                    String eventId, String relatedEventId, Date fromDate, Date toDate);

    /**
     * Retrieve a MessageHistoryEvent via its eventId or relatedEventId
     * This could return an event that spans multiple modules and flows, hence the paging aspect
     * @param pageNo the current page number
     * @param pageSize the page size
     * @param orderBy columns to order by
     * @param orderAscending order ascending?
     * @param eventId an eventId to search for
     * @param lookupRelatedEventId whether to look for a relatedId of the given eventId
     * @return a result set
     */
    RESULT getMessageHistoryEvent(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                  String eventId, boolean lookupRelatedEventId);

}

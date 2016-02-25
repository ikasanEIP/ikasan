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
package org.ikasan.wiretap.dao;

import java.util.Date;
import java.util.Set;

import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;

/**
 * Interface for all wiretap event data access.
 * 
 * @author Ikasan Development Team
 */
public interface WiretapDao
{

    /**
     * Save a wiretapFlowEvent entry.
     * 
     * @param wiretapFlowEvent - The wiretap event to save
     */
    public void save(WiretapEvent wiretapEvent);

    /**
     * Perform a paged search for <code>WiretapFlowEvent</code>s
     * 
     * @param pageNo - The page number to retrieve
     * @param pageSize - The size of the page
     * @param orderBy - order by field
     * @param orderAscending - ascending flag
     * @param moduleNames - The list of module names
     * @param moduleFlow - The name of Flow internal to the Module
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
            final String moduleFlow, final String componentName, final String eventId, final String payloadId, Date fromDate, Date untilDate, String payloadContent);
    
    /**
     * Perform a paged search for <code>WiretapFlowEvent</code>s
     * 
     * @param pageNo - The page number to retrieve
     * @param pageSize - The size of the page
     * @param orderBy - order by field
     * @param orderAscending - ascending flag
     * @param moduleNames - The list of module names
     * @param moduleFlow - The name of Flow internal to the Module
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
            final Set<String> moduleFlow, final Set<String> componentName, final String eventId, final String payloadId, Date fromDate, Date untilDate, String payloadContent);

    /**
     * Find wiretap entry by identifier
     * 
     * @param id - The id to search on
     * @return WiretapFlowEvent
     */
    public WiretapEvent findById(Long id);

    /**
     * Deletes all WiretapFlowEvents that have surpassed their expiryDate
     */
    public void deleteAllExpired();

    public boolean isBatchHousekeepDelete();

    public void setBatchHousekeepDelete(boolean batchHousekeepDelete);

    public Integer getHousekeepingBatchSize();

    public void setHousekeepingBatchSize(Integer housekeepingBatchSize);

    public Integer getTransactionBatchSize();

    public void setTransactionBatchSize(Integer transactionBatchSize);

    /**
     * Method to state that there are housekeepable records available.
     * @return
     */
    public boolean housekeepablesExist();

    void setHousekeepQuery(String housekeepQuery);
}

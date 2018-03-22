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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.ikasan.history.model.MetricEvent;
import org.ikasan.spec.history.FlowInvocationMetric;
import org.ikasan.spec.history.ComponentInvocationMetric;
import org.ikasan.spec.search.PagedSearchResult;

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
    public void save(ComponentInvocationMetric messageHistoryEvent);

    /**
     * Save a MetricEvent
     * @param metricEvent the event
     */
    public void save(MetricEvent metricEvent);

    /**
     * Save a FlowInvocation
     *
     * @param flowInvocationMetric
     */
    public void save(FlowInvocationMetric flowInvocationMetric);


    /**
     * Search for MessageHistoryEvents
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
     * @return a paged result set of MessageHistoryEvent
     */
    public PagedSearchResult<ComponentInvocationMetric> findMessageHistoryEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                                                 Set<String> moduleNames, String flowName, String componentName,
                                                                                 String eventId, String relatedEventId, Date fromDate, Date toDate);

    /**
     * Retrieve a MessageHistoryEvent (or set of events from multiple Flows) using the lifeId or relatedLifeId
     *
     * This is a paged result since the response could contain many modules/flows/components
     * @param pageNo the current page number
     * @param pageSize the page size
     * @param orderBy columns to order by
     * @param orderAscending order ascending?
     * @param eventId the eventId to retrieve
     * @param relatedEventId an optional relatedEventId to retrieve events that had the main eventId mutated
     * @return a paged result set of MessageHistoryEvent
     */
    public PagedSearchResult<ComponentInvocationMetric> getMessageHistoryEvent(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                                               String eventId, String relatedEventId);

    /**
     * Delete all expired MessageHistoryEvents
     */
    public void deleteAllExpired();

    /**
     * Method to state that there are housekeepable records available.
     * @return true if so, false otherwise
     */
    public boolean housekeepablesExist();


    /**
     * Get the events that are ready to be harvested.
     *
     * @param transactionBatchSize
     * @return
     */
    public List<FlowInvocationMetric> getHarvestableRecords(int transactionBatchSize);

    /**
     * Get the events that are already harvested.
     *
     * @param transactionBatchSize
     * @return
     */
    public List<FlowInvocationMetric> getHarvestedRecords(final int transactionBatchSize);

    /**
     * Delete the events in the list.
     *
     * @param events
     */
    public void deleteHarvestableRecords(List<FlowInvocationMetric> events);

    /**
     * Method to state that there are harvestable records available.
     * @return true if so, false otherwise
     */
    public boolean harvestableRecordsExist();

    /**
     * @return the housekeepingBatchSize
     */
    public Integer getHousekeepingBatchSize();

    /**
     * @param housekeepingBatchSize the housekeepingBatchSize to set
     */
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize);

    /**
     * @return the housekeepingBatchSize
     */
    public Integer getTransactionBatchSize();

    /**
     * @param transactionBatchSize the housekeepingBatchSize to set
     */
    public void setTransactionBatchSize(Integer transactionBatchSize);

    /**
     * @return the batchHousekeepDelete
     */
    public boolean isBatchHousekeepDelete();

    /**
     * @param batchHousekeepDelete the batchHousekeepDelete to set
     */
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete);

    /**
     * Update entity as harvested.
     *
     * @param events
     */
    public void updateAsHarvested(List<FlowInvocationMetric> events);

}

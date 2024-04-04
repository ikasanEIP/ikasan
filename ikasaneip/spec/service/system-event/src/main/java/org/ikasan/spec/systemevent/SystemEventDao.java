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
package org.ikasan.spec.systemevent;

import org.ikasan.spec.search.PagedSearchResult;

import java.util.Date;
import java.util.List;

/**
 * Data access interface for persistence of <code>SystemFlowEventDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public interface SystemEventDao<EVENT> {

	/**
	 * Persists a new system event
	 *
	 * @param event
	 */
	void save(EVENT event);


	/**
	 * Performs a paged search for <code>SystemFlowEvent</code>s restricting by criteria fields as supplied
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
	 * @return PagedSearchResult<EVENT> - page friendly search result subset
	 */
	PagedSearchResult<EVENT> find(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,String subject, String action,
			Date timestampFrom, Date timestampTo, String actor);


	/**
	 * This method returns a list of Events based on subjects and actor within a certain date range.
	 *
	 * @param subjects
	 * @param actor
	 * @param timestampFrom
	 * @param timestampTo
	 * @return
	 */
	List<EVENT> list(List<String> subjects, String actor, Date timestampFrom, Date timestampTo);


	/**
	 * Deletes all expired system events
	 */
	void deleteExpired();

	boolean isBatchHousekeepDelete();

	void setBatchHousekeepDelete(boolean batchHousekeepDelete);

	Integer getHousekeepingBatchSize();

	void setHousekeepingBatchSize(Integer housekeepingBatchSize);

	/**
	 * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
	 *
	 * @return true if there is at least 1 expired WiretapFlowEvent
	 */
	boolean housekeepablesExist();

	/**
	 * @param transactionBatchSize the transactionBatchSize to set
	 */
	void setTransactionBatchSize(Integer transactionBatchSize);

    /**
     * Get (housekeepingBatchSize) harvestable records.
     *
     * @param housekeepingBatchSize
     * @return
     */
    List<EVENT> getHarvestableRecords(final int housekeepingBatchSize);

    /**
     * Update entity as being harvested.
     *
     * @param events
     */
    void updateAsHarvested(List<EVENT> events);

    /**
     * Is harvest query ordered?
     *
     * @return true if ordered otherwise false.
     */
    public Boolean getOrderHarvestQuery();

    /**
     * Set flag to determine if harvest query should be ordered.
     *
     * @param orderHarvestQuery
     */
    public void setOrderHarvestQuery(Boolean orderHarvestQuery);

}

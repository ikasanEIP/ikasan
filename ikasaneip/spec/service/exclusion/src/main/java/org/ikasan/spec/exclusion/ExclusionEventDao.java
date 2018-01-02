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
package org.ikasan.spec.exclusion;

import java.util.Date;
import java.util.List;

/**
 * Exclusion Event Data Access Contract.
 * @author Ikasan Development Team
 */
public interface ExclusionEventDao<IDENTIFIER,EVENT>
{
    /**
     * Save the event
     * @param event
     * @return
     */
    public void save(EVENT event);

    /**
     * Remove the event
     * @param moduleName
     * @param flowName
     * @param identifier
     * @return
     */
    public void delete(String moduleName, String flowName, IDENTIFIER identifier);
    
    /**
     * Remove the event
     * @param errorUri
     * @return
     */
    public void delete(String errorUri);

    /**
     * Find a specific excluded event
     * @param moduleName
     * @param flowName
     * @param identifier
     * @return
     */
    public EVENT find(String moduleName, String flowName, IDENTIFIER identifier);

    /**
     * Get the row count for the given criteria.
     *
     * @param moduleName
     * @param flowName
     * @param startDate
     * @param endDate
     * @param identifier
     * @return
     */
    public Long rowCount(final List<String> moduleName,
                         final List<String> flowName, final Date startDate, final Date endDate,
                         final String identifier);
    
    /**
     * Find all excluded events
     * 
     * @return
     */
    public List<EVENT> findAll();
    
    /**
     * Find a list of event entities based on criteria.
     * 
     * @param moduleName
     * @param flowName
     * @param starteDate
     * @param endDate
     * @param identifier
     * @param size
     * @return
     */
    public List<EVENT> find(List<String> moduleName, List<String> flowName, Date starteDate, Date endDate, IDENTIFIER identifier, int size);
    
    /**
     * Find the event based on it's URI
     * @param errorUri
     * @return
     */
    public EVENT find(String errorUri);

    /**
     * Get (housekeepingBatchSize) harvestable records.
     *
     * @param housekeepingBatchSize
     * @return
     */
    public List<EVENT> getHarvestableRecords(final int housekeepingBatchSize);

    /**
     * Delete all e
     */
    public void deleteAllExpired();

    /**
     * Update entity as being harvested.
     *
     * @param events
     */
    public void updateAsHarvested(List<EVENT> events);
}

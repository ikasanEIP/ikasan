/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */

package org.ikasan.filter.duplicate.dao;

import java.util.List;

import org.ikasan.filter.duplicate.model.FilterEntry;

/**
 * DAO interface for interacting with filtered messages.
 * 
 * @author Ikasan Development Team
 *
 */
public interface FilteredMessageDao
{
    /**
     * Save new message.
     * @param message
     */
    public void save(FilterEntry message);

    /**
     * Try to find {@link FilterEntry} by its id: clientId and
     * criteria.
     * 
     * @param message {@link FilterEntry} to be found
     * 
     * @return The found {@link FilterEntry} or null if nothing
     *         found in persistence.
     */
    public FilterEntry findMessage(FilterEntry message);

    /**
     * Delete expired Filter Entries from persistence 
     */
    public void deleteAllExpired();

    /**
     * Allow batching of housekeep tasks to be turned on/off
     * @param batchedHousekeep
     */
    public void setBatchedHousekeep(boolean batchedHousekeep);

    /**
     * Allow the batch size to be overridden
     * @param batchSize
     */
    public void setBatchSize(int batchSize);
    
    /**
     * Allow the transaction batch size to be overridden
     * @param transactionBatchSize
     */
    public void setTransactionBatchSize(int trasnactionBatchSize);
    
    /**
	 * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
	 * 
	 * @return true if there is at least 1 expired WiretapFlowEvent 
	 */
	public boolean housekeepablesExist();
	
	/**
	 * Find expired messages.
	 * 
	 * @return
	 */
	public List<FilterEntry> findExpiredMessages();
}

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
package org.ikasan.filter.duplicate.service;

import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.search.PagedSearchResult;

import java.util.Date;
import java.util.List;

/**
 * The default implementation for {@link ManagementFilterService}
 *
 * @author Ikasan Development Team
 */
public class DefaultManagementFilterService implements ManagementFilterService, HousekeepService
{
    /**
     * {@link FilteredMessageDao} for accessing encountered messages
     */
    private final FilteredMessageDao dao;

    /**
     * Constructor
     *
     * @param dao
     */
    public DefaultManagementFilterService(final FilteredMessageDao dao)
    {
        this.dao = dao;
        if (dao == null)
        {
            throw new IllegalArgumentException("dao cannot be 'null'");
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.management.HousekeeperService#houseKeepablesExist()
     */
    @Override
    public boolean housekeepablesExist()
    {
        return this.dao.housekeepablesExist();
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        this.dao.setHousekeepingBatchSize(housekeepingBatchSize);
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {
        this.dao.setTransactionBatchSize(transactionBatchSize);
    }

    /* (non-Javadoc)
     * @see org.ikasan.filter.duplicate.service.DuplicateFilterService#housekeep()
     */
    @Override
    public void housekeep()
    {
        this.dao.deleteAllExpired();
    }

    @Override
    public void save(FilterEntry message)
    {
        dao.save(message);
    }

    @Override
    public FilterEntry find(Integer criteriaId, String clientId)
    {
        return dao.findMessage(new DefaultFilterEntry(criteriaId, clientId, 0));
    }

    @Override
    public List<FilterEntry> find(String clientId)
    {
        return dao.findMessages(clientId);
    }

    @Override
    public PagedSearchResult<FilterEntry> findMessagesByPage(int pageNo, int pageSize,
        String clientId, Integer criteria,
        Date fromDate, Date untilDate)
    {
        return dao.findMessagesByPage(pageNo, pageSize, criteria, clientId, fromDate, untilDate);
    }

    @Override
    public void delete(Integer criteria, String clientId)
    {
        dao.delete(new DefaultFilterEntry(criteria, clientId, 0));
    }
}

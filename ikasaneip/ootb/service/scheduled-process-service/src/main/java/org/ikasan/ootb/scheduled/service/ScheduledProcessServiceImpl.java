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
package org.ikasan.ootb.scheduled.service;

import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.dao.ScheduledProcessEventDao;
import org.ikasan.spec.scheduled.event.service.ScheduledProcessEventService;

import java.util.List;

/**
 * Implementation of the Scheduled Process Service.
 *
 * @author Ikasan Developmnent Team
 */
public class ScheduledProcessServiceImpl implements ScheduledProcessEventService, HarvestService<ScheduledProcessEvent>, HousekeepService
{
    /** handle to the DAO for this service */
    private ScheduledProcessEventDao scheduledProcessEventDao;

    /**
     * Constructor
     *
     * @param scheduledProcessEventDao
     */
    public ScheduledProcessServiceImpl(ScheduledProcessEventDao scheduledProcessEventDao) {
        this.scheduledProcessEventDao = scheduledProcessEventDao;
    }

    @Override
    public void save(ScheduledProcessEvent scheduledProcessEvent) {
        this.scheduledProcessEventDao.save(scheduledProcessEvent);
    }

    @Override
    public List<ScheduledProcessEvent> harvest(int transactionBatchSize) {
        return this.scheduledProcessEventDao.harvest(transactionBatchSize);
    }

    @Override
    public boolean harvestableRecordsExist() {
        return this.scheduledProcessEventDao.harvestableRecordsExist();
    }

    @Override
    public void saveHarvestedRecord(ScheduledProcessEvent harvestedRecord) {
        this.scheduledProcessEventDao.saveHarvestedRecord(harvestedRecord);
    }

    @Override
    public void updateAsHarvested(List<ScheduledProcessEvent> events) {
        this.scheduledProcessEventDao.updateAsHarvested(events);
    }

    @Override
    public void housekeep() {
        this.scheduledProcessEventDao.housekeep();
    }

    @Override
    public boolean housekeepablesExist() {
        return true;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize) {
        // not relevant for this implementation
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize) {
        // not relevant for this implementation
    }
}

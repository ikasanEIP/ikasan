package org.ikasan.scheduled.service;

import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.scheduled.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.ScheduledProcessEventDao;
import org.ikasan.spec.scheduled.ScheduledProcessEventService;

import java.util.List;

public class ScheduledProcessServiceImpl implements ScheduledProcessEventService, HarvestService<ScheduledProcessEvent>, HousekeepService {

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

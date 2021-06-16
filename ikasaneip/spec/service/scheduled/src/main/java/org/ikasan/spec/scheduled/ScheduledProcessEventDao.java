package org.ikasan.spec.scheduled;

import java.util.List;

public interface ScheduledProcessEventDao {

    /**
     * Save the event.
     *
     * @param scheduledProcessEvent
     */
    public void save(ScheduledProcessEvent scheduledProcessEvent);

    /**
     * Harvest the records.
     *
     * @param transactionBatchSize
     * @return
     */
    public List<ScheduledProcessEvent> harvest(int transactionBatchSize);

    /**
     * Are there any records to harvest?
     *
     * @return
     */
    public boolean harvestableRecordsExist();

    /**
     * Save the harvested record.
     *
     * @param harvestedRecord
     */
    public void saveHarvestedRecord(ScheduledProcessEvent harvestedRecord);

    /**
     * Update entities as being harvested.
     *
     * @param events
     */
    public void updateAsHarvested(List<ScheduledProcessEvent> events);

    /**
     * Housekeep all harvested records
     */
    public void housekeep();

}

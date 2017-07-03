package org.ikasan.harvest;

import java.util.List;

/**
 * Created by Ikasan Development Team on 17/07/2016.
 */
public interface HarvestService<RECORD>
{
    /**
     * This method performs a non-destructive read of RECORDs
     * from the underlying data store, returning  a List
     * of RECORDs that were non-destructively read. RECORDs should
     * be marked as house kept.
     *
     * @param transactionBatchSize
     * @return
     */
    public List<RECORD> harvest(int transactionBatchSize);

    /**
     * Are there any records to harvest?
     *
     * @return
     */
    public boolean harvestableRecordsExist();
}

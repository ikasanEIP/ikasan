/*
 *
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Copyright (c) 2007-2014 Mizuho International plc
 * ====================================================================
 * /
 */

package org.ikasan.systemevent.service;

/**
 * Configuration bean for the System Event service
 */
public class SystemEventServiceConfiguration
{
    /**
     * If batchHousekeepDelete is true, this sets the number of system events
     * deleted on each batch execution
     */
    private int housekeepingBatchSize;

    /**
     * Boolean to determine if system events are deleted in batches during housekeeping
     */
    private boolean batchHousekeepDelete;


    /* Getters and setters */
    public int getHousekeepingBatchSize()
    {
        return housekeepingBatchSize;
    }

    public void setHousekeepingBatchSize(int housekeepingBatchSize)
    {
        this.housekeepingBatchSize = housekeepingBatchSize;
    }

    public boolean isBatchHousekeepDelete()
    {
        return batchHousekeepDelete;
    }

    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {
        this.batchHousekeepDelete = batchHousekeepDelete;
    }
}

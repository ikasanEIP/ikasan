package org.ikasan.backup.h2.job;

import org.ikasan.spec.housekeeping.HousekeepingJob;

public interface H2DatabaseBackupHousekeepingJob extends HousekeepingJob {

    /**
     * Method to allow the job to be cranked manually if necessary.
     */
    void backup();
}

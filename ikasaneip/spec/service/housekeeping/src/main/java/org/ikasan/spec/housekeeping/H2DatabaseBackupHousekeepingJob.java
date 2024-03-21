package org.ikasan.spec.housekeeping;

public interface H2DatabaseBackupHousekeepingJob extends HousekeepingJob {

    /**
     * Method to allow the job to be cranked manually if necessary.
     */
    void backup();
}

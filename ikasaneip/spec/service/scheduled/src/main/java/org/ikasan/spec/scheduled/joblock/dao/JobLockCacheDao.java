package org.ikasan.spec.scheduled.joblock.dao;

import org.ikasan.spec.scheduled.joblock.model.JobLockCacheRecord;

public interface JobLockCacheDao {

    /**
     * Saves the JobLockCacheRecord to the database.
     *
     * @param jobLockCacheLockHolderRecord the JobLockCacheRecord to be saved
     */
    void save(JobLockCacheRecord jobLockCacheLockHolderRecord);

    /**
     * Retrieves the JobLockCacheRecord for the specified environment.
     *
     * @param environment the environment for which to retrieve the JobLockCacheRecord
     * @return the JobLockCacheRecord for the specified environment
     */
    JobLockCacheRecord get(String environment);
}
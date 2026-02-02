package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.joblock.model.JobLockCacheAuditRecord;
import org.ikasan.spec.scheduled.joblock.model.JobLockCacheRecord;
import org.ikasan.spec.search.SearchResults;

public interface JobLockCacheService {

    /**
     * Saves the provided JobLockCacheRecord.
     *
     * @param jobLockCacheLockHolderRecord the JobLockCacheRecord to save
     */
    void save(JobLockCacheRecord jobLockCacheLockHolderRecord);

    /**
     * Retrieves the JobLockCacheRecord associated with the specified environment.
     *
     * @param environment the environment for which to retrieve the JobLockCacheRecord
     * @return the JobLockCacheRecord object associated with the specified environment
     */
    JobLockCacheRecord get(String environment);

    /**
     * Retrieves a list of JobLockCacheAuditRecord objects with a specified limit and offset.
     *
     * @param limit the maximum number of records to retrieve
     * @param offset the number of records to skip before starting to return data
     * @return a SearchResults object containing the list of JobLockCacheAuditRecord objects, total number of results,
     * and query response time
     */
    SearchResults<JobLockCacheAuditRecord> findAll(int limit, int offset);
}
package org.ikasan.spec.scheduled.joblock.dao;

import org.ikasan.spec.scheduled.joblock.model.JobLockCacheAuditRecord;
import org.ikasan.spec.search.SearchResults;

public interface JobLockCacheAuditDao {

    String JOB_LOCK_AUDIT_CACHE_TYPE = "jockLockCacheRecordAudit";
    String JOB_LOCK_AUDIT_CACHE_TYPE_ID = "jockLockCacheRecordAuditID";

    /**
     * Saves a JobLockCacheAuditRecord to the database.
     *
     * @param jobLockCacheAuditRecord the JobLockCacheAuditRecord to be saved
     */
    void save(JobLockCacheAuditRecord jobLockCacheAuditRecord);

    /**
     * Retrieves a list of JobLockCacheAuditRecord objects based on the provided limit and offset values.
     *
     * @param limit the maximum number of records to retrieve
     * @param offset the starting index from which to retrieve records
     * @return a SearchResults object containing a list of JobLockCacheAuditRecord objects, the total number of results, and response time
     */
    SearchResults<JobLockCacheAuditRecord> findAll(int limit, int offset);
}

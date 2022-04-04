package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.joblock.model.JobLockCacheAuditRecord;
import org.ikasan.spec.scheduled.joblock.model.JobLockCacheRecord;
import org.ikasan.spec.search.SearchResults;

public interface JobLockCacheService {

    void save(JobLockCacheRecord jobLockCacheLockHolderRecord);

    JobLockCacheRecord get();

    SearchResults<JobLockCacheAuditRecord> findAll(int limit, int offset);
}
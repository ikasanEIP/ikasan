package org.ikasan.spec.scheduled.joblock.dao;

import org.ikasan.spec.scheduled.joblock.model.JobLockCacheAuditRecord;
import org.ikasan.spec.search.SearchResults;

public interface JobLockCacheAuditDao {

    void save(JobLockCacheAuditRecord jobLockCacheAuditRecord);

    SearchResults<JobLockCacheAuditRecord> findAll(int limit, int offset);
}

package org.ikasan.spec.scheduled.joblock.dao;

import org.ikasan.spec.scheduled.joblock.model.JobLockCacheRecord;

public interface JobLockCacheDao {

    void save(JobLockCacheRecord jobLockCacheLockHolderRecord);

    JobLockCacheRecord get();
}
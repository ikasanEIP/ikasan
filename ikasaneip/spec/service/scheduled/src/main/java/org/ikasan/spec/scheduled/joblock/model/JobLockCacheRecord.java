package org.ikasan.spec.scheduled.joblock.model;

import org.ikasan.spec.scheduled.context.model.JobLockCache;

public interface JobLockCacheRecord {
    String getId();

    void setJobLockCache(JobLockCache jobLockCache);

    JobLockCache getJobLockCache();

    long getTimestamp();
}
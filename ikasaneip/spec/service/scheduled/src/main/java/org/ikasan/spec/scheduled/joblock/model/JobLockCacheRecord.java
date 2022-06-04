package org.ikasan.spec.scheduled.joblock.model;

public interface JobLockCacheRecord {
    String getId();

    void setJobLockCache(JobLockCacheData jobLockCache);

    JobLockCacheData getJobLockCache();

    long getTimestamp();

    long getModifiedTimestamp();
}
package org.ikasan.spec.scheduled.joblock.model;

import org.ikasan.spec.scheduled.context.model.JobLockHolder;

import java.util.concurrent.ConcurrentHashMap;

public interface JobLockCacheData {

    ConcurrentHashMap<String, JobLockHolder> getJobLocksByLockName();

    void setJobLocksByLockName(ConcurrentHashMap<String, JobLockHolder> jobLocksByLockName);

    ConcurrentHashMap<String, JobLockHolder> getJobLocksByIdentifier();

    void setJobLocksByIdentifier(ConcurrentHashMap<String, JobLockHolder> jobLocksByIdentifier);
}

package org.ikasan.spec.scheduled.joblock.model;

import org.ikasan.spec.scheduled.context.model.JobLockHolder;

import java.util.concurrent.ConcurrentHashMap;

public interface JobLockCacheData {

    /**
     * Get the collection that holds the lock holders by name.
     *
     * @return
     */
    ConcurrentHashMap<String, JobLockHolder> getJobLocksByLockName();

    /**
     * Set the collection that holds the lock holders by name.
     *
     * @param jobLocksByLockName
     */
    void setJobLocksByLockName(ConcurrentHashMap<String, JobLockHolder> jobLocksByLockName);

    /**
     * Get the collection that reference lock by the job identifier that they participate in.
     *
     * @return
     */
    ConcurrentHashMap<String, String> getJobLocksByIdentifier();

    /**
     * Set the collection that reference lock by the job identifier that they participate in.
     *
     * @param jobLocksByIdentifier
     */
    void setJobLocksByIdentifier(ConcurrentHashMap<String, String> jobLocksByIdentifier);
}

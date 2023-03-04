package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface JobLock extends Serializable {

    /**
     * Set the name of the job lock.
     *
     * @param name
     */
    void setName(String name);

    /**
     * Get the name of the job lock.
     *
     * @return
     */
    String getName();

    /**
     * Set the name of the lock count.
     *
     * @param lockCount
     */
    void setLockCount(int lockCount);

    /**
     * Get the name of the lock count.
     * @return
     */
    int getLockCount();

    /**
     * Set the scheduler jobs.
     *
     * @param jobs
     */
    void setJobs(Map<String, List<SchedulerJob>> jobs);

    /**
     * Get the scheduler jobs.
     *
     * @return
     */
    Map<String, List<SchedulerJob>> getJobs();

    /**
     * Add flag to indicate that a job lock is exclusive.
     *
     * What is an exclusive lock?
     *
     * If a job that is part of an exclusive lock is required to run, it will take out an exclusive
     * lock and thus prevent any other jobs that are part of any other lock or the same lock from
     * running until the job with the exclusive lock completes.
     *
     * Similarly, if a job with an exclusive lock requires an exclusive lock, it will wait until all
     * other jobs that are part of locks to complete before it can take the exclusive lock and start.
     *
     * @param isExclusiveJobLock
     */
    void setExclusiveJobLock(boolean isExclusiveJobLock);

    /**
     * Get flag to indicate if the lock is an exclusive lock.
     *
     * @return
     */
    boolean isExclusiveJobLock();
}

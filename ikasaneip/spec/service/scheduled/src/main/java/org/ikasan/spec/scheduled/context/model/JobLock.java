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
    void setLockCount(long lockCount);

    /**
     * Get the name of the lock count.
     * @return
     */
    long getLockCount();

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
}

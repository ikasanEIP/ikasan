package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJobLockParticipant extends SchedulerJob {

    /**
     * Get the lock count for the job.
     *
     * @return
     */
    long getLockCount();

    /**
     * Set the lock count for the job.
     *
     * @param lockCount
     */
    void setLockCount(long lockCount);
}

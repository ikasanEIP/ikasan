package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJobLockParticipant extends SchedulerJob {

    /**
     * Retrieves the name of the child context associated with this job.
     *
     * @return the name of the child context
     */
    String getChildContextName();

    /**
     * Sets the name of the child context associated with this job.
     *
     * @param childContextName the name of the child context to set
     */
    void setChildContextName(String childContextName);

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

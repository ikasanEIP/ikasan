package org.ikasan.spec.scheduled.event.model;

public interface JobLockCacheEvent {
    enum EventType {
        LOCK_RELEASED,
        LOCK_OBTAINED,
        JOB_ADDED_TO_JOB_LOCK_QUEUE,
        JOB_REMOVED_FROM_JOB_LOCK_QUEUE
    }

    /**
     * Get the name of the job that was the catalyst for the event.
     *
     * @return
     */
    String getJobIdentifier();

    /**
     * Get the residing context of the job that was the catalyst for the event.
     *
     * @return
     */
    String getContextName();

    /**
     * Get the name of the lock.
     *
     * @return
     */
    String getLockName();

    /**
     * Get the event type associated with the event.
     *
     * @return
     */
    EventType getEvent();
}

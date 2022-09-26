package org.ikasan.spec.scheduled.event.model;

public interface JobLockCacheEvent {
    enum EventType {
        LOCK_RELEASED,
        LOCK_OBTAINED
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
     * Get the event type associated with the event.
     *
     * @return
     */
    EventType getEvent();
}

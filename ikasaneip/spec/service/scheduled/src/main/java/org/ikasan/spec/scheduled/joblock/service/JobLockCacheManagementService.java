package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface JobLockCacheManagementService {

    /**
     * Release the lock on a previously locked job with the given job identifier and context name.
     *
     * @param jobIdentifier the unique identifier of the locked job
     * @param contextName the name of the context to which the job belongs
     */
    void releaseLockedJob(String jobIdentifier, String contextName);

    /**
     * Removes the specified SchedulerJobInitiationEvent from the queue.
     *
     * @param schedulerJobInitiationEvent the SchedulerJobInitiationEvent to be removed
     */
    void removeQueuedSchedulerJobInitiationEvent(SchedulerJobInitiationEvent schedulerJobInitiationEvent);
}

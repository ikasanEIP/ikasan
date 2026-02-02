package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface JobLockCacheManagementService {


    /**
     * Releases the lock on a job with the specified job identifier, context name, and environment.
     * This allows other processes to access and modify the job.
     *
     * @param jobIdentifier the unique identifier of the job to be released
     * @param contextName the name of the context to which the job belongs
     * @param environment the environment in which the job is locked
     */
    void releaseLockedJob(String jobIdentifier, String contextName, String environment);


    /**
     * Remove a queued scheduler job initiation event from the scheduler.
     *
     * @param schedulerJobInitiationEvent the scheduler job initiation event to be removed
     * @param environment the environment in which the job is scheduled to run
     */
    void removeQueuedSchedulerJobInitiationEvent(SchedulerJobInitiationEvent schedulerJobInitiationEvent, String environment);
}

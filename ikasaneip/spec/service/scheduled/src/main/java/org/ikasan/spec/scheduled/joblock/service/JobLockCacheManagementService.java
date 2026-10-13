package org.ikasan.spec.scheduled.joblock.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface JobLockCacheManagementService {


    /**
     * Releases a locked job associated with the provided identifiers and context details.
     *
     * @param jobIdentifier the unique identifier of the job to be released
     * @param parentContextName the name of the parent context the job belongs to
     * @param contextName the name of the specific context the job is associated with
     * @param contextInstanceId the unique identifier of the context instance
     * @param environment the environment in which the job is locked
     */
    void releaseLockedJob(String jobIdentifier, String parentContextName, String contextName
        , String contextInstanceId, String environment);


    /**
     * Remove a queued scheduler job initiation event from the scheduler.
     *
     * @param schedulerJobInitiationEvent the scheduler job initiation event to be removed
     * @param environment the environment in which the job is scheduled to run
     */
    void removeQueuedSchedulerJobInitiationEvent(SchedulerJobInitiationEvent schedulerJobInitiationEvent, String environment);
}

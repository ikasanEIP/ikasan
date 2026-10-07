package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.core.listener.JobLockCacheEventListener;
import org.ikasan.spec.scheduled.event.model.ContextualisedSchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.instance.model.SchedulerJobInstance;
import org.ikasan.spec.scheduled.joblock.model.JobLockCacheRecord;
import org.ikasan.spec.scheduled.joblock.service.JobLockCacheService;

import java.io.Serializable;
import java.util.List;

public interface JobLockCache extends Serializable {


    /**
     * Add multiple JobLock objects to the JobLockCache for a specific environment.
     *
     * @param jobLocks    a List of JobLock objects to add to the cache
     * @param environment the specific environment to add the locks to
     */
    void addLocks(List<JobLock> jobLocks, String environment);



    /**
     * Attempts to acquire a lock for a specific job within a given context and environment.
     *
     * @param jobIdentifier     the identifier of the job to be locked
     * @param parentContextName the name of the parent context of the job
     * @param contextName       the name of the context where the job resides
     * @param contextInstanceId the unique identifier for the specific instance of the context
     * @param environment       the specific environment in which the job lock is being attempted
     * @return true if the lock was successfully acquired, false otherwise
     */
    boolean lock(String jobIdentifier, String parentContextName, String contextName
        , String contextInstanceId, String environment);


    /**
     * Attempts to release a lock for a specific job within the given context and environment.
     *
     * @param jobIdentifier     the identifier of the job whose lock is to be released
     * @param parentContextName the name of the parent context of the job
     * @param contextName       the name of the context where the job resides
     * @param contextInstanceId the unique identifier for the specific instance of the context
     * @param environment       the specific environment from which the lock is to be released
     * @return true if the lock was successfully released, false otherwise
     */
    boolean release(String jobIdentifier, String parentContextName, String contextName
        , String contextInstanceId, String environment);


    /**
     * Determines whether the specified job participates in a lock within the given context and environment.
     *
     * @param jobIdentifier the identifier of the job to check
     * @param parentContextName the name of the parent context of the job
     * @param contextName the name of the context where the job resides
     * @param environment the specific environment in which the check is being performed
     * @return true if the job participates in a lock, false otherwise
     */
    boolean doesJobParticipateInLock(String jobIdentifier, String parentContextName, String contextName, String environment);


    /**
     * Checks if a job with the given job identifier, context name, and environment is currently locked.
     *
     * @param jobIdentifier the identifier of the job to check
     * @param contextName the name of the context where the job resides
     * @param environment the specific environment in which the job is being checked
     * @return true if the job is locked, false otherwise
     */
    boolean locked(String jobIdentifier, String contextName, String environment);


    /**
     * Checks whether a specified job currently holds a lock in the given context and environment.
     *
     * @param jobIdentifier     the identifier of the job to check
     * @param parentContextName the name of the parent context of the job
     * @param contextName       the name of the context where the job resides
     * @param contextInstanceId the unique identifier for the specific instance of the context
     * @param environment       the specific environment in which to check for the lock
     * @return true if the job currently holds a lock, false otherwise
     */
    boolean hasLock(String jobIdentifier, String parentContextName, String contextName
        , String contextInstanceId, String environment);


    /**
     * Reset the job lock cache. All data will be removed from the cache!
     */
    void reset();


    /**
     * Reset the job lock cache for a specific environment. All data will be removed from the cache for that environment!
     *
     * @param environment the specific environment for which to reset the cache
     */
    void reset(String environment);


    /**
     * Reset the lock for a specific name and environment.
     *
     * @param lockName    the name of the lock to reset
     * @param environment the specific environment where the lock exists
     * @return true if the lock was successfully reset, false otherwise
     */
    boolean resetLock(String lockName, String environment);

    /**
     * Set the underlying persistence service.
     *
     * @param jobLockCacheService
     */
    void setJobLockCacheService(JobLockCacheService jobLockCacheService);


    /**
     * Adds a queued scheduler job initiation event to the job lock cache for a specific environment.
     *
     * @param jobIdentifier      the identifier of the job for which the initiation event is being queued
     * @param parentContextName  the name of the parent context associated with the job
     * @param contextName        the name of the specific context where the job resides
     * @param contextInstanceId  the unique identifier for the specific instance of the context
     * @param event              the scheduler job initiation event to be added to the queue
     * @param environment        the specific environment in which the job initiation event is being queued
     */
    void addQueuedSchedulerJobInitiationEvent(String jobIdentifier, String parentContextName
        , String contextName, String contextInstanceId, SchedulerJobInitiationEvent event, String environment);


    /**
     * Remove a queued scheduler job for a specific scheduler job instance and environment.
     *
     * @param schedulerJobInstance The scheduler job instance for which the queued job needs to be removed
     * @param environment The specific environment in which the job is queued
     */
    void removeQueuedSchedulerJob(SchedulerJobInstance schedulerJobInstance, String environment);


    /**
     * Polls the scheduler job initiation event wait queue for events associated with the specified
     * job identifier, parent context name, context name, and environment.
     *
     * @param jobIdentifier     the identifier of the job for which events are being polled
     * @param parentContextName the name of the parent context associated with the job
     * @param contextName       the name of the specific context where the job resides
     * @param environment       the specific environment in which the events are being polled
     * @return a list of {@code ContextualisedSchedulerJobInitiationEvent} objects associated
     *         with the specified criteria
     */
    List<ContextualisedSchedulerJobInitiationEvent> pollSchedulerJobInitiationEventWaitQueue(String jobIdentifier
        , String parentContextName, String contextName, String environment);


    /**
     * Sets a job lock cache record for a specific environment.
     *
     * @param jobLockCacheRecord the JobLockCacheRecord to set
     * @param environment the specific environment to set the cache record for
     */
    void setJobLockCacheRecord(JobLockCacheRecord jobLockCacheRecord, String environment);

    /**
     * Remove all jobs locks for a given context.
     *
     * @param context
     */
    void removeJobsLocksForContext(Context context);

    /**
     * Add a job lock cache event listener.
     *JobLockCacheEvent
     * @param listener
     */
    void addJobLockCacheEventListener(JobLockCacheEventListener listener);
}

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
     * Attempt to lock a job with the given job identifier, context name, and environment.
     *
     * @param jobIdentifier the identifier of the job to lock
     * @param contextName the name of the context the job resides in
     * @param environment the specific environment in which the job is being locked
     * @return true if the job was successfully locked, false otherwise
     */
    boolean lock(String jobIdentifier, String contextName, String environment);


    /**
     * Release a lock on a specific job within a given context and environment.
     *
     * @param jobIdentifier the identifier of the job to release the lock from
     * @param contextName the name of the context the job resides in
     * @param environment the specific environment in which the job is locked
     * @return true if the lock on the*/
    boolean release(String jobIdentifier, String contextName, String environment);


    /**
     * Checks if a job identified by its identifier, context name, and environment participates in a lock.
     *
     * @param jobIdentifier the identifier of the job
     * @param contextName the name of the context where the job resides
     * @param environment the specific environment where the job is being checked
     * @return true if the job participates in a lock, false otherwise
     */
    boolean doesJobParticipateInLock(String jobIdentifier, String contextName, String environment);


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
     * Checks if a specific job identified by its job identifier, context name, and environment has a lock.
     *
     * @param jobIdentifier the identifier of the job
     * @param contextName the name of the context where the job resides
     * @param environment the specific environment in which the job is being checked for a lock
     * @return true if the job has a lock, false otherwise
     */
    boolean hasLock(String jobIdentifier, String contextName, String environment);


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
     * Add a queued scheduler job initiation event to the job lock cache.
     *
     * @param jobIdentifier the identifier of the job
     * @param contextName the name of the context the job belongs to
     * @param event the SchedulerJobInitiationEvent containing details of the job
     * @param environment the specific environment in which the job is initiated
     */
    void addQueuedSchedulerJobInitiationEvent(String jobIdentifier, String contextName, SchedulerJobInitiationEvent event, String environment);


    /**
     * Remove a queued scheduler job for a specific scheduler job instance and environment.
     *
     * @param schedulerJobInstance The scheduler job instance for which the queued job needs to be removed
     * @param environment The specific environment in which the job is queued
     */
    void removeQueuedSchedulerJob(SchedulerJobInstance schedulerJobInstance, String environment);


    /**
     * Polls the scheduler job initiation event wait queue for contextualised scheduler job initiation events.
     *
     * @param jobIdentifier the identifier of the job
     * @param contextName the name of the context the job belongs to
     * @param environment the specific environment in which the job is initiated
     * @return a list of ContextualisedSchedulerJobInitiationEvent objects from the wait queue
     */
    List<ContextualisedSchedulerJobInitiationEvent> pollSchedulerJobInitiationEventWaitQueue(String jobIdentifier
        , String contextName, String environment);


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

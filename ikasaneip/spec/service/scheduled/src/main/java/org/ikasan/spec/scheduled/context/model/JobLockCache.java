package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.event.model.ContextualisedSchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.joblock.model.JobLockCacheRecord;
import org.ikasan.spec.scheduled.joblock.service.JobLockCacheService;

import java.io.Serializable;
import java.util.List;

public interface JobLockCache extends Serializable {

    /**
     * Add locks to the cache
     *
     * @param jobLocks
     */
    void addLocks(List<JobLock> jobLocks);

    /**
     * Lock a job based on its identifier.
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean lock(String jobIdentifier, String contextName);

    /**
     * Release a job basd upon its identifier.
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean release(String jobIdentifier, String contextName);

    /**
     * Determine if a job participates in a lock.
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean doesJobParticipateInLock(String jobIdentifier, String contextName);

    /**
     * Determine if a lock that a job is part of is currently locked.
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean locked(String jobIdentifier, String contextName);

    /**
     * Determine if a job is currently holding a lock.
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean hasLock(String jobIdentifier, String contextName);

    /**
     * Reset the job lock cache. All data will be removed from the cache!
     */
    void reset();

    /**
     * Reset an individual lock based upon its name.
     *
     * @param lockName
     * @return
     */
    boolean resetLock(String lockName);

    /**
     * Set the underlying persistence service.
     *
     * @param jobLockCacheService
     */
    void setJobLockCacheService(JobLockCacheService jobLockCacheService);

    /**
     * Add an event to a locks underlying wait queue.
     *
     * @param jobIdentifier
     * @param contextName
     * @param event
     */
    void addQueuedSchedulerJobInitiationEvent(String jobIdentifier, String contextName, SchedulerJobInitiationEvent event);

    /**
     * Get the next job in the wait queue.
     *
     * @return
     */
    ContextualisedSchedulerJobInitiationEvent pollSchedulerJobInitiationEventWaitQueue(String jobIdentifier, String contextName);

    /**
     * Set the cache record if there is one.
     *
     * @param jobLockCacheRecord
     */
    void setJobLockCacheRecord(JobLockCacheRecord jobLockCacheRecord);
}

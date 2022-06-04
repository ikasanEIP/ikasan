package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;
import java.util.List;

import org.ikasan.spec.scheduled.event.model.ContextualisedSchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.joblock.model.JobLockCacheData;
import org.ikasan.spec.scheduled.joblock.service.JobLockCacheService;

public interface JobLockCache extends Serializable {

    /**
     *
     * @param jobLocks
     */
    void addLocks(List<JobLock> jobLocks);

    /**
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean lock(String jobIdentifier, String contextName);

    /**
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean release(String jobIdentifier, String contextName);

    /**
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean doesJobParticipateInLock(String jobIdentifier, String contextName);

    /**
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean locked(String jobIdentifier, String contextName);

    /**
     *
     * @param jobIdentifier
     * @param contextName
     * @return
     */
    boolean hasLock(String jobIdentifier, String contextName);

    /**
     *
     */
    void reset();

    /**
     *
     * @param lockName
     * @return
     */
    boolean resetLock(String lockName);

    /**
     *
     * @param jobLockCacheService
     */
    void setJobLockCacheService(JobLockCacheService jobLockCacheService);

    /**
     *
     * @param jobIdentifier
     * @param contextName
     * @param event
     */
    void addQueuedSchedulerJobInitiationEvent(String jobIdentifier, String contextName, SchedulerJobInitiationEvent event);

    /**
     *
     * @return
     */
    ContextualisedSchedulerJobInitiationEvent pollSchedulerJobInitiationEventWaitQueue(String jobIdentifier, String contextName);
}

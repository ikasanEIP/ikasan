package org.ikasan.spec.scheduled.joblock.model;

import org.ikasan.spec.scheduled.context.model.JobLockHolder;
import org.ikasan.spec.scheduled.event.model.ContextualisedSchedulerJobInitiationEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public interface JobLockCacheData {

    /**
     * Get the collection that holds the lock holders by name.
     *
     * @return
     */
    ConcurrentHashMap<String, JobLockHolder> getJobLocksByLockName();

    /**
     * Set the collection that holds the lock holders by name.
     *
     * @param jobLocksByLockName
     */
    void setJobLocksByLockName(ConcurrentHashMap<String, JobLockHolder> jobLocksByLockName);

    /**
     * Get the collection that reference lock by the job identifier that they participate in.
     *
     * @return
     */
    ConcurrentHashMap<String, String> getJobLocksByIdentifier();

    /**
     * Set the collection that reference lock by the job identifier that they participate in.
     *
     * @param jobLocksByIdentifier
     */
    void setJobLocksByIdentifier(ConcurrentHashMap<String, String> jobLocksByIdentifier);

    /**
     * Get the exclusive lock event wait queue.
     *
     * @return
     */
    Queue<ContextualisedSchedulerJobInitiationEvent> getExclusiveLockSchedulerJobInitiationEventWaitQueue();

    /**
     * Set the exclusive lock event wait queue.
     *
     * @param contextualisedSchedulerJobInitiationEventQueue
     */
    void setExclusiveLockSchedulerJobInitiationEventWaitQueue(Queue<ContextualisedSchedulerJobInitiationEvent> contextualisedSchedulerJobInitiationEventQueue);

    /**
     * Get the exclusive job lock holder.
     *
     * @return
     */
    JobLockHolder getExclusiveLockHolder();

    /**
     * Set the exclusive job lock holder.
     *
     * @param jobLockHolder
     */
    void setExclusiveLockHolder(JobLockHolder jobLockHolder);
}

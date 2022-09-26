package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.event.model.ContextualisedSchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public interface JobLockHolder extends Serializable {

    /**
     * Get the job lock name.
     *
     * @return
     */
    String getLockName();

    /**
     * et the job lock name.
     *
     * @param lockName
     */
    void setLockName(String lockName);

    /**
     * Get the lock count
     *
     * @return
     */
    long getLockCount();

    /**
     * Set the lock count
     *
     * @param lockCount
     */
    void setLockCount(long lockCount);

    /**
     * Get the jobs associated with the lock
     *
     * @return
     */
    Map<String, List<SchedulerJob>> getSchedulerJobs();

    /**
     * Add jobs to the lock.
     *
     * @param contextName
     * @param jobs
     */
    void addSchedulerJobs(String contextName, List<SchedulerJob> jobs);

    /**
     * Remove all scheduler jobs for a context.
     *
     * @param context
     */
    void removeSchedulerJobsForContext(Context context);

    /**
     * Get the job identifier/s holding the lock.
     *
     * @return
     */
    Set<String> getLockHolders();

    /**
     * Set the identifier of a job holding the lock.
     *
     * @param jobIdentifier
     */
    void addLockHolder(String jobIdentifier);

    /**
     * Remove a job holding the lock based on its identifier.
     *
     * @param jobIdentifier
     * @return
     */
    boolean removeLockHolder(String jobIdentifier);

    /**
     * Get the queued jobs waiting for the lock to be released.
     *
     * @return
     */
    Queue<ContextualisedSchedulerJobInitiationEvent> getSchedulerJobInitiationEventWaitQueue();

    /**
     * Set the queued jobs waiting for the lock to be released.
     *
     * @param contextualisedSchedulerJobInitiationEventQueue
     */
    void setSchedulerJobInitiationEventWaitQueue(Queue<ContextualisedSchedulerJobInitiationEvent> contextualisedSchedulerJobInitiationEventQueue);
}

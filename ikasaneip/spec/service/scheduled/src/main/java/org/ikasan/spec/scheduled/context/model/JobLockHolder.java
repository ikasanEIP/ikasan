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
    int getLockCount();

    /**
     * Set the lock count
     *
     * @param lockCount
     */
    void setLockCount(int lockCount);

    /**
     * Add flag to indicate that a job lock is exclusive.
     *
     * What is an exclusive lock?
     *
     * If a job that is part of an exclusive lock is required to run, it will take out an exclusive
     * lock and thus prevent any other jobs that are part of any other lock or the same lock from
     * running until the job with the exclusive lock completes.
     *
     * Similarly, if a job with an exclusive lock requires an exclusive lock, it will wait until all
     * other jobs that are part of locks to complete before it can take the exclusive lock and start.
     *
     * @param isExclusiveJobLock
     */
    void setExclusiveJobLock(boolean isExclusiveJobLock);

    /**
     * Get flag to indicate if the lock is an exclusive lock.
     *
     * @return
     */
    boolean isExclusiveJobLock();

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

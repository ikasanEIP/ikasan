package org.ikasan.spec.scheduled.context.model;

import java.util.List;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.joblock.service.JobLockCacheService;

public interface JobLockCache {

    void addLocks(List<JobLock> jobLocks);

    boolean lock(String jobIdentifier, String contextId);

    boolean release(String jobIdentifier, String contextId);

    boolean locked(String jobIdentifier);

    boolean hasLock(String jobIdentifier, String contextId);

    void reset();

    boolean resetLock(String lockName);

    void setJobLockCacheService(JobLockCacheService jobLockCacheService);

    List<SchedulerJob> getJobsForIdentifier(String jobIdentifier);

    void addQueuedSchedulerJobInitiationEvent(String jobIdentifier, SchedulerJobInitiationEvent event);

    SchedulerJobInitiationEvent getNextQueuedSchedulerJobInitiationEvent(String jobIdentifierß);
}

package org.ikasan.spec.scheduled.context.model;

import java.util.List;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.joblock.service.JobLockCacheService;

public interface JobLockCache {

    void addLock(JobLock jobLock);

    void addLocks(List<JobLock> jobLocks);

    boolean lock(String jobIdentifier);

    boolean release(String jobIdentifier);

    boolean locked(String jobIdentifier);

    boolean hasLock(String jobIdentifier);

    boolean existsByIdentifier(String jobIdentifier);

    boolean existsByJobLockName(String jobLockName);

    List<SchedulerJob> getJobsForIdentifier(String jobIdentifier);

    void reset();

    boolean resetLock(String lockName);

    void setJobLockCacheService(JobLockCacheService jobLockCacheService);
}

package org.ikasan.spec.scheduled.context.model;

import java.util.List;
import java.util.Set;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

public interface JobLockHolder {
    String getLockName();

    void setLockName(String lockName);

    long getLockCount();

    void setLockCount(long lockCount);

    List<SchedulerJob> getSchedulerJobs();

    void addSchedulerJobs(List<SchedulerJob> jobs);

    Set<String> getLockHolders();

    void addLockHolder(String jobIdentifier);

    boolean removeLockHolder(String jobIdentifier);
}

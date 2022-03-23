package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface JobLock {

    void setName(String name);
    String getName();

    void setLockCount(long lockCount);
    long getLockCount();

    void setJobs(List<SchedulerJob> jobs);
    List<SchedulerJob> getJobs();
}

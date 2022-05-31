package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.List;

public interface JobLock extends Serializable {

    void setName(String name);
    String getName();

    void setLockCount(long lockCount);
    long getLockCount();

    void setJobs(List<SchedulerJob> jobs);
    List<SchedulerJob> getJobs();
}

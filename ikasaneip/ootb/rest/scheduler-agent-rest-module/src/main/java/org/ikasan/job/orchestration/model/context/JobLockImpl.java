package org.ikasan.job.orchestration.model.context;

import java.util.List;
import java.util.Map;

import org.ikasan.spec.scheduled.context.model.JobLock;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

public class JobLockImpl implements JobLock {

    private String name;
    private long lockCount = 1;
    private Map<String, List<SchedulerJob>> jobs;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setLockCount(long lockCount) {
        this.lockCount = lockCount;
    }

    @Override
    public long getLockCount() {
        return lockCount;
    }

    @Override
    public void setJobs(Map<String, List<SchedulerJob>> jobs) {
        this.jobs = jobs;
    }

    @Override
    public Map<String, List<SchedulerJob>> getJobs() {
        return jobs;
    }

}
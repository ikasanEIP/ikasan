package org.ikasan.job.orchestration.model.context;

import java.util.List;
import java.util.Map;

import org.ikasan.spec.scheduled.context.model.JobLock;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJobLockParticipant;

public class JobLockImpl implements JobLock {

    private String name;
    private long lockCount = 1;
    private Map<String, List<SchedulerJobLockParticipant>> jobs;
    private boolean exclusiveJobLock = false;

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
    public void setJobs(Map<String, List<SchedulerJobLockParticipant>> jobs) {
        this.jobs = jobs;
    }

    @Override
    public Map<String, List<SchedulerJobLockParticipant>> getJobs() {
        return jobs;
    }

    @Override
    public boolean isExclusiveJobLock() {
        return exclusiveJobLock;
    }

    @Override
    public void setExclusiveJobLock(boolean exclusiveJobLock) {
        this.exclusiveJobLock = exclusiveJobLock;
    }
}
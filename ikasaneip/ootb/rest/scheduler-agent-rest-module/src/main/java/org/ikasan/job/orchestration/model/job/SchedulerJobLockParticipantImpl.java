package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.SchedulerJobLockParticipant;

public class SchedulerJobLockParticipantImpl extends SchedulerJobImpl implements SchedulerJobLockParticipant {
    private long lockCount = 1;
    private String childContextName;

    @Override
    public String getChildContextName() {
        return childContextName;
    }

    @Override
    public void setChildContextName(String childContextName) {
        this.childContextName = childContextName;
    }

    @Override
    public long getLockCount() {
        return lockCount;
    }

    @Override
    public void setLockCount(long lockCount) {
        this.lockCount = lockCount;
    }
}

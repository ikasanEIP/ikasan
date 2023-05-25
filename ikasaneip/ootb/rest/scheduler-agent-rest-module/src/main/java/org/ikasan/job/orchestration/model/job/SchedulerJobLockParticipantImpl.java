package org.ikasan.job.orchestration.model.job;

import org.ikasan.job.orchestration.model.job.SchedulerJobImpl;
import org.ikasan.spec.scheduled.job.model.SchedulerJobLockParticipant;

public class SchedulerJobLockParticipantImpl extends SchedulerJobImpl implements SchedulerJobLockParticipant {
    private long lockCount = 1;

    @Override
    public long getLockCount() {
        return lockCount;
    }

    @Override
    public void setLockCount(long lockCount) {
        this.lockCount = lockCount;
    }
}

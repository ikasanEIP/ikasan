package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJobWrapper;

import java.util.List;

public class SchedulerJobWrapperImpl implements SchedulerJobWrapper {

    private List<SchedulerJob> schedulerJobs;

    @Override
    public List<SchedulerJob> getJobs() {
        return this.schedulerJobs;
    }

    @Override
    public void setJobs(List<SchedulerJob> schedulerJobs) {
        this.schedulerJobs = schedulerJobs;
    }
}

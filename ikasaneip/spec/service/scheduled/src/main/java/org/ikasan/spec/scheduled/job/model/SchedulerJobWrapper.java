package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface SchedulerJobWrapper {

    List<SchedulerJob> getJobs();

    void setJobs(List<SchedulerJob> schedulerJob);
}

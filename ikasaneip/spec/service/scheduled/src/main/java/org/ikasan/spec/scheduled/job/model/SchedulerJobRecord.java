package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJobRecord<T extends SchedulerJob> {
    String getId();

    String getType();

    String getAgentName();

    String getJobName();

    String getContextId();

    T getJob();

    long getTimestamp();
}

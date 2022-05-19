package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJobRecord<T extends SchedulerJob> {
    String getId();

    String getType();

    String getAgentName();

    String getJobName();

    String getContextId();

    T getJob();

    long getTimestamp();

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}

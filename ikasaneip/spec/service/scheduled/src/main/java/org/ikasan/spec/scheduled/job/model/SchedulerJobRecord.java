package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJobRecord<T extends SchedulerJob> {
    String getId();

    String getType();

    String getAgentName();

    String getJobName();

    String getContextName();

    T getJob();

    long getTimestamp();

    long getModifiedTimestamp();

//    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

//    void setModifiedBy(String modifiedBy);

    public boolean isHeld();

//    public void setHeld(boolean held);

    public boolean isSkipped();

//    public void setSkipped(boolean skipped);

    boolean isTargetResidingContextOnly();
}

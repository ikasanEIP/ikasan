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

    String getModifiedBy();

    boolean isHeld();

    boolean isSkipped();

    boolean isParticipatesInLock();

    boolean isTargetResidingContextOnly();
}

package org.ikasan.spec.scheduled.job.model;

public interface InternalEventDrivenJobRecord {

    public String getId();

    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName();

    public void setJobName(String jobName);

    String getContextName();

    void setContextName(String contextName);

    public InternalEventDrivenJob getInternalEventDrivenJob();

    public void setInternalEventDrivenJob(InternalEventDrivenJob internalEventDrivenJob);

    public long getTimestamp();

    public void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

    boolean isHeld();

    void setHeld(boolean held);

    boolean isSkipped();

    void setSkipped(boolean skipped);

    void setTargetResidingContextOnly(boolean targetResidingContextOnly);

    boolean isTargetResidingContextOnly();

    void setParticipatesInLock(boolean participatesInLock);

    boolean isParticipatesInLock();
}

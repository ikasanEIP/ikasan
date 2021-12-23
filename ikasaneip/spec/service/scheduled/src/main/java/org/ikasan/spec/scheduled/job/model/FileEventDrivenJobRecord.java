package org.ikasan.spec.scheduled.job.model;

public interface FileEventDrivenJobRecord {

    public String getId();

    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName();

    public void setJobName(String jobName);

    String getContextId();

    void setContextId(String contextId);

    public FileEventDrivenJob getFileEventDrivenJob();

    public void setFileEventDrivenJob(FileEventDrivenJob fileEventDrivenJob);

    public long getTimestamp();

    public void setTimestamp(long timestamp);
}

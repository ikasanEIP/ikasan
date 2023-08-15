package org.ikasan.spec.scheduled.job.model;

public interface FileEventDrivenJobRecord {

    public String getId();

    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName();

    public void setJobName(String jobName);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getContextName();

    void setContextName(String contextName);

    public FileEventDrivenJob getFileEventDrivenJob();

    public void setFileEventDrivenJob(FileEventDrivenJob fileEventDrivenJob);

    public long getTimestamp();

    public void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}

package org.ikasan.spec.scheduled.job.model;

public interface QuartzScheduleDrivenJobRecord {

    public String getId();

    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName();

    public void setJobName(String jobName);

    String getContextId();

    void setContextId(String contextId);

    public QuartzScheduleDrivenJob getQuartzScheduleDrivenJob();

    public void setQuartzScheduleDrivenJob(QuartzScheduleDrivenJob quartzScheduleDrivenJob);

    public long getTimestamp();

    public void setTimestamp(long timestamp);
}

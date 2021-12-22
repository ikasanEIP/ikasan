package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJob {
    String getContextId();

    void setContextId(String contextId);

    String getJobIdentifier();

    void setJobIdentifier(String jobIdentifier);

    String getAgentName();

    void setAgentName(String agentName);

    String getJobName();

    void setJobName(String jobName);

    String getJobDescription();

    void setJobDescription(String jobDescription);
}

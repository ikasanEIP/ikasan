package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJob {
    String getContextId();

    void setContextId(String contextId);

    String getIdentifier();

    void setIdentifier(String jobIdentifier);

    String getAgentName();

    void setAgentName(String agentName);

    String getJobName();

    void setJobName(String jobName);

    String getJobDescription();

    void setJobDescription(String jobDescription);

    String getStartupControlType();

    void setStartupControlType(String startupControlType);
}

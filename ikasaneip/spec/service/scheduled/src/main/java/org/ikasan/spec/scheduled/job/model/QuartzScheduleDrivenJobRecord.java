package org.ikasan.spec.scheduled.job.model;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface QuartzScheduleDrivenJobRecord {

    public String getId();

    public void setId(String id);

    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName();

    public void setJobName(String jobName);

    String getContextId();

    void setContextId(String contextId);

    public QuartzScheduleDrivenJob getQuartzScheduleDrivenJob() throws JsonProcessingException;

    public void setQuartzScheduleDrivenJob(QuartzScheduleDrivenJob quartzScheduleDrivenJob) throws JsonProcessingException;

    public long getTimestamp();

    public void setTimestamp(long timestamp);
}

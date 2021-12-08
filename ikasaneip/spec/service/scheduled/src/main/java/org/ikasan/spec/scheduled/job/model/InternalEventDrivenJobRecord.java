package org.ikasan.spec.scheduled.job.model;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface InternalEventDrivenJobRecord {

    public String getId();

    public void setId(String id);

    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName();

    public void setJobName(String jobName);

    String getContextId();

    void setContextId(String contextId);

    public InternalEventDrivenJob getInternalEventDrivenJob() throws JsonProcessingException;

    public void setInternalEventDrivenJob(InternalEventDrivenJob internalEventDrivenJob) throws JsonProcessingException;

    public long getTimestamp();

    public void setTimestamp(long timestamp);
}

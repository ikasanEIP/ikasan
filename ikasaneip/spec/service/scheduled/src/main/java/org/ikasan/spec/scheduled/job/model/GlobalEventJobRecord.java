package org.ikasan.spec.scheduled.job.model;

public interface GlobalEventJobRecord {

    String getId();

    String getAgentName();

    void setAgentName(String agentName);

    String getJobName();

    void setJobName(String jobName);

    String getContextName();

    void setContextName(String contextName);

    GlobalEventJob getGlobalEventJob();

    void  setGlobalEventJob(GlobalEventJob globalEventJob);

    long getTimestamp();

    void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

}

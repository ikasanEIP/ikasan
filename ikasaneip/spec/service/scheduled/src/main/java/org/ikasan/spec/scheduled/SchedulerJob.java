package org.ikasan.spec.scheduled;

public interface SchedulerJob {

    public String getJobIdentifier();

    public void setJobIdentifier(String jobIdentifier);

    public String getAgentName();

    public void setAgentName(String agentName);

    public String getJobName();

    public void setJobName(String jobName);
}

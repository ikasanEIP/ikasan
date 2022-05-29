package org.ikasan.spec.scheduled.instance.model;

public interface SchedulerJobInstanceSearchFilter {
    String getJobName();

    public void setJobName(String jobName);

    String getJobType();

    public void setJobType(String jobType);

    String getContextName();

    public void setContextName(String contextName);

    public String getContextInstanceId();

    public void setContextInstanceId(String contextInstanceId);

    public String getStatus();

    public void setStatus(String status);
}

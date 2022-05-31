package org.ikasan.spec.scheduled.instance.model;

public interface SchedulerJobInstanceSearchFilter {
    String getJobName();

    void setJobName(String jobName);

    String getJobType();

    void setJobType(String jobType);

    String getContextName();

    void setContextName(String contextName);

    String getContextInstanceId();

    void setContextInstanceId(String contextInstanceId);

    String getChildContextName();

    void setChildContextName(String childContextName);

    String getStatus();

    void setStatus(String status);
}

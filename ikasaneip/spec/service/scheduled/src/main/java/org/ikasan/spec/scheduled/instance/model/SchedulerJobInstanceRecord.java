package org.ikasan.spec.scheduled.instance.model;

public interface SchedulerJobInstanceRecord {

    String getId();

    String getType();

    String getJobName();

    void setJobName(String jobName);

    String getContextName();

    void setContextName(String contextName);

    String getChildContextName();

    void setChildContextName(String childContextName);

    String getContextInstanceId();

    void setContextInstanceId(String contextInstanceId);

    SchedulerJobInstance getSchedulerJobInstance();

    void setSchedulerJobInstance(SchedulerJobInstance schedulerJobInstance);

    String getStatus();

    void setStatus(String status);

    long getTimestamp();

    void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}

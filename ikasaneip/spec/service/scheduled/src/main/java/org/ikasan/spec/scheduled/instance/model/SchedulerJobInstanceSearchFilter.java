package org.ikasan.spec.scheduled.instance.model;

public interface SchedulerJobInstanceSearchFilter {
    String getJobName();

    void setJobName(String jobName);

    String getDisplayNameFilter();

    void setDisplayNameFilter(String displayNameFilter);

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

    void setTargetResidingContextOnly(Boolean targetResidingContextOnly);

    Boolean isTargetResidingContextOnly();

    void setParticipatesInLock(Boolean participatesInLock);

    Boolean isParticipatesInLock();

    long getStartTimeWindowStart();

    void setStartTimeWindowStart(long startTimeWindowStart);

    long getStartTimeWindowEnd();

    void setStartTimeWindowEnd(long startTimeWindowEnd);

    long getEndTimeWindowStart();

    void setEndTimeWindowStart(long endTimeWindowStart);

    long getEndTimeWindowEnd();

    void setEndTimeWindowEnd(long endTimeWindowEnd);
}

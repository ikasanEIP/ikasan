package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.context.model.Context;

import java.util.Map;

public interface ContextInstance extends Context<ContextInstance, ContextParameterInstance, SchedulerJobInstance>, StatefulEntity {
    
    String getId();

    void setId(String id);

    long getCreatedDateTime();

    void setCreatedDateTime(long createdDateTime);

    long getUpdatedDateTime();

    void setUpdatedDateTime(long updatedDateTime);

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    String getTimezone();

    void setTimezone(String timezone);

    Map<String, String> getLockHolders();

    void setLockHolders(Map<String, String> lockHolders);

    Map<String, String> getHeldJobs();

    void setHeldJobs(Map<String, String> heldJobs);
}

package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.context.model.Context;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

import java.util.Map;

public interface ContextInstance extends Context<ContextInstance, ContextParameterInstance, SchedulerJobInstance, JobLockInstance>, StatefulEntity {
    
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

    Map<String, SchedulerJobInitiationEvent> getHeldJobs();

    void setHeldJobs(Map<String, SchedulerJobInitiationEvent> heldJobs);
}

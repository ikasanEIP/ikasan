package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;

public interface SchedulerJobInstance extends SchedulerJob, StatefulEntity, Serializable {

    String getContextInstanceId();

    void setContextInstanceId(String contextInstanceId);

    String getChildContextName();

    void setChildContextName(String childContextName);

    boolean isHeld();

    void setHeld(boolean held);

    boolean isInitiationEventRaised();

    void setInitiationEventRaised(boolean initiationEventRaised) ;

    InstanceStatus getStatus();

    void setStatus(InstanceStatus status);

    ScheduledProcessEvent getScheduledProcessEvent();

    void setScheduledProcessEvent(ScheduledProcessEvent scheduledProcessEvent) ;
}

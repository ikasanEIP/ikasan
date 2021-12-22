package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

interface SchedulerJobInstance extends SchedulerJob {

    boolean isHeld();

    void setHeld(boolean held);

    boolean isSkip();

    void setSkip(boolean skip);

    boolean isInitiationEventRaised();

    void setInitiationEventRaised(boolean initiationEventRaised) ;

    InstanceStatus getStatus();

    void setStatus(InstanceStatus status);

    ScheduledProcessEvent getScheduledProcessEvent();

    void setScheduledProcessEvent(ScheduledProcessEvent scheduledProcessEvent) ;
}

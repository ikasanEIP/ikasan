package org.ikasan.spec.scheduled.event.model;

public interface ContextualisedSchedulerJobInitiationEvent {

    void setSchedulerJobInitiationEvent(SchedulerJobInitiationEvent schedulerJobInitiationEvent);

    SchedulerJobInitiationEvent getSchedulerJobInitiationEvent();

    void setContextName(String contextName);

    String getContextName();

}

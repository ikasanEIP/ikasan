package org.ikasan.spec.scheduled.instance.model;

import java.io.Serializable;
import java.util.List;

import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface ScheduledContextInstanceAudit extends Serializable {
    ContextualisedScheduledProcessEvent getProcessEvent();

    void setProcessEvent(ContextualisedScheduledProcessEvent contextualisedScheduledProcessEvent);

    List<SchedulerJobInitiationEvent> getSchedulerJobInitiationEvents();

    void setSchedulerJobInitiationEvents(List<SchedulerJobInitiationEvent> schedulerJobInitiationEvents);

    ContextInstance getPreviousContextInstance();

    void setPreviousContextInstance(ContextInstance previousContext);

    ContextInstance getUpdatedContextInstance();

    void setUpdatedContextInstance(ContextInstance updatedContext);
}

package org.ikasan.spec.scheduled.instance.model;

import java.io.Serializable;
import java.util.List;

import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface ScheduledContextInstanceAuditAggregate extends Serializable {
    ContextualisedScheduledProcessEvent getProcessEvent();

    void setProcessEvent(ContextualisedScheduledProcessEvent contextualisedScheduledProcessEvent);

    List<SchedulerJobInitiationEvent> getSchedulerJobInitiationEvents();

    void setSchedulerJobInitiationEvents(List<SchedulerJobInitiationEvent> schedulerJobInitiationEvents);

    String getPreviousContextInstanceAuditId();

    void setPreviousContextInstanceAuditId(String previousContextInstanceAuditId);

    String getUpdatedContextInstanceAuditId();

    void setUpdatedContextInstanceAuditId(String updatedContextInstanceAuditId);
}

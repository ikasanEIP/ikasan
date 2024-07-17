package org.ikasan.spec.scheduled.instance.model;

import java.io.Serializable;
import java.util.List;

import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface ScheduledContextInstanceAuditAggregate extends Serializable {
    /**
     * Retrieves the ContextualisedScheduledProcessEvent associated with the current instance.
     *
     * @return The ContextualisedScheduledProcessEvent associated with the current instance.
     */
    ContextualisedScheduledProcessEvent getProcessEvent();

    /**
     * Sets the process event for the given context.
     *
     * @param contextualisedScheduledProcessEvent the process event to set
     */
    void setProcessEvent(ContextualisedScheduledProcessEvent contextualisedScheduledProcessEvent);

    /**
     *
     */
    List<SchedulerJobInitiationEvent> getSchedulerJobInitiationEvents();

    /**
     * Sets the list of SchedulerJobInitiationEvents.
     *
     * @param schedulerJobInitiationEvents The list of SchedulerJobInitiationEvents to set.
     */
    void setSchedulerJobInitiationEvents(List<SchedulerJobInitiationEvent> schedulerJobInitiationEvents);

    /**
     * Get the previous context instance audit ID.
     *
     * @return the previous context instance audit ID
     */
    String getPreviousContextInstanceAuditId();

    /**
     * Sets the previous context instance audit ID.
     *
     * @param previousContextInstanceAuditId The previous context instance audit ID to set.
     */
    void setPreviousContextInstanceAuditId(String previousContextInstanceAuditId);

    /**
     * Retrieves the updated context instance audit ID.
     *
     * @return The updated context instance audit ID as a string.
     */
    String getUpdatedContextInstanceAuditId();

    /**
     * Sets the updated context instance audit ID.
     *
     * @param updatedContextInstanceAuditId the new value for the updated context instance audit ID
     */
    void setUpdatedContextInstanceAuditId(String updatedContextInstanceAuditId);
}

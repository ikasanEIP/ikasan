package org.ikasan.spec.scheduled.instance.model;

/**
 * Represents a filter used to search for ScheduledContextInstanceAuditAggregateRecord objects.
 */
public class ScheduledContextInstanceAuditAggregateSearchFilter {
    private String contextName;
    private String contextInstanceId;
    private String scheduledProcessEventName;
    private String raisedInitiationEventName;
    public String status;

    /**
     * Retrieves the name of the context associated with this instance.
     *
     * @return The name of the context.
     */
    public String getContextName() {
        return contextName;
    }

    /**
     * Sets the name of the context associated with this instance.
     *
     * @param contextName The name of the context.
     */
    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    /**
     * Retrieves the context instance ID associated with this instance.
     *
     * @return The context instance ID.
     */
    public String getContextInstanceId() {
        return contextInstanceId;
    }

    /**
     * Sets the context instance ID associated with this instance.
     *
     * @param contextInstanceId The context instance ID.
     */
    public void setContextInstanceId(String contextInstanceId) {
        this.contextInstanceId = contextInstanceId;
    }

    /**
     * Retrieves the name of the scheduled process event associated with this instance.
     *
     * @return The name of the scheduled process event.
     */
    public String getScheduledProcessEventName() {
        return scheduledProcessEventName;
    }

    /**
     * Sets the name of the scheduled process event associated with this instance.
     *
     * @param scheduledProcessEventName The name of the scheduled process event.
     */
    public void setScheduledProcessEventName(String scheduledProcessEventName) {
        this.scheduledProcessEventName = scheduledProcessEventName;
    }

    /**
     * Retrieves the name of the raised initiation event associated with this instance.
     *
     * @return The name of the raised initiation event.
     */
    public String getRaisedInitiationEventName() {
        return raisedInitiationEventName;
    }

    /**
     * Sets the name of the raised initiation event associated with this instance.
     *
     * @param raisedInitiationEventName The name of the raised initiation event.
     */
    public void setRaisedInitiationEventName(String raisedInitiationEventName) {
        this.raisedInitiationEventName = raisedInitiationEventName;
    }

    /**
     * Retrieves the current status.
     *
     * @return the current status as a String.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the object.
     *
     * @param status the status to be set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}

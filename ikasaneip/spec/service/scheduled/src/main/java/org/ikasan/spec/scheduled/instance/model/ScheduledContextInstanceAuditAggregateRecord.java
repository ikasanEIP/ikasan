package org.ikasan.spec.scheduled.instance.model;

/**
 * Interface representing a ScheduledContextInstanceAuditAggregateRecord.
 */
public interface ScheduledContextInstanceAuditAggregateRecord {

    /**
     * Retrieves the ID associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @return The ID of the object.
     */
    String getId();

    /**
     * Retrieves the context name associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @return The context name of the object.
     */
    String getContextName();

    /**
     * Sets the context name for this instance of ScheduledContextInstanceAuditAggregateRecord.
     *
     * @param contextName the context name to be set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the context instance ID associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @return The context instance ID of the object.
     */
    String getContextInstanceId();

    /**
     * Sets the context instance ID associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @param contextInstanceId The context instance ID to be set.
     */
    void setContextInstanceId(String contextInstanceId);

    /**
     * Retrieves the scheduled process event name.
     *
     * @return The scheduled process event name.
     */
    String getScheduledProcessEventName();

    /**
     * Sets the name of the scheduled process event.
     *
     * @param scheduledProcessEventName The name of the scheduled process event to be set.
     */
    void setScheduledProcessEventName(String scheduledProcessEventName);

    /**
     * Retrieves the raised events associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @return The raised events as a String.
     */
    String getRaisedEvents();

    /**
     * Retrieves the ScheduledContextInstanceAuditAggregate.
     *
     * @return The ScheduledContextInstanceAuditAggregate object.
     */
    ScheduledContextInstanceAuditAggregate getScheduledContextInstanceAuditAggregate();

    /**
     * Sets the ScheduledContextInstanceAuditAggregate for the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @param scheduledContextInstanceAudit The ScheduledContextInstanceAuditAggregate object to be set.
     */
    void setScheduledContextInstanceAuditAggregate(ScheduledContextInstanceAuditAggregate scheduledContextInstanceAudit);

    /**
     * Retrieves the timestamp representing the current time in milliseconds since the epoch.
     *
     * @return the current timestamp in milliseconds.
     */
    long getTimestamp();

    /**
     * Retrieves the status of the job associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @return The status of the object as a String.
     */
    String getStatus();

    /**
     * Sets the status of the job associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @param status the new status to set
     */
    void setStatus(String status);

    /**
     * Checks if the job is a repeating job.
     *
     * @return true if the job is a repeating job, false otherwise
     */
    boolean isRepeatingJob();

    /**
     * This method is used to set whether a job is repeating or not.
     *
     * @param repeatingJob a boolean value indicating if the job is repeating or not.
     */
    void setRepeatingJob(boolean repeatingJob);


    /**
     * Retrieves the job type associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @return The job type as a String.
     */
    String getJobType();

    /**
     * Sets the job type associated with the ScheduledContextInstanceAuditAggregateRecord.
     *
     * @param jobType The job type to be set as a String.
     */
    void setJobType(String jobType);
}

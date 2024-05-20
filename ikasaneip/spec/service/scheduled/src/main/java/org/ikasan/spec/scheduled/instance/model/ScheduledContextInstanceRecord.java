package org.ikasan.spec.scheduled.instance.model;

public interface ScheduledContextInstanceRecord {

    /**
     * Get the id
     * @return
     */
    String getId();

    /**
     * Get the context name
     *
     * @return
     */
    String getContextName();

    /**
     * Set the context name
     *
     * @param contextName
     */
    void setContextName(String contextName);

    /**
     * Get the context instance id.
     *
     * @return
     */
    String getContextInstanceId();

    /**
     * Set the context instance id.
     * @param contextInstanceId
     */
    void setContextInstanceId(String contextInstanceId);

    /**
     * Get the context instance.
     * @return
     */
    ContextInstance getContextInstance();

    /**
     * Set the context instance.
     * @param context
     */
    void setContextInstance(ContextInstance context);

    /**
     * Get the status
     *
     * @return
     */
    String getStatus();

    /**
     * Set the status.
     *
     * @param status
     */
    void setStatus(String status);

    /**
     * Get the timestamp.
     *
     * @return
     */
    long getTimestamp();

    /**
     * Set the timestamp.
     *
     * @param timestamp
     */
    void setTimestamp(long timestamp);

    /**
     * Get the modified timestamp.
     *
     * @return
     */
    long getModifiedTimestamp();

    /**
     * Set the modified timestamp.
     *
     * @param timestamp
     */
    void setModifiedTimestamp(long timestamp);

    /**
     * Get the start time.
     *
     * @return
     */
    long getStartTime();

    /**
     * Set the start time.
     */
    void setStartTime(long endTime);

    /**
     * Get the end time.
     *
     * @return
     */
    long getEndTime();

    /**
     * Set the end time.
     */
    void setEndTime(long endTime);

    /**
     * Checks if the ScheduledContextInstanceRecord contains any repeating jobs.
     *
     * @return true if the ScheduledContextInstanceRecord contains repeating jobs, false otherwise.
     */
    boolean isContainsRepeatingJobs();

    /**
     * Sets whether the ScheduledContextInstanceRecord contains any repeating jobs.
     *
     * @param containsRepeatingJobs true if the ScheduledContextInstanceRecord contains repeating jobs, false otherwise.
     */
    void setContainsRepeatingJobs(boolean containsRepeatingJobs);

    /**
     * Get the modified by.
     *
     * @return
     */
    String getModifiedBy();

    /**
     * Set the modified by.
     * @param modifiedBy
     */
    void setModifiedBy(String modifiedBy);
}

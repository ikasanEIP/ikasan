package org.ikasan.spec.scheduled.instance.model;

import java.util.List;

public interface ContextInstanceSearchFilter {
    /**
     * Returns the context search filter.
     *
     * @return The context search filter as a String.
     */
    String getContextSearchFilter();

    /**
     * Sets the context search filter for the ContextInstanceSearchFilter.
     *
     * @param contextSearchFilter the context search filter to be set
     */
    public void setContextSearchFilter(String contextSearchFilter);

    /**
     * Returns a list of context instance names.
     *
     * @return A list of strings representing context instance names.
     */
    List<String> getContextInstanceNames();

    /**
     * Sets the list of context instance names to be used as search criteria.
     *
     * @param contextInstanceNames the list of context instance names
     */
    public void setContextInstanceNames(List<String> contextInstanceNames);

    /**
     * Retrieves the context instance ID.
     *
     * @return The context instance ID.
     */
    public String getContextInstanceId();

    /**
     * Sets the context instance ID.
     *
     * @param contextInstanceId The ID of the context instance to set.
     */
    public void setContextInstanceId(String contextInstanceId);

    /**
     * Retrieves the timestamp when the instance was created.
     *
     * @return The timestamp when the instance was created.
     */
    public long getCreatedTimestamp();

    /**
     * Sets the timestamp when the instance was created.
     *
     * @param createdTimestamp the timestamp when the instance was created
     */
    public void setCreatedTimestamp(long createdTimestamp);

    /**
     * Returns the modified timestamp of the object.
     *
     * @return The modified timestamp of the object.
     */
    public long getModifiedTimestamp();

    /**
     * Sets the modified timestamp of the context instance search filter.
     *
     * @param modifiedTimestamp the modified timestamp to set
     */
    public void setModifiedTimestamp(long modifiedTimestamp);

    /**
     * Retrieves the start time of the context instance search filter.
     *
     * @return the start time of the context instance search filter
     */
    public long getStartTime();

    /**
     * Sets the start time of the context instance search filter.
     * The start time is represented by a Unix timestamp.
     * This method updates the value of the start time in the context instance search filter.
     *
     * @param timestamp the start time to set, represented as a Unix timestamp
     */
    public void setStartTime(long timestamp);

    /**
     * Gets the start time range filter for the ContextInstanceSearchFilter.
     *
     * @return The start time start value.
     */
    public long getStartTimeStart();

    /**
     * Sets the start time start parameter for filtering the search results.
     * This parameter represents the lower bound of the start time range.
     *
     * @param timestamp the start time start parameter to set
     */
    public void setStartTimeStart(long timestamp);

    /**
     * Gets the end time range for filtering the context instances.
     *
     * @return The end time range for filtering the context instances.
     */
    public long getStartTimeEnd();

    /**
     * Sets the end timestamp for the start time criteria in the context instance search filter.
     *
     * @param timestamp The end timestamp for the start time criteria
     */
    public void setStartTimeEnd(long timestamp);

    /**
     * Returns the end time.
     *
     * @return the end time as a long value.
     */
    public long getEndTime();

    /**
     * Sets the end time of the context instance search filter.
     *
     * @param timestamp the new end time
     */
    public void setEndTime(long timestamp);

    /**
     * Retrieves the start time of the end time range for filtering the search results.
     *
     * @return The start time of the end time range.
     */
    public long getEndTimeStart();

    /**
     * Sets the start time for filtering the search results based on the end time.
     *
     * @param timestamp the start time for filtering
     */
    public void setEndTimeStart(long timestamp);

    /**
     * Retrieves the end time end value of the search filter for context instances.
     *
     * @return The end time end value of the search filter.
     */
    public long getEndTimeEnd();

    /**
     * Sets the end time constraint for the search filter.
     *
     * @param timestamp The timestamp to set as the end time constraint.
     */
    public void setEndTimeEnd(long timestamp);

    /**
     * Retrieves the status of the object.
     *
     * @return The status of the object.
     */
    public String getStatus();

    /**
     * Sets the status of the object.
     *
     * @param status the new status to be set
     */
    public void setStatus(String status);
}

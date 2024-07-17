package org.ikasan.spec.scheduled.instance.model;

public interface SchedulerJobInstanceSearchFilter {
    /**
     *
     */
    String getJobName();

    /**
     *
     */
    void setJobName(String jobName);

    /**
     * Determines whether to include start and terminal jobs in the search results.
     *
     * @return true if start and terminal jobs should be included in the search results, false otherwise
     */
    boolean includeStartAndTerminalJobsInSearchResults();

    /**
     * Sets whether to include start and terminal jobs in search results.
     *
     * @param includeStartAndTerminalJobsInSearchResults {@code true} to include start and terminal jobs in search results,
     *                                                     {@code false} otherwise.
     */
    void setIncludeStartAndTerminalJobsInSearchResults(boolean includeStartAndTerminalJobsInSearchResults);

    /**
     * Retrieves the display name filter for the scheduler job instance search.
     *
     * @return The display name filter for the search.
     */
    String getDisplayNameFilter();

    /**
     * Sets the display name filter to be used in the search.
     *
     * @param displayNameFilter the filter to be applied to the display name of the job instances
     */
    void setDisplayNameFilter(String displayNameFilter);

    /**
     * Retrieves the job type for the scheduler job instance search filter.
     *
     * @return The job type as a string.
     */
    String getJobType();

    /**
     * Sets the job type to filter jobs by.
     *
     * @param jobType the job type to filter jobs by
     */
    void setJobType(String jobType);

    /**
     * Returns the context name.
     *
     * @return the context name
     */
    String getContextName();

    /**
     * Sets the context name for the SchedulerJobInstanceSearchFilter.
     * This method sets the value of the context name attribute in the SchedulerJobInstanceSearchFilter.
     * The context name is used to filter the search results based on the context name of the job instances.
     *
     * @param contextName the context name to set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the context instance ID.
     *
     * @return The context instance ID.
     */
    String getContextInstanceId();

    /**
     * Sets the context instance ID for the search filter.
     *
     * @param contextInstanceId The context instance ID to set.
     */
    void setContextInstanceId(String contextInstanceId);

    /**
     * Get the name of the child context.
     *
     * @return the name of the child context
     */
    String getChildContextName();

    /**
     * Sets the child context name for the scheduler job instance search filter.
     *
     * @param childContextName the child context name to be set
     */
    void setChildContextName(String childContextName);

    /**
     *
     */
    String getStatus();

    /**
     *
     */
    void setStatus(String status);

    /**
     * Sets the flag indicating whether the search results should only include job instances residing in the target context.
     *
     * @param targetResidingContextOnly the flag indicating whether the search results should only include job instances residing in the target context
     */
    void setTargetResidingContextOnly(Boolean targetResidingContextOnly);

    /**
     * Checks if the target is residing context only.
     *
     * @return true if the target is residing context only, false otherwise
     */
    Boolean isTargetResidingContextOnly();

    /**
     * Sets whether the job instance participates in a lock.
     *
     * @param participatesInLock true if the job instance participates in a lock, false otherwise
     */
    void setParticipatesInLock(Boolean participatesInLock);

    /**
     * Checks if the job instance participates in a lock.
     *
     * @return true if the job instance participates in a lock, false otherwise.
     */
    Boolean isParticipatesInLock();

    /**
     * Retrieves the start time window start value of the SchedulerJobInstanceSearchFilter.
     *
     * @return the start time window start value
     */
    long getStartTimeWindowStart();

    /**
     * Set the start time window start value for the search filters of the SchedulerJobInstance.
     *
     * @param startTimeWindowStart the start time window start value to be set
     */
    void setStartTimeWindowStart(long startTimeWindowStart);

    /**
     * Returns the end time window for the start time of the scheduler job instance search filter.
     * The end time window defines the upper limit of the range for the start time of the scheduler job instance to be included in the search results.
     *
     * @return The end time window for the start time of the scheduler job instance search filter.
     */
    long getStartTimeWindowEnd();

    /**
     * Sets the end time window for the start time of the scheduler job instance search filter.
     *
     * @param startTimeWindowEnd the end time window for the start time, in milliseconds since the epoch
     */
    void setStartTimeWindowEnd(long startTimeWindowEnd);

    /**
     * Returns the starting time of the end time window.
     *
     * @return The starting time of the end time window.
     */
    long getEndTimeWindowStart();

    /**
     * Sets the start time for the end time window filter in the search criteria.
     * The end time window is a range of time within which the end time of the job instance should fall.
     *
     * @param endTimeWindowStart the start time of the end time window
     */
    void setEndTimeWindowStart(long endTimeWindowStart);

    /**
     * Returns the ending time window for the scheduler job instance search filter.
     *
     * @return the ending time window as a long value
     */
    long getEndTimeWindowEnd();

    /**
     * Sets the end time window for filtering scheduler job instances based on their end time.
     *
     * @param endTimeWindowEnd the end time window value to set
     */
    void setEndTimeWindowEnd(long endTimeWindowEnd);
}

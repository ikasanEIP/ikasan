package org.ikasan.spec.scheduled.job.model;

import java.util.List;

public interface SchedulerJobSearchFilter {

    /**
     * Gets the job name filter used for searching jobs.
     *
     * @return the job name filter
     */
    String getJobNameFilter();

    /**
     * Sets the job name filter to be used for searching job entries.
     *
     * @param jobNameFilter the job name filter to apply
     */
    void setJobNameFilter(String jobNameFilter);

    /**
     * Returns the display name filter used for searching scheduler jobs.
     * The display name filter is a string used to filter scheduler jobs
     * based on their display name.
     *
     * @return the display name filter string
     */
    String getDisplayNameFilter();

    /**
     * Sets the display name filter used for filtering search results based on display name.
     *
     * @param displayNameFilter the display name filter to be applied
     */
    void setDisplayNameFilter(String displayNameFilter);

    /**
     * Retrieves the list of job names that should not be included in the filter.
     *
     * @return A list of job names that are excluded from the filter.
     */
    List<String> getNotJobNameInFilter();

    /**
     * Sets the list of job names that should not be included in the filter.
     *
     * @param notJobNameInFilter a List of job names to be excluded from the filter
     */
    void setNotJobNameInFilter(List<String> notJobNameInFilter);

    /**
     * Get the job type filter that is used in searching for scheduler jobs.
     *
     * @return The job type filter as a String
     */
    String getJobTypeFilter();

    /**
     * Sets the job type filter for searching.
     *
     * @param jobTypeFilter The job type filter to apply for searching. Only jobs of the specified type will
     *                      be included in the search results.
     */
    void setJobTypeFilter(String jobTypeFilter);

    /**
     * Returns the search filter for the context associated with the Scheduler job.
     * This filter is used to constrain the search to specific context names.
     *
     * @return The context search filter as a String.
     */
    String getContextSearchFilter();

    /**
     * Set the context search filter used for searching within specific contexts.
     *
     * @param contextSearchFilter The filter string to be applied to search within specific contexts.
     */
    void setContextSearchFilter(String contextSearchFilter);

    /**
     * Get the list of context names that the search will be
     * constrained to.
     *
     * @return
     */
    List<String> getContextNames();

    /**
     * Set the list of context names that the search will be
     * constrained to.
     *
     * @param contextNames
     */
    void setContextNames(List<String> contextNames);

    /**
     * Retrieves the list of Job types available.
     *
     * @return List<String> representing the Job types
     */
    List<String> getJobTypes();


    /**
     * Sets the list of job types to be used for filtering search results based on job types.
     *
     * @param jobTypes the list of job types to be set
     */
    void setJobTypes(List<String> jobTypes);

    /**
     * Checks whether the SchedulerJobSearchFilter object is currently marked as held.
     *
     * @return true if the object is held, false otherwise
     */
    boolean isHeld();

    /**
     * Set whether the job is currently being held.
     *
     * @param held true if the job is held, false otherwise
     */
    void setHeld(boolean held);

    /**
     * Checks if the job is marked as skipped.
     *
     * @return true if the job is skipped, false otherwise
     */
    boolean isSkipped();

    /**
     * Sets whether the job entry is marked as skipped.
     * @param skipped true to indicate the entry has been skipped, otherwise false
     */
    void setSkipped(boolean skipped);

    /**
     * Sets the status of the object.
     *
     * @param status The new status to be set for the object.
     */
    void setStatus(String status);

    /**
     * Sets whether the target is residing only in the context.
     *
     * @param targetResidingContextOnly a boolean indicating if the target is residing only in the context
     */
    void setTargetResidingContextOnly(Boolean targetResidingContextOnly);

    /**
     * Determines if the target is residing in the context only.
     *
     * @return true if the target is residing in the context only, false otherwise
     */
    Boolean isTargetResidingContextOnly();

    /**
     * Sets whether the object participates in a locking mechanism.
     *
     * @param participatesInLock a boolean value indicating whether the object participates in a lock
     */
    void setParticipatesInLock(Boolean participatesInLock);

    /**
     * Checks if the object participates in a lock operation.
     *
     * @return true if the object participates in a lock operation, false otherwise
     */
    Boolean isParticipatesInLock();
}

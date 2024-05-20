package org.ikasan.spec.scheduled.instance.model;

import java.util.Map;

public interface ContextInstanceAggregateJobStatus {

    /**
     * Get the context instance id that the jobs status is being queried for.
     *
     * @return
     */
    String getContextInstanceId();

    /**
     * Get the context instance name that the status is being queried for.
     *
     * @return
     */
    String getContextInstanceName();

    /**
     * Get the job count for a given status.
     *
     * @param instanceStatus
     * @return
     */
    int getStatusCount(InstanceStatus instanceStatus);

    /**
     * Checks if the ContextInstanceAggregateJobStatus contains any repeatable jobs.
     *
     * @return true if the ContextInstanceAggregateJobStatus contains repeatable jobs, false otherwise.
     */
    boolean containsRepeatableJobs();

    /**
     * Sets whether the ContextInstanceAggregateJobStatus contains any repeatable jobs.
     *
     * @param containsRepeatableJobs true if the ContextInstanceAggregateJobStatus contains repeatable jobs, false otherwise
     */
    void setContainsRepeatableJobs(boolean containsRepeatableJobs);

    /**
     * Returns the count of repeating job instances with the given instance status.
     *
     * @param instanceStatus the instance status to count
     * @return the count of repeating job instances with the given instance status
     */
    int repeatingJobInstanceStatusCount(InstanceStatus instanceStatus);

    /**
     * Sets the counts for each instance status of repeating jobs.
     * The counts are passed as a Map, where the key is the instance status and the value is the count of repeating
     * jobs with that status.
     *
     * Example usage:
     *     Map<String, Integer> statusCounts = new HashMap<>();
     *     statusCounts.put("ERROR", 5);
     *     statusCounts.put("COMPLETE", 10);
     *     setRepeatingJobsStatusCounts(statusCounts);
     *
     * @param repeatingJobsStatusCounts a Map containing the counts for each instance status of repeating jobs
     *                                 where the key is the instance status and the value is the count of repeating jobs with that status
     */
    void setRepeatingJobsStatusCounts(Map<String, Integer> repeatingJobsStatusCounts);
}

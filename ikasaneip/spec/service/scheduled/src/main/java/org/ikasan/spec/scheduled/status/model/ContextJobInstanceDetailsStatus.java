package org.ikasan.spec.scheduled.status.model;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

import java.util.Set;

public interface ContextJobInstanceDetailsStatus {

    /**
     * Retrieves the name of the job.
     *
     * @return the name of the job as a string.
     */
    String getJobName();
    /**
     * Sets the name of the job.
     *
     * @param jobName the name of the job to be set
     */
    void setJobName(String jobName);
    /**
     * Retrieves the set of child context names associated with the job instance details status.
     *
     * @return the set of child context names as a Set of Strings
     */
    Set<String> getChildContextName();
    /**
     * Sets the child context names for the job.
     *
     * @param childContextName a Set of String representing the child context names for the job
     */
    void setChildContextName(Set<String> childContextName);
    /**
     * Retrieves the status of the instance.
     *
     * @return the status of the instance as an InstanceStatus enum value.
     */
    InstanceStatus getInstanceStatus();
    /**
     * Sets the instance status of a job.
     *
     * @param instanceStatus the instance status to be set
     */
    void setInstanceStatus(InstanceStatus instanceStatus);
    /**
     * Checks if the target residing context is the only context for the job.
     *
     * @return {@code true} if the target residing context is the only context for the job,
     *         {@code false} otherwise.
     */
    boolean isTargetResidingContextOnly();
    /**
     * Sets the flag indicating if the job should only target the residing context.
     * If set to true, the job will only exist if it already exists in the residing context,
     * otherwise it will be skipped. If set to false, the job will be created in the residing context
     * if it does not already exist.
     *
     * @param targetResidingContextOnly the flag indicating if the job should only target the residing context
     */
    void setTargetResidingContextOnly(boolean targetResidingContextOnly);
    /**
     * Retrieves the start time of the job instance.
     *
     * @return the start time of the job instance as a long value representing the number of milliseconds since the Unix epoch.
     */
    long getStartTime();
    /**
     * Sets the start time of the job instance.
     *
     * @param startTime the start time of the job instance as a long value representing the number of milliseconds since the epoch
     */
    void setStartTime(long startTime);
    /**
     * Retrieves the end time of the job instance.
     *
     * @return the end time of the job instance as a long value.
     */
    long getEndTime();
    /**
     * Sets the end time of the job.
     *
     * @param endTime the end time of the job as a long value
     */
    void setEndTime(long endTime);

    /**
     * Determines if the error for the job instance has been acknowledged.
     *
     * @return true if the error has been acknowledged, false otherwise.
     */
    boolean isErrorAcknowledged();
    
    /**
     * Sets the flag indicating if the error for the job instance has been acknowledged.
     *
     * @param isAcked the flag indicating if the error has been acknowledged
     */
    void setErrorAcknowledged(boolean isAcked);

    /**
     * Check if the job already exist if targetResidingContextOnly = true.
     * helper method to add into the childContextName.
     *
     * Not required to implement for POJO for model.
     * @param jobName name of job
     * @return true if already existing, false if not
     */
    boolean checkExist(String jobName);
}

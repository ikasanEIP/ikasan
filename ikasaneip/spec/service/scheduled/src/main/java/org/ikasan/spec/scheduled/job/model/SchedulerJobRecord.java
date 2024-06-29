package org.ikasan.spec.scheduled.job.model;

public interface SchedulerJobRecord<T extends SchedulerJob> {
    /**
     * Retrieves the ID associated with this object.
     *
     * @return The ID of the object.
     */
    String getId();

    /**
     * Retrieves the type of the SchedulerJobRecord.
     * The type is determined by concatenating the job name and context name with an underscore.
     *
     * @return the type of the SchedulerJobRecord
     */
    String getType();

    /**
     * Retrieves the name of the agent that this job runs on.
     *
     * @return The agent name for the job.
     */
    String getAgentName();

    /**
     * Retrieves the name of a job.
     *
     * @return the name of the job as a string.
     */
    String getJobName();

    /**
     * Retrieves the display name of the job.
     *
     * @return The display name of the job.
     */
    String getDisplayName();

    /**
     * Retrieves the parent context id that this job belongs to.
     *
     * @return The parent context id as a string.
     */
    String getContextName();

    /**
     * Retrieves the job associated with this object.
     *
     * @return The job as an instance of the SchedulerJob interface.
     */
    T getJob();

    /**
     * Returns the current timestamp.
     *
     * @return The current timestamp as a long value.
     */
    long getTimestamp();

    /**
     * Retrieves the modified timestamp of the SchedulerJobRecord.
     * The modified timestamp represents the last time the SchedulerJobRecord was modified.
     *
     * @return The modified timestamp as a long value.
     */
    long getModifiedTimestamp();

    /**
     * Retrieves the name of the user who last modified the SchedulerJobRecord.
     *
     * @return The name of the user who last modified the SchedulerJobRecord as a String.
     */
    String getModifiedBy();

    /**
     * Checks if the SchedulerJobRecord is currently held.
     *
     * @return true if the SchedulerJobRecord is currently held, false otherwise.
     */
    boolean isHeld();

    /**
     * Checks if the SchedulerJobRecord is skipped.
     *
     * @return true if the SchedulerJobRecord is skipped, false otherwise.
     */
    boolean isSkipped();

    /**
     * Checks if the current SchedulerJobRecord participates in a lock.
     *
     * @return {@code true} if the SchedulerJobRecord participates in a lock, {@code false} otherwise.
     */
    boolean isParticipatesInLock();

    /**
     * Checks if the target of the SchedulerJobRecord resides only within the current context.
     *
     * @return {@code true} if the target resides only within the current context, {@code false} otherwise.
     */
    boolean isTargetResidingContextOnly();
}

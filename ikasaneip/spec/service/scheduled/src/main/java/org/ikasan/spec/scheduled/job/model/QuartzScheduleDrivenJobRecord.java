package org.ikasan.spec.scheduled.job.model;

public interface QuartzScheduleDrivenJobRecord {

    /**
     * Retrieves the ID of the object.
     *
     * @return The unique identifier of the object as a String.
     */
    String getId();

    /**
     * Retrieves the name of the agent that this job runs on.
     *
     * @return The name of the agent.
     */
    String getAgentName();

    /**
     * Sets the name of the agent that this job runs on.
     *
     * @param agentName the name to be set for the agent
     */
    void setAgentName(String agentName);

    /**
     * Retrieves the name of the job.
     *
     * @return The name of the job as a String.
     */
    String getJobName();

    /**
     * Sets the job name for a given task.
     *
     * @param jobName the name of the job to be set
     */
    void setJobName(String jobName);

    /**
     * Retrieves the display name of this job.
     *
     * @return The display name of the job as a String.
     */
    String getDisplayName();

    /**
     * Sets the display name of this job.
     *
     * @param displayName the display name to be set for the job
     */
    void setDisplayName(String displayName);

    /**
     * Retrieves the name of the parent context that this job belongs to.
     *
     * @return The name of the parent context as a String.
     */
    String getContextName();

    /**
     * Sets the name of the parent context that this job belongs to.
     *
     * @param contextName the name of the parent context to be set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the QuartzScheduleDrivenJob object associated with this QuartzScheduleDrivenJobRecord.
     *
     * @return The QuartzScheduleDrivenJob object.
     */
    QuartzScheduleDrivenJob getQuartzScheduleDrivenJob();

    /**
     * Sets the QuartzScheduleDrivenJob for the QuartzScheduleDrivenJobRecord.
     *
     * @param quartzScheduleDrivenJob the QuartzScheduleDrivenJob to be set
     */
    void setQuartzScheduleDrivenJob(QuartzScheduleDrivenJob quartzScheduleDrivenJob);

    /**
     * Retrieves the current timestamp as a long value.
     *
     * @return The current timestamp as a long.
     */
    long getTimestamp();

    /**
     * Sets the timestamp value for the object.
     *
     * @param timestamp the new timestamp value to set
     */
    void setTimestamp(long timestamp);

    /**
     * Retrieves the modified timestamp of the object.
     *
     * @return The modified timestamp as a long value.
     */
    long getModifiedTimestamp();

    /**
     * Sets the modified timestamp of the object.
     *
     * @param timestamp The new modified timestamp value to set.
     */
    void setModifiedTimestamp(long timestamp);

    /**
     * Retrieves the name of the user who last modified the object.
     *
     * @return The name of the user who last modified the object.
     */
    String getModifiedBy();

    /**
     * Sets the name of the user who last modified the object.
     *
     * @param modifiedBy the name of the user
     */
    void setModifiedBy(String modifiedBy);
}

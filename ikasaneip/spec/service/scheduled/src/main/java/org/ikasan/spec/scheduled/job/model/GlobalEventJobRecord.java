package org.ikasan.spec.scheduled.job.model;

public interface GlobalEventJobRecord {

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
     * Retrieves the global event job associated with this GlobalEventJobRecord instance.
     *
     * @return The global event job as a {@link GlobalEventJob} object.
     */
    GlobalEventJob getGlobalEventJob();

    /**
     * Sets the global event job for the GlobalEventJobRecord instance.
     *
     * @param globalEventJob the global event job to be set
     */
    void setGlobalEventJob(GlobalEventJob globalEventJob);

    long getTimestamp();

    void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

}

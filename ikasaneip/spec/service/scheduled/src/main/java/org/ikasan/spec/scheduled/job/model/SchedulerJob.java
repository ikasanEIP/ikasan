package org.ikasan.spec.scheduled.job.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SchedulerJob extends Serializable {

    /**
     * Get the parent context id that this job belongs to.
     * @return
     */
    String getContextName();

    /**
     * Set the parent context id that this job belongs to.
     * @param contextName
     */
    void setContextName(String contextName);

    /**
     * Get the child context ids that this job belongs to. Contexts can appear within contexts.
     *
     * @return
     */
    List<String> getChildContextNames();

    /**
     * Set the child context ids that this job belongs to. Contexts can appear within contexts.
     *
     * @param contextIds
     */
    void setChildContextNames(List<String> contextIds);

    /**
     * Get the unique identifier for this job.
     *
     * @return
     */
    String getIdentifier();

    /**
     * Set the unique identifier for this job.
     *
     * @param jobIdentifier
     */
    void setIdentifier(String jobIdentifier);

    /**
     * Get the name of this agent that the job runs on.
     *
     * @return
     */
    String getAgentName();

    /**
     * Set the name of the agent that this job runs on.
     *
     * @param agentName
     */
    void setAgentName(String agentName);

    /**
     * Get the name of this job.
     *
     * @return
     */
    String getJobName();

    /**
     * Set the name of this job.
     *
     * @param jobName
     */
    void setJobName(String jobName);

    /**
     * Get the description of this job.
     *
     * @return
     */
    String getJobDescription();

    /**
     * Set the description of this job.
     *
     * @param jobDescription
     */
    void setJobDescription(String jobDescription);

    /**
     * Get the start up control type of this job.
     *
     * @return
     */
    String getStartupControlType();

    /**
     * Set the start up control of this job.
     *
     * @param startupControlType
     */
    void setStartupControlType(String startupControlType);

    /**
     * Set a map of contexts within which this job will be skipped
     *
     * @param skippedContexts
     */
    void setSkippedContexts(Map<String, Boolean> skippedContexts);

    /**
     * Get a map of contexts within which this job will be skipped
     *
     * @return
     */
    Map<String, Boolean> getSkippedContexts();

    /**
     * Set a map of contexts within which this job will be held
     *
     * @param heldContexts
     */
    void setHeldContexts(Map<String, Boolean> heldContexts);

    /**
     * Get a map of contexts within which this job will be held
     *
     * @return
     */
    Map<String, Boolean> getHeldContexts();

    /**
     * Set the job ordinal.
     *
     * @param ordinal
     */
    void setOrdinal(int ordinal);

    /**
     * Get the job ordinal.
     *
     * @return
     */
    int getOrdinal();
}

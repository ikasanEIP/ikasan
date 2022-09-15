package org.ikasan.spec.scheduled.job.model;

import java.io.Serializable;
import java.util.List;

public interface SchedulerJob extends Serializable {

    /**
     * Get the parent context id that this job belongs to.
     * @return
     */
    String getContextId();

    /**
     * Set the parent context id that this job belongs to.
     * @param contextId
     */
    void setContextId(String contextId);

    /**
     * Get the child context ids that this job belongs to. Contexts can appear within contexts.
     *
     * @return
     */
    List<String> getChildContextIds();

    /**
     * Set the child context ids that this job belongs to. Contexts can appear within contexts.
     *
     * @param contextIds
     */
    void setChildContextIds(List<String> contextIds);

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

    boolean isSkip();

    void setSkip(boolean skip);
}

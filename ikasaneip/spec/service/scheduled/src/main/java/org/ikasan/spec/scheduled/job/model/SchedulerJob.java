package org.ikasan.spec.scheduled.job.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
     * Get the display name of this job.
     *
     * @return
     */
    String getDisplayName();

    /**
     * Set the display name of this job.
     *
     * @param displayName
     */
    void setDisplayName(String displayName);

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

    @JsonIgnore
    default String getAggregateJobName() {
        return this.getJobName() + "_" + this.getContextName();
    }

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


    /**
     * Set whether this job is a template job or not.
     *
     * @param isTemplateJob true if this job is a template job, false otherwise
     */
    void setTemplateJob(Boolean isTemplateJob);


    /**
     * Check if this job is a template job.
     *
     * @return true if this job is a template job, false otherwise
     */
    Boolean isTemplateJob();

    /**
     * Set whether the job is template-based or not.
     *
     * @param isTemplateBased true if the job is template-based, false otherwise
     */
    void setTemplateBased(Boolean isTemplateBased);

    /**
     * Check if the job is template-based.
     *
     * @return true if the job is template-based, false if it is not
     */
    Boolean isTemplateBased();

    /**
     * Set the template name for this job.
     *
     * @param templateName the name of the template to set
     */
    void setTemplateName(String templateName);

    /**
     * Get the template name associated with this job.
     *
     * @return the template name
     */
    String getTemplateName();
}

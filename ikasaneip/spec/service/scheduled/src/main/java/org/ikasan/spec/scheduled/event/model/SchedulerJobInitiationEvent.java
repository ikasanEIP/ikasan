package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;

import java.io.Serializable;
import java.util.List;

public interface SchedulerJobInitiationEvent<CONTEXT_PARAM extends ContextParameterInstance
    , JOB extends InternalEventDrivenJobInstance, DRY_RUN_PARAMS extends DryRunParameters> extends Serializable {

    /**
     * Get the job
     *
     * @return
     */
    JOB getInternalEventDrivenJob();

    /**
     * Set the job.
     *
     * @param internalEventDrivenJob
     */
    void setInternalEventDrivenJob(JOB internalEventDrivenJob);

    /**
     * Set the context parameters.
     *
     * @param contextParameters
     */
    void setContextParameters(List<CONTEXT_PARAM> contextParameters);

    /**
     * Set the context parameters.
     *
     * @return
     */
    List<CONTEXT_PARAM> getContextParameters();

    /**
     * Get the agent name that will execute the job.
     *
     * @return
     */
    String getAgentName();

    /**
     * Set the agent name that will execute the job.
     *
     * @param agentName
     */
    void setAgentName(String agentName);

    /**
     * Get the agent url that will execute the job.
     *
     * @return
     */
    String getAgentUrl();

    /**
     * Set the agent name that will execute the job.
     *
     * @param agentUrl
     */
    void setAgentUrl(String agentUrl);


    /**
     * Get the name of the job being executed.
     *
     * @return
     */
    String getJobName();

    /**
     * Set the name of the job being executed.
     *
     * @param jobName
     */
    void setJobName(String jobName);

    /**
     * Get the context name that this job belongs to.
     *
     * @return
     */
    String getContextName();

    /**
     * Set the context name that this job belongs to.
     *
     * @param contextName
     */
    void setContextName(String contextName);

    /**
     * Get the child context names that this job belongs to. Contexts can appear within contexts.
     *
     * @return
     */
    List<String> getChildContextNames();

    /**
     * Set the child context names that this job belongs to. Contexts can appear within contexts.
     *
     * @param contextNames
     */
    void setChildContextNames(List<String> contextNames);

    /**
     * Get the context instance id that this job belongs to.
     *
     * @return
     */
    String getContextInstanceId();

    /**
     * Set the context instance id that this job belongs to.
     *
     * @param contextInstanceId
     */
    void setContextInstanceId(String contextInstanceId);

    /**
     * Flag to determine if the job is a dry run.
     *
     * @return
     */
    boolean isDryRun();

    /**
     * Set the flag to indicate that the jobs is a dry run.
     *
     * @param dryRun
     */
    void setDryRun(boolean dryRun);

    /**
     * Set the parameters that will determine the dry run behaviour.
     *
     * @param dryRunParameters
     */
    void setDryRunParameters(DRY_RUN_PARAMS dryRunParameters);

    /**
     * Get the parameters that will determine the dry run behaviour.
     *
     * @return
     */
    DRY_RUN_PARAMS getDryRunParameters();

    /**
     * Set flag to indicate that the job is skipped.
     *
     * @param skipped
     */
    void setSkipped(boolean skipped);

    /**
     * Flag to indicate if the job is skipped.
     *
     * @return
     */
    boolean isSkipped();

    /**
     * Set the event that was responsible for creating this job initiation event.
     *
     * @param scheduledProcessEvent
     */
    void setCatalystEvent(ScheduledProcessEvent scheduledProcessEvent);

    /**
     * Get the event that was responsible for creating this job initiation event.
     *
     * @return
     */
    ScheduledProcessEvent getCatalystEvent();
}

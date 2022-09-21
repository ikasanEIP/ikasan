package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;

import java.io.Serializable;
import java.util.List;

public interface ContextualisedScheduledProcessEvent<OUTCOME, DRY_RUN_PARAMETERS
    extends DryRunParameters> extends ScheduledProcessEvent<OUTCOME, DRY_RUN_PARAMETERS>, Serializable {

    /**
     * Get the parent context name that this job belongs to.
     *
     * @return
     */
    String getContextName();

    /**
     * Set the parent context id that this job belongs to.
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
     * @param contextId
     */
    void setChildContextNames(List<String> contextId);

    /**
     * Get the context instance id that this event is associated with.
     *
     * @return
     */
    String getContextInstanceId();

    /**
     * Set the context instance id that this event is associated with.
     *
     * @param contextInstanceId
     */
     void setContextInstanceId(String contextInstanceId);

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
     * Set the InternalEventDrivenJob
     * @param internalEventDrivenJob
     */
    void setInternalEventDrivenJob(InternalEventDrivenJobInstance internalEventDrivenJob);

    /**
     * Get the InternalEventDrivenJob
     * @return
     */
    InternalEventDrivenJobInstance getInternalEventDrivenJob();
}

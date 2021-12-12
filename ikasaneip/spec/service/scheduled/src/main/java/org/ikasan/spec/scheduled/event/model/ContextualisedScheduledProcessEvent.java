package org.ikasan.spec.scheduled.event.model;

public interface ContextualisedScheduledProcessEvent<OUTCOME, DRY_RUN_PARAMETERS
    extends DryRunParameters> extends ScheduledProcessEvent<OUTCOME, DRY_RUN_PARAMETERS> {

    /**
     * Get the parent context id that this job belongs to.
     *
     * @return
     */
    String getContextId();

    /**
     * Set the parent context id that this job belongs to.
     * @param contextId
     */
     void setContextId(String contextId);

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
}

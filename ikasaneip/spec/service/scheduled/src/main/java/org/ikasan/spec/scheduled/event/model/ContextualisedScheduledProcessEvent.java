package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;

public interface ContextualisedScheduledProcessEvent<OUTCOME> extends ScheduledProcessEvent<OUTCOME> {

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

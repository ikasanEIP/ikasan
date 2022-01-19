package org.ikasan.spec.scheduled.core.listener;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

@FunctionalInterface
public interface SchedulerJobInitiationEventRaisedListener {

    /**
     * Listener for when scheduler jobs initiation events are raised.
     *
     * @param event
     */
    void onSchedulerJobInitiationEventRaised(SchedulerJobInitiationEvent event);
}

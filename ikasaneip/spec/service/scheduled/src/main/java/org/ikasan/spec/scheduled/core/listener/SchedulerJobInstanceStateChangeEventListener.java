package org.ikasan.spec.scheduled.core.listener;


import org.ikasan.spec.scheduled.event.model.SchedulerJobInstanceStateChangeEvent;

@FunctionalInterface
public interface SchedulerJobInstanceStateChangeEventListener {

    /**
     * Listener interface for SchedulerJobInstance state changes.
     *
     * @param event
     */
    public void onSchedulerJobInstanceStateChangeEvent(SchedulerJobInstanceStateChangeEvent event);
}

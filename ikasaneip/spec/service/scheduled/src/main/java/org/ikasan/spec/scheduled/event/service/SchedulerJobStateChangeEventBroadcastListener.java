package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInstanceStateChangeEvent;

public interface SchedulerJobStateChangeEventBroadcastListener {

    /**
     * Called when scheduler job instance state change event occurs.
     *
     * @param event
     */
    void receiveBroadcast(SchedulerJobInstanceStateChangeEvent event);
}

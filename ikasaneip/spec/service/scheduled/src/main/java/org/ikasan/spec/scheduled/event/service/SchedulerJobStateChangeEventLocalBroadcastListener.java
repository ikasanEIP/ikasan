package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInstanceStateChangeEvent;

public interface SchedulerJobStateChangeEventLocalBroadcastListener {

    void receiveBroadcast(SchedulerJobInstanceStateChangeEvent event);
}

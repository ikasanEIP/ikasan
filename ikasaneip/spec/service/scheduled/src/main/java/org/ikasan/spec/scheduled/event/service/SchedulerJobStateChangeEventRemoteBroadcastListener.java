package org.ikasan.spec.scheduled.event.service;



/**
 * Marker interface for remote cluster event broadcast listeners.
 * Implementations receive scheduler job state change events and forward them to other cluster nodes.
 */
public interface SchedulerJobStateChangeEventRemoteBroadcastListener
    extends SchedulerJobStateChangeEventLocalBroadcastListener {
}

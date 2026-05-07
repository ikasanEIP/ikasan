package org.ikasan.spec.scheduled.event.service;

/**
 * Marker interface for remote cluster event broadcast listeners.
 * Implementations receive new scheduler job events and forward them to other cluster nodes.
 */
public interface NewSchedulerJobEventRemoteBroadcastListener
    extends NewSchedulerJobEventLocalBroadcastListener {
}

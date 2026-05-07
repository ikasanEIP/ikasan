package org.ikasan.spec.scheduled.event.service;

/**
 * Marker interface for remote cluster event broadcast listeners.
 * Implementations receive context view update events and forward them to other cluster nodes.
 */
public interface ContextViewUpdateEventRemoteBroadcastListener
    extends ContextViewUpdateEventLocalBroadcastListener {
}

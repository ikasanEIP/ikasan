package org.ikasan.spec.scheduled.event.service;

/**
 * Marker interface for remote cluster event broadcast listeners.
 * Implementations receive context instance DLQ events and forward them to other cluster nodes.
 */
public interface ContextInstanceDlqEventRemoteBroadcastListener
    extends ContextInstanceDlqEventLocalBroadcastListener {
}

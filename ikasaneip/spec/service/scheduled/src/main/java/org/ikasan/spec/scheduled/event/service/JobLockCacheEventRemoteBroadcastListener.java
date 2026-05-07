package org.ikasan.spec.scheduled.event.service;



/**
 * Marker interface for remote cluster event broadcast listeners.
 * Implementations receive job lock cache events and forward them to other cluster nodes.
 */
public interface JobLockCacheEventRemoteBroadcastListener
    extends JobLockCacheEventLocalBroadcastListener {
}

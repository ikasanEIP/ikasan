package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.event.service.ClusterEventService;

/**
 * Delivery channel for broadcasting cluster events to one remote dashboard node.
 */
public interface ClusterEventBroadcastChannel {

    /**
     * Submits a broadcast action to this remote node's delivery lane.
     *
     * @param task broadcast action to execute
     */
    void submit(Runnable task);

    /**
     * Returns the service used to publish events to this remote node.
     *
     * @return cluster event service
     */
    ClusterEventService service();

    /**
     * Releases channel resources.
     */
    void shutdown();
}

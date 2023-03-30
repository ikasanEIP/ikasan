package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

import java.util.function.Consumer;

public interface ContextInstanceSavedEventBroadcaster {

    /**
     * Register a listener for context instance save events.
     *
     * @param listener
     */
    void register(ContextInstanceSavedEventBroadcastListener listener);

    /**
     * Broadcast the context instance save event to all listeners.
     *
     * @param contextInstance
     */
    void broadcast(ContextInstance contextInstance);
}

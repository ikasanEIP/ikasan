package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

import java.util.function.Consumer;

public interface ContextInstanceSavedEventBroadcaster<R> {

    /**
     * Register a listener for context instance save events.
     *
     * @param listener
     * @return
     */
    R register(Consumer<ContextInstance> listener);

    /**
     * Broadcast the context instance save event to all listeners.
     *
     * @param contextInstance
     */
    void broadcast(ContextInstance contextInstance);
}

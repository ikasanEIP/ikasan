package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

public interface ContextInstanceDlqEventLocalBroadcastListener {

    /**
     * Receives a broadcast event with the given context instance.
     *
     * @param contextInstance the context instance associated with the broadcast event
     */
    void receiveBroadcast(ContextInstance contextInstance);
}

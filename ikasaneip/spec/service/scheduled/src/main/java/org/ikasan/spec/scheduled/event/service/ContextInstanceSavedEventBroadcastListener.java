package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

public interface ContextInstanceSavedEventBroadcastListener {

    /**
     * Called when ContextInstance is saved.
     *
     * @param event
     */
    void receiveBroadcast(ContextInstance event);
}

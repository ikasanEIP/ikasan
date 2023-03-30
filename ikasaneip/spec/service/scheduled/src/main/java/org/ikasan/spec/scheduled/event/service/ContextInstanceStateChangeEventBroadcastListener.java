package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.event.model.ContextInstanceStateChangeEvent;

public interface ContextInstanceStateChangeEventBroadcastListener {

    /**
     * Called when context instance state change event occurs.
     *
     * @param event
     */
    void receiveBroadcast(ContextInstanceStateChangeEvent event);
}

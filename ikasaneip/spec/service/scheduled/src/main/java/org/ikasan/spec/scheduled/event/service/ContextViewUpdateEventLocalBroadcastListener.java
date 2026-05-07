package org.ikasan.spec.scheduled.event.service;

public interface ContextViewUpdateEventLocalBroadcastListener {

    /**
     * Called when Context View is updated.
     *
     * @param message
     */
    void receiveBroadcast(String message);
}

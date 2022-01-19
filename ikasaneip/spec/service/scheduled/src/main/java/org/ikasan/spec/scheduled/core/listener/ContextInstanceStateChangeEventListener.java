package org.ikasan.spec.scheduled.core.listener;


import org.ikasan.spec.scheduled.event.model.ContextInstanceStateChangeEvent;

@FunctionalInterface
public interface ContextInstanceStateChangeEventListener {

    /**
     * Listener interface for ContextInstance state changes.
     *
     * @param event
     */
    public void onContextInstanceStateChangeEvent(ContextInstanceStateChangeEvent event);
}

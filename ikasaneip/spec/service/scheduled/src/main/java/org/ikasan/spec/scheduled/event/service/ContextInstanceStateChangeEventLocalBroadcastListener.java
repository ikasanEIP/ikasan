package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.event.model.ContextInstanceStateChangeEvent;

public interface ContextInstanceStateChangeEventLocalBroadcastListener {

    void receiveBroadcast(ContextInstanceStateChangeEvent event);
}

package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

public interface ContextInstanceSavedEventLocalBroadcastListener {

    void receiveBroadcast(ContextInstance event);
}

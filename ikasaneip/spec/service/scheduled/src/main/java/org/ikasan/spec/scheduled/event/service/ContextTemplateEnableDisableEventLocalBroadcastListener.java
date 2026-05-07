package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.context.model.ContextTemplate;

public interface ContextTemplateEnableDisableEventLocalBroadcastListener {

    /**
     * Called when ContextTemplate is enabled or disabled.
     *
     * @param contextTemplate
     */
    void receiveBroadcast(ContextTemplate contextTemplate);
}

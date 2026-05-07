package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.context.model.ContextTemplate;

public interface ContextTemplateSavedEventLocalBroadcastListener {

    /**
     * Called when ContextTemplate is saved.
     *
     * @param contextTemplate
     */
    void receiveContextTemplateSavedEventBroadcast(ContextTemplate contextTemplate);
}

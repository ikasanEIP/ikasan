package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.instance.model.LocalEventJobInstance;

public interface LocalEventService {

    /**
     * Raises a local event job.
     *
     * @param localEventJobInstance The instance of the local event job to raise.
     * @param contextInstanceId The ID of the context instance.
     * @param userName The username of the user.
     */
    void raiseLocalEventJob(LocalEventJobInstance localEventJobInstance, String contextInstanceId, String userName);
}

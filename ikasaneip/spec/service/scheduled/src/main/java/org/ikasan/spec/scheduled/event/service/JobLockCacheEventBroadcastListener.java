package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.event.model.JobLockCacheEvent;

public interface JobLockCacheEventBroadcastListener {

    /**
     * Called when job lock cache event occurs.
     *
     * @param event
     */
    void receiveBroadcast(JobLockCacheEvent event);
}

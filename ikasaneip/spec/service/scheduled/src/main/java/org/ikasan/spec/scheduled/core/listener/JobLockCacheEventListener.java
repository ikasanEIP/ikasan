package org.ikasan.spec.scheduled.core.listener;

import org.ikasan.spec.scheduled.event.model.JobLockCacheEvent;

public interface JobLockCacheEventListener {

    /**
     * Called when a job lock cache event occurs.
     *
     * @param jobLockCacheEvent
     */
    void onJobLockCacheEvent(JobLockCacheEvent jobLockCacheEvent);
}

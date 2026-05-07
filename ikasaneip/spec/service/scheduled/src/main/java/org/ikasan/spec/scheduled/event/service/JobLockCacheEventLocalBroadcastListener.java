package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.event.model.JobLockCacheEvent;

public interface JobLockCacheEventLocalBroadcastListener {

    void receiveBroadcast(JobLockCacheEvent event);
}

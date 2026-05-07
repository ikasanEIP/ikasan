package org.ikasan.spec.scheduled.event.service;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

public interface NewSchedulerJobEventLocalBroadcastListener {

    /**
     * Called when new Scheduler Job is created.
     *
     * @param schedulerJob
     */
    void receiveBroadcast(SchedulerJob schedulerJob);
}

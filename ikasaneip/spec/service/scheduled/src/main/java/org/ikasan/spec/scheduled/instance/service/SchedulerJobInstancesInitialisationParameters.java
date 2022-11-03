package org.ikasan.spec.scheduled.instance.service;

public interface SchedulerJobInstancesInitialisationParameters {

    /**
     * Flags to indicate that jobs should be initialised on hold.
     *
     * @return
     */
    boolean isInitialiseWithJobsOnHold();
}

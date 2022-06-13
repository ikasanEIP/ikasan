package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.quartz.Trigger;

import java.util.List;
import java.util.Optional;

public interface JobInitiationService {

    /**
     * Raise a SchedulerJobInitiationEvent with the relevant agent.
     *
     * @param contextUrl
     * @param event
     * @return
     */
    void raiseSchedulerJobInitiationEvent(String contextUrl, SchedulerJobInitiationEvent event);
}
